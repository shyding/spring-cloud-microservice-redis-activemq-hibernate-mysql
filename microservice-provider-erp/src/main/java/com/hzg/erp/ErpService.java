package com.hzg.erp;

import com.google.common.reflect.TypeToken;
import com.hzg.afterSaleService.ReturnProduct;
import com.hzg.order.Order;
import com.hzg.pay.Pay;
import com.hzg.sys.*;
import com.hzg.tools.*;
import com.sf.openapi.common.entity.AppInfo;
import com.sf.openapi.common.entity.HeadMessageReq;
import com.sf.openapi.common.entity.MessageReq;
import com.sf.openapi.common.entity.MessageResp;
import com.sf.openapi.express.sample.order.dto.*;
import com.sf.openapi.express.sample.order.tools.OrderTools;
import com.sf.openapi.express.sample.security.dto.TokenReqDto;
import com.sf.openapi.express.sample.security.dto.TokenRespDto;
import com.sf.openapi.express.sample.security.tools.SecurityTools;
import com.sf.openapi.express.sample.waybill.dto.WaybillReqDto;
import com.sf.openapi.express.sample.waybill.dto.WaybillRespDto;
import com.sf.openapi.express.sample.waybill.tools.WaybillDownloadTools;
import com.sf.csim.express.service.HttpClientUtil;
import com.sf.csim.express.service.VerifyCodeUtil;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ErpService {

    Logger logger = Logger.getLogger(ErpService.class);

    @Autowired
    private ErpDao erpDao;

    @Autowired
    private SysClient sysClient;

    @Autowired
    private PayClient payClient;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private AfterSaleServiceClient afterSaleServiceClient;

    @Autowired
    private Writer writer;

    @Autowired
    public ObjectToSql objectToSql;

    @Autowired
    public SessionFactory sessionFactory;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    BarcodeUtil barcodeUtil;

    @Autowired
    ImageBase64 imageBase64;

    @Autowired
    private HttpProxyDiscovery httpProxyDiscovery;

    @Autowired
    private SfExpress sfExpress;

    @Autowired
    private HttpClientUtil httpClientUtil;

    @Autowired
    private VerifyCodeUtil verifyCodeUtil;

    public String savePurchase(Purchase purchase) {
        String result = CommonConstant.fail;

        result += erpDao.save(purchase);
        if (purchase.getType().compareTo(ErpConstant.purchase_type_temp) == 0 &&
                purchase.getTemporaryPurchasePayKind().compareTo(ErpConstant.purchase_type_temp_payKind_deposit) == 0) {
            purchase.getPurchaseBook().setPurchase(new Purchase(purchase.getId()));
            purchase.getPurchaseBook().setState(ErpConstant.purchase_type_temp_deposit_uppay);
            result += erpDao.save(purchase.getPurchaseBook());
        }

        result += savePurchaseProducts(purchase);
        result += savePurchasePays(purchase);

        /**
         * 发起采购流程
         */
        String auditEntity = AuditFlowConstant.business_purchase;
        if (purchase.getType().compareTo(ErpConstant.purchase_type_emergency) == 0 ||
                purchase.getType().compareTo(ErpConstant.purchase_type_cash) == 0 ||
                purchase.getType().compareTo(ErpConstant.purchase_type_deposit) == 0) {
            auditEntity = AuditFlowConstant.business_purchaseEmergency;
        }

        result += launchAuditFlow(auditEntity, purchase.getId(), purchase.getNo(), purchase.getName(),
                "请审核采购单：" + purchase.getNo() ,purchase.getInputer());

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String savePurchaseProducts(Purchase purchase) {
        String result = CommonConstant.fail;

        if (purchase.getDetails() != null) {
            for (PurchaseDetail detail : purchase.getDetails()) {
                Product product = detail.getProduct();

                detail.setProduct(product);
                detail.setProductNo(product.getNo());
                detail.setProductName(product.getName());
                detail.setAmount(product.getUnitPrice() * detail.getQuantity());
                detail.setPrice(product.getUnitPrice());

                Purchase doubleRelatePurchase = new Purchase();
                doubleRelatePurchase.setId(purchase.getId());
                detail.setPurchase(doubleRelatePurchase);
                result += erpDao.save(detail);

                erpDao.deleteFromRedis(detail.getClass().getName() + CommonConstant.underline + detail.getId());

                PurchaseDetailProduct detailProduct = new PurchaseDetailProduct();
                detailProduct.setPurchaseDetail(detail);

                /**
                 * 采购了多少数量的商品，就插入多少数量的商品记录
                 */
                int productQuantity = detail.getQuantity().intValue();
                if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                    detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {
                    productQuantity = 1;
                }

                ProductDescribe describe = product.getDescribe();
                result += erpDao.save(describe);

                for (int i = 0; i < productQuantity; i++) {
                    product.setDescribe(describe);
                    result += erpDao.insert(product);

                    /**
                     * 因为保存完 product 才保存它对应的 properties，所以会导致 product 里的 properties，是前一个 product 的 properties
                     * 所以需要删除 redis 里的 product
                     */
                    erpDao.deleteFromRedis(product.getClass().getName() + CommonConstant.underline + product.getId());

                    /**
                     * 使用 new 新建，避免直接使用已经包含 property 属性的 product， 使得 product 与 property 循环嵌套
                     */
                    Product doubleRelateProduct = new Product();
                    doubleRelateProduct.setId(product.getId());

                    if (product.getProperties() != null) {
                        for (ProductOwnProperty ownProperty : product.getProperties()) {
                            ownProperty.setProduct(doubleRelateProduct);
                            result += erpDao.insert(ownProperty);
                        }
                    }

                    detailProduct.setProduct(product);
                    result += erpDao.insert(detailProduct);
                }
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String savePurchasePays(Purchase purchase) {
        String result = CommonConstant.fail;

        for (Pay pay : purchase.getPays()) {
            pay.setPayDate(dateUtil.getSecondCurrentTimestamp());
            pay.setState(PayConstants.pay_state_apply);
            pay.setBalanceType(PayConstants.balance_type_expense);

            pay.setEntity(purchase.getClass().getSimpleName().toLowerCase());
            pay.setEntityId(purchase.getId());
            pay.setEntityNo(purchase.getNo());
        }

        /**
         * 保存临时采购订金支付记录
         */
        if (purchase.getType().compareTo(ErpConstant.purchase_type_temp) == 0 &&
                purchase.getTemporaryPurchasePayKind().compareTo(ErpConstant.purchase_type_temp_payKind_deposit) == 0) {
            Map<String, String> result1 = writer.gson.fromJson(payClient.saveSplitAmountPays(purchase.getPurchaseBook().getDeposit(), writer.gson.toJson(purchase.getPays())),
                    new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
            result += result1.get(CommonConstant.result);

        } else {
            for (Pay pay : purchase.getPays()) {
                Map<String, String> result1 = writer.gson.fromJson(payClient.save(Pay.class.getSimpleName(), writer.gson.toJson(pay)),
                        new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
                result += result1.get(CommonConstant.result);
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String saveProductsCheckDetail(ProductCheck productCheck) {
        String result = CommonConstant.fail;
        // 新建一个商品盘点单并设置该商品盘点单的id为productCheck的id
        ProductCheck productCheck1 = new ProductCheck();
        productCheck1.setId(productCheck.getId());
        if (productCheck.getDetails() != null && !productCheck.getDetails().isEmpty()) {
            for (ProductCheckDetail detail : productCheck.getDetails()) {
                if (detail.getId() != null){
                    result += erpDao.updateById(detail.getId(),detail);
                    continue;
                }
                // 不存在该条形码的商品
                if (erpDao.queryById(detail.getProduct().getId(),Product.class) == null){
                    detail.setProductCheck(productCheck1);
                    detail.setUnit("件");
                    result += erpDao.save(detail);
                    continue;
                }
                // 设置盘点详细条目的盘点单据编号
                detail.setProductCheck(productCheck1);
                ProductCheckDetail detail2 = new ProductCheckDetail();
                detail2.setProductCheck(productCheck1);
                detail2.setProduct(detail.getProduct());
                // 该条形码所对应的详细条目已存在，说明是商品扫重复了
                if(erpDao.query(detail2) != null && !erpDao.query(detail2).isEmpty()){
                    continue;
                }
                Product product = detail.getProduct();
                product = (Product) (erpDao.query(product).get(0));
                ProductCheckDetail detail1 = new ProductCheckDetail();
                detail1.setItemNo(product.getNo());
                detail1.setProductCheck(productCheck1);
                // 该存货编码所对应的盘点详细条目已存在
                if (erpDao.query(detail1) != null && !erpDao.query(detail1).isEmpty()){
                    detail1 = (ProductCheckDetail)(erpDao.query(detail1).get(0));
                    // 更新已存在条目的盘点数量及盘点金额
                    detail1.setCheckQuantity(detail1.getCheckQuantity()+detail.getCheckQuantity());
                    detail1.setCheckAmount((detail1.getCheckQuantity()+detail.getCheckQuantity())*product.getUnitPrice());
                    result += erpDao.updateById(detail1.getId(),detail1);

                    // 插入一条盘点数量和盘点金额为0的数据，目的是为了扫描商品时判断是否扫重
                    detail.setItemNo(detail1.getItemNo());
                    detail.setItemName(detail1.getItemName());
                    detail.setCheckQuantity(0.0f);
                    detail.setCheckAmount(product.getUnitPrice()*0.0f);
                    detail.setPaperQuantity(detail1.getPaperQuantity());
                    detail.setPaperAmount(detail1.getPaperAmount());
                    detail.setUnit(detail1.getUnit());
                    detail.setUnitPrice(detail1.getUnitPrice());
                    result += erpDao.save(detail);

                }else{
                    Stock stock = new Stock();
                    stock.setProductNo(product.getNo());
                    stock.setWarehouse(productCheck.getWarehouse());
                    // 该仓库中存在该商品编码的商品
                    if (erpDao.query(stock) != null && !erpDao.query(stock).isEmpty()){
                        stock = (Stock)(erpDao.query(stock).get(0));
                        detail.setPaperQuantity(stock.getQuantity());
                        detail.setPaperAmount(stock.getQuantity()*product.getUnitPrice());
                    }
                    PurchaseDetail purchaseDetail = new PurchaseDetail();
                    purchaseDetail.setProductNo(product.getNo());
                    purchaseDetail = (PurchaseDetail)(erpDao.query(purchaseDetail).get(0));
                    detail.setUnit(purchaseDetail.getUnit());
                    detail.setUnitPrice(product.getUnitPrice());
                    detail.setItemNo(product.getNo());
                    detail.setItemName(product.getName());
                    detail.setCheckAmount(detail.getCheckQuantity()*product.getUnitPrice());
                    result += erpDao.save(detail);
                }
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String updatePurchase(Purchase purchase) {
        String result = CommonConstant.fail;

        /**
         * 查询数据库里的采购单
         */
        Purchase dbPurchase = queryPurchaseWithPaysById(purchase.getId());
        PurchaseDetail purchaseDetail = (PurchaseDetail) erpDao.queryById(((PurchaseDetail) dbPurchase.getDetails().toArray()[0]).getId(), PurchaseDetail.class);
        Product stateProduct = (Product) erpDao.queryById(((PurchaseDetailProduct) purchaseDetail.getPurchaseDetailProducts().toArray()[0]).getProduct().getId(), Product.class);

        if (stateProduct.getState().compareTo(ErpConstant.product_state_purchase) == 0) {       //采购状态的才可以修改
            result += erpDao.updateById(purchase.getId(), purchase);

            /**
             * 保存采购单里的新商品信息，删除旧商品信息
             */
            result += savePurchaseProducts(purchase);
            result += deletePurchaseProducts(dbPurchase);

            /**
             * 保存采购单里的新支付记录，删除旧支付记录
             */
            result += savePurchasePays(purchase);
            result += deletePurchasePays(dbPurchase);

            /**
             * 保存采购单里的新临时采购预订单，删除旧临时采购预订单
             */
            List<PurchaseBook> dbPurchaseBooks = null;
            if (dbPurchase.getType().compareTo(ErpConstant.purchase_type_temp) == 0) {
                PurchaseBook queryPurchaseBook = new PurchaseBook();
                queryPurchaseBook.setPurchase(new Purchase(purchase.getId()));
                dbPurchaseBooks = erpDao.query(queryPurchaseBook);
            }

            if (purchase.getType().compareTo(ErpConstant.purchase_type_temp) == 0 &&
                    purchase.getTemporaryPurchasePayKind().compareTo(ErpConstant.purchase_type_temp_payKind_deposit) == 0) {
                purchase.getPurchaseBook().setPurchase(new Purchase(purchase.getId()));
                purchase.getPurchaseBook().setState(ErpConstant.purchase_type_temp_deposit_uppay);
                result += erpDao.save(purchase.getPurchaseBook());
            }

            if (dbPurchaseBooks != null) {
                for (PurchaseBook purchaseBook : dbPurchaseBooks) {
                    result += erpDao.delete(purchaseBook);
                }
            }

            /**
             * 修改事宜信息
             */
            String oldEntity = AuditFlowConstant.business_purchase, newEntity = AuditFlowConstant.business_purchase;

            if (dbPurchase.getType().compareTo(ErpConstant.purchase_type_emergency) == 0 ||
                    dbPurchase.getType().compareTo(ErpConstant.purchase_type_cash) == 0 ||
                    dbPurchase.getType().compareTo(ErpConstant.purchase_type_deposit) == 0) {
                oldEntity = AuditFlowConstant.business_purchaseEmergency;
            }

            if (purchase.getType().compareTo(ErpConstant.purchase_type_emergency) == 0 ||
                    purchase.getType().compareTo(ErpConstant.purchase_type_cash) == 0 ||
                    purchase.getType().compareTo(ErpConstant.purchase_type_deposit) == 0) {
                newEntity = AuditFlowConstant.business_purchaseEmergency;
            }

            result += updateAudit(dbPurchase.getId(), oldEntity, newEntity, purchase.getName(), "请审核采购单：" + dbPurchase.getNo());

        } else {
            result += CommonConstant.fail + ", 采购单 " + purchase.getNo() + " 里的商品，已审核通过，不能修改";
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public Purchase queryPurchaseWithPaysById(Integer id) {
        Purchase dbPurchase = (Purchase) erpDao.queryById(id, Purchase.class);
        dbPurchase.setPays(getPaysByEntity(dbPurchase.getClass().getSimpleName().toLowerCase(), id));
        return dbPurchase;
    }

    public String deletePurchaseProducts(Purchase purchase) {
        String result = CommonConstant.fail;

        if (purchase.getDetails() != null) {
            for (PurchaseDetail detail : purchase.getDetails()) {
                PurchaseDetail dbDetail = (PurchaseDetail) erpDao.queryById(detail.getId(), detail.getClass());

                Product product = null;
                for (PurchaseDetailProduct detailProduct : dbDetail.getPurchaseDetailProducts()) {
                    product = (Product) erpDao.queryById(detailProduct.getProduct().getId(), detailProduct.getProduct().getClass());

                    if (product.getProperties() != null && !product.getProperties().isEmpty()) {
                        for (ProductOwnProperty ownProperty : product.getProperties()) {
                            result += erpDao.delete(ownProperty);
                        }
                    }

                    result += erpDao.delete(product);
                }

                if (product != null) {
                    result += erpDao.delete(product.getDescribe());
                }

                result += erpDao.delete(detail);
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String deletePurchasePays(Purchase purchase) {
        String result = CommonConstant.fail;

        if (purchase.getPays() != null) {
            for (Pay pay : purchase.getPays()) {
                Map<String, String> result1 = writer.gson.fromJson(payClient.delete(Pay.class.getSimpleName(), writer.gson.toJson(pay)),
                        new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
                result += result1.get(CommonConstant.result);
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 确认线下支付订单已付款
     * @param purchase
     */
    public String paidOfflinePurchase(Purchase purchase) {
        return payClient.offlinePaid(writer.gson.toJson(purchase.getPays()));
    }




    /**
     * 设置入库
     * @param stockIn
     * @return
     */
    public String stockIn(StockInOut stockIn){
        String result = CommonConstant.fail;

        result += isCanStockIn(stockIn);
        if (!result.contains(CommonConstant.fail+CommonConstant.fail)) {
            StockInOut stateStockIn = new StockInOut();
            stateStockIn.setId(stockIn.getId());
            stateStockIn.setState(stockIn.getState());
            result += erpDao.updateById(stateStockIn.getId(), stateStockIn);

            result += setStockProductIn(stockIn);

            /**
             * 押金入库后通知仓储预计退还货物时间，财务人员预计退还押金时间
             */
            if (stockIn.getType().compareTo(ErpConstant.stockInOut_type_deposit) == 0) {
                result += launchAuditFlow(AuditFlowConstant.business_stockIn_deposit_cangchu, stockIn.getId(), stockIn.getNo(),
                        "押金入库单 " + stockIn.getNo() + ", 预计" + stockIn.getDeposit().getReturnGoodsDate() + "退货",
                        "请注意退货时间：" + stockIn.getDeposit().getReturnGoodsDate(),
                        stockIn.getInputer());

                result += launchAuditFlow(AuditFlowConstant.business_stockIn_deposit_caiwu, stockIn.getId(), stockIn.getNo(),
                        "押金入库单 " + stockIn.getNo() + ", 预计" + stockIn.getDeposit().getReturnDepositDate() + "退押金",
                        "请注意退押金时间：" + stockIn.getDeposit().getReturnDepositDate(),
                        stockIn.getInputer());
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 设置入库库存，商品入库状态
     * @param stockIn
     * @return
     */
    public String setStockProductIn(StockInOut stockIn) {
        String result = CommonConstant.fail;

        for (StockInOutDetail detail : stockIn.getDetails()) {
            detail.setState(ErpConstant.stockInOut_detail_state_finished);
            result += erpDao.updateById(detail.getId(), detail);

            /**
             * 修改商品为入库
             */

            for (StockInOutDetailProduct detailProduct : detail.getStockInOutDetailProducts()) {
                detailProduct.getProduct().setState(getProductStockInState(detailProduct.getProduct()));
                result += erpDao.updateById(detailProduct.getProduct().getId(), detailProduct.getProduct());
            }

            Product dbProduct = (Product) erpDao.queryById(((StockInOutDetailProduct)(detail.getStockInOutDetailProducts().toArray()[0])).getProduct().getId(), Product.class);

            /**
             * 调仓入库，设置调仓出库为完成状态
             */
            if (stockIn.getType().compareTo(ErpConstant.stockInOut_type_changeWarehouse) == 0) {
                StockInOut stockOutChangeWarehouse = getLastStockInOutByProductAndType(dbProduct, ErpConstant.stockOut);
                stockOutChangeWarehouse.getChangeWarehouse().setState(ErpConstant.stockInOut_state_changeWarehouse_finished);
                result += erpDao.updateById(stockOutChangeWarehouse.getChangeWarehouse().getId(), stockOutChangeWarehouse.getChangeWarehouse());
            }

            /**
             * 添加库存
             */
            Stock tempStock = new Stock();
            tempStock.setProductNo(dbProduct.getNo());
            tempStock.setWarehouse(stockIn.getWarehouse());

            /**
             * 在同一个仓库的同类商品做增量入库，才修改商品数量
             */
            if (stockIn.getType().compareTo(ErpConstant.stockInOut_type_increment) == 0) {
                List<Stock> dbStocks = erpDao.query(tempStock);

                if (!dbStocks.isEmpty()) {
                    dbStocks.get(0).setDate(stockIn.getDate());
                    result += setStockQuantity(dbStocks.get(0), detail.getQuantity(), CommonConstant.add);

                } else {
                    result += saveStock(tempStock, detail.getQuantity(), detail.getUnit(), stockIn.getDate());
                }

            } else {
                result += saveStock(tempStock, detail.getQuantity(), detail.getUnit(), stockIn.getDate());
            }

            /**
             * 在 redis 里，使用商品编号关联库存在 redis 里的 key，以便后期快速查询该编号商品库存
             */
            erpDao.putKeyToHash(ErpConstant.stock + CommonConstant.underline + tempStock.getProductNo(),
                    tempStock.getClass().getName() + CommonConstant.underline + tempStock.getId());
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }


    /**
     * 出库
     * @param stockOut
     * @return
     */
    public String stockOut(StockInOut stockOut){
        String result = CommonConstant.fail;

        result += isCanStockOut(stockOut);
        if (!result.contains(CommonConstant.fail+CommonConstant.fail)) {
            StockInOut stateStockOut = new StockInOut();
            stateStockOut.setId(stockOut.getId());
            stateStockOut.setState(stockOut.getState());
            result += erpDao.updateById(stateStockOut.getId(), stateStockOut);

            if (stockOut.getType().compareTo(ErpConstant.stockInOut_type_normal_outWarehouse) == 0) {
                /**
                 * 确认订单支付完成后，系统会自动出库商品，即系统自动出库，这时没有出库人员，因此随机设置出库人员
                 */
                stockOut.setInputer(getRandomStockOutUser());
                result += erpDao.updateById(stockOut.getId(), stockOut);

                /**
                 * 设置出库库存,商品出库状态, 提醒出库人员打印快递单
                 */
                result += setStockProductOut(stockOut);
                result += launchAuditFlow(AuditFlowConstant.business_stockOut_print_expressWaybill_notify, stockOut.getId(), stockOut.getNo(),
                        "打印出库单:" + stockOut.getNo() + " 里商品的快递单", "打印出库单:" + stockOut.getNo() + " 里商品的快递单",
                        stockOut.getInputer());


            } else if (stockOut.getType().compareTo(ErpConstant.stockInOut_type_breakage_outWarehouse) == 0) {
                /**
                 * 报损出库进入报损出库审批流程
                 */
                result += launchAuditFlow(AuditFlowConstant.business_stockOut_breakage, stockOut.getId(), stockOut.getNo(),
                        "出库单:" + stockOut.getNo() + " 商品报损出库", "请报损出库出库单" + stockOut.getNo() + " 的商品",
                        stockOut.getInputer());


            } else {
                /**
                 * 设置出库库存,商品出库状态
                 */
                result += setStockProductOut(stockOut);
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 设置出库库存，商品出库状态
     * @param stockOut
     * @return
     */
    public String setStockProductOut(StockInOut stockOut) {
        String result = CommonConstant.fail;

        for (StockInOutDetail detail : stockOut.getDetails()) {
            detail.setState(ErpConstant.stockInOut_detail_state_finished);
            result += erpDao.updateById(detail.getId(), detail);

            for (StockInOutDetailProduct detailProduct : detail.getStockInOutDetailProducts()) {
                detailProduct.getProduct().setState(getProductStockOutState(detailProduct.getProduct()));
                result += erpDao.updateById(detailProduct.getProduct().getId(), detailProduct.getProduct());
            }

            Product dbProduct = (Product) erpDao.queryById(((StockInOutDetailProduct)(detail.getStockInOutDetailProducts().toArray()[0])).getProduct().getId(), Product.class);

            Stock tempStock = new Stock();
            tempStock.setProductNo(dbProduct.getNo());
            tempStock.setWarehouse(getLastStockInOutByProductAndType(dbProduct, ErpConstant.stockIn).getWarehouse());
            Stock dbStock = (Stock)erpDao.query(tempStock).get(0);

            if (dbStock.getQuantity().compareTo(detail.getQuantity()) >= 0) {
                result += setStockQuantity(dbStock, detail.getQuantity(), CommonConstant.subtract);

                /**
                 * 库存数量为 0，则删除库存
                 */
                if (dbStock.getQuantity().compareTo(0f) == 0) {
                    result += erpDao.delete(dbStock);
                    erpDao.deleteFromRedis(dbStock.getClass().getName() + CommonConstant.underline + dbStock.getId());
                }

            } else {
                result += CommonConstant.fail + ",商品：" + dbStock.getProductNo() + " 库存数量不足，不能出库";
            }

        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String saveStock(Stock stock, Float quantity, String unit, Timestamp date) {
        stock.setNo(erpDao.getNo(ErpConstant.no_stock_perfix));
        stock.setState(ErpConstant.stock_state_valid);
        stock.setQuantity(quantity);
        stock.setUnit(unit);
        stock.setDate(date);

        return  erpDao.save(stock);
    }

    /**
     * 根据商品获取出库/入库
     */
    public StockInOut getLastStockInOutByProductAndType(Product product, String type) {
        StockInOutDetailProduct detailProduct = new StockInOutDetailProduct();
        detailProduct.setProduct(product);
        List<StockInOutDetailProduct> detailProducts = erpDao.query(detailProduct);

        List<StockInOut> stockInOuts = new ArrayList<>();
        for (StockInOutDetailProduct ele : detailProducts) {
            stockInOuts.add((StockInOut) erpDao.queryById(ele.getStockInOutDetail().getStockInOut().getId(), ele.getStockInOutDetail().getStockInOut().getClass()));
        }

        StockInOut[] stockInOutsArr = new StockInOut[stockInOuts.size()];
        stockInOuts.toArray(stockInOutsArr);

        Arrays.sort(stockInOutsArr, new Comparator<StockInOut>() {
            @Override
            public int compare(StockInOut o1, StockInOut o2) {
                if (o1.getId().compareTo(o2.getId()) > 0) {
                    return 1;
                } else if (o1.getId().compareTo(o2.getId()) < 0) {
                    return -1;
                }

                return 0;
            }
        });

        for (int i = 0; i < stockInOutsArr.length; i++) {

            if (type.equals(ErpConstant.stockIn)) {
                if (stockInOutsArr[i].getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) < 0) {
                    return stockInOutsArr[i];
                }

            } else if (type.equals(ErpConstant.stockOut)) {
                if (stockInOutsArr[i].getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) >= 0) {
                    return stockInOutsArr[i];
                }
            }
        }

        return null;
    }

    public String saveStockInOut(StockInOut stockInOut) {
        String result = CommonConstant.fail;

        if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_deposit) == 0) {
            result += erpDao.save(stockInOut.getDeposit());
        }

        if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_process) == 0 ||
                stockInOut.getType().compareTo(ErpConstant.stockInOut_type_repair) == 0) {
            result += erpDao.save(stockInOut.getProcessRepair());
        }

        if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_changeWarehouse_outWarehouse) == 0) {
            stockInOut.getChangeWarehouse().setState(ErpConstant.stockInOut_state_changeWarehouse_unfinished);
            result += erpDao.save(stockInOut.getChangeWarehouse());
        }

        result += erpDao.save(stockInOut);
        result += saveStockInOutDetails(stockInOut);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String saveStockInOutDetails(StockInOut stockInOut) {
        String result = CommonConstant.fail;

        for (StockInOutDetail detail : stockInOut.getDetails()) {
            detail.setState(ErpConstant.stockInOut_detail_state_apply);
            detail.setStockInOut(stockInOut);
            result += erpDao.save(detail);

            for (StockInOutDetailProduct detailProduct : detail.getStockInOutDetailProducts()) {
                /**
                 * 由于 insert 方法里调用了 writer.gson.fromJson 方法，如果类和子类有嵌套，就会一直重复解析子类对象，
                 * 因此new一个对象来防止嵌套
                 */
                StockInOutDetail idDetail = new StockInOutDetail();
                idDetail.setId(detail.getId());
                detailProduct.setStockInOutDetail(idDetail);
                result += erpDao.insert(detailProduct);
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    private String setStockQuantity(Stock stock, Float quantity, String operator) {
        BigDecimal dbQuantity = new BigDecimal(Float.toString(stock.getQuantity()));
        BigDecimal addQuantity = new BigDecimal(Float.toString(quantity));

        if (operator.equals(CommonConstant.add)) {
            stock.setQuantity(dbQuantity.add(addQuantity).floatValue());
        } else if (operator.equals(CommonConstant.subtract)) {
            stock.setQuantity(dbQuantity.subtract(addQuantity).floatValue());
        }

        return erpDao.updateById(stock.getId(), stock);
    }

    public String purchaseStateModify(Audit audit, Integer purchaseState, Integer productState) {
        String result = CommonConstant.fail;

        Purchase purchase = (Purchase)erpDao.queryById(audit.getEntityId(), Purchase.class);

        Purchase statePurchase = new Purchase();
        statePurchase.setId(purchase.getId());
        statePurchase.setState(purchaseState);

        result += erpDao.updateById(statePurchase.getId(), statePurchase);
        if (result.contains(CommonConstant.success)) {
            for (PurchaseDetail detail : purchase.getDetails()) {
                result += setProductStateByPurchaseDetail(detail, productState);
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String purchaseProductsStateModify(Audit audit, Integer productState) {
        String result = CommonConstant.fail;

        Purchase purchase = (Purchase)erpDao.queryById(audit.getEntityId(), Purchase.class);
        for (PurchaseDetail detail : purchase.getDetails()) {
            result += setProductStateByPurchaseDetail(detail, productState);
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String setProductStateByPurchaseDetail(PurchaseDetail detail, Integer productState) {
        String result = CommonConstant.fail;

        PurchaseDetailProduct detailProduct = new PurchaseDetailProduct();
        detailProduct.setPurchaseDetail(detail);
        List<PurchaseDetailProduct> dbDetailProducts = erpDao.query(detailProduct);

        for (PurchaseDetailProduct dbDetailProduct : dbDetailProducts) {
            Product stateProduct = new Product();
            stateProduct.setId(dbDetailProduct.getProduct().getId());
            stateProduct.setState(productState);
            result += erpDao.updateById(stateProduct.getId(), stateProduct);
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }


    public String purchaseEmergencyPass(Audit audit, Integer purchaseState, Integer productState) {
        return purchaseStateModify(audit, purchaseState, productState);
    }

    public String purchasePay(Audit audit) {
        String result = CommonConstant.fail;

        Purchase purchase = (Purchase) erpDao.queryById(audit.getEntityId(), Purchase.class);

        List<Pay> pays = null;
        if (purchase.getType().compareTo(ErpConstant.purchase_type_temp) == 0) {
            PurchaseBook queryPurchaseBook = new PurchaseBook();
            queryPurchaseBook.setPurchase(new Purchase(purchase.getId()));
            List<PurchaseBook> dbPurchaseBooks = erpDao.query(queryPurchaseBook);

            if (!dbPurchaseBooks.isEmpty()) {
                PurchaseBook dbPurchaseBook = dbPurchaseBooks.get(0);
                dbPurchaseBook.setState(ErpConstant.purchase_type_temp_deposit_paid);
                result += erpDao.updateById(dbPurchaseBook.getId(), dbPurchaseBook);

                purchase.setPurchaseBook(dbPurchaseBook);
                pays = queryDepositPaysByPurchase(purchase);
            }
        }

        if (pays == null) {
            pays = getPaysByEntity(purchase.getClass().getSimpleName().toLowerCase(), purchase.getId());
        }

        Map<String, String> result1 = writer.gson.fromJson(payClient.offlinePaid(writer.gson.toJson(pays)), new TypeToken<Map<String, String>>(){}.getType());
        result += result1.get(CommonConstant.result);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String stockInReturnDeposit(Audit audit) {
        String result = CommonConstant.fail;

        StockInOut stockInOut = (StockInOut) erpDao.queryById(audit.getEntityId(), StockInOut.class);
        Purchase purchase = (Purchase) erpDao.queryById(stockInOut.getDeposit().getPurchase().getId(), stockInOut.getDeposit().getPurchase().getClass());

        Map<String, String> result1 = writer.gson.fromJson(
                payClient.refund(audit.getEntity(), audit.getEntityId(), stockInOut.getDeposit().getAmount(),
                                 writer.gson.toJson(getPaysByEntity(purchase.getClass().getSimpleName().toLowerCase(), purchase.getId()))),
                new TypeToken<Map<String, String>>(){}.getType());
        result += result1.get(CommonConstant.result);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public List<Pay> getPaysByEntity(String entity, Integer entityId) {
        Pay pay = new Pay();
        pay.setEntity(entity);
        pay.setEntityId(entityId);

        return writer.gson.fromJson(payClient.query(pay.getClass().getSimpleName(), writer.gson.toJson(pay)), new TypeToken<List<Pay>>(){}.getType());
    }

    public List<Pay> queryDepositPaysByPurchase(Purchase purchase) {
        List<Pay> pays = getPaysByEntity(purchase.getClass().getSimpleName().toLowerCase(), purchase.getId());

        /**
         * 采购预订单支付的记录，是按照订金支付记录，余款支付记录先后排列的，所以订金支付记录的 id 是小的。
         * 这里设置支付记录根据 id 由小到大排序，以便找到订金支付记录
         */
        pays.sort(new Comparator<Pay>() {
            @Override
            public int compare(Pay o1, Pay o2) {
                if (o1.getId().compareTo(o2.getId()) > 0) {
                    return 1;
                } else if (o1.getId().compareTo(o2.getId()) < 0) {
                    return -1;
                }

                return 0;
            }
        });

        List<Pay> depositPays = new ArrayList<>();
        Float sumAmount = 0f;
        for (int i = 0; i < pays.size(); i++) {
            sumAmount = new BigDecimal(Float.toString(sumAmount)).add(new BigDecimal(Float.toString(pays.get(i).getAmount()))).floatValue();

            if (sumAmount.compareTo(purchase.getPurchaseBook().getDeposit()) <= 0) {
                depositPays.add(pays.get(i));
            } else {
                break;
            }
        }

        return depositPays;
    }

    public String productStateModify(Audit audit, Integer state) {
        Product stateProduct = new Product();
        stateProduct.setId(audit.getEntityId());
        stateProduct.setState(state);

        return erpDao.updateById(stateProduct.getId(), stateProduct);
    }

    public Purchase getLastValidPurchase(Product product) {
        PurchaseDetailProduct detailProduct = new PurchaseDetailProduct();
        detailProduct.setProduct(product);
        List<PurchaseDetailProduct> detailProducts = erpDao.query(detailProduct);

        for (PurchaseDetailProduct ele : detailProducts) {
            PurchaseDetail detail = (PurchaseDetail) erpDao.queryById(ele.getPurchaseDetail().getId(), PurchaseDetail.class);
            if (detail.getPurchase().getState().compareTo(ErpConstant.purchase_state_cancel) != 0) {
                return detail.getPurchase();
            }
        }

        return null;
    }

    public String queryTargetEntity(String targetEntity, String entity, Object queryObject, int position, int rowNum) {
        String result = "";

        if (targetEntity.equalsIgnoreCase(Purchase.class.getSimpleName()) &&
                entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {
            List<Purchase> purchases = (List<Purchase>)erpDao.complexQuery(Purchase.class,
                    writer.gson.fromJson(writer.gson.toJson(queryObject), new TypeToken<Map<String, String>>(){}.getType()), position, rowNum);

            Purchase tempPurchase = new Purchase();
            PurchaseDetail tempPurchaseDetail = new PurchaseDetail();

            for (int i = 0; i < purchases.size(); i++) {
                tempPurchase.setId(purchases.get(i).getId());
                tempPurchaseDetail.setPurchase(tempPurchase);

                List<PurchaseDetail> details = erpDao.query(tempPurchaseDetail);

                for (int j = 0; j < details.size(); j++) {
                    if (details.get(j).getPurchaseDetailProducts() != null && !details.get(j).getPurchaseDetailProducts().isEmpty()) {
                        Set<PurchaseDetailProduct> detailProducts = new HashSet<>();

                        for (PurchaseDetailProduct detailProduct : details.get(j).getPurchaseDetailProducts()) {
                            Product product = (Product) erpDao.queryById(detailProduct.getProduct().getId(), detailProduct.getProduct().getClass());
                            if (product != null && (product.getState().compareTo(ErpConstant.product_state_purchase_close) == 0 ||
                                    product.getState().compareTo(ErpConstant.product_state_stockOut) == 0)) {

                                    details.get(j).setProduct(product);
                                    detailProducts.add(detailProduct);
                            }
                        }

                        if (!detailProducts.isEmpty()) {
                            details.get(j).setPurchaseDetailProducts(detailProducts);
                        } else {
                            details.remove(details.get(j));
                            j--;
                        }

                    } else {
                        details.remove(details.get(j));
                        j--;
                    }
                }

                if (!details.isEmpty()) {
                    for (PurchaseDetail detail : details) {
                        if (!detail.getUnit().equals(ErpConstant.unit_g) && !detail.getUnit().equals(ErpConstant.unit_kg) &&
                                !detail.getUnit().equals(ErpConstant.unit_ct) && !detail.getUnit().equals(ErpConstant.unit_oz)) {
                            detail.setQuantity((float)detail.getPurchaseDetailProducts().size());
                        }
                    }

                    purchases.get(i).setDetails(new HashSet<>(details));
                } else {
                    purchases.remove(purchases.get(i));
                    i--;
                }
            }

            result = writer.gson.toJson(purchases);


        } else if (targetEntity.equalsIgnoreCase(PurchaseDetail.class.getSimpleName()) &&
                entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            Map<String, String> queryParameters = writer.gson.fromJson(writer.gson.toJson(queryObject), new TypeToken<Map<String, String>>(){}.getType());
            StockInOut stockInOut = writer.gson.fromJson(queryParameters.get(StockInOut.class.getSimpleName().substring(0,1).toLowerCase()+StockInOut.class.getSimpleName().substring(1)), StockInOut.class);

            List products;
            if (stockInOut == null) {
                products = erpDao.complexQuery(Product.class, queryParameters, position, rowNum);

            } else {
                Class[] clazzs = {Product.class, ProductType.class, ProductDescribe.class};
                Map<String, List<Object>> results = erpDao.queryBySql(getStockInProductsByWarehouseComplexSql(queryParameters, stockInOut, position, rowNum), clazzs);
                products = results.get(Product.class.getName());
                List<Object> types = results.get(ProductType.class.getName());
                List<Object> describes = results.get(ProductDescribe.class.getName());

                int ii = 0;
                for (Object ele : products) {
                    ((Product)ele).setType((ProductType) types.get(ii));
                    ((Product)ele).setDescribe((ProductDescribe) describes.get(ii));
                    ii++;
                }
            }

            if (!products.isEmpty()) {
                List<PurchaseDetail> details = new ArrayList<>();

                PurchaseDetailProduct detailProduct = new PurchaseDetailProduct();
                for (int i = 0; i < products.size(); i++) {
                    detailProduct.setProduct((Product) products.get(i));

                    List<PurchaseDetailProduct> dbDetailProducts = erpDao.query(detailProduct);
                    if (dbDetailProducts != null && !dbDetailProducts.isEmpty()) {
                        PurchaseDetail detail = (PurchaseDetail) erpDao.queryById(dbDetailProducts.get(0).getPurchaseDetail().getId(), dbDetailProducts.get(0).getPurchaseDetail().getClass());

                        if (detail != null) {
                            detail.setProduct((Product) products.get(i));
                            detail.setPurchaseDetailProducts(new HashSet<>());
                            detail.getPurchaseDetailProducts().add(dbDetailProducts.get(0));
                            details.add(detail);
                        }
                    }

                }

                result = writer.gson.toJson(details);
            }
        }

        return result;
    }

    public List privateQuery(String entity, String json, int position, int rowNum) {
        if (entity.equalsIgnoreCase(Stock.class.getSimpleName())) {
            Class[] clazzs = {Stock.class, Product.class, Warehouse.class};
            Map<String, List<Object>> results = erpDao.queryBySql(getStockComplexSql(json, position, rowNum), clazzs);

            List<Object> stocks = results.get(Stock.class.getName());
            List<Object> products = results.get(Product.class.getName());
            List<Object> warehouses = results.get(Warehouse.class.getName());

            int i = 0;
            for (Object stock : stocks) {
                ((Stock)stock).setProduct((Product) products.get(i));
                ((Stock)stock).setWarehouse((Warehouse) warehouses.get(i));

                i++;
            }

            return stocks;

        } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            Class[] clazzs = {Product.class, ProductType.class, Supplier.class, ProductDescribe.class};
            Map<String, List<Object>> results = erpDao.queryBySql(getProductComplexSql(json, position, rowNum), clazzs);

            List<Object> products = results.get(Product.class.getName());
            List<Object> types = results.get(ProductType.class.getName());
            List<Object> suppliers = results.get(Supplier.class.getName());
            List<Object> describes = results.get(ProductDescribe.class.getName());

            int i = 0;
            for (Object product : products) {
                ((Product)product).setType((ProductType) types.get(i));
                ((Product)product).setSupplier((Supplier) suppliers.get(i));
                ((Product)product).setDescribe((ProductDescribe) describes.get(i));
                i++;
            }

            return products;

        } else if (entity.equalsIgnoreCase(ProductDescribe.class.getSimpleName())) {
            return erpDao.queryBySql(getProductDescribeSql(json, position, rowNum), ProductDescribe.class);

        } else if (entity.equalsIgnoreCase(ProductCheck.class.getSimpleName())) {
            Class[] clazzs = {ProductCheck.class, Warehouse.class, Dept.class, User.class, Company.class};
            Map<String, List<Object>> results = erpDao.queryBySql(getProductCheckComplexSql(json, position, rowNum), clazzs);

            List<Object> productChecks = results.get(ProductCheck.class.getName());
            List<Object> warehouses = results.get(Warehouse.class.getName());
            List<Object> depts = results.get(Dept.class.getName());
            List<Object> users = results.get(User.class.getName());
            List<Object> companies = results.get(Company.class.getName());

            int i = 0;
            for (Object productCheck : productChecks) {
                ((ProductCheck)productCheck).setWarehouse((Warehouse) warehouses.get(i));
                ((ProductCheck)productCheck).setDept((Dept) depts.get(i));
                ((ProductCheck)productCheck).setChartMaker((User) users.get(i));
                ((ProductCheck)productCheck).setCompany((Company) companies.get(i));

                i++;
            }
            return productChecks;
        }

        return new ArrayList();
    }

    public BigInteger privateRecordNum(String entity, String json){
        String sql = "";

        if (entity.equalsIgnoreCase(Stock.class.getSimpleName())) {
            sql = getStockComplexSql(json, 0, -1);

        } else if (entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            sql = getProductComplexSql(json, 0, -1);

        } else if (entity.equalsIgnoreCase(ProductDescribe.class.getSimpleName())) {
            sql = getProductDescribeSql(json, 0, -1);

        }else if (entity.equalsIgnoreCase(ProductCheck.class.getSimpleName())) {
            sql = getProductCheckComplexSql(json, 0, -1);
        }

        sql = "select count(t.id) from " + sql.split(" from ")[1];
        return (BigInteger)sessionFactory.getCurrentSession().createSQLQuery(sql).uniqueResult();
    }

    private String getStockComplexSql(String json, int position, int rowNum) {
        String sql = "";

        try {
            Map<String, Object> queryParameters = writer.gson.fromJson(json, new com.google.gson.reflect.TypeToken<Map<String, Object>>() {}.getType());

            String stockSql = objectToSql.generateComplexSqlByAnnotation(Stock.class,
                    writer.gson.fromJson(writer.gson.toJson(queryParameters.get(Stock.class.getSimpleName().toLowerCase())),
                            new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType()), position, rowNum);

            String selectSql = "", fromSql = "", whereSql = "", sortNumSql = "";

            String[] sqlParts = erpDao.getSqlPart(stockSql, Stock.class);
            selectSql = sqlParts[0];
            fromSql = sqlParts[1];
            whereSql = sqlParts[2];
            sortNumSql = sqlParts[3];

            selectSql += ", " + erpDao.getSelectColumns("t11", Product.class);
            fromSql += ", " + objectToSql.getTableName(Product.class) + " t11 ";
            if (!whereSql.trim().equals("")) {
                whereSql += " and ";
            }
            whereSql += " t11." + objectToSql.getColumn(Product.class.getDeclaredField("no")) +
                    " = t." + objectToSql.getColumn(Stock.class.getDeclaredField("productNo"));

            if (queryParameters.get(Product.class.getSimpleName().toLowerCase()) != null) {
                String productSql = objectToSql.generateComplexSqlByAnnotation(Product.class,
                        writer.gson.fromJson(writer.gson.toJson(queryParameters.get(Product.class.getSimpleName().toLowerCase())),
                                new com.google.gson.reflect.TypeToken<Map<String, String>>() {
                                }.getType()), position, rowNum);

                productSql = productSql.substring(0, productSql.indexOf(" order by "));
                productSql = productSql.replace(" t ", " t11 ").replace(" t.", " t11.");

                if (productSql.contains(" where ")) {
                    String[] parts = productSql.split(" where ");
                    String[] tables = parts[0].split(" from ")[1].split(" t11 ");

                    if (tables.length > 1) {
                        fromSql += tables[1];
                    }
                    whereSql += " and " + parts[1];
                }

            }

            selectSql += ", " + erpDao.getSelectColumns("t12", Warehouse.class);
            fromSql += ", " + objectToSql.getTableName(Warehouse.class) + " t12 ";
            whereSql += " and t12." + objectToSql.getColumn(Warehouse.class.getDeclaredField("id")) +
                    " = t." + objectToSql.getColumn(Stock.class.getDeclaredField("warehouse"));


            if (whereSql.indexOf(" and") == 0) {
                whereSql = whereSql.substring(" and".length());
            }

            sql = "select " + selectSql + " from " + fromSql + " where " + whereSql + " order by " + sortNumSql;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql;
    }

    private String getProductComplexSql(String json, int position, int rowNum) {
        String sql = "";

        try {
            Map<String, Object> queryParameters = writer.gson.fromJson(json, new com.google.gson.reflect.TypeToken<Map<String, Object>>() {}.getType());

            String productSql = objectToSql.generateComplexSqlByAnnotation(Product.class,
                    writer.gson.fromJson(writer.gson.toJson(queryParameters.get(Product.class.getSimpleName().toLowerCase())),
                            new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType()), position, rowNum);

            String selectSql = "", fromSql = "", whereSql = "", sortNumSql = "";

            String[] sqlParts = erpDao.getSqlPart(productSql, Product.class);
            selectSql = sqlParts[0];
            fromSql = sqlParts[1];
            whereSql = sqlParts[2];
            sortNumSql = sqlParts[3];


            selectSql += ", " + erpDao.getSelectColumns("t13", ProductType.class);
            fromSql += ", " + objectToSql.getTableName(ProductType.class) + " t13 ";
            if (!whereSql.trim().equals("")) {
                whereSql += " and ";
            }
            whereSql += " t13." + objectToSql.getColumn(ProductType.class.getDeclaredField("id")) +
                    " = t." + objectToSql.getColumn(Product.class.getDeclaredField("type"));

            selectSql += ", " + erpDao.getSelectColumns("t12", Supplier.class);
            fromSql += ", " + objectToSql.getTableName(Supplier.class) + " t12 ";
            whereSql += " and t12." + objectToSql.getColumn(Supplier.class.getDeclaredField("id")) +
                    " = t." + objectToSql.getColumn(Product.class.getDeclaredField("supplier"));

            selectSql += ", " + erpDao.getSelectColumns("t14", ProductDescribe.class);
            fromSql += ", " + objectToSql.getTableName(ProductDescribe.class) + " t14 ";
            whereSql += " and t14." + objectToSql.getColumn(ProductDescribe.class.getDeclaredField("id")) +
                    " = t." + objectToSql.getColumn(Product.class.getDeclaredField("describe"));


            fromSql += ", " + objectToSql.getTableName(StockInOutDetailProduct.class) + " t11 ";
            whereSql += " and t11." + objectToSql.getColumn(StockInOutDetailProduct.class.getDeclaredField("product")) +
                    " = t." + objectToSql.getColumn(Product.class.getDeclaredField("id"));

            fromSql += ", " + objectToSql.getTableName(StockInOutDetail.class) + " t111 ";
            whereSql += " and t111." + objectToSql.getColumn(StockInOutDetail.class.getDeclaredField("id")) +
                    " = t11." + objectToSql.getColumn(StockInOutDetailProduct.class.getDeclaredField("stockInOutDetail"));

            fromSql += ", " + objectToSql.getTableName(StockInOut.class) + " t22 ";
            whereSql += " and t22." + objectToSql.getColumn(StockInOut.class.getDeclaredField("id")) +
                    " = t111." + objectToSql.getColumn(StockInOutDetail.class.getDeclaredField("stockInOut")) +
                    " and t22.state = " + ErpConstant.stockInOut_state_finished;

            String stockInOutEntity = StockInOut.class.getSimpleName().substring(0,1).toLowerCase()+StockInOut.class.getSimpleName().substring(1);
            if (queryParameters.get(stockInOutEntity) != null) {
                String stockInOutSql = objectToSql.generateComplexSqlByAnnotation(StockInOut.class,
                        writer.gson.fromJson(writer.gson.toJson(queryParameters.get(stockInOutEntity)),
                                new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType()), position, rowNum);

                stockInOutSql = stockInOutSql.substring(0, stockInOutSql.indexOf(" order by "));
                stockInOutSql = stockInOutSql.replace(" t ", " t22 ").replace(" t.", " t22.");

                if (stockInOutSql.contains(" where ")) {
                    String[] parts = stockInOutSql.split(" where ");
                    String[] tables = parts[0].split(" from ")[1].split(" t22 ");

                    if (tables.length > 1) {
                        fromSql += tables[1];
                    }
                    whereSql += " and " + parts[1];
                }
            }


            if (whereSql.indexOf(" and") == 0) {
                whereSql = whereSql.substring(" and".length());
            }

            sql = "select distinct " + selectSql + " from " + fromSql + " where " + whereSql + " order by " + sortNumSql;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql;
    }

    private String getProductCheckComplexSql(String json, int position, int rowNum) {
        String sql = "";

        try {
            Map<String, Object> queryParameters = writer.gson.fromJson(json, new com.google.gson.reflect.TypeToken<Map<String, Object>>() {}.getType());

            String productCheckSql = objectToSql.generateComplexSqlByAnnotation(ProductCheck.class,
                    writer.gson.fromJson(writer.gson.toJson(queryParameters.get("productCheck")),
                            new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType()), position, rowNum);

            String selectSql = "", fromSql = "", whereSql = "", sortNumSql = "";

            String[] sqlParts = erpDao.getSqlPart(productCheckSql, ProductCheck.class);
            selectSql = sqlParts[0];
            fromSql = sqlParts[1];
            whereSql = sqlParts[2];
            sortNumSql = sqlParts[3];

            selectSql += ", " + erpDao.getSelectColumns("t11", Warehouse.class);
            fromSql += ", " + objectToSql.getTableName(Warehouse.class) + " t11 ";
            if (!whereSql.trim().equals("")) {
                whereSql += " and ";
            }
            whereSql += " t11." + objectToSql.getColumn(Warehouse.class.getDeclaredField("id")) +
                    " = t." + objectToSql.getColumn(ProductCheck.class.getDeclaredField("warehouse"));

            selectSql += ", " + erpDao.getSelectColumns("t12", Dept.class);
            fromSql += ", " + objectToSql.getTableName(Dept.class) + " t12 ";
            whereSql += " and t12." + objectToSql.getColumn(Dept.class.getDeclaredField("id")) +
                    " = t." + objectToSql.getColumn(ProductCheck.class.getDeclaredField("dept"));

            selectSql += ", " + erpDao.getSelectColumns("t13", User.class);
            fromSql += ", " + objectToSql.getTableName(User.class) + " t13 ";
            whereSql += " and t13." + objectToSql.getColumn(User.class.getDeclaredField("id")) +
                    " = t." + objectToSql.getColumn(ProductCheck.class.getDeclaredField("chartMaker"))+
                    " and t13." + objectToSql.getColumn(User.class.getDeclaredField("name")) +
            " like '%" + ((Map)(queryParameters.get("chartMaker"))).get("name")+"%'";

            selectSql += ", " + erpDao.getSelectColumns("t14", Company.class);
            fromSql += ", " + objectToSql.getTableName(Company.class) + " t14 ";
            whereSql += " and t14." + objectToSql.getColumn(Company.class.getDeclaredField("id")) +
                    " = t." + objectToSql.getColumn(ProductCheck.class.getDeclaredField("company"));

            if (whereSql.indexOf(" and") == 0) {
                whereSql = whereSql.substring(" and".length());
            }

            sql = "select " + selectSql + " from " + fromSql + " where " + whereSql + " order by " + sortNumSql;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql;
    }

    private String getStockInProductsByWarehouseComplexSql(Map<String, String> queryParameters, StockInOut stockInOut, int position, int rowNum) {
        String sql = "";

        try {
            String productSql = objectToSql.generateComplexSqlByAnnotation(Product.class, queryParameters, position, rowNum);

            String selectSql = "", fromSql = "", whereSql = "", sortNumSql = "";

            String[] sqlParts = erpDao.getSqlPart(productSql, Product.class);
            selectSql = sqlParts[0];
            fromSql = sqlParts[1];
            whereSql = sqlParts[2];
            sortNumSql = sqlParts[3];


            selectSql += ", " + erpDao.getSelectColumns("t13", ProductType.class);
            fromSql += ", " + objectToSql.getTableName(ProductType.class) + " t13 ";
            if (!whereSql.trim().equals("")) {
                whereSql += " and ";
            }
            whereSql += " t13." + objectToSql.getColumn(ProductType.class.getDeclaredField("id")) +
                    " = t." + objectToSql.getColumn(Product.class.getDeclaredField("type"));

            selectSql += ", " + erpDao.getSelectColumns("t14", ProductDescribe.class);
            fromSql += ", " + objectToSql.getTableName(ProductDescribe.class) + " t14 ";
            whereSql += " and t14." + objectToSql.getColumn(ProductDescribe.class.getDeclaredField("id")) +
                    " = t." + objectToSql.getColumn(Product.class.getDeclaredField("describe"));


            selectSql += ", " + erpDao.getSelectColumns("t11", StockInOutDetailProduct.class);
            fromSql += ", " + objectToSql.getTableName(StockInOutDetailProduct.class) + " t11 ";
            whereSql += " and t11." + objectToSql.getColumn(StockInOutDetailProduct.class.getDeclaredField("product")) +
                    " = t." + objectToSql.getColumn(Product.class.getDeclaredField("id"));

            selectSql += ", " + erpDao.getSelectColumns("t111", StockInOutDetail.class);
            fromSql += ", " + objectToSql.getTableName(StockInOutDetail.class) + " t111 ";
            whereSql += " and t111." + objectToSql.getColumn(StockInOutDetail.class.getDeclaredField("id")) +
                    " = t11." + objectToSql.getColumn(StockInOutDetailProduct.class.getDeclaredField("stockInOutDetail"));

            selectSql += ", " + erpDao.getSelectColumns("t22", StockInOut.class);
            fromSql += ", " + objectToSql.getTableName(StockInOut.class) + " t22 ";
            whereSql += " and t22." + objectToSql.getColumn(StockInOut.class.getDeclaredField("id")) +
                    " = t111." + objectToSql.getColumn(StockInOutDetail.class.getDeclaredField("stockInOut"));

            String stockInOutSql = objectToSql.generateSelectSqlByAnnotation(stockInOut);

            stockInOutSql = stockInOutSql.substring(0, stockInOutSql.indexOf(" order by "));
            stockInOutSql = stockInOutSql.replace(" t ", " t22 ").replace(" t.", " t22.");

            if (stockInOutSql.contains(" where ")) {
                String[] parts = stockInOutSql.split(" where ");
                String[] tables = parts[0].split(" from ")[1].split(" t22 ");

                if (tables.length > 1) {
                    fromSql += tables[1];
                }
                whereSql += " and " + parts[1];
            }


            if (whereSql.indexOf(" and") == 0) {
                whereSql = whereSql.substring(" and".length());
            }

            sql = "select " + selectSql + " from " + fromSql + " where " + whereSql + " order by " + sortNumSql;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql;
    }

    private String getProductDescribeSql(String json, int position, int rowNum) {
        Map<String, String> queryParameters = writer.gson.fromJson(json, new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
        String sql = objectToSql.generateComplexSqlByAnnotation(ProductDescribe.class, queryParameters, position, rowNum);

        String[] sqlParts = null;

        try {
            if (sql.contains(" where ")) {
                sqlParts = objectToSql.generateComplexSqlByAnnotation(ProductDescribe.class, queryParameters, position, rowNum).split(" where ");
                sql = sqlParts[0] + " where " + objectToSql.getColumn(ProductDescribe.class.getDeclaredField("editor")) + " is not null and " + sqlParts[1];

            } else {
                sqlParts = objectToSql.generateComplexSqlByAnnotation(ProductDescribe.class, queryParameters, position, rowNum).split(" order by ");
                sql = sqlParts[0] + " where " + objectToSql.getColumn(ProductDescribe.class.getDeclaredField("editor")) + " is not null order by " + sqlParts[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql;
    }

    /**
     *
     售价浮动权限
     1.参照价格：销售人员唯一参照售价以官网标注价格为准。
     2.客户报价规定：销售人员给客户的第一报价必须和官网标注价格一致。
     3.售价浮动标准：普通销售人员可在自主售价浮动权限内决定成交售价，但需尽力维持官网标注价格。
     4.普通销售人员售价浮动权限：
     （1）售价1千以内（含）的商品，无浮动权限，维持原价出售，且商品无证书，如客户需要则另加费用50元。
     （2）售价1~3千内（含）的商品，浮动100元以内，可自主决定。
     （3）售价3~1万（含）的商品，浮动300元以内，可自主决定。
     （4）售价1万~3万（含）的商品，浮动500元以内，可自主决定。
     （5）售价3万~10万（含）的商品，浮动1000元以内，可自主决定。
     （6）售价10万以上的商品，浮动需申请，不可自主决定。
     （7）浮动金额可以直接优惠给客户在销售成交价上体现，也可以折算同等浮动金额销售价的赠品赠予。
     （8）浮动金额超出权限，需向主管以上申请批准。
     5.销售主管售价浮动权限
     （1）拥有普通销售人员售价浮动权限。
     （2）所有商品都可在标价的5%范围内自主决定是否浮动。
     （3）浮动金额超出权限，需向经理以上申请批准。
     6.销售经理售价浮动权限
     （1）所有商品都可在标价的10%范围内自主决定是否浮动。但工作职责包括如何提高商品售价的利润最大化。
     （2）浮动金额超出权限，需向总监以上申请批准。
     7.销售总监售价浮动权限
     （1）所有商品都可在标价的15%范围内自主决定是否浮动。但工作职责主要是商品售价的利润最大化。
     （2）浮动金额超出权限，需向副总经理以上申请批准。
     * @param json
     * @param operator
     * @return
     */
    public String saveOrUpdatePriceChange(String json, String operator) {
        String result = CommonConstant.fail;

        ProductPriceChange priceChange = writer.gson.fromJson(json, ProductPriceChange.class);
        Product queryProduct = new Product();
        queryProduct.setNo(priceChange.getProductNo());
        queryProduct.setFatePrice(priceChange.getPrePrice());
        queryProduct.setState(ErpConstant.product_state_onSale);
        Product product = (Product) erpDao.query(queryProduct).get(0);

        if (product.getFatePrice() > ErpConstant.price_1000) {
            User user = (User)erpDao.getFromRedis(
                    (String) erpDao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + priceChange.getSessionId()));

            if (user != null) {
                String auditEntity = null;

                if (priceChange.getState().compareTo(ErpConstant.product_price_change_state_save) != 0) {
                    String resources = (String)erpDao.getFromRedis(user.getUsername() + CommonConstant.underline + CommonConstant.resources);

                    auditEntity = getPriceChangeAuditEntity(priceChange, product, resources);

                    if (auditEntity == null) {
                        priceChange.setState(ErpConstant.product_price_change_state_use);
                    } else {
                        priceChange.setState(ErpConstant.product_price_change_state_apply);
                        priceChange.setIsAudit(CommonConstant.Y);
                    }
                }

                priceChange.setInputDate(dateUtil.getSecondCurrentTimestamp());
                priceChange.setUser(user);
                if (operator.equals(CommonConstant.save)) {
                    result += erpDao.save(priceChange);
                } else {
                    result += erpDao.updateById(priceChange.getId(), priceChange);
                }

                if (auditEntity != null) {
                    result += launchAuditFlow(auditEntity, priceChange.getId(), priceChange.getNo(),
                            "申请商品：" + product.getNo() + "的销售价格浮动为：" + priceChange.getPrice(),
                            "请审核商品：" + product.getNo() + "的销售价格：" + priceChange.getPrice(),
                            user);
                }

            } else {
                result += CommonConstant.fail + ",查询不到设置申请调价的用户，调价失败";
            }


        } else {
            result += CommonConstant.fail + "," + ErpConstant.price_1000 + "元以下的商品不能调价";
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String getPriceChangeAuditEntity(ProductPriceChange priceChange, Product product, String resources) {
        String entity = null;

        if (resources.contains(ErpConstant.privilege_resource_uri_price_change_saler)){
            if(isSalePriceNeedAudit(priceChange.getPrice(), product.getFatePrice())) {
                entity = AuditFlowConstant.business_price_change_saler;
            }
        }

        if (resources.contains(ErpConstant.privilege_resource_uri_price_change_charger)){
            if(priceChange.getPrice()/product.getFatePrice() < ErpConstant.price_percent_95) {
                entity = AuditFlowConstant.business_price_change_charger;
            }
        }

        if (resources.contains(ErpConstant.privilege_resource_uri_price_change_manager)){
            if(priceChange.getPrice()/product.getFatePrice() < ErpConstant.price_percent_90) {
                entity = AuditFlowConstant.business_price_change_manager;
            }
        }

        if (resources.contains(ErpConstant.privilege_resource_uri_price_change_director)){
            if(priceChange.getPrice()/product.getFatePrice() < ErpConstant.price_percent_85) {
                entity = AuditFlowConstant.business_price_change_director;
            }
        }

        return entity;
    }

    public boolean isSalePriceNeedAudit(Float salePrice, Float price) {
        Float subtractPrice =  new BigDecimal(Float.toString(price)).subtract(new BigDecimal(Float.toString(salePrice))).floatValue();

        if (price > ErpConstant.price_1000 && price <= ErpConstant.price_3000 &&
                subtractPrice <= ErpConstant.subtract_price_100) {
            return false;
        }

        if (price > ErpConstant.price_3000 && price <= ErpConstant.price_10000 &&
                subtractPrice <= ErpConstant.subtract_price_300) {
            return false;
        }

        if (price > ErpConstant.price_10000 && price <= ErpConstant.price_30000 &&
                subtractPrice <= ErpConstant.subtract_price_500) {
            return false;
        }

        if (price > ErpConstant.price_30000 && price <= ErpConstant.price_100000 &&
                subtractPrice <= ErpConstant.subtract_price_1000) {
            return false;
        }

        return true;
    }

    public com.hzg.sys.User getRandomStockOutUser() {
        logger.info("getRandomStockOutUser start:");

        PrivilegeResource privilegeResource = new PrivilegeResource();
        privilegeResource.setUri(ErpConstant.privilege_resource_uri_print_expressWaybill);
        List<com.hzg.sys.User> users = writer.gson.fromJson(sysClient.getUsersByUri(writer.gson.toJson(privilegeResource)),
                new com.google.gson.reflect.TypeToken<List<User>>(){}.getType());

        User user = users.get((int)System.currentTimeMillis()%users.size());
        logger.info("getRandomStockOutUser end, user:" + user.getUsername());
        return user;
    }

    public Integer getProductStockInState(Product product) {
        Integer state;

        int compare = getProductStockInQuantity(product).compareTo(getProductQuantity(product));

        if (compare < 0) {
            state = ErpConstant.product_state_stockIn_part;
        } else {
            state = ErpConstant.product_state_stockIn;
        }

        return state;
    }

    public Integer getProductStockOutState(Product product) {
        Integer state;

        int compare = getProductStockOutQuantity(product).compareTo(getProductQuantity(product));

        if (compare < 0) {
            state = ErpConstant.product_state_stockOut_part;
        } else {
            state = ErpConstant.product_state_stockOut;
        }

        return state;
    }

    public Integer getProductSaleState(Product product) {
        Integer state;

        int compare = getProductSoldQuantity(product).compareTo(getProductQuantity(product));

         if (compare < 0) {
            state = ErpConstant.product_state_sold_part;
        } else {
            state = ErpConstant.product_state_sold;
        }

        return state;
    }

    public Integer getProductOnReturnState(Product product) {
        Integer state;

        int compare = getProductOnReturnQuantity(product).compareTo(getProductQuantity(product));

        if (compare < 0) {
            state = ErpConstant.product_state_onReturnProduct_part;
        } else {
            state = ErpConstant.product_state_onReturnProduct;
        }

        return state;
    }

    public Integer getProductReturnedState(Product product) {
        Integer state;

        int compare = getProductReturnedQuantity(product).compareTo(getProductQuantity(product));

        if (compare < 0) {
            state = ErpConstant.product_state_returnedProduct_part;
        } else {
            state = ErpConstant.product_state_returnedProduct;
        }

        return state;
    }

    /**
     * 获取商品前一个状态
     * 获取商品关联的业务实体，对业务实体按生成时间由近到远顺序排序，其中生成时间第二近的业务实体就是商品前一个业务
     * 关联实体关联实体，进而根据该关联实体以及商品当前的关联实体，以及商品当前状态，可以获取商品的
     * 前一个状态
     * @param product
     * @return
     */
    public Integer getProductPreState(Product product) {
        List<Object> relateObjects = new ArrayList<>();

        relateObjects.add(getLastValidPurchase(product));
        relateObjects.add(getLastValidStockIn(product));
        relateObjects.add(writer.gson.fromJson(orderClient.getLastValidOrderByProduct(writer.gson.toJson(product)), Order.class));
        relateObjects.add(getLastValidStockOut(product));
        relateObjects.add(getLastValidExpressDeliver(product));
        relateObjects.add(writer.gson.fromJson(afterSaleServiceClient.getLastValidReturnProductByProduct(writer.gson.toJson(product)), ReturnProduct.class));

        Iterator<Object> relateObjectIterator = relateObjects.iterator();
        while (relateObjectIterator.hasNext()) {
            Object relateObject = relateObjectIterator.next();
            if (relateObject == null) {
                relateObjectIterator.remove();
            }
        }

        Object[] sortRelateObjects = relateObjects.toArray();
        Object temp;
        for (int i=0; i<sortRelateObjects.length-1; i++) {
            for (int j=0; j<sortRelateObjects.length-i-1; j++){
                if (getEntityInputDate(sortRelateObjects[j+1]).compareTo(getEntityInputDate(sortRelateObjects[j])) > 0) {
                    temp = sortRelateObjects[j];
                    sortRelateObjects[j] = sortRelateObjects[j+1];
                    sortRelateObjects[j+1] = temp;
                }
            }
        }

        Integer state = -1;
        Product dbProduct = (Product) erpDao.queryById(product.getId(), product.getClass());

        if (sortRelateObjects[0] instanceof Purchase && sortRelateObjects.length == 1) {
            if (dbProduct.getState().compareTo(ErpConstant.product_state_purchase) == 0) {
                state = ErpConstant.product_state_purchase;
            } else if (dbProduct.getState().compareTo(ErpConstant.product_state_purchase_pass) == 0) {
                state = ErpConstant.product_state_purchase;
            } else if (dbProduct.getState().compareTo(ErpConstant.product_state_purchase_close) == 0) {
                state = ErpConstant.product_state_purchase_pass;
            }

        } else if (sortRelateObjects[0] instanceof Order && sortRelateObjects[1]  instanceof StockInOut) {
            StockInOut stockInOut = (StockInOut) sortRelateObjects[1];
            if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) < 0) {
                state = ErpConstant.product_state_onSale;
            }
        }

        if (state == -1) {
            if (sortRelateObjects[1] instanceof Purchase) {
                state = ErpConstant.product_state_purchase_close;

            } else if (sortRelateObjects[1] instanceof StockInOut) {
                StockInOut stockInOut = (StockInOut) sortRelateObjects[1];
                if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) < 0) {
                    state = getProductStockInState(product);
                } else {
                    state = getProductStockOutState(product);
                }

            } else if (sortRelateObjects[1] instanceof Order) {
                state = getProductSaleState(product);

            } else if (sortRelateObjects[1] instanceof ExpressDeliver) {
                state = getProductShippedState(product);

            } else if (sortRelateObjects[1] instanceof ReturnProduct) {
                state = getProductReturnedState(product);
            }
        }

        return state;
    }

    public Timestamp getEntityInputDate(Object obj) {
        if (obj instanceof Purchase) {
            return ((Purchase)obj).getInputDate();

        } else if (obj instanceof StockInOut) {
            return ((StockInOut)obj).getInputDate();

        }else if (obj instanceof ExpressDeliver) {
            return ((ExpressDeliver)obj).getInputDate();

        }else if (obj instanceof Order) {
            return ((Order)obj).getDate();

        } else if (obj instanceof ReturnProduct) {
            return ((ReturnProduct)obj).getInputDate();
        }

        return null;
    }

    public Float getProductQuantity(Product product) {
        PurchaseDetailProduct detailProduct = new PurchaseDetailProduct();
        detailProduct.setProduct(product);
        List<PurchaseDetailProduct> detailProducts = erpDao.query(detailProduct);

        if (detailProducts.size() > 0) {
            PurchaseDetail detail = detailProducts.get(0).getPurchaseDetail();

            if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                    detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {
                return  detail.getQuantity();
            } else {
                return 1f;
            }
        } else {
            return 0f;
        }
    }

    public Float getPurchaseQuantityByProduct(Product product) {
        Float quantity = 0f;

        PurchaseDetailProduct detailProduct = new PurchaseDetailProduct();
        detailProduct.setProduct(product);
        List<PurchaseDetailProduct> detailProducts = erpDao.query(detailProduct);

        if (detailProducts.size() > 0) {
            quantity = detailProducts.get(0).getPurchaseDetail().getQuantity();
        }

        return quantity;
    }

    /**
     * 获取入库数量
     * @param product
     * @return
     */
    public Float getProductStockInQuantity(Product product) {
        List<StockInOutDetail> details = getStockInOutDetails(product);

        Float quantity = 0f;
        for (StockInOutDetail detail : details) {
            StockInOut stockInOut = (StockInOut) erpDao.queryById(detail.getStockInOut().getId(), detail.getStockInOut().getClass());

            if (detail.getState().compareTo(ErpConstant.stockInOut_detail_state_finished) == 0) {
                Float itemQuantity;
                if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                        detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {
                    itemQuantity = detail.getQuantity();
                } else {
                    itemQuantity = 1f;
                }

                if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_changeWarehouse) <= 0) {
                    quantity = new BigDecimal(Float.toString(quantity)).add(new BigDecimal(Float.toString(itemQuantity))).floatValue();
                } else {
                    quantity = new BigDecimal(Float.toString(quantity)).subtract(new BigDecimal(Float.toString(itemQuantity))).floatValue();
                }
            }
        }

        return quantity;
    }

    /**
     * 获取出库数量
     *
     * 出库数量计算规则：
     * 由于入库是先入库上次出库商品，因此入库商品可能只包含上次出库商品，也可能同时包含上次出库商品及未入库的商品，
     * 因此一次入库的入库数量 = 前次出库数量 + 未出库商品数量（可能为0）。
     * 因此一次出库入库的实际出库数量为: 1.如果 该次出库数量 >= 该次入库数量， 则 一次出库入库的实际出库数量 = 该次出库数量 - 该次入库数量；
     *                                   2. 如果 该次出库数量 < 该次入库数量， 则 一次出库入库的实际出库数量 = 0；
     *
     * @param product
     * @return
     */
    public Float getProductStockOutQuantity(Product product) {
        List<StockInOutDetail> details = getStockInOutDetails(product);

        int startPosition = 0;
        for (int i = details.size()-1; i >= 0; i--) {
            StockInOut stockInOut = (StockInOut) erpDao.queryById(details.get(i).getStockInOut().getId(), details.get(i).getStockInOut().getClass());
            /**
             * details 里的元素是按最新的排列在最前面,排列方式形如：出库/入库/入库/出库/入库。所以从 list 末尾找第一次出库的元素
             */
            if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) >= 0) {
                startPosition = i;
            }
            break;
        }

        Float quantity = 0f;
        for (int i = startPosition; i >= 0; i--) {
            StockInOut stockInOut = (StockInOut) erpDao.queryById(details.get(i).getStockInOut().getId(), details.get(i).getStockInOut().getClass());

            if (details.get(i).getState().compareTo(ErpConstant.stockInOut_detail_state_finished) == 0) {
                Float itemQuantity;
                if (details.get(i).getUnit().equals(ErpConstant.unit_g) || details.get(i).getUnit().equals(ErpConstant.unit_kg) ||
                        details.get(i).getUnit().equals(ErpConstant.unit_ct) || details.get(i).getUnit().equals(ErpConstant.unit_oz)) {
                    itemQuantity = details.get(i).getQuantity();
                } else {
                    itemQuantity = 1f;
                }

                if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) >= 0) {
                    quantity = new BigDecimal(Float.toString(quantity)).add(new BigDecimal(Float.toString(itemQuantity))).floatValue();
                } else {
                    quantity = new BigDecimal(Float.toString(quantity)).subtract(new BigDecimal(Float.toString(itemQuantity))).floatValue();

                    if (quantity.compareTo(0f) < 0) {
                        quantity = 0f;
                    }
                }
            }
        }

        return quantity;
    }


    public Float getProductSoldQuantity(Product product) {
        Map<String, Float> productSoldQuantity = writer.gson.fromJson(orderClient.getProductSoldQuantity(writer.gson.toJson(product)),
                new com.google.gson.reflect.TypeToken<Map<String, Float>>() {}.getType());
        return productSoldQuantity.get(ErpConstant.product_sold_quantity);
    }

    public Float getProductOnReturnQuantity(Product product) {
        Map<String, Float> productSoldQuantity = writer.gson.fromJson(afterSaleServiceClient.getProductOnReturnQuantity(writer.gson.toJson(product)),
                new com.google.gson.reflect.TypeToken<Map<String, Float>>() {}.getType());
        return productSoldQuantity.get(ErpConstant.product_onReturn_quantity);
    }

    public Float getProductReturnedQuantity(Product product) {
        Map<String, Float> productSoldQuantity = writer.gson.fromJson(afterSaleServiceClient.getProductReturnedQuantity(writer.gson.toJson(product)),
                new com.google.gson.reflect.TypeToken<Map<String, Float>>() {}.getType());
        return productSoldQuantity.get(ErpConstant.product_returned_quantity);
    }

    public StockInOut getLastValidStockIn(Product product) {
        StockInOutDetailProduct detailProduct = new StockInOutDetailProduct();
        detailProduct.setProduct(product);
        List<StockInOutDetailProduct> detailProducts = erpDao.query(detailProduct);

        for (StockInOutDetailProduct ele : detailProducts) {
            StockInOutDetail detail = (StockInOutDetail) erpDao.queryById(ele.getStockInOutDetail().getId(), StockInOutDetail.class);
            if (detail.getStockInOut().getState().compareTo(ErpConstant.stockInOut_state_cancel) != 0 &&
                    detail.getStockInOut().getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) < 0) {
                return detail.getStockInOut();
            }
        }

        return null;
    }

    public StockInOut getLastValidStockOut(Product product) {
        StockInOutDetailProduct detailProduct = new StockInOutDetailProduct();
        detailProduct.setProduct(product);
        List<StockInOutDetailProduct> detailProducts = erpDao.query(detailProduct);

        for (StockInOutDetailProduct ele : detailProducts) {
            StockInOutDetail detail = (StockInOutDetail) erpDao.queryById(ele.getStockInOutDetail().getId(), StockInOutDetail.class);
            if (detail.getStockInOut().getState().compareTo(ErpConstant.stockInOut_state_cancel) != 0 &&
                    detail.getStockInOut().getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) >= 0) {
                return detail.getStockInOut();
            }
        }

        return null;
    }

    public List<StockInOutDetail> getStockInOutDetails(Product product) {
        StockInOutDetailProduct queryDetailProduct = new StockInOutDetailProduct();
        queryDetailProduct.setProduct(product);
        List<StockInOutDetailProduct> detailProducts = erpDao.query(queryDetailProduct);

        List<StockInOutDetail> details = new ArrayList<>();

        for (StockInOutDetailProduct detailProduct : detailProducts) {
            boolean isSameDetail = false;

            for (StockInOutDetail detail : details) {
                if (detail.getId().compareTo(detailProduct.getStockInOutDetail().getId()) == 0) {
                    isSameDetail = true;
                }
            }

            if (!isSameDetail) {
                details.add(detailProduct.getStockInOutDetail());
            }
        }

        return details;
    }

    public String changeProductState(List<Integer> productIds, Integer allowState, Integer toState) {
        String result = CommonConstant.fail;

        for (Integer id : productIds) {
            Product stateProduct = (Product) erpDao.queryById(id, Product.class);
            if (stateProduct.getState().compareTo(allowState) != 0) {
                result += CommonConstant.fail;

                if (allowState.compareTo(ErpConstant.product_state_edit) == 0 ||
                        allowState.compareTo(ErpConstant.product_state_returnedProduct) == 0 ||
                        allowState.compareTo(ErpConstant.product_state_returnedProduct_part) == 0 ) {
                    result += ", 商品 " + stateProduct.getNo() + "不是编辑或退货状态，不能上架商品";

                } else if (allowState.compareTo(ErpConstant.product_state_onSale) == 0) {
                    result += ", 商品 " + stateProduct.getNo() + "不是在售状态，不能下架商品";
                }

                break;
            }
        }

        if (!result.contains(CommonConstant.fail+CommonConstant.fail)) {
            for (Integer id : productIds) {
                Product product = (Product) erpDao.queryById(id, Product.class);
                product.setState(toState);
                result += erpDao.updateById(id, product);
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public StockInOut queryStockInOut(Integer id) {
        StockInOut stockInOut = (StockInOut) erpDao.queryById(id, StockInOut.class);

        for (StockInOutDetail detail : stockInOut.getDetails()) {
            StockInOutDetailProduct detailProduct = new StockInOutDetailProduct();
            detailProduct.setStockInOutDetail(detail);
            detail.setStockInOutDetailProducts(new HashSet<>(erpDao.query(detailProduct)));
        }

        return stockInOut;
    }

    public String isCanStockIn(StockInOut stockIn) {
        String result = null;

        for (StockInOutDetail detail : stockIn.getDetails()) {
            for (StockInOutDetailProduct detailProduct : detail.getStockInOutDetailProducts()) {
                Product dbProduct = (Product) erpDao.queryById(detailProduct.getProduct().getId(), detailProduct.getProduct().getClass());

                if (dbProduct.getState().compareTo(ErpConstant.product_state_purchase_close) != 0 &&
                    dbProduct.getState().compareTo(ErpConstant.product_state_stockIn_part) != 0 &&
                    dbProduct.getState().compareTo(ErpConstant.product_state_stockOut) != 0 &&
                    dbProduct.getState().compareTo(ErpConstant.product_state_stockOut_part) != 0 &&
                    dbProduct.getState().compareTo(ErpConstant.product_state_onReturnProduct_part) != 0 &&
                    dbProduct.getState().compareTo(ErpConstant.product_state_returnedProduct) != 0 &&
                    dbProduct.getState().compareTo(ErpConstant.product_state_returnedProduct_part) != 0) {
                    if (detail.getStockInOutDetailProducts().size() > 1) {
                        result = CommonConstant.fail + ",编号：" + dbProduct.getNo() + " 的" + detail.getStockInOutDetailProducts().size() + "件商品中，" +
                                "有不是采购完成、出库或已退货状态的商品，不能入库";
                    } else {
                        result = CommonConstant.fail + ",编号：" + dbProduct.getNo() + " 的商品不是采购完成、出库或已退货状态，不能入库";
                    }

                    break;
                }
            }
        }

        return result;
    }

    public String isCanStockOut(StockInOut stockOut) {
        String result = null;

        for (StockInOutDetail detail : stockOut.getDetails()) {
            for (StockInOutDetailProduct detailProduct : detail.getStockInOutDetailProducts()) {
                Product dbProduct = (Product) erpDao.queryById(detailProduct.getProduct().getId(), detailProduct.getProduct().getClass());

                if (dbProduct.getState().compareTo(ErpConstant.product_state_stockIn) != 0 &&
                    dbProduct.getState().compareTo(ErpConstant.product_state_stockIn_part) != 0 &&
                    dbProduct.getState().compareTo(ErpConstant.product_state_stockOut_part) != 0 &&
                    dbProduct.getState().compareTo(ErpConstant.product_state_sold) != 0 &&
                    dbProduct.getState().compareTo(ErpConstant.product_state_sold_part) != 0 &&
                    dbProduct.getState().compareTo(ErpConstant.product_state_onReturnProduct_part) != 0 &&
                    dbProduct.getState().compareTo(ErpConstant.product_state_returnedProduct_part) != 0) {
                    result = CommonConstant.fail + ",编号：" + dbProduct.getNo() + " 的商品不是入库或已售状态，不能出库";
                    break;
                }
            }
        }

        return result;
    }

    public String isCanSaveStockInOut(StockInOut stockInOut) {
        String result = "";

        for (StockInOutDetail detail : stockInOut.getDetails()) {
            for (StockInOutDetailProduct detailProduct : detail.getStockInOutDetailProducts()) {
                List<StockInOutDetailProduct> dbDetailProducts = erpDao.query(detailProduct);

                for (StockInOutDetailProduct dbDetailProduct : dbDetailProducts) {
                    StockInOutDetail dbDetail = (StockInOutDetail) erpDao.queryById(dbDetailProduct.getStockInOutDetail().getId(), dbDetailProduct.getStockInOutDetail().getClass());
                    if (dbDetail.getStockInOut().getState().compareTo(ErpConstant.stockInOut_state_apply) == 0) {
                        result += CommonConstant.fail + ",申请单：" + dbDetail.getStockInOut().getNo() +"已经含有编号：" + dbDetail.getProductNo() +
                                " 的商品，不能重复申请入库或出库";
                        break;
                    }
                }

                Product dbProduct = (Product) erpDao.queryById(detailProduct.getProduct().getId(), detailProduct.getProduct().getClass());

                if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_changeWarehouse) <= 0) {
                    Float totalStockInQuantity = new BigDecimal(Float.toString(getProductStockInQuantity(detailProduct.getProduct()))).
                            add(new BigDecimal(Float.toString(detail.getQuantity()))).floatValue();

                    if (totalStockInQuantity.compareTo(getPurchaseQuantityByProduct(detailProduct.getProduct())) > 0) {
                        result += CommonConstant.fail + ",商品:" + dbProduct.getNo() + "入库数量大于商品采购数量，不能入库";
                        break;
                    }
                }

                if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) >= 0) {
                    Float totalStockOutQuantity = new BigDecimal(Float.toString(getProductStockOutQuantity(detailProduct.getProduct()))).
                            add(new BigDecimal(Float.toString(detail.getQuantity()))).floatValue();

                    if (totalStockOutQuantity.compareTo(getProductStockInQuantity(detailProduct.getProduct())) > 0) {
                        result += CommonConstant.fail + ",商品:" + dbProduct.getNo() + "出库数量大于商品入库数量，不能出库";
                        break;
                    }
                }

            }
        }

        if (result == null) {
            if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_virtual_outWarehouse) < 0) {
                String msg = isCanStockIn(stockInOut);
                if (msg != null) {
                    result += msg;
                }
            } else {
                String msg = isCanStockOut(stockInOut);
                if (msg != null) {
                    result += msg;
                }
            }
        }

        return result;
    }

    public User getUserBySessionId(String sessionId){
        return (User)erpDao.getFromRedis((String)erpDao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId));
    }

    public String generateBarcodes(StockInOut stockInOut) {
        String barcodesImage = "<table border='1' cellpadding='0' cellspacing='0'><tr>";

        StockInOutDetail[] details = new StockInOutDetail[stockInOut.getDetails().size()];
        stockInOut.getDetails().toArray(details);
        int k = 0;

        for (int i = 0; i < details.length; i++) {
            StockInOutDetailProduct detailProduct = new StockInOutDetailProduct();
            detailProduct.setStockInOutDetail(details[i]);

            List<StockInOutDetailProduct> detailProducts = erpDao.query(detailProduct);
            for (int j = 0; j < detailProducts.size(); j++) {

                Product product = (Product) erpDao.queryById(detailProducts.get(j).getProduct().getId(), detailProducts.get(j).getProduct().getClass());
                barcodesImage += "<td  align='center' style='padding:10px'><img src='data:image/png;base64," + imageBase64.imageToBase64(barcodeUtil.generate(String.valueOf(product.getId()))) + "'/><br/>" +
                        product.getNo() + "</td>";

                if (k % 4 == 0 && k != 0) {
                    barcodesImage += "</tr>";

                    if (j < detailProducts.size()-1 || i < details.length-1) {
                        barcodesImage += "<tr>";
                    }
                }

                k++;
            }
        }

        barcodesImage += "</tr></table>";
        return barcodesImage;
    }

    public String downloadSfExpressWayBillByStockInOut(StockInOut stockOut) {
        String printContent = "";

        printContent = downloadSfWaybill(getSfExpressOrderByStockInOut(stockOut));
        if (printContent == "") {
            printContent += "暂时查询不到快递单，无法打印，请30秒后重试<br/><br/>";
        }

        return printContent;
    }

    public List<ExpressDeliverDetail> getSfExpressWayBillsByStockInOut(StockInOut stockOut) {
        List<ExpressDeliverDetail> details = new ArrayList<>();

        ExpressDeliver expressDeliver = getSfExpressOrderByStockInOut(stockOut);
        for (ExpressDeliverDetail dbDetail : expressDeliver.getDetails()) {
            dbDetail = getSfWaybillInfo(dbDetail);

            if (!dbDetail.getResult().equals("3")) {
                dbDetail.setExpressDeliver(expressDeliver);
                //防止嵌套
                dbDetail.getExpressDeliver().setDetails(null);

                details.add(dbDetail);
            }
        }

        return details;
    }

    /**
     * 根据收件人及出库单产生顺丰快递单
     * @param receiverInfo
     * @param stockOut
     * @return
     */
    public String generateSfExpressOrderByReceiverAndStockOut(ExpressDeliver receiverInfo, StockInOut stockOut){
        logger.info("generateSfExpressOrderByReceiverAndStockOut start: receiverInfo:" + receiverInfo.toString() + ", stockOut:" + stockOut.getId());
        String result = "";

        /**
         * 重复打印时，由于第一次已经生成快递单，不再需要生成顺丰快递单
         */
        ExpressDeliver expressDeliver = getSfExpressOrderByStockInOut(stockOut);
        if (!isReceiverInfoSame(receiverInfo, expressDeliver)) {
            List<ExpressDeliverDetail> details = expressDeliverOrder(generateExpressDeliver(receiverInfo, stockOut));

            if (!details.isEmpty()) {
                result += CommonConstant.success;
            } else {
                result += CommonConstant.fail;
            }

        } else {
            result = CommonConstant.success;
        }

        logger.info("generateSfExpressOrderByReceiverAndStockOut end");
        return result;
    }

    public ExpressDeliver generateExpressDeliver(ExpressDeliver receiverInfo, StockInOut stockOut) {
        logger.info("generateExpressDeliver start, receiverInfo:" + receiverInfo.toString() + ",  stockInOut:" + stockOut.toString());

        User sender = (User) erpDao.queryById(stockOut.getInputer().getId(), stockOut.getInputer().getClass());
        Warehouse warehouse = (Warehouse) erpDao.queryById(stockOut.getWarehouse().getId(), stockOut.getWarehouse().getClass());

        ExpressDeliver expressDeliver = new ExpressDeliver();
        expressDeliver.setDeliver(ErpConstant.deliver_sfExpress);
        expressDeliver.setType(ErpConstant.deliver_sfExpress_type);
        expressDeliver.setDate(receiverInfo.getDate());

        expressDeliver.setReceiver(receiverInfo.getReceiver());
        expressDeliver.setReceiverAddress(receiverInfo.getReceiverAddress());
        expressDeliver.setReceiverCity(receiverInfo.getReceiverCity());
        expressDeliver.setReceiverProvince(receiverInfo.getReceiverProvince());
        expressDeliver.setReceiverCountry(receiverInfo.getReceiverCountry());
        expressDeliver.setReceiverCompany(receiverInfo.getReceiverCompany());
        expressDeliver.setReceiverMobile(receiverInfo.getReceiverMobile());
        expressDeliver.setReceiverTel(receiverInfo.getReceiverTel());
        expressDeliver.setReceiverPostCode(receiverInfo.getReceiverPostCode());

        expressDeliver.setSender(sender.getName());
        expressDeliver.setSenderAddress(warehouse.getCompany().getAddress());
        expressDeliver.setSenderCity(warehouse.getCompany().getCity());
        expressDeliver.setSenderProvince(warehouse.getCompany().getProvince());
        expressDeliver.setSenderCountry(warehouse.getCompany().getCountry());
        expressDeliver.setSenderCompany(warehouse.getCompany().getName());
        expressDeliver.setSenderMobile(sender.getMobile());
        expressDeliver.setSenderTel(warehouse.getCompany().getPhone());
        expressDeliver.setSenderPostCode(warehouse.getCompany().getPostCode());

        Set<ExpressDeliverDetail> deliverDetails = new HashSet<>();

        for (StockInOutDetail detail : stockOut.getDetails()) {
            StockInOutDetailProduct detailProduct = new StockInOutDetailProduct();
            detailProduct.setStockInOutDetail(detail);

            List<StockInOutDetailProduct> detailProducts = erpDao.query(detailProduct);
            Product product = (Product) erpDao.queryById(detailProducts.get(0).getProduct().getId(), detailProducts.get(0).getProduct().getClass());

            ExpressDeliverDetail expressDeliverDetail = new ExpressDeliverDetail();
            expressDeliverDetail.setProductNo(product.getNo());
            expressDeliverDetail.setQuantity(detail.getQuantity());
            expressDeliverDetail.setUnit(detail.getUnit());
            expressDeliverDetail.setPrice(product.getFatePrice());

            Set<ExpressDeliverDetailProduct> deliverDetailProducts = new HashSet<>();
            for (StockInOutDetailProduct ele : detailProducts) {
                ExpressDeliverDetailProduct deliverDetailProduct = new ExpressDeliverDetailProduct();
                deliverDetailProduct.setProduct(ele.getProduct());
                deliverDetailProducts.add(deliverDetailProduct);
            }

            expressDeliverDetail.setExpressDeliverDetailProducts(deliverDetailProducts);
            deliverDetails.add(expressDeliverDetail);
            expressDeliver.setDetails(deliverDetails);
        }

        logger.info("generateExpressDeliver end");
        return expressDeliver;
    }

    boolean isReceiverInfoSame (ExpressDeliver receiverInfo, ExpressDeliver expressDeliver) {
        try {
            if (receiverInfo.getReceiver().equals(expressDeliver.getReceiver()) &&
                    receiverInfo.getReceiverAddress().equals(expressDeliver.getReceiverAddress()) &&
                    receiverInfo.getReceiverCity().equals(expressDeliver.getReceiverCity()) &&
                    receiverInfo.getReceiverCountry().equals(expressDeliver.getReceiverCountry()) &&
                    receiverInfo.getReceiverProvince().equals(expressDeliver.getReceiverProvince()) &&
                    dateUtil.getSimpleDateFormat().format(receiverInfo.getDate()).equals(dateUtil.getSimpleDateFormat().format(expressDeliver.getDate()))) {

                if (receiverInfo.getReceiverMobile() != null && expressDeliver.getReceiverMobile() != null) {
                    if (receiverInfo.getReceiverMobile().equals(expressDeliver.getReceiverMobile())) {
                        return true;
                    }
                }

                if (receiverInfo.getReceiverTel() != null && expressDeliver.getReceiverTel() != null) {
                    if (receiverInfo.getReceiverTel().equals(expressDeliver.getReceiverTel())) {
                        return true;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public List<ExpressDeliverDetail> expressDeliverOrders(List<ExpressDeliver> expressDelivers) {
        List<ExpressDeliverDetail> details = new ArrayList<>();
        for (ExpressDeliver expressDeliver : expressDelivers) {
            details.addAll(expressDeliverOrder(expressDeliver));
        }
        return details;
    }

    public List<ExpressDeliverDetail> expressDeliverOrder(ExpressDeliver expressDeliver) {
        List<ExpressDeliverDetail> details = new ArrayList<>();

        expressDeliver.setState(ErpConstant.express_state_sending);
        expressDeliver.setInputDate(dateUtil.getSecondCurrentTimestamp());
        String result = erpDao.save(expressDeliver);

        for (ExpressDeliverDetail expressDeliverDetail : expressDeliver.getDetails()) {
            expressDeliverDetail.setExpressNo(ErpConstant.no_expressDelivery_perfix + erpDao.getSfTransMessageId());
            expressDeliverDetail.setState(ErpConstant.express_detail_state_unSend);
            expressDeliverDetail.setExpressDeliver(expressDeliver);
            expressDeliverDetail.setWeight(getExpressDeliverDetailWeight(expressDeliverDetail));
            result += erpDao.save(expressDeliverDetail);

            for (ExpressDeliverDetailProduct detailProduct : expressDeliverDetail.getExpressDeliverDetailProducts()) {
                detailProduct.setExpressDeliverDetail(expressDeliverDetail);
                result += erpDao.save(detailProduct);
            }
        }

        if (!result.contains(CommonConstant.fail)) {
            details.addAll(sfBspOrder(expressDeliver));
        }

        /**
         * 防止循环嵌套
         */
        for (ExpressDeliverDetail detail : details) {
            detail.getExpressDeliver().setDetails(null);

            for (ExpressDeliverDetailProduct detailProduct : detail.getExpressDeliverDetailProducts()) {
                detailProduct.getExpressDeliverDetail().setExpressDeliverDetailProducts(null);
            }
        }

        return details;
    }

/*    public String expressDeliverOrder(ExpressDeliver expressDeliver) {
        String result = CommonConstant.fail;

        expressDeliver.setState(ErpConstant.express_state_sending);
        expressDeliver.setInputDate(dateUtil.getSecondCurrentTimestamp());
        result += erpDao.save(expressDeliver);

        for (ExpressDeliverDetail detail : expressDeliver.getDetails()) {
            detail.setExpressNo(ErpConstant.no_expressDelivery_perfix + erpDao.getSfTransMessageId());
            detail.setState(ErpConstant.express_detail_state_unSend);
            detail.setExpressDeliver(expressDeliver);
            detail.setWeight(getExpressDeliverDetailWeight(detail));
            result += erpDao.save(detail);

            for (ExpressDeliverDetailProduct expressDeliverDetailProduct : detail.getExpressDeliverDetailProducts()) {
                expressDeliverDetailProduct.setExpressDeliverDetail(detail);
                result += erpDao.save(expressDeliverDetailProduct);
            }
        }

        result += sfExpressOrder(expressDeliver);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }*/

    public List<OrderQueryRespDto> sfExpressOrderQuery(ExpressDeliver expressDeliver) {
        logger.info("sfExpressOrderQuery start");

        List<OrderQueryRespDto> orderQueryRespDtos = new ArrayList<>();
        for (ExpressDeliverDetail detail : expressDeliver.getDetails()) {
            OrderQueryRespDto orderQueryRespDto = sfExpressOrderQuery(detail.getExpressNo());

            if (orderQueryRespDto != null) {
                orderQueryRespDtos.add(orderQueryRespDto);
            }
        }

        logger.info("sfExpressOrderQuery end");
        return  orderQueryRespDtos;
    }

    /**
     * 下载出库商品快递单
     * @param expressDeliver
     * @return
     */
    public String downloadSfWaybill(ExpressDeliver expressDeliver) {
        logger.info("downloadSfWaybill start:" + expressDeliver.getId());
        String sfWaybills = "";

        List<OrderQueryRespDto> orderQueryRespDtos = sfExpressOrderQuery(expressDeliver);
        if (!orderQueryRespDtos.isEmpty()){
            for (OrderQueryRespDto orderQueryRespDto : orderQueryRespDtos) {

                if (orderQueryRespDto.getMailNo() != null) {
                    String sfWaybill = downloadSfWaybill(orderQueryRespDto.getOrderId());
                    if (sfWaybill.contains("<img")) {
                        sfWaybills += sfWaybill;

                        ExpressDeliverDetail expressDeliverDetail = new ExpressDeliverDetail();
                        expressDeliverDetail.setExpressNo(orderQueryRespDto.getOrderId());
                        ExpressDeliverDetail dbExpressDeliverDetail = (ExpressDeliverDetail) erpDao.query(expressDeliverDetail).get(0);
                        expressDeliverDetail.setState(ErpConstant.express_detail_state_sended);
                        expressDeliverDetail.setId(dbExpressDeliverDetail.getId());
                        erpDao.updateById(expressDeliverDetail.getId(), expressDeliverDetail);
                    }
                }
            }
        }

        logger.info("downloadSfWaybill end");
        return sfWaybills;
    }

    public ExpressDeliver getLastValidExpressDeliver(Product product) {
        ExpressDeliverDetailProduct detailProduct = new ExpressDeliverDetailProduct();
        detailProduct.setProduct(product);
        List<ExpressDeliverDetailProduct> detailProducts = erpDao.query(detailProduct);

        for (ExpressDeliverDetailProduct ele : detailProducts) {
            ExpressDeliverDetail detail = (ExpressDeliverDetail) erpDao.queryById(ele.getExpressDeliverDetail().getId(), ExpressDeliverDetail.class);
            return detail.getExpressDeliver();
        }

        return null;
    }




    /**
     *
     *  ========================= 顺丰丰桥 bsp 接口 =======================
     *
     */

    /**
     * sf bsp 下单
     * @param expressDeliver
     * @return
     */
    public List<ExpressDeliverDetail> sfBspOrder(ExpressDeliver expressDeliver) {
        logger.info("bspOrder start, expressDeliver:" + expressDeliver.toString());

        ExpressDeliverDetail[] detailsArr = new ExpressDeliverDetail[expressDeliver.getDetails().size()];
        expressDeliver.getDetails().toArray(detailsArr);
        List<ExpressDeliverDetail> details = new ArrayList<>();

        int i = 0;
        int tryCount = 0;
        while(i < detailsArr.length){
            String result = sfBspOrder(setSfBspOrderXml(expressDeliver, detailsArr[i]));

            if (result.contains("<Head>OK</Head>")) {
                JSONObject orderResponse = XML.toJSONObject(result).getJSONObject("Response").getJSONObject("Body").getJSONObject("OrderResponse");

                detailsArr[i].setExpressNo(orderResponse.get("orderid").toString());
                detailsArr[i].setMailNo(orderResponse.get("mailno").toString());
                if (result.contains("origincode")) {
                    detailsArr[i].setOrigin(orderResponse.get("origincode").toString());
                }
                if (result.contains("destcode")) {
                    detailsArr[i].setDest(orderResponse.get("destcode").toString());

                }
                detailsArr[i].setResult(orderResponse.get("filter_result").toString());

                details.add(detailsArr[i]);

                ++i;
                tryCount = 0;

            } else {
                ++tryCount;

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            /**
             * 重试次数为 3 次
             */
            if (tryCount == 3) {
                ++i;
                tryCount = 0;
            }
        }

        logger.info("bspOrder end ");
        return details;
    }

    public String setSfBspOrderXml(ExpressDeliver expressInfo, ExpressDeliverDetail detail) {
        String orderXml = "<Request service='" + ErpConstant.sf_bsp_orderService + "' lang='zh-CN'>" +
                "<Head>" + sfExpress.getCustCode() + "</Head>" +
                "<Body>" +
                "<Order orderid ='" + detail.getExpressNo() + "' " +
                "j_company='" + expressInfo.getSenderCompany() + "' " +
                "j_contact='" + expressInfo.getSender() + "' " +
                "j_tel='" + expressInfo.getSenderTel() + "' " +
                "j_mobile='" + expressInfo.getSenderMobile() + "' " +
                "j_province='" + expressInfo.getSenderProvince() + "' " +
                "j_city='" + expressInfo.getSenderCity() + "' " +
                "j_address='" + expressInfo.getSenderAddress() + "' " +
                "d_company='" + expressInfo.getReceiverCompany() + "' " +
                "d_contact='" + expressInfo.getReceiver() + "' " +
                "d_tel='" + expressInfo.getReceiverTel() + "' " +
                "d_mobile='" + expressInfo.getReceiverMobile() + "' ";

        if (expressInfo.getReceiverProvince() != null) {
            orderXml += "d_province='" + expressInfo.getReceiverProvince() + "' ";
        }
        if (expressInfo.getReceiverCity() != null) {
            orderXml += "d_city='" + expressInfo.getReceiverCity() + "' ";
        }

        orderXml += "d_address='" + expressInfo.getReceiverAddress() + "' " +
                "express_type='1' " +
                "pay_method='" + ErpConstant.sf_bsp_payMethod + "' " +
                "custid='" + sfExpress.getCustId() + "' " +
                "parcel_quantity='1' " +
                "sendstarttime='" + dateUtil.getSimpleDateFormat().format(expressInfo.getDate()) + "'>" +
                "<Cargo Name='" + detail.getProductNo() + "' " +
                "count='" + getExpressDeliverDetailCount(detail) + "' " +
                "unit='" + getExpressDeliverDetailUnit(detail) + "' ";

        if (detail.getWeight() != null) {
            orderXml += "weight='" + detail.getWeight() + "' ";
        }

        orderXml += "amount='" + detail.getPrice() + "' " +
                "currency='CNY' " +
                "source_area='中国'></Cargo>";

        if (detail.getInsure() != null) {
            orderXml += "<AddedService name='INSURE' value='" + Integer.toString((int)Math.rint(detail.getInsure().doubleValue())) + "'></AddedService>";
        }

        orderXml += "</Order></Body></Request>";

        return orderXml;
    }

    public String sfBspOrder(String orderXml) {
        logger.info("bspOrder start, orderXml:" + orderXml);
        String result = sfBspHttpRequest(orderXml);
        logger.info("bspOrder end, result:" + result);
        return result;
    }


    /**
     * sf bsp 查询订单
     * @param detail
     * @return
     */
    public ExpressDeliverDetail sfBspOrderQuery(ExpressDeliverDetail detail) {
        String result = sfBspOrderQuery(setSfBspOrderQueryXml(detail));

//        String result = result = "<?xml version='1.0' encoding='UTF-8'?><Response service=\"OrderService\"><Head>OK</Head><Body><OrderResponse filter_result=\"2\" destcode=\"010\" mailno=\"444000094285\" origincode=\"871\" orderid=\"ED201801051134280001\"/></Body></Response>";

        if (result.contains("<Head>OK</Head>")) {
            JSONObject orderResponse = XML.toJSONObject(result).getJSONObject("Response").getJSONObject("Body").getJSONObject("OrderResponse");

            detail.setExpressNo(orderResponse.get("orderid").toString());
            detail.setMailNo(orderResponse.get("mailno").toString());
            detail.setOrigin(orderResponse.get("origincode").toString());
            detail.setDest(orderResponse.get("destcode").toString());
            detail.setResult(orderResponse.get("filter_result").toString());
        }

        return detail;
    }

    public String setSfBspOrderQueryXml(ExpressDeliverDetail detail) {
        return  "<Request service='" + ErpConstant.sf_bsp_OrderSearchService + "' lang='zh-CN'>" +
                "<Head>" + sfExpress.getCustCode() + "</Head>" +
                "<Body>" +
                "<OrderSearch orderid='" + detail.getExpressNo() + "'/>" +
                "</Body>" +
                "</Request>";
    }

    public String sfBspOrderQuery(String orderQueryXml) {
        logger.info("bspOrderQuery start, orderCancelXml:" + orderQueryXml);
        String result = sfBspHttpRequest(orderQueryXml);
        logger.info("bspOrderQuery end, result:" + result);
        return result;
    }



    /**
     * sf bsp 取消订单
     * @param detail
     * @return
     */
    public String sfBspOrderCancel(ExpressDeliverDetail detail) {
        return sfBspOrderCancel(setSfBspOrderCancelXml(detail));
    }

    public String setSfBspOrderCancelXml(ExpressDeliverDetail detail) {
        return  "<Request service='" + ErpConstant.sf_bsp_orderConfirmService +  "' lang='zh-CN'>" +
                "<Head>" + sfExpress.getCustCode() + "</Head>" +
                "<Body>" +
                "<OrderConfirm " +
                "orderid='" + detail.getExpressNo() + "' " +
                "dealtype='2'>" +
                "</OrderConfirm>" +
                "</Body>" +
                "</Request>";
    }

    public String sfBspOrderCancel(String orderCancelXml) {
        logger.info("bspOrderCancel start, orderCancelXml:" + orderCancelXml);

        String result = sfBspHttpRequest(orderCancelXml);

        if (result.contains("<Head>OK</Head>")) {
            result = XML.toJSONObject(result).getJSONObject("Response").getJSONObject("Body").getJSONObject("OrderConfirmResponse").
                    get("res_status").toString().equals("2") ? CommonConstant.success : CommonConstant.fail;
        } else {
            result = XML.toJSONObject(result).getJSONObject("ERROR").getString("content");
        }

        logger.info("bspOrderCancel end, result:" + result);
        return result;
    }


    public String sfBspHttpRequest(String requestXml) {
        logger.info("sfBspHttpRequest start, requestXml:" + requestXml);

        String result = "<Head>ERR</Head>";
        try {
            result = httpClientUtil.postSFAPI(sfExpress.getBspUrl(), requestXml, verifyCodeUtil.md5EncryptAndBase64(requestXml+sfExpress.getBspCheckWord()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("sfBspHttpRequest end, result:" + result);
        return result;
    }

    public ExpressDeliverDetail getSfWaybillInfo(ExpressDeliverDetail detail) {
        return setWaybillinfo(sfBspOrderQuery(detail));
    }

    public ExpressDeliverDetail setWaybillinfo(ExpressDeliverDetail detail) {
        String qzoneMailNo = "";
        for (int i = 0; i < detail.getMailNo().length(); i++) {
            qzoneMailNo += detail.getMailNo().charAt(i);
            if ((i + 1) % 3 == 0) {
                qzoneMailNo += " ";
            }
        }
        detail.setQzoneMailNo(qzoneMailNo);
        detail.setParcelQuantity(1);
        detail.setMailNoBarcode(imageBase64.imageToBase64(barcodeUtil.generate128C(String.valueOf(detail.getMailNo()))));
        detail.setPayType(ErpConstant.sf_bsp_payMethod_name);
        detail.setCustId(sfExpress.getCustId());
        return detail;
    }



    /**
     *
     *  ========================= 顺丰开放平台接口 =======================
     *
     */


    /**
     * 下单
     * @param expressDeliver
     * @return
     */
    public List<String> sfExpressOrder(ExpressDeliver expressDeliver) {
        logger.info("sfExpressOrder start, details:" + expressDeliver.toString());

        List<String> expressNos = new ArrayList<>();

        //设置 uri
        String url = sfExpress.getOrderUri();
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId(sfExpress.getAppId());
        appInfo.setAppKey(sfExpress.getAppKey());
        appInfo.setAccessToken(getSfToken());

        for (ExpressDeliverDetail detail : expressDeliver.getDetails()) {
            //设置请求头
            MessageReq req = new MessageReq();
            HeadMessageReq head = new HeadMessageReq();
            head.setTransType(ErpConstant.sf_action_code_order);
            head.setTransMessageId(detail.getExpressNo().replace(ErpConstant.no_expressDelivery_perfix, ""));
            req.setHead(head);

            OrderReqDto orderReqDto = new OrderReqDto();
            orderReqDto.setOrderId(detail.getExpressNo());
            orderReqDto.setExpressType((short) 1);
            orderReqDto.setPayMethod((short) 1);
            orderReqDto.setNeedReturnTrackingNo((short) 0);
            orderReqDto.setIsDoCall((short) 1);
            orderReqDto.setIsGenBillNo((short) 1);
            orderReqDto.setCustId(sfExpress.getCustId());
            /**
             * 月结卡号对应的网点，如果付款方式为第三方月结卡号支付(即payMethod=3)，则必填
             */
//            orderReqDto.setPayArea(sfExpress.getPayArea());
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
            cargoInfoDto.setCargo(detail.getProductNo());
            cargoInfoDto.setCargoCount(getExpressDeliverDetailCount(detail).toString());
            cargoInfoDto.setCargoUnit(getExpressDeliverDetailUnit(detail));
            cargoInfoDto.setCargoWeight(Float.toString(getExpressDeliverDetailWeight(detail)));
            cargoInfoDto.setCargoAmount(Integer.toString((int)Math.rint(detail.getPrice().doubleValue())));

            orderReqDto.setDeliverInfo(deliverInfoDto);
            orderReqDto.setConsigneeInfo(consigneeInfoDto);
            orderReqDto.setCargoInfo(cargoInfoDto);

            //增值服务，商品保价
            if (detail.getInsure() != null && detail.getInsure().compareTo(0f) > 0) {
                AddedServiceDto insureServiceDto = new AddedServiceDto();
                insureServiceDto.setName(ErpConstant.sf_added_service_name_insure);
                insureServiceDto.setValue(Integer.toString((int)Math.rint(detail.getInsure().doubleValue())));

                orderReqDto.setAddedServices(new ArrayList());
                orderReqDto.getAddedServices().add(insureServiceDto);
            }

            req.setBody(orderReqDto);

            System.out.println("传入参数" + ToStringBuilder.reflectionToString(req));
            MessageResp<OrderRespDto> messageResp = null;
            try {
                messageResp = OrderTools.order(url, appInfo, req);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (messageResp.getHead().getCode().equals(ErpConstant.sf_response_code_success) &&
                    messageResp.getBody().getRemarkCode().equals("01")) {
                expressNos.add(detail.getExpressNo());
            } else {
                logger.info(messageResp.getHead().getMessage());
            }
        }

        logger.info("sfExpressOrder end, " + Arrays.toString(expressNos.toArray()));
        return expressNos;
    }

    /**
     * sf快递单订单查询
     * @param expressNo
     * @return
     */
    public OrderQueryRespDto sfExpressOrderQuery(String expressNo) {
        String url = sfExpress.getOrderQueryUri();
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId(sfExpress.getAppId());
        appInfo.setAppKey(sfExpress.getAppKey());
        appInfo.setAccessToken(getSfToken());

        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(ErpConstant.sf_action_code_query_order);
        head.setTransMessageId(erpDao.getSfTransMessageId());
        req.setHead(head);

        OrderQueryReqDto oderQueryReqDto = new OrderQueryReqDto();
        oderQueryReqDto.setOrderId(expressNo);
        req.setBody(oderQueryReqDto);

        MessageResp<OrderQueryRespDto> messageResp = null;
        try {
            messageResp = OrderTools.orderQuery(url, appInfo, req);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (messageResp.getHead().getCode().equals(ErpConstant.sf_response_code_success)) {
            updateMailNo(expressNo, messageResp.getBody().getMailNo());
            return messageResp.getBody();
        } else {
            return null;
        }
    }

    public void updateMailNo(String expressNo, String mailNo) {
        try {
            ExpressDeliverDetail deliverDetail = new ExpressDeliverDetail();
            deliverDetail.setExpressNo(expressNo);
            ExpressDeliverDetail dbDeliverDetail = (ExpressDeliverDetail) erpDao.query(deliverDetail).get(0);
            dbDeliverDetail.setMailNo(mailNo);
            erpDao.updateById(dbDeliverDetail.getId(), dbDeliverDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载顺丰快递单
     * @param expressNo
     * @return
     */
    public String downloadSfWaybill(String expressNo) {
        String sfWaybillImage = "";

        String url = sfExpress.getImageUri();
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId(sfExpress.getAppId());
        appInfo.setAppKey(sfExpress.getAppKey());
        appInfo.setAccessToken(getSfToken());

        //设置请求头
        MessageReq<WaybillReqDto> req = new MessageReq<>();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(ErpConstant.sf_action_code_download_waybill);
        head.setTransMessageId(erpDao.getSfTransMessageId());
        req.setHead(head);

        WaybillReqDto waybillReqDto = new WaybillReqDto();
        waybillReqDto.setOrderId(expressNo);
        req.setBody(waybillReqDto);

        try {
            MessageResp<WaybillRespDto> messageResp = WaybillDownloadTools.waybillDownload(url, appInfo, req);

            if (messageResp.getHead().getCode().equals(ErpConstant.sf_response_code_success)) {
                String[] images = messageResp.getBody().getImages();

                if (images != null) {
                    for (String image : images) {
                        sfWaybillImage += "<img src='data:image/png;base64," + image + "'/><br/><br/>";
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return sfWaybillImage;
    }

    /**
     * 获取顺丰 token
     * @return
     */
    public String getSfToken() {
        logger.info("getSfToken start");
        String token = (String) erpDao.getFromRedis(ErpConstant.sf_access_token_key);

        if (token == null) {
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
                erpDao.storeToRedis(ErpConstant.sf_refresh_token_key, messageResp.getBody().getRefreshToken(), ErpConstant.sf_refresh_token_time);
            }

            logger.info(messageResp.getHead().getCode() + "," + messageResp.getHead().getMessage() + "," +
                    messageResp.getBody().getAccessToken() + "," + messageResp.getBody().getRefreshToken());

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
                erpDao.storeToRedis(ErpConstant.sf_refresh_token_key, messageResp.getBody().getRefreshToken(), ErpConstant.sf_refresh_token_time);
            }

            logger.info(messageResp.getHead().getCode() + "," + messageResp.getHead().getMessage() + "," +
                    messageResp.getBody().getAccessToken() + "," + messageResp.getBody().getRefreshToken());

        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("refreshSfTokens end");
    }

    public ExpressDeliver getSfExpressOrderByStockInOut(StockInOut stockOut) {
        logger.info("getSfExpressOrderByStockInOut start:" + stockOut.getId());
        ExpressDeliver expressDeliver = null;

        for (StockInOutDetail detail : stockOut.getDetails()) {
            StockInOutDetail dbDetail = (StockInOutDetail) erpDao.queryById(detail.getId(), detail.getClass());

            StockInOutDetailProduct detailProduct = (StockInOutDetailProduct) dbDetail.getStockInOutDetailProducts().toArray()[0];
            Product dbProduct = (Product) erpDao.queryById(detailProduct.getProduct().getId(), detailProduct.getProduct().getClass());
            ExpressDeliverDetail dbExpressDeliverDetail = queryLastExpressDeliverDetailByProduct(dbProduct);

            if (dbExpressDeliverDetail != null) {
                if (expressDeliver == null) {
                    expressDeliver = (ExpressDeliver) erpDao.queryById(dbExpressDeliverDetail.getExpressDeliver().getId(), dbExpressDeliverDetail.getExpressDeliver().getClass());
                    expressDeliver.setDetails(new HashSet<>());
                }
                expressDeliver.getDetails().add(dbExpressDeliverDetail);
            }
        }

        logger.info("getSfExpressOrderByStockInOut end");
        return expressDeliver;
    }

    public ExpressDeliverDetail queryLastExpressDeliverDetailByProduct(Product product) {
        ExpressDeliverDetailProduct detailProduct = new ExpressDeliverDetailProduct();
        detailProduct.setProduct(product);

        List<ExpressDeliverDetailProduct> detailProducts = erpDao.query(detailProduct);
        return detailProducts.isEmpty() ? null : detailProducts.get(0).getExpressDeliverDetail();
    }

    public String setProductShipped(StockInOut stockOut) {
        String result = CommonConstant.fail;

        for (StockInOutDetail detail : stockOut.getDetails()) {
            StockInOutDetail dbDetail = (StockInOutDetail) erpDao.queryById(detail.getId(), detail.getClass());
            for (StockInOutDetailProduct detailProduct : dbDetail.getStockInOutDetailProducts()) {
                detailProduct.getProduct().setState(getProductShippedState(detailProduct.getProduct()));
                result += erpDao.updateById(detailProduct.getProduct().getId(), detailProduct.getProduct());
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public Integer getProductShippedState(Product product) {
        Integer state;

        int compare = getProductShippedQuantity(product).compareTo(getProductQuantity(product));

        if (compare < 0) {
            state = ErpConstant.product_state_shipped_part;
        } else {
            state = ErpConstant.product_state_shipped;
        }

        return state;
    }

    public Float getProductShippedQuantity(Product product) {
        List<ExpressDeliverDetail> details = getExpressDeliverDetail(product);

        Float quantity = 0f;
        for (ExpressDeliverDetail detail : details) {
            if (detail.getState().compareTo(ErpConstant.express_detail_state_sended) == 0) {
                quantity = new BigDecimal(Float.toString(quantity)).add(new BigDecimal(Float.toString(detail.getQuantity()))).floatValue();
            }
        }

        return quantity;
    }

    public List<ExpressDeliverDetail> getExpressDeliverDetail(Product product) {
        ExpressDeliverDetailProduct queryDetailProduct = new ExpressDeliverDetailProduct();
        queryDetailProduct.setProduct(product);
        List<ExpressDeliverDetailProduct> detailProducts = erpDao.query(queryDetailProduct);

        List<ExpressDeliverDetail> details = new ArrayList<>();

        for (ExpressDeliverDetailProduct detailProduct : detailProducts) {
            boolean isSameDetail = false;

            for (ExpressDeliverDetail detail : details) {
                if (detail.getId().compareTo(detailProduct.getExpressDeliverDetail().getId()) == 0) {
                    isSameDetail = true;
                }
            }

            if (!isSameDetail) {
                details.add(detailProduct.getExpressDeliverDetail());
            }
        }

        return details;
    }



    /**
     * 获取快递商品数量
     * @param detail
     * @return
     */
    public Integer getExpressDeliverDetailCount(ExpressDeliverDetail detail) {
        Integer detailCount = null;

        if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {
            detailCount = 1;
        } else {
            detailCount = detail.getQuantity().intValue();
        }

        return detailCount;
    }

    /**
     * 获取快递商品单位
     * @param detail
     * @return
     */
    public String getExpressDeliverDetailUnit(ExpressDeliverDetail detail) {
        String detailUnit = null;

        if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {
            detailUnit = ErpConstant.unit_piece;
        } else {
            detailUnit = detail.getUnit();
        }

        return detailUnit;
    }

    /**
     * 获取快递商品单位重量
     * @param detail
     * @return
     */
    public Float getExpressDeliverDetailWeight(ExpressDeliverDetail detail) {
        Product queryProduct = ((ExpressDeliverDetailProduct)detail.getExpressDeliverDetailProducts().toArray()[0]).getProduct();
        logger.info("getExpressDeliverDetailWeight start, unit:" + detail.getUnit() + ",quantity" + detail.getQuantity() + ",productId:" + queryProduct.getId());

        Double detailWeight = null;

        if (detail.getUnit().equals(ErpConstant.unit_g)) {
            detailWeight = new BigDecimal(Float.toString(detail.getQuantity())).multiply(new BigDecimal("0.001")).doubleValue();

        } else if (detail.getUnit().equals(ErpConstant.unit_kg)) {
            detailWeight = Double.valueOf(Float.toString(detail.getQuantity()));

        } else if (detail.getUnit().equals(ErpConstant.unit_ct)) {
            /**
             * 1 克拉 = 0.2 克 = 0.0002千克
             */
            detailWeight = new BigDecimal(Float.toString(detail.getQuantity())).multiply(new BigDecimal("0.0002")).doubleValue();

        } else if (detail.getUnit().equals(ErpConstant.unit_oz)) {
            /**
             * 1 盎司 = 28.3495 克 = 0.0283495千克
             */
            detailWeight = new BigDecimal(Float.toString(detail.getQuantity())).multiply(new BigDecimal("0.0283495")).doubleValue();

        } else {
            Product dbProduct = (Product) erpDao.query(queryProduct).get(0);
            String productWeight = null;

            if (dbProduct != null) {
                for (ProductOwnProperty property : dbProduct.getProperties()) {
                    if (property.getName().equals(ErpConstant.product_property_name_weight)) {
                        productWeight = property.getValue();
                        break;
                    }
                }
            }

            /**
             * 商品重量单位为 克
             */
            if (productWeight != null) {
                detailWeight = new BigDecimal(productWeight).multiply(new BigDecimal("0.001")).doubleValue();
            }
        }

        /**
         * 精确到克, 即小数点后 3 位
         */
        if (detailWeight != null) {
            detailWeight = Double.valueOf(String.valueOf(Math.round(detailWeight * 1000)/1000d));
        }

        logger.info("getExpressDeliverDetailWeight end, detailWeight:" + detailWeight);

        return detailWeight == null ? 0f: Float.valueOf(Double.toString(detailWeight));
    }





    public String launchAuditFlow(String entity, Integer entityId, String entityNo, String auditName, String content, User user) {
        String result = CommonConstant.fail;

        logger.info("launchAuditFlow start:" + result);

        /**
         * 创建审核流程第一个节点，发起审核流程
         */
        Audit audit = new Audit();
        audit.setEntity(entity);
        audit.setEntityId(entityId);
        audit.setEntityNo(entityNo);
        audit.setName(auditName);
        audit.setContent(content);

        Post post = (Post)(((List<User>)erpDao.query(user)).get(0)).getPosts().toArray()[0];
        audit.setCompany(post.getDept().getCompany());

        Map<String, String> result1 = writer.gson.fromJson(sysClient.launchAuditFlow(writer.gson.toJson(audit)),
                new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());

        result = result1.get(CommonConstant.result);

        logger.info("launchAuditFlow end, result:" + result);

        return result;
    }

    public String launchAuditFlowByPost(String entity, Integer entityId, String entityNo, String auditName, Post post, String preFlowAuditNo) {
        String result = CommonConstant.fail;

        logger.info("launchAuditFlowByPost start:" + result);

        /**
         * 创建审核流程第一个节点，发起审核流程
         */
        Audit audit = new Audit();
        audit.setEntity(entity);
        audit.setEntityId(entityId);
        audit.setEntityNo(entityNo);
        audit.setName(auditName);
        audit.setPreFlowAuditNo(preFlowAuditNo);

        audit.setPost(post);
        audit.setCompany(post.getDept().getCompany());

        Map<String, String> result1 = writer.gson.fromJson(sysClient.launchAuditFlow(writer.gson.toJson(audit)),
                new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());

        result = result1.get(CommonConstant.result);

        logger.info("launchAuditFlowByPost end, audit result:" + result);

        return result;
    }

    /**
     * 查询同批次同类型商品是否已经发起审核流程
     * @param product
     * @return
     */
    public List queryProductAudit(Product product) {
        List audits = null;

        try {
            Audit audit = new Audit();
            audit.setEntity(AuditFlowConstant.business_product);
            audit.setEntityNo(product.getNo());

            String auditSql = objectToSql.generateSelectSqlByAnnotation(audit);
            String selectSql = "", fromSql = "", whereSql = "", sortNumSql = "";

            String[] sqlParts = erpDao.getSqlPart(auditSql, Audit.class);
            selectSql = sqlParts[0];
            fromSql = sqlParts[1];
            whereSql = sqlParts[2];
            sortNumSql = sqlParts[3];

            fromSql += ", " + objectToSql.getTableName(Product.class) + " t21 ";
            if (!whereSql.trim().equals("")) {
                whereSql += " and ";
            }
            whereSql += " t21." + objectToSql.getColumn(Product.class.getDeclaredField("no")) +
                    " = t." + objectToSql.getColumn(Audit.class.getDeclaredField("entityNo")) +
                    " and t21." + objectToSql.getColumn(Product.class.getDeclaredField("describe")) +
                    " = " + product.getDescribe().getId();

            audits = erpDao.queryBySql("select " + selectSql + " from " + fromSql + " where " + whereSql + " order by " + sortNumSql, Audit.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return audits;
    }

    public String queryProductOnSalePreFlowAuditNo(Product product){
        PurchaseDetailProduct detailProduct = new PurchaseDetailProduct();
        detailProduct.setProduct(product);
        PurchaseDetail detail = ((PurchaseDetailProduct)erpDao.query(detailProduct).get(0)).getPurchaseDetail();

        Audit audit = new Audit();
        audit.setEntity(Purchase.class.getSimpleName().toLowerCase());
        audit.setEntityId(detail.getPurchase().getId());

        List<Audit> dbAudits = writer.gson.fromJson(
                sysClient.query(Audit.class.getSimpleName().toLowerCase(), writer.gson.toJson(audit)),
                new com.google.gson.reflect.TypeToken<List<Audit>>() {}.getType());

        return dbAudits.get(0).getNo();
    }

    public String updateAudit(Integer entityId, String oldEntity, String newEntity, String newName, String newContent) {
        String result = CommonConstant.fail;

        Audit audit = new Audit();
        audit.setEntity(oldEntity);
        audit.setEntityId(entityId);

        List<Audit> dbAudits = writer.gson.fromJson(
                sysClient.query(Audit.class.getSimpleName().toLowerCase(), writer.gson.toJson(audit)),
                new com.google.gson.reflect.TypeToken<List<Audit>>() {}.getType());

        for (Audit audit1 : dbAudits) {
            audit.setId(audit1.getId());
            audit.setName(newName);
            audit.setContent(newContent);
            audit.setEntity(newEntity);

            Map<String, String> result1 = writer.gson.fromJson(sysClient.update(Audit.class.getSimpleName().toLowerCase(), writer.gson.toJson(audit)),
                    new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());

            result = result1.get(CommonConstant.result);
        }

        return result;
    }

    public String deleteAudit(Integer entityId, String entity) {
        String result = CommonConstant.fail;

        Audit audit = new Audit();
        audit.setEntity(entity);
        audit.setEntityId(entityId);

        List<Audit> dbAudits = writer.gson.fromJson(
                sysClient.query(Audit.class.getSimpleName().toLowerCase(), writer.gson.toJson(audit)),
                new com.google.gson.reflect.TypeToken<List<Audit>>() {}.getType());

        for (Audit audit1 : dbAudits) {
            audit.setId(audit1.getId());

            Map<String, String> result1 = writer.gson.fromJson(sysClient.delete(Audit.class.getSimpleName().toLowerCase(), writer.gson.toJson(audit)),
                    new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());

            result = result1.get(CommonConstant.result);
        }

        return result;
    }
}