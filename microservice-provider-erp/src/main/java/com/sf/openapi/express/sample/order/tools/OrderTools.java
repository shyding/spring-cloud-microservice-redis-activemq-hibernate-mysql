// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OrderTools.java

package com.sf.openapi.express.sample.order.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sf.openapi.common.entity.*;
import com.sf.openapi.common.utils.SFOpenClient;
import com.sf.openapi.express.sample.order.dto.*;
import java.math.BigDecimal;
import java.util.*;
import org.apache.commons.lang.builder.ToStringBuilder;

public class OrderTools
{

    public OrderTools()
    {
    }

    public static MessageResp order(String url, AppInfo appInfo, MessageReq req)
        throws Exception
    {
        Map paramMap = new HashMap();
        paramMap.put("sf_appid", appInfo.getAppId());
        paramMap.put("sf_appkey", appInfo.getAppKey());
        paramMap.put("access_token", appInfo.getAccessToken());
        SFOpenClient client = SFOpenClient.getInstance();
        return (MessageResp)client.doPost(url, req, new TypeReference<MessageResp<OrderRespDto>>() {}, paramMap);
    }

    public static MessageResp orderQuery(String url, AppInfo appInfo, MessageReq req)
        throws Exception
    {
        Map paramMap = new HashMap();
        paramMap.put("sf_appid", appInfo.getAppId());
        paramMap.put("sf_appkey", appInfo.getAppKey());
        paramMap.put("access_token", appInfo.getAccessToken());
        SFOpenClient client = SFOpenClient.getInstance();
        return (MessageResp)client.doPost(url, req, new TypeReference<MessageResp<OrderQueryRespDto>>() {}, paramMap);
    }

    public static MessageResp filterOrder(String url, AppInfo appInfo, MessageReq req)
        throws Exception
    {
        Map paramMap = new HashMap();
        paramMap.put("sf_appid", appInfo.getAppId());
        paramMap.put("sf_appkey", appInfo.getAppKey());
        paramMap.put("access_token", appInfo.getAccessToken());
        SFOpenClient client = SFOpenClient.getInstance();
        return (MessageResp)client.doPost(url, req, new TypeReference<MessageResp<OrderFilterRespDto>>() {}, paramMap);
    }

    public void testOrder()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/order/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("7D82DC90CC9D40241ED2C95B08529224");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(200);
        head.setTransMessageId("201409040916141688");
        req.setHead(head);
        OrderReqDto orderReqDto = new OrderReqDto();
        orderReqDto.setOrderId("OPEN201409051418379991429491");
        orderReqDto.setExpressType((new Short("15")).shortValue());
        orderReqDto.setPayMethod((new Short("1")).shortValue());
        orderReqDto.setNeedReturnTrackingNo((new Short("0")).shortValue());
        orderReqDto.setIsDoCall((new Short("1")).shortValue());
        orderReqDto.setIsGenBillNo((new Short("1")).shortValue());
        orderReqDto.setCustId("7554111538");
        orderReqDto.setPayArea("755CQ");
        orderReqDto.setSendStartTime("2014-4-24 09:30:00");
        orderReqDto.setRemark("\u6613\u788E\u7269\u54C1\uFF0C\u5C0F\u5FC3\u8F7B\u653E");
        DeliverConsigneeInfoDto deliverInfoDto = new DeliverConsigneeInfoDto();
        deliverInfoDto.setAddress("\u4E0A\u5730");
        deliverInfoDto.setCity("\u671D\u9633");
        deliverInfoDto.setCompany("\u4EAC\u4E1C");
        deliverInfoDto.setContact("\u674E\u56DB");
        deliverInfoDto.setCountry("\u4E2D\u56FD");
        deliverInfoDto.setProvince("\u5317\u4EAC");
        deliverInfoDto.setShipperCode("787564");
        deliverInfoDto.setTel("010-95123669");
        deliverInfoDto.setMobile("13612822894");
        DeliverConsigneeInfoDto consigneeInfoDto = new DeliverConsigneeInfoDto();
        consigneeInfoDto.setAddress("\u4E16\u754C\u7B2C\u4E00\u5E7F\u573A");
        consigneeInfoDto.setCity("\u6DF1\u5733");
        consigneeInfoDto.setCompany("\u987A\u4E30");
        consigneeInfoDto.setContact("\u5F20\u4E09");
        consigneeInfoDto.setCountry("\u4E2D\u56FD");
        consigneeInfoDto.setProvince("\u5E7F\u4E1C");
        consigneeInfoDto.setShipperCode("518100");
        consigneeInfoDto.setTel("0755-33915561");
        consigneeInfoDto.setMobile("18588413321");
        CargoInfoDto cargoInfoDto = new CargoInfoDto();
        cargoInfoDto.setParcelQuantity(Integer.valueOf(1));
        cargoInfoDto.setCargo("\u624B\u673A");
        cargoInfoDto.setCargoCount("1000");
        cargoInfoDto.setCargoUnit("\u90E8");
        cargoInfoDto.setCargoWeight("121");
        cargoInfoDto.setCargoAmount("5200");
        cargoInfoDto.setCargoTotalWeight(new BigDecimal(0x1d8a8));
        List addedServiceDtos = new ArrayList();
        AddedServiceDto addedServiceDto = new AddedServiceDto();
        addedServiceDto.setName("COD");
        addedServiceDto.setValue("20000");
        addedServiceDtos.add(addedServiceDto);
        AddedServiceDto addedServiceCodDto = new AddedServiceDto();
        addedServiceCodDto.setName("CUSTID");
        addedServiceCodDto.setValue("7552732920");
        addedServiceDtos.add(addedServiceCodDto);
        orderReqDto.setDeliverInfo(deliverInfoDto);
        orderReqDto.setConsigneeInfo(consigneeInfoDto);
        orderReqDto.setCargoInfo(cargoInfoDto);
        orderReqDto.setAddedServices(addedServiceDtos);
        req.setBody(orderReqDto);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = order(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }

