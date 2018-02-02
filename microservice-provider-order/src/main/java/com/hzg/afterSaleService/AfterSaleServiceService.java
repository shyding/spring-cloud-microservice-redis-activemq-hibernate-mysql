package com.hzg.afterSaleService;

import com.google.gson.reflect.TypeToken;
import com.hzg.erp.Product;
import com.hzg.erp.StockInOut;
import com.hzg.erp.StockInOutDetail;
import com.hzg.erp.StockInOutDetailProduct;
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
    CustomerClient customerClient;

    /**
     * 保存退货单
     * @param returnProduct
     * @return
     */
    public String saveReturnProduct(ReturnProduct returnProduct) {
        String result = CommonConstant.fail;

        logger.info("saveReturnProduct start");

        ReturnProduct setReturnProduct = setReturnProduct(returnProduct);
        String isCanReturnMsg = isCanReturn(setReturnProduct);

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
        }

        result += setProductsReturnState(ErpConstant.product_action_name_setProductsOnReturn, returnProduct);

        logger.info("saveReturnProduct end, result:" + result);
        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());

    }

    List<ReturnProduct> queryReturnProduct(ReturnProduct returnProduct) {
        List<ReturnProduct> returnProducts = afterSaleServiceDao.query(returnProduct);

        for (ReturnProduct ele : returnProducts) {
            for (ReturnProductDetail detail : ele.getDetails()) {
                ReturnProductDetailProduct detailProduct = new ReturnProductDetailProduct();
                detailProduct.setReturnProductDetail(detail);
                detail.setProduct(((ReturnProductDetailProduct)afterSaleServiceDao.query(detailProduct).get(0)).getProduct());
            }

            Action action = new Action();
            action.setEntity(AfterSaleServiceConstant.returnProduct);
            action.setEntityId(ele.getId());
            ele.setActions(afterSaleServiceDao.query(action));
        }

        return returnProducts;
    }

    /**
     * 设置退货单
     * @param returnProduct
     * @return
     */
    public ReturnProduct setReturnProduct(ReturnProduct returnProduct) {
        if (returnProduct.getEntity().equals(OrderConstant.order)) {
            Order order = orderService.queryOrder(new Order(returnProduct.getEntityId())).get(0);

            returnProduct.setEntityNo(order.getNo());
            returnProduct.setUser(order.getUser());
            returnProduct.setInputDate(dateUtil.getSecondCurrentTimestamp());
            returnProduct.setState(AfterSaleServiceConstant.returnProduct_state_apply);

            returnProduct.setAmount(0f);
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

                                if (productState.compareTo(ErpConstant.product_state_stockOut) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_stockOut_part) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_sold) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_sold_part) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_shipped) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_shipped_part) == 0 ||
                                    productState.compareTo(ErpConstant.product_state_onReturnProduct_part) == 0 ||
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
                                        productState.compareTo(ErpConstant.product_state_returnedProduct_part) == 0) {
                                        detail.getReturnProductDetailProducts().add(new ReturnProductDetailProduct(orderDetailProduct.getProduct()));
                                    }

                                } else {
                                    break;
                                }
                            }
                        }

                        returnProduct.setAmount(new BigDecimal(Float.toString(returnProduct.getAmount())).
                                add(new BigDecimal(Float.toString(detail.getAmount()))).floatValue());

                        break;
                    }
                }
            }
        }

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
        }

        if (!canReturnMsg.equals("")) {
            canReturnMsg = "尊敬的顾客你好，你提交的退货申请单 " + returnProduct.getNo() + "申请退货失败。具体原因是：" +
                    canReturnMsg + "如有帮助需要，请联系我公司客服人员处理";
        }

        return canReturnMsg;
    }

    public String checkReturnProductQuantity(ReturnProduct returnProduct) {
        String canReturnMsg = "";

        if (returnProduct.getEntity().equals(OrderConstant.order)) {
            Order order = (Order) orderDao.queryById(returnProduct.getEntityId(), Order.class);

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

        return returnProduct;
    }

    public User getUserBySessionId(String sessionId){
        return (User)afterSaleServiceDao.getFromRedis((String)afterSaleServiceDao.getFromRedis(CommonConstant.sessionId + CommonConstant.underline + sessionId));
    }


    /**
     * 退货单退款
     * 1.设置退款单及商品为退货状态
     * 2.设置订单为退款状态
     * 3.调用退款接口退款，生成退款记录
     * 4.商品入库，商品状态调整为在售状态
     *
     * @param returnProduct
     */
    public String refundReturnProduct(ReturnProduct returnProduct) {
        String result = CommonConstant.fail;

        String isCanReturnMsg = checkReturnProductQuantity(returnProduct);
        if (!isCanReturnMsg.equals("")) {
            return CommonConstant.fail + isCanReturnMsg;
        }

        returnProduct.setState(AfterSaleServiceConstant.returnProduct_state_refund);
        result += afterSaleServiceDao.updateById(returnProduct.getId(), returnProduct);

        for (ReturnProductDetail returnProductDetail : returnProduct.getDetails()) {
            returnProductDetail.setState(AfterSaleServiceConstant.returnProduct_detail_state_returned);
            result += afterSaleServiceDao.updateById(returnProductDetail.getId(), returnProductDetail);

            ReturnProductDetail dbReturnProductDetail = (ReturnProductDetail) afterSaleServiceDao.queryById(returnProductDetail.getId(), returnProductDetail.getClass());
            returnProductDetail.setReturnProductDetailProducts(dbReturnProductDetail.getReturnProductDetailProducts());
        }

        result += setProductsReturnState(ErpConstant.product_action_name_setProductsReturned, returnProduct);

        if (returnProduct.getEntity().equals(OrderConstant.order)) {
            result += orderService.setOrderRefundState(new Order(returnProduct.getEntityId()));
        }

        /**
         * 调用退款接口退款
         */
        Pay pay = new Pay();
        pay.setEntity(returnProduct.getEntity());
        pay.setEntityId(returnProduct.getEntityId());
        pay.setState(PayConstants.pay_state_success);
        result += ((Map<String, String>)writer.gson.fromJson(
                payClient.refund(AfterSaleServiceConstant.returnProduct, returnProduct.getId(), returnProduct.getAmount(), writer.gson.toJson(pay)),
                new TypeToken<Map<String, String>>(){}.getType())).get(CommonConstant.result);

        /**
         * 商品入库，商品状态调整为在售状态
         */
        if (!result.substring(CommonConstant.fail.length()).contains(CommonConstant.fail)) {
            result += stockIn(returnProduct);
        }
        if (!result.substring(CommonConstant.fail.length()).contains(CommonConstant.fail)) {
            result += setProductEdit(returnProduct);
        }
        if (!result.substring(CommonConstant.fail.length()).contains(CommonConstant.fail)) {
            result += upShelf(returnProduct);
        }

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

        return ((Map<String, String>)writer.gson.fromJson(erpClient.business(productsReturnStateAction, writer.gson.toJson(products)),
                new TypeToken<Map<String, String>>(){}.getType())).get(CommonConstant.result);
    }

    /**
     * 获取商品已退数量
     *
     * 商品已退数量计算规则：
     * 由于销售是先销售上次已退货商品，因此销售商品可能只包含上次已退货商品，也可能同时包含上次已退货商品及未做过退货的商品，
     * 因此一次销售的销售数量 = 前次已退货数量 + 未做过退货商品数量（可能为0）。
     * 因此一次销售及退货的实际退货数量为: 1.如果 该次退货数量 >= 该次销售数量， 则 一次销售及退货的实际退货数量 = 该次退货数量 - 该次销售数量；
     *                                   2. 如果 该次退货数量 < 该次销售数量， 则 一次销售及退货的实际退货数量 = 0；
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
        List<ReturnProductDetail> returnedProductDetails = getReturnedProductDetails(product);
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


        Float quantity = 0f;

        for (int k = 0; k < details.size(); k++) {
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
     * 商品再退数量没有重复再退数量这种情况，因为一次退货的完整过程中，退货记录的状态最终都是退货完成状态，不会存在
     * 再退状态这种中间状态。所以同一商品多次退货，在最后一次退货未完成时，多次的退货记录中的最后一条是在退状态，其
     * 他是退货完成状态；多次退货完成后，退货记录里就没有再退状态。
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
            result += ((Map<String, String>)writer.gson.fromJson(
                    erpClient.business(ErpConstant.stockInOut_action_name_inProduct, writer.gson.toJson(action)),
                    new TypeToken<Map<String, String>>(){}.getType())).get(CommonConstant.result);
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

        return ((Map<String, String>)writer.gson.fromJson(
                erpClient.business(ErpConstant.product_action_name_setProductEdit, writer.gson.toJson(products)),
                new TypeToken<Map<String, String>>(){}.getType())).get(CommonConstant.result);
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

        return ((Map<String, String>)writer.gson.fromJson(
                erpClient.business(ErpConstant.product_action_name_upShelf, writer.gson.toJson(action)),
                new TypeToken<Map<String, String>>(){}.getType())).get(CommonConstant.result);
    }

    public List<ReturnProductDetail> getReturnedProductDetails(Product product){
        List<ReturnProductDetail> returnedDetail = new ArrayList<>();

        Iterator<ReturnProductDetail> iterator = getReturnProductDetails(product).iterator();
        while (iterator.hasNext()) {
            ReturnProductDetail detail = iterator.next();
            if (detail.getState().compareTo(AfterSaleServiceConstant.returnProduct_detail_state_returned) == 0) {
                returnedDetail.add(detail);
            }
        }

        return returnedDetail;
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
}
