// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RouteRespDto.java

package com.sf.openapi.express.sample.route.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class RouteRespDto extends BaseEntity
{

    public RouteRespDto()
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

    public String getProductCode()
    {
        return productCode;
    }

    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getConsignorContName()
    {
        return consignorContName;
    }

    public void setConsignorContName(String consignorContName)
    {
        this.consignorContName = consignorContName;
    }

    public String getAddresseeCompName()
    {
        return addresseeCompName;
    }

    public void setAddresseeCompName(String addresseeCompName)
    {
        this.addresseeCompName = addresseeCompName;
    }

    private String orderId;
    private String mailNo;
    private String acceptTime;
    private String acceptAddress;
    private String opcode;
    private String remark;
    private String productCode;
    private String productName;
    private String consignorContName;
    private String addresseeCompName;
}
