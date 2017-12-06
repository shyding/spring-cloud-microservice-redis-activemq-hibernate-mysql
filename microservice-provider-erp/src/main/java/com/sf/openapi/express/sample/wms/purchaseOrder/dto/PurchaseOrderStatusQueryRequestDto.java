// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PurchaseOrderStatusQueryRequestDto.java

package com.sf.openapi.express.sample.wms.purchaseOrder.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class PurchaseOrderStatusQueryRequestDto extends BaseEntity
{

    public PurchaseOrderStatusQueryRequestDto()
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

    public String getWarehouse()
    {
        return warehouse;
    }

    public void setWarehouse(String warehouse)
    {
        this.warehouse = warehouse;
    }

    private String checkword;
    private String company;
    private String orderid;
    private String warehouse;
}
