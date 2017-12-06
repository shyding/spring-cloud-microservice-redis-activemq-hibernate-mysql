// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PurchaseOrderResponseDto.java

package com.sf.openapi.express.sample.wms.purchaseOrder.dto;

import com.sf.openapi.express.sample.wms.common.dto.LscmResponseDto;

public class PurchaseOrderResponseDto extends LscmResponseDto
{

    public PurchaseOrderResponseDto()
    {
    }

    public String getOrderid()
    {
        return orderid;
    }

    public void setOrderid(String orderid)
    {
        this.orderid = orderid;
    }

    private String orderid;
}
