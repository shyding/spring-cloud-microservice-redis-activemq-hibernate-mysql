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

    public final static Integer product_state_purchase = 0;
    public final static Integer product_state_purchase_pass = 10;
    public final static Integer product_state_purchase_close = 11;
    public final static Integer product_state_stockIn = 1;
    public final static Integer product_state_stockOut = 2;
    public final static Integer product_state_onSale = 3;
    public final static Integer product_state_sold = 4;
    public final static Integer product_state_invalid = 5;
    public final static Integer product_state_edit = 6;
    public final static Integer product_state_mediaFiles_uploaded = 7;

    public final static String no_purchase_perfix = "CG";
    public final static String no_stockInOut_perfix = "RC";
    public final static String no_stock_perfix = "KC";
    public final static String no_warehouse_perfix = "CK";
    public final static String no_expressDelivery_perfix = "ED";

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

    public final static Integer stockInOut_state_apply = 0;
    public final static Integer stockInOut_state_finished = 1;
    public final static Integer stockInOut_state_cancel = 2;
    public final static Integer stockInOut_state_backup = 3;

    public final static Integer stockInOut_state_changeWarehouse_unfinished = 0;
    public final static Integer stockInOut_state_changeWarehouse_finished = 1;

    public final static Integer stock_state_valid = 0;
    public final static Integer stock_state_invalid = 1;

    public final static String return_purchase = "purchase";
    public final static String return_purchase_deposit = "purchaseDeposit";
    public final static String return_product = "returnProduct";
    public final static String change_product = "changeProduct:";

    public final static String no = "no";
    public final static String stock = "stock";
    public final static String stock_quantity = "stockQuantity";

    public final static String stockIn = "stockIn";
    public final static String stockOut = "stockOut";

    public final static String price = "price";
    public final static String price_change = "price_change";

    public final static Integer product_price_change_state_apply = 0;
    public final static Integer product_price_change_state_use = 1;
    public final static Integer product_price_change_state_save = 2;

    public final static float price_1000 = 1000f;
    public final static float price_3000 = 3000f;
    public final static float price_10000 = 10000f;
    public final static float price_30000 = 30000f;
    public final static float price_100000 = 100000f;

    public final static float subtract_price_0 = 0f;
    public final static float subtract_price_100 = 100f;
    public final static float subtract_price_300 = 300f;
    public final static float subtract_price_500 = 500f;
    public final static float subtract_price_1000 = 1000f;

    public final static float price_percent_85 = 0.85f;
    public final static float price_percent_90 = 0.90f;
    public final static float price_percent_95 = 0.95f;

    public final static String post_name_saler = "销售员";
    public final static String post_name_charger = "销售主管";
    public final static String post_name_manager = "销售经理";
    public final static String post_name_director = "销售总监";

    public final static Integer stockInOut_action_print_barcode = 0;
    public final static Integer stockInOut_action_print_stockOutBills = 1;
    public final static Integer stockInOut_action_print_expressWaybill = 2;

    public final static String product_property_name_weight = "重量";

    public final static String sf_response_code_success = "EX_CODE_OPENAPI_0200";
    public final static String sf_response_code_refresh_token_unExist = "EX_CODE_OPENAPI_0104";
    public final static String sf_response_code_refresh_token_timeout = "EX_CODE_OPENAPI_0106";
    public final static String sf_access_token_key = "sf_access_token_key";
    public final static String sf_refresh_token_key = "sf_access_token_key";
    public final static int sf_token_time = 60 * 60 * 24;


    public final static String deliver_sfExpress = "顺丰快递";

    public final static Integer sf_action_code_order = 200;
    public final static Integer sf_action_code_access_token = 301;
    public final static Integer sf_action_code_refresh_Token = 302;
    public final static Integer sf_action_code_download_waybill = 205;

    public final static Integer express_state_sending = 0;
    public final static Integer express_state_sended = 1;
    public final static Integer express_state_send_fail = 2;

    public final static Integer express_detail_state_unReceive = 0;
    public final static Integer express_detail_state_received = 1;
    public final static Integer express_detail_state_receive_fail = 2;


}
