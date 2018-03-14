package com.hzg.afterSaleService;

import com.google.gson.reflect.TypeToken;
import com.hzg.customer.Express;
import com.hzg.erp.*;
import com.hzg.order.*;
import com.hzg.pay.Pay;
import com.hzg.sys.Action;
import com.hzg.sys.User;
import com.hzg.tools.*;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Service
public class AfterSaleServiceService {
    Logger logger = Logger.getLogger(AfterSaleServiceService.class);

    @Autowired
    private AfterSaleServiceDao afterSaleServiceDao;

    @Autowired
    private PayClient payClient;

    @Autowired
    private ErpClient erpClient;

    @Autowired
    private Writer writer;

    @Autowired
    public ObjectToSql objectToSql;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderService orderService;

    @Autowired
    public SessionFactory sessionFactory;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private AfterSaleServiceClient afterSaleServiceClient;

    @Autowired
    private SysClient sysClient;



    /**
     *
     * ********************* 退货 **********************
     *
     */


    /**
     * 保存退货单
     * @param returnProduct
     * @return
     */
    public String saveReturnProduct(ReturnProduct returnProduct) {
        String result = CommonConstant.fail;

        logger.info("saveReturnProduct start");

        if (returnProduct.getEntity().equals(OrderConstant.order)) {
            returnProduct = setReturnProduct(returnProduct);
        } else if (returnProduct.getEntity().equals(ErpConstant.purchase)) {
            returnProduct = setPurchaseReturnProduct(returnProduct);
        }

        String isCanReturnMsg = isCanReturn(returnProduct);

        if (!isCanReturnMsg.equals("")) {
            return CommonConstant.fail + isCanReturnMsg;
        }

        result += afterSaleServiceDao.save(returnProduct);
        ReturnProduct idReturnProduct = new ReturnProduct(returnProduct.getId());
        ReturnProductDetail idReturnProductDetail = new ReturnProductDetail();

        for (ReturnProductDetail returnProductDetail : returnProduct.getDetails()) {
            returnProductDetail.setReturnProduct(idReturnProduct);
            result += afterSaleServiceDao.save(returnProductDetail);

            Set<ReturnProductDetailProduct> returnProductDetailProducts = new HashSet<>();

            idReturnProductDetail.setId(returnProductDetail.getId());
            for (ReturnProductDetailProduct returnProductDetailProduct : returnProductDetail.getReturnProductDetailProducts()) {
                returnProductDetailProduct.setReturnProductDetail(idReturnProductDetail);
                result += afterSaleServiceDao.save(returnProductDetailProduct);

                returnProductDetailProducts.add(returnProductDetailProduct);
            }

            returnProductDetail.setReturnProductDetailProducts(returnProductDetailProducts);
        }

        /**
         * 这里提交事务，使得在设置商品在退状态时，获取的在退商品数量包括当前正在退的商品数量
         */
        sessionFactory.getCurrentSession().getTransaction().commit();
        sessionFactory.getCurrentSession().getTransaction().begin();

        if (returnProduct.getEntity().equals(OrderConstant.order)) {
            result += setProductsReturnState(ErpConstant.product_action_name_setProductsOnReturn, returnProduct);

        } else if (returnProduct.getEntity().equals(ErpConstant.purchase)) {
            result += setProductsReturnState(ErpConstant.product_action_name_setPurchaseProductsOnReturn, returnProduct);
        }

        logger.info("saveReturnProduct end, result:" + result);
        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    List<ReturnProduct> queryReturnProduct(ReturnProduct returnProduct) {
        List<ReturnProduct> returnProducts = afterSaleServiceDao.query(returnProduct);

        for (ReturnProduct ele : returnProducts) {
            for (ReturnProductDetail detail : ele.getDetails()) {
                ReturnProductDetailProduct detailProduct = new ReturnProductDetailProduct();
                detailProduct.setReturnProductDetail(detail);

                List<ReturnProductDetailProduct> returnProductDetailProducts = afterSaleServiceDao.query(detailProduct);
                detail.setProduct(returnProductDetailProducts.get(0).getProduct());
                detail.setReturnProductDetailProducts(new HashSet<>(returnProductDetailProducts));
            }

            if (ele.getEntity().equals(ErpConstant.purchase)) {
                ele.setSysUser(getPurchaseReturnProductUser(ele));
            }

            Action action = new Action();
            action.setEntity(AfterSaleServiceConstant.returnProduct);
            action.setEntityId(ele.getId());
            ele.setActions(afterSaleServiceDao.query(action));
        }

        return returnProducts;
    }

    List<ReturnProduct> complexQueryReturnProduct(String json, int position, int rowNum) {
        List<ReturnProduct> returnProducts = afterSaleServiceDao.complexQuery(ReturnProduct.class,
                writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType()), position, rowNum);

        for (ReturnProduct ele : returnProducts) {
            if (ele.getEntity().equals(ErpConstant.purchase)) {
                ele.setSysUser(getPurchaseReturnProductUser(ele));
            }
            ele.setReturnProductUsername(ele.getReturnProductUsername());
        }

        return returnProducts;
    }

    public User getPurchaseReturnProductUser(ReturnProduct returnProduct) {
        ReturnProduct dbReturnProduct = (ReturnProduct) orderDao.queryBySql(objectToSql.generateSelectSqlByAnnotation(new ReturnProduct(returnProduct.getId())), ReturnProduct.class).get(0);
        return ((List<User>)writer.gson.fromJson(sysClient.query(dbReturnProduct.getUser().getClass().getSimpleName(), writer.gson.toJson(dbReturnProduct.getUser())),
                new TypeToken<List<User>>(){}.getType())).get(0);
    }

    public ReturnProduct setReturnProduct(String json) {
        ReturnProduct returnProduct = new ReturnProduct();
        Map<String, Object> returnProductInfo = writer.gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
        Integer entityId = ((Double)returnProductInfo.get(CommonConstant.entityId)).intValue();
        Order order = orderService.queryOrder(new Order(entityId)).get(0);

        returnProduct.setNo(afterSaleServiceDao.getNo(AfterSaleServiceConstant.no_returnProduct_perfix));
        returnProduct.setEntity(OrderConstant.order);
        returnProduct.setEntityId(order.getId());
        returnProduct.setEntityNo(order.getNo());
        returnProduct.setUser(order.getUser());
        returnProduct.setAmount(order.getPayAmount());

        Set<ReturnProductDetail> details = new HashSet<>();
        for (OrderDetail detail : order.getDetails()) {
            ReturnProductDetail returnProductDetail = new ReturnProductDetail();

            returnProductDetail.setProductNo(detail.getProductNo());
            returnProductDetail.setQuantity(detail.getQuantity());
            returnProductDetail.setUnit(detail.getUnit());

            if (detail.getPriceChange() == null) {
                returnProductDetail.setPrice(detail.getProductPrice());
            } else {
                returnProductDetail.setPrice(detail.getPriceChange().getPrice());
            }
            returnProductDetail.setAmount(detail.getPayAmount());
            returnProductDetail.setProduct(detail.getProduct());

            details.add(returnProductDetail);
        }

        returnProduct.setDetails(details);

        return returnProduct;
    }

    public ReturnProduct setPurchaseReturnProduct(String json) {
        ReturnProduct returnProduct = new ReturnProduct();
        Map<String, Object> returnProductInfo = writer.gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
        Integer entityId = ((Double)returnProductInfo.get(CommonConstant.entityId)).intValue();
        User user = getUserBySessionId((String) returnProductInfo.get(CommonConstant.sessionId));

        Purchase queryPurchase = new Purchase(entityId);
        Purchase purchase = ((List<Purchase>)writer.gson.fromJson(erpClient.query(queryPurchase.getClass().getSimpleName(), writer.gson.toJson(queryPurchase)),
                new TypeToken<List<Purchase>>(){}.getType())).get(0);

        returnProduct.setNo(afterSaleServiceDao.getNo(AfterSaleServiceConstant.no_returnProduct_perfix));
        returnProduct.setEntity(ErpConstant.purchase);
        returnProduct.setEntityId(purchase.getId());
        returnProduct.setEntityNo(purchase.getNo());
        returnProduct.setUser(writer.gson.fromJson(writer.gson.toJson(user), com.hzg.customer.User.class));
        returnProduct.setSysUser(user);
        returnProduct.setAmount(purchase.getAmount());

        Set<ReturnProductDetail> details = new HashSet<>();
        for (PurchaseDetail detail : purchase.getDetails()) {
            ReturnProductDetail returnProductDetail = new ReturnProductDetail();

            returnProductDetail.setProductNo(detail.getProductNo());
            returnProductDetail.setQuantity(detail.getQuantity());
            returnProductDetail.setUnit(detail.getUnit());

            returnProductDetail.setPrice(detail.getPrice());
            returnProductDetail.setAmount(detail.getAmount());
            returnProductDetail.setProduct(detail.getProduct());

            details.add(returnProductDetail);

            returnProduct.setDetails(details);

        }

        return returnProduct;
    }

