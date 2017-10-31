package com.hzg.erp;

import com.google.gson.reflect.TypeToken;
import com.hzg.customer.User;
import com.hzg.order.Order;
import com.hzg.order.OrderDetail;
import com.hzg.sys.Audit;
import com.hzg.tools.*;
import com.sf.openapi.common.entity.AppInfo;
import com.sf.openapi.common.entity.HeadMessageReq;
import com.sf.openapi.common.entity.MessageReq;
import com.sf.openapi.common.entity.MessageResp;
import com.sf.openapi.express.sample.order.dto.CargoInfoDto;
import com.sf.openapi.express.sample.order.dto.DeliverConsigneeInfoDto;
import com.sf.openapi.express.sample.order.dto.OrderReqDto;
import com.sf.openapi.express.sample.order.tools.OrderTools;
import com.sf.openapi.express.sample.security.dto.TokenReqDto;
import com.sf.openapi.express.sample.security.dto.TokenRespDto;
import com.sf.openapi.express.sample.security.tools.SecurityTools;
import com.sf.openapi.express.sample.waybill.dto.WaybillReqDto;
import com.sf.openapi.express.sample.waybill.dto.WaybillRespDto;
import com.sf.openapi.express.sample.waybill.tools.WaybillDownloadTools;
import org.apache.commons.lang.builder.ToStringBuilder;
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
            writer.writeObjectToJson(response, erpService.privateQuery(entity,
                    "{\"" + Stock.class.getSimpleName().toLowerCase() + "\":" + json + "}", 0, -1));

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

    @RequestMapping(value = "/getStockQuantity", method = {RequestMethod.GET, RequestMethod.POST})
    public void getStockQuantity(HttpServletResponse response, @RequestBody String json){
        logger.info("getStockQuantity start, parameter:" + json);
        float stockQuantity = 0;

        Stock stock = writer.gson.fromJson(json, Stock.class);

        /**
         * 先从 redis 里获取对应商品编号的库存，没有再从数据库获取
         */
        List<Object> stocks = erpDao.getValuesFromHash(ErpConstant.stock + CommonConstant.underline + stock.getProductNo());
        if (stocks.isEmpty()) {
            stocks = erpDao.query(stock);
        }

        for (Object temp : stocks) {
            stockQuantity += ((Stock)temp).getQuantity();
        }

        writer.writeStringToJson(response, "{\"" + ErpConstant.stock_quantity +"\":\"" + stockQuantity + "\"}");

        logger.info("getStockQuantity start, end");
    }

    @RequestMapping(value = "/querySalePrice", method = {RequestMethod.GET, RequestMethod.POST})
    public void querySalePrice(HttpServletResponse response, @RequestBody String json){
        logger.info("querySalePrice start, parameter:" + json);

        Float salePrice;
        ProductPriceChange priceChange = writer.gson.fromJson(json, ProductPriceChange.class);

        if (priceChange.getNo() != null) {
            ProductPriceChange cachePriceChange = (ProductPriceChange) erpDao.getFromRedis((String) erpDao.getFromRedis(
                    ErpConstant.price_change + CommonConstant.underline + priceChange.getProduct().getId() + priceChange.getNo()));

            if (cachePriceChange != null) {
                salePrice = cachePriceChange.getPrice();
            } else {
                salePrice = ((List<ProductPriceChange>)erpDao.query(priceChange)).get(0).getPrice();
            }

        } else {
            salePrice = ((Product)erpDao.queryById(priceChange.getProduct().getId(), priceChange.getProduct().getClass())).getFatePrice();
        }

        writer.writeStringToJson(response, "{\"" + ErpConstant.price +"\":\"" + salePrice + "\"}");

        logger.info("querySalePrice start, end");
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

    /**
     *
     *  ========================= 顺丰接口 =======================
     *  https://open.sf-express.com/apitools/sdk.html
     */


    /**
     * 调用顺丰快递单接口，在顺丰系统生成快递单
     * @param json
     * @param response
     *
     *  快递单内容
     *
     *  请求报文内容
    字段名称 类型 是否
    必须
    描述
    orderId  String(56)  是  客户订单号，最大长度限于 56 位，该字段客户
    可自行定义，请尽量命名的规范有意义，如
    SFAPI20160830001，订单号作为客户下单的凭
    证， 不允许重复提交 订单号。
    expressType  String(5)  是  常用快件产品类别：
    类别 描述
    1  顺丰标快
    2  顺丰特惠
    3  电商特惠
    5  顺丰次晨
    6  顺丰即日
    7  电商速配
    15  生鲜速配
    payMethod  Number(1)  是  付款方式：
    类别 描述
    1  寄付现结（可不传 custId）
    /寄付月结 【默认值】 (必传
    custId)
    2  收方付
    3  第三方月结卡号支付
    isDoCall  Number(1)  否  是否下 call（通知收派员上门取件）
    类别 描述
    1  下 call
    0  不下 call【默认值】
    isGenBillno  Number(1)  否  是否申请运单号
    类别 描述
    1  申请【默认值】
    0  不申请
    isGenEletricPic  Number(1)  否  是否生成电子运单图片
    类别 描述
    1  生成【默认值】
    0  不生成
    custId  String(20)  是  顺丰月结卡号
    顺丰开放平台接口接入规范 V1.0
    12  顺丰科技
    2016 年 08 月 30 日
    payArea  String(20)  否  月结卡号对应的网点，如果付款方式为第三方月
    结卡号支付，则必填
    sendStartTime  String(18)
    否  要求上门取件开始时间，格式：YYYY-MM-DD
    HH24:MM:SS，示例：2016-8-30 09:30:00，
    默认值为系统收到订单的系统时间
    needReturnTrackingNo String(2)  否  是否需要签回单号
    类别 描述
    1  需要
    0  不需要【默认值】
    remark String(100) 否 备注，最大长度 30 个汉字
    deliverInfo  否  寄件方信息
    company  String(100)  否  寄件方公司名称
    如果不提供，将从系统默认配置获取
    contact  String(100)  否  寄件方联系人
    如果不提供，将从系统默认配置获取
    tel  String(20)  否  寄件方联系电话
    如果不提供，将从系统默认配置获取
    province  String(30)  否  寄件方所在省份，必须是标准的省名称称谓
    如： 广东省（省字不要省略）
    如果是直辖市，请直接传北京市、上海市等
    如果不提供，将从系统默认配置获取
    city  String(100)  否  寄件方所属城市名称，必须是标准的城市称谓
    如： 深圳市（市字不要省略）
    如果是直辖市，请直接传北京市、上海市等
    如果不提供，将从系统默认配置获取
    county  String(30)  否
    寄件人所在县/区，必须是标准的县/区称谓
    示例： 福田区（区字不要省略）
    如果不提供，将从系统默认配置获取
    address  String(200)  否  寄件方详细地址
    如：“福田区新洲十一街万基商务大厦 10 楼”
    如果不提供，将从系统默认配置获取
    shipperCode  String(30)  否  寄件方邮编代码
    mobile String(20) 否 寄件方手机
    consignee Info  收件方信息
    company  String(100)  是  到件方公司名称
    contact  String(100)  是  到件方联系人
    tel  String(20)  是  到件方联系电话
    province  String(30)  是  到件方所在省份，必须是标准的省名称称谓
    如：广东省（省字不要省略）
    如果是直辖市，请直接传北京市、上海市等
    city  String(100)  是  到件方所属城市名称，必须是标准的城市称谓
    如：深圳市（市字不要省略）
    如果是直辖市，请直接传北京市、上海市等
    county  String(30)  是
    到件人所在县/区，必须是标准的县/区称谓
    如：福田区（区字不要省略）
    address  String(200)  是  到件方详细地址
    如：“新洲十一街万基商务大厦 10 楼”
    shipperCode  String(30)  否  到件方邮编代码
    mobile String(20) 否 到件方手机
    顺丰开放平台接口接入规范 V1.0
    13  顺丰科技
    2016 年 08 月 30 日
    cargoInfo  货物信息
    parcelQuantity  Number(5)  否  包裹数，一个包裹对应一个运单号，如果是大于
    1 个包裹，则返回按照子母件的方式返回母运单
    号和子运单号。默认为 1
    cargo  String(4000)  是  货物名称，如果有多个货物，以英文逗号分隔，
    如：“手机,IPAD,充电器”
    cargoCount  String(4000)  否  货物数量，多个货物时以英文逗号分隔，且与货
    物名称一一对应
    如：2,1,3
    cargoUnit  String(4000)  否  货物单位，多个货物时以英文逗号分隔，且与货
    物名称一一对应
    如：个,台,本
    cargoWeight  String(4000)  否  货物重量，多个货物时以英文逗号分隔，且与货
    物名称一一对应
    如：1.0035,1.0,3.0
    cargoAmount  String(4000)  否  货物单价，多个货物时以英文逗号分隔，且与货
    物名称一一对应
    如：1000,2000,1500
    cargoTotalWeight Number(10,2) 否 订单货物总重量， 单位 KG， 如果提供此值， 必须>0
    addedServices  增值服务 （注意字段名称必须为英文字母大写 ）
    CUSTID  String(30)  否  代收货款月结卡号，如果选择 COD 增值服务-代
    收货款， 必填， 该项为代送货款使用的月结卡号，
    该项值必须在，COD 前设置（即先传 CUSTID 值再
    传 COD 值） 否则无效
    COD  String(20)  否  代收货款，value代收货款值，上限为20000，以
    原寄地所在区域币种为准，如中国大陆为人民
    币，香港为港币，保留1位小数，如 99.9 。
    value1为代收货款协议卡号（可能与月结卡号相
    同），
    如选择此服务，须增加CUSTID字段
    INSURE  String(30)  否  保价，value为声明价值(即该包裹的价值)
    MSG  String(30)  否  签收短信通知，value 为手机号码
    PKFREE  String(30)  否  包装费，value 为包装费费用.
    SINSURE String(30)  否  特殊保价，value 为服务费.
    SDELIVERY  String(30)  否  特殊配送，value为服务特殊配送服务费.
    SADDSERVICE  String(30)  否  特殊增值服务，value 特殊增值服务费
    5.1.1.2.1.2  响应 报文内容
    字段名称  类型  是否
    必须
    描述
    orderId  String  是  客户订单号
    filterLevel  String  否  筛单级别 0：不筛单 4：四级筛单
    orderTriggerCondi
    tion
    String  否  订单触发条件 1：上门收件 2 电子称 3：
    收件入仓 4：大客户装包 5：大客户装车
    remarkCode  String  否  01 ：下单操作成功 02：下单操作失败 03：
    订单号重复
     *
     */
    public void sfExpressOrder(String json, HttpServletResponse response) {
        logger.info("sfExpressOrder start, stockOutId:" + json);

        String result = CommonConstant.fail;

        //设置 uri
        String url = httpProxyDiscovery.getHttpProxyAddress() + sfExpress.getOrderUri();
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId(sfExpress.getAppId());
        appInfo.setAppKey(sfExpress.getAppKey());
        appInfo.setAccessToken(getSfToken(sfExpress.getAppId(), sfExpress.getAppKey()));

        List<OrderDetail> details = writer.gson.fromJson(json, new TypeToken<List<OrderDetail>>(){}.getType());
        try {
            for (OrderDetail detail : details) {
                Order order = ((List<Order>)writer.gson.fromJson(
                        orderClient.query(detail.getOrder().getClass().getSimpleName(), writer.gson.toJson(detail.getOrder())), new TypeToken<List<Order>>() {}.getType())).get(0);
                User user = ((List<User>)writer.gson.fromJson(
                        customerClient.query(order.getUser().getClass().getSimpleName(), writer.gson.toJson(order.getUser())), new TypeToken<List<User>>() {}.getType())).get(0);

                StockInOut stockOut = erpService.getLastStockInOutByProductAndType(detail.getProduct(), ErpConstant.stockOut);
                Warehouse warehouse = (Warehouse) erpDao.queryById(stockOut.getWarehouse().getId(), stockOut.getWarehouse().getClass());

                StockInOut stockIn = erpService.getLastStockInOutByProductAndType(detail.getProduct(), ErpConstant.stockIn);
                StockInOut dbStockIn = (StockInOut) erpDao.queryById(stockIn.getId(), stockIn.getClass());

                Product product = (Product) erpDao.queryById(detail.getProduct().getId(), detail.getProduct().getClass());

                //设置请求头
                MessageReq req = new MessageReq();
                HeadMessageReq head = new HeadMessageReq();
                head.setTransType(ErpConstant.sf_action_code_order);
                head.setTransMessageId(erpDao.getSfTransMessageId());
                req.setHead(head);

                OrderReqDto orderReqDto = new OrderReqDto();
                orderReqDto.setOrderId(ErpConstant.no_expressDelivery_perfix + head.getTransMessageId());
                orderReqDto.setExpressType((new Short("5")).shortValue());
                orderReqDto.setPayMethod((new Short("1")).shortValue());
                orderReqDto.setNeedReturnTrackingNo((new Short("0")).shortValue());
                orderReqDto.setIsDoCall((new Short("1")).shortValue());
                orderReqDto.setIsGenBillNo((new Short("1")).shortValue());
                orderReqDto.setCustId(sfExpress.getCustId());
                orderReqDto.setPayArea(sfExpress.getPayArea());
                orderReqDto.setSendStartTime(dateUtil.getSimpleDateFormat().format(details.get(0).getExpressDate()));
                orderReqDto.setRemark("易碎物品，小心轻放");

                //收件人信息
                DeliverConsigneeInfoDto consigneeInfoDto = new DeliverConsigneeInfoDto();
                consigneeInfoDto.setCompany(user.getCustomer().getHirer());
                consigneeInfoDto.setAddress(detail.getExpress().getAddress());
                consigneeInfoDto.setCity(detail.getExpress().getCity());
                consigneeInfoDto.setProvince(detail.getExpress().getProvince());
                consigneeInfoDto.setCountry(detail.getExpress().getCountry());
                consigneeInfoDto.setShipperCode(detail.getExpress().getPostCode());
                consigneeInfoDto.setMobile(detail.getExpress().getPhone());
                consigneeInfoDto.setTel(detail.getExpress().getPhone());
                consigneeInfoDto.setContact(detail.getExpress().getReceiver());

                //寄件人信息
                DeliverConsigneeInfoDto deliverInfoDto = new DeliverConsigneeInfoDto();
                deliverInfoDto.setAddress(warehouse.getCompany().getAddress());
                deliverInfoDto.setCity(warehouse.getCompany().getCity());
                deliverInfoDto.setProvince(warehouse.getCompany().getProvince());
                deliverInfoDto.setCountry(warehouse.getCompany().getCountry());
                deliverInfoDto.setShipperCode(warehouse.getCompany().getPostCode());
                deliverInfoDto.setTel(warehouse.getCompany().getPhone());
                deliverInfoDto.setContact(dbStockIn.getInputer().getName());
                deliverInfoDto.setCompany(warehouse.getCompany().getName());

                //货物信息
                CargoInfoDto cargoInfoDto = new CargoInfoDto();
                cargoInfoDto.setParcelQuantity(Integer.valueOf(1));
                cargoInfoDto.setCargo(product.getName());
                cargoInfoDto.setCargoCount(Float.toString(detail.getQuantity()));
                cargoInfoDto.setCargoUnit(detail.getUnit());
                cargoInfoDto.setCargoAmount(Float.toString(detail.getAmount()));

                String productWeight = null;
                for (ProductOwnProperty property : product.getProperties()) {
                    if (property.getName().contains(ErpConstant.product_property_name_weight)) {
                        productWeight = ((Integer.parseInt(property.getValue())/1000))+"";
                    }
                }
                if (productWeight != null) {
                    cargoInfoDto.setCargoWeight(productWeight);
                }

                orderReqDto.setDeliverInfo(deliverInfoDto);
                orderReqDto.setConsigneeInfo(consigneeInfoDto);
                orderReqDto.setCargoInfo(cargoInfoDto);
                req.setBody(orderReqDto);

                System.out.println("传入参数" + ToStringBuilder.reflectionToString(req));
                MessageResp messageResp = OrderTools.order(url, appInfo, req);

                /**
                 * 调用顺丰下单接口下单成功，保存本地快递单数据
                 */
                if (messageResp.getHead().getCode().equals(ErpConstant.sf_response_code_success)) {
                    ExpressDeliver expressDeliver = new ExpressDeliver();
                    expressDeliver.setNo(orderReqDto.getOrderId());
                    expressDeliver.setDeliver(ErpConstant.deliver_sfExpress);
                    expressDeliver.setType(orderReqDto.getExpressType()+"");
                    expressDeliver.setDate(Timestamp.valueOf(orderReqDto.getSendStartTime()));
                    expressDeliver.setState(ErpConstant.express_state_sending);

                    expressDeliver.setReceiverAddress(orderReqDto.getConsigneeInfo().getAddress());
                    expressDeliver.setReceiverCity(orderReqDto.getConsigneeInfo().getCity());
                    expressDeliver.setReceiverProvince(orderReqDto.getConsigneeInfo().getProvince());
                    expressDeliver.setReceiverCountry(orderReqDto.getConsigneeInfo().getCountry());
                    expressDeliver.setReceiverCompany(orderReqDto.getConsigneeInfo().getCompany());
                    expressDeliver.setReceiverMobile(orderReqDto.getConsigneeInfo().getMobile());
                    expressDeliver.setReceiverTel(orderReqDto.getConsigneeInfo().getTel());
                    expressDeliver.setReceiverPostCode(orderReqDto.getConsigneeInfo().getShipperCode());

                    expressDeliver.setSenderAddress(orderReqDto.getDeliverInfo().getAddress());
                    expressDeliver.setSenderCity(orderReqDto.getDeliverInfo().getCity());
                    expressDeliver.setSenderProvince(orderReqDto.getDeliverInfo().getProvince());
                    expressDeliver.setSenderCountry(orderReqDto.getDeliverInfo().getCountry());
                    expressDeliver.setSenderCompany(orderReqDto.getDeliverInfo().getCompany());
                    expressDeliver.setSenderMobile(orderReqDto.getDeliverInfo().getMobile());
                    expressDeliver.setSenderTel(orderReqDto.getDeliverInfo().getTel());
                    expressDeliver.setSenderPostCode(orderReqDto.getDeliverInfo().getShipperCode());

                    result += erpDao.save(expressDeliver);

                    ExpressDeliverDetail expressDeliverDetail = new ExpressDeliverDetail();
                    expressDeliverDetail.setProduct(product);
                    expressDeliverDetail.setQuantity(Integer.parseInt(orderReqDto.getCargoInfo().getCargoCount()));
                    expressDeliverDetail.setUnit(orderReqDto.getCargoInfo().getCargoUnit());
                    expressDeliverDetail.setWeight(orderReqDto.getCargoInfo().getCargoWeight());
                    expressDeliverDetail.setAmount(Float.valueOf(orderReqDto.getCargoInfo().getCargoAmount()));
                    expressDeliverDetail.setState(ErpConstant.express_detail_state_unReceive);
                    expressDeliverDetail.setExpressDeliver(expressDeliver);

                    result += erpDao.save(expressDeliverDetail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("sfExpressOrder end");
    }

    /**
     * 下载顺丰快递单
     * @param stockOutId
     * @return
     */
    public void downloadSfWaybill(HttpServletResponse response, Integer stockOutId) {
        String result = "";

        String url = httpProxyDiscovery.getHttpProxyAddress() + sfExpress.getOrderUri();
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId(sfExpress.getAppId());
        appInfo.setAppKey(sfExpress.getAppKey());
        appInfo.setAccessToken(getSfToken(sfExpress.getAppId(), sfExpress.getAppKey()));

        StockInOut stockInOut = (StockInOut) erpDao.queryById(stockOutId, StockInOut.class);

        ExpressDeliverDetail dbExpressDeliverDetail = null;
        ExpressDeliverDetail expressDeliverDetail = new ExpressDeliverDetail();
        for (StockInOutDetail detail : stockInOut.getDetails()) {
            expressDeliverDetail.setProduct(detail.getProduct());
            expressDeliverDetail.setState(ErpConstant.express_detail_state_unReceive);

            dbExpressDeliverDetail = (ExpressDeliverDetail) erpDao.query(expressDeliverDetail).get(0);

            //设置请求头
            MessageReq<WaybillReqDto> req = new MessageReq<>();
            HeadMessageReq head = new HeadMessageReq();
            head.setTransType(ErpConstant.sf_action_code_download_waybill);
            head.setTransMessageId(erpDao.getSfTransMessageId());
            req.setHead(head);

            WaybillReqDto waybillReqDto = new WaybillReqDto();
            waybillReqDto.setOrderId(dbExpressDeliverDetail.getExpressDeliver().getNo());
            req.setBody(waybillReqDto);

            try {
                MessageResp<WaybillRespDto> messageResp = WaybillDownloadTools.waybillDownload(url, appInfo, req);

                if (messageResp.getHead().getCode().equals(ErpConstant.sf_response_code_success)) {
                    String[] images = messageResp.getBody().getImages();

                    for (String image : images) {
                        result = "<img src='data:image/png;base64," + image + "'/><br/><br/>";
                    }
                } else {
                    result = "下载顺丰快递单失败";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
    }

    /**
     * 获取顺丰 token
     * @param appId
     * @param appKey
     * @return
     */
    public String getSfToken(String appId, String appKey) {
        String token = (String) erpDao.getFromRedis(ErpConstant.sf_access_token_key);

        if (token != null) {
            refreshSfTokens((String) erpDao.getFromRedis(ErpConstant.sf_access_token_key),
                    (String) erpDao.getFromRedis(ErpConstant.sf_refresh_token_key));
        } else {
            setSfTokens();
        }

        return (String) erpDao.getFromRedis(ErpConstant.sf_access_token_key);
    }

    public void setSfTokens() {
        String url = httpProxyDiscovery.getHttpProxyAddress() + sfExpress.getTokenUri();
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId(sfExpress.getAppId());
        appInfo.setAppKey(sfExpress.getAppKey());

        MessageReq<TokenReqDto> req = new MessageReq<>();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(ErpConstant.sf_action_code_access_token);
        head.setTransMessageId(erpDao.getSfTransMessageId());
        req.setHead(head);

        try {
            MessageResp<TokenRespDto> messageResp = SecurityTools.applyAccessToken(url, appInfo, req);

            if (messageResp.getHead().getCode().equals(ErpConstant.sf_response_code_success)) {
                erpDao.storeToRedis(ErpConstant.sf_access_token_key, messageResp.getBody().getAccessToken(), ErpConstant.sf_token_time);
                erpDao.storeToRedis(ErpConstant.sf_refresh_token_key, messageResp.getBody().getRefreshToken(), ErpConstant.sf_token_time);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshSfTokens(String accessToken, String refreshToken) {
        String url = httpProxyDiscovery.getHttpProxyAddress() + sfExpress.getTokenRefreshUri();
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId(sfExpress.getAppId());
        appInfo.setAppKey(sfExpress.getAppKey());
        appInfo.setAccessToken(accessToken);
        appInfo.setRefreshToken(refreshToken);

        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(ErpConstant.sf_action_code_refresh_Token);
        head.setTransMessageId(erpDao.getSfTransMessageId());
        req.setHead(head);

        try {
            MessageResp<TokenRespDto> messageResp = SecurityTools.refreshAccessToken(url, appInfo, req);

            if (messageResp.getHead().getCode().equals(ErpConstant.sf_response_code_refresh_token_unExist) ||
                    messageResp.getHead().getCode().equals(ErpConstant.sf_response_code_refresh_token_timeout)){
                setSfTokens();

            } else if(messageResp.getHead().getCode().equals(ErpConstant.sf_response_code_success)) {
                erpDao.storeToRedis(ErpConstant.sf_access_token_key, messageResp.getBody().getAccessToken(), ErpConstant.sf_token_time);
                erpDao.storeToRedis(ErpConstant.sf_refresh_token_key, messageResp.getBody().getRefreshToken(), ErpConstant.sf_token_time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
