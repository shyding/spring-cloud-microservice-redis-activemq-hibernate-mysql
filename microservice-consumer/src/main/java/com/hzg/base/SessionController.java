package com.hzg.base;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.CommonConstant;
import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionController {
    Logger logger = Logger.getLogger(SessionController.class);

    public Client client;

    @Autowired
    private Writer writer;

    @Autowired
    public Dao dao;

    public SessionController(Client client) {
        this.client = client;
    }

    @GetMapping("/list/{entity}/{json}")
    public String list(Map<String, Object> model, @PathVariable("entity") String entity, @PathVariable("json") String json,
            @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        model.put(CommonConstant.resources, dao.getFromRedis((String)dao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId) + CommonConstant.underline + CommonConstant.resources));
        model.put("entity", entity);
        model.put("json", json);
        return "list";
    }

    @PostMapping("/save/{entity}")
    public void save(HttpServletResponse response, String json, @PathVariable("entity") String entity) {
        logger.info("save start, entity:" + entity + ", json:" + json);
        writer.writeStringToJson(response, client.save(entity, json));
        logger.info("save end");
    }

    @PostMapping("/update/{entity}")
    public void update(HttpServletResponse response, String json, @PathVariable("entity") String entity,
                       @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("update start, entity:" + entity + ", json:" + json);
        json = json.substring(0, json.length()-1) + ",\"" + CommonConstant.sessionId + "\":\"" + sessionId + "\"}";
        writer.writeStringToJson(response, client.update(entity, json));
        logger.info("update end");
    }

    @GetMapping("/query/{entity}")
    public void query(HttpServletResponse response, String json, @PathVariable("entity") String entity,
                      @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("query start, entity:" + entity + ", json:" + json);
        json = json.substring(0, json.length()-1) + ",\"" + CommonConstant.sessionId + "\":\"" + sessionId + "\"}";
        writer.writeStringToJson(response, client.query(entity, json));
        logger.info("query end");
    }

    @GetMapping("/suggest/{entity}/{properties}/{word}")
    public void suggest(HttpServletResponse response, @PathVariable("entity") String entity,
                        @PathVariable("properties") String properties, @PathVariable("word") String word,
                        @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("suggest start, entity:" + entity + ",properties:" + properties + ", word:" + word);

        String json = "";
        String[] propertiesArr = properties.split("#");
        for (String property:propertiesArr) {
            if (property.trim().length() > 0)
                json += "\"" + property + "\":\"" + word + "\",";
        }
        json = "{" + json.substring(0, json.length()-1) + "}";

        json = json.substring(0, json.length()-1) + ",\"" + CommonConstant.sessionId + "\":\"" + sessionId + "\"}";
        writer.writeStringToJson(response, client.suggest(entity, json));
        logger.info("suggest end");
    }


    /**
     * dataTable 分页查询
     * @param response
     * @param dataTableParameters
     * @param json
     * @param entity
     */
    @PostMapping("/complexQuery/{entity}")
    public void complexQuery(HttpServletResponse response, String dataTableParameters, String json, Integer recordsSum, @PathVariable("entity") String entity,
                             @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("complexQuery start, entity:" + entity + ", dataTableParameters:" + dataTableParameters + ", json:" + json + ",recordsSum" + recordsSum);

        int sEcho = 0;// 记录操作的次数  每次加1
        int iDisplayStart = 0;// 起始
        int iDisplayLength = 30;// 每页显示条数
        String sSearch = "";// 搜索的关键字

        List<Map<String, String>> dtParameterMaps = writer.gson.fromJson(dataTableParameters, new TypeToken<List<Map<String, String>>>(){}.getType());
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
        json = json.substring(0, json.length()-1) + ",\"" + CommonConstant.sessionId + "\":\"" + sessionId + "\"}";
        String result = client.complexQuery(entity, json, iDisplayStart, iDisplayLength);

        if (recordsSum == -1) {
            recordsSum = ((Map<String, Integer>)writer.gson.fromJson(client.recordsSum(entity, json), new TypeToken<Map<String, Integer>>(){}.getType())).get(CommonConstant.recordsSum);
        }

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("sEcho", sEcho+"");
        dataMap.put("iTotalRecords", String.valueOf(recordsSum)); //实际的行数
        dataMap.put("iTotalDisplayRecords", String.valueOf(recordsSum)); ////显示的行数,这个要和 iTotalRecords 一样
        dataMap.put("aaData", result); //数据

        writer.writeObjectToJson(response, dataMap);
        logger.info("complexQuery end");
    }

    @PostMapping("/isValueRepeat/{entity}")
    public void isValueRepeat(HttpServletResponse response, @PathVariable("entity") String entity, String json) {
        logger.info("isValueRepeat start, entity:" + entity + ",json:" + json);
        writer.writeStringToJson(response, client.isValueRepeat(entity, json));
        logger.info("isValueRepeat end");
    }

    @PostMapping("/delete/{entity}")
    public void delete(HttpServletResponse response, String json, @PathVariable("entity") String entity,
                       @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("delete start, entity:" + entity + ", json:" + json);
        json = json.substring(0, json.length()-1) + ",\"" + CommonConstant.sessionId + "\":\"" + sessionId + "\"}";
        writer.writeStringToJson(response, client.delete(entity, json));
        logger.info("delete end");
    }

    @PostMapping("/recover/{entity}")
    public void recover(HttpServletResponse response, String json, @PathVariable("entity") String entity,
                        @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("recover start, entity:" + entity + ", json:" + json);
        json = json.substring(0, json.length()-1) + ",\"" + CommonConstant.sessionId + "\":\"" + sessionId + "\"}";
        writer.writeStringToJson(response, client.recover(entity, json));
        logger.info("recover end");
    }
}
