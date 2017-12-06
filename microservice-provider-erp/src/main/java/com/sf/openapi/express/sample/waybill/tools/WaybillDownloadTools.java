// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   WaybillDownloadTools.java

package com.sf.openapi.express.sample.waybill.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.openapi.common.entity.*;
import com.sf.openapi.common.utils.*;
import com.sf.openapi.express.sample.waybill.dto.CargoInfoDto;
import com.sf.openapi.express.sample.waybill.dto.WaybillDto;
import com.sf.openapi.express.sample.waybill.dto.WaybillRespDto;

import java.io.*;
import java.net.URLConnection;
import java.util.*;

public class WaybillDownloadTools
{

    public WaybillDownloadTools()
    {
    }

    public static MessageResp waybillDownload(String url, AppInfo appInfo, MessageReq req)
        throws Exception
    {
        Map paramMap = new HashMap();
        paramMap.put("sf_appid", appInfo.getAppId());
        paramMap.put("sf_appkey", appInfo.getAppKey());
        paramMap.put("access_token", appInfo.getAccessToken());
        SFOpenClient client = SFOpenClient.getInstance();
        return (MessageResp)client.doPost(url, req, new TypeReference<MessageResp<WaybillRespDto>>() {}, paramMap);
    }

    public static void main_11(String args[])
        throws Exception
    {
        URLConnection conn = null;
        conn = URLConnectionUtils.getHttpConnection(ParamUtils.initParamToURL("http://localhost:4040/sf/waybill/print?type=poster_100mm150mm&output=print", null), "POST");
        conn.setReadTimeout(0x4c4b40);
        List waybillDtoList = new ArrayList();
        WaybillDto dto = new WaybillDto();
        dto.setMailNo("120000000001,001123456789");
        dto.setConsignerAddress("\u83B2\u82B1\u5C71\u897F\u7B2C\u4E00\u4E16\u754C\u5E7F\u573A");
        dto.setConsignerCity("\u6DF1\u5733\u5E02");
        dto.setConsignerCompany("\u987A\u4E30\u901F\u8FD0\u96C6\u56E2");
        dto.setConsignerMobile("15820456987");
        dto.setConsignerName("\u4E30\u4ED4");
        dto.setConsignerProvince("\u5E7F\u4E1C\u7701");
        dto.setConsignerShipperCode("5180000");
        dto.setConsignerTel("0755-33066758");
        dto.setDeliverProvince("\u6C5F\u82CF\u7701");
        dto.setDeliverCity("\u5357\u4EAC\u5E02");
        dto.setDeliverAddress("\u65B0\u6D32\u4E07\u57FA\u5546\u52A1\u5927\u53A6");
        dto.setDestCode("755");
        dto.setZipCode("655");
        dto.setExpressType(1);
        dto.setOrderNo("SFAPI00000000000000001");
        dto.setPayMethod(1);
        dto.setReturnTrackingNo("477329969357");
        CargoInfoDto cargo = new CargoInfoDto();
        cargo.setCargo("\u6587\u4EF6");
        cargo.setCargoCount(Integer.valueOf(1));
        cargo.setCargoUnit("\u4EF6");
        cargo.setSku("");
        cargo.setRemark("\u91CD\u8981\u6587\u4EF6");
        List cargoInfoList = new ArrayList();
        cargoInfoList.add(cargo);
        dto.setCargoInfoDtoList(cargoInfoList);
        waybillDtoList.add(dto);
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, waybillDtoList);
        System.out.println(stringWriter.toString());
        conn.getOutputStream().write(stringWriter.toString().getBytes());
        conn.getOutputStream().flush();
        conn.getOutputStream().close();
        InputStream in = conn.getInputStream();
        InputStreamReader reader = new InputStreamReader(in);
        int count = 0;
        char buffer[] = new char[1024];
        String response;
        for(response = ""; (count = reader.read(buffer)) != -1; response = (new StringBuilder(String.valueOf(response))).append(new String(buffer, 0, count)).toString());
        System.out.println(response);
    }
}
