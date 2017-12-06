// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderPushReqDto.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.express.sample.wms.common.dto.LscmRequestDto;

public class SailOrderPushReqDto extends LscmRequestDto
{

    public SailOrderPushReqDto()
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

    public String getWarehouse()
    {
        return warehouse;
    }

    public void setWarehouse(String warehouse)
    {
        this.warehouse = warehouse;
    }

    public String getErpOrder()
    {
        return erpOrder;
    }

    public void setErpOrder(String erpOrder)
    {
        this.erpOrder = erpOrder;
    }

    public String getSalesOrderType()
    {
        return salesOrderType;
    }

    public void setSalesOrderType(String salesOrderType)
    {
        this.salesOrderType = salesOrderType;
    }

    private String company;
    private String warehouse;
    private String erpOrder;
    private String salesOrderType;
}
