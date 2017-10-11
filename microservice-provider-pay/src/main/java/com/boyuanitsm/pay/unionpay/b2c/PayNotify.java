package com.boyuanitsm.pay.unionpay.b2c;

/**
 * unionpay 网关支付通知类
 * https://open.unionpay.com/ajweb/product/newProDetail?proId=1&cataId=11
 *
 * 参数名称  参数说明

 respCode           应答码
 respMsg            应答信息
 orderId            原交易的orderId
 queryId            银联流水号
 txnType            原交易的txnType
 txnSubType          一般情况同原交易的txnSubType。当txnType为01时，如果txnSubType为03，表示这笔交易为分期交易。
 settleDate          清算日期。为银联和入网机构间的交易结算日期。一般前一日23点至当天23点为一个清算日。也就是23点前的交易，当天23点之后开始结算，23点之后的交易，要第二天23点之后才会结算。
 settleCurrencyCode  清算币种。境内商户固定返回156。
 settleAmt           清算金额。境内商户同原交易的txnAmt。
 traceTime           交易传输时间。（月月日日时时分分秒秒）24小时制收单机构对账时使用，该域透传了请求上送的txnTime。
 traceNo             系统跟踪号。收单机构对账时使用，该域由银联系统产生。

 */
public class PayNotify {

    private String respCode;
    private String respMsg;
    private String orderId;
    private String queryId;
    private String txnType;
    private String txnSubType;
    private String settleDate;
    private String settleCurrencyCode;
    private Float settleAmt;
    private String traceTime;
    private String traceNo;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getTxnSubType() {
        return txnSubType;
    }

    public void setTxnSubType(String txnSubType) {
        this.txnSubType = txnSubType;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getSettleCurrencyCode() {
        return settleCurrencyCode;
    }

    public void setSettleCurrencyCode(String settleCurrencyCode) {
        this.settleCurrencyCode = settleCurrencyCode;
    }

    public Float getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(Float settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getTraceTime() {
        return traceTime;
    }

    public void setTraceTime(String traceTime) {
        this.traceTime = traceTime;
    }

    public String getTraceNo() {
        return traceNo;
    }

    public void setTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }
}
