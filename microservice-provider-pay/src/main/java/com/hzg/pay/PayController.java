package com.hzg.pay;

import com.boyuanitsm.pay.alipay.bean.AyncNotify;
import com.boyuanitsm.pay.alipay.bean.RefundAyncNotify;
import com.boyuanitsm.pay.alipay.bean.SyncReturn;
import com.boyuanitsm.pay.alipay.util.AlipayNotify;
import com.boyuanitsm.pay.alipay.util.AlipaySubmit;
import com.boyuanitsm.pay.unionpay.Acp;
import com.boyuanitsm.pay.unionpay.b2c.FrontConsume;
import com.boyuanitsm.pay.unionpay.b2c.PayNotify;
import com.boyuanitsm.pay.unionpay.common.AcpService;
import com.boyuanitsm.pay.wxpay.bean.Result;
import com.boyuanitsm.pay.wxpay.bean.SimpleOrder;
import com.boyuanitsm.pay.wxpay.business.UnifiedOrderBusiness;
import com.boyuanitsm.pay.wxpay.common.Signature;
import com.boyuanitsm.pay.wxpay.common.XMLParser;
import com.boyuanitsm.pay.wxpay.protocol.RefundResultCallback;
import com.boyuanitsm.pay.wxpay.protocol.ResultCallback;
import com.boyuanitsm.pay.wxpay.protocol.refund_protocol.RefundReqData;
import com.boyuanitsm.pay.wxpay.protocol.refund_protocol.RefundResData;
import com.boyuanitsm.pay.wxpay.protocol.unified_order_protocol.UnifiedOrderReqData;
import com.boyuanitsm.pay.wxpay.protocol.unified_order_protocol.UnifiedOrderResData;
import com.boyuanitsm.pay.wxpay.service.RefundService;
import com.google.gson.reflect.TypeToken;
import com.hzg.tools.*;
import net.glxn.qrgen.javase.QRCode;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

@Controller
@RequestMapping("/pay")
public class PayController {

    Logger logger = Logger.getLogger(PayController.class);

    @Autowired
    private PayDao payDao;

    @Autowired
    private Writer writer;

    @Autowired
    private PayService payService;

    @Autowired
    private Transcation transcation;

    @Autowired
    private DateUtil dateUtil;

