package com.hzg.erp;

import com.google.gson.reflect.TypeToken;
import com.hzg.sys.Audit;
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

    @Autowired
    private ErpService erpService;

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
            if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
                Purchase purchase = writer.gson.fromJson(json, Purchase.class);
                purchase.setInputDate(inputDate);
                result += erpDao.save(purchase);

                result += erpService.savePurchaseProducts(purchase);

                /**
                 * 发起采购流程
                 */
                String auditEntity = AuditFlowConstant.business_purchase;
                switch (purchase.getType()) {
                    case 2:
                        auditEntity = AuditFlowConstant.business_purchaseEmergency;
                        break;
                }

                result += erpService.launchAuditFlow(auditEntity, purchase.getId(), purchase.getName(), purchase.getInputer());

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

            } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
                StockInOut stockInOut = writer.gson.fromJson(json, StockInOut.class);

                /**
                 * 入库
                 */
                if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) < 0) {
                    if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_deposit) == 0) {
                        result += erpDao.save(stockInOut.getDeposit());
                    }

                    if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_repair) == 0) {
                        result += erpDao.save(stockInOut.getProcessRepair());
                    }

                    result += erpDao.save(stockInOut);

                    result += erpService.saveStockInProducts(stockInOut);

                    /**
                     * 押金入库后通知仓储预计退还货物时间，财务人员预计退还押金时间
                     */
                    if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_deposit) == 0) {
                        result += erpService.launchAuditFlow(AuditFlowConstant.business_stockIn_deposit_cangchu, stockInOut.getId(),
                                "押金入库单 " + stockInOut.getNo() + ", 预计" + stockInOut.getDeposit().getReturnGoodsDate() + "退货",
                                stockInOut.getInputer());

                        result += erpService.launchAuditFlow(AuditFlowConstant.business_stockIn_deposit_caiwu, stockInOut.getId(),
                                "押金入库单 " + stockInOut.getNo() + ", 预计" + stockInOut.getDeposit().getReturnDepositDate() + "退押金",
                                stockInOut.getInputer());
                    }
                }

            } else if (entity.equalsIgnoreCase(Warehouse.class.getSimpleName())) {
                Warehouse warehouse = writer.gson.fromJson(json, Warehouse.class);
                warehouse.setInputDate(inputDate);
                result = erpDao.save(warehouse);

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
            if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
                Purchase purchase = writer.gson.fromJson(json, Purchase.class);

                /**
                 * 查询数据库里的采购单
                 */
                Purchase dbPurchase = (Purchase) erpDao.queryById(purchase.getId(), Purchase.class);
                Set<PurchaseDetail> details = dbPurchase.getDetails();
                for (PurchaseDetail detail : details) {
                    detail.setProduct((Product) erpDao.query(detail.getProduct()).get(0));
                }

                Product stateProduct = ((PurchaseDetail) dbPurchase.getDetails().toArray()[0]).getProduct();

                if (stateProduct.getState().compareTo(ErpConstant.product_state_purchase) == 0) {       //采购状态的才可以修改
                    result += erpDao.updateById(purchase.getId(), purchase);

                    /**
                     * 保存采购单里的新商品信息，删除旧商品信息
                     */
                    result += erpService.savePurchaseProducts(purchase);
                    result += erpService.deletePurchaseProducts(dbPurchase);


                    /**
                     * 修改事宜信息
                     */
                    String oldEntity = AuditFlowConstant.business_purchase, newEntity = AuditFlowConstant.business_purchase;
                    switch (dbPurchase.getType()) {
                        case 2:
                            oldEntity = AuditFlowConstant.business_purchaseEmergency;
                            break;
                    }

                    switch (purchase.getType()) {
                        case 2:
                            newEntity = AuditFlowConstant.business_purchaseEmergency;
                            break;
                    }

                    result += erpService.updateAudit(dbPurchase.getId(), oldEntity, purchase.getName(), newEntity);

                } else {
                    result = CommonConstant.fail + ", 采购单 " + purchase.getNo() + " 里的商品，已审核通过，不能修改";
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

            } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
                StockInOut stockInOut = writer.gson.fromJson(json, StockInOut.class);
                StockInOut dbStockInOut = (StockInOut) erpDao.queryById(stockInOut.getId(), StockInOut.class);

                /**
                 * 入库
                 */
                if (dbStockInOut.getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) < 0) {

                    if (dbStockInOut.getState().compareTo(ErpConstant.stockInOut_state_apply) == 0) {

                        if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_deposit) == 0) {
                            result += erpDao.updateById(stockInOut.getDeposit().getId(), stockInOut.getDeposit());
                        }

                        if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_repair) == 0) {
                            result += erpDao.updateById(stockInOut.getProcessRepair().getId(), stockInOut.getProcessRepair());
                        }

                        result += erpDao.updateById(stockInOut.getId(), stockInOut);

                        /**
                         * 保存入库单里的新商品信息，删除旧商品信息
                         */
                        result += erpService.saveStockInProducts(stockInOut);
                        result += erpService.deleteStockInProducts(dbStockInOut);

                        /**
                         * 修改事宜信息
                         */
                        if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_deposit) == 0) {
                            result += erpService.updateAudit(stockInOut.getId(), AuditFlowConstant.business_stockIn_deposit_cangchu,
                                    AuditFlowConstant.business_stockIn_deposit_cangchu,
                                    "押金入库单 " + stockInOut.getNo() + ", 预计" + stockInOut.getDeposit().getReturnGoodsDate() + "退货");

                            result += erpService.updateAudit(stockInOut.getId(), AuditFlowConstant.business_stockIn_deposit_caiwu,
                                    AuditFlowConstant.business_stockIn_deposit_caiwu,
                                    "押金入库单 " + stockInOut.getNo() + ", 预计" + stockInOut.getDeposit().getReturnDepositDate() + "退押金");
                        }

                    } else {
                        result = CommonConstant.fail + ", 入库单 " + stockInOut.getNo() + " 里的商品已上架，不能再修改";
                    }
                }

            } else if (entity.equalsIgnoreCase(Warehouse.class.getSimpleName())) {
                Warehouse warehouse = writer.gson.fromJson(json, Warehouse.class);
                result = erpDao.updateById(warehouse.getId(), warehouse);

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
    @PostMapping("/delete")
    public void delete(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("delete start, parameter:" + entity + ":" + json);

        String result = CommonConstant.fail;

        try {
            if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
                Purchase purchase = writer.gson.fromJson(json, Purchase.class);

                Purchase dbPurchase = (Purchase) erpDao.queryById(purchase.getId(), Purchase.class);
                Set<PurchaseDetail> details = dbPurchase.getDetails();
                for (PurchaseDetail detail : details) {
                    detail.setProduct((Product) erpDao.query(detail.getProduct()).get(0));
                }

                Product stateProduct = ((PurchaseDetail) dbPurchase.getDetails().toArray()[0]).getProduct();

                if (stateProduct.getState().compareTo(ErpConstant.product_state_purchase) == 0) {       //采购状态的才可以修改
                    result += erpDao.updateById(purchase.getId(), purchase);

                    /**
                     * 修改商品为无效状态
                     */
                    Product tempProduct = new Product();
                    for (PurchaseDetail detail : details) {
                        tempProduct.setId(detail.getProduct().getId());
                        tempProduct.setState(ErpConstant.product_state_invalid);

                        result += erpDao.updateById(tempProduct.getId(), tempProduct);
                    }

                    /**
                     * 删除事宜信息
                     */
                    String auditEntity = AuditFlowConstant.business_purchase;
                    switch (dbPurchase.getType()) {
                        case 2:
                            auditEntity = AuditFlowConstant.business_purchaseEmergency;
                            break;
                    }

                    result += erpService.deleteAudit(dbPurchase.getId(), auditEntity);

                } else {
                    result = CommonConstant.fail + ", 采购单 " + dbPurchase.getNo() + " 里的商品，已审核通过，不能作废";
                }


            } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
                StockInOut stockInOut = writer.gson.fromJson(json, StockInOut.class);
                StockInOut dbStockInOut = (StockInOut) erpDao.queryById(stockInOut.getId(), StockInOut.class);

                /**
                 * 入库
                 */
                if (dbStockInOut.getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) < 0) {
                    if (dbStockInOut.getState().compareTo(ErpConstant.stockInOut_state_apply) == 0) {

                        result += erpDao.updateById(stockInOut.getId(), stockInOut);

                        /**
                         * 修改商品为采购审核通过状态，减少对应商品库存量
                         */
                        result += erpService.setStocks(stockInOut, CommonConstant.subtract);


                        /**
                         * 删除事宜信息
                         */
                        if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_deposit) == 0) {
                            result += erpService.deleteAudit(dbStockInOut.getId(), AuditFlowConstant.business_stockIn_deposit_cangchu);
                            result += erpService.deleteAudit(dbStockInOut.getId(), AuditFlowConstant.business_stockIn_deposit_caiwu);
                        }

                    } else {
                        result = CommonConstant.fail + ", 入库单 " + dbStockInOut.getNo() + " 里的商品已上架，不能作废";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("delete end, result:" + result);
    }



    @Transactional
    @PostMapping("/recover")
    public void recover(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("recover start, parameter:" + entity + ":" + json);

        String result = CommonConstant.fail;

        try {
            if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
                Purchase purchase = writer.gson.fromJson(json, Purchase.class);

                Purchase dbPurchase = (Purchase) erpDao.queryById(purchase.getId(), Purchase.class);
                Set<PurchaseDetail> details = dbPurchase.getDetails();
                for (PurchaseDetail detail : details) {
                    detail.setProduct((Product) erpDao.query(detail.getProduct()).get(0));
                }

                if (dbPurchase.getState().compareTo(ErpConstant.purchase_state_cancel) == 0) {       //作废状态的才可以恢复
                    result += erpDao.updateById(purchase.getId(), purchase);

                    /**
                     * 修改商品为无效状态
                     */
                    Product tempProduct = new Product();
                    for (PurchaseDetail detail : details) {
                        tempProduct.setId(detail.getProduct().getId());
                        tempProduct.setState(ErpConstant.product_state_purchase);

                        result += erpDao.updateById(tempProduct.getId(), tempProduct);
                    }

                    /**
                     * 发起采购流程
                     */
                    String auditEntity = AuditFlowConstant.business_purchase;
                    switch (dbPurchase.getType()) {
                        case 2:
                            auditEntity = AuditFlowConstant.business_purchaseEmergency;
                            break;
                    }
                    result += erpService.launchAuditFlow(auditEntity, dbPurchase.getId(), dbPurchase.getName(), dbPurchase.getInputer());


                } else {
                    result = CommonConstant.fail + ", 采购单 " + dbPurchase.getNo() + " 里的商品，不为无效状态，不能恢复";
                }


            } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
                StockInOut stockInOut = writer.gson.fromJson(json, StockInOut.class);
                StockInOut dbStockInOut = (StockInOut) erpDao.queryById(stockInOut.getId(), StockInOut.class);

                /**
                 * 入库
                 */
                if (dbStockInOut.getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) < 0) {
                    if (dbStockInOut.getState().compareTo(ErpConstant.stockInOut_state_cancel) == 0) {

                        result += erpDao.updateById(stockInOut.getId(), stockInOut);

                        /**
                         * 修改商品为入库状态，设置库存为有效状态，或增加对应商品库存量
                         */
                        result += erpService.setStocks(stockInOut, CommonConstant.add);


                        /**
                         * 押金入库后通知仓储预计退还货物时间，财务人员预计退还押金时间
                         */
                        if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_deposit) == 0) {
                            result += erpService.launchAuditFlow(AuditFlowConstant.business_stockIn_deposit_cangchu, dbStockInOut.getId(),
                                    "押金入库单 " + dbStockInOut.getNo() + ", 预计" + dbStockInOut.getDeposit().getReturnGoodsDate() + "退货",
                                    dbStockInOut.getInputer());

                            result += erpService.launchAuditFlow(AuditFlowConstant.business_stockIn_deposit_caiwu, dbStockInOut.getId(),
                                    "押金入库单 " + dbStockInOut.getNo() + ", 预计" + dbStockInOut.getDeposit().getReturnDepositDate() + "退押金",
                                    dbStockInOut.getInputer());
                        }

                    } else {
                        result = CommonConstant.fail + ", 入库单 " + dbStockInOut.getNo() + " 不是无效状态，不能恢复";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("recover end, result:" + result);
    }



    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public void query(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("query start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
            List<Purchase> purchases = erpDao.query(writer.gson.fromJson(json, Purchase.class));

            for (Purchase purchase : purchases) {
                for (PurchaseDetail detail : purchase.getDetails()) {
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

        } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
            List<StockInOut> stockInOuts = erpDao.query(writer.gson.fromJson(json, StockInOut.class));

            for (StockInOut stockInOut : stockInOuts) {
                Set<Stock> stocks = new HashSet<>();

                for (StockInOutDetail detail : stockInOut.getDetails()) {
                    Product product = (Product) erpDao.query(detail.getProduct()).get(0);
                    Stock stock = erpService.getDbStock(product.getNo(), stockInOut.getWarehouse());

                    stock.setProduct(product);

                    if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_increment) == 0) {
                        PurchaseDetail purchaseDetail = erpService.getPurchaseDetail(product.getId());
                        stock.setQuantity(purchaseDetail.getQuantity());
                    }

                    stocks.add(stock);
                }

                stockInOut.setStocks(stocks);
            }

            writer.writeObjectToJson(response, stockInOuts);

        } else if (entity.equalsIgnoreCase(Stock.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.query(writer.gson.fromJson(json, Stock.class)));

        } else if (entity.equalsIgnoreCase(Warehouse.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.query(writer.gson.fromJson(json, Warehouse.class)));
        }

        logger.info("query end");
    }

    @RequestMapping(value = "/suggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void suggest(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("suggest start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
            Purchase purchase = writer.gson.fromJson(json, Purchase.class);
            writer.writeObjectToJson(response, erpDao.suggest(purchase, null));

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

        } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
            StockInOut stockInOut = writer.gson.fromJson(json, StockInOut.class);
            writer.writeObjectToJson(response, erpDao.suggest(stockInOut, null));

        } else if (entity.equalsIgnoreCase(Warehouse.class.getSimpleName())) {
            Warehouse warehouse = writer.gson.fromJson(json, Warehouse.class);
            writer.writeObjectToJson(response, erpDao.suggest(warehouse, null));
        }

        logger.info("suggest end");
    }

    @RequestMapping(value = "/entitiesSuggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void entitiesSuggest(HttpServletResponse response, String targetEntities,  String entities,  String properties, String word){
        logger.info("entitiesSuggest start, parameter:" + targetEntities + "," + entities + "," + properties + "," + word);

        String result = "[";

        String[] targetEntitiesArr = targetEntities.split("#");
        String[] entitiesArr = entities.split("#");
        String[] propertiesArr = properties.split("#");

        for (int i = 0; i < entitiesArr.length; i++) {
            String json = "";

            if (propertiesArr[i].trim().length() > 0) {
                json += "\"" + propertiesArr[i] + "\":\"" + word + "\",";
            }

            if (!json.equals("")) {
                json = "{" + json.substring(0, json.length() - 1) + "}";

                String partResult = erpService.queryTargetEntity(targetEntitiesArr[i], entitiesArr[i], json);

                if (partResult != null) {
                    if (!partResult.equals("[]") && !partResult.trim().equals("")) {
                        result += partResult.substring(1, partResult.length()-1) + ",";
                    }
                }
            }
        }

        int pos = result.lastIndexOf(",");
        if (pos != -1) {
            result = result.substring(0, pos);
        }

        result += "]";

        writer.writeStringToJson(response, result);

        logger.info("entitiesSuggest end");
    }

    @RequestMapping(value = "/complexQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void complexQuery(HttpServletResponse response, String entity, @RequestBody String json, int position, int rowNum){
        logger.info("complexQuery start, parameter:" + entity + ":" + json + "," + position + "," + rowNum);

        Map<String, String> queryParameters = null;
        try {
            queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
        } catch (Exception e){
            e.getMessage();
        }

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

        } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.complexQuery(StockInOut.class, queryParameters, position, rowNum));

        } else if (entity.equalsIgnoreCase(Stock.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpService.privateQuery(entity, json, position, rowNum));

        } else if (entity.equalsIgnoreCase(Warehouse.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.complexQuery(Warehouse.class, queryParameters, position, rowNum));
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

        Map<String, String> queryParameters = null;
        try {
            queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
        } catch (Exception e){
            e.getMessage();
        }

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

        } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
            recordsSum =  erpDao.recordsSum(StockInOut.class, queryParameters);

        } else if (entity.equalsIgnoreCase(Warehouse.class.getSimpleName())) {
            recordsSum =  erpDao.recordsSum(Warehouse.class, queryParameters);
        }

        writer.writeStringToJson(response, "{\"recordsSum\":" + recordsSum + "}");

        logger.info("recordsSum end");
    }

    @RequestMapping(value = "/getNo", method = {RequestMethod.GET, RequestMethod.POST})
    public void getNo(HttpServletResponse response, String prefix){
        logger.info("getNo start, parameter:" + prefix);

        writer.writeStringToJson(response, "{\"no\":\"" + erpDao.getNo(prefix) + "\"}");

        logger.info("getNo start, end");
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
        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":" + isRepeat + "}");

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
        String result = CommonConstant.fail;

        try {
            Audit audit = writer.gson.fromJson(json, Audit.class);
            switch (audit.getAction()) {
                case AuditFlowConstant.action_purchase_product_pass:
                    result = erpService.purchaseProductsStateModify(audit, ErpConstant.product_state_purchase_pass);
                    break;

                case AuditFlowConstant.action_product_modify:
                    result = erpService.purchaseProductsStateModify(audit, ErpConstant.product_state_purchase);
                    break;

                case AuditFlowConstant.action_purchase_close:
                    result = erpService.purchaseStateModify(audit, ErpConstant.purchase_state_close, ErpConstant.product_state_purchase_pass);
                    break;

                case AuditFlowConstant.action_purchase_modify:
                    result = erpService.purchaseStateModify(audit, ErpConstant.purchase_state_apply, ErpConstant.product_state_purchase);
                    break;

                case AuditFlowConstant.action_purchase_emergency_pass:
                    result = erpService.purchaseEmergencyPass(audit, ErpConstant.purchase_state_close, ErpConstant.product_state_purchase_pass);
                    break;

                case AuditFlowConstant.action_purchase_emergency_pay:
                    result = erpService.purchaseEmergencyPay(audit);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":" + result + "}");
        logger.info("auditAction end");
    }

}
