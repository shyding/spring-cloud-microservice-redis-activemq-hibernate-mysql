package com.hzg;

import com.hzg.base.Dao;
import com.hzg.tools.CommonConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class Index {
    static Logger logger = Logger.getLogger(Index.class.getName());

    @Autowired
    private Dao dao;

    /**
     * 跳转到默认页面
     */
    @RequestMapping("/")
    public String index(Map<String, Object> model, @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        String username = (String)dao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId);

        if (username != null) {
            //如果当前有用相同用户名已登录系统的用户，则跳转到登录页面
            String signInedUserSessionId = (String)dao.getFromRedis(CommonConstant.user + CommonConstant.underline + username);
            if (signInedUserSessionId != null && !signInedUserSessionId.equals(sessionId)) {
                return "redirect:/sys/user/signIn";
            }
        }

        /**
         * 权限为空
         */
        if (dao.getFromRedis(username + CommonConstant.underline + CommonConstant.resources) == null) {
            return "redirect:/sys/user/signIn";
        }

        model.put(CommonConstant.sessionId, sessionId);
        model.put(CommonConstant.username, username);
        model.put(CommonConstant.resources, dao.getFromRedis(username + CommonConstant.underline + CommonConstant.resources));

        return CommonConstant.index;
    }

    /**
     * 跳转到打印页面
     */
    @PostMapping("/print")
    public String print(Map<String, Object> model, String printContent) {
        model.put(CommonConstant.printContent, printContent);
        return CommonConstant.print;
    }
}