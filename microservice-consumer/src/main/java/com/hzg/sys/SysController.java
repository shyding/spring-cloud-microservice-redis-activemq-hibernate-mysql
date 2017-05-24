package com.hzg.sys;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.StrUtil;
import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/sys")
public class SysController extends com.hzg.base.Controller {

    Logger logger = Logger.getLogger(SysController.class);

    @Autowired
    private Writer writer;

    @Autowired
    private SysClient sysClient;

    @Autowired
    private StrUtil strUtil;

    public SysController(SysClient sysClient) {
        super(sysClient);
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

        } else if (entity.equalsIgnoreCase(Audit.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Audit>>() {}.getType());

            String queryJson = "";
            if (!entities.isEmpty()) {
                queryJson = "{\"entityId\":" + ((Audit)entities.get(0)).getEntityId() + "}";
            }
            model.put("entities", writer.gson.fromJson(client.query(entity, queryJson), new TypeToken<List<Audit>>() {}.getType()));

        } else if (entity.equalsIgnoreCase(AuditFlow.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<AuditFlow>>() {}.getType());
        }

        model.put("entity", entities.isEmpty() ? null : entities.get(0));
        logger.info("viewById end");

        return "/sys/" + entity;
    }

    @RequestMapping(value = "/privateQuery/{entity}", method = {RequestMethod.GET, RequestMethod.POST})
    public void privateQuery(HttpSession session, HttpServletResponse response, String dataTableParameters, String json, Integer recordsSum, @PathVariable("entity") String entity) {
        logger.info("privateQuery start, entity:" + entity + ", json:" + json);
        String privateCondition = "";

        if (entity.equals("audit")) {
            User user = (User) dao.getFromRedis((String)dao.getFromRedis("sessionId_" + session.getId()));
            for (Post post : user.getPosts()) {
                privateCondition += post.getId() + ",";
            }

            if (!privateCondition.equals("")) {
                if (!json.equals("{}")) {
                    json = json.substring(0, json.length()-1) + ",\"post\":\" in (" + privateCondition.substring(0, privateCondition.length()-1) + ")\"}";
                } else {
                    json = "{" + "\"post\": in(" + privateCondition.substring(0, privateCondition.length()-1) + ")}";
                }
            }

            complexQuery(response, dataTableParameters, json, recordsSum, entity);
        }

        logger.info("privateQuery " + entity + " end");
    }

    @RequestMapping(value = "/business/{name}", method = {RequestMethod.GET, RequestMethod.POST})
    public String business(Map<String, Object> model, @PathVariable("name") String name, String json) {
        logger.info("business start, name:" + name + ", json:" + json);

        if (name.equals("assignPrivilege")) {
            Map<String, Object> result = writer.gson.fromJson(sysClient.queryPrivilege(json), new TypeToken<Map<String, Object>>(){}.getType());
            if (result.get("post") != null) {
                model.put("entity", result.get("post"));
            }
            model.put("unAssignPrivileges", result.get("unAssignPrivileges"));

        }

        logger.info("business " + name + " end");

        return "/sys/" + name;
    }

    /**
     * 事宜办理
     * @param response
     * @param json
     */
    @PostMapping("/audit")
    public void audit(HttpServletResponse response, String json) {
        logger.info("audit start, json:" + json);
        writer.writeStringToJson(response, sysClient.audit(json));
        logger.info("audit end");
    }

    /**
     * 到登录页面，设置加密密码的 salt
     * @param model
     * @param session
     * @return
     */
    @GetMapping("/user/signIn")
    public String signIn(Map<String, Object> model, HttpSession session) {
        String salt = strUtil.generateRandomStr(256);
        dao.storeToRedis("salt_" + session.getId(), salt, 7200);

        if (session.getAttribute("result") != null) {
            model.put("result", session.getAttribute("result"));
            session.removeAttribute("result");
        }
        model.put("salt", "\"" + salt + "\"");

        return "/signIn";
    }

    /**
     * 用户登录，注销，重复登录
     * @param name
     * @param json
     * @return
     */
    @PostMapping("/user/{name}")
    public String user(@PathVariable("name") String name, HttpSession session, String json) {
        logger.info("user start, name:" + name + ", json:" + json);

        String page;

        Map<String, String> result = null;
        if (name.equals("signIn")) {
            result = writer.gson.fromJson(sysClient.signIn(json), new TypeToken<Map<String, String>>(){}.getType());

        } else if (name.equals("signOut")) {
            result = writer.gson.fromJson(sysClient.signOut(json), new TypeToken<Map<String, String>>(){}.getType());

        } else if (name.equals("hasLoginDeal")) {
            result = writer.gson.fromJson(sysClient.hasLoginDeal(json), new TypeToken<Map<String, String>>(){}.getType());
        }

        if (result.get("result").equals("success")) {
            page = "redirect:/";

            if (name.equals("signOut")) {
                page = "redirect:/sys/user/signIn";
            }

        } else {
            session.setAttribute("result", result.get("result"));
            page = "redirect:/sys/user/signIn";

            if (name.equals("signOut")) {
                session.removeAttribute("result");
                page = "redirect:/";
            }
        }

        logger.info("user " + name + " end");

        return page;
    }
}
