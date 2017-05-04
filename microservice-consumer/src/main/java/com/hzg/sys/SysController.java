package com.hzg.sys;

import com.google.gson.reflect.TypeToken;
import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
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

        String json = "{\"id\":" + id + "}";

        if (entity.equalsIgnoreCase(User.class.getSimpleName())) {
            List<User> users = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<User>>() {}.getType());
            model.put("user", users.isEmpty() ? null : users.get(0));

        } else if (entity.equalsIgnoreCase(Post.class.getSimpleName())) {
            List<Post> posts = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Post>>() {}.getType());
            model.put("post", posts.isEmpty() ? null : posts.get(0));

        } else if (entity.equalsIgnoreCase(Dept.class.getSimpleName())) {
            List<Dept> depts = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Dept>>() {}.getType());
            model.put("dept", depts.isEmpty() ? null : depts.get(0));

        } else if (entity.equalsIgnoreCase(Company.class.getSimpleName())) {
            List<Company> companies = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Company>>() {}.getType());
            model.put("company", companies.isEmpty() ? null : companies.get(0));
        }

        logger.info("viewById end");

        return "/sys/" + entity;
    }

    /**
     * dataTable 分页查询
     * @param response
     * @param dataTableParameters
     * @param json
     * @param entity
     */
    @PostMapping("/complexQuery/{entity}")
    public void complexQuery(HttpServletResponse response, String dataTableParameters, String json, @PathVariable("entity") String entity) {
        logger.info("query start, entity:" + entity + ", dataTableParameters:" + dataTableParameters + ", json:" + json);

        int sEcho = 0;// 记录操作的次数  每次加1
        int iDisplayStart = 0;// 起始
        int iDisplayLength = 30;// 每页显示条数
        String sSearch = "";// 搜索的关键字

        List<Map<String, String>> dtParameterMaps = writer.gson.fromJson(dataTableParameters, new TypeToken<List<Map<String, String>>>(){}.getType());
        //分别为关键的参数赋值
        for(Map dtParameterMap : dtParameterMaps) {
           if (dtParameterMap.get("name").equals("sEcho"))
               sEcho = Integer.parseInt(dtParameterMap.get("value").toString());

           if (dtParameterMap.get("name").equals("iDisplayStart"))
               iDisplayStart = Integer.parseInt(dtParameterMap.get("value").toString());

           if (dtParameterMap.get("name").equals("iDisplayLength"))
               iDisplayLength = Integer.parseInt(dtParameterMap.get("value").toString());

           if (dtParameterMap.get("name").equals("sSearch"))
               sSearch = dtParameterMap.get("value").toString();
        }


        sEcho += 1; //为操作次数加1
        String result = client.complexQuery(entity, json, iDisplayStart, iDisplayLength*10);
        int count = result.split("\\{").length-1 ;  //查询出来的数量

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("sEcho", sEcho+"");
        dataMap.put("iTotalRecords", count+""); //实际的行数
        dataMap.put("iTotalDisplayRecords", count+""); ////显示的行数,这个要和 iTotalRecords 一样
        dataMap.put("aaData", result); //数据

        writer.writeObjectToJson(response, dataMap);
        logger.info("query end");
    }
}
