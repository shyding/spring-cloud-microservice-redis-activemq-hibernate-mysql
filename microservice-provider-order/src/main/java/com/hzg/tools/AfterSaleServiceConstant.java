package com.hzg.tools;

public class AfterSaleServiceConstant {
    public final static String no_returnProduct_perfix = "RP";

    public final static String returnProduct = "returnProduct";

    public final static String returnProduct_action_name_setReturnProduct = "setReturnProduct";
    public final static String returnProduct_action_name_saleAudit = "returnProductSaleAudit";
    public final static String returnProduct_action_name_directorAudit = "returnProductDirectorAudit";
    public final static String returnProduct_action_name_warehousingAudit = "returnProductWarehousingAudit";
    public final static String returnProduct_action_name_refund = "returnProductRefund";

    public final static Integer returnProduct_action_refund = 1;
    public final static Integer returnProduct_action_salePass = 3;
    public final static Integer returnProduct_action_saleNotPass = 31;
    public final static Integer returnProduct_action_directorPass = 4;
    public final static Integer returnProduct_action_directorNotPass = 41;
    public final static Integer returnProduct_action_warehousingPass = 5;
    public final static Integer returnProduct_action_warehousingNotPass = 51;

    public final static Integer returnProduct_state_apply = 0;
    public final static Integer returnProduct_state_refund = 1;
    public final static Integer returnProduct_state_cancel = 2;
    public final static Integer returnProduct_state_salePass = 3;
    public final static Integer returnProduct_state_saleNotPass = 31;
    public final static Integer returnProduct_state_directorPass = 4;
    public final static Integer returnProduct_state_directorNotPass = 41;
    public final static Integer returnProduct_state_warehousingPass = 5;
    public final static Integer returnProduct_state_warehousingNotPass = 51;

    public final static Integer returnProduct_detail_state_unReturn = 0;
    public final static Integer returnProduct_detail_state_returned = 1;
    public final static Integer returnProduct_detail_state_cannotReturn = 2;
}
