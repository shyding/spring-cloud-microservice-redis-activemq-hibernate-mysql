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

package com.boyuanitsm.pay.alipay.bean;


/**
 * 支付宝对商户的请求数据处理完成后，会将处理的结果数据通过系统程序控制客户端页面自动跳转的方式通知给商户网站。这些处理结果数据就是页面跳转同步通知参数。
 *
 * @see <a href="https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.7EWBns&treeId=62&articleId=104743&docType=1#s1">https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.7EWBns&treeId=62&articleId=104743&docType=1#s1</a>
 * @author hookszhang on 7/15/16.
 *
 * 页面跳转同步参数说明
 *
 * 支付宝对商户的请求数据处理完成后，会将处理的结果数据通过系统程序控制客户端页面自动跳转的方式通知给商户网站。这些处理结果数据就是页面跳转同步通知参数。

参数  参数名称  类型（字节长度） 参数说明 是否可为空 样例

is_success 	成功标识 	String(1) 	表示接口调用是否成功，并不表明业务处理结果。 	不可空 	T
sign_type 	签名方式 	String 	DSA、RSA、MD5三个值可选，必须大写。 	不可空 	MD5
sign 	签名 	String(32) 	请参见本文档“附录：签名与验签”。 	不可空 	b1af584504b8e845ebe40b8e0e733729
out_trade_no 	商户网站唯一订单号 	String(64) 	对应商户网站的订单系统中的唯一订单号，非支付宝交易号。需保证在商户网站中的唯一性。是请求时对应的参数，原样返回。 	可空 	6402757654153618
subject 	商品名称 	String(256) 	商品的标题/交易标题/订单标题/订单关键字等。 	可空 	手套
payment_type 	支付类型 	String(4) 	只支持取值为1（商品购买）。 	可空 	1
exterface 	接口名称 	String 	标志调用哪个接口返回的链接。 	可空 	create_direct_pay_by_user
trade_no 	支付宝交易号 	String(64) 	该交易在支付宝系统中的交易流水号。最长64位。 	可空 	2014040311001004370000361525
trade_status 	交易状态 	String

交易目前所处的状态。成功状态的值只有两个：

TRADE_FINISHED（普通即时到账的交易成功状态）；
TRADE_SUCCESS（开通了高级即时到账或机票分销产品后的交易成功状态）

可空 	TRADE_FINISHED
notify_id 	通知校验ID 	String 	支付宝通知校验ID，商户可以用这个流水号询问支付宝该条通知的合法性。 	可空 	RqPnCoPT3K9%2Fvwbh3I%2BODmZS9o4qChHwPWbaS7UMBJpUnBJlzg42y9A8gQlzU6m3fOhG
notify_time 	通知时间 	Date 	通知时间（支付宝时间）。格式为yyyy-MM-dd HH:mm:ss。 	可空 	2008-10-23 13:17:39
notify_type 	通知类型 	String 	返回通知类型。 	可空 	trade_status_sync
seller_email 	卖家支付宝账号 	String(100) 	卖家支付宝账号，可以是Email或手机号码。 	可空 	chao.chenc1@alipay.com
buyer_email 	买家支付宝账号 	String(100) 	买家支付宝账号，可以是Email或手机号码。 	可空 	tst***@alipay.com
seller_id 	卖家支付宝账户号 	String(30) 	卖家支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字。 	可空 	2088002007018916
buyer_id 	买家支付宝账户号 	String(30) 	买家支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字。 	可空 	2088101000082594
total_fee 	交易金额 	Number 	该笔订单的资金总额，单位为RMB-Yuan。取值范围为[0.01,100000000.00]，精确到小数点后两位。 	可空 	10.00
body 	商品描述 	String(1000) 	对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。 	可空 	Hello
extra_common_param 	公用回传参数 	String 	用于商户回传参数，该值不能包含“=”、“&”等特殊字符。如果用户请求时传递了该参数，则返回给商户时会回传该参数。 	可空 	你好，这是测试商户的广告。

http://商户自定义地址/return_url.php?is_success=T&sign=b1af584504b8e845ebe40b8e0e733729&sign_type=MD5&body=Hello&buyer_email=xin***%40163.com&buyer_id=2088101000082594&exterface=create_direct_pay_by_user&out_trade_no=6402757654153618&payment_type=1&seller_email=chao.chenc1%40alipay.com&seller_id=2088002007018916&subject=%E5%A4%96%E9%83%A8FP&total_fee=10.00&trade_no=2014040311001004370000361525&trade_status=TRADE_FINISHED&notify_id=RqPnCoPT3K9%252Fvwbh3I%252BODmZS9o4qChHwPWbaS7UMBJpUnBJlzg42y9A8gQlzU6m3fOhG&notify_time=2008-10-23+13%3A17%3A39&notify_type=trade_status_sync&extra_common_param=%E4%BD%A0%E5%A5%BD%EF%BC%8C%E8%BF%99%E6%98%AF%E6%B5%8B%E8%AF%95%E5%95%86%E6%88%B7%E7%9A%84%E5%B9%BF%E5%91%8A%E3%80%82
 *
 */
