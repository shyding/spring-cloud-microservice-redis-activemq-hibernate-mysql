package com.hzg.tools;

public class ErpConstant {

    public final static Integer purchase_state_apply = 0;
    public final static Integer purchase_state_close = 1;
    public final static Integer purchase_state_cancel = 2;

    public final static Integer purchase_type_normal = 0;
    public final static Integer purchase_type_temp = 1;
    public final static Integer purchase_type_emergency = 2;
    public final static Integer purchase_type_cash = 3;
    public final static Integer purchase_type_deposit = 4;

    public final static Integer purchase_type_temp_payKind_totalPayment = 0;
    public final static Integer purchase_type_temp_payKind_deposit = 1;

    public final static Integer purchase_type_temp_deposit_uppay = 0;
    public final static Integer purchase_type_temp_deposit_paid = 1;
    public final static Integer purchase_type_temp_deposit_cancel = 2;

    public final static Integer product_state_purchase = 0;
    public final static Integer product_state_purchase_pass = 10;
    public final static Integer product_state_purchase_close = 11;
    public final static Integer product_state_stockIn = 1;
    public final static Integer product_state_stockIn_part = 12;
    public final static Integer product_state_stockOut = 2;
    public final static Integer product_state_stockOut_part = 21;
    public final static Integer product_state_onSale = 3;
    public final static Integer product_state_sold = 4;
    public final static Integer product_state_sold_part = 41;
    public final static Integer product_state_invalid = 5;
    public final static Integer product_state_edit = 6;
    public final static Integer product_state_mediaFiles_uploaded = 7;
    public final static Integer product_state_shipped = 8;
    public final static Integer product_state_shipped_part = 81;
    public final static Integer product_state_onReturnProduct = 9;
    public final static Integer product_state_onReturnProduct_part = 94;
    public final static Integer product_state_returnedProduct = 91;
    public final static Integer product_state_returnedProduct_part = 95;
    public final static Integer product_state_onChangeProduct = 92;
    public final static Integer product_state_onChangeProduct_part = 96;
    public final static Integer product_state_onChangedProduct = 93;
    public final static Integer product_state_onChangedProduct_part = 97;

    public final static String no_purchase_perfix = "CG";
    public final static String no_stockInOut_perfix = "RC";
    public final static String no_stock_perfix = "KC";
    public final static String no_warehouse_perfix = "CK";
    public final static String no_productCheck_perfix = "PD";

    public final static Integer stockInOut_type_cash = 0;
    public final static Integer stockInOut_type_consignment = 1;
    public final static Integer stockInOut_type_increment = 2;
    public final static Integer stockInOut_type_process = 3;
    public final static Integer stockInOut_type_deposit = 4;
    public final static Integer stockInOut_type_repair = 5;
    public final static Integer stockInOut_type_changeWarehouse = 6;
    public final static Integer stockInOut_type_virtual_outWarehouse = 10;
    public final static Integer stockInOut_type_normal_outWarehouse = 11;
    public final static Integer stockInOut_type_breakage_outWarehouse = 12;
    public final static Integer stockInOut_type_changeWarehouse_outWarehouse = 13;
    public final static Integer stockInOut_type_innerBuy_outWarehouse = 14;
    public final static Integer stockInOut_type_normal_outWarehouse_manual = 15;

    public final static Integer stockInOut_state_apply = 0;
    public final static Integer stockInOut_state_finished = 1;
    public final static Integer stockInOut_state_cancel = 2;

    public final static String stockIn = "stockIn";
    public final static String stockOut = "stockOut";
    public final static String stockInOut = "stockInOut";

    public final static String product = "product";
    public final static String productUpDownShelf = "productUpDownShelf";

    // 商品计量单位
    public final static String productUnit = "productUnit";
    // 商品盘点录入
    public final static String productCheckInput = "productCheckInput";

    public final static Integer stock_state_valid = 0;
    public final static Integer stock_state_invalid = 1;

    public final static int product_price_change_no_length = 6;

    public final static Integer product_price_change_state_apply = 0;
    public final static Integer product_price_change_state_use = 1;
    public final static Integer product_price_change_state_save = 2;

    public final static String stockInOut_action_name_print_barcode = "barcode";
    public final static String stockInOut_action_name_print_stockOutBills = "stockOutBills";
    public final static String stockInOut_action_name_print_expressWaybill = "expressWaybill";
    public final static String stockInOut_action_name_inProduct = "stockInProduct";
    public final static String stockInOut_action_name_outProduct = "stockOutProduct";

    public final static Integer stockInOut_action_print_barcode = 0;
    public final static Integer stockInOut_action_print_stockOutBills = 1;
    public final static Integer stockInOut_action_print_expressWaybill = 2;
    public final static Integer stockInOut_action_print_stockInBills = 3;
    public final static Integer stockInOut_action_inProduct = 4;
    public final static Integer stockInOut_action_outProduct = 5;


    public final static String unit_g = "克";
    public final static String unit_kg = "千克";
    public final static String unit_ct = "克拉";
    public final static String unit_oz = "盎司";


    public final static Integer product_action_upShelf = 0;
    public final static Integer product_action_downShelf = 1;

    public final static String product_action_name_queryProductAccessAllow = "queryProductAccessAllow";
    public final static String product_action_name_updateUploadMediaFilesInfo = "updateUploadMediaFilesInfo";
    public final static String product_action_name_setProductsSold = "setProductsSold";
    public final static String product_action_name_upShelf = "upShelfProduct";
    public final static String product_action_name_downShelf = "downShelfProduct";
    public final static String product_action_name_generateSfExpressOrderByReceiverAndStockOut = "generateSfExpressOrderByReceiverAndStockOut";

    public final static String privilege_resource_uri_print_expressWaybill = "/erp/print/expressWaybill";
    public final static String expressWaybill = "expressWaybill";
    public final static String waybill = "waybill";
}
