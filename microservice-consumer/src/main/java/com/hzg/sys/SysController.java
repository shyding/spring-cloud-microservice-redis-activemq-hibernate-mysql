package com.hzg.sys;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @Autowired
    private CookieUtils cookieUtils;

    @Autowired
    public Integer sessionTime;

    @Autowired
    public RenderHtmlData renderHtmlData;

    public SysController(SysClient sysClient) {
        super(sysClient);
    }

    @GetMapping("/view/{entity}/{id}")
    public String viewById(Map<String, Object> model, @PathVariable("entity") String entity, @PathVariable("id") Integer id,
            @CookieValue(name="sessionId", defaultValue = "")String sessionId) {
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
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Audit>>(){}.getType());

            List<Audit> audits = null;
            if (!entities.isEmpty()) {
                audits = writer.gson.fromJson(client.query(entity, "{\"no\":" + ((Audit)entities.get(0)).getNo() + "}"),
                        new TypeToken<List<Audit>>() {}.getType());
            }

            String refuseUserOptions = "";
            if (!audits.isEmpty()) {
                User currentUser = (User)dao.getFromRedis((String)dao.getFromRedis("sessionId_" + sessionId));
                refuseUserOptions = renderHtmlData.getRefuseUserOptions(currentUser, audits, "", 0);
            }

            if (!refuseUserOptions.equals("")) {
                model.put("refuseUserOptions", refuseUserOptions);
            }

            model.put("entities", audits);
            model.put("sessionId", sessionId);

        } else if (entity.equalsIgnoreCase(AuditFlow.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<AuditFlow>>() {}.getType());

            if (!entities.isEmpty()) {
                AuditFlow auditFlow = (AuditFlow) entities.get(0);

                if (!auditFlow.getAuditFlowNodes().isEmpty()) {
                    AuditFlowNode[] auditFlowNodes = new AuditFlowNode[auditFlow.getAuditFlowNodes().size()];
                    auditFlow.getAuditFlowNodes().toArray(auditFlowNodes);

                    Arrays.sort(auditFlowNodes, new Comparator<AuditFlowNode>() {
                        @Override
                        public int compare(AuditFlowNode o1, AuditFlowNode o2) {
                            if (o1.getId().compareTo(o2.getId()) > 0) {
                                return 1;
                            } else if (o1.getId().compareTo(o2.getId()) < 0) {
                                return -1;
                            }

                            return 0;
                        }
                    });

                    model.put("auditFlowNodes", auditFlowNodes);
                }
            }
        }

        model.put("entity", entities.isEmpty() ? null : entities.get(0));
        logger.info("viewById end");

        return "/sys/" + entity;
    }

    @RequestMapping(value = "/privateQuery/{entity}", method = {RequestMethod.GET, RequestMethod.POST})
    public void privateQuery(HttpServletResponse response, String dataTableParameters, String json, Integer recordsSum,
                             @PathVariable("entity") String entity, @CookieValue(name="sessionId", defaultValue = "")String sessionId) {
        logger.info("privateQuery start, entity:" + entity + ", json:" + json);
        String privateCondition = "";

        if (entity.equals("audit")) {
            User user = (User) dao.getFromRedis((String)dao.getFromRedis("sessionId_" + sessionId));
            for (Post post : user.getPosts()) {
                privateCondition += post.getId() + ",";
            }

            if (!privateCondition.equals("")) {
                if (!json.equals("{}")) {
                    json = json.substring(0, json.length()-1) + ",\"post\":\" in (" + privateCondition.substring(0, privateCondition.length()-1) + ")\"" +
                            ",\"user\":\"" + user.getId() +"\"}";
                } else {
                    json = "{" + "\"post\": \"in(" + privateCondition.substring(0, privateCondition.length()-1) + ")\"" +
                            ",\"user\":\"" + user.getId() + "\"}";
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

    @PostMapping("/authorize")
    public void authorize(HttpServletResponse response, String sessionId, String uri) {
        logger.info("authorize start, sessionId:" + sessionId + ", uri:" + uri);

        String result = "{\"" + CommonConstant.result + "\":\"" + CommonConstant.fail +"\"}";

        String username = (String)dao.getFromRedis("sessionId_" + sessionId);

        if (username != null) {
            String signInedUserSessionId = (String)dao.getFromRedis("user_" + username);

            if (signInedUserSessionId != null && signInedUserSessionId.equals(sessionId)) {

                String resources = (String)dao.getFromRedis(username + "_resources");
                if (resources != null && resources.contains(uri)) {
                    result = "{\"" + CommonConstant.result + "\":\"" + CommonConstant.success +"\"}";
                }
            }
        }

        writer.writeObjectToJsonAccessAllow(response, result);
        logger.info("authorize end");
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
     * @return
     */
    @GetMapping("/user/signIn")
    public String signIn(HttpServletResponse response, Map<String, Object> model) {
        String sessionId = strUtil.generateRandomStr(32);

        String salt = "";
        if (model.get("oldSessionId") == null) {
            salt = strUtil.generateRandomStr(256);
        } else {
            salt = (String) dao.getFromRedis("salt_" + (String)model.get("oldSessionId"));
        }

        cookieUtils.addCookie(response, "sessionId", sessionId);
        dao.storeToRedis("salt_" + sessionId, salt, sessionTime);

        model.put("salt", salt);
        model.put("sessionId", sessionId);

        return "/signIn";
    }

    /**
     * 显示登录结果
     * @param model
     * @param sessionId
     * @return
     */
    @GetMapping("/user/signResult")
    public String signResult(HttpServletResponse response, Map<String, Object> model, String sessionId) {
        model.put("result", dao.getFromRedis("result_" + sessionId));
        model.put("oldSessionId", sessionId);
        return signIn(response, model);
    }

    /**
     * 用户登录，注销，重复登录
     * @param name
     * @param json
     * @return
     */
    @PostMapping("/user/{name}")
    public String user(HttpServletRequest request, HttpServletResponse response, @PathVariable("name") String name, String json) {
        logger.info("user start, name:" + name + ", json:" + json);

        String page;

        Map<String, String> result = null;
        if (name.equals("signIn")) {
            result = writer.gson.fromJson(sysClient.signIn(json), new TypeToken<Map<String, String>>(){}.getType());

        } else if (name.equals("signOut")) {
            cookieUtils.delCookie(request, response, "sessionId");
            result = writer.gson.fromJson(sysClient.signOut(json), new TypeToken<Map<String, String>>(){}.getType());

        } else if (name.equals("hasLoginDeal")) {
            result = writer.gson.fromJson(sysClient.hasLoginDeal(json), new TypeToken<Map<String, String>>(){}.getType());
        }

        if (result.get("result").equals(CommonConstant.success)) {
            page = "redirect:/";

            if (name.equals("signOut")) {
                page = "redirect:/sys/user/signIn";
            }

        } else {
            Map<String, String> formInfo = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());

            dao.storeToRedis("result_" + formInfo.get("sessionId"), result.get("result"), 30);
            page = "redirect:/sys/user/signResult?sessionId=" + formInfo.get("sessionId");

            if (name.equals("signOut")) {
                page = "redirect:/";
            }
        }

        logger.info("user " + name + " end");

        return page;
    }
}
