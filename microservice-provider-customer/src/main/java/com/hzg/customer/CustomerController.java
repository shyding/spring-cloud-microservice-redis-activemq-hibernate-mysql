package com.hzg.customer;

import com.google.gson.reflect.TypeToken;
import com.hzg.base.Dao;
import com.hzg.sys.Action;
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
@RequestMapping("/customer")
public class CustomerController {

    Logger logger = Logger.getLogger(CustomerController.class);

    @Autowired
    private Dao dao;

    @Autowired
    private SmsClient smsClient;

    @Autowired
    private Writer writer;

    @Autowired
    private SignInUtil signInUtil;

    @Autowired
    private Transcation transcation;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private StrUtil strUtil;

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
        Timestamp inputDate = dateUtil.getSecondCurrentTimestamp();

        try {
            /**
             * 客户、用户注册要验证短信验证码
             */
            Map<String, Object> saveData = writer.gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
            String validateCode = saveData.get(SmsConstant.validateCode).toString();
            String dbValidateCode = smsClient.getValidateCode(saveData.get(SmsConstant.phone).toString(), saveData.get(SmsConstant.validateCodeKey).toString());

            if (dbValidateCode != null && validateCode.equals(dbValidateCode)) {
                /**
                 * 客户注册
                 */
                if (entity.equalsIgnoreCase(Customer.class.getSimpleName())) {
                    Customer customer = writer.gson.fromJson(json, Customer.class);

                    Degree degree = new Degree();
                    degree.setLoyalty(CustomerConstant.customer_init_loyalty);
                    degree.setCredit(CustomerConstant.customer_init_credit);
                    degree.setLevel(CustomerConstant.customer_level_normal);
                    degree.setSpendAmount(0f);
                    degree.setSpendCount(0);
                    degree.setSpendPoints(0);
                    result += dao.save(degree);

                    customer.setDegree(degree);
                    customer.setInputDate(inputDate);
                    result += dao.save(customer);

                    Customer idCustomer = new Customer();
                    idCustomer.setId(customer.getId());

                    User user = (User) customer.getUsers().toArray()[0];
                    if (!dao.isValueRepeat(User.class, CommonConstant.username, user.getUsername(), user.getId())) {
                        user.setCustomer(idCustomer);
                        user.setInputDate(inputDate);
                        user.setState(CustomerConstant.user_state_onUse);
                        user.setOnlineTime(0);
                        user.setClickCount(0);
                        user.setLoginCount(0);
                        user.setPoints(0);

                        result += dao.save(user);

                        /**
                         * 一对多保存时，为一的实体里的子元素 id 为空，重新设置 redis 里为一的实体里的子元素
                         */
                        customer.getUsers().clear();
                        customer.getUsers().add(user);
                        dao.storeToRedis(customer.getClass().getName() + CommonConstant.underline + customer.getId(), customer);

                    } else {
                        result = CommonConstant.fail + ",用户名已经存在";
                    }

                    for (Express express : customer.getExpresses()) {
                        express.setCustomer(idCustomer);
                        express.setState(CustomerConstant.express_state_onUse);
                        express.setDefaultUse(CustomerConstant.express_default_use);
                        result += dao.save(express);
                    }

                } else if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
                    User singInUser = (User) dao.getFromRedis((String) dao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + saveData.get(CommonConstant.sessionId).toString()));

                    User registryUser = writer.gson.fromJson(json, User.class);
                    if (!dao.isValueRepeat(User.class, CommonConstant.username, registryUser.getUsername(), registryUser.getId())) {
                        Customer idCustomer = new Customer();
                        idCustomer.setId(singInUser.getCustomer().getId());
                        registryUser.setCustomer(idCustomer);

                        result += dao.save(registryUser);

                        /**
                         * 多次增加一对多中为一的实体里的子元素，需要重新设置为一的实体到 redis
                         */
                        singInUser.getCustomer().getUsers().add(registryUser);
                        dao.storeToRedis(singInUser.getCustomer().getClass().getName() + CommonConstant.underline + singInUser.getCustomer().getId(), singInUser.getCustomer());

                    }  else {
                        result = CommonConstant.fail + ",用户名已经存在";
                    }
                }

