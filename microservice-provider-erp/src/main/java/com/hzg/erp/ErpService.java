package com.hzg.erp;

import com.google.common.reflect.TypeToken;
import com.hzg.order.Order;
import com.hzg.order.OrderDetail;
import com.hzg.order.OrderDetailProduct;
import com.hzg.pay.Account;
import com.hzg.pay.Pay;
import com.hzg.pay.Refund;
import com.hzg.sys.*;
import com.hzg.tools.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
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

    public String launchAuditFlow(String entity, Integer entityId, String auditName, String content, User user) {
        String result = CommonConstant.fail;

        logger.info("launchAuditFlow start:" + result);

        /**
         * 创建审核流程第一个节点，发起审核流程
         */
        Audit audit = new Audit();
        audit.setEntity(entity);
        audit.setEntityId(entityId);
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

    public String launchAuditFlowByPost(String entity, Integer entityId, String auditName, Post post, String preFlowAuditNo) {
        String result = CommonConstant.fail;

        logger.info("launchAuditFlowByPost start:" + result);

        /**
         * 创建审核流程第一个节点，发起审核流程
         */
        Audit audit = new Audit();
        audit.setEntity(entity);
        audit.setEntityId(entityId);
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

    public String queryProductOnSalePreFlowAuditNo(Product product){
        PurchaseDetail purchaseDetail = new PurchaseDetail();
        purchaseDetail.setProduct(product);
        Purchase purchase = ((PurchaseDetail)erpDao.query(purchaseDetail).get(0)).getPurchase();

        Audit audit = new Audit();
        audit.setEntity(Purchase.class.getSimpleName().toLowerCase());
        audit.setEntityId(purchase.getId());

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

    public String savePurchaseProducts(Purchase purchase) {
        String result = CommonConstant.fail;

        if (purchase.getDetails() != null) {
            for (PurchaseDetail detail : purchase.getDetails()) {
                Product product = detail.getProduct();

                detail.setProduct(product);
                detail.setNo(product.getNo());
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

                for (int i = 0; i < productQuantity; i++) {
                    ProductDescribe describe = product.getDescribe();
                    result += erpDao.insert(describe);

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

    public String deletePurchaseProducts(Purchase purchase) {
        String result = CommonConstant.fail;

        if (purchase.getDetails() != null) {
            for (PurchaseDetail detail : purchase.getDetails()) {
                PurchaseDetail dbDetail = (PurchaseDetail) erpDao.queryById(detail.getId(), detail.getClass());

                for (PurchaseDetailProduct detailProduct : dbDetail.getPurchaseDetailProducts()) {
                    Product product = (Product) erpDao.queryById(detailProduct.getProduct().getId(), detailProduct.getProduct().getClass());

                    if (product.getProperties() != null && !product.getProperties().isEmpty()) {
                        for (ProductOwnProperty ownProperty : product.getProperties()) {
                            result += erpDao.delete(ownProperty);
                        }
                    }

                    result += erpDao.delete(product.getDescribe());
                    result += erpDao.delete(product);
                }

                result += erpDao.delete(detail);
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 设置入库库存，商品入库状态
     * @param stockInOut
     * @return
     */
    public String stockIn(StockInOut stockInOut){
        String result = CommonConstant.fail;

        result += setStockProductIn(stockInOut);

        /**
         * 押金入库后通知仓储预计退还货物时间，财务人员预计退还押金时间
         */
        if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_deposit) == 0) {
            result += launchAuditFlow(AuditFlowConstant.business_stockIn_deposit_cangchu, stockInOut.getId(),
                    "押金入库单 " + stockInOut.getNo() + ", 预计" + stockInOut.getDeposit().getReturnGoodsDate() + "退货",
                    "请注意退货时间：" + stockInOut.getDeposit().getReturnGoodsDate(),
                    stockInOut.getInputer());

            result += launchAuditFlow(AuditFlowConstant.business_stockIn_deposit_caiwu, stockInOut.getId(),
                    "押金入库单 " + stockInOut.getNo() + ", 预计" + stockInOut.getDeposit().getReturnDepositDate() + "退押金",
                    "请注意退押金时间：" + stockInOut.getDeposit().getReturnDepositDate(),
                    stockInOut.getInputer());
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 设置入库库存，商品入库状态
     * @param stockInOut
     * @return
     */
    public String setStockProductIn(StockInOut stockInOut) {
        String result = CommonConstant.fail;

        for (StockInOutDetail detail : stockInOut.getDetails()) {
            result += backupPreStockInOut(((StockInOutDetailProduct)detail.getStockInOutDetailProducts().toArray()[0]).getProduct(), stockInOut.getId());

            /**
             * 修改商品为入库
             */
            for (StockInOutDetailProduct detailProduct : detail.getStockInOutDetailProducts()) {
                detailProduct.getProduct().setState(ErpConstant.product_state_stockIn);
                result += erpDao.updateById(detailProduct.getProduct().getId(), detailProduct.getProduct());
            }

            Product dbProduct = (Product) erpDao.queryById(((StockInOutDetailProduct)(detail.getStockInOutDetailProducts().toArray()[0])).getProduct().getId(), Product.class);

            /**
             * 调仓入库，设置调仓出库为完成状态
             */
            if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_changeWarehouse) == 0) {
                StockInOut stockOutChangeWarehouse = getLastStockInOutByProductAndType(dbProduct, ErpConstant.stockOut);
                stockOutChangeWarehouse.getChangeWarehouse().setState(ErpConstant.stockInOut_state_changeWarehouse_finished);
                result += erpDao.updateById(stockOutChangeWarehouse.getChangeWarehouse().getId(), stockOutChangeWarehouse.getChangeWarehouse());
            }

            /**
             * 添加库存
             */
            Stock tempStock = new Stock();
            tempStock.setProductNo(dbProduct.getNo());
            tempStock.setWarehouse(stockInOut.getWarehouse());

            /**
             * 在同一个仓库的同类商品做增量入库，才修改商品数量
             */
            if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_increment) == 0) {
                List<Stock> dbStocks = erpDao.query(tempStock);

                if (!dbStocks.isEmpty()) {
                    dbStocks.get(0).setDate(stockInOut.getDate());
                    result += setStockQuantity(dbStocks.get(0), detail.getQuantity(), CommonConstant.add);

                } else {
                    result += saveStock(tempStock, detail.getQuantity(), detail.getUnit(), stockInOut.getDate());
                }

            } else {
                result += saveStock(tempStock, detail.getQuantity(), detail.getUnit(), stockInOut.getDate());
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
     * 设置出库库存，商品出库状态
     * @param stockInOut
     * @return
     */
    public String stockOut(StockInOut stockInOut) {
        String result = CommonConstant.fail;

        for (StockInOutDetail detail : stockInOut.getDetails()) {
            result += backupPreStockInOut(((StockInOutDetailProduct)detail.getStockInOutDetailProducts().toArray()[0]).getProduct(), stockInOut.getId());

            for (StockInOutDetailProduct detailProduct : detail.getStockInOutDetailProducts()) {
                detailProduct.getProduct().setState(ErpConstant.product_state_stockOut);
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

    /**
     * 设置之前的入库出库完成记录为归档状态
     * @param product
     * @param currentStockInOutId
     * @return
     */
    public String backupPreStockInOut(Product product, Integer currentStockInOutId){
        String result = "";

        StockInOutDetailProduct queryDetailProduct = new StockInOutDetailProduct();
        queryDetailProduct.setProduct(product);
        List<StockInOutDetailProduct> StockInOutDetailProduct = erpDao.query(queryDetailProduct);

        for (StockInOutDetailProduct dbDetailProduct : StockInOutDetailProduct) {
            StockInOutDetail dbDetail = (StockInOutDetail) erpDao.queryById(dbDetailProduct.getStockInOutDetail().getId(), dbDetailProduct.getStockInOutDetail().getClass());

            if (dbDetail.getStockInOut().getState().compareTo(ErpConstant.stockInOut_state_finished) == 0 &&
                    dbDetail.getStockInOut().getId().compareTo(currentStockInOutId) != 0) {
                dbDetail.getStockInOut().setState(ErpConstant.stockInOut_state_backup);
                result += erpDao.updateById(dbDetail.getStockInOut().getId(), dbDetail.getStockInOut());
            }
        }

        return result;
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

        result += erpDao.save(stockInOut);
        result += saveStockInOutDetails(stockInOut);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String saveStockInOutDetails(StockInOut stockInOut) {
        String result = CommonConstant.fail;

        for (StockInOutDetail detail : stockInOut.getDetails()) {
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

    public PurchaseDetail getPurchaseDetail(Integer productId) {
        PurchaseDetail tempPurchaseDetail = new PurchaseDetail();
        Product tempProduct = new Product();
        tempProduct.setId(productId);
        tempPurchaseDetail.setProduct(tempProduct);

        return (PurchaseDetail) erpDao.query(tempPurchaseDetail).get(0);
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
        String result = CommonConstant.fail;

        result += purchaseStateModify(audit, purchaseState, productState);

        if (result.contains(CommonConstant.success)) {
            Purchase purchase = (Purchase) erpDao.queryById(audit.getEntityId(), Purchase.class);

            Pay pay = new Pay();
            pay.setAmount(-purchase.getAmount());
            pay.setState(PayConstants.state_pay_apply);

            pay.setPayAccount(purchase.getAccount().getAccount());
            pay.setPayBranch(purchase.getAccount().getBranch());
            pay.setPayBank(purchase.getAccount().getBank());

            pay.setEntity(Purchase.class.getSimpleName().toLowerCase());
            pay.setEntityId(purchase.getId());
            pay.setEntityNo(purchase.getNo());

            PurchaseDetail detail = null;
            for (PurchaseDetail ele : purchase.getDetails()) {
                detail = ele;
                break;
            }

            if (detail != null) {
                detail = (PurchaseDetail) erpDao.queryById(detail.getId(), PurchaseDetail.class);

                pay.setReceiptAccount(detail.getSupplier().getAccount());
                pay.setReceiptBranch(detail.getSupplier().getBranch());
                pay.setReceiptBank(detail.getSupplier().getBank());
            }

            Map<String, String> result1 = writer.gson.fromJson(payClient.save(pay.getClass().getSimpleName(), writer.gson.toJson(pay)),
                    new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
            result += result1.get(CommonConstant.result);
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String purchaseEmergencyPay(Audit audit) {

        Purchase purchase = (Purchase) erpDao.queryById(audit.getEntityId(), Purchase.class);
        return setPayAccountInfo(getPaysByEntity(purchase.getClass().getSimpleName().toLowerCase(), purchase.getId()), purchase.getAccount(),
                PayConstants.state_pay_success, CommonConstant.add);
    }

    public String stockInReturnDeposit(Audit audit) {
        String result = CommonConstant.fail;

        StockInOut stockInOut = (StockInOut) erpDao.queryById(audit.getEntityId(), StockInOut.class);
        Purchase purchase = (Purchase) erpDao.queryById(stockInOut.getDeposit().getPurchase().getId(), stockInOut.getDeposit().getPurchase().getClass());
        Pay pay = getPaysByEntity(purchase.getClass().getSimpleName().toLowerCase(), purchase.getId());

        List<Account> accounts = writer.gson.fromJson(payClient.query(purchase.getAccount().getClass().getSimpleName(), writer.gson.toJson(purchase.getAccount())),
                new TypeToken<List<Account>>(){}.getType());
        result += setPayAccountInfo(pay, accounts.get(0), null, CommonConstant.subtract);

        Refund refund = new Refund();
        refund.setPay(pay);
        refund.setState(PayConstants.state_refund_apply);
        refund.setPayBank(pay.getPayBank());
        refund.setRefundDate(stockInOut.getDeposit().getReturnDepositDate());
        refund.setInputDate(dateUtil.getSecondCurrentTimestamp());
        refund.setEntity(ErpConstant.return_purchase_deposit);
        refund.setEntityId(purchase.getId());

        Map<String, String> result1 = writer.gson.fromJson(payClient.save(refund.getClass().getSimpleName(), writer.gson.toJson(refund)),
                new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
        result += result1.get(CommonConstant.result);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String setPayAccountInfo(Pay pay, Account account, Integer payState, String operator) {
        String result = CommonConstant.fail;

        pay.setState(payState);
        pay.setPayDate(dateUtil.getSecondCurrentTimestamp());

        Map<String, String> result1 = writer.gson.fromJson(payClient.update(Pay.class.getSimpleName(), writer.gson.toJson(pay)),
                new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
        result += result1.get(CommonConstant.result);

        if (result.contains(CommonConstant.success)) {
            /**
             * 使用 BigDecimal 进行精度计算
             */
            BigDecimal accountAmount = new BigDecimal(Float.toString(account.getAmount()));
            BigDecimal payAmount = new BigDecimal(Float.toString(pay.getAmount()));

            if (operator.equals(CommonConstant.add)) {
                account.setAmount(accountAmount.add(payAmount).floatValue());
            } else if (operator.equals(CommonConstant.subtract)) {
                account.setAmount(accountAmount.subtract(payAmount).floatValue());
            }

            result1 = writer.gson.fromJson(payClient.update(Account.class.getSimpleName(), writer.gson.toJson(account)),
                    new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
            result += result1.get(CommonConstant.result);
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public Pay getPaysByEntity(String entity, Integer entityId) {
        Pay pay = new Pay();
        pay.setEntity(entity);
        pay.setEntityId(entityId);

        List<Pay> pays = writer.gson.fromJson(payClient.query(pay.getClass().getSimpleName(), writer.gson.toJson(pay)),
                new TypeToken<List<Pay>>(){}.getType());

        Collections.sort(pays, new Comparator<Pay>() {
            @Override
            public int compare(Pay o1, Pay o2) {
                if (o1.getId().compareTo(o2.getId()) > 0) {
                    return 1;
                } else if(o1.getId().compareTo(o2.getId()) < 0) {
                    return -1;
                }

                return 0;
            }
        });

        return pays.isEmpty() ? null : pays.get(0);
    }

    public String productStateModify(Audit audit, Integer state) {
        Product stateProduct = new Product();
        stateProduct.setId(audit.getEntityId());
        stateProduct.setState(state);

        return erpDao.updateById(stateProduct.getId(), stateProduct);
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
                        PurchaseDetailProduct detailProduct = (PurchaseDetailProduct) details.get(j).getPurchaseDetailProducts().toArray()[0];

                        Product product = (Product) erpDao.queryById(detailProduct.getProduct().getId(), detailProduct.getProduct().getClass());
                        if (product != null) {

                            if (product.getState().compareTo(ErpConstant.product_state_purchase_close) == 0) {
                                details.get(j).setProduct(product);

                            } else {
                                details.remove(details.get(j));
                                j--;
                            }

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
            Class[] clazzs = {Product.class, ProductType.class, Supplier.class, ProductDescribe.class, StockInOutDetailProduct.class, StockInOutDetail.class, StockInOut.class};
            Map<String, List<Object>> results = erpDao.queryBySql(getProductComplexSql(json, position, rowNum), clazzs);

            List<Object> products = results.get(Product.class.getName());
            List<Object> types = results.get(ProductType.class.getName());
            List<Object> suppliers = results.get(Supplier.class.getName());
            List<Object> describes = results.get(ProductDescribe.class.getName());

            List<Object> stockInOutDetails = results.get(StockInOutDetail.class.getName());
            List<Object> stockInOuts = results.get(StockInOut.class.getName());

            int i = 0;
            for (Object stockInOutDetail : stockInOutDetails) {
                Product product = (Product) products.get(i);
                product.setType((ProductType) types.get(i));
                product.setSupplier((Supplier) suppliers.get(i));
                product.setDescribe((ProductDescribe) describes.get(i));

                ((StockInOutDetail)stockInOutDetail).setProduct(product);
                ((StockInOutDetail)stockInOutDetail).setStockInOut((StockInOut) stockInOuts.get(i));

                i++;
            }

            return stockInOutDetails;

        } else if (entity.equalsIgnoreCase(ProductDescribe.class.getSimpleName())) {
            return erpDao.queryBySql(getProductDescribeSql(json, position, rowNum), ProductDescribe.class);

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
                    result += launchAuditFlow(auditEntity, priceChange.getId(),
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
        PrivilegeResource privilegeResource = new PrivilegeResource();
        privilegeResource.setUri(ErpConstant.privilege_resource_uri_print_expressWaybill);
        List<com.hzg.sys.User> users = writer.gson.fromJson(sysClient.getUsersByUri(writer.gson.toJson(privilegeResource)),
                new com.google.gson.reflect.TypeToken<List<User>>(){}.getType());
        return users.get((int)System.currentTimeMillis()%users.size());
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

    public ExpressDeliver generateExpressDeliver(ExpressDeliver receiverInfo, StockInOut stockOut) {
        logger.info("generateExpressDeliver start, receiverInfo:" + receiverInfo.toString() + ",  stockInOut:" + stockOut.toString());

        User sender = (User) erpDao.queryById(stockOut.getInputer().getId(), stockOut.getInputer().getClass());
        Warehouse warehouse = (Warehouse) erpDao.queryById(stockOut.getWarehouse().getId(), stockOut.getWarehouse().getClass());

        ExpressDeliver expressDeliver = new ExpressDeliver();
        expressDeliver.setDeliver(ErpConstant.deliver_sfExpress);
        expressDeliver.setType(ErpConstant.deliver_sfExpress_type);
        expressDeliver.setDate(receiverInfo.getDate());
        expressDeliver.setState(ErpConstant.express_state_sending);

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
            expressDeliverDetail.setExpressNo(ErpConstant.no_expressDelivery_perfix + erpDao.getSfTransMessageId());
            expressDeliverDetail.setProductNo(product.getNo());
            expressDeliverDetail.setQuantity(detail.getQuantity());
            expressDeliverDetail.setUnit(detail.getUnit());
            expressDeliverDetail.setPrice(product.getFatePrice());
            expressDeliverDetail.setState(ErpConstant.express_detail_state_unReceive);

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

    public ExpressDeliverDetail queryLastUnReceiveExpressDeliverDetailByProductNo(String productNo) {
        ExpressDeliverDetail detail = new ExpressDeliverDetail();
        detail.setProductNo(productNo);
        detail.setState(ErpConstant.express_detail_state_unReceive);

        return (ExpressDeliverDetail) erpDao.query(detail).get(0);
    }

    public Float getCanSellProductQuantity(String productNo) {
        Stock stock = new Stock();
        stock.setProductNo(productNo);
        stock.setState(ErpConstant.stock_state_valid);
        List<Stock> stocks = erpDao.query(stock);
        BigDecimal canSellQuantity = new BigDecimal(0);

        for (Stock ele : stocks) {
            canSellQuantity.add(new BigDecimal(Float.toString(ele.getQuantity())));
        }

        return canSellQuantity.floatValue();
    }
}