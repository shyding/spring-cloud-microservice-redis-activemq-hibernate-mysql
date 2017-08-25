package com.hzg.pay;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.*;
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
@RequestMapping("/pay")
public class PayController {

    Logger logger = Logger.getLogger(PayController.class);

    @Autowired
    private PayDao payDao;

    @Autowired
    private Writer writer;

    @Autowired
    private PayService payService;

    @Autowired
    private Transcation transcation;

    @Autowired
    private DateUtil dateUtil;

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
            if (entity.equalsIgnoreCase(Pay.class.getSimpleName())) {
                Pay pay = writer.gson.fromJson(json, Pay.class);
                pay.setNo(payDao.getNo());
                pay.setInputDate(inputDate);
                result += payDao.save(pay);

            } else if (entity.equalsIgnoreCase(Account.class.getSimpleName())) {
                Account account = writer.gson.fromJson(json, Account.class);
                result += payDao.save(account);

            } else if (entity.equalsIgnoreCase(Refund.class.getSimpleName())) {
                Refund refund = writer.gson.fromJson(json, Refund.class);
                result += payDao.save(refund);
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

    @Transactional()
    @PostMapping("/update")
    public void update(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("update start, parameter:" + entity + ":" + json);

        String result = CommonConstant.fail;

        try {
            if (entity.equalsIgnoreCase(Pay.class.getSimpleName())) {
                Pay pay = writer.gson.fromJson(json, Pay.class);
                result += payDao.updateById(pay.getId(), pay);

            } else if (entity.equalsIgnoreCase(Account.class.getSimpleName())) {
                Account account = writer.gson.fromJson(json, Account.class);
                result += payDao.updateById(account.getId(), account);

            } else if (entity.equalsIgnoreCase(Refund.class.getSimpleName())) {
                Refund refund = writer.gson.fromJson(json, Refund.class);
                result += payDao.updateById(refund.getId(), refund);
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

    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public void query(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("query start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(Pay.class.getSimpleName())) {
            writer.writeObjectToJson(response, payDao.query(writer.gson.fromJson(json, Pay.class)));

        } else if (entity.equalsIgnoreCase(Account.class.getSimpleName())) {
            writer.writeObjectToJson(response, payDao.query(writer.gson.fromJson(json, Account.class)));
        }

        logger.info("query end");
    }

    @RequestMapping(value = "/suggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void suggest(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("suggest start, parameter:" + entity + ":" + json);


        logger.info("suggest end");
    }

    @RequestMapping(value = "/complexQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void complexQuery(HttpServletResponse response, String entity, @RequestBody String json, int position, int rowNum){
        logger.info("complexQuery start, parameter:" + entity + ":" + json + "," + position + "," + rowNum);

        Map<String, String> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());

        if (entity.equalsIgnoreCase(Pay.class.getSimpleName())) {
            writer.writeObjectToJson(response, payDao.complexQuery(Pay.class, queryParameters, position, rowNum));

        } else if (entity.equalsIgnoreCase(Account.class.getSimpleName())) {
            writer.writeObjectToJson(response, payDao.complexQuery(Account.class, queryParameters, position, rowNum));

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


        writer.writeStringToJson(response, "{\"" + CommonConstant.recordsSum + "\":" + recordsSum + "}");

        logger.info("recordsSum end");
    }
}