public class SyncReturn {

    private String is_success;
    private String sing_type;
    private String sign;
    private String out_trade_no;
    private String subject;
    private String payment_type;
    private String exterface;
    private String trade_no;
    private String trade_status;
    private String notify_id;
    private String notify_time;
    private String notify_type;
    private String seller_email;
    private String buyer_email;
    private String seller_id;
    private String buyer_id;
    private Float total_fee;
    private String body;
    private String extra_common_param;

    public String getIs_success() {
        return is_success;
    }

    public void setIs_success(String is_success) {
        this.is_success = is_success;
    }

    public String getSing_type() {
        return sing_type;
    }

    public void setSing_type(String sing_type) {
        this.sing_type = sing_type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getExterface() {
        return exterface;
    }

    public void setExterface(String exterface) {
        this.exterface = exterface;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getTrade_status() {
        return trade_status;
    }

    public void setTrade_status(String trade_status) {
        this.trade_status = trade_status;
    }

    public String getNotify_id() {
        return notify_id;
    }

    public void setNotify_id(String notify_id) {
        this.notify_id = notify_id;
    }

    public String getNotify_time() {
        return notify_time;
    }

    public void setNotify_time(String notify_time) {
        this.notify_time = notify_time;
    }

    public String getNotify_type() {
        return notify_type;
    }

    public void setNotify_type(String notify_type) {
        this.notify_type = notify_type;
    }

    public String getSeller_email() {
        return seller_email;
    }

    public void setSeller_email(String seller_email) {
        this.seller_email = seller_email;
    }

    public String getBuyer_email() {
        return buyer_email;
    }

    public void setBuyer_email(String buyer_email) {
        this.buyer_email = buyer_email;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    public Float getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(Float total_fee) {
        this.total_fee = total_fee;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getExtra_common_param() {
        return extra_common_param;
    }

    public void setExtra_common_param(String extra_common_param) {
        this.extra_common_param = extra_common_param;
    }

    @Override
    public String toString() {
        return "AyncNotify{" +
                "is_success='" + is_success + '\'' +
                ", sing_type='" + sing_type + '\'' +
                ", sign='" + sign + '\'' +
                ", out_trade_no='" + out_trade_no + '\'' +
                ", subject='" + subject + '\'' +
                ", payment_type='" + payment_type + '\'' +
                ", exterface='" + exterface + '\'' +
                ", trade_no='" + trade_no + '\'' +
                ", trade_status='" + trade_status + '\'' +
                ", notify_id='" + notify_id + '\'' +
                ", notify_time='" + notify_time + '\'' +
                ", notify_type='" + notify_type + '\'' +
                ", seller_email='" + seller_email + '\'' +
                ", buyer_email='" + buyer_email + '\'' +
                ", seller_id='" + seller_id + '\'' +
                ", buyer_id='" + buyer_id + '\'' +
                ", total_fee='" + total_fee + '\'' +
                ", body='" + body + '\'' +
                ", extra_common_param='" + extra_common_param + '\'' +
                '}';
    }
}