                if (!result.contains(CommonConstant.fail)) {
                    smsClient.delValidateCode(saveData.get(SmsConstant.phone).toString(), saveData.get(SmsConstant.validateCodeKey).toString());
                }

            } else {
                if (dbValidateCode != null) {
                    result += CommonConstant.fail + ",验证码不正确，不能保存信息";

                } else {
                    result += CommonConstant.fail + ",验证码已经过期，请重新获取验证码，来保存信息";
                }
            }

            if (entity.equalsIgnoreCase(Express.class.getSimpleName())) {
                User singInUser = (User) dao.getFromRedis((String) dao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + saveData.get(CommonConstant.sessionId).toString()));

                Express express = writer.gson.fromJson(json, Express.class);
                Customer idCustomer = new Customer();
                idCustomer.setId(singInUser.getCustomer().getId());
                express.setCustomer(idCustomer);

                result += dao.save(express);

                singInUser.getCustomer().getExpresses().add(express);
                dao.storeToRedis(singInUser.getCustomer().getClass().getName() + CommonConstant.underline + singInUser.getCustomer().getId(), singInUser.getCustomer());
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

        User signUser = getSignUser(json);

        try {
            if (entity.equalsIgnoreCase(Customer.class.getSimpleName())) {
                Customer customer = writer.gson.fromJson(json, Customer.class);

                if (customer.getId().compareTo(signUser.getCustomer().getId()) == 0) {
                    result += dao.updateById(customer.getId(), customer);
                }

            } else if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
                User user = writer.gson.fromJson(json, User.class);

                if (user.getId().compareTo(signUser.getId()) == 0) {
                    result += dao.updateById(user.getId(), user);
                }

            } else if (entity.equalsIgnoreCase(Express.class.getSimpleName())) {
                Express express = writer.gson.fromJson(json, Express.class);

                Express dbExpress = (Express) dao.queryById(express.getId(), express.getClass());
                if (dbExpress.getCustomer().getId().compareTo(signUser.getCustomer().getId()) == 0) {
                    result += dao.updateById(express.getId(), express);
                }
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
    @PostMapping("/business")
    public void business(HttpServletResponse response, String name, @RequestBody String json){
        logger.info("business start, parameter:" + name + ":" + json);

        String result = CommonConstant.fail;

        try {
            if (name.equalsIgnoreCase(CustomerConstant.user_action_name_modifyPassword)) {
                Action action = writer.gson.fromJson(json, Action.class);
                User user = (User) dao.queryById(action.getEntityId(), User.class);

                Object sessionUser = getUserBySessionId(action.getSessionId());
                if (sessionUser != null) {
                    /**
                     * 由于前台用户及后台用户（管理员）都可以修改前台用户密码
                     * 如果是前台用户自己修改，则判断修改人和要修改密码的用户是否是同一个人
                     */
                    if (sessionUser instanceof User) {
                        if (((User)sessionUser).getId().compareTo(user.getId()) != 0) {
                            result += CommonConstant.fail + "，非法操作，用户只能修改自己的密码";
                        }
                    }

                    if (!result.substring(CommonConstant.fail.length()).contains(CommonConstant.fail)) {
                        if (user.getPassword().equals(action.getOldPassword())) {
                            user.setPassword(action.getNewPassword());
                            result += dao.updateById(user.getId(), user);

                            action.setEntity(CustomerConstant.user);
                            action.setType(CustomerConstant.user_action_modifyPassword);
                            /**
                             * 前台用户和后台用户类型不一致，这里如果是前台用户，则把前台用户简单转换为后台用户对象里进行保存
                             */
                            action.setInputer(writer.gson.fromJson(writer.gson.toJson(sessionUser), com.hzg.sys.User.class));
                            action.setInputDate(dateUtil.getSecondCurrentTimestamp());
                            result += dao.save(action);

                        } else {
                            result += CommonConstant.fail + ",旧密码不对，不能修改密码";
                        }
                    }
                } else {
                    result += CommonConstant.fail + ",用户没有登录或会话已过期，不能修改密码";
                }

            } else if (name.equalsIgnoreCase(CustomerConstant.user_action_name_resetPassword)) {
                Action action = writer.gson.fromJson(json, Action.class);
                User user = (User) dao.queryById(action.getEntityId(), User.class);

                Object sessionUser = getUserBySessionId(action.getSessionId());
                if (sessionUser != null) {
                    String newPassword = strUtil.generateRandomStr(8);
                    user.setPassword(DigestUtils.md5Hex(newPassword).toUpperCase());
                    result += dao.updateById(user.getId(), user);

                    action.setEntity(CustomerConstant.user);
                    action.setType(CustomerConstant.user_action_resetPassword);
                    /**
                     * 前台用户和后台用户类型不一致，这里如果是前台用户，则把前台用户简单转换为后台用户对象里进行保存
                     */
                    action.setInputer(writer.gson.fromJson(writer.gson.toJson(sessionUser), com.hzg.sys.User.class));
                    action.setInputDate(dateUtil.getSecondCurrentTimestamp());
                    result += dao.save(action);

                    result = transcation.dealResult(result);
                    writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\",\"newPassword\":\"" + newPassword + "\"}");
                    logger.info("business end, result:" + result);
                    return;

                } else {
                    result += CommonConstant.fail + ",用户没有登录或会话已过期，不能重置密码";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("business end, result:" + result);
    }

    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public void query(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("query start, parameter:" + entity + ":" + json);

        User signUser = getSignUser(json);

        if (entity.equalsIgnoreCase(Customer.class.getSimpleName())) {
            writer.writeObjectToJson(response, writer.gson.toJson(dao.queryById(signUser.getCustomer().getId(), signUser.getCustomer().getClass())));

        } else if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            writer.writeObjectToJson(response, signUser);

        } else if (entity.equalsIgnoreCase(Express.class.getSimpleName())) {
            Express express = writer.gson.fromJson(json, Express.class);
            express.setCustomer(signUser.getCustomer());
            writer.writeObjectToJson(response, dao.query(express));
        }

        logger.info("query end");
    }

    @RequestMapping(value = "/unlimitedQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void unlimitedQuery(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("unlimitedQuery start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(Customer.class.getSimpleName())) {
            writer.writeObjectToJson(response, dao.query(writer.gson.fromJson(json, Customer.class)));

        } if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            writer.writeObjectToJson(response, dao.query(writer.gson.fromJson(json, User.class)));

        } else if (entity.equalsIgnoreCase(Express.class.getSimpleName())) {
            writer.writeObjectToJson(response, dao.query(writer.gson.fromJson(json, Express.class)));
        }

        logger.info("unlimitedQuery end");
    }

    @RequestMapping(value = "/suggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void suggest(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("suggest start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            User user = writer.gson.fromJson(json, User.class);
            /**
             * 用户只能查询自己所属同一个客户下的所有账户
             */
            User sessionUser = (User) getUserBySessionId(user.getSessionId());
            user.setCustomer(sessionUser.getCustomer());

            List<User> users = dao.suggest(user, null);
            for (User ele : users) {
                ele.setCustomer((Customer) dao.queryById(ele.getCustomer().getId(), ele.getCustomer().getClass()));
            }

            writer.writeObjectToJson(response, users);
        }

        logger.info("suggest end");
    }

    @RequestMapping(value = "/unlimitedSuggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void unlimitedSuggest(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("unlimitedSuggest start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(Customer.class.getSimpleName())) {
            writer.writeObjectToJson(response, dao.suggest(writer.gson.fromJson(json, Customer.class), null));

        } else if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            List<User> users = dao.suggest(writer.gson.fromJson(json, User.class), null);
            for (User ele : users) {
                ele.setCustomer((Customer) dao.queryById(ele.getCustomer().getId(), ele.getCustomer().getClass()));
            }
            writer.writeObjectToJson(response, users);
        }

        logger.info("unlimitedSuggest end");
    }

    @RequestMapping(value = "/unlimitedComplexQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void unlimitedComplexQuery(HttpServletResponse response, String entity, @RequestBody String json, int position, int rowNum){
        logger.info("unlimitedComplexQuery start, parameter:" + entity + ":" + json + "," + position + "," + rowNum);

        if (entity.equalsIgnoreCase(Customer.class.getSimpleName())) {
            writer.writeObjectToJson(response, dao.complexQuery(Customer.class,
                    writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType()), position, rowNum));

        } else if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            writer.writeObjectToJson(response, dao.complexQuery(User.class,
                    writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType()), position, rowNum));
        }

        logger.info("unlimitedComplexQuery end");
    }

    /**
     * 查询条件限制下的记录数
     * @param response
     * @param entity
     * @param json
     */
    @RequestMapping(value = "/unlimitedRecordsSum", method = {RequestMethod.GET, RequestMethod.POST})
    public void unlimitedRecordsSum(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("unlimitedRecordsSum start, parameter:" + entity + ":" + json);
        BigInteger recordsSum = new BigInteger("-1");

        if (entity.equalsIgnoreCase(Customer.class.getSimpleName())) {
            recordsSum = dao.recordsSum(Customer.class, writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType()));

        } else if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            recordsSum = dao.recordsSum(User.class, writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType()));
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.recordsSum + "\":" + recordsSum + "}");
        logger.info("unlimitedRecordsSum end");
    }

    public User getSignUser(String json) {
        Map<String, Object> jsonData = writer.gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
        User user = (User) dao.getFromRedis((String) dao.getFromRedis(CommonConstant.sessionId +
                CommonConstant.underline + (String) jsonData.get(CommonConstant.sessionId)));

        User signUser = new User();
        signUser.setId(user.getId());
        signUser.setCustomer(user.getCustomer());

        return signUser;
    }

    /**
     * 用户登录
     * @param response
     * @param json
     */
    @RequestMapping(value = "/user/signIn", method = {RequestMethod.GET, RequestMethod.POST})
    public void signIn(HttpServletResponse response, @RequestBody String json) {
        logger.info("signIn start, parameter:" + json);

        String result = CommonConstant.fail;

        Map<String, String> signInInfo = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        String username = signInInfo.get(CommonConstant.username);

        long waitTime = signInUtil.userWait(username);
        if (waitTime > 0) {
            result = "请等待" + (waitTime/1000) + "秒后再次登录";
            writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
            return ;
        }

        String salt = (String)dao.getFromRedis(CommonConstant.salt + CommonConstant.underline + signInInfo.get(CommonConstant.sessionId));
        if (salt == null) {
            result = "加密信息丢失，请刷新后再次登录";
            writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
            return ;
        }

        User user = new User(username);
        user.setState(0); //有效用户

        List<User> dbUsers = dao.query(user);
        if (dbUsers.size() == 1) {
            String encryptDbPassword = DigestUtils.md5Hex(dbUsers.get(0).getPassword().toUpperCase() + salt).toUpperCase();

            /**
             * 账号密码正确
             */
            if (encryptDbPassword.equals(signInInfo.get("encryptPassword"))) {
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
                if (!signInUtil.getSessionIdByUser(username).equals(signInInfo.get(CommonConstant.sessionId))) {
                    dao.storeToRedis(username + CommonConstant.underline + signInInfo.get(CommonConstant.sessionId), dbUsers.get(0), signInUtil.sessionTime);
                    result = username + "已经登录";
                }
            }
        }

        /**
         * 登录成功,设置用户,该用户权限资源到 redis
         */
        if (result.equals(CommonConstant.success)) {
            dao.storeToRedis(username, dbUsers.get(0), signInUtil.sessionTime);
            signInUtil.setUser(signInInfo.get(CommonConstant.sessionId), username);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("signIn end");
    }


    /**
     * 用户注销
     */
    @RequestMapping(value="/user/signOut")
    public void signOut(HttpServletResponse response,  @RequestBody String json) {
        logger.info("signOut start, parameter:" + json);

        String result = CommonConstant.fail;

        Map<String, String> signOutInfo = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        String sessionId =  signOutInfo.get(CommonConstant.sessionId);

        String username = (String)dao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId);
        if (username != null) {
            dao.deleteFromRedis(username);
            dao.deleteFromRedis(username + CommonConstant.underline + CommonConstant.resources);
            dao.deleteFromRedis(CommonConstant.salt + CommonConstant.underline + sessionId);
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
    @RequestMapping(value="/user/hasLoginDeal")
    public void hasLoginDeal(HttpServletResponse response,  @RequestBody String json) {
        logger.info("hasLoginDeal start, parameter:" + json);

        String result = CommonConstant.fail;

        Map<String, String> dealInfo = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        String username = dealInfo.get(CommonConstant.username);
        String sessionId = dealInfo.get(CommonConstant.sessionId);
        String oldSessionId = dealInfo.get(CommonConstant.oldSessionId);

        String tempUserKey = username + CommonConstant.underline +  oldSessionId;

        if (dealInfo.get("dealType").equals("againSignIn")) {
            User user = (User) dao.getFromRedis(tempUserKey);

            if (user != null) {
                //移除之前登录的用户
                dao.deleteFromRedis(CommonConstant.salt + CommonConstant.underline + oldSessionId);
                signInUtil.removeUser(username);

                dao.storeToRedis(username, user, signInUtil.sessionTime);
                signInUtil.setUser(sessionId, username);

                //移除临时登录用户
                dao.deleteFromRedis(tempUserKey);

                result = CommonConstant.success;
            }

        } else {
            result = "no operation";
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("hasLoginDeal end");
    }

    public Object getUserBySessionId(String sessionId){
        return dao.getFromRedis((String)dao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId));
    }
}
