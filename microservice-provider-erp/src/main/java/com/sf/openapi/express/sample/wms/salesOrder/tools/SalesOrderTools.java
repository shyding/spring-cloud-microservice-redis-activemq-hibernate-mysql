// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SalesOrderTools.java

package com.sf.openapi.express.sample.wms.salesOrder.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sf.openapi.common.entity.*;
import com.sf.openapi.common.utils.SFOpenClient;
import com.sf.openapi.express.sample.wms.salesOrder.dto.*;
import java.io.PrintStream;
import java.util.*;
import org.apache.commons.lang.builder.ToStringBuilder;

public class SalesOrderTools
{

    public SalesOrderTools()
    {
    }

    public static MessageResp salesOrder(String url, AppInfo appInfo, MessageReq req)
        throws Exception
    {
        Map paramMap = new HashMap();
        paramMap.put("sf_appid", appInfo.getAppId());
        paramMap.put("sf_appkey", appInfo.getAppKey());
        paramMap.put("access_token", appInfo.getAccessToken());
        SFOpenClient client = SFOpenClient.getInstance();
        return (MessageResp)client.doPost(url, req, new TypeReference() {

        }
, paramMap);
    }

    public static MessageResp salesOrderStatusQuery(String url, AppInfo appInfo, MessageReq req)
        throws Exception
    {
        Map paramMap = new HashMap();
        paramMap.put("sf_appid", appInfo.getAppId());
        paramMap.put("sf_appkey", appInfo.getAppKey());
        paramMap.put("access_token", appInfo.getAccessToken());
        SFOpenClient client = SFOpenClient.getInstance();
        return (MessageResp)client.doPost(url, req, new TypeReference() {

        }
, paramMap);
    }

    public static MessageResp salesOrderQuery(String url, AppInfo appInfo, MessageReq req)
        throws Exception
    {
        Map paramMap = new HashMap();
        paramMap.put("sf_appid", appInfo.getAppId());
        paramMap.put("sf_appkey", appInfo.getAppKey());
        paramMap.put("access_token", appInfo.getAccessToken());
        SFOpenClient client = SFOpenClient.getInstance();
        return (MessageResp)client.doPost(url, req, new TypeReference() {

        }
, paramMap);
    }

    public static MessageResp salesOrderCancel(String url, AppInfo appInfo, MessageReq req)
        throws Exception
    {
        Map paramMap = new HashMap();
        paramMap.put("sf_appid", appInfo.getAppId());
        paramMap.put("sf_appkey", appInfo.getAppKey());
        paramMap.put("access_token", appInfo.getAccessToken());
        SFOpenClient client = SFOpenClient.getInstance();
        return (MessageResp)client.doPost(url, req, new TypeReference() {

        }
, paramMap);
    }

    public static MessageResp salesOrderPush(String url, AppInfo appInfo, MessageReq req)
        throws Exception
    {
        Map paramMap = new HashMap();
        paramMap.put("sf_appid", appInfo.getAppId());
        paramMap.put("sf_appkey", appInfo.getAppKey());
        paramMap.put("access_token", appInfo.getAccessToken());
        SFOpenClient client = SFOpenClient.getInstance();
        return (MessageResp)client.doPost(url, req, new TypeReference() {

        }
, paramMap);
    }

    public static MessageResp salesOrderInvoice(String url, AppInfo appInfo, MessageReq req)
        throws Exception
    {
        Map paramMap = new HashMap();
        paramMap.put("sf_appid", appInfo.getAppId());
        paramMap.put("sf_appkey", appInfo.getAppKey());
        paramMap.put("access_token", appInfo.getAccessToken());
        SFOpenClient client = SFOpenClient.getInstance();
        return (MessageResp)client.doPost(url, req, new TypeReference() {

        }
, paramMap);
    }

