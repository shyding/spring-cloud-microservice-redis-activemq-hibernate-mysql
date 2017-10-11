package com.hzg.tools;

import java.util.HashMap;
import java.util.Map;

public class PayConstants {
    public final static String no_prefix_pay = "PA";
    public final static String no_prefix_trade = "TN";

    public final static int state_pay_apply = 0;
    public final static int state_pay_success = 1;
    public final static int state_pay_fail = 2;

    public final static int type_pay_cash = 0;
    public final static int type_pay_net = 1;
    public final static int type_pay_qrcode = 2;
    public final static int type_pay_remit = 3;

    public final static int state_refund_apply = 0;
    public final static int state_refund_success = 1;
    public final static int state_refund_fail = 2;

    public final static String process_notify = "process_notify";

    public final static String bank_alipay = "alipay";
    public final static String alipay_trade_success = "TRADE_SUCCESS";
    public final static String alipay_trade_finished = "TRADE_FINISHED";
    public final static String alipay_refund_success = "REFUND_SUCCESS";
    public final static String alipay_refund_detail_splitor = "^";

    public final static String bank_wechat = "wechat";
    public final static String wechat_pay_date_fromate = "yyyyMMddHHmmss";
    public final static String wechat_refund_success = "SUCCESS";

    public final static String bank_unionpay = "unionpay";
    public final static String unionpay_trade_success = "00";
    public final static String unionpay_trade_success_defect = "A6"; //A6有缺陷的交易成功，参与清算

    public final static int process_time = 15;
}
