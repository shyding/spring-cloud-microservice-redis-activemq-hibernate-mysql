package com.hzg.erp;

import com.google.common.reflect.TypeToken;
import com.hzg.pay.Account;
import com.hzg.pay.Pay;
import com.hzg.sys.Audit;
import com.hzg.sys.Post;
import com.hzg.sys.User;
import com.hzg.tools.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
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

    public String launchAuditFlow(String entity, Integer entityId, String auditName, User user) {
        String result = CommonConstant.fail;

        /**
         * 创建审核流程第一个节点，发起审核流程
         */
        Audit audit = new Audit();
        audit.setEntity(entity);
        audit.setEntityId(entityId);
        audit.setName(auditName);

        Post post = (Post)(((List<User>)erpDao.query(user)).get(0)).getPosts().toArray()[0];
        audit.setCompany(post.getDept().getCompany());

        Map<String, String> result1 = writer.gson.fromJson(sysClient.audit(writer.gson.toJson(audit)),
                new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());

        result = result1.get(CommonConstant.result);

        logger.info("audit result:" + result);

        return result;
    }

    public String updateAudit(Integer entityId, String oldEntity, String newName, String newEntity) {
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

                ProductDescribe describe = product.getDescribe();
                result += erpDao.save(describe);

                product.setDescribe(describe);
                result += erpDao.save(product);

                /**
                 * 使用 new 新建，避免直接使用已经包含 property 属性的 product， 使得 product 与 property 循环嵌套
                 */
                Product doubleRelateProduct = new Product();
                doubleRelateProduct.setId(product.getId());

                if (product.getProperties() != null) {
                    for (ProductOwnProperty ownProperty : product.getProperties()) {
                        ownProperty.setProduct(doubleRelateProduct);
                        result += erpDao.save(ownProperty);
                    }
                }

                detail.setProduct(product);
                detail.setNo(product.getNo());
                detail.setProductName(product.getName());
                detail.setAmount(product.getUnitPrice() * detail.getQuantity());
                detail.setPrice(product.getUnitPrice());

                Purchase doubleRelatePurchase = new Purchase();
                doubleRelatePurchase.setId(purchase.getId());

                detail.setPurchase(doubleRelatePurchase);

                result += erpDao.save(detail);
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String deletePurchaseProducts(Purchase purchase) {
        String result = CommonConstant.fail;

        if (purchase.getDetails() != null) {
            for (PurchaseDetail detail : purchase.getDetails()) {

                if (detail.getProduct().getProperties() != null) {
                    for (ProductOwnProperty ownProperty : detail.getProduct().getProperties()) {
                        result += erpDao.delete(ownProperty);
                    }
                }

                result += erpDao.delete(detail.getProduct().getDescribe());
                result += erpDao.delete(detail.getProduct());
                result += erpDao.delete(detail);
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String saveStockInProducts(StockInOut stockInOut) {
        String result = CommonConstant.fail;

        for (StockInOutDetail detail : stockInOut.getDetails()) {
            detail.setStockInOut(stockInOut);
            result += erpDao.save(detail);

            detail.getProduct().setState(ErpConstant.product_state_stockIn);
            result += erpDao.updateById(detail.getProduct().getId(), detail.getProduct());
        }

        Stock tempStock = new Stock();
        for (Stock stock : stockInOut.getStocks()) {
            if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_increment) == 0) {
                /**
                 * 在同一个仓库的同类商品做增量入库，才修改商品数量
                 */
                tempStock.setProductNo(stock.getProductNo());
                tempStock.setWarehouse(stock.getWarehouse());
                List<Stock> dbStocks = erpDao.query(tempStock);

                if (!dbStocks.isEmpty()) {
                    result += setStockQuantity(dbStocks.get(0), stock.getQuantity(), CommonConstant.add);
                } else {
                    stock.setNo(erpDao.getNo(ErpConstant.no_stock_perfix));
                    result += erpDao.save(stock);
                }

            } else {
                stock.setNo(erpDao.getNo(ErpConstant.no_stock_perfix));
                result += erpDao.save(stock);
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String deleteStockInProducts(StockInOut stockInOut) {
        String result = CommonConstant.fail;

        for (StockInOutDetail detail : stockInOut.getDetails()) {
            result += erpDao.delete(detail);

            detail.getProduct().setState(ErpConstant.product_state_purchase_pass);
            result += erpDao.updateById(detail.getProduct().getId(), detail.getProduct());

            Product dbProduct = (Product) erpDao.queryById(detail.getProduct().getId(), Product.class);
            PurchaseDetail purchaseDetail = getPurchaseDetail(dbProduct.getId());
            Stock dbStock = getDbStock(dbProduct.getNo(), stockInOut.getWarehouse());

            if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_increment) == 0) {
                result += setStockQuantity(dbStock, purchaseDetail.getQuantity(), CommonConstant.subtract);
                if (dbStock.getQuantity().compareTo(0f) == 0) {
                    result += erpDao.delete(dbStock);
                }
            } else {
                result += erpDao.delete(dbStock);

            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String setStocks(StockInOut stockInOut, String operation) {
        String result = CommonConstant.fail;

        for (StockInOutDetail detail : stockInOut.getDetails()) {

            if (operation.equals(CommonConstant.add)) {
                detail.getProduct().setState(ErpConstant.product_state_stockIn);
            } else if (operation.equals(CommonConstant.subtract)) {
                detail.getProduct().setState(ErpConstant.product_state_purchase_pass);
            }

            result += erpDao.updateById(detail.getProduct().getId(), detail.getProduct());

            Product dbProduct = (Product) erpDao.queryById(detail.getProduct().getId(), Product.class);
            PurchaseDetail purchaseDetail = getPurchaseDetail(dbProduct.getId());
            Stock dbStock = getDbStock(dbProduct.getNo(), stockInOut.getWarehouse());

            if (stockInOut.getType().compareTo(ErpConstant.stockInOut_type_increment) == 0) {
                result += setStockQuantity(dbStock, purchaseDetail.getQuantity(), operation);

            } else {
                if (operation.equals(CommonConstant.add)) {
                    dbStock.setState(ErpConstant.stock_state_valid);
                } else if (operation.equals(CommonConstant.subtract)) {
                    dbStock.setState(ErpConstant.stock_state_invalid);
                }

                result += erpDao.updateById(dbStock.getId(), dbStock);


            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public Stock getDbStock(String productNo, Warehouse warehouse) {
        Stock tempStock = new Stock();
        tempStock.setProductNo(productNo);
        tempStock.setWarehouse(warehouse);

        return (Stock) erpDao.query(tempStock).get(0);
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

        Purchase temp = new Purchase();
        temp.setId(purchase.getId());
        temp.setState(purchaseState);

        result += erpDao.updateById(temp.getId(), temp);

        if (result.contains(CommonConstant.success)) {
            Product temp1 = new Product();
            Set<PurchaseDetail> details = purchase.getDetails();
            for (PurchaseDetail detail : details) {

                temp1.setId(detail.getProduct().getId());
                temp1.setState(productState);
                result += erpDao.updateById(temp1.getId(), temp1);
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String purchaseProductsStateModify(Audit audit, Integer productState) {
        String result = CommonConstant.fail;

        Purchase purchase = (Purchase)erpDao.queryById(audit.getEntityId(), Purchase.class);

        Product temp1 = new Product();
        Set<PurchaseDetail> details = purchase.getDetails();
        for (PurchaseDetail detail : details) {

            temp1.setId(detail.getProduct().getId());
            temp1.setState(productState);
            result += erpDao.updateById(temp1.getId(), temp1);
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
            pay.setState(PayConstants.state_not_pay);

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

            Map<String, String> result1 = writer.gson.fromJson(payClient.save(Pay.class.getSimpleName(), writer.gson.toJson(pay)),
                    new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
            result += result1.get(CommonConstant.result);
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String purchaseEmergencyPay(Audit audit) {
        String result = CommonConstant.fail;

        Pay pay = new Pay();
        pay.setEntity(Purchase.class.getSimpleName().toLowerCase());
        pay.setEntityId(audit.getEntityId());

        List<Pay> pays = writer.gson.fromJson(payClient.query(Pay.class.getSimpleName(), writer.gson.toJson(pay)),
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

        if (!pays.isEmpty()) {
            pay.setId(pays.get(0).getId());
            pay.setState(PayConstants.state_pay_success);
            pay.setPayDate(new Timestamp(System.currentTimeMillis()));

            Map<String, String> result1 = writer.gson.fromJson(payClient.update(Pay.class.getSimpleName(), writer.gson.toJson(pay)),
                    new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
            result += result1.get(CommonConstant.result);

            if (result.contains(CommonConstant.success)) {
                Purchase purchase = (Purchase) erpDao.queryById(audit.getEntityId(), Purchase.class);
                Account account = purchase.getAccount();

                /**
                 * 使用 BigDecimal 进行精度计算
                 */
                BigDecimal accountAmount = new BigDecimal(Double.toString(account.getAmount()));
                BigDecimal payAmount = new BigDecimal(Float.toString(pays.get(0).getAmount()));

                account.setAmount(accountAmount.add(payAmount).doubleValue());


                result1 = writer.gson.fromJson(payClient.update(Account.class.getSimpleName(), writer.gson.toJson(account)),
                        new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType());
                result += result1.get(CommonConstant.result);
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String queryTargetEntity(String targetEntity, String entity, String json) {
        String result = "";

        if (targetEntity.equalsIgnoreCase(Purchase.class.getSimpleName()) &&
                entity.equalsIgnoreCase(Purchase.class.getSimpleName())) {

            Purchase purchase = writer.gson.fromJson(json, Purchase.class);
            purchase.setState(1);

            Field[] limitFields = new Field[1];
            try {
                limitFields[0] = purchase.getClass().getDeclaredField("state");
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<Purchase> purchases = (List<Purchase>) erpDao.suggest(purchase, limitFields);

            Purchase tempPurchase = new Purchase();
            PurchaseDetail tempPurchaseDetail = new PurchaseDetail();
            for (Purchase ele : purchases) {
                tempPurchase.setId(ele.getId());
                tempPurchaseDetail.setPurchase(tempPurchase);

                ele.setDetails(new HashSet(erpDao.query(tempPurchaseDetail)));

                for (PurchaseDetail detail : ele.getDetails()) {
                    detail.setProduct((Product) erpDao.query(detail.getProduct()).get(0));
                }
            }

            result = writer.gson.toJson(purchases);


        } else if (targetEntity.equalsIgnoreCase(PurchaseDetail.class.getSimpleName()) &&
                entity.equalsIgnoreCase(Product.class.getSimpleName())) {
            Product product = writer.gson.fromJson(json, Product.class);
            product.setState(10);

            Field[] limitFields = new Field[1];
            try {
                limitFields[0] = product.getClass().getDeclaredField("state");
            } catch (Exception e) {
                e.printStackTrace();
            }


            List<Product> products = (List<Product>) erpDao.suggest(writer.gson.fromJson(json, Product.class), limitFields);

            if (!products.isEmpty()) {
                List<PurchaseDetail> details = new ArrayList<>();
                Product tempProduct = new Product();
                PurchaseDetail tempPurchaseDetail = new PurchaseDetail();

                for (Product ele : products) {
                    tempProduct.setId(ele.getId());
                    tempPurchaseDetail.setProduct(tempProduct);

                    details.add((PurchaseDetail) erpDao.query(tempPurchaseDetail).get(0));
                }

                for (PurchaseDetail detail : details) {
                    detail.setProduct((Product) erpDao.query(detail.getProduct()).get(0));
                }

                result = writer.gson.toJson(details);
            }

        }

        return result;
    }
}
