package com.hzg.erp;

import com.google.gson.reflect.TypeToken;
import com.hzg.pay.Pay;
import com.hzg.sys.*;
import com.hzg.tools.*;
import com.sf.openapi.common.entity.MessageResp;
import com.sf.openapi.express.sample.order.dto.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.math.BigDecimal;
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
    private  StrUtil strUtil;

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
                result += erpService.savePurchase(purchase);

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

                String msg = erpService.isCanSaveStockInOut(stockInOut);
                if (msg.equals("")) {
                    result += erpService.saveStockInOut(stockInOut);
                } else {
                    result += msg;
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
                result += erpService.updatePurchase(writer.gson.fromJson(json, Purchase.class));

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
                        ele.setState(ErpConstant.product_state_edit);
                        result += erpDao.updateById(ele.getId(), ele);
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
            if (name.equalsIgnoreCase(ErpConstant.product_action_name_updateUploadMediaFilesInfo)) {
                Map<String, Object> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());

                Product product = writer.gson.fromJson(writer.gson.toJson(queryParameters.get("product")), Product.class);
                product.setState(ErpConstant.product_state_mediaFiles_uploaded);

                result += erpDao.updateById(product.getId(), product);
                result += erpDao.updateById(product.getDescribe().getId(), product.getDescribe());

                if (erpService.queryProductAudit(product).isEmpty()) {
                    /**
                     * 上传完商品多媒体图片后，发起商品上架审核流程
                     */
                    result += erpService.launchAuditFlowByPost(AuditFlowConstant.business_product, product.getId(), product.getNo(),
                            "编辑、审核及上架编号为:" + product.getNo() + "的商品",
                            writer.gson.fromJson(writer.gson.toJson(queryParameters.get("post")), Post.class),
                            erpService.queryProductOnSalePreFlowAuditNo(product));
                }

            } else if (name.equalsIgnoreCase(ErpConstant.product_action_name_setProductsSold)) {
                List<Product> products = writer.gson.fromJson(json, new TypeToken<List<Product>>(){}.getType());
                for (Product product : products) {
                    product.setState(erpService.getProductSaleState(product));
                    result += erpDao.updateById(product.getId(), product);
                }

            } else if (name.equalsIgnoreCase(ErpConstant.product_action_name_setProductsOnReturn)) {
                List<Product> products = writer.gson.fromJson(json, new TypeToken<List<Product>>(){}.getType());
                for (Product product : products) {
                    product.setState(erpService.getProductOnReturnState(product));
                    result += erpDao.updateById(product.getId(), product);
                }

            } else if (name.equalsIgnoreCase(ErpConstant.product_action_name_setProductsReturned)) {
                List<Product> products = writer.gson.fromJson(json, new TypeToken<List<Product>>(){}.getType());
                for (Product product : products) {
                    product.setState(erpService.getProductReturnedState(product));
                    result += erpDao.updateById(product.getId(), product);
                }

            } else if (name.equalsIgnoreCase(ErpConstant.stockInOut_action_name_inProduct)) {
                Action action = writer.gson.fromJson(json, Action.class);
                StockInOut stockIn = erpService.queryStockInOut(action.getEntityId());
                stockIn.setState(ErpConstant.stockInOut_state_finished);

                /**
                 * 入库，设置库存
                 */
                 result += erpService.stockIn(stockIn);

                action.setEntity(ErpConstant.stockInOut);
                action.setType(ErpConstant.stockInOut_action_inProduct);
                action.setInputer(erpService.getUserBySessionId(action.getSessionId()));
                action.setInputDate(dateUtil.getSecondCurrentTimestamp());
                result += erpDao.save(action);

                result = "{\"" + CommonConstant.result + "\":\"" + transcation.dealResult(result) + "\"" +
                        ",\"" + CommonConstant.id + "\":" + stockIn.getId() +
                        ",\"" + CommonConstant.type + "\":" + stockIn.getType() +
                        ",\"" + CommonConstant.state + "\":" + stockIn.getState() + "}";
                writer.writeStringToJson(response, result);
                logger.info("business end, result:" + result);
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
                logger.info("business end, result:" + result);
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

            } else if (name.equalsIgnoreCase(ErpConstant.product_action_name_generateSfExpressOrderByReceiverAndStockOut)) {
                Action action = writer.gson.fromJson(json, Action.class);
                StockInOut stockInOut = (StockInOut) erpDao.queryById(action.getEntityId(), StockInOut.class);

                result += erpService.generateSfExpressOrderByReceiverAndStockOut(writer.gson.fromJson((
                        (Map<String, Object>) writer.gson.fromJson(json, new com.google.gson.reflect.TypeToken<Map<String, Object>>() {}.getType())).
                        get(CommonConstant.expressReceiverInfo).toString(), ExpressDeliver.class), stockInOut);

                action.setEntity(ErpConstant.stockInOut);
                action.setType(ErpConstant.product_action_upShelf);
                action.setInputer(erpService.getUserBySessionId(action.getSessionId()));
                action.setInputDate(dateUtil.getSecondCurrentTimestamp());
                result += erpDao.save(action);

            } else if (name.equalsIgnoreCase(ErpConstant.purchase_action_name_purchaseBookPaid)) {
                Action action = writer.gson.fromJson(json, Action.class);
                Purchase purchase = (Purchase) erpDao.queryById(action.getEntityId(), Purchase.class);
                purchase.setPays(erpService.getPaysByEntity(purchase.getClass().getSimpleName().toLowerCase(), purchase.getId()));
                result += erpService.paidOfflinePurchase(purchase);

                action.setEntity(ErpConstant.purchase);
                action.setType(ErpConstant.purchase_action_purchaseBookPaid);
                action.setInputer(erpService.getUserBySessionId(action.getSessionId()));
                action.setInputDate(dateUtil.getSecondCurrentTimestamp());
                result += erpDao.save(action);

            } else if (name.equalsIgnoreCase(ErpConstant.product_action_name_recoverState)) {
                List<Product> products = writer.gson.fromJson(json, new TypeToken<List<Product>>(){}.getType());
                for (Product product : products) {
                    product.setState(erpService.getProductPreState(product));
                    result += erpDao.updateById(product.getId(), product);
                }

            } else if (name.equalsIgnoreCase(ErpConstant.product_action_name_setProductEdit)) {
                List<Product> products = writer.gson.fromJson(json, new TypeToken<List<Product>>(){}.getType());
                for (Product product : products) {
                    product.setState(ErpConstant.product_state_edit);
                    result += erpDao.updateById(product.getId(), product);
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

        String result = CommonConstant.fail;
        String printContent = "";

        try {
            Action action = writer.gson.fromJson(json, Action.class);
            StockInOut stockInOut = (StockInOut) erpDao.queryById(action.getEntityId(), StockInOut.class);

            if (name.equalsIgnoreCase(ErpConstant.stockInOut_action_name_print_barcode)) {
                printContent = erpService.generateBarcodes(stockInOut);
                action.setType(ErpConstant.stockInOut_action_print_barcode);

            } else if (name.equalsIgnoreCase(ErpConstant.stockInOut_action_name_print_stockOutBills)) {
                printContent = ((Map<String, Object>) writer.gson.fromJson(json, new TypeToken<Map<String, Object>>() {
                }.getType())).get(CommonConstant.printContent).toString();
                action.setType(ErpConstant.stockInOut_action_print_stockOutBills);

            } else if (name.equalsIgnoreCase(ErpConstant.stockInOut_action_name_print_expressWaybill)) {
                List<ExpressDeliverDetail> details = erpService.getSfExpressWayBillsByStockInOut(stockInOut);
                printContent = writer.gson.toJson(details);
                action.setType(ErpConstant.stockInOut_action_print_expressWaybill);

                for (ExpressDeliverDetail detail : details) {
                    detail.setState(ErpConstant.express_detail_state_sended);
                    result += erpDao.updateById(detail.getId(), detail);
                }
                /**
                 * 打印快递单即表示商品已发货
                 */
                result += erpService.setProductShipped(stockInOut);
            }

            action.setEntity(ErpConstant.stockInOut);
            action.setInputer(erpService.getUserBySessionId(action.getSessionId()));
            action.setInputDate(dateUtil.getSecondCurrentTimestamp());
            result += erpDao.save(action);

        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

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
                    if (dbPurchase.getType().compareTo(ErpConstant.purchase_type_emergency) == 0 ||
                            dbPurchase.getType().compareTo(ErpConstant.purchase_type_cash) == 0 ||
                            dbPurchase.getType().compareTo(ErpConstant.purchase_type_deposit) == 0) {
                        auditEntity = AuditFlowConstant.business_purchaseEmergency;
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
                    if (dbPurchase.getType().compareTo(ErpConstant.purchase_type_emergency) == 0 ||
                            dbPurchase.getType().compareTo(ErpConstant.purchase_type_cash) == 0 ||
                            dbPurchase.getType().compareTo(ErpConstant.purchase_type_deposit) == 0) {
                        auditEntity = AuditFlowConstant.business_purchaseEmergency;
                    }

                    result += erpService.launchAuditFlow(auditEntity, dbPurchase.getId(), dbPurchase.getNo(), dbPurchase.getName(),
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

                if (purchase.getType().compareTo(ErpConstant.purchase_type_temp) == 0) {
                    PurchaseBook queryPurchaseBook = new PurchaseBook();
                    queryPurchaseBook.setPurchase(new Purchase(purchase.getId()));
                    List<PurchaseBook> purchaseBooks = erpDao.query(queryPurchaseBook);

                    if (!purchaseBooks.isEmpty()) {
                        PurchaseBook purchaseBook = purchaseBooks.get(0);
                        purchase.setPurchaseBook(purchaseBook);

                        purchase.setPurchaseBookPaid(false);
                        if (purchaseBook.getState().compareTo(ErpConstant.purchase_type_temp_deposit_paid) == 0) {
                            purchase.setPurchaseBookPaid(true);
                        }
                    }
                }

                purchase.setPays(erpService.getPaysByEntity(purchase.getClass().getSimpleName().toLowerCase(), purchase.getId()));
                purchase.setToPayBalance(false);
                for (Pay pay : purchase.getPays()) {
                    if (pay.getState().compareTo(PayConstants.pay_state_apply) == 0) {
                        purchase.setToPayBalance(true);
                        break;
                    }
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
                    result += partResult.substring(1, partResult.length() - 1) + ",";
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
        writer.writeStringToJson(response, "{\"" + CommonConstant.no + "\":\"" + erpDao.getNo(prefix) + "\"}");
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
            stockQuantity += new BigDecimal(Float.toString(stockQuantity)).add(new BigDecimal(Float.toString(((Stock)temp).getQuantity()))).floatValue();
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

                case AuditFlowConstant.action_purchase_close: {
                    result += erpService.purchasePay(audit);
                    result += erpService.purchaseStateModify(audit, ErpConstant.purchase_state_close, ErpConstant.product_state_purchase_close);
                    break;
                }

                case AuditFlowConstant.action_purchase_modify:
                    result += erpService.purchaseStateModify(audit, ErpConstant.purchase_state_apply, ErpConstant.product_state_purchase);
                    break;

                case AuditFlowConstant.action_purchase_emergency_pass:
                    result += erpService.purchaseEmergencyPass(audit, ErpConstant.purchase_state_close, ErpConstant.product_state_purchase_close);
                    break;

                case AuditFlowConstant.action_purchase_emergency_pay:
                    result += erpService.purchasePay(audit);
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
                    Product stateProduct = (Product) erpDao.queryById(audit.getEntityId(), Product.class);
                    if (stateProduct.getState().compareTo(ErpConstant.product_state_mediaFiles_uploaded) == 0 ||
                            stateProduct.getState().compareTo(ErpConstant.product_state_edit) == 0) {
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
        writer.writeObjectToJson(response, erpDao.query(warehouse).get(0));

        logger.info("getWarehouseByCompany end");
    }

    @PostMapping(value = "/getLastStockInOutByProductAndType")
    public void getLastStockInOutByProductAndType(HttpServletResponse response,  @RequestBody String json, String type){
        logger.info("getLastStockInOutByProductAndType start, parameter:" + json + "," + type);
        writer.writeObjectToJson(response, erpService.getLastStockInOutByProductAndType(writer.gson.fromJson(json, Product.class), type));
        logger.info("getLastStockInOutByProductAndType end");
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
    @Transactional
    @PostMapping(value = "/sfExpress/order")
    public void sfExpressOrder( HttpServletResponse response, @RequestBody String json) {
        logger.info("sfExpressOrder start, json:" + json);

        String result = CommonConstant.fail;

        try {
            List<ExpressDeliverDetail> details = erpService.expressDeliverOrders(writer.gson.fromJson(json, new TypeToken<List<ExpressDeliver>>(){}.getType()));
            if (!details.isEmpty()) {
                result += CommonConstant.success;
            } else {
                result += CommonConstant.fail;
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
     * 查询快递单情况
     * @param expressNos
     * @param response
     */
    @GetMapping(value = "/sfExpress/order/query/{expressNos}")
    public void sfExpressOrderQuery(HttpServletResponse response, @RequestBody String expressNos) {
        logger.info("sfExpressOrderQuery start, expressNos:" + expressNos);
        String[] expressNosArr = expressNos.split(",");
        List<ExpressDeliverDetail> details = new ArrayList<>();

        for (String expressNo : expressNosArr) {
            ExpressDeliverDetail detail = new ExpressDeliverDetail();
            detail.setExpressNo(expressNo);
            details.add(erpService.sfBspOrderQuery(detail));
        }

        writer.writeObjectToJson(response, details);
        logger.info("sfExpressOrderQuery end");
    }

    /**
     * 查询快递单情况
     * @param expressNos
     * @param response
     */
    @GetMapping(value = "/sfExpress/order/cancel/{expressNos}")
    public void sfExpressOrderCancel(HttpServletResponse response, @PathVariable("expressNos") String expressNos) {
        logger.info("sfExpressOrderQuery start, expressNos:" + expressNos);
        String[] expressNosArr = expressNos.split(",");
        List<String> cancelExpressNos = new ArrayList<>();

        for (String expressNo : expressNosArr) {
            ExpressDeliverDetail detail = new ExpressDeliverDetail();
            detail.setExpressNo(expressNo);

            if (erpService.sfBspOrderCancel(detail).equals(CommonConstant.success)) {
                cancelExpressNos.add(expressNo);
            }
        }

        writer.writeObjectToJson(response, cancelExpressNos);
        logger.info("sfExpressOrderQuery end");
    }

    /**
     * 产生顺丰快递单
     * @param expressNos
     * @param model
     */
    @GetMapping(value = "/sfExpress/waybill/{expressNos}")
    public String sfExpressWaybill(@PathVariable("expressNos") String expressNos, Map<String, Object> model) {
        logger.info("sfExpressWaybillDownload start, expressNo:" + expressNos);

        String[] expressNosArr = expressNos.split(",");
        List<ExpressDeliverDetail> details = new ArrayList<>();

        for (String expressNo : expressNosArr) {
            ExpressDeliverDetail detail = new ExpressDeliverDetail();
            detail.setExpressNo(expressNo);
            List<ExpressDeliverDetail> dbDetails = erpDao.query(detail);

            if (!dbDetails.isEmpty()) {
                ExpressDeliverDetail dbDetail = erpService.getSfWaybillInfo(dbDetails.get(0));
                if (!dbDetail.getResult().equals("3")) {
                    details.add(dbDetail);
                }
            }
        }

        model.put("details", details);
        logger.info("sfExpressWaybillDownload end");
        return "waybill";
    }

    /**
     * sf快递单下单及产生快递单
     * @param json
     * @param model
     */
    @Transactional
    @PostMapping(value = "/sfExpress/sfExpressOrderAndWaybill")
    public String sfExpressOrderAndWaybill(String json, Map<String, Object> model) {
        logger.info("sfExpressOrderAndWaybill start, json:" + json);

        List<ExpressDeliverDetail> details = new ArrayList<>();
        try {
            List<ExpressDeliver> expressDelivers = writer.gson.fromJson(json, new TypeToken<List<ExpressDeliver>>(){}.getType());
            details = erpService.expressDeliverOrders(expressDelivers);

            for (ExpressDeliverDetail detail : details) {
                erpService.setWaybillinfo(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            transcation.rollback();
        }

        model.put("details", details);
        logger.info("sfExpressWaybillDownload end");
        return "waybill";
    }

    /**
     * sf快递单订单结果通知
     * @param json
     * @param response
     */
    @Transactional
    @PostMapping(value = "/sfExpress/order/notify")
    public void sfExpressOrderNotify(@RequestBody String json, HttpServletResponse response) {
        logger.info("sfExpressOrderNotify start, json:" + json);

        String result = CommonConstant.fail;

        MessageResp<OrderNotifyReqDto> messageResp = writer.gson.fromJson(json, new TypeToken<MessageResp<OrderNotifyReqDto>>(){}.getType());
        if (messageResp.getHead().getCode().equals(ErpConstant.sf_response_code_success) && messageResp.getBody().getMailNo() != null) {
            ExpressDeliverDetail expressDeliverDetail = new ExpressDeliverDetail();
            expressDeliverDetail.setExpressNo(messageResp.getBody().getOrderId());

            ExpressDeliverDetail dbExpressDeliverDetail = (ExpressDeliverDetail) erpDao.query(expressDeliverDetail).get(0);
            expressDeliverDetail.setId(dbExpressDeliverDetail.getId());
            expressDeliverDetail.setMailNo(messageResp.getBody().getMailNo());
            result = erpDao.updateById(expressDeliverDetail.getId(), expressDeliverDetail);
        }

        if (!result.contains(CommonConstant.fail)) {
            MessageResp<OrderNotifyRespDto> resp = new MessageResp<>();
            resp.getHead().setTransType(4201);
            resp.getHead().setTransMessageId(erpDao.getSfTransMessageId());
            resp.getBody().setOrderId(messageResp.getBody().getOrderId());

            writer.writeStringToJson(response, writer.gson.toJson(resp));
        } else {
            writer.writeStringToJson(response, result);
        }

        logger.info("sfExpressOrderNotify end");
    }
}
