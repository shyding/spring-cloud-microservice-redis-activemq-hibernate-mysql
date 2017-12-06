// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RouteIncRespDto.java

package com.sf.openapi.express.sample.route.dto;

import java.math.BigDecimal;

public class RouteIncRespDto
{

    public RouteIncRespDto()
    {
    }

    public BigDecimal getId()
    {
        return id;
    }

    public void setId(BigDecimal id)
    {
        this.id = id;
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

    public String getAcceptAddress()
    {
        return acceptAddress;
    }

    public void setAcceptAddress(String acceptAddress)
    {
        this.acceptAddress = acceptAddress;
    }

    public String getOpcode()
    {
        return opcode;
    }

    public void setOpcode(String opcode)
    {
        this.opcode = opcode;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getAcceptTime()
    {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime)
    {
        this.acceptTime = acceptTime;
    }

    private BigDecimal id;
    private String orderId;
    private String mailNo;
    private String acceptTime;
    private String acceptAddress;
    private String opcode;
    private String remark;
}
