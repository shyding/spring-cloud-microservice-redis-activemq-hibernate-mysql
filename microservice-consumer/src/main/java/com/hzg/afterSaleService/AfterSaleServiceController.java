package com.hzg.afterSaleService;

import com.google.common.reflect.TypeToken;
import com.hzg.order.Order;
import com.hzg.pay.Account;
import com.hzg.pay.PayClient;
import com.hzg.tools.AfterSaleServiceConstant;
import com.hzg.tools.CommonConstant;
import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/afterSaleService")
public class AfterSaleServiceController extends com.hzg.base.Controller {

    Logger logger = Logger.getLogger(AfterSaleServiceController.class);

    @Autowired
    private AfterSaleServiceClient afterSaleServiceClient;

    @Autowired
    private Writer writer;

    public AfterSaleServiceController(AfterSaleServiceClient afterSaleServiceClient) {
        super(afterSaleServiceClient);
    }

    @GetMapping("/view/{entity}/{id}")
    public String viewById(Map<String, Object> model, @PathVariable("entity") String entity, @PathVariable("id") Integer id,
                           @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("viewById start, entity:" + entity + ", id:" + id);

        List<Object> entities = null;

        String json = "{\"" + CommonConstant.id +"\":" + id + "}";
        entities = writer.gson.fromJson(afterSaleServiceClient.unlimitedQuery(entity, json), new TypeToken<List<ReturnProduct>>() {}.getType());

        model.put(CommonConstant.resources, dao.getFromRedis((String)dao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId) +
                CommonConstant.underline + CommonConstant.resources));
        model.put(CommonConstant.sessionId, sessionId);
        model.put(CommonConstant.entity, entities.isEmpty() ? null : entities.get(0));
        logger.info("viewById end");

        return "/afterSaleService/" + entity;
    }

    @RequestMapping(value = "/business/{name}", method = {RequestMethod.GET, RequestMethod.POST})
    public String business(Map<String, Object> model, @PathVariable("name") String name, String json,
                           @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("business start, name:" + name + ", json:" + json);
        model.put(CommonConstant.entity, writer.gson.fromJson(afterSaleServiceClient.business(name, json), ReturnProduct.class));
        model.put(CommonConstant.resources, dao.getFromRedis((String)dao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId) +
                CommonConstant.underline + CommonConstant.resources));
        model.put(CommonConstant.sessionId, sessionId);
        logger.info("business " + name + " end");
        return "/afterSaleService/" + name;
    }

    @RequestMapping(value = "/doBusiness/{name}", method = {RequestMethod.GET, RequestMethod.POST})
    public void doBusiness(HttpServletResponse response, @PathVariable("name") String name, String json) {
        writer.writeStringToJson(response, afterSaleServiceClient.business(name, json));
    }

    @GetMapping("/unlimitedQuery/{entity}")
    public void unlimitedQuery(HttpServletResponse response, String json, @PathVariable("entity") String entity) {
        logger.info("query start, entity:" + entity + ", json:" + json);
        writer.writeStringToJson(response, afterSaleServiceClient.unlimitedQuery(entity, json));
        logger.info("query end");
    }

    @GetMapping("/unlimitedSuggest/{entity}/{properties}/{word}")
    public void unlimitedSuggest(HttpServletResponse response, @PathVariable("entity") String entity,
                        @PathVariable("properties") String properties, @PathVariable("word") String word) {
        logger.info("unlimitedSuggest start, entity:" + entity + ",properties:" + properties + ", word:" + word);

        String json = "";
        String[] propertiesArr = properties.split("#");
        for (String property:propertiesArr) {
            if (property.trim().length() > 0)
                json += "\"" + property + "\":\"" + word + "\",";
        }
        json = "{" + json.substring(0, json.length()-1) + "}";

        writer.writeStringToJson(response, afterSaleServiceClient.unlimitedSuggest(entity, json));
        logger.info("unlimitedSuggest end");
    }


    /**
     * dataTable 分页查询
     * @param response
     * @param dataTableParameters
     * @param json
     * @param entity
     */
    @PostMapping("/unlimitedComplexQuery/{entity}")
    public void unlimitedComplexQuery(HttpServletResponse response, String dataTableParameters, String json, Integer recordsSum, @PathVariable("entity") String entity) {
        logger.info("unlimitedComplexQuery start, entity:" + entity + ", dataTableParameters:" + dataTableParameters + ", json:" + json + ",recordsSum" + recordsSum);

        int sEcho = 0;// 记录操作的次数  每次加1
        int iDisplayStart = 0;// 起始
        int iDisplayLength = 30;// 每页显示条数
        String sSearch = "";// 搜索的关键字

        List<Map<String, String>> dtParameterMaps = writer.gson.fromJson(dataTableParameters, new com.google.gson.reflect.TypeToken<List<Map<String, String>>>(){}.getType());
        //分别为关键的参数赋值
        for(Map dtParameterMap : dtParameterMaps) {
            if (dtParameterMap.get("name").equals("sEcho"))
                sEcho = Integer.parseInt(dtParameterMap.get("value").toString());

            if (dtParameterMap.get("name").equals("iDisplayLength"))
                iDisplayLength = Integer.parseInt(dtParameterMap.get("value").toString());

            if (dtParameterMap.get("name").equals("iDisplayStart"))
                iDisplayStart = Integer.parseInt(dtParameterMap.get("value").toString());

            if (dtParameterMap.get("name").equals("sSearch"))
                sSearch = dtParameterMap.get("value").toString();
        }

        sEcho += 1; //为操作次数加1
        String result = afterSaleServiceClient.unlimitedComplexQuery(entity, json, iDisplayStart, iDisplayLength);

        if (recordsSum == -1) {
            recordsSum = ((Map<String, Integer>)writer.gson.fromJson(afterSaleServiceClient.unlimitedRecordsSum(entity, json), new com.google.gson.reflect.TypeToken<Map<String, Integer>>(){}.getType())).get(CommonConstant.recordsSum);
        }

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("sEcho", sEcho+"");
        dataMap.put("iTotalRecords", String.valueOf(recordsSum)); //实际的行数
        dataMap.put("iTotalDisplayRecords", String.valueOf(recordsSum)); ////显示的行数,这个要和 iTotalRecords 一样
        dataMap.put("aaData", result); //数据

        writer.writeObjectToJson(response, dataMap);
        logger.info("unlimitedComplexQuery end");
    }
}
