package com.hzg.tools;

public class OrderConstant {
    //未支付订单有效时常
    public final static int order_session_time = 7200;
    public final static long order_session_time_millisecond = 7200000;
    public final static int order_book_deposit_less_half_product_lock_time = 172800; // 2 * 24 * 3600
    public final static int order_book_deposit_notLess_half_product_lock_time = 94608000; // 3 * 365 * 24 * 3600

    public final static String no_order_perfix = "FR";

    public final static Integer order_state_unPay = 0;
    public final static Integer order_state_paid = 1;
    public final static Integer order_state_cancel = 2;
    public final static Integer order_state_refund = 3;
    public final static Integer order_state_paid_confirm = 4;
    public final static Integer order_state_refund_part = 5;

    public final static Integer order_detail_state_unSale = 0;
    public final static Integer order_detail_state_saled = 1;
    public final static Integer order_detail_state_book = 2;
    public final static Integer order_detail_state_return_goods = 3;
    public final static Integer order_detail_state_change_goods = 4;

    public final static Integer order_type_selfService = 0;
    public final static Integer order_type_assist  = 1;
    public final static Integer order_type_assist_process  = 4;
    public final static Integer order_type_private = 2;
    public final static Integer order_type_book = 3;

    public final static Integer order_private_type_process = 0;
    public final static Integer order_private_type_customize  = 1;

    public final static String order = "order";
    public final static String queue_order = "queue_order";
    public final static String order_no = "orderNo";

    public final static String lock_product_quantity = "lock_product_quantity";

    public final static String order_class_field_date = "date";
    public final static String order_class_field_state = "state";
    public final static String order_class_field_type = "type";

    public final static String order_action_name_cancel = "cancel";
    public final static String order_action_name_paid = "paid";
    public final static String order_action_name_audit = "audit";
    public final static String order_action_name_authorizeOrderPrivateAmount = "authorizeOrderPrivateAmount";
    public final static String order_action_name_paidOrder = "paidOrder";
    public final static String order_action_name_setOrderRefundState = "setOrderRefundState";

    public final static Integer order_action_cancel = 0;
    public final static Integer order_action_paid = 1;
    public final static Integer order_action_audit = 2;
}
