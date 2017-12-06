// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderStatusQueryRequestDto.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class SailOrderStatusQueryRequestDto extends BaseEntity
{

    public SailOrderStatusQueryRequestDto()
    {
    }

    public String getCheckword()
    {
        return checkword;
    }

    public void setCheckword(String checkword)
    {
        this.checkword = checkword;
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

    public String getWaybillno()
    {
        return waybillno;
    }

    public void setWaybillno(String waybillno)
    {
        this.waybillno = waybillno;
    }

    private String checkword;
    private String company;
    private String orderid;
    private String waybillno;
}
