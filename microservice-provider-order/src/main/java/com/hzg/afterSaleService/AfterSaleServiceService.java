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
    private SysClient sysClient;

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

        setProductsReturnState(ErpConstant.product_action_name_setProductsOnReturn, returnProduct);

        logger.info("saveReturnProduct end, result:" + result);
        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());

    }

    public List<ReturnProduct> queryReturnProduct(ReturnProduct returnProduct) {
        List<ReturnProduct> returnProducts = afterSaleServiceDao.query(returnProduct);

        for (ReturnProduct item : returnProducts) {
            for (ReturnProductDetail detail : item.getDetails()) {
            }
        }

        return returnProducts;
    }

    /**
     * 检查是否可退货
     * @param returnProduct
     * @return
     */
    public String isCanReturn(ReturnProduct returnProduct) {
        String canSellMsg = "";

        if (returnProduct.getEntity().equals(OrderConstant.order)) {
            Order order = (Order) orderDao.queryById(returnProduct.getEntityId(), Order.class);
            if (order.getType().compareTo(OrderConstant.order_type_private) == 0) {
                canSellMsg += "订单：" + order.getNo() + "为私人订制单，不能退货";
            }
            if (order.getType().compareTo(OrderConstant.order_type_assist_process) == 0) {
                canSellMsg += "订单：" + order.getNo() + "为加工单，不能退货";
            }
        }

        if (canSellMsg.equals("")) {
            for (ReturnProductDetail detail : returnProduct.getDetails()) {
                if (detail.getReturnProductDetailProducts().size() == 0) {
                    canSellMsg += "商品：" + detail.getProductNo() + "退货数量为: 0;";
                }

                if (!detail.getUnit().equals(ErpConstant.unit_g) && !detail.getUnit().equals(ErpConstant.unit_kg) &&
                        !detail.getUnit().equals(ErpConstant.unit_oz) && !detail.getUnit().equals(ErpConstant.unit_ct) ) {
                    if (detail.getQuantity().intValue() != detail.getReturnProductDetailProducts().size()) {
                        canSellMsg += "商品：" + detail.getProductNo() + "退货数量为: " + detail.getQuantity() +
                                "，而实际可退数量为: " + detail.getReturnProductDetailProducts().size() + ";";
                    }
                }
            }
        }

        if (!canSellMsg.equals("")) {
            canSellMsg += "尊敬的顾客你好，你提交的退货申请单 " + returnProduct.getNo() + "申请退货失败。具体原因是：" +
                    canSellMsg + "。如有帮助需要，请联系我公司客服人员处理";
        }

        return canSellMsg;
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

            /**
             * 根据 sessionId 获取到用户，如果用户是后台销售人员，则设置为销售确认可退状态，否则设置为申请状态
             */
            Object user = afterSaleServiceDao.getFromRedis((String)afterSaleServiceDao.
                    getFromRedis(CommonConstant.sessionId + CommonConstant.underline + returnProduct.getSessionId()));
            if (user instanceof com.hzg.sys.User) {
                returnProduct.setState(AfterSaleServiceConstant.returnProduct_state_salePass);
            } else {
                returnProduct.setState(AfterSaleServiceConstant.returnProduct_state_apply);
            }

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
                            if (detail.getReturnProductDetailProducts().size() <= detail.getQuantity()) {

                                if (orderDetailProduct.getProduct().getState().compareTo(ErpConstant.product_state_sold) == 0 ||
                                        orderDetailProduct.getProduct().getState().compareTo(ErpConstant.product_state_shipped) == 0) {
                                    detail.getReturnProductDetailProducts().add(new ReturnProductDetailProduct(orderDetailProduct.getProduct()));
                                }

                            } else {
                                break;
                            }
                        }

                        break;
                    }
                }
            }
        }

        return returnProduct;
    }

    public String doReturnProductBusinessAction(String json, Integer returnProductPassState, Integer actionPassState,
                                                Integer returnProductNotPassState, Integer actionNotPassState) {
        String result = CommonConstant.fail;

        Action action = writer.gson.fromJson(json, Action.class);
        ReturnProduct returnProduct = (ReturnProduct) afterSaleServiceDao.queryById(action.getEntityId(), ReturnProduct.class);

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
        }

        result += afterSaleServiceDao.updateById(returnProduct.getId(), returnProduct);

        action.setEntity(AfterSaleServiceConstant.returnProduct);
        action.setInputer(getUserBySessionId(action.getSessionId()));
        action.setInputDate(dateUtil.getSecondCurrentTimestamp());
        result += afterSaleServiceDao.save(action);

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
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

        returnProduct.setState(AfterSaleServiceConstant.returnProduct_state_refund);
        result += afterSaleServiceDao.updateById(returnProduct.getId(), returnProduct);

        for (ReturnProductDetail returnProductDetail : returnProduct.getDetails()) {
            returnProductDetail.setState(AfterSaleServiceConstant.returnProduct_detail_state_returned);
            result += afterSaleServiceDao.updateById(returnProductDetail.getId(), returnProductDetail);

            ReturnProductDetail dbReturnProductDetail = (ReturnProductDetail) afterSaleServiceDao.queryById(returnProductDetail.getId(), returnProductDetail.getClass());
            returnProductDetail.setReturnProductDetailProducts(dbReturnProductDetail.getReturnProductDetailProducts());
        }

        setProductsReturnState(ErpConstant.product_action_name_setProductsReturned, returnProduct);

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
        result += payClient.refund(AfterSaleServiceConstant.returnProduct, returnProduct.getId(), returnProduct.getAmount(), writer.gson.toJson(pay));

        /**
         * 商品入库，商品状态调整为在售状态
         */
        if (!result.substring(CommonConstant.fail.length()).contains(CommonConstant.fail)) {
            result += stockIn(returnProduct);
            result += upShelf(returnProduct);
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
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
                /**
                 * size > 1 表示商品是按件数退货，<= 1 表示商品是按件数或者重量或者其他不可数单位退货
                 */
                detailProduct.getProduct().setReturnQuantity(detail.getReturnProductDetailProducts().size() > 1 ? 1 : detail.getQuantity());
                detailProduct.getProduct().setSoldUnit(detail.getUnit());
                products.add(detailProduct.getProduct());
            }
        }

        return erpClient.business(productsReturnStateAction, writer.gson.toJson(products));
    }

    public Float getProductOnReturnQuantity(Product product) {
        List<ReturnProductDetail> details = getReturnProductDetail(product);

        Float quantity = 0f;
        for (ReturnProductDetail detail : details) {
            if (detail.getState().compareTo(AfterSaleServiceConstant.returnProduct_detail_state_unReturn) == 0) {
                quantity = new BigDecimal(Float.toString(quantity)).add(new BigDecimal(Float.toString(detail.getQuantity()))).floatValue();
            }
        }

        return quantity;
    }

    public Float getProductReturnedQuantity(Product product) {
        List<ReturnProductDetail> details = getReturnProductDetail(product);

        Float quantity = 0f;
        for (ReturnProductDetail detail : details) {
            if (detail.getState().compareTo(AfterSaleServiceConstant.returnProduct_detail_state_returned) == 0) {
                quantity = new BigDecimal(Float.toString(quantity)).add(new BigDecimal(Float.toString(detail.getQuantity()))).floatValue();
            }
        }

        return quantity;
    }

    private String stockIn(ReturnProduct returnProduct) {
        String result = CommonConstant.fail;

        Map<String, Object> saveResult = writer.gson.fromJson(saveStockIn(returnProduct), new TypeToken<Map<String, Object>>(){}.getType());
        result += (String) saveResult.get(CommonConstant.result);

        if (saveResult.get(CommonConstant.result).equals(CommonConstant.success)) {
            Action action = new Action();
            action.setEntityId((Integer) saveResult.get(CommonConstant.id));
            action.setSessionId(returnProduct.getSessionId());
            result += erpClient.business(ErpConstant.stockInOut_action_name_inProduct, writer.gson.toJson(action));
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    /**
     * 插入入库数据
     */
    private String saveStockIn(ReturnProduct returnProduct) {
        StockInOut stockIn = new StockInOut();
        stockIn.setNo(((Map<String, String>)writer.gson.fromJson(erpClient.getNo(ErpConstant.no_stockOut_perfix), new TypeToken<Map<String, String>>() {}.getType())).get(CommonConstant.no));
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

        return erpClient.business(ErpConstant.product_action_name_upShelf, writer.gson.toJson(action));
    }

    public List<ReturnProductDetail> getReturnProductDetail(Product product) {
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
                details.add(detailProduct.getReturnProductDetail());
            }
        }

        return details;
    }
}
