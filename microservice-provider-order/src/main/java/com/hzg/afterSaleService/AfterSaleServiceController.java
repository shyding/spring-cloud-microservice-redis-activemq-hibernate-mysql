package com.hzg.afterSaleService;

import com.google.gson.reflect.TypeToken;
import com.hzg.erp.Product;
import com.hzg.order.*;
import com.hzg.sys.Action;
import com.hzg.tools.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/afterSaleService")
public class AfterSaleServiceController {

    Logger logger = Logger.getLogger(AfterSaleServiceController.class);

    @Autowired
    private AfterSaleServiceDao afterSaleServiceDao;

    @Autowired
    private AfterSaleServiceService afterSaleServiceService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private Writer writer;

    @Autowired
    private Transcation transcation;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    CustomerClient customerClient;

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

        try {
            if (entity.equalsIgnoreCase(ReturnProduct.class.getSimpleName())) {
                ReturnProduct returnProduct = writer.gson.fromJson(json, ReturnProduct.class);
                result += afterSaleServiceService.saveReturnProduct(returnProduct);

                result = "{\"" + CommonConstant.result + "\":\"" + transcation.dealResult(result) + "\"" +
                        ",\"" + CommonConstant.id + "\":" + returnProduct.getId() + "}";

                writer.writeStringToJson(response, result);
                logger.info("save end, result:" + result);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result += CommonConstant.fail;
        } finally {
            result = transcation.dealResult(result);
        }

        writer.writeStringToJson(response, result);
        logger.info("save end, result:" + result);
    }


    @Transactional
    @PostMapping("/business")
    public void business(HttpServletResponse response, String name, @RequestBody String json){
        logger.info("business start, parameter:" + name + ":" + json);

        String result = CommonConstant.fail;

        try {
            if (name.equals(AfterSaleServiceConstant.returnProduct_action_name_returnProduct)) {
                ReturnProduct returnProduct = new ReturnProduct();
                Map<String, Object> returnProductInfo = writer.gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());

                if (returnProductInfo.get(CommonConstant.entity).equals(OrderConstant.order)) {
                    String entityId = String.valueOf(returnProductInfo.get(CommonConstant.entityId));
                    Order order = orderService.queryOrder(new Order(Integer.parseInt(entityId.substring(0, entityId.indexOf("."))))).get(0);

                    returnProduct.setNo(afterSaleServiceDao.getNo(AfterSaleServiceConstant.no_returnProduct_perfix));
                    returnProduct.setEntity(OrderConstant.order);
                    returnProduct.setEntityId(order.getId());
                    returnProduct.setEntityNo(order.getNo());
                    returnProduct.setUser(order.getUser());
                    returnProduct.setAmount(order.getPayAmount());

                    Set<ReturnProductDetail> details = new HashSet<>();
                    for (OrderDetail orderDetail : order.getDetails()) {
                        ReturnProductDetail returnProductDetail = new ReturnProductDetail();

                        returnProductDetail.setProductNo(orderDetail.getProductNo());
                        returnProductDetail.setQuantity(orderDetail.getQuantity());
                        returnProductDetail.setUnit(orderDetail.getUnit());

                        if (orderDetail.getPriceChange() == null) {
                            returnProductDetail.setPrice(orderDetail.getProductPrice());
                        } else {
                            returnProductDetail.setPrice(orderDetail.getPriceChange().getPrice());
                        }
                        returnProductDetail.setAmount(orderDetail.getPayAmount());
                        returnProductDetail.setProduct(orderDetail.getProduct());

                        details.add(returnProductDetail);
                    }

                    returnProduct.setDetails(details);
                    result += CommonConstant.success;

                    writer.writeStringToJson(response, writer.gson.toJson(returnProduct));
                    logger.info("business end");
                    return;
                }

            } else if (name.equals(AfterSaleServiceConstant.returnProduct_action_name_saleAudit)) {
                result += afterSaleServiceService.doReturnProductBusinessAction(json, AfterSaleServiceConstant.returnProduct_state_salePass,
                        AfterSaleServiceConstant.returnProduct_action_salePass, AfterSaleServiceConstant.returnProduct_state_saleNotPass,
                        AfterSaleServiceConstant.returnProduct_state_saleNotPass);

            } else if (name.equals(AfterSaleServiceConstant.returnProduct_action_name_directorAudit)) {
                result += afterSaleServiceService.doReturnProductBusinessAction(json, AfterSaleServiceConstant.returnProduct_state_directorPass,
                        AfterSaleServiceConstant.returnProduct_action_directorPass, AfterSaleServiceConstant.returnProduct_state_directorNotPass,
                        AfterSaleServiceConstant.returnProduct_state_directorNotPass);

            } else if (name.equals(AfterSaleServiceConstant.returnProduct_action_name_warehousingAudit)) {
                result += afterSaleServiceService.doReturnProductBusinessAction(json, AfterSaleServiceConstant.returnProduct_state_warehousingPass,
                        AfterSaleServiceConstant.returnProduct_action_warehousingPass, AfterSaleServiceConstant.returnProduct_state_warehousingNotPass,
                        AfterSaleServiceConstant.returnProduct_state_warehousingNotPass);

            } else if (name.equals(AfterSaleServiceConstant.returnProduct_action_name_refund)) {
                Action action = writer.gson.fromJson(json, Action.class);
                ReturnProduct returnProduct = (ReturnProduct) afterSaleServiceDao.queryById(action.getEntityId(), ReturnProduct.class);
                /**
                 * 设置sessionId，后面入库用到
                 */
                returnProduct.setSessionId(action.getSessionId());

                result += afterSaleServiceService.refundReturnProduct(returnProduct);

                action.setEntity(AfterSaleServiceConstant.returnProduct);
                action.setType(AfterSaleServiceConstant.returnProduct_action_refund);
                action.setInputer(afterSaleServiceService.getUserBySessionId(action.getSessionId()));
                action.setInputDate(dateUtil.getSecondCurrentTimestamp());
                result += afterSaleServiceDao.save(action);
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

    @RequestMapping(value = "/getProductOnReturnQuantity", method = {RequestMethod.GET, RequestMethod.POST})
    public void getProductOnReturnQuantity(HttpServletResponse response, @RequestBody String json){
        logger.info("getProductOnReturnQuantity start, parameter:" + json);
        writer.writeStringToJson(response, "{\"" + ErpConstant.product_onReturn_quantity +"\":\"" + afterSaleServiceService.getProductOnReturnQuantity(writer.gson.fromJson(json, Product.class)) + "\"}");
        logger.info("getProductOnReturnQuantity start, end");
    }

    @RequestMapping(value = "/getProductReturnedQuantity", method = {RequestMethod.GET, RequestMethod.POST})
    public void getProductReturnedQuantity(HttpServletResponse response, @RequestBody String json){
        logger.info("getProductReturnedQuantity start, parameter:" + json);
        writer.writeStringToJson(response, "{\"" + ErpConstant.product_returned_quantity +"\":\"" + afterSaleServiceService.getProductReturnedQuantity(writer.gson.fromJson(json, Product.class)) + "\"}");
        logger.info("getProductReturnedQuantity start, end");
    }

    @RequestMapping(value = "/unlimitedQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void unlimitedQuery(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("unlimitedQuery start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(ReturnProduct.class.getSimpleName())) {
            List<ReturnProduct> returnProducts = afterSaleServiceService.queryReturnProduct(writer.gson.fromJson(json, ReturnProduct.class));
            writer.writeObjectToJson(response, returnProducts);
        }

        logger.info("unlimitedQuery end");
    }

    @RequestMapping(value = "/unlimitedSuggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void unlimitedSuggest(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("unlimitedSuggest start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(ReturnProduct.class.getSimpleName())) {
            writer.writeObjectToJson(response, afterSaleServiceDao.suggest(writer.gson.fromJson(json, ReturnProduct.class), null));
        }

        logger.info("unlimitedSuggest end");
    }

