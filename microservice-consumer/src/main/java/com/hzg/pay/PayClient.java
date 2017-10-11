package com.hzg.pay;

import com.boyuanitsm.pay.alipay.bean.AyncNotify;
import com.boyuanitsm.pay.alipay.bean.SyncReturn;
import com.boyuanitsm.pay.wxpay.protocol.pay_query_protocol.OrderQueryResData;
import com.boyuanitsm.pay.wxpay.protocol.refund_protocol.RefundResData;
import com.boyuanitsm.pay.wxpay.protocol.refund_query_protocol.RefundQueryResData;
import com.hzg.base.Client;
import com.hzg.tools.CommonConstant;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;

@FeignClient(name = "microservice-provider-pay", path = "/pay", fallback = PayClient.ErpClientFallback.class)
public interface PayClient extends Client {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PayClient.class);

    @RequestMapping(value = "/alipay/pay", method = RequestMethod.POST)
    String alipay(@RequestParam("no") String no, @RequestParam("payType") String payType);

    @RequestMapping(value = "/alipay/refund", method = RequestMethod.POST)
    String alipayRefund(@RequestParam("no") String no);

    @RequestMapping(value = "/alipay/syncReturn", method = RequestMethod.GET)
    String alipaySyncReturn(@RequestBody String json);

    @RequestMapping(value = "/alipay/ayncNotify", method = RequestMethod.POST)
    String alipayAyncNotify(@RequestBody String json);

    @RequestMapping(value = "/alipay/refundAyncNotify", method = RequestMethod.POST)
    String alipayRefundAyncNotify(@RequestBody String json);

    @RequestMapping(value = "/alipay/payQrcode", method = RequestMethod.GET)
    byte[] alipayPayQrcode(@RequestParam("no") String no);

    @RequestMapping(value = "/alipay/toAlipay", method = RequestMethod.GET)
    String toAlipay(@RequestParam("no") String no, @RequestParam("payType") String payType);

    @RequestMapping(value = "/wechat/unifiedOrder", method = RequestMethod.GET)
    byte[] wechatUnifiedOrder(@RequestParam("no") String no);

    @RequestMapping(value = "/wechat/refund", method = RequestMethod.POST)
    String wechatRefund(@RequestParam("no") String no);

    @RequestMapping(value = "/wechat/payResultCallback", method = RequestMethod.POST)
    String wechatPayResultCallback(@RequestBody String responseString);

    @RequestMapping(value = "/wechat/refundResultCallback", method = RequestMethod.POST)
    String wechatRefundResultCallback(@RequestBody String responseString);

    @RequestMapping(value = "/unionpay/acp/frontConsume", method = RequestMethod.GET)
    String unionpayFrontConsume(@RequestParam("no") String no);

    @RequestMapping(value = "/unionpay/acp/frontNotify", method = RequestMethod.POST)
    String unionpayFrontNotify(@RequestBody String json);

    @RequestMapping(value = "/unionpay/acp/backNotify", method = RequestMethod.GET)
    String unionpayBackNotify(@RequestBody String json);

    @Component
    class ErpClientFallback extends ClientFallback implements PayClient {
        public String alipay(String no, String payType) {
            logger.info("alipay 异常发生，进入fallback方法，接收的参数：" + no + "," + payType);
            return "<html><head><title>error</title></head><body>系统异常，支付宝即时到账交易失败</body></html>";
        }

        public String alipayRefund(String no) {
            logger.info("alipayRefund 异常发生，进入fallback方法，接收的参数：" + no);
            return "<html><head><title>error</title></head><body>系统异常，支付宝即时到账批量退款失败</body></html>";
        }

        public String alipaySyncReturn(String json){
            logger.info("alipaySyncReturn 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，支付宝页面跳转同步通知出错\"}";
        }

        public String alipayAyncNotify(String json){
            logger.info("alipayAyncNotify 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，支付宝服务器异步通知出错\"}";
        }

        public String alipayRefundAyncNotify(String json){
            logger.info("alipayRefundAyncNotify 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，支付宝服务器异步通知出错\"}";
        }


        public byte[] alipayPayQrcode(String no){
            logger.info("payQrcode 异常发生，进入fallback方法，接收的参数：" + no);
            try {
                return ("{\"" + CommonConstant.result + "\":\"系统异常，获取支付宝支付二维码出错\"}").getBytes(CommonConstant.UTF8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String toAlipay(String no, String payType){
            logger.info("toAlipay 异常发生，进入fallback方法，接收的参数：" + no + "," + payType);
            return "<html><head><title>error</title></head><body>系统异常，跳转到支付宝支付出错</body></html>";
        }

        public byte[] wechatUnifiedOrder(String no){
            logger.info("unifiedOrder 异常发生，进入fallback方法，接收的参数：" + no);
            try {
                return ("{\"" + CommonConstant.result + "\":\"系统异常，微信扫码支付出错\"}").getBytes(CommonConstant.UTF8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String wechatRefund(String no){
            logger.info("wechatRefund 异常发生，进入fallback方法，接收的参数：" + no);
            return "{\"" + CommonConstant.result + "\":\"系统异常，微信退款申请出错\"}";
        }

        public String wechatPayResultCallback(String responseString){
            logger.info("wechatPayResultCallback 异常发生，进入fallback方法," + responseString);
            return "<html><head><title>error</title></head><body>系统异常，微信支付结果通知出错</body></html>";
        }

        public String wechatRefundResultCallback(String responseString){
            logger.info("wechatRefundResultCallback 异常发生，进入fallback方法," + responseString);
            return "<html><head><title>error</title></head><body>系统异常，微信退款结果通知出错</body></html>";
        }

        public String unionpayFrontConsume(@RequestParam("no") String no){
            logger.info("unionpayFrontConsume 异常发生，进入fallback方法，接收的参数：" + no);
            return "<html><head><title>error</title></head><body>系统异常，银联网关支付出错</body></html>";
        }

        public String unionpayFrontNotify(String json){
            logger.info("unionpayFrontNotify 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，支付宝服务器异步通知出错\"}";
        }

        public String unionpayBackNotify(String json){
            logger.info("alipaySyncReturn 异常发生，进入fallback方法，接收的参数：" + json);
            return "{\"" + CommonConstant.result + "\":\"系统异常，支付宝页面跳转同步通知出错\"}";
        }
    }
}
