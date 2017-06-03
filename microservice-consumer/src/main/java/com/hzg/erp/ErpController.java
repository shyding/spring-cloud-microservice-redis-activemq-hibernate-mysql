package com.hzg.erp;

import com.google.gson.reflect.TypeToken;
import com.hzg.sys.User;
import com.hzg.tools.StrUtil;
import com.hzg.tools.Writer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/erp")
public class ErpController extends com.hzg.base.Controller {

    Logger logger = Logger.getLogger(ErpController.class);

    @Autowired
    private Writer writer;

    @Autowired
    private ErpClient erpClient;

    @Autowired
    private StrUtil strUtil;

    public ErpController(ErpClient erpClient) {
        super(erpClient);
    }

    @GetMapping("/view/{entity}/{id}")
    public String viewById(HttpSession session, Map<String, Object> model, @PathVariable("entity") String entity, @PathVariable("id") Integer id) {
        logger.info("viewById start, entity:" + entity + ", id:" + id);

        List<Object> entities = null;

        String json = "{\"id\":" + id + "}";

        if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Purchase>>() {}.getType());

        } else if (entity.equalsIgnoreCase(Supplier.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Supplier>>() {}.getType());

        } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Product>>() {}.getType());

        } else if (entity.equalsIgnoreCase(ProductType.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<ProductType>>() {}.getType());
        }

        User user = (User)dao.getFromRedis((String)dao.getFromRedis("sessionId_" + session.getId()));

        model.put("entity", entities.isEmpty() ? null : entities.get(0));
        model.put("userId", user.getId());
        logger.info("viewById end");

        return "/erp/" + entity;
    }

    @RequestMapping(value = "/privateQuery/{entity}", method = {RequestMethod.GET, RequestMethod.POST})
    public void privateQuery(HttpSession session, HttpServletResponse response, String dataTableParameters, String json, Integer recordsSum, @PathVariable("entity") String entity) {
        logger.info("privateQuery start, entity:" + entity + ", json:" + json);
        String privateCondition = "";

        logger.info("privateQuery " + entity + " end");
    }

    @RequestMapping(value = "/business/{name}", method = {RequestMethod.GET, RequestMethod.POST})
    public String business(Map<String, Object> model, @PathVariable("name") String name, String json) {
        logger.info("business start, name:" + name + ", json:" + json);

        logger.info("business " + name + " end");

        return "/sys/" + name;
    }
}
