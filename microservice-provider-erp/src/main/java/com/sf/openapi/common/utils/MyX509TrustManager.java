// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MyX509TrustManager.java

package com.sf.openapi.common.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class MyX509TrustManager
    implements X509TrustManager
{

    public MyX509TrustManager()
    {
    }

    public X509Certificate[] getAcceptedIssuers()
    {
        return null;
    }

    public boolean isClientTrusted(X509Certificate arg0[])
    {
        return true;
    }

    public boolean isServerTrusted(X509Certificate arg0[])
    {
        return true;
    }

    public void checkClientTrusted(X509Certificate ax509certificate[], String s)
        throws CertificateException
    {
    }

    public void checkServerTrusted(X509Certificate ax509certificate[], String s)
        throws CertificateException
    {
    }
}
