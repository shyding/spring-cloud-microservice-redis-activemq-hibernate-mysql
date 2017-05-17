package com.hzg.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
public class VisitInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    public List<String> noAuthUris;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    /**
     * 拦截访问的 uri，在可以访问的 uri 里，则通过，否则返回错误提示
     */
    public boolean preHandle(javax.servlet.http.HttpServletRequest request,
                             javax.servlet.http.HttpServletResponse response, java.lang.Object handler){

        String visitingURI = request.getRequestURI();

        if (isNoAuthUris(visitingURI)) {
            return true;
        }

        String sessionId = request.getSession().getId();
        String username = (String)redisTemplate.opsForValue().get("sessionId_" + sessionId);
        String resources = null;

        if (username != null) {
            String signInedUserSessionId = (String)redisTemplate.opsForValue().get("user_" + username);
            if (signInedUserSessionId != null && !signInedUserSessionId.equals(sessionId)) {
                try {
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().print("对不起，你的账号已被注销，不能访问该页面");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }

            resources = (String)redisTemplate.opsForValue().get(username + "_resources");
        }

        if (resources != null) {
            if (resources.contains(visitingURI)) {
                return pass(username, sessionId);

            } else {
                /**
                 * 处理 restful 风格 url, 该类型 url 最后一个字符 / 后的字符串为记录 id，
                 *  由于该字符串可变，所以后台权限处设置该字符串，用 {} 包裹，如：
                 *  /sys/view/user/{id}, {id} 即表示可变的 id
                 */
                String[] partUris = visitingURI.split("/");

                int i = 1;
                String parUrisStr = partUris[0];
                while (resources.contains(parUrisStr) && i < partUris.length) {
                    parUrisStr += "/" + partUris[i++];
                }

                if (resources.contains(parUrisStr.substring(0, parUrisStr.lastIndexOf("/")) + "/{")) {
                    return pass(username, sessionId);

                } else {
                    return notPass(response);
                }
            }

        } else {
            return notPass(response);
        }
    }

    /**
     *
     * @param username
     * @param sessionId
     * @return
     */
    public Boolean pass(String username, String sessionId) {
        /**
         * 表示用户在线，重新设置 半小时 后会话过期
         */
        redisTemplate.boundValueOps(username).expire(1800, TimeUnit.SECONDS);
        redisTemplate.boundValueOps(username + "_resources").expire(1800, TimeUnit.SECONDS);
        redisTemplate.boundValueOps("sessionId_" + sessionId).expire(1800, TimeUnit.SECONDS);
        redisTemplate.boundValueOps("user_" + username).expire(1800, TimeUnit.SECONDS);

        return true;
    }

    public Boolean notPass(javax.servlet.http.HttpServletResponse response) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print("对不起，你访问的页面不存在，或者没有权限访问;或者会话已经过期,请重新登录");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isNoAuthUris(String uri) {
        if (uri.contains(".")) { //静态资源
            return true;
        }
        if ("/".equals(uri)) {
            return true;
        }

        for (int i = 0; i < noAuthUris.size(); i++) {
            if (uri.contains(noAuthUris.get(i))) {
                return true;
            }
        }

        return false;
    }
}