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
 * 支付宝对商户的请求数据处理完成后，会将处理的结果数据通过服务器主动通知的方式通知给商户网站。这些处理结果数据就是服务器异步通知参数。
 *
 * @see <a href="https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.7EWBns&treeId=62&articleId=104743&docType=1#s3">https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.7EWBns&treeId=62&articleId=104743&docType=1#s3</a>
 * @author hookszhang on 7/15/16.
 *
 * 服务器异步通知参数说明

支付宝对商户的请求数据处理完成后，会将处理的结果数据通过服务器主动通知的方式通知给商户网站。这些处理结果数据就是服务器异步通知参数。

参数  参数名称  类型（字节长度）  参数说明  是否可为空  样例

基本参数

notify_time   通知时间   Date   通知的发送时间。  格式为yyyy-MM-dd HH:mm:ss。   不可空   2009-08-12 11:08:32
notify_type   通知类型   String   通知的类型。   不可空   trade_status_sync
notify_id   通知校验ID   String   通知校验ID。   不可空   70fec0c2730b27528665af4517c27b95
sign_type   签名方式   String   DSA、RSA、MD5三个值可选，必须大写。   不可空   DSA
sign   签名   String   请参见本文档“附录：签名与验签”。   不可空   _p_w_l_h_j0b_gd_aejia7n_ko4_m%2Fu_w_jd3_nx_s_k_mxus9_hoxg_y_r_lunli_pmma29_t_q%3D%  3D  业务参数
out_trade_no   商户网站唯一订单号   String(64)   对应商户网站的订单系统中的唯一订单号，非支付宝交易号。  需保证在商户网站中的唯一性。是请求时对应的参数，原样返回。   可空   3618810634349901
subject   商品名称   String(256)   商品的标题/交易标题/订单标题/订单关键字等。  它在支付宝的交易明细中排在第一列，对于财务对账尤为重要。是请求时对应的参数，原样通知回来。   可空   phone手机  payment_type   支付类型   String(4)   只支持取值为1（商品购买）。   可空   1
trade_no   支付宝交易号   String(64)   该交易在支付宝系统中的交易流水号。最长64位。   可空   2014040311001004370000361525
trade_status   交易状态   String   取值范围请参见交易状态。   可空   TRADE_FINISHED
gmt_create   交易创建时间   Date   该笔交易创建的时间。  格式为yyyy-MM-dd HH:mm:ss。   可空   2008-10-22 20:49:31
gmt_payment   交易付款时间   Date   该笔交易的买家付款时间。  格式为yyyy-MM-dd HH:mm:ss。   可空   2008-10-22 20:49:50
gmt_close   交易关闭时间   Date   交易关闭时间。  格式为yyyy-MM-dd HH:mm:ss。   可空   2008-10-22 20:49:46
refund_status   退款状态   String   取值范围请参见退款状态。   可空   REFUND_SUCCESS
gmt_refund   退款时间   Date   卖家退款的时间，退款通知时会发送。  格式为yyyy-MM-dd HH:mm:ss。   可空   2008-10-29 19:38:25
seller_email   卖家支付宝账号   String(100)   卖家支付宝账号，可以是email和手机号码。   可空   chao.chenc1@alipay.com
buyer_email   买家支付宝账号   String(100)   买家支付宝账号，可以是Email或手机号码。   可空   137******70
seller_id   卖家支付宝账户号   String(30)   卖家支付宝账号对应的支付宝唯一用户号。  以2088开头的纯16位数字。   可空   2088002007018916
buyer_id   买家支付宝账户号   String(30)   买家支付宝账号对应的支付宝唯一用户号。  以2088开头的纯16位数字。   可空   2088002007013600
price   商品单价   Number   如果请求时使用的是total_fee，那么price等于total_fee；如果请求时使用的是price，那么对应请求时的price参数，原样通知回来。   可空   10.00
total_fee   交易金额   Number   该笔订单的总金额。  请求时对应的参数，原样通知回来。   可空   10.00
quantity   购买数量   Number   如果请求时使用的是total_fee，那么quantity等于1；如果请求时使用的是quantity，那么对应请求时的quantity参数，原样通知回来。   可空   1
body   商品描述   String(1000)   该笔订单的备注、描述、明细等。  对应请求时的body参数，原样通知回来。   可空   Hello
discount   折扣   Number   支付宝系统会把discount的值加到交易金额上，如果需要折扣，本参数为负数。   可空   -5
is_total_fee_adjust   是否调整总价   String(1)   该交易是否调整过价格。   可空   N
use_coupon   是否使用红包买家   String(1)   是否在交易过程中使用了红包。   可空   N
extra_common_param   公用回传参数   String   用于商户回传参数，该值不能包含“=”、“&”等特殊字符。  如果用户请求时传递了该参数，则返回给商户时会回传该参数。   可空   你好，这是测试商户的广告。

