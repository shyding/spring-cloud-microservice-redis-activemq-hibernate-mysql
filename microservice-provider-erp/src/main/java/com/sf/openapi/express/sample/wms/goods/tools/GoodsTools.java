// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GoodsTools.java

package com.sf.openapi.express.sample.wms.goods.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sf.openapi.common.entity.*;
import com.sf.openapi.common.utils.SFOpenClient;
import com.sf.openapi.express.sample.wms.goods.dto.Item;
import com.sf.openapi.express.sample.wms.goods.dto.ItemRequestDto;
import java.io.PrintStream;
import java.util.*;
import org.apache.commons.lang.builder.ToStringBuilder;

public class GoodsTools
{

    public GoodsTools()
    {
    }

    public static MessageResp goods(String url, AppInfo appInfo, MessageReq req)
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

    public void testGoods()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/wms/goods/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("EBFF34171A718FC277F2B95EEA670AFB");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(601);
        head.setTransMessageId("201409040916141688");
        req.setHead(head);
        ItemRequestDto itemRequest = new ItemRequestDto();
        List itemList = new ArrayList();
        Item item = new Item();
        itemRequest.setCheckword("01f18980363f40e48416464baf4cc7c0");
        itemRequest.setInterfaceActionCode("NEW");
        itemRequest.setCompany("12345678");
        item.setItem("777777777");
        item.setDescription("iphone5s");
        item.setItemClass("\u7535\u5B50\u7C7B");
        item.setItemCategory1("\u624B\u673A");
        item.setLongDescription("IOS2014");
        item.setItemStyle("100");
        item.setItemColor("\u91D1\u8272");
        item.setItemSize("123.8*58.6*7.6");
        item.setStorageTemplate("5s\u6A21\u677F");
        item.setLotTemplate("\u82F9\u679C\u624B\u673A\u6A21\u677F");
        item.setxRefItem1("53DG534543");
        item.setxRefItem2("FSDFG4535G");
        item.setxRefItem3("3453DF565S");
        item.setxRefItem4("FGSG543535");
        item.setLength1("123.8");
        item.setWidth1("58.6");
        item.setHeight1("7.6");
        item.setDimensionUm1("CM");
        item.setWeight1("112");
        item.setWeightUm1("KG");
        item.setQuantityUm1("\u90E8");
        item.setTreatAsLoose1("Y");
        item.setLotControlled("Y");
        item.setBomAction("N");
        item.setSerialNumTrackInbound("Y");
        item.setSerialNumTrackOutbound("Y");
        item.setItemCategory3("Y");
        item.setDepartment("\u82F9\u679C\u624B\u673A\uFF0C\u624B\u673A\u4E2D\u7684\u6218\u6597\u673A");
        item.setDivision("\u5B8C\u5168\u7B26\u5408\u56FD\u9645\u89C4\u683C");
        itemList.add(item);
        itemRequest.setItemlist(itemList);
        req.setBody(itemRequest);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = goods(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }
}
