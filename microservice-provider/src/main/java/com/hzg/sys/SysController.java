package com.hzg.sys;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.Writer;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys")
public class SysController {

    Logger logger = Logger.getLogger(SysController.class);

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

            if (!isUsernameExist(user.getId(), user.getUsername())) {
                user.setInputDate(inputDate);
                user.setState(0);

                if (user.getPassword().length() < 32) {
                    user.setPassword(DigestUtils.md5Hex(user.getPassword()).toUpperCase());
                }

                result = sysDao.save(user);

            } else {
                result = "用户名已经存在";
            }

        }else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            Post post = writer.gson.fromJson(json, Post.class);
            post.setInputDate(inputDate);
            result = sysDao.save(post);

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            Dept dept = writer.gson.fromJson(json, Dept.class);
            dept.setInputDate(inputDate);
            result = sysDao.save(dept);

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            Company company = writer.gson.fromJson(json, Company.class);
            company.setInputDate(inputDate);
            result = sysDao.save(company);
        }

        writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
        logger.info("save end, result:" + result);
    }

    @Transactional
    @PostMapping("/update")
    public void update(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("update start, parameter:" + entity + ":" + json);

        String result = "fail";

        if (entity.equalsIgnoreCase(User.class.getSimpleName()) ) {
            User user = writer.gson.fromJson(json, User.class);
            if (!isUsernameExist(user.getId(), user.getUsername())) {

                List<Integer> relateIds = new ArrayList<>();
                for (Post post : user.getPosts()) {
                    relateIds.add(post.getId());
                }

                User dbUser = (User) sysDao.queryById(user.getId(), User.class);
                List<Integer> unRelateIds = new ArrayList<>();
                for (Post post : dbUser.getPosts()) {
                    unRelateIds.add(post.getId());
                }

                /**
                 * 由于保存实体后，就马上从数据库查询该实体，导致关联关系还是旧的关联关系，
                 * 所以先重置关联关系，再保存实体，这样查询出来的关联关系就是最新的
                 */
                sysDao.updateRelateId(user.getId(), relateIds, unRelateIds, User.class);
                result = sysDao.updateById(user.getId(), user);
            } else {
                result = "用户名已经存在";
            }

        }else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            Post post = writer.gson.fromJson(json, Post.class);
            result = sysDao.updateById(post.getId(), post);

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            Dept dept = writer.gson.fromJson(json, Dept.class);
            result = sysDao.updateById(dept.getId(), dept);

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            Company company = writer.gson.fromJson(json, Company.class);
            result = sysDao.updateById(company.getId(), company);
        }

        writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
        logger.info("update end, result:" + result);
    }

    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public void query(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("query start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            User user = writer.gson.fromJson(json, User.class);
            List<User> users = sysDao.query(user);
            for (User user1 : users) {
                user1.setPassword(null);
            }

            writer.writeObjectToJson(response, users);

        }else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            Post post = writer.gson.fromJson(json, Post.class);
            List<Post> posts = sysDao.query(post);
            writer.writeObjectToJson(response, posts);

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            Dept dept = writer.gson.fromJson(json, Dept.class);
            List<Dept> depts = sysDao.query(dept);
            writer.writeObjectToJson(response, depts);

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            Company company = writer.gson.fromJson(json, Company.class);
            List<Company> companies = sysDao.query(company);
            writer.writeObjectToJson(response, companies);
        }

        logger.info("query end");
    }

    @RequestMapping(value = "/suggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void suggest(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("suggest start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            User user = writer.gson.fromJson(json, User.class);
            List<User> users = sysDao.suggest(user);
            for (User user1 : users) {
                user1.setPassword(null);
            }

            writer.writeObjectToJson(response, users);

        }else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            Post post = writer.gson.fromJson(json, Post.class);
            List<Post> posts = sysDao.suggest(post);
            writer.writeObjectToJson(response, posts);

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            Dept dept = writer.gson.fromJson(json, Dept.class);
            List<Dept> depts = sysDao.suggest(dept);
            writer.writeObjectToJson(response, depts);

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            Company company = writer.gson.fromJson(json, Company.class);
            List<Company> companies = sysDao.suggest(company);
            writer.writeObjectToJson(response, companies);
        }

        logger.info("suggest end");
    }

    @RequestMapping(value = "/complexQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void complexQuery(HttpServletResponse response, String entity, @RequestBody String json, int position, int rowNum){
        logger.info("complexQuery start, parameter:" + entity + ":" + json + "," + position + "," + rowNum);

        Map<String, String> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            List<User> users = sysDao.complexQuery(User.class, queryParameters, position, rowNum);
            for (User user1 : users) {
                user1.setPassword(null);
            }
            writer.writeObjectToJson(response, users);

        }else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            List<Post> posts = sysDao.complexQuery(Post.class, queryParameters, position, rowNum);
            writer.writeObjectToJson(response, posts);

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            List<Dept> depts = sysDao.complexQuery(Dept.class, queryParameters, position, rowNum);
            writer.writeObjectToJson(response, depts);

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            List<Company> companies = sysDao.complexQuery(Company.class, queryParameters, position, rowNum);
            writer.writeObjectToJson(response, companies);
        }

        logger.info("complexQuery end");
    }

    @RequestMapping(value = "/recordsSum", method = {RequestMethod.GET, RequestMethod.POST})
    public void recordsSum(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("recordsSum start, parameter:" + entity + ":" + json);
        BigInteger recordsSum = new BigInteger("-1");

        Map<String, String> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            recordsSum = sysDao.recordsSum(User.class, queryParameters);

        }else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            recordsSum =  sysDao.recordsSum(Post.class, queryParameters);

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            recordsSum =  sysDao.recordsSum(Dept.class, queryParameters);

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            recordsSum =  sysDao.recordsSum(Company.class, queryParameters);
        }

        writer.writeStringToJson(response, "{\"recordsSum\":" + recordsSum + "}");

        logger.info("recordsSum end");
    }

    boolean isUsernameExist(Integer id, String username) {
        boolean isExist = true;

        User user = new User();
        user.setUsername(username);
        List<User> users = sysDao.query(user);

        if (users.size() == 0) {
            isExist = false;
        } else if (users.size() == 1) {
            if (id == users.get(0).getId()) {
                isExist = false;
            }
        }

        return isExist;
    }
}
