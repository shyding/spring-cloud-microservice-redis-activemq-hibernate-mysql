package com.hzg;

import com.hzg.base.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;
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
    public String index(HttpSession session, Map<String, Object> model) {
        String username = (String)dao.getFromRedis("sessionId_" + session.getId());

        if (username != null) {
            //如果当前有用相同用户名已登录系统的用户，则跳转到登录页面
            String signInedUserSessionId = (String)dao.getFromRedis("user_" + username);
            if (signInedUserSessionId != null && !signInedUserSessionId.equals(session.getId())) {
                return "redirect:/sys/user/signIn";
            }
        }

        /**
         * 权限为空
         */
        if (dao.getFromRedis(username + "_resources") == null) {
            return "redirect:/sys/user/signIn";
        }

        model.put("resources", dao.getFromRedis(username + "_resources"));

        return "index";
    }
}