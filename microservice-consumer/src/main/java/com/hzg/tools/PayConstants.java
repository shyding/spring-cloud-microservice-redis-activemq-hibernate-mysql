package com.hzg.tools;

import org.apache.commons.collections.map.HashedMap;

import java.util.Map;
import java.util.Set;

public class PayConstants {
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

    public final static String bank_alipay = "alipay";
    public final static String bank_wechat = "wechat";
    public final static String bank_unionpay = "unionpay";

    public final static String bank_PBC = "PBC";
    public final static String bank_ICBC = "ICBC";
    public final static String bank_CCB = "CCB";
    public final static String bank_HSBC = "HSBC";
    public final static String bank_BC = "BC";

    public final static String bank_ABC = "ABC";
    public final static String bank_BCM = "BCM";
    public final static String bank_CMB = "CMB";
    public final static String bank_CMSB = "CMSB";
    public final static String bank_SPDB = "SPDB";

    public final static String bank_CITIC = "CITIC";
    public final static String bank_CEB = "CEB";
    public final static String bank_HXB = "HXB";
    public final static String bank_GDB = "GDB";
    public final static String bank_SDB = "SDB";

    public final static String bank_CIB = "CIB";
    public final static String bank_PSBC = "PSBC";
    public final static String bank_CDB = "CDB";
    public final static String bank_EIBC = "EIBC";
    public final static String bank_ADBC = "ADBC";
    public final static String bank_BOB = "BOB";

    public final static String bank_paypal = "paypal";
    public final static String bank_VISA = "VISA";
    public final static String bank_MasterCard = "MasterCard";

    public final static Map<String, String> bankNames = new HashedMap();
    static {
        bankNames.put(bank_alipay, "支付宝");
        bankNames.put(bank_wechat, "微信");
        bankNames.put(bank_unionpay, "银联");
        bankNames.put(bank_ICBC, "中国工商银行");
        bankNames.put(bank_CCB, "中国建设银行");
        bankNames.put(bank_HSBC, "汇丰银行");
        bankNames.put(bank_BC, "中国银行");
        bankNames.put(bank_ABC, "中国农业银行");
        bankNames.put(bank_BCM, "中国交通银行");
        bankNames.put(bank_CMB, "中国招商银行");
        bankNames.put(bank_CMSB, "中国民生银行");
        bankNames.put(bank_SPDB, "上海浦东发展银行");
        bankNames.put(bank_CITIC, "中信银行");
        bankNames.put(bank_CEB, "中国光大银行");
        bankNames.put(bank_HXB, "华夏银行");
        bankNames.put(bank_GDB, "广东发展银行");
        bankNames.put(bank_SDB, "深圳发展银行");
        bankNames.put(bank_CIB, "中国兴业银行");
        bankNames.put(bank_PSBC, "中国邮政储蓄银行");
        bankNames.put(bank_CDB, "国家开发银行");
        bankNames.put(bank_EIBC, "中国进出口银行");
        bankNames.put(bank_ADBC, "中国农业发展银行");
        bankNames.put(bank_BOB, "北京银行");
        bankNames.put(bank_paypal, "贝宝");
        bankNames.put(bank_VISA, "VISA");
        bankNames.put(bank_MasterCard, "MasterCard");
    }

    public static String bankSelectOptions =
            "<option value=\"支付宝\">支付宝</option>" +
            "<option value=\"微信\">微信</option>" +
            "<option value=\"中国工商银行\">中国工商银行</option>" +
            "<option value=\"中国建设银行\">中国建设银行</option>" +
            "<option value=\"中国银行\">中国银行</option>" +
            "<option value=\"中国交通银行\">中国交通银行</option>" +
            "<option value=\"中国招商银行\">中国招商银行</option>" +
            "<option value=\"中国兴业银行\">中国兴业银行</option>" +
            "<option value=\"中国邮政储蓄银行\">中国邮政储蓄银行</option>" +
            "<option value=\"上海浦东发展银行\">上海浦东发展银行</option>" +
            "<option value=\"广东发展银行\">广东发展银行</option>" +
            "<option value=\"中国农业银行\">中国农业银行</option>" +
            "<option value=\"中国民生银行\">中国民生银行</option>" +
            "<option value=\"华夏银行\">华夏银行</option>" +
            "<option value=\"中国光大银行\">中国光大银行</option>" +
            "<option value=\"中信银行\">中信银行</option>" +
            "<option value=\"汇丰银行\">汇丰银行</option>" +
            "<option value=\"深圳发展银行\">深圳发展银行</option>" +
            "<option value=\"北京银行\">北京银行</option>" +
            "<option value=\"银联\">银联</option>" +
            "<option value=\"paypal\">paypal</option>" +
            "<option value=\"VISA\">VISA</option>" +
            "<option value=\"MasterCard\">MasterCard</option>" +
            "<option value=\"国家开发银行\">国家开发银行</option>" +
            "<option value=\"中国进出口银行\">中国进出口银行</option>" +
            "<option value=\"中国农业发展银行\">中国农业发展银行</option>";
}