    public void testSalesOrder()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/wms/sales/order/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("EBFF34171A718FC277F2B95EEA670AFB");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(603);
        head.setTransMessageId("201409040916141689");
        req.setHead(head);
        SailOrderRequestDto sailOrderRequest = new SailOrderRequestDto();
        SailOrderHeader header = new SailOrderHeader();
        SailOrderDetail detail = new SailOrderDetail();
        List detailList = new ArrayList();
        sailOrderRequest.setCheckword("01f18980363f40e48416464baf4cc7c0");
        header.setCompany("12345678");
        header.setWarehouse("CODE_CK001");
        header.setErpOrder("987654321");
        header.setShopName("\u82F9\u679C\u624B\u673A\u4E13\u5356\u5E97");
        header.setOrderType("\u9500\u552E\u8BA2\u5355");
        header.setOrderDate("2014-08-18 15:08:55");
        header.setFromFlag("Y");
        header.setShipToName("\u534E\u4E3A\u6280\u672F\u6709\u9650\u516C\u53F8");
        header.setShipToAttentionTo("\u4EFB\u6B63\u975E");
        header.setShipToPhoneNum("13645124578");
        header.setShipToTelNum("0755-84512578");
        header.setShipToEmailAddress("543532532@qq.com");
        header.setShipToPostalCode("784512");
        header.setShipToCountry("\u4E2D\u56FD");
        header.setShipToProvince("\u5E7F\u4E1C\u7701");
        header.setShipToCity("\u6DF1\u5733\u5E02");
        header.setShipToArea("\u9F99\u5C97\u533A");
        header.setShipToAddress("\u5742\u7530\u8857\u9053\u534E\u4E3A\u57FA\u5730A\u533A12\u680B");
        header.setShipFromName("\u767E\u5EA6\u79D1\u6280\u6709\u9650\u516C\u53F8");
        header.setShipFromAttentionTo("\u674E\u5F66\u5B8F");
        header.setShipFromPhoneNum("13978451254");
        header.setShipFromTelNum("010-78451254");
        header.setShipFromEmailAddress("64543325423@qq.com");
        header.setShipFromPostalCode("854879");
        header.setShipFromCountry("\u4E2D\u56FD");
        header.setShipFromProvince("\u5317\u4EAC\u5E02");
        header.setShipFromCity("\u5317\u4EAC\u5E02");
        header.setShipFromArea("\u6D77\u6DC0\u533A");
        header.setShipFromAddress("\u4E0A\u5730\u5341\u885710\u53F7\u767E\u5EA6\u5927\u53A6");
        header.setCarrier("\u987A\u4E30\u901F\u8FD0");
        header.setCarrierService("\u6807\u51C6\u5FEB\u9012");
        header.setRouteNumbers("");
        header.setPackingNote("\u91C7\u7528\u793C\u54C1\u5305\u88C5");
        header.setCompleteDelivery("Y");
        header.setFreight("500");
        header.setPaymentOfCharge("\u7B2C\u4E09\u65B9\u4ED8");
        header.setPaymentDistrict("\u6DF1\u5733\u5E02");
        header.setDeliveryDate("2014-08-28 15:43:07");
        header.setDeliveryRequested("\u4EC5\u9650\u767D\u5929\u914D\u9001");
        header.setSelfPickup("Y");
        header.setReturnReceiptService("N");
        header.setCod("Y");
        header.setAmount("200");
        header.setValueInsured("Y");
        header.setDeclaredValue("400");
        header.setOrderNote("\u4E09\u65E5\u5185\u4E0D\u80FD\u9001\u8FBE\u81EA\u52A8\u9000\u8D27");
        header.setCompanyNote("\u4F18\u8D28\u5546\u5BB6");
        header.setPriority("1");
        header.setOrderTotalAmount("85124");
        header.setOrderDiscount("544");
        header.setBalanceAmount("35");
        header.setCouponsAmount("86");
        header.setGiftCardAmount("45");
        header.setOtherCharge("788");
        header.setActualAmount("642");
        header.setCustomerPaymentMethod("\u652F\u4ED8\u5B9D");
        header.setMonthlyAccount("353535345");
        header.setIsAppointDelivery("Y");
        header.setAppointDeliveryStatus("2");
        header.setAppointDeliveryRemark("\u5907\u6CE8\u4E2A\u6BDB");
        detail.setErpOrderLineNum("1");
        detail.setItem("999999999");
        detail.setItemName("\u5C0F\u732A");
        detail.setLot("FD43S6F45GFG43");
        detail.setQty("100");
        detail.setUom("\u53EA");
        detail.setItemPrice("54545");
        detail.setItemDiscount("415");
        detail.setBomAction("N");
        sailOrderRequest.setHeader(header);
        detailList.add(detail);
        sailOrderRequest.setDetailList(detailList);
        req.setBody(sailOrderRequest);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = salesOrder(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }

    public void testSalesOrderStatusQuery()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/wms/sales/order/status/query/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("EBFF34171A718FC277F2B95EEA670AFB");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(606);
        head.setTransMessageId("201409040916141743");
        req.setHead(head);
        SailOrderStatusQueryRequestDto sailOrderStatusQueryRequest = new SailOrderStatusQueryRequestDto();
        sailOrderStatusQueryRequest.setCheckword("01f18980363f40e48416464baf4cc7c0");
        sailOrderStatusQueryRequest.setCompany("12345678");
        sailOrderStatusQueryRequest.setOrderid("987654321");
        sailOrderStatusQueryRequest.setWaybillno("");
        req.setBody(sailOrderStatusQueryRequest);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = salesOrderStatusQuery(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }

