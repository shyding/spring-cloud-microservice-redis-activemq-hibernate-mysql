// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   URLConnectionUtils.java

package com.sf.openapi.common.utils;

import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import javax.net.ssl.*;

// Referenced classes of package com.sf.openapi.common.utils:
//            MyX509TrustManager

public class URLConnectionUtils
{

    public URLConnectionUtils()
    {
    }

    public static HttpsURLConnection getHttpsConnection(String url, String method)
        throws Exception
    {
        System.out.println(url);
        TrustManager tm[] = {
            new MyX509TrustManager()
        };
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, tm, new SecureRandom());
        System.setProperty("java.protocol.handler.pkgs", "javax.net.ssl");
        HostnameVerifier hv = new HostnameVerifier() {

            public boolean verify(String urlHostName, SSLSession session)
            {
                return true;
            }

        }
;
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
        javax.net.ssl.SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL myURL = new URL(url);
        HttpsURLConnection httpsConn = (HttpsURLConnection)myURL.openConnection();
        httpsConn.setSSLSocketFactory(ssf);
        httpsConn.setDoOutput(true);
        httpsConn.setDoInput(true);
        httpsConn.setUseCaches(false);
        httpsConn.setRequestMethod(method);
        httpsConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        httpsConn.setConnectTimeout(5000);
        httpsConn.setReadTimeout(0x186a0);
        return httpsConn;
    }

    public static HttpURLConnection getHttpConnection(String url, String method)
        throws Exception
    {
        URL myURL = new URL(url);
        HttpURLConnection httpConn = (HttpURLConnection)myURL.openConnection();
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setUseCaches(false);
        httpConn.setRequestMethod(method);
        httpConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        httpConn.setConnectTimeout(5000);
        httpConn.setReadTimeout(0x186a0);
        return httpConn;
    }

    private static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
    private static final int CONNECT_TIME_OUT = 5000;
    private static final int READ_TIME_OUT = 0x186a0;
}