    public void testOrderQuery()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/order/query/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("F6982CA7BB9EF5A44A78CD77A84D9DF6");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(203);
        head.setTransMessageId("201409040916141689");
        req.setHead(head);
        OrderQueryReqDto rrderQueryReqDto = new OrderQueryReqDto();
        rrderQueryReqDto.setOrderId("201409051418379991429491");
        req.setBody(rrderQueryReqDto);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = orderQuery(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }

    public void testFilterOrder()
        throws Exception
    {
        String url = "https://open-sbox.st.sf-express.com:2000/rest/v1.0/filter/access_token/{access_token}/sf_appid/{sf_appid}/sf_appkey/{sf_appkey}";
        AppInfo appInfo = new AppInfo();
        appInfo.setAppId("01000035");
        appInfo.setAppKey("A9A0CDAC06D2BFA55E888C2FA48FADA5");
        appInfo.setAccessToken("F6982CA7BB9EF5A44A78CD77A84D9DF6");
        MessageReq req = new MessageReq();
        HeadMessageReq head = new HeadMessageReq();
        head.setTransType(204);
        head.setTransMessageId("201409040916141690");
        req.setHead(head);
        OrderFilterReqDto orderFilterReqDto = new OrderFilterReqDto();
        orderFilterReqDto.setFilterType(Integer.valueOf(1));
        orderFilterReqDto.setConsigneeCountry("\u4E2D\u56FD");
        orderFilterReqDto.setConsigneeProvince("\u5E7F\u4E1C\u7701");
        orderFilterReqDto.setConsigneeCity("\u6DF1\u5733\u5E02");
        orderFilterReqDto.setConsigneeCounty("\u5357\u5C71\u533A");
        orderFilterReqDto.setConsigneeAddress("\u5357\u6D77\u5927\u90533688\u53F7");
        orderFilterReqDto.setConsigneeTel("26536114");
        orderFilterReqDto.setDeliverCountry("\u4E2D\u56FD");
        orderFilterReqDto.setDeliverProvince("\u6E56\u5317\u7701");
        orderFilterReqDto.setDeliverCity("\u6B66\u6C49\u5E02");
        orderFilterReqDto.setDeliverCounty("\u6B66\u660C\u533A");
        orderFilterReqDto.setDeliverAddress("\u516B\u4E00\u8DEF299\u53F7");
        orderFilterReqDto.setDeliverTel("74512587");
        req.setBody(orderFilterReqDto);
        System.out.println((new StringBuilder("\u4F20\u5165\u53C2\u6570")).append(ToStringBuilder.reflectionToString(req)).toString());
        MessageResp response = filterOrder(url, appInfo, req);
        System.out.println((new StringBuilder("\u8FD4\u56DE\u53C2\u6570")).append(ToStringBuilder.reflectionToString(response)).toString());
        System.exit(0);
    }
}
