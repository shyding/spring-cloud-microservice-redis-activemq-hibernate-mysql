package com.hzg.sys;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.*;
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
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

@Controller
@RequestMapping("/sys")
public class SysController {

    Logger logger = Logger.getLogger(SysController.class);

    @Autowired
    private SysDao sysDao;

    @Autowired
    private Writer writer;

    @Autowired
    private SignInUtil signInUtil;

    @Autowired
    private SysService sysService;

    @Autowired
    private Transcation transcation;

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

        String result = CommonConstant.fail;
        Timestamp inputDate = new Timestamp(System.currentTimeMillis());

        try {
            if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
                User user = writer.gson.fromJson(json, User.class);

                if (!sysDao.isValueRepeat(User.class, "username", user.getUsername(), user.getId())) {
                    user.setInputDate(inputDate);

                    if (user.getPassword().length() < 32) {
                        user.setPassword(DigestUtils.md5Hex(user.getPassword()).toUpperCase());
                    }

                    result = sysDao.save(user);

                } else {
                    result = CommonConstant.fail + ",用户名已经存在";
                }

            } else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
                Post post = writer.gson.fromJson(json, Post.class);
                post.setInputDate(inputDate);
                result = sysDao.save(post);

            } else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
                Dept dept = writer.gson.fromJson(json, Dept.class);
                dept.setInputDate(inputDate);
                result = sysDao.save(dept);

            } else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
                Company company = writer.gson.fromJson(json, Company.class);
                company.setInputDate(inputDate);
                result = sysDao.save(company);

            } else if (entity.equalsIgnoreCase(PrivilegeResource.class.getSimpleName())) {
                PrivilegeResource privilegeResource = writer.gson.fromJson(json, PrivilegeResource.class);
                privilegeResource.setInputDate(inputDate);
                result = sysDao.save(privilegeResource);

            } else if (entity.equalsIgnoreCase(Audit.class.getSimpleName())) {
                Audit audit = writer.gson.fromJson(json, Audit.class);
                audit.setInputDate(inputDate);
                result = sysDao.save(audit);

            } else if (entity.equalsIgnoreCase(AuditFlow.class.getSimpleName())) {
                AuditFlow auditFlow = writer.gson.fromJson(json, AuditFlow.class);
                auditFlow.setInputDate(inputDate);
                result = sysDao.save(auditFlow);

                Set<AuditFlowNode> auditFlowNodes = auditFlow.getAuditFlowNodes();
                for (AuditFlowNode auditFlowNode : auditFlowNodes) {
                    auditFlowNode.setAuditFlow(auditFlow);
                    sysDao.save(auditFlowNode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("save end, result:" + result);
    }

    @Transactional
    @PostMapping("/update")
    public void update(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("update start, parameter:" + entity + ":" + json);

        String result = CommonConstant.fail;

        try {
            if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
                User user = writer.gson.fromJson(json, User.class);
                if (!sysDao.isValueRepeat(User.class, "username", user.getUsername(), user.getId()) || user.getUsername() == null) {

                    if (user.getPosts() != null) {
                        List<Integer> relateIds = new ArrayList<>();
                        for (Post post : user.getPosts()) {
                            relateIds.add(post.getId());
                        }

                        User dbUser = (User) sysDao.queryById(user.getId(), User.class);
                        List<Integer> unRelateIds = new ArrayList<>();
                        if (dbUser.getPosts() != null) {
                            for (Post post : dbUser.getPosts()) {
                                unRelateIds.add(post.getId());
                            }
                        }

                        result += sysDao.updateRelateId(user.getId(), relateIds, unRelateIds, User.class);
                    }

                    /**
                     * 由于保存实体后，就马上从数据库查询该实体，导致关联关系还是旧的关联关系，
                     * 所以先重置关联关系，再保存实体，这样查询出来的关联关系就是最新的
                     */

                    result += sysDao.updateById(user.getId(), user);
                } else {
                    result = CommonConstant.fail + ",用户名已经存在";
                }

            } else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
                Post post = writer.gson.fromJson(json, Post.class);

                if (post.getPrivilegeResources() != null) {
                    List<Integer> relateIds = new ArrayList<>();
                    for (PrivilegeResource privilegeResource : post.getPrivilegeResources()) {
                        relateIds.add(privilegeResource.getId());
                    }

                    Post dbPost = (Post) sysDao.queryById(post.getId(), Post.class);
                    List<Integer> unRelateIds = new ArrayList<>();
                    if (dbPost.getPrivilegeResources() != null) {
                        for (PrivilegeResource privilegeResource : dbPost.getPrivilegeResources()) {
                            unRelateIds.add(privilegeResource.getId());
                        }
                    }

                    result += sysDao.updateRelateId(post.getId(), relateIds, unRelateIds, Post.class);
                }

                result += sysDao.updateById(post.getId(), post);

            } else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
                Dept dept = writer.gson.fromJson(json, Dept.class);
                result = sysDao.updateById(dept.getId(), dept);

            } else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
                Company company = writer.gson.fromJson(json, Company.class);
                result = sysDao.updateById(company.getId(), company);

            } else if (entity.equalsIgnoreCase(PrivilegeResource.class.getSimpleName())) {
                PrivilegeResource privilegeResource = writer.gson.fromJson(json, PrivilegeResource.class);
                result = sysDao.updateById(privilegeResource.getId(), privilegeResource);

            } else if (entity.equalsIgnoreCase(Audit.class.getSimpleName())) {
                Audit audit = writer.gson.fromJson(json, Audit.class);
                result = sysDao.updateById(audit.getId(), audit);

            } else if (entity.equalsIgnoreCase(AuditFlow.class.getSimpleName())) {
                AuditFlow auditFlow = writer.gson.fromJson(json, AuditFlow.class);
                result = sysDao.updateById(auditFlow.getId(), auditFlow);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("update end, result:" + result);
    }

    @Transactional
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public void delete(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("delete start, parameter:" + entity + ":" + json);
        String result = CommonConstant.fail;

        try {
            if (entity.equalsIgnoreCase(Audit.class.getSimpleName())) {
                result = sysDao.delete(writer.gson.fromJson(json, Audit.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeObjectToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("delete end");
    }

    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public void query(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("query start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            User user = writer.gson.fromJson(json, User.class);
            writer.writeObjectToJson(response, sysDao.query(user));

        }else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            Post post = writer.gson.fromJson(json, Post.class);
            writer.writeObjectToJson(response, sysDao.query(post));

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            Dept dept = writer.gson.fromJson(json, Dept.class);
            writer.writeObjectToJson(response, sysDao.query(dept));

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            Company company = writer.gson.fromJson(json, Company.class);
            writer.writeObjectToJson(response, sysDao.query(company));

        }else if (entity.equalsIgnoreCase(PrivilegeResource.class.getSimpleName())) {
            PrivilegeResource privilegeResource = writer.gson.fromJson(json, PrivilegeResource.class);
            writer.writeObjectToJson(response, sysDao.query(privilegeResource));

        }else if (entity.equalsIgnoreCase(Audit.class.getSimpleName())) {
            Audit audit = writer.gson.fromJson(json, Audit.class);
            writer.writeObjectToJson(response, sysDao.query(audit));

        }else if (entity.equalsIgnoreCase(AuditFlow.class.getSimpleName())) {
            AuditFlow auditFlow = writer.gson.fromJson(json, AuditFlow.class);
            List<AuditFlow> auditFlows = sysDao.query(auditFlow);

            Set<AuditFlowNode> nodes = null;
            if (!auditFlows.isEmpty()) {
                nodes = auditFlows.get(0).getAuditFlowNodes();

                for (AuditFlowNode node : nodes) {
                    node.setPost((Post) sysDao.queryById(node.getPost().getId(), Post.class));
                }
            }
            writer.writeObjectToJson(response, auditFlows);
        }

        logger.info("query end");
    }

    @RequestMapping(value = "/suggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void suggest(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("suggest start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            User user = writer.gson.fromJson(json, User.class);
            user.setState(0);

            Field[] limitFields = new Field[1];
            try {
                limitFields[0] = user.getClass().getDeclaredField("state");
            } catch (Exception e) {
                e.printStackTrace();
            }

            writer.writeObjectToJson(response, sysDao.suggest(user, limitFields));

        }else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            Post post = writer.gson.fromJson(json, Post.class);
            writer.writeObjectToJson(response, sysDao.suggest(post, null));

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            Dept dept = writer.gson.fromJson(json, Dept.class);
            writer.writeObjectToJson(response, sysDao.suggest(dept, null));

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            Company company = writer.gson.fromJson(json, Company.class);
            writer.writeObjectToJson(response,  sysDao.suggest(company, null));

        } else if (entity.equalsIgnoreCase(PrivilegeResource.class.getSimpleName())) {
            PrivilegeResource privilegeResource = writer.gson.fromJson(json, PrivilegeResource.class);
            writer.writeObjectToJson(response, sysDao.suggest(privilegeResource, null));

        } else if (entity.equalsIgnoreCase(AuditFlow.class.getSimpleName())) {
            AuditFlow auditFlow = writer.gson.fromJson(json, AuditFlow.class);
            auditFlow.setState(0);

            Field[] limitFields = new Field[1];
            try {
                limitFields[0] = auditFlow.getClass().getDeclaredField("state");
            } catch (Exception e) {
                e.printStackTrace();
            }
            writer.writeObjectToJson(response,  sysDao.suggest(auditFlow, limitFields));
        }

        logger.info("suggest end");
    }

    @RequestMapping(value = "/complexQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void complexQuery(HttpServletResponse response, String entity, @RequestBody String json, int position, int rowNum){
        logger.info("complexQuery start, parameter:" + entity + ":" + json + "," + position + "," + rowNum);

        Map<String, String> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            List<User> users = sysDao.complexQuery(User.class, queryParameters, position, rowNum);
            writer.writeObjectToJson(response, users);

        }else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            List<Post> posts = sysDao.complexQuery(Post.class, queryParameters, position, rowNum);

            for (Post post : posts) {
                post.setDept((Dept)sysDao.queryById(post.getDept().getId(), Dept.class));
            }

            writer.writeObjectToJson(response, posts);

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            List<Dept> depts = sysDao.complexQuery(Dept.class, queryParameters, position, rowNum);
            writer.writeObjectToJson(response, depts);

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            List<Company> companies = sysDao.complexQuery(Company.class, queryParameters, position, rowNum);
            writer.writeObjectToJson(response, companies);

        }else if (entity.equalsIgnoreCase(PrivilegeResource.class.getSimpleName())) {
            List<PrivilegeResource> privilegeResources = sysDao.complexQuery(PrivilegeResource.class, queryParameters, position, rowNum);
            writer.writeObjectToJson(response, privilegeResources);

        }else if (entity.equalsIgnoreCase(Audit.class.getSimpleName())) {
            Integer userId = null;
            if (queryParameters.get("user") != null) {
                userId = Integer.valueOf(queryParameters.get("user"));

                /**
                 * 查询对应岗位的待办事宜
                 */
                if (Integer.valueOf(queryParameters.get("state")).compareTo(AuditFlowConstant.audit_state_todo) == 0) {
                    queryParameters.remove("user");
                }
            }

            List<Audit> audits = sysDao.complexQuery(Audit.class, queryParameters, position, rowNum);
            if (userId != null) {
                if (!audits.isEmpty()) {

                    for (int i = 0; i < audits.size(); i++) {
                        if (audits.get(i).getUser() != null) {

                            /**
                             * 去除指定办理人非本人的待办事宜
                             */
                            if (audits.get(i).getUser().getId().compareTo(userId) != 0 &&
                                    audits.get(i).getState().compareTo(AuditFlowConstant.audit_state_todo) == 0) {
                                audits.remove(i);
                                i--;
                            }
                        }
                    }
                }
            }

            writer.writeObjectToJson(response, audits);

        }else if (entity.equalsIgnoreCase(AuditFlow.class.getSimpleName())) {
            List<AuditFlow> auditFlows = sysDao.complexQuery(AuditFlow.class, queryParameters, position, rowNum);
            writer.writeObjectToJson(response, auditFlows);

        }

        logger.info("complexQuery end");
    }

    /**
     * 查询条件限制下的记录数
     * @param response
     * @param entity
     * @param json
     */
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

        }else if (entity.equalsIgnoreCase(PrivilegeResource.class.getSimpleName())) {
            recordsSum =  sysDao.recordsSum(PrivilegeResource.class, queryParameters);

        } else if (entity.equalsIgnoreCase(AuditFlow.class.getSimpleName())) {
            recordsSum =  sysDao.recordsSum(AuditFlow.class, queryParameters);
        }

        writer.writeStringToJson(response, "{\"recordsSum\":" + recordsSum + "}");

        logger.info("recordsSum end");
    }

    /**
     * 查询岗位已有权限，及没有的权限
     * @param response
     * @param json
     */
    @RequestMapping(value = "/queryPrivilege", method = {RequestMethod.GET, RequestMethod.POST})
    public void queryPrivilege(HttpServletResponse response, @RequestBody String json) {
        logger.info("queryPrivilege start, parameter:" + json);

        Map<String, Object> result = new HashMap();

        List<Post> posts = sysDao.query(writer.gson.fromJson(json, Post.class));
        String queryParameters = "{}";
        if (!posts.isEmpty()) {

            Post post = posts.get(0);
            result.put("post", post);

            String ids = "";
            for (PrivilegeResource resource : post.getPrivilegeResources()) {
                ids += resource.getId() + ",";
            }

            if (!ids.equals("")) {
                queryParameters = "{\"id\": \" not in (" + ids.substring(0, ids.length()-1) + ")\"}";
            }

        }

        List<PrivilegeResource> unAssignPrivileges = sysDao.complexQuery(PrivilegeResource.class,
                writer.gson.fromJson(queryParameters, new TypeToken<Map<String, String>>(){}.getType()), 0, -1);
        result.put("unAssignPrivileges", unAssignPrivileges);

        logger.info("queryPrivilege end");

        writer.writeObjectToJson(response, result);
    }

    /**
     * 办理、审核事宜
     * @param response
     * @param json
     */
    @Transactional
    @PostMapping("/audit")
    public void audit(HttpServletResponse response, @RequestBody String json){
        logger.info("audit start, parameter:" + json);
        String result = CommonConstant.fail, auditResult = CommonConstant.fail;

        Audit audit = writer.gson.fromJson(json, Audit.class);

        try {
            /**
             * 发起新流程，创建流程的第一个事宜节点
             */
            if (audit.getId() == null) {
                Audit firstAudit = sysService.getFirstAudit(audit);
                firstAudit.setNo(sysDao.getNo(AuditFlowConstant.no_prefix_audit));
                result += sysDao.save(firstAudit);

                auditResult = AuditFlowConstant.audit_do;
            }

            /**
             * 办理、审核事宜
             */
            else {
                Map<String, Object> auditInfo = writer.gson.fromJson(json, new TypeToken<Map<String, Object>>() {
                }.getType());
                User user = (User) sysDao.getFromRedis((String) sysDao.getFromRedis("sessionId_" + auditInfo.get("sessionId").toString()));

                if (user == null) {
                    result += CommonConstant.fail + ",会话信息丢失，请重新登录后办理、审核事宜";
                    writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
                    return;
                }

                Audit dbAudit = (Audit) sysDao.queryById(audit.getId(), Audit.class);
                if (dbAudit.getState().compareTo(AuditFlowConstant.audit_state_done) == 0) {
                    result += CommonConstant.fail + ",不能重复办理已办理、审核的事宜";
                    writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
                    return;
                }

                audit.setUser(user);
                audit.setState(AuditFlowConstant.audit_state_done);
                audit.setDealDate(new Timestamp(System.currentTimeMillis()));
                result += sysDao.updateById(audit.getId(), audit);


                /**
                 * 审核通过，处理相应的业务逻辑
                 */
                if (audit.getResult().equals(AuditFlowConstant.audit_pass)) {
                    Audit nextAudit = sysService.getNextAudit(dbAudit, AuditFlowConstant.flow_direct_forward);

                    /**
                     * 处理相应的业务逻辑
                     */
                    result += sysService.doAction(AuditFlowConstant.flow_direct_forward, dbAudit);

                    /**
                     * 有下一个节点, 插入下一个节点
                     */
                    if (nextAudit != null) {
                        result += sysDao.save(nextAudit);
                        auditResult = AuditFlowConstant.audit_pass;
                    }

                    /**
                     * 如果没有下一个节点，则流程办理完毕，调用流程结束动作
                     */
                    else {
                        result += sysService.doFlowAction(dbAudit);
                        auditResult = AuditFlowConstant.audit_finish;
                    }
                }

                /**
                 * 审核未通过，插入节点
                 */
                else {
                    Audit refuseAudit = null;
                    if (audit.getToRefuseUser() != null) {
                        refuseAudit = new Audit();

                        refuseAudit.setNo(dbAudit.getNo());
                        refuseAudit.setEntity(dbAudit.getEntity());
                        refuseAudit.setEntityId(dbAudit.getEntityId());

                        refuseAudit.setCompany(dbAudit.getCompany());
                        refuseAudit.setUser(audit.getToRefuseUser());

                        dbAudit.setToRefuseUser(audit.getToRefuseUser());
                        refuseAudit.setPost(sysService.getPostByAuditUser(dbAudit));

                        refuseAudit = sysService.getAudit(refuseAudit);
                        refuseAudit.setRefusePost(dbAudit.getPost());
                        refuseAudit.setName(dbAudit.getName());
                        refuseAudit.setState(AuditFlowConstant.audit_state_todo);


                    } else {
                        refuseAudit = sysService.getNextAudit(dbAudit, AuditFlowConstant.flow_direct_backwards);
                    }

                    /**
                     * 设置拒绝节点对应动作，处理相应的业务逻辑
                     */
                    result += sysService.doAction(AuditFlowConstant.flow_direct_backwards, refuseAudit);

                    result += sysDao.save(refuseAudit);
                    auditResult = AuditFlowConstant.audit_deny;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\", \"auditResult\":\"" + auditResult + "\"}");
        logger.info("audit end");
    }



    /**
     * 用户登录
     * @param response
     * @param json
     */
    @RequestMapping(value = "/signIn", method = {RequestMethod.GET, RequestMethod.POST})
    public void signIn(HttpServletResponse response, @RequestBody String json) {
        logger.info("signIn start, parameter:" + json);

        String result = CommonConstant.fail;

        Map<String, String> signInInfo = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        String username = signInInfo.get("username");

        long waitTime = signInUtil.userWait(username);
        if (waitTime > 0) {
            result = "请等待" + (waitTime/1000) + "秒后再次登录";
            writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
            return ;
        }

        String salt = (String)sysDao.getFromRedis("salt_" + signInInfo.get("sessionId"));
        if (salt == null) {
            result = "加密信息丢失，请刷新后再次登录";
            writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
            return ;
        }

        User user = new User(username);
        user.setState(0); //有效用户

        List<User> dbUsers = sysDao.query(user);
        if (dbUsers.size() == 1) {
            String encryptDbPassword = DigestUtils.md5Hex(dbUsers.get(0).getPassword().toUpperCase() + salt).toUpperCase();

            /**
             * 账号密码正确
             */
            if (encryptDbPassword.equals(signInInfo.get("encryptPassword"))) {
                Set<Post> posts = new HashSet<>();
                for (Post post : dbUsers.get(0).getPosts()) {
                    posts.add((Post)sysDao.queryById(post.getId(), Post.class));
                }
                dbUsers.get(0).setPosts(posts);

                result = CommonConstant.success;
                signInUtil.removeSignInFailInfo(username);

            } else {
                result = "用户名或密码错误";

                signInUtil.setSignInFailInfo(username);
                long waitSeconds = signInUtil.getWaitSeconds(username);
                if (waitSeconds != 0) {
                    result += "<br/>已<span style='color:#db6a41;padding-left:2px;padding-right:2px'>" +
                             signInUtil.getSignInFailCount(username) +
                             "</span>次登录错误，请等待" + waitSeconds + "秒后再次登录";

                }
            }

        } else if (dbUsers.size() < 1){
           result = "查询不到 " + user.getUsername() + "，或者该用户已注销";

        } else if (dbUsers.size() > 1){
           result = dbUsers.get(0).getUsername() + " 是重名用户，请联系管理员处理";
        }

        /**
         * 用户已经登录
         */
        if (signInUtil.isUserExist(username) && result.equals(CommonConstant.success)) {
            if (signInUtil.getSessionIdByUser(username) != null) {
                if (!signInUtil.getSessionIdByUser(username).equals(signInInfo.get("sessionId"))) {
                    sysDao.storeToRedis(username + "_" + signInInfo.get("sessionId"), dbUsers.get(0), signInUtil.sessionTime);
                    result = username + "已经登录";
                }
            }
        }

        /**
         * 登录成功,设置用户,该用户权限资源到 redis
         */
        if (result.equals(CommonConstant.success)) {
            String resources = "";
            for (Post post : dbUsers.get(0).getPosts()) {
                for (PrivilegeResource resource : post.getPrivilegeResources()) {
                    resources += resource.getUri() + ",";
                }

                /**
                 * 由于已经获得了权限，移除对象里的权限
                 */
                post.setPrivilegeResources(null);
            }

            sysDao.storeToRedis(username, dbUsers.get(0), signInUtil.sessionTime);
            sysDao.storeToRedis(username + "_resources", resources, signInUtil.sessionTime);
            signInUtil.setUser(signInInfo.get("sessionId"), username);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("signIn end");
    }


    /**
     * 用户注销
     */
    @RequestMapping(value="/signOut")
    public void signOut(HttpServletResponse response,  @RequestBody String json) {
        logger.info("signOut start, parameter:" + json);

        String result = CommonConstant.fail;

        Map<String, String> signOutInfo = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        String sessionId =  signOutInfo.get("sessionId");

        String username = (String)sysDao.getFromRedis("sessionId_" + sessionId);
        if (username != null) {
            sysDao.deleteFromRedis(username);
            sysDao.deleteFromRedis(username + "_resources");
            sysDao.deleteFromRedis("salt_" + sessionId);
            signInUtil.removeUser(username);

            logger.info(username + "注销");

            result = CommonConstant.success;
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("signOut end");
    }

    /**
     * 处理重复登录
     */
    @RequestMapping(value="/hasLoginDeal")
    public void hasLoginDeal(HttpServletResponse response,  @RequestBody String json) {
        logger.info("hasLoginDeal start, parameter:" + json);

        String result = CommonConstant.fail;

        Map<String, String> dealInfo = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        String username = dealInfo.get("username");
        String sessionId = dealInfo.get("sessionId");

        String tempUserKey = username + "_" +  dealInfo.get("oldSessionId");

        if (dealInfo.get("dealType").equals("againSignIn")) {
            User user = (User) sysDao.getFromRedis(tempUserKey);

            if (user != null) {
                //移除之前登录的用户
                sysDao.deleteFromRedis("salt_" + signInUtil.getSessionIdByUser(username));
                signInUtil.removeUser(username);

                sysDao.storeToRedis(username, user, signInUtil.sessionTime);
                signInUtil.setUser(sessionId, username);

                sysDao.deleteFromRedis(tempUserKey);

                result = CommonConstant.success;
            }

        } else {
            result = "no operation";
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("hasLoginDeal end");
    }
}