    public void testSalesOrderQuery()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/wms/sales/order/query/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("EBFF34171A718FC277F2B95EEA670AFB");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(605);
        head.setTransMessageId("201409040916141158");
        req.setHead(head);
        SailOrderQueryRequestDto sailOrderQueryRequest = new SailOrderQueryRequestDto();
        sailOrderQueryRequest.setCheckword("01f18980363f40e48416464baf4cc7c0");
        sailOrderQueryRequest.setCompany("12345678");
        sailOrderQueryRequest.setWarehouse("CODE_CK001");
        sailOrderQueryRequest.setOrderid("987654321");
        sailOrderQueryRequest.setItem("999999999");
        req.setBody(sailOrderQueryRequest);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = salesOrderQuery(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }

    public void testSalesOrderCancel()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/wms/sales/order/cancel/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("EBFF34171A718FC277F2B95EEA670AFB");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(608);
        head.setTransMessageId("201409040916141845");
        req.setHead(head);
        SailOrderCancelReqDto sailOrderCancelReq = new SailOrderCancelReqDto();
        sailOrderCancelReq.setCheckword("01f18980363f40e48416464baf4cc7c0");
        sailOrderCancelReq.setCompany("12345678");
        sailOrderCancelReq.setOrderid("987654321");
        req.setBody(sailOrderCancelReq);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = salesOrderCancel(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }

    public void testSalesOrderPush()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/wms/sales/order/push/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("94F3F23DBB19059623F5C6A29F91EEBF");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(609);
        head.setTransMessageId("201409040916141688");
        req.setHead(head);
        SailOrderPushReqDto sailOrderPushReq = new SailOrderPushReqDto();
        sailOrderPushReq.setCheckword("01f18980363f40e48416464baf4cc7c0");
        sailOrderPushReq.setCompany("12345678");
        sailOrderPushReq.setWarehouse("CODE_CK001");
        sailOrderPushReq.setErpOrder("987654321");
        sailOrderPushReq.setSalesOrderType("1");
        req.setBody(sailOrderPushReq);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = salesOrderPush(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }

    public void testSalesOrderInvoice()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/wms/sales/order/invoice/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("CE7177113185A2E77D34C8F8597CD8EB");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(609);
        head.setTransMessageId("201409040916141688");
        req.setHead(head);
        SailOrderInvoiceReqDto sailOrderInvoiceReq = new SailOrderInvoiceReqDto();
        List invoiceList = new ArrayList();
        SailOrderInvoice sailOrderInvoice = new SailOrderInvoice();
        List detailList = new ArrayList();
        SailOrderInvoiceDetail detail = new SailOrderInvoiceDetail();
        SailOrderInvoiceHeader header = new SailOrderInvoiceHeader();
        sailOrderInvoiceReq.setCheckword("01f18980363f40e48416464baf4cc7c0");
        sailOrderInvoiceReq.setCompany("12345678");
        sailOrderInvoiceReq.setErpOrder("987654321");
        header.setSequence("1");
        header.setNo("2");
        header.setType("\u589E\u503C\u7A0E\u53D1\u7968");
        header.setTitle("\u6682\u65E0\u6807\u9898");
        header.setContent("\u6682\u65E0\u5185\u5BB9");
        header.setTotalAmount("9000");
        header.setReceiveAddr("\u987A\u4E30\u901F\u8FD0");
        header.setReceivePerson("\u5F20\u4E09");
        header.setReceiveMobile("13645784595");
        detail.setSequence("1");
        detail.setProjectName("OPENAPI");
        detail.setSpecification("\u6682\u65E0\u89C4\u683C");
        detail.setUnit("\u53EA");
        detail.setQuantity("100");
        detail.setPrice("200");
        detail.setTotalAmount("300");
        detail.setTaxRate("20%");
        detail.setTaxAmount("600");
        sailOrderInvoice.setHeader(header);
        detailList.add(detail);
        sailOrderInvoice.setDetailList(detailList);
        invoiceList.add(sailOrderInvoice);
        sailOrderInvoiceReq.setInvoiceList(invoiceList);
        req.setBody(sailOrderInvoiceReq);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = salesOrderInvoice(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }
}
