// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OrderRespDto.java

package com.sf.openapi.express.sample.order.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class OrderRespDto extends BaseEntity
{

    public OrderRespDto()
    {
    }

    public OrderRespDto(String orderId)
    {
        this.orderId = orderId;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getFilterLevel()
    {
        return filterLevel;
    }

    public void setFilterLevel(String filterLevel)
    {
        this.filterLevel = filterLevel;
    }

    public String getOrderTriggerCondition()
    {
        return orderTriggerCondition;
    }

    public void setOrderTriggerCondition(String orderTriggerCondition)
    {
        this.orderTriggerCondition = orderTriggerCondition;
    }

    public String getRemarkCode() {
        return remarkCode;
    }

    public void setRemarkCode(String remarkCode) {
        this.remarkCode = remarkCode;
    }

    private static final long serialVersionUID = 0x73e9e999daa55f70L;
    private String orderId;
    private String filterLevel;
    private String orderTriggerCondition;
    private String remarkCode;
}
