// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PurchaseOrderTools.java

package com.sf.openapi.express.sample.wms.purchaseOrder.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sf.openapi.common.entity.*;
import com.sf.openapi.common.utils.SFOpenClient;
import com.sf.openapi.express.sample.wms.purchaseOrder.dto.*;
import java.io.PrintStream;
import java.util.*;
import org.apache.commons.lang.builder.ToStringBuilder;

public class PurchaseOrderTools
{

    public PurchaseOrderTools()
    {
    }

    public static MessageResp purchaseOrder(String url, AppInfo appInfo, MessageReq req)
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

    public static MessageResp purchaseOrderStatusQuery(String url, AppInfo appInfo, MessageReq req)
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

    public static MessageResp purchaseOrderPush(String url, AppInfo appInfo, MessageReq req)
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

    public void testPurchaseOrder()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/wms/purchase/order/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("F6982CA7BB9EF5A44A78CD77A84D9DF6");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(602);
        head.setTransMessageId("201409040916141688");
        req.setHead(head);
        PurchaseOrderRequestDto purchaseOrderRequest = new PurchaseOrderRequestDto();
        PurchaseHeader header = new PurchaseHeader();
        PurchaseDetail detail = new PurchaseDetail();
        List detailList = new ArrayList();
        purchaseOrderRequest.setCheckword("01f18980363f40e48416464baf4cc7c0");
        header.setCompany("12345678");
        header.setWarehouse("CODE_CK001");
        header.setErpOrderNum("123456789");
        header.setOriginalOrderNo("35345345");
        header.setBuyer("\u5C0F\u732A");
        header.setBuyerPhone("13645784548");
        header.setErpOrderType("\u91C7\u8D2D\u5165\u5E93");
        header.setOrderDate("2014-08-18 15:08:55");
        header.setScheduledReceiptDate("2014-09-18 15:08:55");
        header.setSourceId("467563");
        header.setTransferWarehouse("53FD553");
        header.setNoteToReceiver("\u8D35\u91CD\u5546\u54C1\uFF0C\u5C0F\u5FC3\u8FD0\u9001");
        header.setOtherInboundNote("\u5982\u679C\u4E22\u5931\uFF0C\u62FF\u547D\u8D54\u4ED8");
        detail.setErpOrderLineNum("1");
        detail.setItem("111111111");
        detail.setTotalQty("300");
        detail.setLot("54743432");
        detail.setNote("\u5C0A\u8D35\u5BA2\u6237\uFF0C\u4F18\u5148\u8FD0\u9001");
        purchaseOrderRequest.setHeader(header);
        detailList.add(detail);
        purchaseOrderRequest.setDetailList(detailList);
        req.setBody(purchaseOrderRequest);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = purchaseOrder(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }

    public void testPurchaseOrderStatusQuery()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/wms/purchase/order/status/query/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("CF53DFBFA79D4A44F48FDF246DFAFD0F");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(604);
        head.setTransMessageId("201409040916141688");
        req.setHead(head);
        PurchaseOrderStatusQueryRequestDto purchaseOrderStatusQueryRequest = new PurchaseOrderStatusQueryRequestDto();
        purchaseOrderStatusQueryRequest.setCheckword("01f18980363f40e48416464baf4cc7c0");
        purchaseOrderStatusQueryRequest.setCompany("12345678");
        purchaseOrderStatusQueryRequest.setWarehouse("CODE_CK001");
        purchaseOrderStatusQueryRequest.setOrderid("123456789");
        req.setBody(purchaseOrderStatusQueryRequest);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = purchaseOrderStatusQuery(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }

    public void testPurchaseOrderPush()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/wms/purchase/order/push/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("F6982CA7BB9EF5A44A78CD77A84D9DF6");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(610);
        head.setTransMessageId("201409040916141688");
        req.setHead(head);
        PurchaseOrderPushReqDto purchaseOrderPushReq = new PurchaseOrderPushReqDto();
        purchaseOrderPushReq.setCheckword("01f18980363f40e48416464baf4cc7c0");
        purchaseOrderPushReq.setCompany("12345678");
        purchaseOrderPushReq.setErpOrderNum("123456789");
        req.setBody(purchaseOrderPushReq);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = purchaseOrderPush(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }
}
