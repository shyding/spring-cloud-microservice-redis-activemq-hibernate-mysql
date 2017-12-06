// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProductTools.java

package com.sf.openapi.express.sample.product.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sf.openapi.common.entity.*;
import com.sf.openapi.common.utils.SFOpenClient;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ProductTools
{

    public ProductTools()
    {
    }

    public static MessageResp queryService(String url, AppInfo appInfo, MessageReq req)
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

    public void testBaseService()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/product/basic/query/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("451E99EE4E8ED29545E7E0C0D306ABD4");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(250);
        head.setTransMessageId("201409040916141694");
        req.setHead(head);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = queryService(url, appInfo, req);
        System.out.println(response.getHead().getTransType());
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }

    public void testAdditionService()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/product/additional/query/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("451E99EE4E8ED29545E7E0C0D306ABD4");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(251);
        head.setTransMessageId("201409040916141695");
        req.setHead(head);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = queryService(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }
}
