package com.hzg.sys;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequestMapping("/sys")
public class SysController extends com.hzg.base.Controller {

    Logger logger = Logger.getLogger(SysController.class);

    @Autowired
    private Writer writer;

    public SysController(SysClient sysClient) {
        super(sysClient);
    }

    @GetMapping("")
    public String welcome(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("message", "Hello World");
        return "index";
    }

    @GetMapping("/list")
    public String list() {
        return "/sys/list";
    }

    @GetMapping("/view/{entity}/{id}")
    public String viewById(Map<String, Object> model, @PathVariable("entity") String entity, @PathVariable("id") Integer id) {
        logger.info("viewById start, entity:" + entity + ", id:" + id);

        List<Object> entities = null;

        String json = "{\"id\":" + id + "}";

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<User>>() {}.getType());

        } else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Post>>() {}.getType());

        } else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Dept>>() {}.getType());

        } else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Company>>() {}.getType());

        } else if (entity.equalsIgnoreCase(PrivilegeResource.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<PrivilegeResource>>() {}.getType());
        }

        model.put("entity", entities.isEmpty() ? null : entities.get(0));
        logger.info("viewById end");

        return "/sys/" + entity;
    }

    @RequestMapping(value = "/business/{name}", method = {RequestMethod.GET, RequestMethod.POST})
    public String business(Map<String, Object> model, @PathVariable("name") String name, String json) {
        logger.info("business start, name:" + name + ", json:" + json);

        if (name.equals("assignPrivilege")) {
            List<Post> posts = writer.gson.fromJson(client.query("post", json), new TypeToken<List<Post>>() {}.getType());

            List<PrivilegeResource> unAssignPrivileges = null;

            if (!posts.isEmpty()) {
                Post post = posts.get(0);
                model.put("entity", post);

                String ids = "";
                for (PrivilegeResource resource : post.getPrivilegeResources()) {
                    ids += resource.getId() + ",";
                }

                String params = "{}";
                if (!ids.equals("")) {
                   params = "{\"id\": \" not in (" + ids.substring(0, ids.length()-1) + ")\"}";
                }

                unAssignPrivileges = writer.gson.fromJson(client.complexQuery("privilegeResource", params, 0, -1),
                        new TypeToken<List<PrivilegeResource>>() {}.getType());
            }

            model.put("unAssignPrivileges", unAssignPrivileges);
        }

        logger.info("business " + name + " end");

        return "sys/" + name;
    }
}