    public PayController() throws Exception {}

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
            if (entity.equalsIgnoreCase(Pay.class.getSimpleName())) {
                Pay pay = writer.gson.fromJson(json, Pay.class);
                pay.setNo(payDao.getNo());
                pay.setInputDate(inputDate);
                result += payDao.save(pay);

            } else if (entity.equalsIgnoreCase(Account.class.getSimpleName())) {
                Account account = writer.gson.fromJson(json, Account.class);
                result += payDao.save(account);

            } else if (entity.equalsIgnoreCase(Refund.class.getSimpleName())) {
                Refund refund = writer.gson.fromJson(json, Refund.class);
                result += payDao.save(refund);
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

    @Transactional()
    @PostMapping("/update")
    public void update(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("update start, parameter:" + entity + ":" + json);

        String result = CommonConstant.fail;

        try {
            if (entity.equalsIgnoreCase(Pay.class.getSimpleName())) {
                Pay pay = writer.gson.fromJson(json, Pay.class);
                result += payDao.updateById(pay.getId(), pay);

            } else if (entity.equalsIgnoreCase(Account.class.getSimpleName())) {
                Account account = writer.gson.fromJson(json, Account.class);
                result += payDao.updateById(account.getId(), account);

            } else if (entity.equalsIgnoreCase(Refund.class.getSimpleName())) {
                Refund refund = writer.gson.fromJson(json, Refund.class);
                result += payDao.updateById(refund.getId(), refund);
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

    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public void query(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("query start, parameter:" + entity + ":" + json);

        if (entity.equalsIgnoreCase(Pay.class.getSimpleName())) {
            writer.writeObjectToJson(response, payDao.query(writer.gson.fromJson(json, Pay.class)));

        } else if (entity.equalsIgnoreCase(Account.class.getSimpleName())) {
            writer.writeObjectToJson(response, payDao.query(writer.gson.fromJson(json, Account.class)));
        }

        logger.info("query end");
    }

    @RequestMapping(value = "/suggest", method = {RequestMethod.GET, RequestMethod.POST})
    public void suggest(HttpServletResponse response, String entity, @RequestBody String json){
        logger.info("suggest start, parameter:" + entity + ":" + json);


        logger.info("suggest end");
    }

    @RequestMapping(value = "/complexQuery", method = {RequestMethod.GET, RequestMethod.POST})
    public void complexQuery(HttpServletResponse response, String entity, @RequestBody String json, int position, int rowNum){
        logger.info("complexQuery start, parameter:" + entity + ":" + json + "," + position + "," + rowNum);

        Map<String, String> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());

        if (entity.equalsIgnoreCase(Pay.class.getSimpleName())) {
            writer.writeObjectToJson(response, payDao.complexQuery(Pay.class, queryParameters, position, rowNum));

        } else if (entity.equalsIgnoreCase(Account.class.getSimpleName())) {
            writer.writeObjectToJson(response, payDao.complexQuery(Account.class, queryParameters, position, rowNum));

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

        Map<String, String> queryParameters = writer.gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());

        if (entity.equalsIgnoreCase(Pay.class.getSimpleName())) {
            recordsSum =  payDao.recordsSum(Pay.class, queryParameters);
        }

        writer.writeStringToJson(response, "{\"" + CommonConstant.recordsSum + "\":" + recordsSum + "}");

        logger.info("recordsSum end");
    }




    /**
     * boyuanitsm 支付项目接口
     * 项目地址https://github.com/boyuanitsm/pay
     *
     *
     *  ========================= alipay 支付接口 =======================
     */

    @Autowired
    private AlipaySubmit alipaySubmit;

    @Autowired
    private AlipayNotify alipayNotify;


    /**
     * 支付宝即时到账交易接口快速通道
     *
     * @param no 支付号
     * @param payType
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/alipay/pay", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
    public void alipay(String no, String payType, HttpServletResponse response) {
        logger.info("alipay, no:" + no + ",payType:" + payType);

        String payHtml = null;

        Pay pay = new Pay();
        pay.setNo(no);
        Pay dbPay = (Pay)payDao.query(pay).get(0);

        if (dbPay.getState().compareTo(PayConstants.state_pay_apply) == 0) {
            payHtml = alipaySubmit.buildRequest(dbPay.getNo(), dbPay.getEntity() + CommonConstant.underline + dbPay.getEntityId(), dbPay.getAmount().toString(), payType);
        } else {
            payHtml = "支付记录：" + no + "不是未支付状态，不能支付";
        }

        writer.write(response, payHtml);
    }

    /**
     * 支付宝即时到账批量退款有密接口快速通道
     *
     * @param no 退款编号
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/alipay/refund", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
    public void alipayRefund(String no, HttpServletResponse response) {
        logger.info("alipayRefund, no:" + no);

        String refundHtml = "";

        Refund refund = new Refund();
        refund.setNo(no);
        Refund dbRefund = (Refund)payDao.query(refund).get(0);

        if (dbRefund.getState().compareTo(PayConstants.state_refund_apply) == 0) {
            refundHtml = alipaySubmit.buildRequest(dbRefund.getNo(), "1",
                    dbRefund.getBankBillNo() + PayConstants.alipay_refund_detail_splitor + refund.getAmount() +
                            PayConstants.alipay_refund_detail_splitor + refund.getEntity() + CommonConstant.underline + refund.getEntityId());
        } else {
            refundHtml = "退款记录：" + no + "不是未退款状态，不能退款";
        }

        writer.write(response, refundHtml);
    }

    /**
     * 支付宝支付服务器异步通知
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/alipay/ayncNotify", method = RequestMethod.POST)
    public void alipayAyncNotify(@RequestBody String json, HttpServletResponse response) {
        logger.info("alipayAyncNotify, json" + json);
        String result = CommonConstant.fail;

        // 验证签名
        if (alipayNotify.verifyRequest(writer.gson.fromJson(json, new TypeToken<Map<String, String[]>>(){}.getType()))) {
            logger.info("verify success!");

            AyncNotify ayncNotify = writer.gson.fromJson(json, new TypeToken<AyncNotify>(){}.getType());

            String no = ayncNotify.getOut_trade_no();
            String processKey = PayConstants.process_notify + no;
            if (payDao.getFromRedis(processKey) == null) {
                payDao.storeToRedis(processKey, no, PayConstants.process_time);

                if(ayncNotify.getTrade_status().equals(PayConstants.alipay_trade_finished) ||
                        ayncNotify.getTrade_status().equals(PayConstants.alipay_trade_success)){
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                    //如果有做过处理，不执行商户的业务程序
                    //注意：退款日期超过可退款期限后（如三个月可退款），支付宝系统发送 TRADE_FINISHED 状态通知.
                    //付款完成后，支付宝系统发送 TRADE_SUCCESS 状态通知

                    result += payService.processAlipayNotify(ayncNotify);
                }

                payDao.deleteFromRedis(processKey);
            }

        } else {
            logger.info("verify fail!");
        }


        result = result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
        if (!result.contains(CommonConstant.fail)) {
            result = CommonConstant.success;
        } else {
            result = CommonConstant.fail;
        }

        writer.write(response, result);
    }

    /**
     * 支付宝支付页面跳转同步通知页面
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/alipay/syncReturn", method = RequestMethod.GET)
    public void alipaySyncReturn(@RequestBody String json, HttpServletResponse response) {
        logger.info("alipaySyncReturn, json:" + json);
        String result = CommonConstant.fail;

        SyncReturn syncReturn = writer.gson.fromJson(json, new TypeToken<SyncReturn>(){}.getType());

        // 验证签名
        if (alipayNotify.verifyRequest(writer.gson.fromJson(json, new TypeToken<Map<String, String[]>>(){}.getType()))) {
            logger.info("verify success");

            String no = syncReturn.getOut_trade_no();
            String processKey = PayConstants.process_notify + no;
            if (payDao.getFromRedis(processKey) == null) {
                payDao.storeToRedis(processKey, no, PayConstants.process_time);

                if(syncReturn.getTrade_status().equals(PayConstants.alipay_trade_finished) ||
                        syncReturn.getTrade_status().equals(PayConstants.alipay_trade_success)){
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序

                    result += payService.processAlipayReturn(syncReturn);
                }

                payDao.deleteFromRedis(processKey);
            }

        } else {
            logger.info("verify fail!");
        }


        result = result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
        if (!result.contains(CommonConstant.fail)) {
            result = CommonConstant.success;
        } else {
            result = CommonConstant.fail;
        }

        Pay pay = new Pay();
        pay.setNo(syncReturn.getOut_trade_no());
        Pay dbPay = (Pay)payDao.query(pay).get(0);

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + result +
                "\", \"" + CommonConstant.id + "\":" + dbPay.getEntityId() + ",\"" + CommonConstant.entity +"\":\"" + dbPay.getEntity() + "\"}");
    }

    /**
     * 支付宝服务器退款异步通知页面
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/alipay/refundAyncNotify", method = RequestMethod.POST)
    public void alipayRefundAyncNotify(@RequestBody String json, HttpServletResponse response) {
        logger.info("alipayRefundAyncNotify, json:" + json);
        String result = CommonConstant.fail;

        // 验证签名
        if (alipayNotify.verifyRequest(writer.gson.fromJson(json, new TypeToken<Map<String, String[]>>(){}.getType()))) {
            logger.info("verify success!");

            RefundAyncNotify refundAyncNotify = writer.gson.fromJson(json, new TypeToken<RefundAyncNotify>(){}.getType());

            String no = refundAyncNotify.getBatch_no();
            String processKey = PayConstants.process_notify + no;
            if (payDao.getFromRedis(processKey) == null) {
                payDao.storeToRedis(processKey, no, PayConstants.process_time);

                result += payService.processAlipayRefundNotify(refundAyncNotify);

                payDao.deleteFromRedis(processKey);
            }

        } else {
            logger.info("verify fail!");
        }


        result = result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
        if (!result.contains(CommonConstant.fail)) {
            result = CommonConstant.success;
        } else {
            result = CommonConstant.fail;
        }

        writer.write(response, result);
    }

    /**
     * 获取一码付二维码图像
     * 二维码支付
     * @param no 支付号
     * @return 二维码图像
     */
    @RequestMapping(value = "/alipay/payQrcode", produces = "image/jpeg;charset=UTF-8", method = RequestMethod.GET)
    public void alipayPayQrcode(String no, HttpServletRequest request, HttpServletResponse response) {
        logger.info("alipayPayQrcode, no:" + no);

        //获取对应的支付账户操作工具（可根据账户id）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //这里为需要生成二维码的地址
        StringBuffer url = request.getRequestURL();
        url = new StringBuffer(url.substring(0, url.lastIndexOf(request.getRequestURI())));
        url .append("/alipay/toAlipay?");
        url.append("no=").append(no);
        url.append("&payType=2");

        try {
            ImageIO.write(MatrixToImageWriter.writeInfoToJpgBuff(url.toString()), "JPEG", baos);
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        writer.writeBytes(response, baos.toByteArray());
    }

    /**
     *
     * 支付宝转跳
     * @param no 支付号
     * @return 支付宝与微信平台的判断
     */
    @RequestMapping(value = "/alipay/toAlipay", produces = "text/html;charset=UTF-8", method = RequestMethod.GET)
    public void toAlipay(String no, String payType, HttpServletResponse response) {
        logger.info("toAlipay, no:" + no + ", payType:" + payType);

        StringBuilder payHtml = new StringBuilder();

        payHtml.append("<html><head></head><form name='alipayForm' action='/pay/alipay/pay' method='post'>" +
                "<input type='text' name='no' value='" + no + "'>" +
                "<input type='text' name='payType' value='" + payType +"'>" +
                "</form><body><script type=\"text/javascript\">\n");

        payHtml.append("if(isAliPay()){\n");
        payHtml.append("document.forms['alipayForm'].submit();");
        payHtml.append("\n } else {");
        payHtml.append("{\n alert('请使用支付宝App扫码'+window.navigator.userAgent.toLowerCase());}\n }");

        //判断是否为支付宝
        payHtml.append("function isAliPay(){\n" +
                " var ua = window.navigator.userAgent.toLowerCase();\n" +
                " if(ua.match(/AlipayClient/i) =='alipayclient'){\n" +
                "  return true;\n" +
                " }\n" +
                "  return false;\n" +
                "}</script><body></html>");

        writer.write(response, payHtml.toString());
    }

/*
    *//**
     * 移动支付 签名机制
     *
     * @param outTradeNO 商户网站唯一订单号
     * @param subject 商品名称
     * @param totalFee total_fee
     * @return orderStr 主要包含商户的订单信息，key=“value”形式，以&连接。
     * @throws UnsupportedEncodingException
     *//*
    @RequestMapping(value = "/alipay/mobilePaymentSign", method = RequestMethod.GET)
    public String mobilePaymentSign(String outTradeNO, String subject, String totalFee) throws UnsupportedEncodingException {
        return AlipayMobilePaymentSign.pay(outTradeNO, subject, totalFee);
    }*/




    /**
     *
     * ========================= 微信支付接口 =======================
     */

    @Autowired
    private UnifiedOrderBusiness unifiedOrderBusiness;

    @Autowired
    private RefundService refundService;

    /*private OrderQueryService orderQueryService = new OrderQueryService();
    private RefundQueryService refundQueryService = new RefundQueryService();
    private DownloadBillService downloadBillService = new DownloadBillService();*/

    /**
     * 统一下单
     * 除被扫支付场景以外，商户系统先调用该接口在微信支付服务后台生成预支付交易单，返回正确的预支付交易回话标识后再按扫码、JSAPI、APP等不同场景生成交易串调起支付。
     *
     * @param no 支付号
     * @see <a href="https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_1">https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_1</a>
     */
    @RequestMapping(value = "/wechat/unifiedOrder", produces = "image/jpeg;charset=UTF-8", method = RequestMethod.GET)
    public void unifiedOrder(HttpServletResponse response, String no)  {
        logger.info("unifiedOrder, no:" + no);

        // 调用统一下单API
        try {
            Pay pay = new Pay();
            pay.setNo(no);
            Pay dbPay = (Pay)payDao.query(pay).get(0);

            if (dbPay.getState().compareTo(PayConstants.state_pay_apply) == 0) {
                UnifiedOrderResData resData = unifiedOrderBusiness.run(new UnifiedOrderReqData
                        (new SimpleOrder("hzg wechat qrcode pay", dbPay.getNo(), (int)(dbPay.getAmount()*100f), dbPay.getEntity() + CommonConstant.underline + dbPay.getEntityId(), "2")));
                logger.info("订单信息: {}" + resData.toString());
                // 获得二维码URL
                String qrcodeUrl = resData.getCode_url();
                // 生成二维码字节数组输出流
                ByteArrayOutputStream stream = QRCode.from(qrcodeUrl).stream();
                // 输出
                writer.writeBytes(response, stream.toByteArray());
            } else {
                logger.info("支付记录：" + no + "不是未支付状态，不能支付");
                response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 支付结果通用通知
     * 支付完成后，微信会把相关支付结果和用户信息发送给商户，商户需要接收处理，并返回应答。
     * 对后台通知交互时，如果微信收到商户的应答不是成功或超时，微信认为通知失败，微信会通过一定的策略定期重新发起通知，尽可能提高通知的成功率，但微信不保证通知最终能成功。 （通知频率为15/15/30/180/1800/1800/1800/1800/3600，单位：秒）
     * 注意：同样的通知可能会多次发送给商户系统。商户系统必须能够正确处理重复的通知。
     * 推荐的做法是，当收到通知进行处理时，首先检查对应业务数据的状态，判断该通知是否已经处理过，如果没有处理过再进行处理，如果处理过直接返回结果成功。在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱。
     * 特别提醒：商户系统对于支付结果通知的内容一定要做签名验证，防止数据泄漏导致出现“假通知”，造成资金损失。
     * 技术人员可登进微信商户后台扫描加入接口报警群。
     *
     * @param responseString
     * @return success or fail
     * @see <a href="https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_7">https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_7</a>
     */
    @RequestMapping(value = "/wechat/payResultCallback", method = RequestMethod.POST)
    public void wechatPayResultCallback(@RequestBody String responseString, HttpServletResponse response)  {
        logger.info("wechatPayResultCallback, responseString:" + responseString);
        String result = CommonConstant.fail;

        try {
            boolean isSign = Signature.checkIsSignValidFromResponseString(responseString);
            if (isSign) {
                logger.info("verify success!");
                ResultCallback reqData = (ResultCallback) XMLParser.getObjectFromXML(responseString, ResultCallback.class);

                String no = reqData.getOut_trade_no();
                String processKey = PayConstants.process_notify + no;
                if (payDao.getFromRedis(processKey) == null) {
                    payDao.storeToRedis(processKey, no, PayConstants.process_time);

                    result += payService.processWechatCallback(no, reqData);

                    payDao.deleteFromRedis(processKey);
                }
            } else {
                logger.info("verify fail: {}" + responseString);
                result += "sign fail";
            }

        } catch (Exception e) {
            logger.info("pay_result_callback error!", e);
            result += "code exception fail";
        }

        result = result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
        if (!result.contains(CommonConstant.fail)) {
            result = XMLParser.getXMLFromObject(new Result(CommonConstant.success.toUpperCase(), CommonConstant.OK));
        } else {
            result = XMLParser.getXMLFromObject(new Result(CommonConstant.fail.toUpperCase(), result));
        }

        writer.write(response, result);
    }


    /**
     * 申请退款
     * 当交易发生之后一段时间内，由于买家或者卖家的原因需要退款时，卖家可以通过退款接口将支付款退还给买家，微信支付将在收到退款请求并且验证成功之后，按照退款规则将支付款按原路退到买家帐号上。
     * 注意：
     * 1、交易时间超过一年的订单无法提交退款；
     * 2、微信支付退款支持单笔交易分多次退款，多次退款需要提交原支付订单的商户订单号和设置不同的退款单号。一笔退款失败后重新提交，要采用原来的退款单号。总退款金额不能超过用户实际支付金额。
     *
     * @param no 退款编号
     * @param response
     * @return
     */
    @RequestMapping(value = "/wechat/refund", method = RequestMethod.POST)
    public void wechatRefund(String no, HttpServletResponse response) {
        logger.info("wechatRefund, no:" + no);

        String result = null;

        Refund refund = new Refund();
        refund.setNo(no);
        Refund dbRefund = (Refund)payDao.query(refund).get(0);

        if (dbRefund.getState().compareTo(PayConstants.state_refund_apply) == 0) {
            RefundResData refundResData = null;
            try {
                refundResData = refundService.refund(new RefundReqData(dbRefund.getPay().getBankBillNo(),
                        dbRefund.getPay().getNo(), dbRefund.getNo(), (int)(dbRefund.getAmount()*100F), (int)(dbRefund.getAmount()*100F)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (refundResData != null && refundResData.getReturn_code().equalsIgnoreCase(CommonConstant.success) &&
                    refundResData.getResult_code().equalsIgnoreCase(CommonConstant.success)) {
                result = "{\"" + CommonConstant.result + "\":\"退款记录：" + no + "退款申请完成\"}";
            } else {
                result = "{\"" + CommonConstant.result + "\":\"退款记录：" + no + "申请退款失败,请联系工作人员处理\"}";
            }

        } else {
            result = "{\"" + CommonConstant.result + "\":\"退款记录：" + no + "不是未退款状态，不能退款\"}";
        }

        writer.writeStringToJson(response, result);
    }

    @RequestMapping(value = "/wechat/refundResultCallback", method = RequestMethod.POST)
    public void wechatRefundResultCallback(@RequestBody String responseString, HttpServletResponse response)  {
        logger.info("wechatRefundResultCallback, responseString:" + responseString);
        String result = CommonConstant.fail;

        try {
            /**
             * 解密退款数据
             */
            RefundResultCallback reqData = payService.decryptWechatRefundResult(responseString);

            String no = reqData.getOut_trade_no();
            String processKey = PayConstants.process_notify + no;
            if (payDao.getFromRedis(processKey) == null) {
                payDao.storeToRedis(processKey, no, PayConstants.process_time);

                result += payService.processWechatRefundCallback(no, reqData);

                payDao.deleteFromRedis(processKey);
            }
        } catch (Exception e) {
            logger.info("pay_result_callback error!", e);
            result += "code exception fail";
        }

        result = result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
        if (!result.contains(CommonConstant.fail)) {
            result = XMLParser.getXMLFromObject(new Result(CommonConstant.success.toUpperCase(), CommonConstant.OK));
        } else {
            result = XMLParser.getXMLFromObject(new Result(CommonConstant.fail.toUpperCase(), result));
        }

        writer.write(response, result);
    }


    /**
     * 查询订单
     * 该接口提供所有微信支付订单的查询，商户可以通过该接口主动查询订单状态，完成下一步的业务逻辑。
     * 需要调用查询接口的情况：
     * ◆ 当商户后台、网络、服务器等出现异常，商户系统最终未接收到支付通知；
     * ◆ 调用支付接口后，返回系统错误或未知交易状态情况；
     * ◆ 调用被扫支付API，返回USERPAYING的状态；
     * ◆ 调用关单或撤销接口API之前，需确认支付状态；
     *
     * @param transactionID 是微信系统为每一笔支付交易分配的订单号，通过这个订单号可以标识这笔交易，它由支付订单API支付成功时返回的数据里面获取到。建议优先使用
     * @param outTradeNo    商户系统内部的订单号,transaction_id 、out_trade_no 二选一，如果同时存在优先级：transaction_id>out_trade_no
     * @return 订单详情
     *//*

    @RequestMapping(value = "/wechat/orderQuery", method = RequestMethod.GET)
    public void orderQuery(String transactionID, String outTradeNo, HttpServletResponse response)  {
        try {
            writer.writeObjectToJson(response, orderQueryService.query(new OrderQueryReqData(transactionID, outTradeNo)));
        } catch (Exception e) {
            logger.error("order_query error!", e);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            writer.writeStringToJson(response, "{}");
        }
    }*/


    /**
     * 查询退款
     * 提交退款申请后，通过调用该接口查询退款状态。退款有一定延时，用零钱支付的退款20分钟内到账，银行卡支付的退款3个工作日后重新查询退款状态。
     *
     * @param transactionID 是微信系统为每一笔支付交易分配的订单号，通过这个订单号可以标识这笔交易，它由支付订单API支付成功时返回的数据里面获取到。建议优先使用
     * @param outTradeNo    商户系统内部的订单号,transaction_id 、out_trade_no 二选一，如果同时存在优先级：transaction_id>out_trade_no
     * @param outRefundNo   商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
     * @param refundID      来自退款API的成功返回，微信退款单号refund_id、out_refund_no、out_trade_no 、transaction_id 四个参数必填一个，如果同事存在优先级为：refund_id>out_refund_no>transaction_id>out_trade_no
     * @param response
     * @return
     *//*
    @RequestMapping(value = "/wechat/refundQuery", method = RequestMethod.GET)
    public void refundQuery(String transactionID, String outTradeNo, String outRefundNo,
                            String refundID, HttpServletResponse response) {
        try {
            writer.writeObjectToJson(response, refundQueryService.refundQuery(new RefundQueryReqData(transactionID, outTradeNo, outRefundNo, refundID)));
        } catch (Exception e) {
            logger.error("refund query error!", e);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            writer.writeStringToJson(response, "{}");
        }
    }*/


    /**
     * 获得App 调起支付需要的请求参数
     * APP端调起支付的参数列表
     *
     * @param productId 产品ID
     * @return 调起支付需要的请求参数
     * @see <a href="https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_12&index=2">https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_12&index=2</a>
     *//*
    @RequestMapping(value = "/wechat/appPayParams", method = RequestMethod.GET)
    public AppPayParams appPayParams(String productId, HttpServletResponse response) {
        try {
            UnifiedOrderResData resData = unifiedOrderBusiness.run(new UnifiedOrderReqData("WxPay Text", 1, "wxtest" + System.currentTimeMillis()));
            logger.debug("订单信息: {}" + resData.toString());
            // 获得预支付交易会话ID
            String prepay_id = resData.getPrepay_id();
            return new AppPayParams(prepay_id);
        } catch (Exception e) {
            logger.error("app_pay_params error!", e);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    *//**
     * 获得H5 调起支付需要的请求参数
     * H5端调起支付的参数列表
     *
     * @param openId openid
     * @return 调起支付需要的请求参数
     * @see <a href="https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=7_7&index=6">https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=7_7&index=6</a>
     *//*
    @RequestMapping(value = "/wechat/h5PayParams", method = RequestMethod.GET)
    public H5PayParams h5PayParams(String openId, HttpServletResponse response) {
        try {
            UnifiedOrderResData resData = unifiedOrderBusiness.run(new UnifiedOrderReqData("WxPay Text", "wxtest" + System.currentTimeMillis(), openId, 1));
            logger.debug("订单信息: {}" + resData.toString());
            // 获得预支付交易会话ID
            String prepay_id = resData.getPrepay_id();
            return new H5PayParams(prepay_id);
        } catch (Exception e) {
            logger.error("h5_pay_params error!", e);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }


    *//**
     * 下载对账单
     * 商户可以通过该接口下载历史交易清单。比如掉单、系统错误等导致商户侧和微信侧数据不一致，通过对账单核对后可校正支付状态。
     * 注意：
     * 1、微信侧未成功下单的交易不会出现在对账单中。支付成功后撤销的交易会出现在对账单中，跟原支付单订单号一致，bill_type为REVOKED；
     * 2、微信在次日9点启动生成前一天的对账单，建议商户10点后再获取；
     * 3、对账单中涉及金额的字段单位为“元”。
     * <p>
     * 4、对账单接口只能下载三个月以内的账单。
     *
     * @param billDate   下载对账单的日期，格式：yyyyMMdd 例如：20140603
     * @param billType   账单类型
     *                   ALL，返回当日所有订单信息，默认值
     *                   SUCCESS，返回当日成功支付的订单
     *                   REFUND，返回当日退款订单
     *                   REVOKED，已撤销的订单
     * @return
     *//*
    @RequestMapping(value = "/wechat/downloadBill", method = RequestMethod.GET)
    public String downloadBill(String billDate, String billType, HttpServletResponse response) {
        try {
            return downloadBillService.request(new DownloadBillReqData(billDate, billType));
        } catch (Exception e) {
            logger.error("download bill error!", e);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }*/



    /**
     *
     * ========================= 银联支付接口接口 =======================
     * Unionpay - 网关支付: https://open.unionpay.com/ajweb/product/newProDetail?proId=1&cataId=11
     */

/*    private OpenCardFront openCardFront = new OpenCardFront();
    private OpenQuery openQuery = new OpenQuery();
    private ConsumeSMS consumeSMS = new ConsumeSMS();
    private Consume consume = new Consume();
    private OpenAndConsume openAndConsume = new OpenAndConsume();
    private ConsumeStatusQuery consumeStatusQuery = new ConsumeStatusQuery();
    private DeleteToken deleteToken = new DeleteToken();*/

    @Autowired
    private FrontConsume frontConsume;


    /**
     * Unionpay - 网关支付
     * @param no
     * @param response
     */
    @RequestMapping(value = "/unionpay/acp/frontConsume", method = RequestMethod.POST)
    public void unionpayFrontConsume(String no, HttpServletResponse response) {
        logger.info("unionpayFrontConsume, no:" + no);

        String payHtml = null;

        Pay pay = new Pay();
        pay.setNo(no);
        Pay dbPay = (Pay)payDao.query(pay).get(0);

        if (dbPay.getState().compareTo(PayConstants.state_pay_apply) == 0) {
            payHtml = frontConsume.consume(((int)(dbPay.getAmount()*100f)+""));
        } else {
            payHtml = "支付记录：" + no + "不是未支付状态，不能支付";
        }

        writer.write(response, payHtml);
    }


    /**
     * unionpay 支付页面跳转同步通知页面
     * @param json
     * @param response
     */
    @RequestMapping("/unionpay/acp/frontNotify")
    public void unionFrontNotify(@RequestBody String json, HttpServletResponse response) {
        logger.info("unionFrontNotify, json:" + json);
        PayNotify payNotify =  writer.gson.fromJson(json, new TypeToken<PayNotify>(){}.getType());

        Pay pay = new Pay();
        pay.setNo(payNotify.getOrderId());
        Pay dbPay = (Pay)payDao.query(pay).get(0);

        writer.writeStringToJson(response, "{\"" + CommonConstant.result + "\":\"" + processUnionpayNotify(json) + "" +
                "\", \"" + CommonConstant.id + "\":" + dbPay.getEntityId() + ",\"" + CommonConstant.entity +"\":\"" + dbPay.getEntity() + "\"}");

    }

    /**
     * unionpay 支付服务器异步通知
     * @param json
     * @param response
     */
    @RequestMapping("/unionpay/acp/backNotify")
    public void unionpayBackNotify(@RequestBody String json, HttpServletResponse response) {
        logger.info("unionpayBackNotify, json:" + json);
        writer.write(response, processUnionpayNotify(json));
    }

    private String processUnionpayNotify(String json) {
        String result = CommonConstant.fail;

        if (AcpService.validate(AcpService.getAllRequestParam(writer.gson.fromJson(json, new TypeToken<Map<String, String[]>>(){}.getType())), Acp.encoding_UTF8)) {
            logger.info("Sign validate success!");

            PayNotify payNotify =  writer.gson.fromJson(json, new TypeToken<PayNotify>(){}.getType());

            String no = payNotify.getOrderId();
            String processKey = PayConstants.process_notify + no;
            if (payDao.getFromRedis(processKey) == null) {
                payDao.storeToRedis(processKey, no, PayConstants.process_time);

                if(payNotify.getRespCode().equals(PayConstants.unionpay_trade_success) ||
                        payNotify.getRespCode().equals(PayConstants.unionpay_trade_success_defect)){

                    result += payService.processUnionpayNotify(payNotify);
                }

                payDao.deleteFromRedis(processKey);
            }

        } else {
            logger.info("Sign validate fail!");
        }

        result = result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
        if (!result.contains(CommonConstant.fail)) {
            result = CommonConstant.ok;
        } else {
            result = CommonConstant.fail;
        }

        return  result;
    }


    /*@RequestMapping("/unionpay/acp/openCardFront")
    public String openCardFront(String orderId) {
        String html = openCardFront.build(orderId);
        return html;
    }

    @RequestMapping("/unionpay/acp/openQuery")
    public Map<String, String> openQuery(String orderId, HttpServletRequest request) {
        try {
            return openQuery.query(orderId, null);
        } catch (SignValidateFailException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/unionpay/acp/consumeSms")
    public Map<String, String> consumeSMS(String orderId, String txnAmt, String token) {
        try {
            return consumeSMS.request(orderId, txnAmt, token);
        } catch (SignValidateFailException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/unionpay/acp/consume")
    public Map<String, String> consume(String orderId, String txnAmt, String token, String smsCode, String reqReserved) {
        try {
            return consume.consume(orderId, txnAmt, token, smsCode, reqReserved);
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (SignValidateFailException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/unionpay/acp/openConsume")
    public String openConsume(String orderId, String txnAmt, String accNo) {
        return openAndConsume.build(orderId, txnAmt, accNo);
    }

    @RequestMapping("/unionpay/acp/consumeStatusQuery")
    public Map<String, String> consumeStatusQuery(String orderId, String txnTime) {
        try {
            return consumeStatusQuery.query(orderId, txnTime);
        } catch (SignValidateFailException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/unionpay/acp/deleteToken")
    public Map<String, String> deleteToken(String orderId, String token) {
        try {
            return deleteToken.delete(orderId, token);
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (SignValidateFailException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    @GetMapping("/httpProxyAddress")
    public void getHttpProxyAddress(HttpServletResponse response) {
        writer.writeStringToJson(response, "{\"alipay\":\"" + alipaySubmit.getHttpProxyDiscovery().getHttpProxyAddress() + "\"," +
                "\"wechat\":\"" + unifiedOrderBusiness.getConfigure().getHttpProxyDiscovery().getHttpProxyAddress() + "\"}");
    }
}