    @RequestMapping(value = "/unlimitedComplexQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void unlimitedComplexQuery(HttpServletResponse response, String entity, @RequestBody String json, int position, int rowNum){
        logger.info("unlimitedComplexQuery start, parameter:" + entity + ":" + json + "," + position + "," + rowNum);

        if (entity.equalsIgnoreCase(ReturnProduct.class.getSimpleName())) {
            writer.writeObjectToJson(response, afterSaleServiceDao.complexQuery(ReturnProduct.class,
                    writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType()), position, rowNum));
        }

        logger.info("unlimitedComplexQuery end");
    }

    /**
     * 查询条件限制下的记录数
     * @param response
     * @param entity
     * @param json
     */
    @RequestMapping(value = "/unlimitedRecordsSum", method = {RequestMethod.GET, RequestMethod.POST})
    public void unlimitedRecordsSum(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("unlimitedRecordsSum start, parameter:" + entity + ":" + json);
        BigInteger recordsSum = new BigInteger("-1");

        if (entity.equalsIgnoreCase(ReturnProduct.class.getSimpleName())) {
            recordsSum = afterSaleServiceDao.recordsSum(ReturnProduct.class, writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType()));
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.recordsSum + "\":" + recordsSum + "}");
        logger.info("unlimitedRecordsSum end");
    }

    @RequestMapping(value = "/getLastValidReturnProductByProduct", method = {RequestMethod.GET, RequestMethod.POST})
    public void getLastValidReturnProductByProduct(HttpServletResponse response, @RequestBody String json){
        logger.info("getLastValidReturnProductByProduct start, parameter:" + json);
        writer.writeObjectToJson(response, afterSaleServiceService.getLastValidReturnProductByProduct(writer.gson.fromJson(json, Product.class)));
        logger.info("getLastValidReturnProductByProduct end");
    }
}
