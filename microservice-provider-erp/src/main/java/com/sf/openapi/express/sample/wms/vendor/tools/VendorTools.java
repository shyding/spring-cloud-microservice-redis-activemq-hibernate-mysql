// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   VendorTools.java

package com.sf.openapi.express.sample.wms.vendor.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sf.openapi.common.entity.*;
import com.sf.openapi.common.utils.SFOpenClient;
import com.sf.openapi.express.sample.wms.vendor.dto.VendorRequestDto;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

public class VendorTools
{

    public VendorTools()
    {
    }

    public static MessageResp vendor(String url, AppInfo appInfo, MessageReq req)
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

    public void testVendor()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/wms/vendor/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("F6982CA7BB9EF5A44A78CD77A84D9DF6");
        MessageReq req = new MessageReq();
        VendorRequestDto vendorRequest = new VendorRequestDto();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(600);
        head.setTransMessageId("201409040916141688");
        req.setHead(head);
        req.setBody(vendorRequest);
        vendorRequest.setInterfaceActionCode("NEW");
        vendorRequest.setCheckword("01f18980363f40e48416464baf4cc7c0");
        vendorRequest.setCompany("12345678");
        vendorRequest.setVendorName("\u5C0F\u7C73\u79D1\u6280");
        vendorRequest.setVendor("xiaomi");
        vendorRequest.setAttentionTo("\u96F7\u519B");
        vendorRequest.setPhoneNum("13645784512");
        vendorRequest.setPostalCode("518147");
        vendorRequest.setFaxNum("0755-74587426");
        vendorRequest.setEmailAddress("leijun@qq.com");
        vendorRequest.setCountry("\u4E2D\u56FD");
        vendorRequest.setState("\u5E7F\u4E1C\u7701");
        vendorRequest.setCity("\u6DF1\u5733\u5E02");
        vendorRequest.setAddress("\u5357\u5C71\u533A\u79D1\u6280\u56ED\u5C0F\u7C73\u79D1\u6280\u6709\u9650\u516C\u53F8");
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = vendor(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }
}
