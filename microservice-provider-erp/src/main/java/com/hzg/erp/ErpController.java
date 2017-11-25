package com.hzg.erp;

import com.google.gson.reflect.TypeToken;
import com.hzg.sys.*;
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
import org.springframework.web.bind.annotation.*;

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
    private ErpService erpService;

    @Autowired
    private Transcation transcation;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private  SysClient sysClient;

    @Autowired
    private  StrUtil strUtil;

    @Autowired
    private HttpProxyDiscovery httpProxyDiscovery;

    @Autowired
    private SfExpress sfExpress;

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
            if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
                Purchase purchase = writer.gson.fromJson(json, Purchase.class);
                purchase.setInputDate(inputDate);
                result += erpDao.save(purchase);

                result += erpService.savePurchaseProducts(purchase);

                /**
                 * 发起采购流程
                 */
                String auditEntity = AuditFlowConstant.business_purchase;
                if (purchase.getType().compareTo(ErpConstant.purchase_type_emergency) == 0 ||
                        purchase.getType().compareTo(ErpConstant.purchase_type_cash) == 0 ||
                        purchase.getType().compareTo(ErpConstant.purchase_type_deposit) == 0) {
                    auditEntity = AuditFlowConstant.business_purchaseEmergency;
                }

                result += erpService.launchAuditFlow(auditEntity, purchase.getId(), purchase.getName(),
                        "请审核采购单：" + purchase.getNo() ,purchase.getInputer());

            }else if(entity.equalsIgnoreCase(ProductCheck.class.getSimpleName())){
                ProductCheck productCheck = writer.gson.fromJson(json, ProductCheck.class);
                ProductCheck productCheck1 = new ProductCheck();
                productCheck1.setCheckNo(productCheck.getCheckNo());
                if (erpDao.query(productCheck1) == null || erpDao.query(productCheck1).isEmpty()){
                    // 设置盘点日期
                    productCheck.setCheckDate(inputDate);
                    com.hzg.sys.User user = productCheck.getChartMaker();
                    user = (com.hzg.sys.User)(erpDao.query(user).get(0));
                    Set<Post> posts = user.getPosts();
                    Post post = posts.iterator().next();
                    Dept dept = post.getDept();
                    Company company = dept.getCompany();
                    // 设置盘点部门
                    productCheck.setDept(dept);
                    // 设置盘点公司
                    productCheck.setCompany(company);
                    // 保存商品盘点单
                    result += erpDao.save(productCheck);
                } else {
                    productCheck1 = (ProductCheck) (erpDao.query(productCheck1).get(0));
                    productCheck.setId(productCheck1.getId());
                }
                // 保存商品盘点单详细条目
                result += erpService.saveProductsCheckDetail(productCheck);

            } else if (entity.equalsIgnoreCase(Supplier.class.getSimpleName())) {
                Supplier supplier = writer.gson.fromJson(json, Supplier.class);
                supplier.setInputDate(inputDate);
                result += erpDao.save(supplier);

            } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
                Product product = writer.gson.fromJson(json, Product.class);
                result += erpDao.save(product);

            } else if (entity.equalsIgnoreCase(ProductType.class.getSimpleName())) {
                ProductType productType = writer.gson.fromJson(json, ProductType.class);
                result += erpDao.save(productType);

            } else if (entity.equalsIgnoreCase(ProductProperty.class.getSimpleName())) {
                ProductProperty productProperty = writer.gson.fromJson(json, ProductProperty.class);
                result += erpDao.save(productProperty);

            } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
                StockInOut stockInOut = writer.gson.fromJson(json, StockInOut.class);
                stockInOut.setInputDate(dateUtil.getSecondCurrentTimestamp());

                /**
                 * 保存入库数据
                 */
                if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) < 0) {
                    String msg = erpService.isCanStockIn(stockInOut);
                    if (msg == null) {
                        result += erpService.saveStockInOut(stockInOut);
                    } else {
                        result += msg;
                    }

                /**
                 * 保存出库数据
                  */
                } else {
                    String msg = erpService.isCanStockOut(stockInOut);
                    if (msg == null) {
                        result += erpService.saveStockInOut(stockInOut);
                    } else {
                        result += msg;
                    }
                }

                result = "{\"" + CommonConstant.result + "\":\"" + transcation.dealResult(result) + "\"" +
                        ",\"" + CommonConstant.id + "\":" + stockInOut.getId() + "}";
                writer.writeStringToJson(response, result);
                logger.info("save end, result:" + result);
                return;

            } else if (entity.equalsIgnoreCase(Warehouse.class.getSimpleName())) {
                Warehouse warehouse = writer.gson.fromJson(json, Warehouse.class);
                warehouse.setInputDate(inputDate);
                result += erpDao.save(warehouse);

            } else if (entity.equalsIgnoreCase(ProductPriceChange.class.getSimpleName())) {
                result += erpService.saveOrUpdatePriceChange(json, CommonConstant.save);
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
                PurchaseDetail purchaseDetail = (PurchaseDetail) erpDao.queryById(((PurchaseDetail) dbPurchase.getDetails().toArray()[0]).getId(), PurchaseDetail.class);
                Product stateProduct = (Product) erpDao.queryById(((PurchaseDetailProduct) purchaseDetail.getPurchaseDetailProducts().toArray()[0]).getProduct().getId(), Product.class);

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

                    result += erpService.updateAudit(dbPurchase.getId(), oldEntity, newEntity, purchase.getName(), "请审核采购单：" + dbPurchase.getNo());

                } else {
                    result = CommonConstant.fail + ", 采购单 " + purchase.getNo() + " 里的商品，已审核通过，不能修改";
                }

            } else if (entity.equalsIgnoreCase(Supplier.class.getSimpleName())) {
                Supplier supplier = writer.gson.fromJson(json, Supplier.class);
                result += erpDao.updateById(supplier.getId(), supplier);

            } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
                Product product = writer.gson.fromJson(json, Product.class);
                Product dbProduct = (Product) erpDao.queryById(product.getId(), Product.class);

                if (dbProduct.getState().compareTo(ErpConstant.product_state_edit) == 0) {
                    if (product.getDescribe() != null) {
                        result += erpDao.updateById(product.getDescribe().getId(), product.getDescribe());
                    }

                    if (product.getProperties() != null) {
                        for (ProductOwnProperty property : product.getProperties()) {
                            Product tempProduct = new Product();
                            tempProduct.setId(product.getId());

                            property.setProduct(tempProduct);
                            result += erpDao.save(property);
                        }

                        for (ProductOwnProperty property : dbProduct.getProperties()) {
                            result += erpDao.delete(property);
                        }
                    }

                    result += erpDao.updateById(product.getId(), product);
                } else {
                    result += CommonConstant.fail + ", 只有编辑状态的商品才可以修改";
                }

            } else if (entity.equalsIgnoreCase(ProductDescribe.class.getSimpleName())) {
                Product product = writer.gson.fromJson(json, Product.class);
                List<Product> stateProducts = erpDao.query(product);

                String msg = null;
                for (Product stateProduct : stateProducts) {
                    /**
                     * 商品描述对应商品中有在售的商品，则不能修改商品描述
                     */
                    if (stateProduct.getState().compareTo(ErpConstant.product_state_edit) != 0 &&
                            stateProduct.getState().compareTo(ErpConstant.product_state_mediaFiles_uploaded) != 0) {
                        msg = CommonConstant.fail + ", 商品 " + stateProduct.getNo() + "不是已上传多媒体文件状态或编辑状态，不能编辑商品描述";

                        break;
                    }
                }

                if (msg == null) {
                    product.getDescribe().setDate(dateUtil.getSecondCurrentTimestamp());
                    result += erpDao.updateById(product.getDescribe().getId(), product.getDescribe());

                    for (Product ele : stateProducts) {
                        product.setState(ErpConstant.product_state_edit);
                        result += erpDao.updateById(ele.getId(), product);
                    }
                } else {
                    result += msg;
                }

            }  else if (entity.equalsIgnoreCase(ProductType.class.getSimpleName())) {
                ProductType productType = writer.gson.fromJson(json, ProductType.class);
                result += erpDao.updateById(productType.getId(), productType);

            } else if (entity.equalsIgnoreCase(ProductProperty.class.getSimpleName())) {
                ProductProperty productProperty = writer.gson.fromJson(json, ProductProperty.class);
                result += erpDao.updateById(productProperty.getId(), productProperty);

            } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
                StockInOut stockInOut = writer.gson.fromJson(json, StockInOut.class);
                StockInOut dbStockInOut = (StockInOut) erpDao.queryById(stockInOut.getId(), StockInOut.class);

                /**
                 * 修改入库信息
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
                        result += erpService.saveStockInOutDetails(stockInOut);
                        for (StockInOutDetail detail : dbStockInOut.getDetails()) {

                            StockInOutDetailProduct detailProduct = new StockInOutDetailProduct();
                            detailProduct.setStockInOutDetail(detail);
                            List<StockInOutDetailProduct> detailProducts = erpDao.query(detailProduct);

                            for (StockInOutDetailProduct dbDetailProduct : detailProducts) {
                                erpDao.delete(dbDetailProduct);
                            }

                            result += erpDao.delete(detail);
                        }

                    } else {
                        result += CommonConstant.fail + ", 入库单 " + stockInOut.getNo() + " 不是申请状态，不能修改";
                    }
                }

                result = "{\"" + CommonConstant.result + "\":\"" + transcation.dealResult(result) + "\"" +
                        ",\"" + CommonConstant.id + "\":" + stockInOut.getId() +
                        ",\"" + CommonConstant.type + "\":" + stockInOut.getType() +
                        ",\"" + CommonConstant.state + "\":" + stockInOut.getState() + "}";
                writer.writeStringToJson(response, result);
                logger.info("save end, result:" + result);
                return;

            } else if (entity.equalsIgnoreCase(Warehouse.class.getSimpleName())) {
                Warehouse warehouse = writer.gson.fromJson(json, Warehouse.class);
                result += erpDao.updateById(warehouse.getId(), warehouse);

            } else if (entity.equalsIgnoreCase(ProductPriceChange.class.getSimpleName())) {
                ProductPriceChange priceChange = writer.gson.fromJson(json, ProductPriceChange.class);
                ProductPriceChange dbPriceChange = (ProductPriceChange) erpDao.queryById(priceChange.getId(), priceChange.getClass());

                if (dbPriceChange.getState().compareTo(ErpConstant.product_price_change_state_save) == 0) {
                    result += erpService.saveOrUpdatePriceChange(json, CommonConstant.update);
                } else {
                    result += CommonConstant.fail + ",已在用或申请状态的变动价格不能修改";
                }

            } else if (entity.equalsIgnoreCase(ProductCheck.class.getSimpleName())) {
                ProductCheck productCheck = writer.gson.fromJson(json, ProductCheck.class);
                result += erpDao.updateById(productCheck.getId(), productCheck);
                Set<ProductCheckDetail> productCheckDetails = productCheck.getDetails();
                for (ProductCheckDetail productCheckDetail : productCheckDetails) {
                    result += erpDao.updateById(productCheckDetail.getId(), productCheckDetail);
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
            Map<String, Object> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());

            if (name.equalsIgnoreCase(ErpConstant.product_action_name_updateUploadMediaFilesInfo)) {
                Product product = writer.gson.fromJson(writer.gson.toJson(queryParameters.get("product")), Product.class);

                product.setState(ErpConstant.product_state_mediaFiles_uploaded);

                result += erpDao.updateById(product.getId(), product);
                result += erpDao.updateById(product.getDescribe().getId(), product.getDescribe());


                Audit queryAudit = new Audit();
                queryAudit.setEntity(AuditFlowConstant.business_product);
                queryAudit.setEntityId(product.getId());
                List<Audit> audits = writer.gson.fromJson(sysClient.query(queryAudit.getClass().getSimpleName(), writer.gson.toJson(queryAudit)),
                        new TypeToken<List<Audit>>(){}.getType());

                if (audits.isEmpty()) {
                    /**
                     * 上传完商品多媒体图片后，发起商品上架审核流程
                     */
                    result += erpService.launchAuditFlowByPost(AuditFlowConstant.business_product, product.getId(),
                            "编辑、审核及上架编号为:" + product.getNo() + "的商品",
                            writer.gson.fromJson(writer.gson.toJson(queryParameters.get("post")), Post.class),
                            erpService.queryProductOnSalePreFlowAuditNo(product));
                }

            } else if (name.equalsIgnoreCase(ErpConstant.product_action_name_setProductsSold)) {
                List<Product> products = writer.gson.fromJson(writer.gson.toJson(queryParameters.get("product")), new TypeToken<List<Product>>(){}.getType());
                for (Product product : products) {

                    boolean isSetProductSold = true;
                    if (product.getSoldUnit().equals(ErpConstant.unit_g) || product.getSoldUnit().equals(ErpConstant.unit_kg) ||
                            product.getSoldUnit().equals(ErpConstant.unit_oz) || product.getSoldUnit().equals(ErpConstant.unit_ct) ) {
                        if (erpService.getCanSellProductQuantity(product.getNo()).compareTo(product.getSoldQuantity()) > 0) {
                            isSetProductSold = false;
                        }
                    }

                    if (isSetProductSold) {
                        product.setState(ErpConstant.product_state_sold);
                        result += erpDao.updateById(product.getId(), product);
                    } else {
                        result += CommonConstant.success;
                    }
                }

            } else if (name.equalsIgnoreCase(ErpConstant.stockInOut_action_name_inProduct)) {
                Action action = writer.gson.fromJson(json, Action.class);
                StockInOut stockOut = erpService.queryStockInOut(action.getEntityId());
                stockOut.setState(ErpConstant.stockInOut_state_finished);

                /**
                 * 入库，设置库存
                 */
                 result += erpService.stockIn(stockOut);

                action.setEntity(ErpConstant.stockInOut);
                action.setType(ErpConstant.stockInOut_action_inProduct);
                action.setInputer(erpService.getUserBySessionId(action.getSessionId()));
                action.setInputDate(dateUtil.getSecondCurrentTimestamp());
                result += erpDao.save(action);

                result = "{\"" + CommonConstant.result + "\":\"" + transcation.dealResult(result) + "\"" +
                        ",\"" + CommonConstant.id + "\":" + stockOut.getId() +
                        ",\"" + CommonConstant.type + "\":" + stockOut.getType() +
                        ",\"" + CommonConstant.state + "\":" + stockOut.getState() + "}";
                writer.writeStringToJson(response, result);
                logger.info("save end, result:" + result);
                return;

            } else if (name.equalsIgnoreCase(ErpConstant.stockInOut_action_name_outProduct)) {
                Action action = writer.gson.fromJson(json, Action.class);
                StockInOut stockOut = erpService.queryStockInOut(action.getEntityId());
                stockOut.setState(ErpConstant.stockInOut_state_finished);

                result += erpService.stockOut(stockOut);

                action.setEntity(ErpConstant.stockInOut);
                action.setType(ErpConstant.stockInOut_action_outProduct);
                action.setInputer(erpService.getUserBySessionId(action.getSessionId()));
                action.setInputDate(dateUtil.getSecondCurrentTimestamp());
                result += erpDao.save(action);

                result = "{\"" + CommonConstant.result + "\":\"" + transcation.dealResult(result) + "\"" +
                        ",\"" + CommonConstant.id + "\":" + stockOut.getId() +
                        ",\"" + CommonConstant.type + "\":" + stockOut.getType() +
                        ",\"" + CommonConstant.state + "\":" + stockOut.getState() + "}";
                writer.writeStringToJson(response, result);
                logger.info("save end, result:" + result);
                return;

            } else if (name.equalsIgnoreCase(ErpConstant.product_action_name_upShelf)) {
                Action action = writer.gson.fromJson(json, Action.class);
                result += erpService.changeProductState(action.getEntityIds(), ErpConstant.product_state_edit, ErpConstant.product_state_onSale);

                action.setEntity(ErpConstant.product);
                action.setType(ErpConstant.product_action_upShelf);
                action.setInputer(erpService.getUserBySessionId(action.getSessionId()));
                action.setInputDate(dateUtil.getSecondCurrentTimestamp());
                for (Integer entityId : action.getEntityIds()) {
                    action.setEntityId(entityId);
                    result += erpDao.save(action);
                }

            } else if (name.equalsIgnoreCase(ErpConstant.product_action_name_downShelf)) {
                Action action = writer.gson.fromJson(json, Action.class);
                result += erpService.changeProductState(action.getEntityIds(), ErpConstant.product_state_onSale, ErpConstant.product_state_edit);

                action.setEntity(ErpConstant.product);
                action.setType(ErpConstant.product_action_upShelf);
                action.setInputer(erpService.getUserBySessionId(action.getSessionId()));
                action.setInputDate(dateUtil.getSecondCurrentTimestamp());
                for (Integer entityId : action.getEntityIds()) {
                    action.setEntityId(entityId);
                    result += erpDao.save(action);
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

    @Transactional
    @PostMapping("/print")
    public void print(HttpServletResponse response, String name, @RequestBody String json){
        logger.info("print start, parameter:" + name + ":" + json);

        String printContent = "";
        Action action = writer.gson.fromJson(json, Action.class);
        StockInOut stockInOut = (StockInOut) erpDao.queryById(action.getEntityId(), StockInOut.class);

        if (name.equalsIgnoreCase(ErpConstant.stockInOut_action_name_print_barcode)) {
            printContent = erpService.generateBarcodes(stockInOut);
            action.setType(ErpConstant.stockInOut_action_print_barcode);

        } else if (name.equalsIgnoreCase(ErpConstant.stockInOut_action_name_print_stockOutBills)) {
            printContent = ((Map<String, Object>) writer.gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType())).get(CommonConstant.printContent).toString();
            action.setType(ErpConstant.stockInOut_action_print_stockOutBills);

        } else if (name.equalsIgnoreCase(ErpConstant.stockInOut_action_name_print_expressWaybill)) {
            if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_normal_outWarehouse) == 0) {
                printContent = downloadSfWaybill(stockInOut);

            } else if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_normal_outWarehouse_manual) == 0) {
                /**
                 * 人工正常出库打印顺丰快递单，先生成顺丰快递单，再下载快递单图片
                 */
                ExpressDeliver expressDeliver = erpService.generateExpressDeliver(
                        writer.gson.fromJson(((Map<String, Object>)writer.gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType())).get(CommonConstant.expressReceiverInfo).toString(), ExpressDeliver.class), stockInOut);
                String sfExpressOrderResult = expressDeliverOrder(expressDeliver);

                if (!sfExpressOrderResult.contains(CommonConstant.fail)) {
                    printContent = downloadSfWaybill(stockInOut);
                }
            }

            action.setType(ErpConstant.stockInOut_action_print_expressWaybill);
        }

        action.setEntity(ErpConstant.stockInOut);
        action.setInputer(erpService.getUserBySessionId(action.getSessionId()));
        action.setInputDate(dateUtil.getSecondCurrentTimestamp());
        String result = erpDao.save(action);

        if (result.contains(CommonConstant.fail)) {
            writer.write(response, result);
        } else {
            writer.write(response, printContent);
        }
        logger.info("print end, result:" + result);
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
                PurchaseDetail purchaseDetail = (PurchaseDetail) erpDao.queryById(((PurchaseDetail) dbPurchase.getDetails().toArray()[0]).getId(), PurchaseDetail.class);
                Product stateProduct = (Product) erpDao.queryById(((PurchaseDetailProduct) purchaseDetail.getPurchaseDetailProducts().toArray()[0]).getProduct().getId(), Product.class);

                if (stateProduct.getState().compareTo(ErpConstant.product_state_purchase) == 0) {       //采购状态的才可以修改
                    Purchase tempPurchase = new Purchase();

                    tempPurchase.setId(purchase.getId());
                    tempPurchase.setState(ErpConstant.purchase_state_cancel);
                    result += erpDao.updateById(purchase.getId(), tempPurchase);

                    /**
                     * 修改商品为无效状态
                     */
                    for (PurchaseDetail detail : dbPurchase.getDetails()) {
                        PurchaseDetail dbDetail = (PurchaseDetail) erpDao.queryById(detail.getId(), detail.getClass());

                        for (PurchaseDetailProduct detailProduct : dbDetail.getPurchaseDetailProducts()) {
                            detailProduct.getProduct().setState(ErpConstant.product_state_invalid);
                            result += erpDao.updateById(detailProduct.getProduct().getId(), detailProduct.getProduct());
                        }
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
                    result += CommonConstant.fail + ", 采购单 " + dbPurchase.getNo() + " 里的商品，已审核通过，不能作废";
                }


            } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
                Product product = writer.gson.fromJson(json, Product.class);
                Product dbProduct = (Product) erpDao.queryById(product.getId(), Product.class);

                if (dbProduct.getState().compareTo(ErpConstant.product_state_edit) == 0) {
                    Product tempProduct = new Product();
                    tempProduct.setId(product.getId());
                    tempProduct.setState(ErpConstant.product_state_invalid);

                    result += erpDao.updateById(product.getId(), product);
                } else {
                    result += CommonConstant.fail + ", 只有编辑状态的商品才可以作废";
                }

            } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
                StockInOut stockInOut = writer.gson.fromJson(json, StockInOut.class);
                StockInOut dbStockInOut = (StockInOut) erpDao.queryById(stockInOut.getId(), StockInOut.class);

                /**
                 * 入库
                 */
                if (dbStockInOut.getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) < 0) {
                    if (dbStockInOut.getState().compareTo(ErpConstant.stockInOut_state_apply) == 0) {

                        StockInOut tempStockInOut = new StockInOut();
                        tempStockInOut.setId(stockInOut.getId());
                        tempStockInOut.setState(ErpConstant.stockInOut_state_cancel);
                        result += erpDao.updateById(stockInOut.getId(), tempStockInOut);
                    } else {
                        result += CommonConstant.fail + ", 入库单不是申请状态，不能作废";
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

                if (dbPurchase.getState().compareTo(ErpConstant.purchase_state_cancel) == 0) {       //作废状态的才可以恢复
                    dbPurchase.setState(ErpConstant.purchase_state_apply);
                    result += erpDao.updateById(purchase.getId(), dbPurchase);

                    /**
                     * 修改商品为采购状态
                     */
                    for (PurchaseDetail detail : dbPurchase.getDetails()) {
                        PurchaseDetail dbDetail = (PurchaseDetail) erpDao.queryById(detail.getId(), detail.getClass());

                        for (PurchaseDetailProduct detailProduct : dbDetail.getPurchaseDetailProducts()) {
                            detailProduct.getProduct().setState(ErpConstant.product_state_purchase);
                            result += erpDao.updateById(detailProduct.getProduct().getId(), detailProduct.getProduct());
                        }
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
                    result += erpService.launchAuditFlow(auditEntity, dbPurchase.getId(), dbPurchase.getName(),
                            "请审核采购单：" + purchase.getNo() ,dbPurchase.getInputer());


                } else {
                    result += CommonConstant.fail + ", 采购单 " + dbPurchase.getNo() + " 里的商品，不是作废状态，不能恢复";
                }


            } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
                Product product = writer.gson.fromJson(json, Product.class);
                Product dbProduct = (Product) erpDao.queryById(product.getId(), Product.class);

                if (dbProduct.getState().compareTo(ErpConstant.product_state_edit) == 0) {
                    Product tempProduct = new Product();
                    tempProduct.setId(product.getId());
                    tempProduct.setState(ErpConstant.product_state_stockIn);

                    result += erpDao.updateById(product.getId(), product);
                } else {
                    result += CommonConstant.fail + ", 只有作废状态的商品才可以恢复";
                }

            } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
                StockInOut stockInOut = writer.gson.fromJson(json, StockInOut.class);
                StockInOut dbStockInOut = (StockInOut) erpDao.queryById(stockInOut.getId(), StockInOut.class);

                /**
                 * 入库
                 */
                if (dbStockInOut.getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) < 0) {
                    if (dbStockInOut.getState().compareTo(ErpConstant.stockInOut_state_cancel) == 0) {

                        StockInOut tempStockInOut = new StockInOut();
                        tempStockInOut.setId(stockInOut.getId());
                        tempStockInOut.setState(ErpConstant.stockInOut_state_apply);
                        result += erpDao.updateById(stockInOut.getId(), tempStockInOut);

                    } else {
                        result += CommonConstant.fail + ", 入库单 " + dbStockInOut.getNo() + " 不是作废状态，不能恢复";
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
                    PurchaseDetailProduct detailProduct = new PurchaseDetailProduct();
                    detailProduct.setPurchaseDetail(detail);
                    PurchaseDetailProduct dbDetailProduct = (PurchaseDetailProduct) erpDao.query(detailProduct).get(0);

                    detail.setProduct((Product)erpDao.queryById(dbDetailProduct.getProduct().getId(), dbDetailProduct.getProduct().getClass()));
                }
            }

            writer.writeObjectToJson(response, purchases);

        } else if (entity.equalsIgnoreCase(Supplier.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.query(writer.gson.fromJson(json, Supplier.class)));

        } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.query(writer.gson.fromJson(json, Product.class)));

        } else if (entity.equalsIgnoreCase(ProductDescribe.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.query(writer.gson.fromJson(json, ProductDescribe.class)));

        } else if (entity.equalsIgnoreCase(ProductType.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.query(writer.gson.fromJson(json, ProductType.class)));

        } else if (entity.equalsIgnoreCase(ProductProperty.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.query(writer.gson.fromJson(json, ProductProperty.class)));

        } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
            List<StockInOut> stockInOuts = erpDao.query(writer.gson.fromJson(json, StockInOut.class));

            for (StockInOut stockInOut : stockInOuts) {
                Set<StockInOutDetail> dbDetails = new HashSet<>();
                for (StockInOutDetail detail : stockInOut.getDetails()) {
                    StockInOutDetail dbDetail = (StockInOutDetail) erpDao.queryById(detail.getId(), detail.getClass());
                    dbDetail.setProduct((Product) erpDao.queryById(((StockInOutDetailProduct)dbDetail.getStockInOutDetailProducts().toArray()[0]).getProduct().getId(), Product.class));

                    dbDetails.add(dbDetail);
                }

                stockInOut.setDetails(dbDetails);

                if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_deposit) == 0) {
                    PurchaseDetailProduct detailProduct = new PurchaseDetailProduct();
                    detailProduct.setProduct(((StockInOutDetail)stockInOut.getDetails().toArray()[0]).getProduct());
                    PurchaseDetailProduct dbDetailProduct = (PurchaseDetailProduct) erpDao.query(detailProduct).get(0);

                    stockInOut.getDeposit().setPurchase(((PurchaseDetail)erpDao.queryById(dbDetailProduct.getPurchaseDetail().getId(), dbDetailProduct.getPurchaseDetail().getClass())).getPurchase());
                }

                if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_changeWarehouse_outWarehouse) == 0) {
                    stockInOut.getChangeWarehouse().setTargetWarehouse(
                            (Warehouse) erpDao.queryById(stockInOut.getChangeWarehouse().getTargetWarehouse().getId(), stockInOut.getChangeWarehouse().getTargetWarehouse().getClass()));
                }
            }

            writer.writeObjectToJson(response, stockInOuts);

        } else if (entity.equalsIgnoreCase(Stock.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpService.privateQuery(entity,
                    "{\"" + Stock.class.getSimpleName().toLowerCase() + "\":" + json + "}", 0, -1));

        } else if (entity.equalsIgnoreCase(Warehouse.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.query(writer.gson.fromJson(json, Warehouse.class)));

        } else if (entity.equalsIgnoreCase(ProductPriceChange.class.getSimpleName())) {
            List<ProductPriceChange> priceChanges = erpDao.query(writer.gson.fromJson(json, ProductPriceChange.class));
            Product queryProduct = new Product();

            for (ProductPriceChange priceChange : priceChanges) {
                queryProduct.setNo(priceChange.getProductNo());
                queryProduct.setFatePrice(priceChange.getPrePrice());
                priceChange.setProduct((Product)erpDao.query(queryProduct).get(0));
            }

            writer.writeObjectToJson(response, priceChanges);

        } else if (entity.equalsIgnoreCase(ProductCheck.class.getSimpleName()) || entity.equalsIgnoreCase(ErpConstant.productCheckInput)) {
            List<ProductCheck> productChecks = erpDao.query(writer.gson.fromJson(json, ProductCheck.class));
            for (ProductCheck productCheck : productChecks) {
                Float amount = 0.0f;
                Set<ProductCheckDetail> productCheckDetails = productCheck.getDetails();
                String remark = "";
                for (ProductCheckDetail productCheckDetail : productCheckDetails) {
                    // 盘点数量为0，说明该条形码对应的商品编码之前已经扫过，即为同一种商品
                    if (productCheckDetail.getCheckQuantity() == 0.00f) {
                        continue;
                    }
                    // 盘点金额为0，说明该商品不存在
                    if (productCheckDetail.getCheckAmount() == null) {
                        remark += "条形码为" + productCheckDetail.getProduct().getId() + "，即商品编码为" + productCheckDetail.getItemNo() + "的商品不存在，盈亏总金额不包含该条形码的！";
                        continue;
                    }
                    // 存在该商品，但没有入库，即库存数量为null
                    if (productCheckDetail.getPaperQuantity() == null) {
                        productCheckDetail.setQuantity(productCheckDetail.getCheckQuantity());
                        productCheckDetail.setAmount(productCheckDetail.getCheckAmount());
                        amount += productCheckDetail.getAmount();
                    } else {
                        productCheckDetail.setQuantity(productCheckDetail.getCheckQuantity() - productCheckDetail.getPaperQuantity());
                        productCheckDetail.setAmount(productCheckDetail.getCheckAmount() - productCheckDetail.getPaperAmount());
                        amount += productCheckDetail.getAmount();
                    }
                }
                if (productCheck.getRemark() == null || productCheck.getRemark() == ""){
                    productCheck.setRemark(remark);
                }
                productCheck.setAmount(amount);
            }
            writer.writeObjectToJson(response, productChecks);

        }

        logger.info("query end");
    }

    @RequestMapping(value = "/privateQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void privateQuery(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("privateQuery start, parameter:" + entity + ":" + json);

        Map<String, Object> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());

        if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
            StockInOut lastStockInOut = erpService.getLastStockInOutByProductAndType(
                    writer.gson.fromJson(writer.gson.toJson(queryParameters.get("product")), Product.class), (String)queryParameters.get("type"));

            if (lastStockInOut != null) {
                if (lastStockInOut.getType().compareTo(ErpConstant.stockInOut_type_changeWarehouse_outWarehouse) == 0) {
                    lastStockInOut.setChangeWarehouse(
                            (StockChangeWarehouse) erpDao.queryById(lastStockInOut.getChangeWarehouse().getId(), lastStockInOut.getChangeWarehouse().getClass()));
                }
            }

            writer.writeObjectToJson(response, lastStockInOut);

        } else if (entity.equalsIgnoreCase(ErpConstant.productUnit)) {
            String unit = "";
            if (erpDao.queryById(writer.gson.fromJson(json, Product.class).getId(), Product.class) == null) {
                unit = "";
            } else {
                Product product = (Product) (erpDao.queryById(writer.gson.fromJson(json, Product.class).getId(), Product.class));
                PurchaseDetail purchaseDetail = new PurchaseDetail();
                purchaseDetail.setProductNo(product.getNo());
                if (erpDao.query(purchaseDetail) != null && !erpDao.query(purchaseDetail).isEmpty()) {
                    purchaseDetail = (PurchaseDetail) (erpDao.query(purchaseDetail).get(0));
                    unit = purchaseDetail.getUnit();
                } else {
                    unit = "";
                }
            }
            String js = "{\"unit\":\"" + unit + "\"}";
            writer.writeStringToJson(response, js);
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
            List<Product> products = erpDao.complexQuery(Product.class, writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType()), 0, 30);
            for (Product dbProduct : products) {
                dbProduct.setDescribe((ProductDescribe) erpDao.queryById(dbProduct.getDescribe().getId(), ProductDescribe.class));
                dbProduct.setType((ProductType) erpDao.queryById(dbProduct.getType().getId(), ProductType.class));
            }
            writer.writeObjectToJson(response, products);

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

        } else if (entity.equalsIgnoreCase(ProductPriceChange.class.getSimpleName())) {
            ProductPriceChange priceChange = writer.gson.fromJson(json, ProductPriceChange.class);
            Set<Field> limitFields = new HashSet<>();

            try {
                if (priceChange.getState() != null) {
                    limitFields.add(priceChange.getClass().getDeclaredField("state"));
                }

                if (priceChange.getProduct() != null) {
                    limitFields.add(priceChange.getClass().getDeclaredField("productNo"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<ProductPriceChange> priceChanges = erpDao.suggest(priceChange, limitFields.toArray(new Field[limitFields.size()]));
            Product queryProduct = new Product();

            for (ProductPriceChange ele : priceChanges) {
                queryProduct.setNo(ele.getProductNo());
                queryProduct.setFatePrice(ele.getPrePrice());
                ele.setProduct((Product) erpDao.query(queryProduct).get(0));
            }

            writer.writeObjectToJson(response, priceChanges);
        }

        logger.info("suggest end");
    }

    @RequestMapping(value = "/entitiesSuggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void entitiesSuggest(HttpServletResponse response, String targetEntities,  String entities, @RequestBody String json){
        logger.info("entitiesSuggest start, parameter:" + targetEntities + "," + entities + "," + json);

        String result = "[";

        String[] targetEntitiesArr = targetEntities.split("#");
        String[] entitiesArr = entities.split("#");

        Map<String, Object> queryEntities = writer.gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());

        for (int i = 0; i < entitiesArr.length; i++) {
            String partResult = erpService.queryTargetEntity(targetEntitiesArr[i], entitiesArr[i], queryEntities.get(entitiesArr[i]), 0, 30);

            if (partResult != null) {
                if (!partResult.equals("[]") && !partResult.trim().equals("")) {
                    result += partResult.substring(1, partResult.length()-1) + ",";
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
            queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
        } catch (Exception e){
            e.getMessage();
        }

        if (entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.complexQuery(Purchase.class, queryParameters, position, rowNum));

        } else if (entity.equalsIgnoreCase(Supplier.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.complexQuery(Supplier.class, queryParameters, position, rowNum));

        } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpService.privateQuery(entity, json, position, rowNum));

        } else if (entity.equalsIgnoreCase(ProductDescribe.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpService.privateQuery(entity, json, position, rowNum));

        } else if (entity.equalsIgnoreCase(ProductType.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.complexQuery(ProductType.class, queryParameters, position, rowNum));

        } else if (entity.equalsIgnoreCase(ProductProperty.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.complexQuery(ProductProperty.class, queryParameters, position, rowNum));

        } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
            Map<Integer, String> uniqueActionCodes = new HashMap();
            List<Action> repeatActions = new ArrayList<>();
            List<StockInOut> stockInOuts = erpDao.complexQuery(StockInOut.class, queryParameters, position, rowNum);

            Action queryAction = new Action();
            for (StockInOut stockInOut : stockInOuts) {
                uniqueActionCodes.put(stockInOut.getId(), "");

                queryAction.setEntity(ErpConstant.stockInOut);
                queryAction.setEntityId(stockInOut.getId());
                List<Action> actions = erpDao.query(queryAction);
                stockInOut.setActions(new HashSet<>(actions));

                for (Action action : stockInOut.getActions()) {
                    if (uniqueActionCodes.get(stockInOut.getId()).contains("," + action.getType() + ",")) {
                        repeatActions.add(action);

                    } else {
                        String uniqueActionCode = uniqueActionCodes.get(stockInOut.getId());
                        uniqueActionCode += "," + action.getType() + ",";
                        uniqueActionCodes.put(stockInOut.getId(), uniqueActionCode);
                    }
                }
            }

            for (StockInOut stockInOut : stockInOuts) {
                for (Action action : repeatActions) {
                    if (stockInOut.getActions().contains(action)) {
                        stockInOut.getActions().remove(action);
                    }
                }
            }

            writer.writeObjectToJson(response, stockInOuts);

        } else if (entity.equalsIgnoreCase(Stock.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpService.privateQuery(entity, json, position, rowNum));

        } else if (entity.equalsIgnoreCase(Warehouse.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.complexQuery(Warehouse.class, queryParameters, position, rowNum));

        } else if (entity.equalsIgnoreCase(ProductPriceChange.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpDao.complexQuery(ProductPriceChange.class, queryParameters, position, rowNum));

        }else if (entity.equalsIgnoreCase(ProductCheck.class.getSimpleName())) {
            writer.writeObjectToJson(response, erpService.privateQuery(entity, json, position, rowNum));
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
            recordsSum = erpService.privateRecordNum(entity, json);

        } else if (entity.equalsIgnoreCase(ProductDescribe.class.getSimpleName())) {
            recordsSum =  erpService.privateRecordNum(entity, json);

        } else if (entity.equalsIgnoreCase(ProductType.class.getSimpleName())) {
            recordsSum =  erpDao.recordsSum(ProductType.class, queryParameters);

        } else if (entity.equalsIgnoreCase(ProductProperty.class.getSimpleName())) {
            recordsSum =  erpDao.recordsSum(ProductProperty.class, queryParameters);

        } else if (entity.equalsIgnoreCase(StockInOut.class.getSimpleName())) {
            recordsSum =  erpDao.recordsSum(StockInOut.class, queryParameters);

        } else if (entity.equalsIgnoreCase(Stock.class.getSimpleName())) {
            recordsSum = erpService.privateRecordNum(entity, json);

        } else if (entity.equalsIgnoreCase(Warehouse.class.getSimpleName())) {
            recordsSum =  erpDao.recordsSum(Warehouse.class, queryParameters);

        } else if (entity.equalsIgnoreCase(ProductCheck.class.getSimpleName())) {
            recordsSum = erpService.privateRecordNum(entity, json);

        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.recordsSum + "\":" + recordsSum + "}");

        logger.info("recordsSum end");
    }

    @RequestMapping(value = "/getNo", method = {RequestMethod.GET, RequestMethod.POST})
    public void getNo(HttpServletResponse response, String prefix){
        logger.info("getNo start, parameter:" + prefix);
        writer.writeStringToJson(response, "{\"" + ErpConstant.no + "\":\"" + erpDao.getNo(prefix) + "\"}");
        logger.info("getNo start, end");
    }

    @RequestMapping(value = "/getSimpleNo", method = {RequestMethod.GET, RequestMethod.POST})
    public void getSimpleNo(HttpServletResponse response, Integer length){
        logger.info("getSimpleNo start, parameter:" + length);
        writer.writeStringToJson(response, "{\"" + ErpConstant.no + "\":\"" + strUtil.generateRandomStr(length) + "\"}");
        logger.info("getSimpleNo start, end");
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

        if (priceChange.getId() != null) {
            salePrice = ((List<ProductPriceChange>)erpDao.query(priceChange)).get(0).getPrice();
        } else {
            priceChange.getProduct().setState(ErpConstant.product_state_onSale);
            salePrice = ((Product)erpDao.query(priceChange.getProduct()).get(0)).getFatePrice();
        }

        writer.writeStringToJson(response, "{\"" + ErpConstant.price +"\":" + salePrice + "}");

        logger.info("querySalePrice start, end");
    }

    /**
     * 商品属性是否重复
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
        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + isRepeat + "\"}");

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
                    result += erpService.purchaseProductsStateModify(audit, ErpConstant.product_state_purchase_pass);
                    break;

                case AuditFlowConstant.action_product_modify:
                    result += erpService.purchaseProductsStateModify(audit, ErpConstant.product_state_purchase);
                    break;

                case AuditFlowConstant.action_purchase_close:
                    result += erpService.purchaseStateModify(audit, ErpConstant.purchase_state_close, ErpConstant.product_state_purchase_close);
                    break;

                case AuditFlowConstant.action_purchase_modify:
                    result += erpService.purchaseStateModify(audit, ErpConstant.purchase_state_apply, ErpConstant.product_state_purchase);
                    break;

                case AuditFlowConstant.action_purchase_emergency_pass:
                    result += erpService.purchaseEmergencyPass(audit, ErpConstant.purchase_state_close, ErpConstant.product_state_purchase_close);
                    break;

                case AuditFlowConstant.action_purchase_emergency_pay:
                    result += erpService.purchaseEmergencyPay(audit);
                    break;

                case AuditFlowConstant.action_stockIn_return_deposit:
                    result += erpService.stockInReturnDeposit(audit);
                    break;

                case AuditFlowConstant.action_product_files_upload:{
                    Product statepProduct = (Product) erpDao.queryById(audit.getEntityId(), Product.class);
                    if (statepProduct.getState().compareTo(ErpConstant.product_state_mediaFiles_uploaded) == 0 ||
                            statepProduct.getState().compareTo(ErpConstant.product_state_edit) == 0) {
                        result += erpService.productStateModify(audit, ErpConstant.product_state_stockIn);

                    } else {
                        result += CommonConstant.fail + ",商品状态不为已上传完多媒体文件状态或编辑状态，不能打回给摄影上传文件";
                    }
                }
                break;

                case AuditFlowConstant.action_product_stockIn_modify:{
                    Product statepProduct = (Product) erpDao.queryById(audit.getEntityId(), Product.class);
                    if (statepProduct.getState().compareTo(ErpConstant.product_state_mediaFiles_uploaded) == 0 ||
                            statepProduct.getState().compareTo(ErpConstant.product_state_edit) == 0) {
                        result += erpService.productStateModify(audit, ErpConstant.product_state_edit);
                    } else {
                        result += CommonConstant.fail + ",商品状态不为已上传完多媒体文件状态或编辑状态，不能打回给入库员编辑商品信息";
                    }
                }
                break;

                case AuditFlowConstant.action_onSale:{
                    Product statepProduct = (Product) erpDao.queryById(audit.getEntityId(), Product.class);
                    if (statepProduct.getState().compareTo(ErpConstant.product_state_mediaFiles_uploaded) == 0 ||
                            statepProduct.getState().compareTo(ErpConstant.product_state_edit) == 0) {
                        result += erpService.productStateModify(audit, ErpConstant.product_state_onSale);
                    } else {
                        result += CommonConstant.fail + ",商品状态不为已上传完多媒体文件状态或编辑状态，不能上架";
                    }
                }
                break;

                case AuditFlowConstant.action_price_change_set_state_use:{
                    ProductPriceChange priceChange = new ProductPriceChange();
                    priceChange.setId(audit.getEntityId());
                    priceChange.setState(ErpConstant.product_price_change_state_use);
                    priceChange.setDate(dateUtil.getSecondCurrentTimestamp());

                    result += erpDao.updateById(priceChange.getId(), priceChange);
                }
                break;

                case AuditFlowConstant.action_price_change_modify:{
                    ProductPriceChange priceChange = new ProductPriceChange();
                    priceChange.setId(audit.getEntityId());
                    priceChange.setState(ErpConstant.product_price_change_state_save);

                    result += erpDao.updateById(priceChange.getId(), priceChange);
                }
                break;

                case AuditFlowConstant.action_stockOut_product_breakage:{
                    StockInOut stockInOut = new StockInOut();
                    stockInOut.setId(audit.getEntityId());

                    result += erpService.stockOut((StockInOut) erpDao.queryById(stockInOut.getId(), stockInOut.getClass()));
                }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("auditAction end");
    }

    /**
     * 根据公司获取仓库
     */
    @PostMapping(value="/getWarehouseByCompany")
    public void getWarehouseByCompany(HttpServletResponse response,  @RequestBody String json) {
        logger.info("getWarehouseByCompany start, parameter:" + json);

        Warehouse warehouse = new Warehouse();
        warehouse.setCompany(writer.gson.fromJson(json, Company.class));
        writer.writeObjectToJson(response, erpDao.query(warehouse));

        logger.info("getWarehouseByCompany end");
    }

    @PostMapping(value = "/getLastStockInOutByProductAndType")
    public void getLastStockInOutByProductAndType(HttpServletResponse response,  @RequestBody String json, String type){
        writer.writeObjectToJson(response, erpService.getLastStockInOutByProductAndType(writer.gson.fromJson(json, Product.class), type));
    }

    @GetMapping(value = "/getExpressNo")
    public void getExpressNo(HttpServletResponse response){
        writer.writeStringToJson(response, "{\"" + CommonConstant.no + "\":\"" + ErpConstant.no_expressDelivery_perfix + erpDao.getSfTransMessageId() + "\"");
    }


    /**
     *
     *  ========================= 顺丰接口 =======================
     *  https://open.sf-express.com/apitools/sdk.html
     */

    /**
     * sf快递单下单接口
     * @param json
     * @param response
     */
    @PostMapping(value = "/sfExpressOrder")
    public void sfExpressOrder(String json, HttpServletResponse response) {
        logger.info("sfExpressOrder start, json:" + json);

        String result = CommonConstant.fail;

        try {
            result += expressDeliverOrder(writer.gson.fromJson(json, ExpressDeliver.class));
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result + "\"}");
        logger.info("sfExpressOrder end");
    }

    public String expressDeliverOrder(ExpressDeliver expressDeliver) {
        String result = CommonConstant.fail;

        result += erpDao.save(expressDeliver);

        for (ExpressDeliverDetail expressDeliverDetail : expressDeliver.getDetails()) {
            expressDeliverDetail.setExpressDeliver(expressDeliver);
            result += erpDao.save(expressDeliverDetail);

            for (ExpressDeliverDetailProduct expressDeliverDetailProduct : expressDeliverDetail.getExpressDeliverDetailProducts()) {
                expressDeliverDetailProduct.setExpressDeliverDetail(expressDeliverDetail);
                result += erpDao.save(expressDeliverDetailProduct);
            }
        }

        result += sfExpressOrder(expressDeliver);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 调用顺丰快递单接口，在顺丰系统生成快递单
     * @param expressDeliver
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
    public String sfExpressOrder(ExpressDeliver expressDeliver) {
        logger.info("sfExpressOrder start, details:" + expressDeliver.toString());

        String result = CommonConstant.fail;

        //设置 uri
        String url = httpProxyDiscovery.getHttpProxyAddress() + sfExpress.getOrderUri();
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId(sfExpress.getAppId());
        appInfo.setAppKey(sfExpress.getAppKey());
        appInfo.setAccessToken(getSfToken(sfExpress.getAppId(), sfExpress.getAppKey()));

        for (ExpressDeliverDetail expressDeliverDetail : expressDeliver.getDetails()) {
            //设置请求头
            MessageReq req = new MessageReq();
            HeadMessageReq head = new HeadMessageReq();
            head.setTransType(ErpConstant.sf_action_code_order);
            head.setTransMessageId(expressDeliverDetail.getExpressNo().replace(ErpConstant.no_expressDelivery_perfix, ""));
            req.setHead(head);

            OrderReqDto orderReqDto = new OrderReqDto();
            orderReqDto.setOrderId(expressDeliverDetail.getExpressNo());
            orderReqDto.setExpressType((short) 5);
            orderReqDto.setPayMethod((short) 1);
            orderReqDto.setNeedReturnTrackingNo((short) 0);
            orderReqDto.setIsDoCall((short) 1);
            orderReqDto.setIsGenBillNo((short) 1);
            orderReqDto.setCustId(sfExpress.getCustId());
            orderReqDto.setPayArea(sfExpress.getPayArea());
            orderReqDto.setSendStartTime(dateUtil.getSimpleDateFormat().format(expressDeliver.getDate()));
            orderReqDto.setRemark("易碎物品，小心轻放");

            //收件人信息
            DeliverConsigneeInfoDto consigneeInfoDto = new DeliverConsigneeInfoDto();
            consigneeInfoDto.setCompany(expressDeliver.getReceiverCompany());
            consigneeInfoDto.setAddress(expressDeliver.getReceiverAddress());
            consigneeInfoDto.setCity(expressDeliver.getReceiverCity());
            consigneeInfoDto.setProvince(expressDeliver.getReceiverProvince());
            consigneeInfoDto.setCountry(expressDeliver.getReceiverCountry());
            consigneeInfoDto.setShipperCode(expressDeliver.getReceiverPostCode());
            consigneeInfoDto.setMobile(expressDeliver.getReceiverMobile());
            consigneeInfoDto.setTel(expressDeliver.getReceiverTel());
            consigneeInfoDto.setContact(expressDeliver.getReceiver());

            //寄件人信息
            DeliverConsigneeInfoDto deliverInfoDto = new DeliverConsigneeInfoDto();
            deliverInfoDto.setCompany(expressDeliver.getSenderCompany());
            deliverInfoDto.setAddress(expressDeliver.getSenderAddress());
            deliverInfoDto.setCity(expressDeliver.getSenderCity());
            deliverInfoDto.setProvince(expressDeliver.getSenderProvince());
            deliverInfoDto.setCountry(expressDeliver.getSenderCountry());
            deliverInfoDto.setShipperCode(expressDeliver.getSenderPostCode());
            deliverInfoDto.setMobile(expressDeliver.getSenderMobile());
            deliverInfoDto.setTel(expressDeliver.getSenderTel());
            deliverInfoDto.setContact(expressDeliver.getSender());

            //货物信息
            CargoInfoDto cargoInfoDto = new CargoInfoDto();
            cargoInfoDto.setParcelQuantity(Integer.valueOf(1));
            cargoInfoDto.setCargo(expressDeliverDetail.getProductNo());
            cargoInfoDto.setCargoCount(Float.toString(expressDeliverDetail.getQuantity()));
            cargoInfoDto.setCargoUnit(expressDeliverDetail.getUnit());
            cargoInfoDto.setCargoAmount(Float.toString(expressDeliverDetail.getPrice()));

            orderReqDto.setDeliverInfo(deliverInfoDto);
            orderReqDto.setConsigneeInfo(consigneeInfoDto);
            orderReqDto.setCargoInfo(cargoInfoDto);
            req.setBody(orderReqDto);

            System.out.println("传入参数" + ToStringBuilder.reflectionToString(req));
            MessageResp messageResp = null;
            try {
                messageResp = OrderTools.order(url, appInfo, req);
            } catch (Exception e) {
                e.printStackTrace();
                result += CommonConstant.fail;
            }

            if (messageResp.getHead().getCode().equals(ErpConstant.sf_response_code_success)) {
                result += CommonConstant.success;
            } else {
                result += CommonConstant.fail;
            }
        }

        logger.info("sfExpressOrder end");
        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 下载顺丰快递单
     * @param stockInOut
     * @return
     */
    public String downloadSfWaybill(StockInOut stockInOut) {
        logger.info("downloadSfWaybill start:" + stockInOut.getId());

        String sfWaybillImages = "";

        String url = httpProxyDiscovery.getHttpProxyAddress() + sfExpress.getOrderUri();
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId(sfExpress.getAppId());
        appInfo.setAppKey(sfExpress.getAppKey());
        appInfo.setAccessToken(getSfToken(sfExpress.getAppId(), sfExpress.getAppKey()));

        for (StockInOutDetail detail : stockInOut.getDetails()) {
            StockInOutDetail dbDetail = (StockInOutDetail) erpDao.queryById(detail.getId(), detail.getClass());
            for (StockInOutDetailProduct detailProduct : dbDetail.getStockInOutDetailProducts()) {

                Product dbProduct = (Product) erpDao.queryById(detailProduct.getProduct().getId(), detailProduct.getProduct().getClass());
                ExpressDeliverDetail dbExpressDeliverDetail = erpService.queryLastUnReceiveExpressDeliverDetailByProductNo(dbProduct.getNo());

                //设置请求头
                MessageReq<WaybillReqDto> req = new MessageReq<>();
                HeadMessageReq head = new HeadMessageReq();
                head.setTransType(ErpConstant.sf_action_code_download_waybill);
                head.setTransMessageId(erpDao.getSfTransMessageId());
                req.setHead(head);

                WaybillReqDto waybillReqDto = new WaybillReqDto();
                waybillReqDto.setOrderId(dbExpressDeliverDetail.getExpressNo());
                req.setBody(waybillReqDto);

                try {
                    MessageResp<WaybillRespDto> messageResp = WaybillDownloadTools.waybillDownload(url, appInfo, req);

                    if (messageResp.getHead().getCode().equals(ErpConstant.sf_response_code_success)) {
                        String[] images = messageResp.getBody().getImages();

                        for (String image : images) {
                            sfWaybillImages += "<img src='data:image/png;base64," + image + "'/><br/><br/>";
                        }
                    } else {
                        sfWaybillImages = "下载顺丰快递单失败," + messageResp.getHead().getMessage();
                    }

                    logger.info(messageResp.getHead().getCode() + "," + messageResp.getHead().getMessage());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        logger.info("downloadSfWaybill end");

        return sfWaybillImages;
    }

    /**
     * 获取顺丰 token
     * @param appId
     * @param appKey
     * @return
     */
    public String getSfToken(String appId, String appKey) {
        logger.info("getSfToken start");

        String token = (String) erpDao.getFromRedis(ErpConstant.sf_access_token_key);

        if (token != null) {
            refreshSfTokens((String) erpDao.getFromRedis(ErpConstant.sf_access_token_key),
                   (String) erpDao.getFromRedis(ErpConstant.sf_refresh_token_key));
        } else {
            setSfTokens();
        }

        logger.info("getSfToken end");

        return (String) erpDao.getFromRedis(ErpConstant.sf_access_token_key);
    }

    public void setSfTokens() {
        logger.info("setSfTokens start");

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

            logger.info(messageResp.getHead().getCode() + "," + messageResp.getHead().getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("setSfTokens end");
    }

    public void refreshSfTokens(String accessToken, String refreshToken) {
        logger.info("refreshSfTokens start:" + accessToken + "," + refreshToken);

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

            logger.info(messageResp.getHead().getCode() + "," + messageResp.getHead().getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("refreshSfTokens end");
    }
}
