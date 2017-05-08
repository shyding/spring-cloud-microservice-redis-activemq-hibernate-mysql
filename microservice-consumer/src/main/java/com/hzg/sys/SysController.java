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
    public String welcome() {
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
}
