// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderCancelReqDto.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.express.sample.wms.common.dto.LscmRequestDto;

public class SailOrderCancelReqDto extends LscmRequestDto
{

    public SailOrderCancelReqDto()
    {
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getOrderid()
    {
        return orderid;
    }

    public void setOrderid(String orderid)
    {
        this.orderid = orderid;
    }

    private String company;
    private String orderid;
}
