package com.hzg.tools;

public class AuditFlowConstant {
    public final static String no_prefix_audit = "AU";

    public final static String audit_do = "Y";
    public final static String audit_pass = "Y";
    public final static String audit_deny = "N";
    public final static String audit_finish = "F";

    public final static Integer audit_state_done = 1;
    public final static Integer audit_state_todo = 0;

    public final static String flow_direct_forward = "forward";
    public final static String flow_direct_backwards = "backwards";

    public final static Integer flow_state_use = 0;
    public final static Integer flow_state_notUse = 1;

    public final static String business_purchase = "purchase";
    public final static String business_purchaseEmergency = "purchaseEmergency";
    public final static String business_stockIn = "stockInOut";
    public final static String business_stockIn_deposit_cangchu = "stockInOutDepositCangchu";
    public final static String business_stockIn_deposit_caiwu = "stockInOutDepositCaiwu";
    public final static String business_product = "product";
    public final static String business_returnProduct = "returnProduct";
    public final static String business_changeProduct = "changeProduct";
    public final static String business_orderPersonal = "orderPersonal";

    public final static String action_recover_prefix = "recover";

    public final static String action_purchase_product_pass = "purchaseAuditProductPass";
    public final static String action_purchase_close = "purchaseClose";
    public final static String action_purchase_emergency_pass = "purchaseEmergencyPass";
    public final static String action_purchase_emergency_pay = "purchasePayPass";
    public final static String action_stockIn = "stockIn";
    public final static String action_onSale = "onSale";

    public final static String action_purchase_modify = "purchaseModify";
    public final static String action_product_modify = "productModify";

    public final static String action_flow_purchase = "launchStockInFlow";
    public final static String action_flow_purchase_emergency = "launchStockInFlow";
    public final static String action_flow_StockIn = "launchOnSaleFlow";
}