http://商户自定义地址/notify_url.php?trade_no=2014040311001004370000361525&out_trade_no=3618810634349901&discount=-5&payment_type=1&subject=iphone%E6%89%8B%E6%9C%BA&body=Hello&price=10.00&quantity=1&total_fee=10.00&trade_status=TRADE_FINISHED&refund_status=REFUND_SUCCESS&seller_email=chao.chenc1%40alipay.com&seller_id=2088002007018916&buyer_id=2088002007013600&buyer_email=137******70&gmt_create=2008-10-22+20%3A49%3A31&is_total_fee_adjust=N&gmt_payment=2008-10-22+20%3A49%3A50&gmt_close=2008-10-22+20%3A49%3A46&gmt_refund=2008-10-29+19%3A38%3A25&use_coupon=N&notify_time=2009-08-12+11%3A08%3A32&notify_type=trade_status_sync&notify_id=70fec0c2730b27528665af4517c27b95&sign_type=DSA&sign=_p_w_l_h_j0b_gd_aejia7n_ko4_m%252Fu_w_jd3_nx_s_k_mxus9_hoxg_y_r_lunli_pmma29_t_q%253D%253D&extra_common_param=%E4%BD%A0%E5%A5%BD%EF%BC%8C%E8%BF%99%E6%98%AF%E6%B5%8B%E8%AF%95%E5%95%86%E6%88%B7%E7%9A%84%E5%B9%BF%E5%91%8A%E3%80%82
 *
 */
public class AyncNotify {

    private String notify_time;
    private String notify_type;
    private String notify_id;
    private String sign_type;
    private String sign;
    private String out_trade_no;
    private String subject;
    private String payment_type;
    private String trade_no;
    private String trade_status;
    private String gmt_create;
    private String gmt_payment;
    private String gmt_close;
    private String refund_status;
    private String gmt_refund;
    private String seller_email;
    private String buyer_email;
    private String seller_id;
    private String buyer_id;
    private Float price;
    private Float total_fee;
    private Float quantity;
    private String body;
    private Float discount;
    private String is_total_fee_adjust;
    private String use_coupon;
    private String extra_common_param;
    private String business_scene;

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

    public String getNotify_id() {
        return notify_id;
    }

    public void setNotify_id(String notify_id) {
        this.notify_id = notify_id;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
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

    public String getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(String gmt_create) {
        this.gmt_create = gmt_create;
    }

    public String getGmt_payment() {
        return gmt_payment;
    }

    public void setGmt_payment(String gmt_payment) {
        this.gmt_payment = gmt_payment;
    }

    public String getGmt_close() {
        return gmt_close;
    }

    public void setGmt_close(String gmt_close) {
        this.gmt_close = gmt_close;
    }

    public String getRefund_status() {
        return refund_status;
    }

    public void setRefund_status(String refund_status) {
        this.refund_status = refund_status;
    }

    public String getGmt_refund() {
        return gmt_refund;
    }

    public void setGmt_refund(String gmt_refund) {
        this.gmt_refund = gmt_refund;
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

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(Float total_fee) {
        this.total_fee = total_fee;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getIs_total_fee_adjust() {
        return is_total_fee_adjust;
    }

    public void setIs_total_fee_adjust(String is_total_fee_adjust) {
        this.is_total_fee_adjust = is_total_fee_adjust;
    }

    public String getUse_coupon() {
        return use_coupon;
    }

    public void setUse_coupon(String use_coupon) {
        this.use_coupon = use_coupon;
    }

    public String getExtra_common_param() {
        return extra_common_param;
    }

    public void setExtra_common_param(String extra_common_param) {
        this.extra_common_param = extra_common_param;
    }

    public String getBusiness_scene() {
        return business_scene;
    }

    public void setBusiness_scene(String business_scene) {
        this.business_scene = business_scene;
    }

    @Override
    public String toString() {
        return "SyncReturn{" +
                "notify_time='" + notify_time + '\'' +
                ", notify_type='" + notify_type + '\'' +
                ", notify_id='" + notify_id + '\'' +
                ", sign_type='" + sign_type + '\'' +
                ", sign='" + sign + '\'' +
                ", out_trade_no='" + out_trade_no + '\'' +
                ", subject='" + subject + '\'' +
                ", payment_type='" + payment_type + '\'' +
                ", trade_no='" + trade_no + '\'' +
                ", trade_status='" + trade_status + '\'' +
                ", gmt_create='" + gmt_create + '\'' +
                ", gmt_payment='" + gmt_payment + '\'' +
                ", gmt_close='" + gmt_close + '\'' +
                ", refund_status='" + refund_status + '\'' +
                ", gmt_refund='" + gmt_refund + '\'' +
                ", seller_email='" + seller_email + '\'' +
                ", buyer_email='" + buyer_email + '\'' +
                ", seller_id='" + seller_id + '\'' +
                ", buyer_id='" + buyer_id + '\'' +
                ", price='" + price + '\'' +
                ", total_fee='" + total_fee + '\'' +
                ", quantity='" + quantity + '\'' +
                ", body='" + body + '\'' +
                ", discount='" + discount + '\'' +
                ", is_total_fee_adjust='" + is_total_fee_adjust + '\'' +
                ", use_coupon='" + use_coupon + '\'' +
                ", extra_common_param='" + extra_common_param + '\'' +
                ", business_scene='" + business_scene + '\'' +
                '}';
    }
}
