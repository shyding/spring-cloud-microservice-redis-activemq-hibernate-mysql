package com.hzg.tools;

public class ErpConstant {

    public final static String product = "product";

    public final static String product_id = "productId";
    public final static String product_no = "productNo";
    public final static String stock_quantity = "stockQuantity";
    public final static String product_sold_quantity = "productSoldQuantity";
    public final static String product_onReturn_quantity = "productOnReturnQuantity";
    public final static String product_returned_quantity = "productReturnedQuantity";

    public final static String price = "price";

    public final static Integer product_price_change_state_use = 1;

    public final static String no_stockInOut_perfix = "RC";
    public final static Integer stockInOut_type_returnProduct = 51;
    public final static Integer stockInOut_type_normal_outWarehouse = 11;
    public final static Integer stockInOut_state_finished = 1;

    public final static Integer product_state_onSale = 3;
    public final static Integer product_state_sold = 4;
    public final static Integer product_state_sold_part = 41;
    public final static Integer product_state_shipped = 8;
    public final static Integer product_state_shipped_part = 81;

    public final static String deliver_sfExpress = "顺丰快递";
    public final static String deliver_sfExpress_type = "顺丰标快";

    public final static Integer express_state_sending = 0;
    public final static Integer express_state_sended = 1;
    public final static Integer express_state_send_fail = 2;

    public final static Integer express_detail_state_unSend = 0;
    public final static Integer express_detail_state_sended = 1;
    public final static Integer express_detail_state_received = 2;
    public final static Integer express_detail_state_receive_fail = 3;

    public final static String stockOut = "stockOut";

    public final static String product_action_name_setProductsSold = "setProductsSold";
    public final static String product_action_name_setProductsOnReturn = "setProductsOnReturn";
    public final static String product_action_name_setProductsReturned = "setProductsReturned";
    public final static String stockInOut_action_name_inProduct = "stockInProduct";
    public final static String stockInOut_action_name_outProduct = "stockOutProduct";
    public final static String product_action_name_upShelf = "upShelfProduct";

    public final static String unit_g = "克";
    public final static String unit_kg = "千克";
    public final static String unit_ct = "克拉";
    public final static String unit_oz = "盎司";
}
