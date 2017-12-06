// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RoutePushQueryRespDto.java

package com.sf.openapi.express.sample.route.dto;


public class RoutePushQueryRespDto
{

    public RoutePushQueryRespDto()
    {
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getMailNo()
    {
        return mailNo;
    }

    public void setMailNo(String mailNo)
    {
        this.mailNo = mailNo;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getAcceptTime()
    {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime)
    {
        this.acceptTime = acceptTime;
    }

    public String getAcceptAddress()
    {
        return acceptAddress;
    }

    public void setAcceptAddress(String acceptAddress)
    {
        this.acceptAddress = acceptAddress;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getOpCode()
    {
        return opCode;
    }

    public void setOpCode(String opCode)
    {
        this.opCode = opCode;
    }

    private Integer id;
    private String mailNo;
    private String orderId;
    private String acceptTime;
    private String acceptAddress;
    private String remark;
    private String opCode;
}
