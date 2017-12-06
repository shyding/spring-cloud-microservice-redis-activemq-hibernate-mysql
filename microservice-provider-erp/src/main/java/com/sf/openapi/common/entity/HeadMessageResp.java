// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HeadMessageResp.java

package com.sf.openapi.common.entity;

import com.sf.aries.core.model.BaseEntity;

public class HeadMessageResp extends BaseEntity
{

    public HeadMessageResp()
    {
    }

    public HeadMessageResp(String code, String message)
    {
        this.code = code;
        this.message = message;
    }

    public int getTransType()
    {
        return transType;
    }

    public void setTransType(int transType)
    {
        this.transType = transType;
    }

    public String getTransMessageId()
    {
        return transMessageId;
    }

    public void setTransMessageId(String transMessageId)
    {
        this.transMessageId = transMessageId;
    }

    public static final HeadMessageResp getOk()
    {
        HeadMessageResp processOk = new HeadMessageResp();
        processOk.setCode("EX_CODE_OPENAPI_0200");
        processOk.setMessage("\u5904\u7406\u6210\u529F");
        return processOk;
    }

    public static final HeadMessageResp getSecurityErr()
    {
        HeadMessageResp securityErr = new HeadMessageResp();
        securityErr.setCode("EX_CODE_OPENAPI_0500");
        securityErr.setMessage("\u7CFB\u7EDF\u5F02\u5E38");
        return securityErr;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    private static final long serialVersionUID = 0x8e6bc221456fc4e2L;
    private int transType;
    private String transMessageId;
    private String code;
    private String message;
}
