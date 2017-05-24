package com.hzg.sys;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.SignInUtil;
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

        }else if (entity.equalsIgnoreCase(PrivilegeResource.class.getSimpleName())) {
            PrivilegeResource privilegeResource = writer.gson.fromJson(json, PrivilegeResource.class);
            privilegeResource.setInputDate(inputDate);
            result = sysDao.save(privilegeResource);

        }else if (entity.equalsIgnoreCase(Audit.class.getSimpleName())) {
            Audit audit = writer.gson.fromJson(json, Audit.class);
            audit.setInputDate(inputDate);
            result = sysDao.save(audit);

        }else if (entity.equalsIgnoreCase(AuditFlow.class.getSimpleName())) {
            AuditFlow auditFlow = writer.gson.fromJson(json, AuditFlow.class);
            auditFlow.setInputDate(inputDate);
            result = sysDao.save(auditFlow);

            Set<AuditFlowNode> auditFlowNodes = auditFlow.getAuditFlowNodes();
            for (AuditFlowNode auditFlowNode : auditFlowNodes) {
                auditFlowNode.setAuditFlow(auditFlow);
                sysDao.save(auditFlowNode);
            }
        }

        writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
        logger.info("save end, result:" + result);
    }

    /**
     * 保存实体列表
     * @param response
     * @param entity
     * @param json
     */
    @Transactional
    @PostMapping("/saveList")
    public void saveList(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("saveList start, parameter:" + entity + ":" + json);

        int index = 0;
        String result = "save " + index + " items success";

        writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
        logger.info("saveList end, result:" + result);
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

            List<Integer> relateIds = new ArrayList<>();
            for (PrivilegeResource privilegeResource : post.getPrivilegeResources()) {
                relateIds.add(privilegeResource.getId());
            }

            Post dbPost = (Post) sysDao.queryById(post.getId(), Post.class);
            List<Integer> unRelateIds = new ArrayList<>();
            for (PrivilegeResource privilegeResource : dbPost.getPrivilegeResources()) {
                unRelateIds.add(privilegeResource.getId());
            }

            sysDao.updateRelateId(post.getId(), relateIds, unRelateIds, Post.class);
            result = sysDao.updateById(post.getId(), post);

        }else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            Dept dept = writer.gson.fromJson(json, Dept.class);
            result = sysDao.updateById(dept.getId(), dept);

        }else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            Company company = writer.gson.fromJson(json, Company.class);
            result = sysDao.updateById(company.getId(), company);

        }else if (entity.equalsIgnoreCase(PrivilegeResource.class.getSimpleName())) {
            PrivilegeResource privilegeResource = writer.gson.fromJson(json, PrivilegeResource.class);
            result = sysDao.updateById(privilegeResource.getId(), privilegeResource);

        } else if (entity.equalsIgnoreCase(Audit.class.getSimpleName())) {
            Audit audit = writer.gson.fromJson(json, Audit.class);
            result = sysDao.updateById(audit.getId(), audit);
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

        }else if (entity.equalsIgnoreCase(PrivilegeResource.class.getSimpleName())) {
            PrivilegeResource privilegeResource = writer.gson.fromJson(json, PrivilegeResource.class);
            List<PrivilegeResource> privilegeResources = sysDao.query(privilegeResource);
            writer.writeObjectToJson(response, privilegeResources);

        }else if (entity.equalsIgnoreCase(Audit.class.getSimpleName())) {
            Audit audit = writer.gson.fromJson(json, Audit.class);
            List<Audit> audits = sysDao.query(audit);
            writer.writeObjectToJson(response, audits);

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

        } else if (entity.equalsIgnoreCase(PrivilegeResource.class.getSimpleName())) {
            PrivilegeResource privilegeResource = writer.gson.fromJson(json, PrivilegeResource.class);
            List<PrivilegeResource> privilegeResources = sysDao.suggest(privilegeResource);
            writer.writeObjectToJson(response, privilegeResources);

        } else if (entity.equalsIgnoreCase(AuditFlow.class.getSimpleName())) {
            AuditFlow auditFlow = writer.gson.fromJson(json, AuditFlow.class);
            List<AuditFlow> auditFlows = sysDao.suggest(auditFlow);
            writer.writeObjectToJson(response, auditFlows);
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

        }else if (entity.equalsIgnoreCase(PrivilegeResource.class.getSimpleName())) {
            List<PrivilegeResource> privilegeResources = sysDao.complexQuery(PrivilegeResource.class, queryParameters, position, rowNum);
            writer.writeObjectToJson(response, privilegeResources);

        }else if (entity.equalsIgnoreCase(Audit.class.getSimpleName())) {
            List<Audit> audits = sysDao.complexQuery(Audit.class, queryParameters, position, rowNum);
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
    public void audit(HttpServletResponse response, @RequestBody String json) {
        logger.info("audit start, parameter:" + json);
        String result = "fail";

        Map<String, String> auditInfo = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        User user = (User)sysDao.getFromRedis((String)sysDao.getFromRedis("sessionId_" + auditInfo.get("sessionId")));

        if (user == null) {
            writer.writeStringToJson(response, "{\"result\":\"会话信息丢失，请重新登录后办理、审核事宜\"}");
            return;
        }

        /**
         * 保存当前办理、审核的事宜
         */
        Audit audit = writer.gson.fromJson(json, Audit.class);

        Audit dbAudit = (Audit) sysDao.queryById(audit.getId(), Audit.class);
        if (dbAudit.getState() == 0) {
            writer.writeStringToJson(response, "{\"result\":\"不能重复办理已办理、审核的事宜\"}");
            return ;
        }

        audit.setUser(user);
        audit.setState(0);
        audit.setDealDate(new Timestamp(System.currentTimeMillis()));
        sysDao.updateById(audit.getId(), audit);

        AuditFlow auditFlow = new AuditFlow();
        auditFlow.setEntity(audit.getEntity());
        auditFlow.setCompany(dbAudit.getCompany());
        auditFlow.setState(0);

        List<AuditFlow> dbAuditFlows =  sysDao.query(auditFlow);

        if (!dbAuditFlows.isEmpty()) {
            AuditFlow dbAuditFlow =  dbAuditFlows.get(0);

            AuditFlowNode auditFlowNode = new AuditFlowNode();
            auditFlowNode.setAuditFlow(dbAuditFlow);
            auditFlowNode.setPost(audit.getPost());

            List<AuditFlowNode> dbAuditFlowNodes = sysDao.query(auditFlowNode);
            if (!dbAuditFlowNodes.isEmpty()) {
                AuditFlowNode dbAuditFlowNode = dbAuditFlowNodes.get(0);

                if (dbAuditFlowNode.getNextPost() != null) {
                    /**
                     * 查询到下一个工作流程节点，则设置下一个事宜、审核节点
                     */
                    Audit newAudit = new Audit();
                    newAudit.setState(1);
                    newAudit.setInputDate(new Timestamp(System.currentTimeMillis()));

                    newAudit.setName(dbAuditFlowNode.getName());
                    newAudit.setPost(dbAuditFlowNode.getNextPost());

                    newAudit.setCompany(dbAuditFlow.getCompany());
                    newAudit.setEntity(dbAuditFlow.getEntity());
                    newAudit.setEntityId(dbAudit.getEntityId());

                    sysDao.save(newAudit);

                } else {

                }

                result = "success";
            }

        }

        writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
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

        String result = "fail";

        Map<String, String> signInInfo = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        String username = signInInfo.get("username");

        long waitTime = signInUtil.userWait(username);
        if (waitTime > 0) {
            result = "请等待" + (waitTime/1000) + "秒后再次登录";
            writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
            return ;
        }

        String salt = (String)sysDao.getFromRedis("salt_" + signInInfo.get("sessionId"));
        if (salt == null) {
            result = "加密信息丢失，请刷新后再次登录";
            writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
            return ;
        }

        List<User> dbUsers = sysDao.query(new User(username));
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

                result = "success";
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

        } else if (dbUsers.size() > 1){
           result = dbUsers.get(0).getName() + "是重名用户，请联系管理员处理";
        }

        /**
         * 用户已经登录
         */
        if (signInUtil.isUserExist(username) && result.equals("success")) {
            if (signInUtil.getSessionIdByUser(username) != null) {
                if (!signInUtil.getSessionIdByUser(username).equals(signInInfo.get("sessionId"))) {
                    sysDao.storeToRedis(username + "_" + signInInfo.get("sessionId"), dbUsers.get(0), 1800);
                    result = username + "已经登录";
                }
            }
        }

        /**
         * 登录成功,设置用户,该用户权限资源到 redis
         */
        if (result.equals("success")) {
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

            sysDao.storeToRedis(username, dbUsers.get(0), 1800);
            sysDao.storeToRedis(username + "_resources", resources, 1800);
            signInUtil.setUser(signInInfo.get("sessionId"), username);
        }

        logger.info("signIn end");

        writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
    }


    /**
     * 用户注销
     */
    @RequestMapping(value="/signOut")
    public void signOut(HttpServletResponse response,  @RequestBody String json) {
        logger.info("signOut start, parameter:" + json);

        String result = "fail";

        Map<String, String> signOutInfo = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        String sessionId =  signOutInfo.get("sessionId");

        String username = (String)sysDao.getFromRedis("sessionId_" + sessionId);
        if (username != null) {
            sysDao.deleteFromRedis(username);
            sysDao.deleteFromRedis(username + "_resources");
            sysDao.deleteFromRedis("salt_" + sessionId);
            signInUtil.removeUser(username);

            logger.info(username + "注销");

            result = "success";
        }

        logger.info("signOut end");

        writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
    }

    /**
     * 处理重复登录
     */
    @RequestMapping(value="/hasLoginDeal")
    public void hasLoginDeal(HttpServletResponse response,  @RequestBody String json) {
        logger.info("hasLoginDeal start, parameter:" + json);

        String result = "fail";

        Map<String, String> dealInfo = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        String username = dealInfo.get("username");
        String sessionId = dealInfo.get("sessionId");
        String tempUserKey = username + "_" + sessionId;

        if (dealInfo.get("dealType").equals("againSignIn")) {
            User user = (User) sysDao.getFromRedis(tempUserKey);

            if (user != null) {
                //移除之前登录的用户
                signInUtil.removeUser(username);

                sysDao.storeToRedis(username, user, 1800);
                signInUtil.setUser(sessionId, username);

                sysDao.deleteFromRedis(tempUserKey);

                result = "success";
            }

        } else {
            result = "no operation";
        }

        logger.info("hasLoginDeal end");

        writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
    }
}
