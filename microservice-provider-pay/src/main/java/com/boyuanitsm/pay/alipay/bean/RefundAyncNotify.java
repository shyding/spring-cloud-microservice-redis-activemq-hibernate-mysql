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
 * https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.6NR2Ed&treeId=62&articleId=104744&docType=1#s2
 * @author hookszhang on 7/15/16.
 *
 * 支付宝退款服务器异步通知
 *
 * 参数说明
 *
 * 参数  参数名称  类型（长度范围） 参数说明  是否可为空  样例
 *
 * notify_time   通知时间   Date   通知发送的时间。  格式为：yyyy-MM-dd HH:mm:ss。   不可空   2009-08-12 11:08:32
 * notify_type   通知类型   String   通知的类型。   不可空   batch_refund_notify
 * notify_id   通知校验ID   String   通知校验ID。   不可空   70fec0c2730b27528665af4517c27b95
 * sign_type   签名方式   String   DSA、RSA、MD5三个值可选，必须大写。   不可空   MD5
 * sign   签名   String   请参见本文档“附录：签名与验签”。   不可空   b7baf9af3c91b37bef4261849aa76281  业务参数
 * batch_no   退款批次号   String   原请求退款批次号。   不可空   20060702001
 * success_num   退款成功总数   String   退交易成功的笔数。  0<= success_num<= 总退款笔数。   不可空   2
 *
 * result_details   退款结果明细   String   退款结果明细。  退手续费结果返回格式：交易号^退款金额^处理结果$退费账号^退费账户ID^退费金额^处理结果；
 * 不退手续费结果返回格式：交易号^退款金额^处理结果。  若退款申请提交成功，处理结果会返回“SUCCESS”。  若提交失败，退款的处理结果中会有报错码，
 * 参见业务错误码。   不可空   2014040311001004370000361525^80^SUCCESS$jax_chuanhang@alipay.com^2088101003147483^0.01^SUCCESS 1
 *
 * http://商户自定义地址/alipay/notify_url.php?notify_time=2009-08-12+11%3A08%3A32&notify_type=batch_refund_notify&notify_id=70fec0c2730b27528665af4517c27b95&
 * sign_type=MD5&sign=_p_w_l_h_j0b_gd_aejia7n_ko4_m%252Fu_w_jd3_nx_s_k_mxus9_hoxg_y_r_lunli_pmma29_t_q%3D%3D&batch_no=20060702001&success_num=2&
 * result_details=2014040311001004370000361525%5E80%5ESUCCESS
 *
 */

public class RefundAyncNotify {

    private String notify_time;
    private String notify_type;
    private String notify_id;
    private String sign_type;
    private String sign;
    private String batch_no;
    private String success_num;
    private String result_details;

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

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public String getSuccess_num() {
        return success_num;
    }

    public void setSuccess_num(String success_num) {
        this.success_num = success_num;
    }

    public String getResult_details() {
        return result_details;
    }

    public void setResult_details(String result_details) {
        this.result_details = result_details;
    }


}
