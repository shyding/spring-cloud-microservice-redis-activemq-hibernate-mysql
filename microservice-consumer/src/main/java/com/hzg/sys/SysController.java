package com.hzg.sys;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.CookieUtils;
import com.hzg.tools.StrUtil;
import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private CookieUtils cookieUtils;

    @Autowired
    public Integer sessionTime;

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

            String queryJson = "";
            if (!entities.isEmpty()) {
                Audit audit = (Audit)entities.get(0);
                queryJson = "{\"entityId\":" + audit.getEntityId() + "}";

                List<AuditFlow> auditFlows =  writer.gson.fromJson(client.query(AuditFlow.class.getSimpleName(),
                        "{\"entity\":" + audit.getEntity() + ", \"company\":{\"id\":" + audit.getCompany().getId() +"}}"),
                              new TypeToken<List<AuditFlow>>() {}.getType());

                if (!auditFlows.isEmpty()) {
                    String refusePostOptions = "";

                    if (!auditFlows.get(0).getAuditFlowNodes().isEmpty()) {
                        AuditFlowNode[] auditFlowNodes = new AuditFlowNode[auditFlows.get(0).getAuditFlowNodes().size()];
                        auditFlows.get(0).getAuditFlowNodes().toArray(auditFlowNodes);

                        Arrays.sort(auditFlowNodes, new Comparator<AuditFlowNode>() {
                            @Override
                            public int compare(AuditFlowNode o1, AuditFlowNode o2) {
                                if (o1.getId().compareTo(o2.getId()) > 0) {
                                    return 1;
                                } else if(o1.getId().compareTo(o2.getId()) < 0) {
                                    return -1;
                                }

                                return 0;
                            }
                        });

                        int pos = 0;
                        AuditFlowNode currentNode = null, preNode = null;
                        for (int i = 0; i < auditFlowNodes.length; i++) {
                            if (audit.getPost().getId().compareTo(auditFlowNodes[i].getPost().getId()) == 0) {
                                currentNode = auditFlowNodes[i];
                                pos = i;
                                break;
                            }
                        }

                        if (currentNode != null) {
                            if (pos > 0) {
                                preNode = auditFlowNodes[pos-1];
                            }


                            for (int i = 0; i < auditFlowNodes.length; i++) {
                                if (auditFlowNodes[i].getId() < currentNode.getId()) {
                                    refusePostOptions += "<option value='" + auditFlowNodes[i].getPost().getId() + "'>" + auditFlowNodes[i].getPost().getName() + "</option>";

                                } else {
                                    break;
                                }
                            }
                        }

                        if (preNode != null) {
                            model.put("prePostOptions", "<option value='" + preNode.getPost().getId() + "'>上一节点</option>");
                        }
                        if (!refusePostOptions.equals("")) {
                            model.put("refusePostOptions", refusePostOptions);
                        }
                    }
                }
            }

            model.put("entities", writer.gson.fromJson(client.query(entity, queryJson), new TypeToken<List<Audit>>() {}.getType()));
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
     * @return
     */
    @GetMapping("/user/signIn")
    public String signIn(HttpServletResponse response, Map<String, Object> model) {
        String salt = strUtil.generateRandomStr(256);
        String sessionId = strUtil.generateRandomStr(32);

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

        if (result.get("result").equals("success")) {
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
