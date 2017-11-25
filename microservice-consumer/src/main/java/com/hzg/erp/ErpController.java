package com.hzg.erp;

import com.google.gson.reflect.TypeToken;
import com.hzg.pay.Account;
import com.hzg.pay.PayClient;
import com.hzg.sys.Post;
import com.hzg.sys.SysClient;
import com.hzg.sys.User;
import com.hzg.tools.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
    private SysClient sysClient;

    @Autowired
    private PayClient payClient;

    @Autowired
    private DateUtil dateUtil;

    public ErpController(ErpClient erpClient) {
        super(erpClient);
    }

    @GetMapping("/view/{entity}/{id}")
    public String viewById(Map<String, Object> model, @PathVariable("entity") String entity, @PathVariable("id") Integer id,
                           @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("viewById start, entity:" + entity + ", id:" + id);

        List<Object> entities = null;

        String json = "{\"" + CommonConstant.id +"\":" + id + "}";

        if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Purchase>>() {}.getType());

            if (entities.isEmpty()) {
                Map<String, String> no = writer.gson.fromJson(erpClient.getNo(ErpConstant.no_purchase_perfix), new TypeToken<Map<String, String>>() {}.getType());
                model.put(CommonConstant.no, no.get(CommonConstant.no));
            }

            model.put("currentDay", dateUtil.getCurrentDayStr());
            model.put("productTypes", writer.gson.fromJson(erpClient.complexQuery("productType", "{}", 0, -1), new TypeToken<List<ProductType>>() {}.getType()));
            model.put("accounts", writer.gson.fromJson(payClient.query(Account.class.getSimpleName().toLowerCase(), "{}"), new TypeToken<List<Account>>() {}.getType()));

        } else if (entity.equalsIgnoreCase(Supplier.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Supplier>>() {}.getType());

        } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Product>>() {}.getType());
            model.put("productTypes", writer.gson.fromJson(erpClient.complexQuery("productType", "{}", 0, -1), new TypeToken<List<ProductType>>() {}.getType()));

        } else if (entity.equalsIgnoreCase(ProductDescribe.class.getSimpleName())) {
            Product queryProduct = new Product();
            queryProduct.setDescribe(writer.gson.fromJson(json, ProductDescribe.class));
            entities = writer.gson.fromJson(client.query(Product.class.getSimpleName().toLowerCase(), writer.gson.toJson(queryProduct)), new TypeToken<List<Product>>() {}.getType());

        } else if (entity.equalsIgnoreCase(ProductType.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<ProductType>>() {}.getType());

        } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<StockInOut>>() {}.getType());

            if (entities.isEmpty()) {
                Map<String, String> no = writer.gson.fromJson(erpClient.getNo(ErpConstant.no_stockInOut_perfix), new TypeToken<Map<String, String>>() {}.getType());
                model.put(CommonConstant.no, no.get(CommonConstant.no));
            }

            model.put(CommonConstant.sessionId, sessionId);

        } else if (entity.equalsIgnoreCase(Warehouse.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Warehouse>>() {}.getType());

            if (entities.isEmpty()) {
                Map<String, String> no = writer.gson.fromJson(erpClient.getNo(ErpConstant.no_warehouse_perfix), new TypeToken<Map<String, String>>() {}.getType());
                model.put(CommonConstant.no, no.get(CommonConstant.no));
            }

        } else if (entity.equalsIgnoreCase(Stock.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<Stock>>() {}.getType());

        } else if (entity.equalsIgnoreCase(ProductPriceChange.class.getSimpleName())) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<ProductPriceChange>>() {}.getType());
            if (entities.isEmpty()) {
                Map<String, String> no = writer.gson.fromJson(erpClient.getSimpleNo(ErpConstant.product_price_change_no_length), new TypeToken<Map<String, String>>() {}.getType());
                model.put(CommonConstant.no, no.get(CommonConstant.no));
            }
            model.put(CommonConstant.sessionId, sessionId);

        } else if (entity.equalsIgnoreCase(ProductCheck.class.getSimpleName()) || entity.equalsIgnoreCase(ErpConstant.productCheckInput)) {
            entities = writer.gson.fromJson(client.query(entity, json), new TypeToken<List<ProductCheck>>() {}.getType());
            if (entities.isEmpty()) {
                Map<String, String> no = writer.gson.fromJson(erpClient.getNo(ErpConstant.no_productCheck_perfix), new TypeToken<Map<String, String>>() {}.getType());
                model.put(CommonConstant.no, no.get(CommonConstant.no));
            }

        } else if (entity.equalsIgnoreCase(ErpConstant.productUpDownShelf)) {
            model.put(CommonConstant.sessionId, sessionId);
        }

        User user = (User)dao.getFromRedis((String)dao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId));

        model.put(CommonConstant.entity, entities == null ? null : (entities.isEmpty() ? null : entities.get(0)));
        model.put(CommonConstant.userId, user.getId());
        logger.info("viewById end");

        return "/erp/" + entity;
    }


    @RequestMapping(value = "/privateQuery/{entity}", method = {RequestMethod.GET, RequestMethod.POST})
    public void privateQuery(HttpServletResponse response, String json, @PathVariable("entity") String entity) {
        logger.info("privateQuery start, entity:" + entity + ", json:" + json);

        if (entity.equalsIgnoreCase(ProductProperty.class.getSimpleName())) {
            writer.writeStringToJson(response, client.complexQuery(entity, json, 0, 30));

        } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            writer.writeStringToJsonAccessAllow(response, client.suggest(entity, json));

        } else if (entity.equalsIgnoreCase(ProductPriceChange.class.getSimpleName())) {
            writer.writeStringToJson(response, client.suggest(entity, json));

        } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
            writer.writeStringToJson(response, erpClient.privateQuery(entity, json));

        } else if (entity.equalsIgnoreCase(ErpConstant.productUnit)) {
            writer.writeStringToJson(response, erpClient.privateQuery(entity, json));
        }

        logger.info("privateQuery " + entity + " end");
    }

    @RequestMapping(value = "/privateView/{entity}/{properties}/{values}", method = {RequestMethod.GET, RequestMethod.POST})
    public String privateView(Map<String, Object> model, @PathVariable("entity") String entity,
                             @PathVariable("properties") String properties, @PathVariable("values") String values) {
        logger.info("privateView start, entity:" + entity + ", properties:" + properties + ",values:" + values);

        List<Object> entities = null;

        String[] propertiesArr = properties.split(CommonConstant.pound_sign);
        String[] valuesArr = values.split(CommonConstant.pound_sign);

        if (entity.equalsIgnoreCase(ProductPriceChange.class.getSimpleName())) {
            ProductPriceChange priceChange = new ProductPriceChange();
            for (int i = 0; i < propertiesArr.length; i++) {
                String setValueMethod = CommonConstant.set + propertiesArr[i].substring(0,1).toUpperCase() + propertiesArr[i].substring(1);
                try {
                    priceChange.getClass().getMethod(setValueMethod, priceChange.getClass().getDeclaredField(propertiesArr[i]).getType()).invoke(priceChange, valuesArr[i]);
                } catch (Exception e) {
                    logger.info(e.getMessage());
                }
            }

            entities = writer.gson.fromJson(client.query(entity, writer.gson.toJson(priceChange)), new TypeToken<List<ProductPriceChange>>() {}.getType());

        } else if (entity.equalsIgnoreCase(ProductCheck.class.getSimpleName())) {
            ProductCheck productCheck = new ProductCheck();
            for (int i = 0; i < propertiesArr.length; i++) {
                String setValueMethod = CommonConstant.set + propertiesArr[i].substring(0,1).toUpperCase() + propertiesArr[i].substring(1);
                try {
                    productCheck.getClass().getMethod(setValueMethod, productCheck.getClass().getDeclaredField(propertiesArr[i]).getType()).invoke(productCheck, valuesArr[i]);
                } catch (Exception e) {
                    logger.info(e.getMessage());
                }
            }

            entities = writer.gson.fromJson(client.query(entity, writer.gson.toJson(productCheck)), new TypeToken<List<ProductCheck>>() {}.getType());
        }

        model.put(CommonConstant.entity, entities.isEmpty() ? null : entities.get(0));
        logger.info("privateView " + entity + " end");

        return "/erp/" + entity;
    }

    @RequestMapping(value = "/business/{name}", method = {RequestMethod.GET, RequestMethod.POST})
    public String business(Map<String, Object> model, @PathVariable("name") String name, String json,
                           @CookieValue(name=CommonConstant.sessionId, defaultValue = "")String sessionId) {
        logger.info("business start, name:" + name + ", json:" + json);

        User user = (User)dao.getFromRedis((String)dao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId));
        model.put(CommonConstant.sessionId, sessionId);
        model.put("userId", user.getId());
        logger.info("business " + name + " end");

        return "/erp/" + name;
    }

    @RequestMapping(value = "/doBusiness/{name}", method = {RequestMethod.GET, RequestMethod.POST})
    public void doBusiness(javax.servlet.http.HttpServletRequest request, HttpServletResponse response,
                           @PathVariable("name") String name, String json, String sessionId) {
        logger.info("doBusiness start, name:" + name + ", json:" + json);

        if (name.equals(ErpConstant.product_action_name_updateUploadMediaFilesInfo)) {
            Map<String, Object> postParams = new HashedMap();
            postParams.put("uri", request.getRequestURI());
            postParams.put("posts", ((User)dao.getFromRedis((String)dao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId))).getPosts());
            List<Post> posts = writer.gson.fromJson(sysClient.getPostByUri(writer.gson.toJson(postParams)), new TypeToken<List<Post>>(){}.getType());

            if (posts.size() == 1) {
                Map<String, Object> params = new HashedMap();
                params.put("product", writer.gson.fromJson(json, Product.class));
                params.put("post", posts.get(0));

                writer.writeStringToJsonAccessAllow(response, erpClient.business(name, writer.gson.toJson(params)));

            } else {
                writer.writeStringToJsonAccessAllow(response, "{\"" + CommonConstant.result + "\":\"不能确定用户上传多媒体文件所属岗位，上传失败\"}");
            }

        } else if (name.equalsIgnoreCase(ErpConstant.product_action_name_queryProductAccessAllow)) {
            writer.writeStringToJsonAccessAllow(response, client.query(Product.class.getSimpleName(), json));

        } else {
            writer.writeStringToJsonAccessAllow(response, erpClient.business(name, json));
        }

        logger.info("doBusiness " + name + " end");
    }

    @RequestMapping(value = "/print/{name}", method = {RequestMethod.POST})
    public String print(Map<String, Object> model, @PathVariable("name") String name, String json) {
        logger.info("print start, name:" + name + ", json:" + json);

        model.put(CommonConstant.printContent, erpClient.print(name, json));
        logger.info("print " + name + " end");

        return  CommonConstant.print;
    }

    @PostMapping("/entitiesSuggest/{targetEntities}/{entities}")
    public void entitiesSuggest(HttpServletResponse response,
                                @PathVariable("targetEntities") String targetEntities,
                                @PathVariable("entities") String entities,
                                String json) {
        logger.info("suggest start, entities:" + entities + ",json:" + json);

        writer.writeStringToJson(response, erpClient.entitiesSuggest(targetEntities, entities, json));

        logger.info("suggest end");
    }
}
