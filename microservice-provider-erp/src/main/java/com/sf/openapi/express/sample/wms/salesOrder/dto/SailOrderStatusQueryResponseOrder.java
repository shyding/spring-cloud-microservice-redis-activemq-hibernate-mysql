// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderStatusQueryResponseOrder.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.common.entity.BaseEntity;
import java.util.List;

public class SailOrderStatusQueryResponseOrder extends BaseEntity
{

    public SailOrderStatusQueryResponseOrder()
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

    public String getWaybillno()
    {
        return waybillno;
    }

    public void setWaybillno(String waybillno)
    {
        this.waybillno = waybillno;
    }

    public List getSteps()
    {
        return steps;
    }

    public void setSteps(List steps)
    {
        this.steps = steps;
    }

    private String orderid;
    private String waybillno;
    private List steps;
}
