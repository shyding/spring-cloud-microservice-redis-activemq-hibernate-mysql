package com.hzg.sys;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/16.
 */
@Controller
@RequestMapping("/sys")
public class SysController {

    Logger logger = Logger.getLogger(SysController.class);

    @Autowired
    private SysClient sysClient;

    @Autowired
    private Writer writer;

    @GetMapping("")
    public String welcome(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("message", "Hello World");
        return "index";
    }

    @PostMapping("/save/{entity}")
    public void save(HttpServletResponse response, String json, @PathVariable("entity") String entity) {
        writer.writeStringToJson(response, sysClient.save(entity, json));
    }

    @PostMapping("/update/{entity}")
    public void update(HttpServletResponse response, String json, @PathVariable("entity") String entity) {
        writer.writeStringToJson(response, sysClient.update(entity, json));
    }

    @PostMapping("/viewList/{entity}")
    public String viewList(Map<String, Object> model, String json, @PathVariable("entity") String entity) {

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            List<User> users = writer.gson.fromJson(sysClient.query(entity, json), new TypeToken<List<User>>(){}.getType());
            model.put("users", users);
        }

        if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            List<Post> posts = writer.gson.fromJson(sysClient.query(entity, json), new TypeToken<List<Post>>(){}.getType());
            model.put("posts", posts);
        }

        if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            List<Dept> depts = writer.gson.fromJson(sysClient.query(entity, json), new TypeToken<List<Dept>>(){}.getType());
            model.put("depts", depts);
        }

        if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            List<Company> companies = writer.gson.fromJson(sysClient.query(entity, json), new TypeToken<List<Company>>(){}.getType());
            model.put("companies", companies);
        }

        return "/sys/"+entity;
    }

    @GetMapping("/view/{entity}/{id}")
    public String viewById(Map<String, Object> model, @PathVariable("entity") String entity, @PathVariable("id") Integer id) {

        String json = "{\"id\":" + id + "}";

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            List<User> users = writer.gson.fromJson(sysClient.query(entity, json), new TypeToken<List<User>>(){}.getType());
            model.put("user", users.get(0));
        }

        if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            List<Post> posts = writer.gson.fromJson(sysClient.query(entity, json), new TypeToken<List<Post>>(){}.getType());
            model.put("post", posts.get(0));
        }

        if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            List<Dept> depts = writer.gson.fromJson(sysClient.query(entity, json), new TypeToken<List<Dept>>(){}.getType());
            model.put("dept", depts.get(0));
        }

        if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            List<Company> companies = writer.gson.fromJson(sysClient.query(entity, json), new TypeToken<List<Company>>(){}.getType());
            model.put("company", companies.get(0));
        }

        return "/sys/"+entity;
    }
}
