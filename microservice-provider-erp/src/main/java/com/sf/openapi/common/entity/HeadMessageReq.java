// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HeadMessageReq.java

package com.sf.openapi.common.entity;

import com.sf.aries.core.model.BaseEntity;

public class HeadMessageReq extends BaseEntity
{

    public HeadMessageReq()
    {
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

    private static final long serialVersionUID = 0xa4908d5f2211a06dL;
    private int transType;
    private String transMessageId;
}
