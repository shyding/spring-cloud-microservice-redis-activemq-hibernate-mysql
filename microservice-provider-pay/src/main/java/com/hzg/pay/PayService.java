package com.hzg.pay;

import com.boyuanitsm.pay.alipay.bean.AyncNotify;
import com.boyuanitsm.pay.alipay.bean.RefundAyncNotify;
import com.boyuanitsm.pay.alipay.bean.SyncReturn;
import com.boyuanitsm.pay.alipay.config.AlipayConfig;
import com.boyuanitsm.pay.unionpay.Acp;
import com.boyuanitsm.pay.unionpay.b2c.PayNotify;
import com.boyuanitsm.pay.wxpay.common.Configure;
import com.boyuanitsm.pay.wxpay.common.MD5;
import com.boyuanitsm.pay.wxpay.common.XMLParser;
import com.boyuanitsm.pay.wxpay.protocol.RefundResultCallback;
import com.boyuanitsm.pay.wxpay.protocol.ResultCallback;
import com.hzg.order.Order;
import com.hzg.tools.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Service
public class PayService {
    Logger logger = Logger.getLogger(PayService.class);

    @Autowired
    private PayDao payDao;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private Aes aes;

    @Autowired
    private OrderClient orderClient;

    public String processAlipayNotify(AyncNotify ayncNotify) {
        String result = CommonConstant.fail;

        Pay pay = new Pay();
        pay.setNo(ayncNotify.getOut_trade_no());
        Pay dbPay = (Pay)payDao.query(pay).get(0);

        if (dbPay.getAmount().compareTo(ayncNotify.getTotal_fee()) == 0 &&
                AlipayConfig.seller_id.equals(ayncNotify.getSeller_id())) {

            if (dbPay.getState().compareTo(PayConstants.state_pay_apply) == 0) {
                pay.setId(dbPay.getId());
                pay.setState(PayConstants.state_pay_success);

                pay.setPayType(Integer.parseInt(ayncNotify.getBody()));
                pay.setPayDate(Timestamp.valueOf(ayncNotify.getGmt_payment()));
                pay.setBankBillNo(ayncNotify.getTrade_no());

                pay.setPayBank(PayConstants.bank_alipay);
                pay.setPayAccount(ayncNotify.getBuyer_id());
                pay.setReceiptBank(PayConstants.bank_alipay);
                pay.setReceiptAccount(ayncNotify.getSeller_id());

                result += payDao.updateById(pay.getId(), pay);

                result += setAccountAmount(dbPay.getReceiptBank(), dbPay.getReceiptAccount(), dbPay.getAmount());
                result += doBusinessAfterPay(dbPay.getEntity(), dbPay.getEntityId());

            } else if(dbPay.getState().compareTo(PayConstants.state_pay_success) == 0) {
                result += CommonConstant.success;
            }

        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String processAlipayReturn(SyncReturn syncReturn) {
        String result = CommonConstant.fail;

        Pay pay = new Pay();
        pay.setNo(syncReturn.getOut_trade_no());
        Pay dbPay = (Pay)payDao.query(pay).get(0);

        if (dbPay.getAmount().compareTo(syncReturn.getTotal_fee()) == 0 &&
                AlipayConfig.seller_id.equals(syncReturn.getSeller_id())) {

            if (dbPay.getState().compareTo(PayConstants.state_pay_apply) == 0) {
                pay.setId(dbPay.getId());
                pay.setState(PayConstants.state_pay_success);

                pay.setPayType(Integer.parseInt(syncReturn.getBody()));
                pay.setPayDate(Timestamp.valueOf(syncReturn.getNotify_time()));
                pay.setBankBillNo(syncReturn.getTrade_no());

                pay.setPayBank(PayConstants.bank_alipay);
                pay.setPayAccount(syncReturn.getBuyer_id());
                pay.setReceiptBank(PayConstants.bank_alipay);
                pay.setReceiptAccount(syncReturn.getSeller_id());

                result += payDao.updateById(pay.getId(), pay);

                result += setAccountAmount(dbPay.getReceiptBank(), dbPay.getReceiptAccount(), dbPay.getAmount());
                result += doBusinessAfterPay(dbPay.getEntity(), dbPay.getEntityId());
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String processAlipayRefundNotify(RefundAyncNotify refundAyncNotify) {
        String result = CommonConstant.fail;

        if (refundAyncNotify.getResult_details().split(PayConstants.alipay_refund_detail_splitor)[2].equalsIgnoreCase(CommonConstant.success)) {

            Refund refund = new Refund();
            refund.setNo(refundAyncNotify.getBatch_no());
            Refund dbRefund = (Refund) payDao.query(refund).get(0);

            if (dbRefund != null) {
                if (dbRefund.getState().compareTo(PayConstants.state_refund_apply) == 0) {
                    refund.setId(dbRefund.getId());
                    refund.setState(PayConstants.state_refund_success);
                    refund.setRefundDate(Timestamp.valueOf(refundAyncNotify.getNotify_time()));

                    result += payDao.updateById(refund.getId(), refund);

                    result += setAccountAmount(dbRefund.getPay().getReceiptBank(), dbRefund.getPay().getReceiptAccount(), -dbRefund.getAmount());
                    result += doBusinessAfterRefund(dbRefund.getPay().getEntity(), dbRefund.getPay().getEntityId());

                } else if (dbRefund.getState().compareTo(PayConstants.state_refund_success) == 0) {
                    result += CommonConstant.success;
                }
            }
        }

        return result;
    }

    public String processWechatCallback(String no, ResultCallback resultCallback) {
        String result = CommonConstant.fail;

        Pay pay = new Pay();
        pay.setNo(no);
        Pay dbPay = (Pay)payDao.query(pay).get(0);

        if (dbPay.getAmount().toString().equals(resultCallback.getTotal_fee()) &&
                Configure.getMchid().equals(resultCallback.getMch_id())) {

            if (dbPay.getState().compareTo(PayConstants.state_pay_apply) == 0) {
                pay.setId(dbPay.getId());
                pay.setState(PayConstants.state_pay_success);

                pay.setPayType(Integer.valueOf(resultCallback.getAttach()));
                pay.setPayDate(Timestamp.valueOf(dateUtil.getSimpleDateFormat().format(
                        dateUtil.getDate(PayConstants.wechat_pay_date_fromate, resultCallback.getTime_end()))));
                pay.setBankBillNo(resultCallback.getTransaction_id());

                pay.setPayBank(PayConstants.bank_wechat + "|" + resultCallback.getTrade_type() + "|" + resultCallback.getBank_type());
                pay.setPayAccount(resultCallback.getOpenid());
                pay.setReceiptBank(PayConstants.bank_wechat);
                pay.setReceiptAccount(resultCallback.getMch_id());

                result += payDao.updateById(pay.getId(), pay);

                result += setAccountAmount(dbPay.getReceiptBank(), dbPay.getReceiptAccount(), dbPay.getAmount());
                result += doBusinessAfterPay(dbPay.getEntity(), dbPay.getEntityId());

            } else if(dbPay.getState().compareTo(PayConstants.state_pay_success) == 0) {
                result += CommonConstant.success;
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String processWechatRefundCallback(String no, RefundResultCallback resultCallback) {
        String result = CommonConstant.fail;

        if (resultCallback.getRefund_status().equals(PayConstants.wechat_refund_success)) {
            Refund refund = new Refund();
            refund.setNo(resultCallback.getOut_refund_no());
            Refund dbRefund = (Refund) payDao.query(refund).get(0);

            if (dbRefund != null) {
                if (dbRefund.getState().compareTo(PayConstants.state_refund_apply) == 0) {
                    refund.setId(dbRefund.getId());
                    refund.setState(PayConstants.state_refund_success);
                    refund.setRefundDate(Timestamp.valueOf(dateUtil.getSimpleDateFormat().format(
                            dateUtil.getDate(PayConstants.wechat_pay_date_fromate, resultCallback.getSuccess_time()))));

                    result += payDao.updateById(refund.getId(), refund);

                    result += setAccountAmount(dbRefund.getPay().getReceiptBank(), dbRefund.getPay().getReceiptAccount(), -dbRefund.getAmount());
                    result += doBusinessAfterRefund(dbRefund.getPay().getEntity(), dbRefund.getPay().getEntityId());

                } else if (dbRefund.getState().compareTo(PayConstants.state_refund_success) == 0) {
                    result += CommonConstant.success;
                }
            }
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String processUnionpayNotify(PayNotify payNotify) {
        String result = CommonConstant.fail;

        Pay pay = new Pay();
        pay.setNo(payNotify.getOrderId());
        Pay dbPay = (Pay)payDao.query(pay).get(0);

        if (dbPay.getState().compareTo(PayConstants.state_pay_apply) == 0) {
            pay.setId(dbPay.getId());
            pay.setState(PayConstants.state_pay_success);

            pay.setPayType(1);
            pay.setPayDate(new java.sql.Timestamp(dateUtil.getDate("yyyyMMddHHmmss",
                    dateUtil.getCurrentDateStr().substring(0, 4) + payNotify.getTraceTime()).getTime()));
            pay.setBankBillNo(payNotify.getQueryId());
            pay.setSettleAmount(payNotify.getSettleAmt());

            pay.setPayBank(PayConstants.bank_unionpay+ "|" + payNotify.getTxnType());
            pay.setReceiptBank(PayConstants.bank_unionpay);
            pay.setReceiptAccount(Acp.merId);

            result += payDao.updateById(pay.getId(), pay);

            result += setAccountAmount(dbPay.getReceiptBank(), dbPay.getReceiptAccount(), dbPay.getAmount());
            result += doBusinessAfterPay(dbPay.getEntity(), dbPay.getEntityId());

        } else if(dbPay.getState().compareTo(PayConstants.state_pay_success) == 0) {
            result += CommonConstant.success;
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }

    public String setAccountAmount(String bank, String account, Float amount){
        String result = "";

        Account queryAccount = new Account();
        queryAccount.setBank(bank);
        queryAccount.setAccount(account);

        Account dbAccount = (Account) payDao.query(queryAccount).get(0);
        BigDecimal accountAmount = new BigDecimal(Float.toString(dbAccount.getAmount()));
        BigDecimal itemAmount = new BigDecimal(Float.toString(amount));
        dbAccount.setAmount(accountAmount.add(itemAmount).floatValue());
        result = payDao.updateById(dbAccount.getId(), dbAccount);

        return result;
    }

    public String doBusinessAfterPay(String entity, Integer entityId) {
        String result = "";

        if (entity.equalsIgnoreCase(Order.class.getSimpleName())) {
            result += orderClient.business("paidOrder", "{\"" + CommonConstant.id +"\":" + entityId + "}");
        }

        return result;
    }

    public String doBusinessAfterRefund(String entity, Integer entityId) {
        return "";
    }

    public RefundResultCallback decryptWechatRefundResult(String responseString) throws Exception{
        RefundResultCallback resultCallback = (RefundResultCallback) XMLParser.getObjectFromXML(responseString, RefundResultCallback.class);

        String decodeStr = aes.decryptStr(new String((new BASE64Decoder()).decodeBuffer(resultCallback.getReq_info())),
                MD5.MD5Encode(Configure.getKey()).toLowerCase());

        logger.info("decodeStr:" + decodeStr);

        return (RefundResultCallback) XMLParser.getObjectFromXML(responseString.replace("</xml>", decodeStr+"</xml>"), RefundResultCallback.class);
    }
}
