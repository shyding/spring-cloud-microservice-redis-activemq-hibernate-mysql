package com.hzg.user;

import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/3/16.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    private UserDao userDao;

    @RequestMapping(value="/queryUser",method= RequestMethod.GET)
    public void queryUser(HttpServletResponse response, User user){
        logger.info("queryUser start, parameter:" + user.toString());

        User dbUser = userDao.queryUser(user);
        logger.info("queryUser end, result:" + dbUser.toString());

        Writer.writeObjectToJson(response, dbUser);
    }

    @RequestMapping(value="/updateUser",method= RequestMethod.GET)
    public void updateUser(HttpServletResponse response, User user){
        logger.info("updateUser start, parameter:" + user.toString());

        int result = userDao.updateUser(user);
        Writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");

        logger.info("updateUser end, result:" + result);
    }
}
