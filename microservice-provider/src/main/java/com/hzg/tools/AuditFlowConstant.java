package com.hzg.tools;


import java.util.HashMap;
import java.util.Map;

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
    public final static String business_stockIn_notify = "stockInNotify";
    public final static String business_stockIn_notify_caiwu = "stockInNotifyCaiwu";
    public final static String business_stockIn_deposit_cangchu = "stockInOutDepositCangchu";
    public final static String business_stockIn_deposit_caiwu = "stockInOutDepositCaiwu";
    public final static String business_product = "product";
    public final static String business_returnProduct = "returnProduct";
    public final static String business_changeProduct = "changeProduct";
    public final static String business_orderPersonal = "orderPersonal";

    public final static String business_price_change_saler = "priceChangeSaler";
    public final static String business_price_change_charger = "priceChangeCharger";
    public final static String business_price_change_manager = "priceChangeManager";
    public final static String business_price_change_director = "priceChangeDirector";


    public final static String action_recover_prefix = "recover";

    public final static String action_purchase_product_pass = "purchaseAuditProductPass";
    public final static String action_purchase_close = "purchaseClose";
    public final static String action_purchase_emergency_pass = "purchaseEmergencyPass";
    public final static String action_purchase_emergency_pay = "purchasePayPass";
    public final static String action_stockIn = "stockIn";
    public final static String action_stockIn_return_deposit = "returnDeposit";
    public final static String action_onSale = "onSale";
    public final static String action_purchase_modify = "purchaseModify";
    public final static String action_product_modify = "productModify";

    public final static String action_product_files_upload = "productFilesUpload";
    public final static String action_product_stockIn_modify = "stockInProductModify";
    public final static String action_product_onSale = "productOnSale";

    public final static String action_flow_StockIn_notify = "stockInFlowNotify";
    public final static String action_flow_StockIn_notify_caiwu = "stockInFlowNotifyCaiwu";

    public final static String action_price_change_set_state_use = "priceChangeSetStateUse";

    public final static Map<String, String> action_names = new HashMap<>();
    static {
        action_names.put(AuditFlowConstant.action_flow_StockIn_notify, "提示入库商品");
        action_names.put(AuditFlowConstant.action_flow_StockIn_notify_caiwu, "财务提示入库商品");

        action_names.put(AuditFlowConstant.action_purchase_product_pass, "审核通过商品");
        action_names.put(AuditFlowConstant.action_purchase_emergency_pass, "审核通过采购单");
        action_names.put(AuditFlowConstant.action_purchase_close, "关闭采购单");
        action_names.put(AuditFlowConstant.action_purchase_emergency_pay, "财务确认付款");
        action_names.put(AuditFlowConstant.action_stockIn, "入库商品");
        action_names.put(AuditFlowConstant.action_stockIn_return_deposit, "退还押金");
        action_names.put(AuditFlowConstant.action_onSale, "上架商品");

        action_names.put(AuditFlowConstant.action_purchase_modify, "可以修改采购单");
        action_names.put(AuditFlowConstant.action_product_modify, "可以修改采购单里的商品");

        action_names.put(AuditFlowConstant.action_product_files_upload, "可以上传商品多媒体文件");
        action_names.put(AuditFlowConstant.action_product_stockIn_modify, "可以修改入库商品");
        action_names.put(AuditFlowConstant.action_product_onSale, "可以上架商品");

        action_names.put(AuditFlowConstant.action_price_change_set_state_use, "设置浮动价格可用");

        action_names.put(null, "无");
    }

    public final static Map<String, String> flow_action_entity_mapping = new HashMap<>();
    static {
        action_names.put(AuditFlowConstant.action_flow_StockIn_notify, AuditFlowConstant.business_stockIn_notify);
        action_names.put(AuditFlowConstant.action_flow_StockIn_notify_caiwu, AuditFlowConstant.business_stockIn_notify_caiwu);
    }
}
