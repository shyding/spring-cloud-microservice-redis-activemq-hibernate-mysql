/*
 * Copyright 2016-2017 Shanghai Boyuan IT Services Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.boyuanitsm.pay.wxpay.protocol.refund_query_protocol;

/**
 * User: rizenguo
 * Date: 2014/10/25
 * Time: 16:36
 *
 * https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_5
 *
 * 字段名 	变量名 	必填 	类型 	示例值 	描述
 返回状态码 	return_code 	是 	String(16) 	SUCCESS 	SUCCESS/FAIL
 返回信息 	return_msg 	否 	String(128) 	签名失败

 返回信息，如非空，为错误原因

 签名失败

 参数格式校验错误

 以下字段在return_code为SUCCESS的时候有返回
 字段名 	变量名 	必填 	类型 	示例值 	描述
 业务结果 	result_code 	是 	String(16) 	SUCCESS

 SUCCESS/FAIL

 SUCCESS退款申请接收成功，结果通过退款查询接口查询

 FAIL
 错误码 	err_code 	是 	String(32) 	SYSTEMERROR 	错误码详见第6节
 错误描述 	err_code_des 	是 	String(128) 	系统错误 	结果信息描述
 公众账号ID 	appid 	是 	String(32) 	wx8888888888888888 	微信分配的公众账号ID（企业号corpid即为此appId）
 商户号 	mch_id 	是 	String(32) 	1900000109 	微信支付分配的商户号
 随机字符串 	nonce_str 	是 	String(32) 	5K8264ILTKCH16CQ2502SI8ZNMTM67VS 	随机字符串，不长于32位
 签名 	sign 	是 	String(32) 	C380BEC2BFD727A4B6845133519F3AD6 	签名，详见签名算法
 微信订单号 	transaction_id 	是 	String(32) 	1217752501201407033233368018 	微信订单号
 商户订单号 	out_trade_no 	是 	String(32) 	1217752501201407033233368018 	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
 订单金额 	total_fee 	是 	Int 	100 	订单总金额，单位为分，只能为整数，详见支付金额
 应结订单金额 	settlement_total_fee 	否 	Int 	100 	当订单使用了免充值型优惠券后返回该参数，应结订单金额=订单金额-免充值优惠券金额。
 货币种类 	fee_type 	否 	String(8) 	CNY 	订单金额货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
 现金支付金额 	cash_fee 	是 	Int 	100 	现金支付金额，单位为分，只能为整数，详见支付金额
 退款笔数 	refund_count 	是 	Int 	1 	退款记录数
 商户退款单号 	out_refund_no_$n 	是 	String(64) 	1217752501201407033233368018 	商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
 微信退款单号 	refund_id_$n 	是 	String(32) 	1217752501201407033233368018 	微信退款单号
 退款渠道 	refund_channel_$n 	否 	String(16) 	ORIGINAL

 ORIGINAL—原路退款

 BALANCE—退回到余额

 OTHER_BALANCE—原账户异常退到其他余额账户

 OTHER_BANKCARD—原银行卡异常退到其他银行卡
 申请退款金额 	refund_fee_$n 	是 	Int 	100 	退款总金额,单位为分,可以做部分退款
 退款金额 	settlement_refund_fee_$n 	否 	Int 	100 	退款金额=申请退款金额-非充值代金券退款金额，退款金额<=申请退款金额
 代金券类型 	coupon_type_$n_$m 	否 	String(8) 	CASH

 CASH--充值代金券

 NO_CASH---非充值优惠券

 开通免充值券功能，并且订单使用了优惠券后有返回（取值：CASH、NO_CASH）。$n为下标,$m为下标,从0开始编号，举例：coupon_type_$0_$1
 总代金券退款金额 	coupon_refund_fee_$n 	否 	Int 	100 	代金券退款金额<=退款金额，退款金额-代金券或立减优惠退款金额为现金，说明详见代金券或立减优惠
 退款代金券使用数量 	coupon_refund_count_$n 	否 	Int 	1 	退款代金券使用数量 ,$n为下标,从0开始编号
 退款代金券ID 	coupon_refund_id_$n_$m 	否 	String(20) 	10000  	退款代金券ID, $n为下标，$m为下标，从0开始编号
 单个代金券退款金额 	coupon_refund_fee_$n_$m 	否 	Int 	100 	单个退款代金券支付金额, $n为下标，$m为下标，从0开始编号
 退款状态 	refund_status_$n 	是 	String(16) 	SUCCESS

 退款状态：

 SUCCESS—退款成功

 REFUNDCLOSE—退款关闭。

 PROCESSING—退款处理中

 CHANGE—退款异常，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，可前往商户平台（pay.weixin.qq.com）-交易中心，手动处理此笔退款。$n为下标，从0开始编号。
 退款资金来源 	refund_account_$n 	否 	String(30) 	REFUND_SOURCE_RECHARGE_FUNDS

 REFUND_SOURCE_RECHARGE_FUNDS---可用余额退款/基本账户

 REFUND_SOURCE_UNSETTLED_FUNDS---未结算资金退款

 $n为下标，从0开始编号。
 退款入账账户 	refund_recv_accout_$n 	是 	String(64) 	招商银行信用卡0403 	取当前退款单的退款入账方

 1）退回银行卡：

 {银行名称}{卡类型}{卡尾号}

 2）退回支付用户零钱:

 支付用户零钱

 3）退还商户:

 商户基本账户

 商户结算银行账户

 4）退回支付用户零钱通:

 支付用户零钱通
 退款成功时间 	refund_success_time_$n 	否 	String(20) 	2016-07-25 15:26:26 	退款成功时间，当退款状态为退款成功时有返回。$n为下标，从0开始编号。
 */
public class RefundQueryResData {
    //协议层
    private String return_code = "";
    private String return_msg = "";

    //协议返回的具体数据（以下字段在return_code 为SUCCESS 的时候有返回）
    private String result_code = "";
    private String err_code = "";
    private String err_code_des = "";
    private String appid = "";
    private String mch_id = "";
    private String nonce_str = "";
    private String sign = "";


    private String device_info = "";
    private String transaction_id = "";
    private String out_trade_no = "";
    private int refund_count = 0;

    //TODO 这里要用对象来装，因为有可能出现多个数据
    private String out_refund_no = "";
    private String refund_id = "";
    private String refund_channel = "";
    private String refund_fee = "";
    private String coupon_refund_fee = "";
    private String refund_status = "";

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getErr_code_des() {
        return err_code_des;
    }

    public void setErr_code_des(String err_code_des) {
        this.err_code_des = err_code_des;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public int getRefund_count() {
        return refund_count;
    }

    public void setRefund_count(int refund_count) {
        this.refund_count = refund_count;
    }

    public String getOut_refund_no() {
        return out_refund_no;
    }

    public void setOut_refund_no(String out_refund_no) {
        this.out_refund_no = out_refund_no;
    }

    public String getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }

    public String getRefund_channel() {
        return refund_channel;
    }

    public void setRefund_channel(String refund_channel) {
        this.refund_channel = refund_channel;
    }

    public String getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(String refund_fee) {
        this.refund_fee = refund_fee;
    }

    public String getCoupon_refund_fee() {
        return coupon_refund_fee;
    }

    public void setCoupon_refund_fee(String coupon_refund_fee) {
        this.coupon_refund_fee = coupon_refund_fee;
    }

    public String getRefund_status() {
        return refund_status;
    }

    public void setRefund_status(String refund_status) {
        this.refund_status = refund_status;
    }
}
