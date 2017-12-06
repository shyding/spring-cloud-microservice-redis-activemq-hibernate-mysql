// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SecurityTools.java

package com.sf.openapi.express.sample.security.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sf.openapi.common.entity.*;
import com.sf.openapi.common.utils.SFOpenClient;
import java.util.HashMap;
import java.util.Map;

import com.sf.openapi.express.sample.security.dto.TokenRespDto;
import org.apache.commons.lang.builder.ToStringBuilder;

public class SecurityTools
{

    public SecurityTools()
    {
    }

    public static MessageResp queryAccessToken(String url, AppInfo appInfo, MessageReq accessTokenQueryReq)
        throws Exception
    {
        Map paramMap = new HashMap();
        paramMap.put("sf_appid", appInfo.getAppId());
        paramMap.put("sf_appkey", appInfo.getAppKey());
        SFOpenClient client = SFOpenClient.getInstance();
        return (MessageResp)client.doPost(url, accessTokenQueryReq, new TypeReference<MessageResp<TokenRespDto>>() {}, paramMap);
    }

    public static MessageResp applyAccessToken(String url, AppInfo appInfo, MessageReq accessTokenReq)
        throws Exception
    {
        Map paramMap = new HashMap();
        paramMap.put("sf_appid", appInfo.getAppId());
        paramMap.put("sf_appkey", appInfo.getAppKey());
        SFOpenClient client = SFOpenClient.getInstance();
        return (MessageResp)client.doPost(url, accessTokenReq, new TypeReference<MessageResp<TokenRespDto>>() {}, paramMap);
    }

    public static MessageResp refreshAccessToken(String url, AppInfo appInfo, MessageReq accessTokenReq)
        throws Exception
    {
        Map paramMap = new HashMap();
        paramMap.put("sf_appid", appInfo.getAppId());
        paramMap.put("sf_appkey", appInfo.getAppKey());
        paramMap.put("access_token", appInfo.getAccessToken());
        paramMap.put("refresh_token", appInfo.getRefreshToken());
        SFOpenClient client = SFOpenClient.getInstance();
        return (MessageResp)client.doPost(url, accessTokenReq, new TypeReference<MessageResp<TokenRespDto>>() {}, paramMap);
    }

    public static void testAccessTokenQuery()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/public/v1.0/security/access_token/query/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("913CC24527FB502CA7D18C7D2A90AC1D");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(300);
        head.setTransMessageId("201409040916141694");
        req.setHead(head);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = queryAccessToken(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }
}
