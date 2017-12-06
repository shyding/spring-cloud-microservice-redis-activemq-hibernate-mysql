// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InventoryQueryTools.java

package com.sf.openapi.express.sample.wms.inventory.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sf.openapi.common.entity.*;
import com.sf.openapi.common.utils.SFOpenClient;
import com.sf.openapi.express.sample.wms.inventory.dto.InventoryBalancePageQueryRequestDto;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

public class InventoryQueryTools
{

    public InventoryQueryTools()
    {
    }

    public static MessageResp inventoryQuery(String url, AppInfo appInfo, MessageReq req)
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

    public void testInventoryQuery()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/wms/inventory/query/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("EBFF34171A718FC277F2B95EEA670AFB");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(607);
        head.setTransMessageId("201409040916141954");
        req.setHead(head);
        InventoryBalancePageQueryRequestDto inventoryBalancePageQueryRequest = new InventoryBalancePageQueryRequestDto();
        inventoryBalancePageQueryRequest.setCheckword("01f18980363f40e48416464baf4cc7c0");
        inventoryBalancePageQueryRequest.setCompany("12345678");
        inventoryBalancePageQueryRequest.setWarehouse("CODE_CK001");
        inventoryBalancePageQueryRequest.setItem("111111111");
        inventoryBalancePageQueryRequest.setPage_index("1");
        inventoryBalancePageQueryRequest.setInventory_sts("10");
        req.setBody(inventoryBalancePageQueryRequest);
        System.out.println(ToStringBuilder.reflectionToString((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(req).toString()));
        MessageResp response = inventoryQuery(url, appInfo, req);
        System.out.println(ToStringBuilder.reflectionToString((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(response).toString()));
        System.exit(0);
    }
}
