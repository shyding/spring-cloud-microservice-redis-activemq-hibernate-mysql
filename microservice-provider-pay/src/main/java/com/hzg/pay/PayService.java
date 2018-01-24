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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    @Autowired
    private Writer writer;

    public String processAlipayNotify(AyncNotify ayncNotify) {
        String result = CommonConstant.fail;

        Pay pay = new Pay();
        pay.setNo(ayncNotify.getOut_trade_no());
        Pay dbPay = (Pay)payDao.query(pay).get(0);

        if (dbPay.getAmount().compareTo(ayncNotify.getTotal_fee()) == 0 &&
                AlipayConfig.seller_id.equals(ayncNotify.getSeller_id())) {

            if (dbPay.getState().compareTo(PayConstants.pay_state_apply) == 0) {
                pay.setId(dbPay.getId());
                pay.setState(PayConstants.pay_state_success);

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

            } else if(dbPay.getState().compareTo(PayConstants.pay_state_success) == 0) {
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

            if (dbPay.getState().compareTo(PayConstants.pay_state_apply) == 0) {
                pay.setId(dbPay.getId());
                pay.setState(PayConstants.pay_state_success);

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
                if (dbRefund.getState().compareTo(PayConstants.refund_state_apply) == 0) {
                    refund.setId(dbRefund.getId());
                    refund.setState(PayConstants.refund_state_success);
                    refund.setRefundDate(Timestamp.valueOf(refundAyncNotify.getNotify_time()));

                    result += payDao.updateById(refund.getId(), refund);

                    result += setAccountAmount(dbRefund.getPay().getReceiptBank(), dbRefund.getPay().getReceiptAccount(), -dbRefund.getAmount());
                    result += doBusinessAfterRefund(dbRefund.getPay().getEntity(), dbRefund.getPay().getEntityId());

                } else if (dbRefund.getState().compareTo(PayConstants.refund_state_success) == 0) {
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

            if (dbPay.getState().compareTo(PayConstants.pay_state_apply) == 0) {
                pay.setId(dbPay.getId());
                pay.setState(PayConstants.pay_state_success);

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

            } else if(dbPay.getState().compareTo(PayConstants.pay_state_success) == 0) {
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
                if (dbRefund.getState().compareTo(PayConstants.refund_state_apply) == 0) {
                    refund.setId(dbRefund.getId());
                    refund.setState(PayConstants.refund_state_success);
                    refund.setRefundDate(Timestamp.valueOf(dateUtil.getSimpleDateFormat().format(
                            dateUtil.getDate(PayConstants.wechat_pay_date_fromate, resultCallback.getSuccess_time()))));

                    result += payDao.updateById(refund.getId(), refund);

                    result += setAccountAmount(dbRefund.getPay().getReceiptBank(), dbRefund.getPay().getReceiptAccount(), -dbRefund.getAmount());
                    result += doBusinessAfterRefund(dbRefund.getPay().getEntity(), dbRefund.getPay().getEntityId());

                } else if (dbRefund.getState().compareTo(PayConstants.refund_state_success) == 0) {
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

        if (dbPay.getState().compareTo(PayConstants.pay_state_apply) == 0) {
            pay.setId(dbPay.getId());
            pay.setState(PayConstants.pay_state_success);

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

        } else if(dbPay.getState().compareTo(PayConstants.pay_state_success) == 0) {
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
            result += orderClient.business(OrderConstant.order_action_name_paidOnlineOrder, "{\"" + CommonConstant.id +"\":" + entityId + "}");
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

    /**
     * 查询支付记录可退余额
     * @param pay
     * @return
     */
    public List<Pay> queryBalancePay(Pay pay) {
        List<Pay> balancePays = new ArrayList<>();

        List<Pay> pays = payDao.query(pay);
        Refund queryRefund = new Refund();
        queryRefund.setState(PayConstants.refund_state_success);

        for (Pay ele : pays) {
            queryRefund.setPay(ele);
            List<Refund> refunds = payDao.query(queryRefund);

            for (Refund refund : refunds) {
                ele.setAmount(new BigDecimal(Float.toString(ele.getAmount())).
                        subtract(new BigDecimal(Float.toString(refund.getAmount()))).floatValue());
            }

            if (ele.getAmount().compareTo(0f) > 0) {
                balancePays.add(ele);
            }
        }

        return balancePays;
    }

    /**
     * 支付记录列表里的支付记录按指定金额保存
     *
     *
     * 先对支付记录列表按照支付金额有大到小排序
     *
     * 1、如果支付记录列表里有支付金额 = 指定金额的，则首先保存该支付记录，再保存其他支付记录
     * 2、如果支付记录列表里没有支付金额 = 指定金额的,但是有支付记录 > 指定金额的，则把该支付记录分为 2 条支付记录，其中一条的
     *    支付金额 = 指定金额，然后先保存等于指定金额的这条支付记录及另一条支付记录,再保存其他支付记录
     * 3、如果支付记录列表里的支付金额都 < 指定金额，则先从支付记录列表头开始累加支付记录的金额，直到该累加的支付金额 >= 指定金额，
     *  再把最后累加的支付记录分为 2 条支付记录，其中一条的金额 + 前面累加的 (n -1)支付金额 = 指定金额，然后先保存累加的 (n -1) 条
     *  支付记录，再保存分割后的这条支付记录及另一条支付记录，再保存其他支付记录
     *
     * @param amount
     * @param pays
     * @return
     */
    public String saveSplitAmountPays(Float amount, List<Pay> pays) {
        String result = CommonConstant.fail;
        List<Pay> toSavePays = new ArrayList<>();

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

        int equalPosition = -1, largerPosition = -1;
        for (int i = 0; i < pays.size(); i++) {
            if (equalPosition == -1) {
                if (pays.get(i).getAmount().compareTo(amount) == 0) {
                    equalPosition = i;
                }
            }

            if (largerPosition == -1) {
                if (pays.get(i).getAmount().compareTo(amount) > 0) {
                    largerPosition = i;
                }
            }
        }

        if (equalPosition != -1) {
            toSavePays.add(pays.get(equalPosition));

            for (int i = 0; i < pays.size(); i++) {
                if (i != equalPosition) {
                    toSavePays.add(pays.get(i));
                }
            }

        } else {
            if (largerPosition > -1) {
                Pay equalPay = writer.gson.fromJson(writer.gson.toJson(pays.get(largerPosition)), Pay.class);
                equalPay.setAmount(amount);
                toSavePays.add(equalPay);

                Pay differencePay = writer.gson.fromJson(writer.gson.toJson(pays.get(largerPosition)), Pay.class);
                differencePay.setAmount(new BigDecimal(Float.toString(pays.get(largerPosition).getAmount())).subtract(new BigDecimal(Float.toString(amount))).floatValue());
                toSavePays.add(differencePay);

                for (int i = largerPosition+1; i < pays.size(); i++) {
                    toSavePays.add(pays.get(i));
                }

            } else {
                int notSplitPosition = -1;
                Float balance = amount;
                for (int i = 0; i < pays.size(); i++) {
                   if (balance.compareTo(pays.get(i).getAmount()) >= 0) {
                       toSavePays.add(pays.get(i));
                       balance = new BigDecimal(Float.toString(balance)).subtract(new BigDecimal(Float.toString(pays.get(i).getAmount()))).floatValue();

                   } else {
                       Pay equalPay = writer.gson.fromJson(writer.gson.toJson(pays.get(i)), Pay.class);
                       equalPay.setAmount(balance);
                       toSavePays.add(equalPay);

                       Pay differencePay = writer.gson.fromJson(writer.gson.toJson(pays.get(i)), Pay.class);
                       differencePay.setAmount(new BigDecimal(Float.toString(pays.get(i).getAmount())).subtract(new BigDecimal(Float.toString(balance))).floatValue());
                       toSavePays.add(differencePay);

                       notSplitPosition = i+1;
                   }
                }

                if (notSplitPosition != -1) {
                    for (int i = notSplitPosition; i < pays.size(); i++) {
                        toSavePays.add(pays.get(i));
                    }
                }
            }
        }

        Timestamp inputDate = dateUtil.getSecondCurrentTimestamp();
        for (Pay pay : toSavePays) {
            pay.setNo(payDao.getNo());
            pay.setInputDate(inputDate);
            result += payDao.save(pay);
        }

        return result.equals(CommonConstant.fail) ? result : result.substring(CommonConstant.fail.length());
    }
}
