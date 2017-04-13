package com.hzg.user;

import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/16.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    private UserClient userClient;

    @RequestMapping(value="/queryUsers",method = RequestMethod.GET)
    public void queryUsers(HttpServletResponse response, User user){
        logger.info("queryUsers start, parameter:" + user.toString());

        List<User> users = userClient.queryUsers(user);
        logger.info("queryUsers end, result size:" + users.size());

        Writer.writeObjectToJson(response, users);
    }

    @GetMapping("")
    public String welcome(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("message", "Hello World");
        return "index";
    }
}
