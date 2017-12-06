// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderInvoiceRespDto.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class SailOrderInvoiceRespDto extends BaseEntity
{

    public SailOrderInvoiceRespDto()
    {
    }

    public String getErpOrder()
    {
        return erpOrder;
    }

    public void setErpOrder(String erpOrder)
    {
        this.erpOrder = erpOrder;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    private static final long serialVersionUID = 0xd537d3595327d859L;
    private String erpOrder;
    private String result;
    private String remark;
}
