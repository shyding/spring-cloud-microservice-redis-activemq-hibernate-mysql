package com.hzg.tools;

public class PayConstants {
    public final static String no_prefix_pay = "PA";

    public final static int pay_state_apply = 0;
    public final static int pay_state_success = 1;
    public final static int pay_state_fail = 2;

    public final static int pay_type_cash = 0;
    public final static int pay_type_net = 1;
    public final static int pay_type_qrcode = 2;
    public final static int pay_type_remit = 3;
    public final static int pay_type_transfer_accounts = 4;
    public final static int pay_type_other = 5;
    public final static int pay_type_transfer_accounts_alipay = 6;
    public final static int pay_type_transfer_accounts_weixin = 7;

    public final static int state_refund_apply = 0;
    public final static int state_refund_success = 1;
    public final static int state_refund_fail = 2;

    public final static int balance_type_income = 0;
    public final static int balance_type_expense = 1;
}
