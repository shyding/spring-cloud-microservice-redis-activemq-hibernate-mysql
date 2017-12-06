// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OrderNotifyReqDto.java

package com.sf.openapi.express.sample.order.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class OrderNotifyReqDto extends BaseEntity
{

    public OrderNotifyReqDto()
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

    public String getMailNo()
    {
        return mailNo;
    }

    public void setMailNo(String mailNo)
    {
        this.mailNo = mailNo;
    }

    public String getOriginCode()
    {
        return originCode;
    }

    public void setOriginCode(String originCode)
    {
        this.originCode = originCode;
    }

    public String getDestCode()
    {
        return destCode;
    }

    public void setDestCode(String destCode)
    {
        this.destCode = destCode;
    }

    public String getFilterResult()
    {
        return filterResult;
    }

    public void setFilterResult(String filterResult)
    {
        this.filterResult = filterResult;
    }

    public String getReturnTrackingNo()
    {
        return returnTrackingNo;
    }

    public void setReturnTrackingNo(String returnTrackingNo)
    {
        this.returnTrackingNo = returnTrackingNo;
    }

    private String orderId;
    private String mailNo;
    private String originCode;
    private String destCode;
    private String filterResult;
    private String returnTrackingNo;
}