    /**
     * 设置退货单
     * @param returnProduct
     * @return
     */
    public ReturnProduct setReturnProduct(ReturnProduct returnProduct) {
        Order order = orderService.queryOrder(new Order(returnProduct.getEntityId())).get(0);

        returnProduct.setEntityNo(order.getNo());
        returnProduct.setUser(order.getUser());
        returnProduct.setInputDate(dateUtil.getSecondCurrentTimestamp());
        returnProduct.setState(AfterSaleServiceConstant.returnProduct_state_apply);

        BigDecimal amount = new BigDecimal(0);

        for (ReturnProductDetail detail : returnProduct.getDetails()) {
            for (OrderDetail orderDetail : order.getDetails()) {
                if (detail.getProductNo().equals(orderDetail.getProductNo())) {

                    detail.setState(AfterSaleServiceConstant.returnProduct_detail_state_unReturn);
                    detail.setUnit(orderDetail.getUnit());

                    if (orderDetail.getPriceChange() == null) {
                        detail.setPrice(orderDetail.getProductPrice());
                    } else {
                        detail.setPrice(orderDetail.getPriceChange().getPrice());
                    }
                    detail.setAmount(new BigDecimal(Float.toString(detail.getPrice())).
                            multiply(new BigDecimal(Float.toString(detail.getQuantity()))).floatValue());

                    detail.setReturnProductDetailProducts(new HashSet<>());
                    for (OrderDetailProduct orderDetailProduct : orderDetail.getOrderDetailProducts()) {
                        Integer productState = orderDetailProduct.getProduct().getState();

                        if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                                detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {

                            /**
                             * 出库，已售，已邮寄，部分商品在退货，这些商品可以提交订单退货
                             */
                            if (productState.compareTo(ErpConstant.product_state_stockOut) == 0 ||
                                productState.compareTo(ErpConstant.product_state_stockOut_part) == 0 ||
                                productState.compareTo(ErpConstant.product_state_sold) == 0 ||
                                productState.compareTo(ErpConstant.product_state_sold_part) == 0 ||
                                productState.compareTo(ErpConstant.product_state_shipped) == 0 ||
                                productState.compareTo(ErpConstant.product_state_shipped_part) == 0 ||
                                productState.compareTo(ErpConstant.product_state_onReturnProduct_part) == 0 ||
                                productState.compareTo(ErpConstant.product_state_onChangeOnReturnProduct_part) == 0 ||
                                productState.compareTo(ErpConstant.product_state_returnedProduct_part) == 0) {
                                detail.getReturnProductDetailProducts().add(new ReturnProductDetailProduct(orderDetailProduct.getProduct()));
                            }

                        } else {
                            if (detail.getReturnProductDetailProducts().size() <= detail.getQuantity()) {
                                if (productState.compareTo(ErpConstant.product_state_stockOut) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_stockOut_part) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_sold) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_sold_part) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_shipped) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_shipped_part) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_onReturnProduct_part) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_onChangeOnReturnProduct_part) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_returnedProduct_part) == 0) {
                                    detail.getReturnProductDetailProducts().add(new ReturnProductDetailProduct(orderDetailProduct.getProduct()));
                                }

                            } else {
                                break;
                            }
                        }
                    }

                    amount = amount.add(new BigDecimal(Float.toString(detail.getAmount())));
                    break;
                }
            }
        }

        returnProduct.setAmount(amount.subtract(new BigDecimal(Float.toString(returnProduct.getFee()))).floatValue());

        return returnProduct;
    }

    /**
     * 设置采购退货单
     * @param returnProduct
     * @return
     */
    public ReturnProduct setPurchaseReturnProduct(ReturnProduct returnProduct) {
        Purchase queryPurchase = new Purchase(returnProduct.getEntityId());
        Purchase purchase = ((List<Purchase>)writer.gson.fromJson(erpClient.query(queryPurchase.getClass().getSimpleName(), writer.gson.toJson(queryPurchase)),
                new TypeToken<List<Purchase>>(){}.getType())).get(0);
        User user = getUserBySessionId(returnProduct.getSessionId());

        returnProduct.setEntityNo(purchase.getNo());
        /**
         * 由于系统退货用户与客户退货用户类型不同，这里暂时把系统用户简单转换为客户用户里来保存
         */
        returnProduct.setUser(writer.gson.fromJson(writer.gson.toJson(user), com.hzg.customer.User.class));
        returnProduct.setInputDate(dateUtil.getSecondCurrentTimestamp());
        returnProduct.setState(AfterSaleServiceConstant.returnProduct_purchase_state_apply);

        BigDecimal amount = new BigDecimal(0);

        for (ReturnProductDetail detail : returnProduct.getDetails()) {
            for (PurchaseDetail purchaseDetail : purchase.getDetails()) {
                if (detail.getProductNo().equals(purchaseDetail.getProductNo())) {

                    detail.setState(AfterSaleServiceConstant.returnProduct_detail_state_unReturn);
                    detail.setUnit(purchaseDetail.getUnit());

                    detail.setPrice(purchaseDetail.getPrice());
                    detail.setAmount(new BigDecimal(Float.toString(detail.getPrice())).
                            multiply(new BigDecimal(Float.toString(detail.getQuantity()))).floatValue());

                    detail.setReturnProductDetailProducts(new HashSet<>());
                    for (PurchaseDetailProduct purchaseDetailProduct : purchaseDetail.getPurchaseDetailProducts()) {
                        Integer productState = purchaseDetailProduct.getProduct().getState();

                        if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                                detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {

                            /**
                             * 采购完成，入库，订单商品退货后，这些商品可以提交采购退货
                             */
                            if (productState.compareTo(ErpConstant.product_state_purchase_close) == 0 ||
                                productState.compareTo(ErpConstant.product_state_stockIn) == 0 ||
                                productState.compareTo(ErpConstant.product_state_stockIn_part) == 0 ||
                                productState.compareTo(ErpConstant.product_state_onReturnProduct_part) == 0 ||
                                productState.compareTo(ErpConstant.product_state_returnedProduct) == 0 ||
                                productState.compareTo(ErpConstant.product_state_returnedProduct_part) == 0) {
                                detail.getReturnProductDetailProducts().add(new ReturnProductDetailProduct(purchaseDetailProduct.getProduct()));
                            }

                        } else {
                            if (detail.getReturnProductDetailProducts().size() <= detail.getQuantity()) {
                                if (productState.compareTo(ErpConstant.product_state_purchase_close) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_stockIn) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_stockIn_part) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_onReturnProduct_part) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_returnedProduct) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_returnedProduct_part) == 0) {
                                    detail.getReturnProductDetailProducts().add(new ReturnProductDetailProduct(purchaseDetailProduct.getProduct()));
                                }

                            } else {
                                break;
                            }
                        }
                    }

                    amount = amount.add(new BigDecimal(Float.toString(detail.getAmount())));
                    break;
                }
            }
        }

        returnProduct.setAmount(amount.subtract(new BigDecimal(Float.toString(returnProduct.getFee()))).floatValue());

        return returnProduct;
    }

    /**
     * 检查是否可退货
     * @param returnProduct
     * @return
     */
    public String isCanReturn(ReturnProduct returnProduct) {
        String canReturnMsg = "";

        if (returnProduct.getEntity().equals(OrderConstant.order)) {
            Order order = (Order) orderDao.queryById(returnProduct.getEntityId(), Order.class);
            if (order.getType().compareTo(OrderConstant.order_type_private) == 0) {
                canReturnMsg += "订单：" + order.getNo() + "为私人订制单，不能退货";
            }
            if (order.getType().compareTo(OrderConstant.order_type_assist_process) == 0) {
                canReturnMsg += "订单：" + order.getNo() + "为加工单，不能退货";
            }
        }

        if (canReturnMsg.equals("")) {
            for (ReturnProductDetail detail : returnProduct.getDetails()) {
                if (detail.getQuantity() == 0) {
                    canReturnMsg += "商品：" + detail.getProductNo() + "申请退货数量为: 0;";
                }
            }
        }

        if (canReturnMsg.equals("")) {
            canReturnMsg += checkReturnProductQuantity(returnProduct);
        }

        if (!canReturnMsg.equals("")) {
            canReturnMsg = "尊敬的顾客你好，你提交的退货申请单 " + returnProduct.getNo() + "申请退货失败。具体原因是：" +
                    canReturnMsg + "如有帮助需要，请联系我公司客服人员处理";
        }

        return canReturnMsg;
    }

    public String checkReturnProductQuantity(ReturnProduct returnProduct) {
        String canReturnMsg = "";

        ReturnProduct queryReturnProduct = new ReturnProduct();
        queryReturnProduct.setState(AfterSaleServiceConstant.returnProduct_state_refund);
        queryReturnProduct.setEntity(returnProduct.getEntity());
        queryReturnProduct.setEntityId(returnProduct.getEntityId());
        List<ReturnProduct> returnProducts = orderDao.query(queryReturnProduct);

        for (ReturnProductDetail detail : returnProduct.getDetails()) {
            if (detail.getReturnProductDetailProducts() == null ||
                    detail.getReturnProductDetailProducts().isEmpty()) {
                canReturnMsg += "商品：" + detail.getProductNo() + "可退数量为0，不能退货。";
                break;
            }

            if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                    detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {
                if (detail.getReturnProductDetailProducts().size() != 1) {
                    canReturnMsg += "商品：" + detail.getProductNo() + "为单件商品，但退货有多件商品，不能退货。";
                    break;
                }

            } else {
                if (detail.getReturnProductDetailProducts().size() != detail.getQuantity().intValue()) {
                    canReturnMsg += "商品：" + detail.getProductNo() + "实际可退" + detail.getReturnProductDetailProducts().size() +
                            "件，但申请退货件数为" + detail.getQuantity().intValue() + "，不能退货。";
                    break;
                }
            }
        }

        if (canReturnMsg.equals("")) {
            if (returnProduct.getEntity().equals(OrderConstant.order)) {
                Order order = (Order) orderDao.queryById(returnProduct.getEntityId(), Order.class);

                for (ReturnProductDetail detail : returnProduct.getDetails()) {
                    Float returnedProductQuantity = 0f;
                    for (ReturnProduct returnedProduct : returnProducts) {
                        for (ReturnProductDetail returnedDetail : returnedProduct.getDetails()) {
                            if (returnedDetail.getProductNo().equals(detail.getProductNo())) {

                                returnedProductQuantity = new BigDecimal(Float.toString(returnedProductQuantity)).
                                        add(new BigDecimal(Float.toString(returnedDetail.getQuantity()))).floatValue();

                            }
                        }
                    }

                    Float canReturnProductQuantity = 0f;
                    for (OrderDetail orderDetail : order.getDetails()) {
                        if (orderDetail.getProductNo().equals(detail.getProductNo())) {

                            canReturnProductQuantity = new BigDecimal(Float.toString(orderDetail.getQuantity())).
                                    subtract(new BigDecimal(Float.toString(returnedProductQuantity))).floatValue();
                        }
                    }

                    if (detail.getQuantity().compareTo(canReturnProductQuantity) > 0) {
                        canReturnMsg += "商品：" + detail.getProductNo() + "申请退货数量为: " + detail.getQuantity() +
                                "，而实际可退数量为: " + canReturnProductQuantity + "。";
                        break;
                    }
                }

            } else if (returnProduct.getEntity().equals(ErpConstant.purchase)) {
                Purchase queryPurchase = new Purchase(returnProduct.getEntityId());
                Purchase purchase = ((List<Purchase>)writer.gson.fromJson(erpClient.query(queryPurchase.getClass().getSimpleName(), writer.gson.toJson(queryPurchase)),
                        new TypeToken<List<Purchase>>(){}.getType())).get(0);

                for (ReturnProductDetail detail : returnProduct.getDetails()) {
                    Float returnedProductQuantity = 0f;
                    for (ReturnProduct returnedProduct : returnProducts) {
                        for (ReturnProductDetail returnedDetail : returnedProduct.getDetails()) {
                            if (returnedDetail.getProductNo().equals(detail.getProductNo())) {

                                returnedProductQuantity = new BigDecimal(Float.toString(returnedProductQuantity)).
                                        add(new BigDecimal(Float.toString(returnedDetail.getQuantity()))).floatValue();

                            }
                        }
                    }

                    Float canReturnProductQuantity = 0f;
                    for (PurchaseDetail purchaseDetail : purchase.getDetails()) {
                        if (purchaseDetail.getProductNo().equals(detail.getProductNo())) {

                            canReturnProductQuantity = new BigDecimal(Float.toString(purchaseDetail.getQuantity())).
                                    subtract(new BigDecimal(Float.toString(returnedProductQuantity))).floatValue();
                        }
                    }

                    if (detail.getQuantity().compareTo(canReturnProductQuantity) > 0) {
                        canReturnMsg += "商品：" + detail.getProductNo() + "申请退货数量为: " + detail.getQuantity() +
                                "，而实际可退数量为: " + canReturnProductQuantity + "。";
                        break;
                    }
                }
            }
        }

        return canReturnMsg;
    }

    public String doReturnProductBusinessAction(String json, Integer returnProductPassState, Integer actionPassState,
                                                Integer returnProductNotPassState, Integer actionNotPassState) {
        String result = CommonConstant.fail;

        Action action = writer.gson.fromJson(json, Action.class);
        ReturnProduct returnProduct = queryReturnProductById(action.getEntityId());

        if (action.getAuditResult().equals(CommonConstant.Y)) {
            returnProduct.setState(returnProductPassState);
            action.setType(actionPassState);

        } else {
            returnProduct.setState(returnProductNotPassState);
            action.setType(actionNotPassState);

            for (ReturnProductDetail returnProductDetail : returnProduct.getDetails()) {
                returnProductDetail.setState(AfterSaleServiceConstant.returnProduct_detail_state_cannotReturn);
                result += afterSaleServiceDao.updateById(returnProductDetail.getId(), returnProductDetail);
            }

            result += recoverProductState(returnProduct);
        }

        result += afterSaleServiceDao.updateById(returnProduct.getId(), returnProduct);

        action.setEntity(AfterSaleServiceConstant.returnProduct);
        action.setInputer(getUserBySessionId(action.getSessionId()));
        action.setInputDate(dateUtil.getSecondCurrentTimestamp());
        result += afterSaleServiceDao.save(action);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public ReturnProduct queryReturnProductById(Integer id) {
        ReturnProduct returnProduct = (ReturnProduct) afterSaleServiceDao.queryById(id, ReturnProduct.class);
        for (ReturnProductDetail detail : returnProduct.getDetails()) {
            ReturnProductDetailProduct detailProduct = new ReturnProductDetailProduct();
            detailProduct.setReturnProductDetail(detail);
            List<ReturnProductDetailProduct> detailProducts = afterSaleServiceDao.query(detailProduct);
            detail.setReturnProductDetailProducts(new HashSet<>(detailProducts));
        }

        if (returnProduct.getEntity().equals(ErpConstant.purchase)) {
            returnProduct.setSysUser(getPurchaseReturnProductUser(returnProduct));
        }

        return returnProduct;
    }

    public User getUserBySessionId(String sessionId){
        return (User)afterSaleServiceDao.getFromRedis((String)afterSaleServiceDao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId));
    }


    /**
     * 退货单退款
     * 1.设置退货单及商品为退货状态
     * 2.设置订单为退款状态
     * 3.调用退款接口退款，生成退款记录
     *
     * @param returnProduct
     */
    public String refundReturnProduct(ReturnProduct returnProduct) {
        String result = CommonConstant.fail;

        String isCanReturnMsg = checkReturnProductQuantity(returnProduct);
        if (!isCanReturnMsg.equals("")) {
            return CommonConstant.fail + isCanReturnMsg;
        }

        returnProduct.setDate(dateUtil.getSecondCurrentTimestamp());
        returnProduct.setState(AfterSaleServiceConstant.returnProduct_state_refund);
        result += afterSaleServiceDao.updateById(returnProduct.getId(), returnProduct);

        for (ReturnProductDetail returnProductDetail : returnProduct.getDetails()) {
            returnProductDetail.setState(AfterSaleServiceConstant.returnProduct_detail_state_returned);
            result += afterSaleServiceDao.updateById(returnProductDetail.getId(), returnProductDetail);

            ReturnProductDetail dbReturnProductDetail = (ReturnProductDetail) afterSaleServiceDao.queryById(returnProductDetail.getId(), returnProductDetail.getClass());
            returnProductDetail.setReturnProductDetailProducts(dbReturnProductDetail.getReturnProductDetailProducts());
        }

        sessionFactory.getCurrentSession().getTransaction().commit();
        sessionFactory.getCurrentSession().getTransaction().begin();

        if (returnProduct.getEntity().equals(OrderConstant.order)) {
            result += setProductsReturnState(ErpConstant.product_action_name_setProductsReturned, returnProduct);

        } else if (returnProduct.getEntity().equals(ErpConstant.purchase)) {
            result += setProductsReturnState(ErpConstant.product_action_name_setPurchaseProductsReturned, returnProduct);
        }

        /**
         * 调用退款接口退款
         */
        Pay pay = new Pay();
        pay.setEntity(returnProduct.getEntity());
        pay.setEntityId(returnProduct.getEntityId());
        pay.setState(PayConstants.pay_state_success);
        result += writer.getResult(payClient.refund(AfterSaleServiceConstant.returnProduct, returnProduct.getId(), returnProduct.getNo(),
                returnProduct.getAmount(), writer.gson.toJson(pay)));

        if (returnProduct.getEntity().equals(OrderConstant.order)) {
            result += orderService.setOrderRefundState(new Order(returnProduct.getEntityId()));
        }
        /**
         * 商品入库，商品状态调整为在售状态
         */
/*        if (returnProduct.getEntity().equals(OrderConstant.order)) {
            if (!result.substring(CommonConstant.fail.length()).contains(CommonConstant.fail)) {
                result += stockIn(returnProduct);
            }

            if (!result.substring(CommonConstant.fail.length()).contains(CommonConstant.fail)) {
                result += setProductEdit(returnProduct);
            }

            if (!result.substring(CommonConstant.fail.length()).contains(CommonConstant.fail)) {
                result += upShelf(returnProduct);
            }
        }*/

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 还原商品状态
     * @param returnProduct
     * @return
     */
    private String recoverProductState(ReturnProduct returnProduct) {
        List<Product> products = new ArrayList<>();
        for (ReturnProductDetail detail : returnProduct.getDetails()) {
            for (ReturnProductDetailProduct detailProduct : detail.getReturnProductDetailProducts()) {
                products.add(detailProduct.getProduct());
            }
        }

        return erpClient.business(ErpConstant.product_action_name_recoverState, writer.gson.toJson(products));
    }


    /**
     * 设置商品为退货状态
     * @param returnProduct
     * @return
     */
    private String setProductsReturnState(String productsReturnStateAction, ReturnProduct returnProduct) {
        List<Product> products = new ArrayList<>();
        for (ReturnProductDetail detail : returnProduct.getDetails()) {
            for (ReturnProductDetailProduct detailProduct : detail.getReturnProductDetailProducts()) {
                products.add(detailProduct.getProduct());
            }
        }

        return writer.getResult(erpClient.business(productsReturnStateAction, writer.gson.toJson(products)));
    }

    /**
     * 获取商品已退数量
     *
     * 商品已退数量计算规则：
     * 由于销售是先销售上次已退货商品，因此销售商品可能只包含上次已退货商品，也可能同时包含上次已退货商品及未做过退货的商品，
     * 因此一次销售的销售数量 = 前次已退货数量 + 未做过退货商品数量（可能为0）。
     * 因此一次销售及退货后的实际退货数量为: 1.如果 该次退货数量 >= 该次销售数量， 则 一次销售及退货后的实际退货数量 = 该次退货数量 - 该次销售数量；
     *                                   2. 如果 该次退货数量 < 该次销售数量， 则 一次销售及退货后的实际退货数量 = 0；
     *
     * @param product
     * @return
     */
    public Float getProductReturnedQuantity(Product product) {
        /**
         * 先把订单明细，退货明细按时间由早到晚排序
         */
        List<Object> details = new ArrayList<>();
        List<OrderDetail> orderDetails = orderService.getOrderSoldDetails(product);
        System.out.println("sold order details:" + writer.gson.toJson(orderDetails));
        List<ReturnProductDetail> returnedProductDetails = getReturnedProductDetails(product);
        System.out.println("returnedProduct details:" + writer.gson.toJson(returnedProductDetails));
        int i = orderDetails.size()-1, iStartPosition = orderDetails.size()-1,
            j = returnedProductDetails.size()-1, jStartPosition = returnedProductDetails.size()-1;

        for (; i >= 0; i--) {
            boolean isLarger = false;
            for (; j >= 0; j--) {
                if (returnedProductDetails.get(j).getReturnProduct().getDate().compareTo(orderDetails.get(i).getOrder().getSoldDate()) < 0) {
                    isLarger = true;

                    details.add(returnedProductDetails.get(j));
                    iStartPosition = i + 1;
                    jStartPosition = j - 1;
                    break;
                }
            }

            if (!isLarger) {
                details.add(orderDetails.get(i));
            } else {
                i = iStartPosition;
            }
            j = jStartPosition;
        }
        for (; j >= 0; j--) {
            details.add(returnedProductDetails.get(j));
        }


        Float quantity = 0f;

        /**
         * 第一个元素是最开始的订单明细，它不包含退货商品，所以排除
         */
        for (int k = 1; k < details.size(); k++) {
            Float itemQuantity = 0f;
            String unit = "";

            if (details.get(k) instanceof ReturnProductDetail) {
                unit = ((ReturnProductDetail)details.get(k)).getUnit();
            } else if (details.get(k) instanceof OrderDetail) {
                unit = ((OrderDetail)details.get(k)).getUnit();
            }

            if (unit.equals(ErpConstant.unit_g) || unit.equals(ErpConstant.unit_kg) ||
                    unit.equals(ErpConstant.unit_ct) || unit.equals(ErpConstant.unit_oz)) {

                if (details.get(k) instanceof ReturnProductDetail) {
                    itemQuantity = ((ReturnProductDetail)details.get(k)).getQuantity();
                } else if (details.get(k) instanceof OrderDetail) {
                    itemQuantity = ((OrderDetail)details.get(k)).getQuantity();
                }
            } else {
                itemQuantity = 1f;
            }


            if (details.get(k) instanceof ReturnProductDetail) {
                quantity = new BigDecimal(Float.toString(quantity)).add(new BigDecimal(Float.toString(itemQuantity))).floatValue();
            } else if (details.get(k) instanceof OrderDetail) {
                quantity = new BigDecimal(Float.toString(quantity)).subtract(new BigDecimal(Float.toString(itemQuantity))).floatValue();
                if (quantity.compareTo(0f) < 0) {
                    quantity = 0f;
                }
            }
        }

        return quantity;
    }

    /**
     * 获取商品重复已退数量
     * @param product
     * @return
     */
    public Float getProductRepeatReturnedQuantity(Product product) {
        return getQuantity(getReturnedProductDetails(product));
    }

    /**
     * 获取商品在退数量
     * 商品在退数量没有重复在退数量这种情况，因为一次退货的完整过程中，退货记录的状态最终都是退货完成状态，不会存在
     * 在退状态这种中间状态。所以同一商品多次退货，在最后一次退货未完成时，多次的退货记录中的最后一条是在退状态，其
     * 他是退货完成状态；多次退货完成后，退货记录里就没有在退状态。
     * @param product
     * @return
     */
    public Float getProductOnReturnQuantity(Product product) {
        return getQuantity(getOnReturnProductDetails(product));
    }

    public Float getQuantity( List<ReturnProductDetail> details) {
        Float quantity = 0f;
        for (ReturnProductDetail detail : details) {
            Float itemQuantity;
            if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                    detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {
                itemQuantity = detail.getQuantity();
            } else {
                itemQuantity = 1f;
            }

            quantity = new BigDecimal(Float.toString(quantity)).add(new BigDecimal(Float.toString(itemQuantity))).floatValue();
        }


        return quantity;
    }

    /**
     * 获取商品采购退货数量
     * 采购退货的商品不存在重复采购退货，所以没有重复退货数量
     * @param product
     * @return
     */
    public Float getPurchaseProductReturnedQuantity (Product product) {
        return getQuantity(getPurchaseReturnedProductDetails(product));
    }

    /**
     * 获取商品采购在退数量
     * @param product
     * @return
     */
    public Float getPurchaseProductOnReturnQuantity (Product product) {
        return getQuantity(getPurchaseOnReturnProductDetails(product));
    }

    private String stockIn(ReturnProduct returnProduct) {
        String result = CommonConstant.fail;

        Map<String, Object> saveResult = writer.gson.fromJson(saveStockIn(returnProduct), new TypeToken<Map<String, Object>>(){}.getType());
        result += (String) saveResult.get(CommonConstant.result);

        if (saveResult.get(CommonConstant.result).equals(CommonConstant.success)) {
            Action action = new Action();
            /**
             * gson 默认将数字转换为 Double 类型
             */
            action.setEntityId(((Double)saveResult.get(CommonConstant.id)).intValue());
            action.setSessionId(returnProduct.getSessionId());
            result += writer.getResult(erpClient.business(ErpConstant.stockInOut_action_name_inProduct, writer.gson.toJson(action)));
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 插入入库数据
     */
    private String saveStockIn(ReturnProduct returnProduct) {
        StockInOut stockIn = new StockInOut();
        stockIn.setNo(((Map<String, String>)writer.gson.fromJson(erpClient.getNo(ErpConstant.no_stockInOut_perfix), new TypeToken<Map<String, String>>() {}.getType())).get(CommonConstant.no));
        stockIn.setType(ErpConstant.stockInOut_type_returnProduct);

        stockIn.setState(ErpConstant.stockInOut_state_finished);
        stockIn.setDate(dateUtil.getSecondCurrentTimestamp());
        stockIn.setInputDate(dateUtil.getSecondCurrentTimestamp());
        stockIn.setDescribes("退货单：" + returnProduct.getNo() + "退款完成，货品自动入库");
        stockIn.setWarehouse(orderService.getWarehouseByUser(getUserBySessionId(returnProduct.getSessionId())));

        Set<StockInOutDetail> stockInDetails = new HashSet<>();
        for (ReturnProductDetail detail : returnProduct.getDetails()) {
            StockInOutDetail stockInDetail = new StockInOutDetail();
            stockInDetail.setProductNo(detail.getProductNo());
            stockInDetail.setQuantity(detail.getQuantity());
            stockInDetail.setUnit(detail.getUnit());

            Set<StockInOutDetailProduct> detailProducts = new HashSet<>();
            for (ReturnProductDetailProduct orderDetailProduct : detail.getReturnProductDetailProducts()) {
                StockInOutDetailProduct detailProduct = new StockInOutDetailProduct();
                detailProduct.setProduct(orderDetailProduct.getProduct());
                detailProducts.add(detailProduct);
            }
            stockInDetail.setStockInOutDetailProducts(detailProducts);

            stockInDetails.add(stockInDetail);
        }

        stockIn.setDetails(stockInDetails);
        return  erpClient.save(stockIn.getClass().getSimpleName(), writer.gson.toJson(stockIn));
    }

    public String setProductEdit(ReturnProduct returnProduct) {
        List<Product> products = new ArrayList<>();
        for (ReturnProductDetail detail : returnProduct.getDetails()) {
            for (ReturnProductDetailProduct detailProduct : detail.getReturnProductDetailProducts()) {
                products.add(detailProduct.getProduct());
            }
        }

        return writer.getResult(erpClient.business(ErpConstant.product_action_name_setProductEdit, writer.gson.toJson(products)));
    }

    public String upShelf(ReturnProduct returnProduct) {
        List<Integer> productIds = new ArrayList<>();
        for (ReturnProductDetail detail : returnProduct.getDetails()) {
            for (ReturnProductDetailProduct detailProduct : detail.getReturnProductDetailProducts()) {
                productIds.add(detailProduct.getProduct().getId());
            }
        }

        Action action = new Action();
        action.setEntityIds(productIds);
        action.setSessionId(returnProduct.getSessionId());

        return writer.getResult(erpClient.business(ErpConstant.product_action_name_upShelf, writer.gson.toJson(action)));
    }

    public List<ReturnProductDetail> getReturnedProductDetails(Product product){
        List<ReturnProductDetail> details = new ArrayList<>();

        List<ReturnProductDetail> dbDetails = getReturnProductDetails(product);
        for (ReturnProductDetail detail : dbDetails) {
            if (detail.getState().compareTo(AfterSaleServiceConstant.returnProduct_detail_state_returned) == 0) {
                details.add(detail);
            }
        }

        return details;
    }

    /**
     * 商品在退状态是指申请退货到退货完成之前的状态
     * @param product
     * @return
     */
    public List<ReturnProductDetail> getOnReturnProductDetails(Product product){
        List<ReturnProductDetail> onReturnDetails = new ArrayList<>();

        Iterator<ReturnProductDetail> iterator = getReturnProductDetails(product).iterator();
        while (iterator.hasNext()) {
            ReturnProductDetail detail = iterator.next();
            if (detail.getReturnProduct().getState().compareTo(AfterSaleServiceConstant.returnProduct_state_apply) == 0 ||
                detail.getReturnProduct().getState().compareTo(AfterSaleServiceConstant.returnProduct_state_salePass) == 0 ||
                detail.getReturnProduct().getState().compareTo(AfterSaleServiceConstant.returnProduct_state_directorPass) == 0 ||
                detail.getReturnProduct().getState().compareTo(AfterSaleServiceConstant.returnProduct_state_warehousingPass) == 0) {
                onReturnDetails.add(detail);
            }
        }

        return onReturnDetails;
    }

    /**
     * 获取采购退货已退商品明细
     * @param product
     * @return
     */
    public List<ReturnProductDetail> getPurchaseReturnedProductDetails(Product product){
        List<ReturnProductDetail> returnedDetail = new ArrayList<>();

        Iterator<ReturnProductDetail> iterator = getReturnProductDetails(product).iterator();
        while (iterator.hasNext()) {
            ReturnProductDetail detail = iterator.next();
            if (detail.getState().compareTo(AfterSaleServiceConstant.returnProduct_purchase_state_refund) == 0) {
                returnedDetail.add(detail);
            }
        }

        return returnedDetail;
    }

    /**
     * 获取采购退货在退商品明细
     * @param product
     * @return
     */
    public List<ReturnProductDetail> getPurchaseOnReturnProductDetails(Product product){
        List<ReturnProductDetail> onReturnDetails = new ArrayList<>();

        Iterator<ReturnProductDetail> iterator = getReturnProductDetails(product).iterator();
        while (iterator.hasNext()) {
            ReturnProductDetail detail = iterator.next();
            if (detail.getReturnProduct().getState().compareTo(AfterSaleServiceConstant.returnProduct_state_purchase_purchasePass) == 0 ||
                detail.getReturnProduct().getState().compareTo(AfterSaleServiceConstant.returnProduct_state_purchase_warehousingPass) == 0 ||
                detail.getReturnProduct().getState().compareTo(AfterSaleServiceConstant.returnProduct_state_purchase_supplierPass) == 0) {
                onReturnDetails.add(detail);
            }
        }

        return onReturnDetails;
    }

    public List<ReturnProductDetail> getReturnProductDetails(Product product) {
        ReturnProductDetailProduct queryDetailProduct = new ReturnProductDetailProduct();
        queryDetailProduct.setProduct(product);
        List<ReturnProductDetailProduct> detailProducts = afterSaleServiceDao.query(queryDetailProduct);

        List<ReturnProductDetail> details = new ArrayList<>();

        for (ReturnProductDetailProduct detailProduct : detailProducts) {
            boolean isSameDetail = false;

            for (ReturnProductDetail detail : details) {
                if (detail.getId().compareTo(detailProduct.getReturnProductDetail().getId()) == 0) {
                    isSameDetail = true;
                }
            }

            if (!isSameDetail) {
                details.add((ReturnProductDetail)afterSaleServiceDao.queryById(detailProduct.getReturnProductDetail().getId(), detailProduct.getReturnProductDetail().getClass()));
            }
        }

        return details;
    }

    public ReturnProduct getLastValidReturnProductByProduct(Product product) {
        ReturnProductDetailProduct detailProduct = new ReturnProductDetailProduct();
        detailProduct.setProduct(product);
        List<ReturnProductDetailProduct> detailProducts = orderDao.query(detailProduct);

        for (ReturnProductDetailProduct ele : detailProducts) {
            ReturnProductDetail detail = (ReturnProductDetail) orderDao.queryById(ele.getReturnProductDetail().getId(), ReturnProductDetail.class);
            if (detail.getReturnProduct().getState().compareTo(AfterSaleServiceConstant.returnProduct_state_cancel) != 0) {
                return detail.getReturnProduct();
            }
        }

        return null;
    }




    /**
     *
     * ********************* 换货 **********************
     *
     */


    /**
     * 保存换货单
     * @param changeProduct
     * @return
     */
    public String saveChangeProduct(ChangeProduct changeProduct) {
        String result = CommonConstant.fail;
        logger.info("saveChangeProduct start");

        String canSellMsg = isChangeProductCanSell(changeProduct);
        if (!canSellMsg.equals("")) {
            return CommonConstant.fail + canSellMsg;
        }

        changeProduct = setChangeProduct(changeProduct);
        String isCanReturnMsg = isReturnProductCanReturn(changeProduct);
        if (!isCanReturnMsg.equals("")) {
            return CommonConstant.fail + isCanReturnMsg;
        }

        String isAmountRight = checkAmount(changeProduct);
        if (!isAmountRight.equals("")) {
            return CommonConstant.fail + isAmountRight;
        }

        result += afterSaleServiceDao.save(changeProduct);
        ChangeProduct idChangeProduct = new ChangeProduct(changeProduct.getId());
        ChangeProductDetail idChangeProductDetail = new ChangeProductDetail();

        for (ChangeProductDetail changeProductDetail : changeProduct.getDetails()) {
            changeProductDetail.setChangeProduct(idChangeProduct);
            result += afterSaleServiceDao.save(changeProductDetail);

            Set<ChangeProductDetailProduct> changeProductDetailProducts = new HashSet<>();

            idChangeProductDetail.setId(changeProductDetail.getId());
            for (ChangeProductDetailProduct changeProductDetailProduct : changeProductDetail.getChangeProductDetailProducts()) {
                changeProductDetailProduct.setChangeProductDetail(idChangeProductDetail);
                result += afterSaleServiceDao.save(changeProductDetailProduct);

                changeProductDetailProducts.add(changeProductDetailProduct);
            }

            changeProductDetail.setChangeProductDetailProducts(changeProductDetailProducts);
        }

        sessionFactory.getCurrentSession().getTransaction().commit();
        sessionFactory.getCurrentSession().getTransaction().begin();

        result += setProductsChangeReturnState(AfterSaleServiceConstant.changeProduct_detail_type_changeProduct,
                ErpConstant.product_action_name_setProductsOnChange, changeProduct);
        result += setProductsChangeReturnState(AfterSaleServiceConstant.changeProduct_detail_type_returnProduct,
                ErpConstant.product_action_name_setProductsOnChangeOnReturn, changeProduct);

        for (Pay pay : changeProduct.getPays()) {
            pay.setPayDate(dateUtil.getSecondCurrentTimestamp());

            pay.setState(PayConstants.pay_state_apply);
            pay.setBalanceType(PayConstants.balance_type_income);

            pay.setEntity(changeProduct.getClass().getSimpleName());
            pay.setEntityId(changeProduct.getId());
            pay.setEntityNo(changeProduct.getNo());
            pay.setUser(changeProduct.getUser());

            result += writer.getResult(payClient.save(Pay.class.getSimpleName(), writer.gson.toJson(pay)));
        }

        logger.info("saveChangeProduct end, result:" + result);
        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());

    }

    public String doChangeProductBusinessAction(String json, Integer changeProductPassState, Integer actionPassState,
                                                Integer changeProductNotPassState, Integer actionNotPassState) {
        String result = CommonConstant.fail;

        Action action = writer.gson.fromJson(json, Action.class);
        ChangeProduct changeProduct = queryChangeProductById(action.getEntityId());

        if (action.getAuditResult().equals(CommonConstant.Y)) {
            changeProduct.setState(changeProductPassState);
            action.setType(actionPassState);

        } else {
            changeProduct.setState(changeProductNotPassState);
            action.setType(actionNotPassState);

            for (ChangeProductDetail changeProductDetail : changeProduct.getDetails()) {
                changeProductDetail.setState(AfterSaleServiceConstant.changeProduct_detail_state_undo);
                result += afterSaleServiceDao.updateById(changeProductDetail.getId(), changeProductDetail);
            }

            result += recoverProductState(changeProduct);
        }

        result += afterSaleServiceDao.updateById(changeProduct.getId(), changeProduct);

        action.setEntity(AfterSaleServiceConstant.changeProduct);
        action.setInputer(getUserBySessionId(action.getSessionId()));
        action.setInputDate(dateUtil.getSecondCurrentTimestamp());
        result += afterSaleServiceDao.save(action);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }


    List<ChangeProduct> queryChangeProduct(ChangeProduct changeProduct) {
        List<ChangeProduct> changeProducts = afterSaleServiceDao.query(changeProduct);

        for (ChangeProduct ele : changeProducts) {
            for (ChangeProductDetail detail : ele.getDetails()) {
                ChangeProductDetailProduct detailProduct = new ChangeProductDetailProduct();
                detailProduct.setChangeProductDetail(detail);
                detail.setProduct(((ChangeProductDetailProduct)afterSaleServiceDao.query(detailProduct).get(0)).getProduct());
            }

            ele.setActions(queryActionsByChangeProduct(ele));
        }

        return changeProducts;
    }

    /**
     * 检查要换商品是否可销售
     * @param changeProduct
     * @return
     */
    public String isChangeProductCanSell(ChangeProduct changeProduct) {
        String canSellMsg = "";

        for (ChangeProductDetail detail : changeProduct.getDetails()) {
            if (detail.getType().compareTo(AfterSaleServiceConstant.changeProduct_detail_type_changeProduct) == 0) {
                Float sellableQuantity = orderService.getProductOnSaleQuantity(detail.getProductNo());

                if (sellableQuantity.compareTo(detail.getQuantity()) < 0) {
                    canSellMsg += detail.getQuantity() + detail.getUnit() + "编号为:" + detail.getProductNo() +
                            "的商品，但该商品可售数量为：" + sellableQuantity + "；";
                }
            }
        }

        if (!canSellMsg.equals("")) {
            canSellMsg = "尊敬的顾客你好，你要换" + canSellMsg + "申请换货失败。如有帮助需要，请联系我公司客服人员处理";
        }

        return canSellMsg;
    }

    /**
     * 检查要退商品是否可退货
     * @param changeProduct
     * @return
     */
    public String isReturnProductCanReturn(ChangeProduct changeProduct) {
        String canReturnMsg = "";

        if (changeProduct.getEntity().equals(OrderConstant.order)) {
            Order order = (Order) orderDao.queryById(changeProduct.getEntityId(), Order.class);
            if (order.getType().compareTo(OrderConstant.order_type_private) == 0) {
                canReturnMsg += "订单：" + order.getNo() + "为私人订制单，不能退货";
            }
            if (order.getType().compareTo(OrderConstant.order_type_assist_process) == 0) {
                canReturnMsg += "订单：" + order.getNo() + "为加工单，不能退货";
            }

            if (canReturnMsg.equals("")) {
                for (ChangeProductDetail detail : changeProduct.getDetails()) {
                    if (detail.getType().compareTo(AfterSaleServiceConstant.changeProduct_detail_type_returnProduct) == 0) {
                        if (detail.getQuantity() == 0) {
                            canReturnMsg += "商品：" + detail.getProductNo() + "申请退货数量为: 0;";
                        }
                    }
                }
            }

            if (canReturnMsg.equals("")) {
                canReturnMsg += checkReturnProductQuantity(changeProduct);
            }
        }

        if (!canReturnMsg.equals("")) {
            canReturnMsg = "尊敬的顾客你好，你提交的换货申请单 " + changeProduct.getNo() + "申请换货失败。具体原因是：" +
                    canReturnMsg + "如有帮助需要，请联系我公司客服人员处理";
        }

        return canReturnMsg;
    }

    public String checkReturnProductQuantity(ChangeProduct changeProduct) {
        String canReturnMsg = "";

        if (changeProduct.getEntity().equals(OrderConstant.order)) {
            Order order = (Order) orderDao.queryById(changeProduct.getEntityId(), Order.class);

            ReturnProduct queryReturnProduct = new ReturnProduct();
            queryReturnProduct.setState(AfterSaleServiceConstant.returnProduct_state_refund);
            queryReturnProduct.setEntity(changeProduct.getEntity());
            queryReturnProduct.setEntityId(changeProduct.getEntityId());
            List<ReturnProduct> returnProducts = orderDao.query(queryReturnProduct);

            for (ChangeProductDetail detail : changeProduct.getDetails()) {
                if (detail.getType().compareTo(AfterSaleServiceConstant.changeProduct_detail_type_returnProduct) == 0) {
                    if (detail.getChangeProductDetailProducts() == null ||
                            detail.getChangeProductDetailProducts().isEmpty()) {
                        canReturnMsg += "商品：" + detail.getProductNo() + "可退数量为0，不能退货。";
                        break;
                    }

                    if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                            detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {
                        if (detail.getChangeProductDetailProducts().size() != 1) {
                            canReturnMsg += "商品：" + detail.getProductNo() + "为单件商品，但退货有多件商品，不能退货。";
                            break;
                        }

                    } else {
                        if (detail.getChangeProductDetailProducts().size() != detail.getQuantity().intValue()) {
                            canReturnMsg += "商品：" + detail.getProductNo() + "实际可退" + detail.getChangeProductDetailProducts().size() +
                                    "件，但申请退货件数为" + detail.getQuantity().intValue() + "，不能退货。";
                            break;
                        }
                    }

                    Float returnedProductQuantity = 0f;
                    for (ReturnProduct returnedProduct : returnProducts) {
                        for (ReturnProductDetail returnedDetail : returnedProduct.getDetails()) {
                            if (returnedDetail.getProductNo().equals(detail.getProductNo())) {

                                returnedProductQuantity = new BigDecimal(Float.toString(returnedProductQuantity)).
                                        add(new BigDecimal(Float.toString(returnedDetail.getQuantity()))).floatValue();

                            }
                        }
                    }

                    Float canReturnProductQuantity = 0f;
                    for (OrderDetail orderDetail : order.getDetails()) {
                        if (orderDetail.getProductNo().equals(detail.getProductNo())) {

                            canReturnProductQuantity = new BigDecimal(Float.toString(orderDetail.getQuantity())).
                                    subtract(new BigDecimal(Float.toString(returnedProductQuantity))).floatValue();
                        }
                    }


                    if (detail.getQuantity().compareTo(canReturnProductQuantity) > 0) {
                        canReturnMsg += "商品：" + detail.getProductNo() + "申请退货数量为: " + detail.getQuantity() +
                                "，而实际可退数量为: " + canReturnProductQuantity + "。";
                        break;
                    }
                }
            }
        }

        return canReturnMsg;
    }

    /**
     * 检查金额是否正确
     * @param changeProduct
     * @return
     */
    public String checkAmount(ChangeProduct changeProduct) {
        String result = "";

        BigDecimal amount = new BigDecimal(0);

        for (ChangeProductDetail detail : changeProduct.getDetails()) {
            if (detail.getType().compareTo(AfterSaleServiceConstant.changeProduct_detail_type_changeProduct) == 0) {

                String queryJson = "{\"" + ErpConstant.product + "\":{\"no\":" + detail.getProductNo() + "}}";
                Map<String, Float> salePrice = writer.gson.fromJson(erpClient.querySalePrice(queryJson), new TypeToken<Map<String, Float>>(){}.getType());
                BigDecimal detailAmount = new BigDecimal(Float.toString(salePrice.get(ErpConstant.price))).
                        multiply(new BigDecimal(Float.toString(detail.getQuantity())));

                if (detailAmount.floatValue() != detail.getAmount() || detailAmount.floatValue() == 0f) {
                    result += "商品" + detail.getProductNo() + "支付金额不对;";
                    break;
                } else {
                    amount = amount.add(detailAmount);
                }

            } else if (detail.getType().compareTo(AfterSaleServiceConstant.changeProduct_detail_type_returnProduct) == 0) {
                amount = amount.subtract(new BigDecimal(Float.toString(detail.getAmount())));
            }
        }

        if (result.equals("")) {
            if (amount.add(new BigDecimal(Float.toString(changeProduct.getFee()))).floatValue() != changeProduct.getAmount()) {
                result =  "换货差价不对";
            }
        }

        if (result.equals("")) {
            BigDecimal paysAmount = new BigDecimal(0);
            for (Pay pay : changeProduct.getPays()) {
                paysAmount = paysAmount.add(new BigDecimal(Float.toString(pay.getAmount())));
            }

            if (paysAmount.floatValue() != changeProduct.getAmount()) {
                result =  "支付记录的总支付金额与换货差价金额不符";
            }
        }

        return result;
    }

    public ChangeProduct setChangeProduct(String json) {
        ChangeProduct changeProduct = new ChangeProduct();
        ReturnProduct returnProduct = setReturnProduct(json);

        changeProduct.setNo(afterSaleServiceDao.getNo(AfterSaleServiceConstant.no_changeProduct_perfix));
        changeProduct.setEntity(returnProduct.getEntity());
        changeProduct.setEntityId(returnProduct.getEntityId());
        changeProduct.setEntityNo(returnProduct.getEntityNo());
        changeProduct.setUser(returnProduct.getUser());
        changeProduct.setAmount(0f);

        Pay pay = new Pay();
        pay.setEntity(returnProduct.getEntity());
        pay.setEntityId(returnProduct.getEntityId());
        changeProduct.setPays(writer.gson.fromJson(payClient.query(pay.getClass().getSimpleName(), writer.gson.toJson(pay)), new TypeToken<List<Pay>>() {}.getType()));

        Set<ChangeProductDetail> details = new HashSet<>();
        for (ReturnProductDetail returnProductDetail : returnProduct.getDetails()) {
            ChangeProductDetail changeProductDetail = new ChangeProductDetail();

            changeProductDetail.setType(AfterSaleServiceConstant.changeProduct_detail_type_returnProduct);
            changeProductDetail.setProductNo(returnProductDetail.getProductNo());
            changeProductDetail.setQuantity(returnProductDetail.getQuantity());
            changeProductDetail.setUnit(returnProductDetail.getUnit());
            changeProductDetail.setPrice(returnProductDetail.getPrice());
            changeProductDetail.setAmount(returnProductDetail.getAmount());

            changeProductDetail.setProduct(returnProductDetail.getProduct());

            details.add(changeProductDetail);
        }
        changeProduct.setDetails(details);

        return changeProduct;
    }


    /**
     * 设置换货单
     * @param changeProduct
     * @return
     */
    public ChangeProduct setChangeProduct(ChangeProduct changeProduct) {
        if (changeProduct.getEntity().equals(OrderConstant.order)) {
            Order order = orderService.queryOrder(new Order(changeProduct.getEntityId())).get(0);

            changeProduct.setEntityNo(order.getNo());
            changeProduct.setUser(order.getUser());
            changeProduct.setInputDate(dateUtil.getSecondCurrentTimestamp());
            changeProduct.setState(AfterSaleServiceConstant.changeProduct_state_apply);

            for (ChangeProductDetail detail : changeProduct.getDetails()) {
                detail.setState(AfterSaleServiceConstant.changeProduct_detail_state_todo);

                if (detail.getType().compareTo(AfterSaleServiceConstant.changeProduct_detail_type_changeProduct) == 0) {
                    detail.setChangeProductDetailProducts(new HashSet<>());

                    Product queryProduct = new Product();
                    queryProduct.setNo(detail.getProductNo());
                    queryProduct.setState(ErpConstant.product_state_onSale);
                    List<Product> products = writer.gson.fromJson(erpClient.query(queryProduct.getClass().getSimpleName(), writer.gson.toJson(queryProduct)),
                            new TypeToken<List<Product>>(){}.getType());

                    int productQuantity = detail.getQuantity().intValue();
                    if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                            detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {
                        productQuantity = 1;
                    }

                    for (int i = 0; i < productQuantity; i++) {
                        ChangeProductDetailProduct detailProduct = new ChangeProductDetailProduct();
                        detailProduct.setProduct(products.get(i));
                        detail.getChangeProductDetailProducts().add(detailProduct);
                    }

                } else if (detail.getType().compareTo(AfterSaleServiceConstant.changeProduct_detail_type_returnProduct) == 0) {

                    for (OrderDetail orderDetail : order.getDetails()) {
                        if (detail.getProductNo().equals(orderDetail.getProductNo())) {

                            detail.setUnit(orderDetail.getUnit());

                            if (orderDetail.getPriceChange() == null) {
                                detail.setPrice(orderDetail.getProductPrice());
                            } else {
                                detail.setPrice(orderDetail.getPriceChange().getPrice());
                            }
                            detail.setAmount(new BigDecimal(Float.toString(detail.getPrice())).
                                    multiply(new BigDecimal(Float.toString(detail.getQuantity()))).floatValue());

                            detail.setChangeProductDetailProducts(new HashSet<>());
                            for (OrderDetailProduct orderDetailProduct : orderDetail.getOrderDetailProducts()) {
                                Integer productState = orderDetailProduct.getProduct().getState();

                                if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                                        detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {

                                    if (productState.compareTo(ErpConstant.product_state_stockOut) == 0 ||
                                            productState.compareTo(ErpConstant.product_state_stockOut_part) == 0 ||
                                            productState.compareTo(ErpConstant.product_state_sold) == 0 ||
                                            productState.compareTo(ErpConstant.product_state_sold_part) == 0 ||
                                            productState.compareTo(ErpConstant.product_state_shipped) == 0 ||
                                            productState.compareTo(ErpConstant.product_state_shipped_part) == 0 ||
                                            productState.compareTo(ErpConstant.product_state_onReturnProduct_part) == 0 ||
                                            productState.compareTo(ErpConstant.product_state_onChangeOnReturnProduct_part) == 0 ||
                                            productState.compareTo(ErpConstant.product_state_returnedProduct_part) == 0) {
                                        detail.getChangeProductDetailProducts().add(new ChangeProductDetailProduct(orderDetailProduct.getProduct()));
                                    }

                                } else {
                                    if (detail.getChangeProductDetailProducts().size() <= detail.getQuantity()) {
                                        if (productState.compareTo(ErpConstant.product_state_stockOut) == 0 ||
                                                productState.compareTo(ErpConstant.product_state_stockOut_part) == 0 ||
                                                productState.compareTo(ErpConstant.product_state_sold) == 0 ||
                                                productState.compareTo(ErpConstant.product_state_sold_part) == 0 ||
                                                productState.compareTo(ErpConstant.product_state_shipped) == 0 ||
                                                productState.compareTo(ErpConstant.product_state_shipped_part) == 0 ||
                                                productState.compareTo(ErpConstant.product_state_onReturnProduct_part) == 0 ||
                                                productState.compareTo(ErpConstant.product_state_onChangeOnReturnProduct_part) == 0 ||
                                                productState.compareTo(ErpConstant.product_state_returnedProduct_part) == 0) {
                                            detail.getChangeProductDetailProducts().add(new ChangeProductDetailProduct(orderDetailProduct.getProduct()));
                                        }

                                    } else {
                                        break;
                                    }
                                }
                            }

                            break;
                        }
                    }
                }
            }
        }

        return changeProduct;
    }

    private String setProductsChangeReturnState(Integer detailType, String productsStateAction, ChangeProduct changeProduct) {
        List<Product> products = new ArrayList<>();
        for (ChangeProductDetail detail : changeProduct.getDetails()) {
            if (detail.getType().compareTo(detailType) == 0) {
                for (ChangeProductDetailProduct detailProduct : detail.getChangeProductDetailProducts()) {
                    products.add(detailProduct.getProduct());
                }
            }
        }

        return writer.getResult(erpClient.business(productsStateAction, writer.gson.toJson(products)));
    }

    /**
     * 还原商品状态
     * @param changeProduct
     * @return
     */
    private String recoverProductState(ChangeProduct changeProduct) {
        List<Product> products = new ArrayList<>();
        for (ChangeProductDetail detail : changeProduct.getDetails()) {
            for (ChangeProductDetailProduct detailProduct : detail.getChangeProductDetailProducts()) {
                products.add(detailProduct.getProduct());
            }
        }

        return writer.getResult(erpClient.business(ErpConstant.product_action_name_recoverState, writer.gson.toJson(products)));
    }

    public ChangeProduct queryChangeProductById(Integer id) {
        ChangeProduct changeProduct = (ChangeProduct) afterSaleServiceDao.queryById(id, ChangeProduct.class);
        for (ChangeProductDetail detail : changeProduct.getDetails()) {
            ChangeProductDetailProduct detailProduct = new ChangeProductDetailProduct();
            detailProduct.setChangeProductDetail(detail);
            List<ChangeProductDetailProduct> detailProducts = afterSaleServiceDao.query(detailProduct);
            detail.setChangeProductDetailProducts(new HashSet<>(detailProducts));
        }

        return changeProduct;
    }



    /**
     * 获取商品已换数量
     *
     * 商品已换数量计算规则：
     * 由于销售是先销售上次已换货商品，因此销售商品可能只包含上次已换货商品，也可能同时包含上次已换货商品及未做过换货的商品，
     * 因此一次销售的销售数量 = 前次已换货数量 + 未做过换货商品数量（可能为0）。
     * 因此一次销售及换货后的实际换货数量为: 1.如果 该次换货数量 >= 该次销售数量， 则 一次销售及换货后的实际换货数量 = 该次换货数量 - 该次销售数量；
     *                                   2. 如果 该次换货数量 < 该次销售数量， 则 一次销售及换货后的实际换货数量 = 0；
     *
     * @param product
     * @return
     */
    public Float getProductChangedQuantity(Product product) {
        /**
         * 先把订单明细，换货明细按时间由早到晚排序
         */
        List<Object> details = new ArrayList<>();
        List<OrderDetail> orderDetails = orderService.getOrderSoldDetails(product);
        List<ChangeProductDetail> changedProductDetails = getChangedProductDetails(product);
        int i = orderDetails.size()-1, iStartPosition = orderDetails.size()-1,
                j = changedProductDetails.size()-1, jStartPosition = changedProductDetails.size()-1;

        for (; i >= 0; i--) {
            boolean isLarger = false;
            for (; j >= 0; j--) {
                if (changedProductDetails.get(j).getChangeProduct().getDate().compareTo(orderDetails.get(i).getOrder().getSoldDate()) < 0) {
                    isLarger = true;

                    details.add(changedProductDetails.get(j));
                    iStartPosition = i + 1;
                    jStartPosition = j - 1;
                    break;
                }
            }

            if (!isLarger) {
                details.add(orderDetails.get(i));
            } else {
                i = iStartPosition;
            }
            j = jStartPosition;
        }
        for (; j >= 0; j--) {
            details.add(changedProductDetails.get(j));
        }


        Float quantity = 0f;

        /**
         * 第一个元素是最开始的订单明细，它不包含退货商品，所以排除
         */
        for (int k = 1; k < details.size(); k++) {
            Float itemQuantity = 0f;
            String unit = "";

            if (details.get(k) instanceof ChangeProductDetail) {
                unit = ((ChangeProductDetail)details.get(k)).getUnit();
            } else if (details.get(k) instanceof OrderDetail) {
                unit = ((OrderDetail)details.get(k)).getUnit();
            }

            if (unit.equals(ErpConstant.unit_g) || unit.equals(ErpConstant.unit_kg) ||
                    unit.equals(ErpConstant.unit_ct) || unit.equals(ErpConstant.unit_oz)) {

                if (details.get(k) instanceof ChangeProductDetail) {
                    itemQuantity = ((ChangeProductDetail)details.get(k)).getQuantity();
                } else if (details.get(k) instanceof OrderDetail) {
                    itemQuantity = ((OrderDetail)details.get(k)).getQuantity();
                }
            } else {
                itemQuantity = 1f;
            }


            if (details.get(k) instanceof ChangeProductDetail) {
                quantity = new BigDecimal(Float.toString(quantity)).add(new BigDecimal(Float.toString(itemQuantity))).floatValue();
            } else if (details.get(k) instanceof OrderDetail) {
                quantity = new BigDecimal(Float.toString(quantity)).subtract(new BigDecimal(Float.toString(itemQuantity))).floatValue();
                if (quantity.compareTo(0f) < 0) {
                    quantity = 0f;
                }
            }
        }

        return quantity;
    }

    /**
     * 获取商品在换数量
     * 商品在换数量没有重复在换数量这种情况，因为一次换货的完整过程中，换货记录的状态最终都是换货完成状态，不会存在
     * 在换状态这种中间状态。所以同一商品多次换货，在最后一次换货未完成时，多次的换货记录中的最后一条是在换状态，其
     * 他是换货完成状态；多次换货完成后，换货记录里就没有在换状态。
     * @param product
     * @return
     */
    public Float getProductOnChangeQuantity(Product product) {
        return getChangeProductQuantity(getOnChangeProductDetails(product));
    }

    /**
     * 获取换货的在退商品数量
     * 商品换货在退数量没有重复换货在退数量这种情况，因为一次换货的完整过程中，换货记录的状态最终都是换货完成状态，不会存在
     * 换货在退状态这种中间状态。所以同一商品多次换货，在最后一次换货未完成时，多次的换货记录中的最后一条是换货在退状态，其
     * 他是换货完成状态；多次换货完成后，换货记录里就没有换货在退状态。
     * @param product
     * @return
     */
    public Float getProductOnChangeOnReturnQuantity(Product product) {
        return getChangeProductQuantity(getOnChangeOnReturnProductDetails(product));
    }

    /**
     * 获取状态为已换的换货单明细
     * @param product
     * @return
     */
    public List<ChangeProductDetail> getChangedProductDetails(Product product){
        List<ChangeProductDetail> changedDetails = new ArrayList<>();

        Iterator<ChangeProductDetail> iterator = getChangeProductDetails(product).iterator();
        while (iterator.hasNext()) {
            ChangeProductDetail detail = iterator.next();
            if (detail.getState().compareTo(AfterSaleServiceConstant.changeProduct_detail_state_done) == 0) {
                changedDetails.add(detail);
            }
        }

        return changedDetails;
    }

    /**
     * 商品在换状态是指申请换货到换货完成之前的状态
     * @param product
     * @return
     */
    public List<ChangeProductDetail> getOnChangeProductDetails(Product product){
        List<ChangeProductDetail> onChangeDetails = new ArrayList<>();

        Iterator<ChangeProductDetail> iterator = getChangeProductDetails(product).iterator();
        while (iterator.hasNext()) {
            ChangeProductDetail detail = iterator.next();
            if (detail.getChangeProduct().getState().compareTo(AfterSaleServiceConstant.changeProduct_state_apply) == 0 ||
                    detail.getChangeProduct().getState().compareTo(AfterSaleServiceConstant.changeProduct_state_salePass) == 0 ||
                    detail.getChangeProduct().getState().compareTo(AfterSaleServiceConstant.changeProduct_state_directorPass) == 0 ||
                    detail.getChangeProduct().getState().compareTo(AfterSaleServiceConstant.changeProduct_state_warehousingPass) == 0) {
                onChangeDetails.add(detail);
            }
        }

        return onChangeDetails;
    }

    public List<ChangeProductDetail> getChangeProductDetails(Product product) {
        ChangeProductDetailProduct queryDetailProduct = new ChangeProductDetailProduct();
        queryDetailProduct.setProduct(product);
        List<ChangeProductDetailProduct> detailProducts = afterSaleServiceDao.query(queryDetailProduct);

        List<ChangeProductDetail> details = new ArrayList<>();

        for (ChangeProductDetailProduct detailProduct : detailProducts) {
            boolean isSameDetail = false;

            for (ChangeProductDetail detail : details) {
                if (detail.getId().compareTo(detailProduct.getChangeProductDetail().getId()) == 0) {
                    isSameDetail = true;
                }
            }

            if (!isSameDetail) {
                ChangeProductDetail dbDetail = (ChangeProductDetail)afterSaleServiceDao.queryById(detailProduct.getChangeProductDetail().getId(), detailProduct.getChangeProductDetail().getClass());
                if (dbDetail.getType().compareTo(AfterSaleServiceConstant.changeProduct_detail_type_changeProduct) == 0) {
                    details.add(dbDetail);
                }
            }
        }

        return details;
    }

    /**
     * 商品换货在退状态是指被退的商品在申请换货到换货完成之前的状态
     * @param product
     * @return
     */
    public List<ChangeProductDetail> getOnChangeOnReturnProductDetails(Product product){
        List<ChangeProductDetail> onChangeDetails = new ArrayList<>();

        Iterator<ChangeProductDetail> iterator = getChangeReturnProductDetails(product).iterator();
        while (iterator.hasNext()) {
            ChangeProductDetail detail = iterator.next();
            if (detail.getChangeProduct().getState().compareTo(AfterSaleServiceConstant.changeProduct_state_apply) == 0 ||
                    detail.getChangeProduct().getState().compareTo(AfterSaleServiceConstant.changeProduct_state_salePass) == 0 ||
                    detail.getChangeProduct().getState().compareTo(AfterSaleServiceConstant.changeProduct_state_directorPass) == 0 ||
                    detail.getChangeProduct().getState().compareTo(AfterSaleServiceConstant.changeProduct_state_warehousingPass) == 0) {
                onChangeDetails.add(detail);
            }
        }

        return onChangeDetails;
    }

    public List<ChangeProductDetail> getChangeReturnProductDetails(Product product) {
        ChangeProductDetailProduct queryDetailProduct = new ChangeProductDetailProduct();
        queryDetailProduct.setProduct(product);
        List<ChangeProductDetailProduct> detailProducts = afterSaleServiceDao.query(queryDetailProduct);

        List<ChangeProductDetail> details = new ArrayList<>();

        for (ChangeProductDetailProduct detailProduct : detailProducts) {
            boolean isSameDetail = false;

            for (ChangeProductDetail detail : details) {
                if (detail.getId().compareTo(detailProduct.getChangeProductDetail().getId()) == 0) {
                    isSameDetail = true;
                }
            }

            if (!isSameDetail) {
                ChangeProductDetail dbDetail = (ChangeProductDetail)afterSaleServiceDao.queryById(detailProduct.getChangeProductDetail().getId(), detailProduct.getChangeProductDetail().getClass());
                if (dbDetail.getType().compareTo(AfterSaleServiceConstant.changeProduct_detail_type_returnProduct) == 0) {
                    details.add(dbDetail);
                }
            }
        }

        return details;
    }

    public Float getChangeProductQuantity(List<ChangeProductDetail> details) {
        Float quantity = 0f;
        for (ChangeProductDetail detail : details) {
            Float itemQuantity;
            if (detail.getUnit().equals(ErpConstant.unit_g) || detail.getUnit().equals(ErpConstant.unit_kg) ||
                    detail.getUnit().equals(ErpConstant.unit_ct) || detail.getUnit().equals(ErpConstant.unit_oz)) {
                itemQuantity = detail.getQuantity();
            } else {
                itemQuantity = 1f;
            }

            quantity = new BigDecimal(Float.toString(quantity)).add(new BigDecimal(Float.toString(itemQuantity))).floatValue();
        }


        return quantity;
    }

    public ChangeProduct getLastValidChangeProductByProduct(Product product) {
        ChangeProductDetailProduct detailProduct = new ChangeProductDetailProduct();
        detailProduct.setProduct(product);
        List<ChangeProductDetailProduct> detailProducts = orderDao.query(detailProduct);

        for (ChangeProductDetailProduct ele : detailProducts) {
            ChangeProductDetail detail = (ChangeProductDetail) orderDao.queryById(ele.getChangeProductDetail().getId(), ChangeProductDetail.class);
            if (detail.getChangeProduct().getState().compareTo(AfterSaleServiceConstant.changeProduct_state_cancel) != 0) {
                return detail.getChangeProduct();
            }
        }

        return null;
    }

    /**
     * 换货完成
     * 1.根据换货商品生成换货订单
     * 2.设置换货订单已支付
     * 3.根据退货商品生成退货单
     * 4.设置退货单已退款
     *
     * @param changeProduct
     */
    public String changeProductComplete(ChangeProduct changeProduct) {
        String result = CommonConstant.fail;

        /**
         * 还原商品状态到换货之前的状态，这样才可以生成订单及退货
         */
        result += recoverProductState(changeProduct);

        /**
         * 生成订单及付款
         */
        Order order = generateOrderByChangeProduct(changeProduct);
        String saveOrderResult = orderClient.save(order.getClass().getSimpleName(), writer.gson.toJson(order));
        result += writer.getResult(saveOrderResult);

        if (!result.substring(CommonConstant.fail.length()).contains(CommonConstant.fail)) {
            List<Order> orders = writer.gson.fromJson(orderClient.unlimitedQuery(order.getClass().getSimpleName(), saveOrderResult),
                    new TypeToken<List<Order>>(){}.getType());
            Order dbOrder = orders.get(0);
            dbOrder.setSessionId(changeProduct.getSessionId());
            result += writer.getResult(orderClient.paid(writer.gson.toJson(dbOrder)));
        }

        /**
         * 生成退货单及退款
         */
        if (!result.substring(CommonConstant.fail.length()).contains(CommonConstant.fail)) {
            ReturnProduct returnProduct = generateReturnProductByChangeProduct(changeProduct);
            Map<String, Object> saveResultMap = writer.gson.fromJson(afterSaleServiceClient.save(returnProduct.getClass().getSimpleName(), writer.gson.toJson(returnProduct)),
                    new TypeToken<Map<String, Object>>(){}.getType());

            result += saveResultMap.get(CommonConstant.result).toString();
            if (!result.substring(CommonConstant.fail.length()).contains(CommonConstant.fail)) {
                Action action = new Action();
                action.setEntityId(((Double)saveResultMap.get(CommonConstant.id)).intValue());
                action.setAuditResult(CommonConstant.Y);
                action.setRemark(AfterSaleServiceConstant.action_remark_changeProduct_refund);
                action.setSessionId(changeProduct.getSessionId());

                result += writer.getResult(afterSaleServiceClient.business(AfterSaleServiceConstant.returnProduct_action_name_refund, writer.gson.toJson(action)));
            }
        }

        if (!result.substring(CommonConstant.fail.length()).contains(CommonConstant.fail)) {
            for (ChangeProductDetail detail : changeProduct.getDetails()) {
                detail.setState(AfterSaleServiceConstant.changeProduct_detail_state_done);
                result += afterSaleServiceDao.updateById(detail.getId(), detail);
            }

            changeProduct.setState(AfterSaleServiceConstant.changeProduct_state_complete);
            changeProduct.setDate(dateUtil.getSecondCurrentTimestamp());
            result += afterSaleServiceDao.updateById(changeProduct.getId(), changeProduct);

            /**
             * 删除换货支付记录（换货中的支付记录已在要换货订单里）
             */
            List<Pay> pays = getPaysByChangeProduct(changeProduct);
            for (Pay pay : pays) {
                result += writer.getResult(payClient.delete(pay.getClass().getSimpleName(), writer.gson.toJson(pay)));
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public Order generateOrderByChangeProduct(ChangeProduct changeProduct) {
        Float amount = 0f, payAmount =0f;

        List<Action> actions = queryActionsByChangeProduct(changeProduct);
        User user = actions.get(actions.size()-1).getInputer();

        Express express = null;
        Timestamp expressDate = new Timestamp(dateUtil.getDay(1).getTime());
        List<Pay> pays = new ArrayList<>();
        Set<OrderDetail> details = new HashSet<>();

        for (ChangeProductDetail detail : changeProduct.getDetails()) {
            if (detail.getType().compareTo(AfterSaleServiceConstant.changeProduct_detail_type_returnProduct) == 0) {
                ChangeProductDetail dbDetail = ((List<ChangeProductDetail>) afterSaleServiceDao.query(detail)).get(0);

                for (ChangeProductDetailProduct detailProduct : dbDetail.getChangeProductDetailProducts()) {
                    OrderDetailProduct orderDetailProduct = new OrderDetailProduct();
                    orderDetailProduct.setProduct(detailProduct.getProduct());

                    OrderDetail orderDetail = ((List<OrderDetailProduct>)orderDao.query(orderDetailProduct)).get(0).getOrderDetail();
                    express = orderDetail.getExpress();
                    pays = orderService.queryPaysByOrder(orderDetail.getOrder());
                    break;
                }
                break;
            }
        }

        for (ChangeProductDetail detail : changeProduct.getDetails()) {
            if (detail.getType().compareTo(AfterSaleServiceConstant.changeProduct_detail_type_changeProduct) == 0) {
                ChangeProductDetail dbDetail = ((List<ChangeProductDetail>) afterSaleServiceDao.query(detail)).get(0);

                Product product = null;
                for (ChangeProductDetailProduct detailProduct : dbDetail.getChangeProductDetailProducts()) {
                    product = ((List<Product>)writer.gson.fromJson(erpClient.query(detailProduct.getProduct().getClass().getSimpleName(), writer.gson.toJson(detailProduct.getProduct())),
                            new TypeToken<List<Product>>(){}.getType())).get(0);
                    break;
                }

                BigDecimal detailAmount = new BigDecimal(Float.toString(product.getPrice())).multiply(new BigDecimal(Float.toString(dbDetail.getQuantity())));

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setProductNo(product.getNo());
                orderDetail.setProductPrice(product.getFatePrice());
                orderDetail.setQuantity(dbDetail.getQuantity());
                orderDetail.setUnit(dbDetail.getUnit());

                orderDetail.setAmount(detailAmount.floatValue());
                orderDetail.setPayAmount(dbDetail.getAmount());
                orderDetail.setExpress(express);
                orderDetail.setExpressDate(expressDate);

                details.add(orderDetail);

                amount = new BigDecimal(Float.toString(amount)).add(detailAmount).floatValue();
                payAmount = new BigDecimal(Float.toString(payAmount)).add(new BigDecimal(Float.toString(dbDetail.getAmount()))).floatValue();
            }
        }

        Order order = new Order();
        order.setType(OrderConstant.order_type_changeProduct);
        order.setAmount(amount);
        order.setPayAmount(payAmount);

        order.setUser(changeProduct.getUser());
        order.setSaler(user);
        order.setDetails(details);

        List<Pay> orderPays = new ArrayList<>();
        Float savePayAmount = payAmount;
        if (changeProduct.getAmount().compareTo(0f) > 0) {
            savePayAmount = new BigDecimal(Float.toString(payAmount)).subtract(new BigDecimal(Float.toString(changeProduct.getAmount()))).floatValue();
        }
        orderPays.addAll(getPaysByAmount(pays, savePayAmount));
        if (changeProduct.getAmount().compareTo(0f) > 0) {
            orderPays.addAll(getPaysByChangeProduct(changeProduct));
        }
        for (Pay pay : orderPays) {
            pay.setId(null);
        }
        order.setPays(orderPays);

        return order;
    }

    public ReturnProduct generateReturnProductByChangeProduct(ChangeProduct changeProduct) {
        ReturnProduct returnProduct = new ReturnProduct();

        returnProduct.setNo(afterSaleServiceDao.getNo(AfterSaleServiceConstant.no_returnProduct_perfix));
        returnProduct.setEntity(changeProduct.getEntity());
        returnProduct.setEntityId(changeProduct.getEntityId());
        returnProduct.setSessionId(changeProduct.getSessionId());
        returnProduct.setReason(AfterSaleServiceConstant.action_remark_changeProduct_returnProduct_reason);
        returnProduct.setFee(changeProduct.getFee());

        Set<ReturnProductDetail> details = new HashSet<>();
        for (ChangeProductDetail detail : changeProduct.getDetails()) {
            if (detail.getType().compareTo(AfterSaleServiceConstant.changeProduct_detail_type_returnProduct) == 0) {
                ChangeProductDetail dbDetail = ((List<ChangeProductDetail>) afterSaleServiceDao.query(detail)).get(0);

                ReturnProductDetail returnProductDetail = new ReturnProductDetail();
                returnProductDetail.setProductNo(dbDetail.getProductNo());
                returnProductDetail.setQuantity(dbDetail.getQuantity());
                returnProductDetail.setPrice(dbDetail.getPrice());
                returnProductDetail.setAmount(dbDetail.getAmount());

                details.add(returnProductDetail);
            }
        }
        returnProduct.setDetails(details);

        return returnProduct;
    }

    public List<Action> queryActionsByChangeProduct(ChangeProduct changeProduct) {
        Action action = new Action();
        action.setEntity(changeProduct.getClass().getSimpleName());
        action.setEntityId(changeProduct.getId());
        return afterSaleServiceDao.query(action);
    }

    public List<Pay> getPaysByAmount(List<Pay> pays, Float amount) {
        List<Pay> amountPays = new ArrayList<>();

        pays.sort( new Comparator<Pay>() {
            /**
             * 如果要按照升序排序，
             * 则o1 小于o2，返回-1（负数），相等返回0，o1大于o2返回1（正数）
             * 如果要按照降序排序
             * 则o1 小于o2，返回1（正数），相等返回0，o1大于o2返回-1（负数）
             * @param o1
             * @param o2
             * @return
             */
            @Override
            public int compare(Pay o1, Pay o2) {
                if (o1.getAmount().compareTo(o2.getAmount()) > 0) {
                    return -1;
                } else if (o1.getAmount().compareTo(o2.getAmount()) < 0) {
                    return 1;
                }

                return 0;
            }
        });

        int i = 0;
        for (; i < pays.size(); i++) {
            if (amount.compareTo(pays.get(i).getAmount()) > 0) {
                amountPays.add(pays.get(i));
                amount = new BigDecimal(Float.valueOf(amount)).subtract(new BigDecimal(Float.toString(pays.get(i).getAmount()))).floatValue();
            } else {
                break;
            }
        }

        if (i < pays.size()) {
            pays.get(i).setAmount(amount);
            amountPays.add(pays.get(i));
        } else {
            amountPays.get(i-1).setAmount(new BigDecimal(Float.valueOf(amount)).add(new BigDecimal(Float.toString(amountPays.get(i-1).getAmount()))).floatValue());
        }

        return amountPays;
    }

    public List<Pay> getPaysByChangeProduct(ChangeProduct changeProduct) {
        Pay pay = new Pay();
        pay.setEntity(changeProduct.getClass().getSimpleName());
        pay.setEntityId(changeProduct.getId());

        return writer.gson.fromJson(payClient.query(pay.getClass().getSimpleName(), writer.gson.toJson(pay)), new com.google.common.reflect.TypeToken<List<Pay>>(){}.getType());
    }

}
