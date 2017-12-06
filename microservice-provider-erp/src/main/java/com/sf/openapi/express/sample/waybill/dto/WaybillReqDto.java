// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   WaybillReqDto.java

package com.sf.openapi.express.sample.waybill.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class WaybillReqDto extends BaseEntity
{

    public WaybillReqDto()
    {
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    private String orderId;
}
