package com.hzg.tools;

public class AuditFlowConstant {
    public final static String business_purchase = "purchase";
    public final static String business_purchaseEmergency = "purchaseEmergency";
    public final static String business_product = "product";
    public final static String business_returnProduct = "returnProduct";
    public final static String business_changeProduct = "changeProduct";
    public final static String business_orderPersonal = "orderPersonal";

    public final static String audit_do = "Y";
    public final static String audit_pass = "Y";
    public final static String audit_deny = "N";
    public final static String audit_finish = "F";

    public final static Integer audit_state_done = 0;
    public final static Integer audit_state_todo = 1;

    public final static String flow_direct_forward = "forward";
    public final static String flow_direct_backwards = "backwards";

    public final static Integer flow_state_use = 0;
    public final static Integer flow_state_notUse = 1;
}
