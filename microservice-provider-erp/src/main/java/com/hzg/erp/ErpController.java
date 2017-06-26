package com.hzg.erp;

import com.google.gson.reflect.TypeToken;
import com.hzg.sys.Audit;
import com.hzg.sys.Post;
import com.hzg.sys.User;
import com.hzg.tools.AuditFlowConstant;
import com.hzg.tools.ErpConstant;
import com.hzg.tools.Writer;
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
@RequestMapping("/erp")
public class ErpController {

    Logger logger = Logger.getLogger(ErpController.class);

    @Autowired
    private ErpDao erpDao;

    @Autowired
    private Writer writer;

    @Autowired
    private SysClient sysClient;

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

        if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
            Purchase purchase = writer.gson.fromJson(json, Purchase.class);
            purchase.setInputDate(inputDate);
            result = erpDao.save(purchase);

            if (purchase.getDetails() != null) {
                for (PurchaseDetail detail : purchase.getDetails()) {
                    Product product = detail.getProduct();

                    ProductDescribe describe = product.getDescribe();
                    erpDao.save(describe);

                    product.setDescribe(describe);
                    erpDao.save(product);

                    /**
                     * 使用 new 新建，避免直接使用已经包含 property 属性的 product， 使得 product 与 property 循环嵌套
                     */
                    Product doubleRelateProduct = new Product();
                    doubleRelateProduct.setId(product.getId());

                    if (product.getProperties() != null) {
                        for (ProductOwnProperty ownProperty : product.getProperties()) {
                            ownProperty.setProduct(doubleRelateProduct);
                            erpDao.save(ownProperty);
                        }
                    }

                    detail.setProduct(product);
                    detail.setProductName(product.getName());
                    detail.setAmount(product.getUnitPrice() * detail.getQuantity());
                    detail.setPrice(product.getUnitPrice());

                    Purchase doubleRelatePurchase = new Purchase();
                    doubleRelatePurchase.setId(purchase.getId());

                    detail.setPurchase(doubleRelatePurchase);

                    erpDao.save(detail);
                }

                /**
                 * 创建审核流程第一个节点，发起审核流程
                 */
                Audit audit = new Audit();
                audit.setEntity(AuditFlowConstant.business_purchase);
                audit.setEntityId(purchase.getId());
                audit.setName(purchase.getName());
                Post post = (Post)(((List<User>)erpDao.query(purchase.getInputer())).get(0)).getPosts().toArray()[0];
                audit.setCompany(post.getDept().getCompany());

                String auditResult = sysClient.audit(writer.gson.toJson(audit));
                logger.info("audit result:" + auditResult);

                /**
                 * 发起事宜出错，则触发异常，回滚事务
                 */
                if (!auditResult.contains("success")) {
                    int t = 1/0;
                }
            }

        } else if (entity.equalsIgnoreCase(Supplier.class.getSimpleName())) {
            Supplier supplier = writer.gson.fromJson(json, Supplier.class);
            supplier.setInputDate(inputDate);
            result = erpDao.save(supplier);

        } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            Product product = writer.gson.fromJson(json, Product.class);
            result = erpDao.save(product);

        } else if (entity.equalsIgnoreCase(ProductType.class.getSimpleName())) {
            ProductType productType = writer.gson.fromJson(json, ProductType.class);
            result = erpDao.save(productType);

        } else if (entity.equalsIgnoreCase(ProductProperty.class.getSimpleName())) {
            ProductProperty productProperty = writer.gson.fromJson(json, ProductProperty.class);
            result = erpDao.save(productProperty);
        }

        writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
        logger.info("save end, result:" + result);
    }

    @Transactional
    @PostMapping("/update")
    public void update(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("update start, parameter:" + entity + ":" + json);

        String result = "fail";

        if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
            Purchase purchase = writer.gson.fromJson(json, Purchase.class);
            result = erpDao.updateById(purchase.getId(), purchase);

            if (purchase.getDetails() != null) {
                for (PurchaseDetail detail : purchase.getDetails()) {
                    Product product = detail.getProduct();

                    if (product.getState() == 1) {       //采购状态的才可以修改
                        erpDao.updateById(product.getDescribe().getId(), product.getDescribe());

                        erpDao.updateById(product.getId(), product);


                        /**
                         * 先保存新属性，再删除就属性
                         */
                        if (product.getProperties() != null) {
                            Set<ProductOwnProperty> oldProperties = ((Product) (erpDao.queryById(product.getId(), Product.class))).getProperties();

                            Product doubleRelateProduct = new Product();
                            doubleRelateProduct.setId(product.getId());

                            for (ProductOwnProperty ownProperty : product.getProperties()) {
                                ownProperty.setProduct(doubleRelateProduct);
                                erpDao.save(ownProperty);
                            }

                            for (ProductOwnProperty oldProperty : oldProperties) {
                                erpDao.delete(oldProperty);
                            }
                        }


                        detail.setProductName(product.getName());
                        detail.setAmount(product.getUnitPrice() * detail.getQuantity());
                        detail.setPrice(product.getUnitPrice());

                        erpDao.updateById(detail.getId(), detail);

                    } else {
                        result += " product " + product.getNo() + " state != 1, cann't update;";
                    }
                }
            }
        } else if (entity.equalsIgnoreCase(Supplier.class.getSimpleName())) {
            Supplier supplier = writer.gson.fromJson(json, Supplier.class);
            result = erpDao.updateById(supplier.getId(), supplier);

        } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            Product product = writer.gson.fromJson(json, Product.class);
            result = erpDao.updateById(product.getId(), product);

        } else if (entity.equalsIgnoreCase(ProductType.class.getSimpleName())) {
            ProductType productType = writer.gson.fromJson(json, ProductType.class);
            result = erpDao.updateById(productType.getId(), productType);

        } else if (entity.equalsIgnoreCase(ProductProperty.class.getSimpleName())) {
            ProductProperty productProperty = writer.gson.fromJson(json, ProductProperty.class);
            result = erpDao.updateById(productProperty.getId(), productProperty);
        }

        writer.writeStringToJson(response, "{\"result\":\"" + result + "\"}");
        logger.info("update end, result:" + result);
    }

    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public void query(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("query start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
            List<Purchase> purchases = erpDao.query(writer.gson.fromJson(json, Purchase.class));

            for (Purchase purchase : purchases) {
                Set<PurchaseDetail> details = purchase.getDetails();
                for (PurchaseDetail detail : details) {
                    detail.setProduct((Product) erpDao.query(detail.getProduct()).get(0));
                }
            }

            writer.writeObjectToJson(response, purchases);

        } else if (entity.equalsIgnoreCase(Supplier.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.query(writer.gson.fromJson(json, Supplier.class)));

        } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.query(writer.gson.fromJson(json, Product.class)));

        } else if (entity.equalsIgnoreCase(ProductType.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.query(writer.gson.fromJson(json, ProductType.class)));

        } else if (entity.equalsIgnoreCase(ProductProperty.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.query(writer.gson.fromJson(json, ProductProperty.class)));
        }

        logger.info("query end");
    }

    @RequestMapping(value = "/suggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void suggest(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("suggest start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
            Purchase purchase = writer.gson.fromJson(json, Purchase.class);
            purchase.setState(0);

            Field[] limitFields = new Field[1];
            try {
                limitFields[0] = purchase.getClass().getDeclaredField("state");
            } catch (Exception e) {
                e.printStackTrace();
            }

            writer.writeObjectToJson(response, erpDao.suggest(purchase, limitFields));

        } else if (entity.equalsIgnoreCase(Supplier.class.getSimpleName())) {
            Supplier supplier = writer.gson.fromJson(json, Supplier.class);
            supplier.setState(0);

            Field[] limitFields = new Field[1];
            try {
                limitFields[0] = supplier.getClass().getDeclaredField("state");
            } catch (Exception e) {
                e.printStackTrace();
            }

            writer.writeObjectToJson(response, erpDao.suggest(supplier, limitFields));

        } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            Product product = writer.gson.fromJson(json, Product.class);
            writer.writeObjectToJson(response, erpDao.suggest(product, null));

        } else if (entity.equalsIgnoreCase(ProductType.class.getSimpleName())) {
            ProductType productType = writer.gson.fromJson(json, ProductType.class);
            writer.writeObjectToJson(response, erpDao.suggest(productType, null));

        } else if (entity.equalsIgnoreCase(ProductProperty.class.getSimpleName())) {
            ProductProperty productProperty = writer.gson.fromJson(json, ProductProperty.class);
            writer.writeObjectToJson(response, erpDao.suggest(productProperty, null));
        }

        logger.info("suggest end");
    }

    @RequestMapping(value = "/complexQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void complexQuery(HttpServletResponse response, String entity, @RequestBody String json, int position, int rowNum){
        logger.info("complexQuery start, parameter:" + entity + ":" + json + "," + position + "," + rowNum);

        Map<String, String> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());

        if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.complexQuery(Purchase.class, queryParameters, position, rowNum));

        } else if (entity.equalsIgnoreCase(Supplier.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.complexQuery(Supplier.class, queryParameters, position, rowNum));

        } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.complexQuery(Product.class, queryParameters, position, rowNum));

        } else if (entity.equalsIgnoreCase(ProductType.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.complexQuery(ProductType.class, queryParameters, position, rowNum));

        } else if (entity.equalsIgnoreCase(ProductProperty.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.complexQuery(ProductProperty.class, queryParameters, position, rowNum));
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

        if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
            recordsSum =  erpDao.recordsSum(Purchase.class, queryParameters);

        } else if (entity.equalsIgnoreCase(Supplier.class.getSimpleName())) {
            recordsSum =  erpDao.recordsSum(Supplier.class, queryParameters);

        } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            recordsSum =  erpDao.recordsSum(Product.class, queryParameters);

        } else if (entity.equalsIgnoreCase(ProductType.class.getSimpleName())) {
            recordsSum =  erpDao.recordsSum(ProductType.class, queryParameters);

        } else if (entity.equalsIgnoreCase(ProductProperty.class.getSimpleName())) {
            recordsSum =  erpDao.recordsSum(ProductProperty.class, queryParameters);
        }

        writer.writeStringToJson(response, "{\"recordsSum\":" + recordsSum + "}");

        logger.info("recordsSum end");
    }


    /**
     * 查询条件限制下的记录数
     * @param response
     * @param entity
     * @param json
     */
    @RequestMapping(value = "/isValueRepeat", method = {RequestMethod.GET, RequestMethod.POST})
    public void isValueRepeat(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("isValueRepeat start, parameter:" + entity + ":" + json);

        Boolean isRepeat = false;

        Map<String, String> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());

        if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            isRepeat =  erpDao.isValueRepeat(Product.class, queryParameters.get("field"), queryParameters.get("value"), Integer.parseInt(queryParameters.get("id")));
        }
        writer.writeStringToJson(response, "{\"result\":" + isRepeat + "}");

        logger.info("isValueRepeat end");
    }

    /**
     * 流程审核动作
     * @param response
     * @param json
     */
    @Transactional
    @RequestMapping(value = "/auditAction", method = {RequestMethod.GET, RequestMethod.POST})
    public void auditAction(HttpServletResponse response, @RequestBody String json){
        logger.info("auditAction start, parameter:" + json);
        String result;

        Audit audit = writer.gson.fromJson(json, Audit.class);

        switch (audit.getAction()) {
            case AuditFlowConstant.action_purchase_product_pass: productPass(audit);
            case AuditFlowConstant.action_purchase_close: purchaseClose(audit);
            default:;
        }

        result = "success";

        writer.writeStringToJson(response, "{\"result\":" + result + "}");

        logger.info("isValueRepeat end");
    }

    private void productPass(Audit audit){
        Purchase purchase = (Purchase)erpDao.queryById(audit.getEntityId(), Purchase.class);

        Product temp = new Product();
        Set<PurchaseDetail> details = purchase.getDetails();
        for (PurchaseDetail detail : details) {

            temp.setId(detail.getProduct().getId());
            temp.setState(ErpConstant.product_state_purchase_pass);
            erpDao.updateById(temp.getId(), temp);
        }
    }

    private void purchaseClose(Audit audit) {
        Purchase purchase = (Purchase)erpDao.queryById(audit.getEntityId(), Purchase.class);

        Purchase temp = new Purchase();
        temp.setId(purchase.getId());
        temp.setState(ErpConstant.purchase_state_close);

        erpDao.updateById(temp.getId(), temp);
    }

}
