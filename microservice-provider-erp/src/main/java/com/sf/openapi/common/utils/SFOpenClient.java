// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SFOpenClient.java

package com.sf.openapi.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.openapi.common.entity.MessageReq;
import java.io.*;
import java.net.URLConnection;
import java.util.Map;

// Referenced classes of package com.sf.openapi.common.utils:
//            ParamUtils, URLConnectionUtils

public class SFOpenClient
{

    public SFOpenClient() {
    }

    public static SFOpenClient getInstance() {
        Class var0 = SFOpenClient.class;
        synchronized(SFOpenClient.class) {
            if(client == null) {
                client = new SFOpenClient();
            }
        }

        return client;
    }

    public Object doPost(String url, MessageReq req, TypeReference response, Map paramMap)
        throws Exception
    {
        URLConnection conn = null;
        if(url.startsWith("http:"))
            conn = URLConnectionUtils.getHttpConnection(ParamUtils.initParamToURL(url, paramMap), "POST");
        else
            conn = URLConnectionUtils.getHttpsConnection(ParamUtils.initParamToURL(url, paramMap), "POST");
        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, req);
        System.out.println(stringWriter.toString());
        conn.getOutputStream().write(stringWriter.toString().getBytes("utf-8"));
        conn.getOutputStream().flush();
        conn.getOutputStream().close();

        String responseStr = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            responseStr += line;
        }
        in.close();
        conn.getInputStream().close();
        System.out.println("post request, response content:" + responseStr);

        return objectMapper.readValue(new BufferedInputStream(new ByteArrayInputStream(responseStr.getBytes())), response);
    }

    public Object doGet(String url, TypeReference response, Map paramMap)
        throws Exception
    {
        URLConnection conn = null;
        if(url.startsWith("http:"))
            conn = URLConnectionUtils.getHttpConnection(ParamUtils.initParamToURL(url, paramMap), "GET");
        else
            conn = URLConnectionUtils.getHttpsConnection(ParamUtils.initParamToURL(url, paramMap), "GET");

        String responseStr = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            responseStr += line;
        }
        in.close();
        conn.getInputStream().close();
        System.out.println("get request, response content:" + responseStr);

        return objectMapper.readValue(new BufferedInputStream(new ByteArrayInputStream(responseStr.getBytes())), response);
    }

    private static SFOpenClient client = new SFOpenClient();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final String REQUEST_METHOD_POST = "POST";
    private static final String REQUEST_METHOD_GET = "GET";

}
