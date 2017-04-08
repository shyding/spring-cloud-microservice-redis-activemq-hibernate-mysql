package com.hzg.user;

import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    private UserDao userDao;

    @RequestMapping(value="/queryUsers",method = {RequestMethod.GET,RequestMethod.POST})
    public void queryUsers(HttpServletResponse response, @RequestBody User user){
        logger.info("queryUsers start, parameter:" + user.toString());

        List<User> users = userDao.queryUsers(user);
        logger.info("queryUsers end, result size:" + users.size());

        Writer.writeObjectToJson(response, users);
    }

    @RequestMapping(value="/updateUser",method= RequestMethod.GET)
    public void updateUser(HttpServletResponse response, User user){
        logger.info("updateUser start, parameter:" + user.toString());

        int result = userDao.updateUser(user);
        Writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");

        logger.info("updateUser end, result:" + result);
    }
}
