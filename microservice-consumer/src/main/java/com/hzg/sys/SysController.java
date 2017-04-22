package com.hzg.sys;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author smjie
 * @version 1.00
 * @Date 2017/3/16
 */
@Controller
@RequestMapping("/sys")
public class SysController extends com.hzg.base.Controller {

    Logger logger = Logger.getLogger(SysController.class);

    @Autowired
    private Writer writer;

    public SysController(SysClient sysClient){
        super(sysClient);
    }

    @GetMapping("")
    public String welcome(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("message", "Hello World");
        return "index";
    }

    @GetMapping("/view/{entity}/{id}")
    public String viewById(Map<String, Object> model, @PathVariable("entity") String entity, @PathVariable("id") Integer id) {

        logger.info("viewById start, entity:" + entity + ", id:" + id);

        String json = "{\"id\":" + id + "}";

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            List<User> users = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<User>>(){}.getType());
            model.put("user", users.isEmpty() ? null : users.get(0));

        }else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            List<Post> posts = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Post>>(){}.getType());
            model.put("post", posts.isEmpty() ? null : posts.get(0));

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            List<Dept> depts = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Dept>>(){}.getType());
            model.put("dept", depts.isEmpty() ? null : depts.get(0));

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            List<Company> companies = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Company>>(){}.getType());
            model.put("company", companies.isEmpty() ? null : companies.get(0));
        }

        logger.info("viewById end");

        return "/sys/"+entity;
    }
}
