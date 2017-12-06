// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderResponseDto.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.express.sample.wms.common.dto.LscmResponseDto;

public class SailOrderResponseDto extends LscmResponseDto
{

    public SailOrderResponseDto()
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
