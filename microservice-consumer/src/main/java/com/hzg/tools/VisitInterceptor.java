package com.hzg.tools;

import com.hzg.sys.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@ConfigurationProperties("visitInterceptor")
public class VisitInterceptor extends HandlerInterceptorAdapter {

    public List<String> noAuthUris;
    public List<String> noAuthUrisAfterSignIn;
    public List<String> macValidateUris;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;
    @Autowired
    public Writer writer;
    @Autowired
    public CookieUtils cookieUtils;

    @Autowired
    public Integer sessionTime;

    /**
     * 拦截访问的 uri，在可以访问的 uri 里，则通过，否则返回错误提示
     */
    public boolean preHandle(javax.servlet.http.HttpServletRequest request,
                             javax.servlet.http.HttpServletResponse response, java.lang.Object handler){

        String visitingURI = request.getRequestURI();

        if (isNoAuthUris(visitingURI, noAuthUris)) {
            return true;
        }

        String sessionId = cookieUtils.getCookieValue(request, CommonConstant.sessionId);
        if (sessionId == null) {
            sessionId = request.getParameter(CommonConstant.sessionId);
        }
        String username = (String)redisTemplate.opsForValue().get(CommonConstant.sessionId + CommonConstant.underline + sessionId);
        String resources = null;

        if (username != null) {
            String signInedUserSessionId = (String)redisTemplate.opsForValue().get(CommonConstant.user + CommonConstant.underline + username);
            if (signInedUserSessionId != null && !signInedUserSessionId.equals(sessionId)) {
                return notPass(response, "对不起，你的账号已被注销，不能访问该页面");
            }

            if (isNoAuthUris(visitingURI, noAuthUrisAfterSignIn)) {
                if (macValidate(request, visitingURI, sessionId)) {
                    return pass(response, username, sessionId);
                } else {
                    return notPass(response, "MAC 校验不通过");
                }
            }

            resources = (String)redisTemplate.opsForValue().get(username + CommonConstant.underline + CommonConstant.resources);
        }


        if (resources != null) {
            if (resources.contains(visitingURI)) {

                /**
                 * 表单提交 mac 校验
                 */
                if (macValidate(request, visitingURI, sessionId)) {
                    return pass(response, username, sessionId);
                } else {
                    return notPass(response, "MAC 校验不通过");
                }

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

                int pos = parUrisStr.lastIndexOf("/");
                if (pos == -1 || pos == 0) {
                    parUrisStr += "/{";
                } else {
                    parUrisStr = parUrisStr.substring(0, parUrisStr.lastIndexOf("/")) + "/{";
                }

                if (resources.contains(parUrisStr)) {
                    if (macValidate(request, visitingURI, sessionId)) {
                        return pass(response, username, sessionId);
                    } else {
                        return notPass(response, "MAC 校验不通过");
                    }


                } else {
                    return notPass(response, "对不起，你访问的页面不存在，或者没有权限访问");
                }
            }


        } else {
            return notPass(response, "对不起，你访问的页面不存在，或者会话已经过期,请重新登录");
        }
    }

    /**
     *
     * @param username
     * @param sessionId
     * @return
     */
    public Boolean pass(javax.servlet.http.HttpServletResponse response, String username, String sessionId) {
        /**
         * 表示用户在线，重新设置 半小时 后会话过期
         */
        redisTemplate.boundValueOps(username).expire(sessionTime, TimeUnit.SECONDS);
        redisTemplate.boundValueOps(username + CommonConstant.underline + CommonConstant.resources).expire(sessionTime, TimeUnit.SECONDS);
        redisTemplate.boundValueOps(CommonConstant.sessionId + CommonConstant.underline + sessionId).expire(sessionTime, TimeUnit.SECONDS);
        redisTemplate.boundValueOps(CommonConstant.user + CommonConstant.underline + username).expire(sessionTime, TimeUnit.SECONDS);
        redisTemplate.boundValueOps(CommonConstant.salt + CommonConstant.underline + sessionId).expire(sessionTime, TimeUnit.SECONDS);

        cookieUtils.addCookie(response, CommonConstant.sessionId, sessionId);

        return true;
    }

    public Boolean notPass(javax.servlet.http.HttpServletResponse response, String msg) {
        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\": \"" + msg + "\"}");
        return false;
    }

    public boolean isNoAuthUris(String uri, List<String> noAuthUris) {
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

    public boolean isMacValidateUris(String uri) {
        for (int i = 0; i < macValidateUris.size(); i++) {
            if (uri.contains(macValidateUris.get(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 表单提交 mac 校验
     * @param request
     * @param visitingURI
     * @param sessionId
     * @return
     */
    public boolean macValidate(javax.servlet.http.HttpServletRequest request, String visitingURI, String sessionId) {
        boolean pass = false;

        if (isMacValidateUris(visitingURI)) {
            String json = request.getParameter("json");
            String mac = request.getParameter("mac");

            String salt = (String)redisTemplate.opsForValue().get(CommonConstant.salt + CommonConstant.underline + sessionId);
            User user = (User) redisTemplate.opsForValue().get((String)redisTemplate.opsForValue().get(CommonConstant.sessionId + CommonConstant.underline + sessionId));
            String pin = DigestUtils.md5Hex(salt + user.getPassword()).toUpperCase();


            if (mac.equals(DigestUtils.md5Hex(json + pin).toUpperCase())) {
                pass = true;
            }
        } else {
            pass = true;
        }

        return pass;
    }

    public List<String> getNoAuthUris() {
        return noAuthUris;
    }

    public void setNoAuthUris(List<String> noAuthUris) {
        this.noAuthUris = noAuthUris;
    }

    public List<String> getMacValidateUris() {
        return macValidateUris;
    }

    public void setMacValidateUris(List<String> macValidateUris) {
        this.macValidateUris = macValidateUris;
    }

    public List<String> getNoAuthUrisAfterSignIn() {
        return noAuthUrisAfterSignIn;
    }

    public void setNoAuthUrisAfterSignIn(List<String> noAuthUrisAfterSignIn) {
        this.noAuthUrisAfterSignIn = noAuthUrisAfterSignIn;
    }
}