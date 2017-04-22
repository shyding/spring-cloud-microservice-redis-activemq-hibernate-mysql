package com.hzg.sys;

import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author smjie
 * @version 1.00
 * @Date 2017/3/16
 */
@Controller
@RequestMapping("/sys")
public class SysController {

    Logger logger = Logger.getLogger(SysController.class);

    public String redisKeyPerfixSys = "sys_";

    @Autowired
    private SysDao sysDao;

    @Autowired
    private Writer writer;

    /**
     * 保存实体
     * @param response
     * @param entity
     * @param json
     */
    @Transactional
    @PostMapping("/save")
    public void save(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("save start, parameter:" + entity + ":" + json);

        String result = "fail";
        Timestamp inputDate = new Timestamp(System.currentTimeMillis());

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            User user = writer.gson.fromJson(json, User.class);
            user.setInputDate(inputDate);
            user.setState(0);
            result = sysDao.save(user, User.class,redisKeyPerfixSys+User.class.getSimpleName());

        }else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            Post post = writer.gson.fromJson(json, Post.class);
            post.setInputDate(inputDate);
            result = sysDao.save(post, Post.class,redisKeyPerfixSys+Post.class.getSimpleName());

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            Dept dept = writer.gson.fromJson(json, Dept.class);
            dept.setInputDate(inputDate);
            result = sysDao.save(dept, Dept.class,redisKeyPerfixSys+Dept.class.getSimpleName());

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            Company company = writer.gson.fromJson(json, Company.class);
            company.setInputDate(inputDate);
            result = sysDao.save(company, Company.class,redisKeyPerfixSys+Company.class.getSimpleName());
        }

        writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
        logger.info("save end, result:" + result);
    }

    @Transactional
    @PostMapping("/update")
    public void update(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("update start, parameter:" + entity + ":" + json);

        String result = "fail";

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            User user = writer.gson.fromJson(json, User.class);
            result = sysDao.updateById(user.getId(), user, User.class,redisKeyPerfixSys+User.class.getSimpleName());

        }else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            Post post = writer.gson.fromJson(json, Post.class);
            result = sysDao.updateById(post.getId(), post, Post.class,redisKeyPerfixSys+Post.class.getSimpleName());

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            Dept dept = writer.gson.fromJson(json, Dept.class);
            result = sysDao.updateById(dept.getId(), dept, Dept.class,redisKeyPerfixSys+Dept.class.getSimpleName());

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            Company company = writer.gson.fromJson(json, Company.class);
            result = sysDao.updateById(company.getId(), company, Company.class,redisKeyPerfixSys+Company.class.getSimpleName());
        }

        writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
        logger.info("update end, result:" + result);
    }

    @PostMapping("/query")
    public void query(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("query start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            User user = writer.gson.fromJson(json, User.class);
            List<User> users = sysDao.query(user.getId(), user, User.class,redisKeyPerfixSys+User.class.getSimpleName());
            writer.writeObjectToJson(response, users);

        }else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            Post post = writer.gson.fromJson(json, Post.class);
            List<Post> posts = sysDao.query(post.getId(), post, Post.class,redisKeyPerfixSys+Post.class.getSimpleName());
            writer.writeObjectToJson(response, posts);

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            Dept dept = writer.gson.fromJson(json, Dept.class);
            List<Dept> depts = sysDao.query(dept.getId(), dept, Dept.class,redisKeyPerfixSys+Dept.class.getSimpleName());
            writer.writeObjectToJson(response, depts);

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            Company company = writer.gson.fromJson(json, Company.class);
            List<Company> companies = sysDao.query(company.getId(), company, Company.class,redisKeyPerfixSys+Company.class.getSimpleName());
            writer.writeObjectToJson(response, companies);
        }

        logger.info("query end");
    }

}
