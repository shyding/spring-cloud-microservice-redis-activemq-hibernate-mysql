// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OrderFilterReqDto.java

package com.sf.openapi.express.sample.order.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class OrderFilterReqDto extends BaseEntity
{

    public OrderFilterReqDto()
    {
    }

    public Integer getFilterType()
    {
        return filterType;
    }

    public void setFilterType(Integer filterType)
    {
        this.filterType = filterType;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getConsigneeAddress()
    {
        return consigneeAddress;
    }

    public void setConsigneeAddress(String consigneeAddress)
    {
        this.consigneeAddress = consigneeAddress;
    }

    public String getDeliverCustId()
    {
        return deliverCustId;
    }

    public void setDeliverCustId(String deliverCustId)
    {
        this.deliverCustId = deliverCustId;
    }

    public String getDeliverTel()
    {
        return deliverTel;
    }

    public void setDeliverTel(String deliverTel)
    {
        this.deliverTel = deliverTel;
    }

    public String getDeliverAddress()
    {
        return deliverAddress;
    }

    public void setDeliverAddress(String deliverAddress)
    {
        this.deliverAddress = deliverAddress;
    }

    public String getDeliverProvince()
    {
        return deliverProvince;
    }

    public void setDeliverProvince(String deliverProvince)
    {
        this.deliverProvince = deliverProvince;
    }

    public String getDeliverCity()
    {
        return deliverCity;
    }

    public void setDeliverCity(String deliverCity)
    {
        this.deliverCity = deliverCity;
    }

    public String getDeliverCounty()
    {
        return deliverCounty;
    }

    public void setDeliverCounty(String deliverCounty)
    {
        this.deliverCounty = deliverCounty;
    }

    public String getDeliverCountry()
    {
        return deliverCountry;
    }

    public void setDeliverCountry(String deliverCountry)
    {
        this.deliverCountry = deliverCountry;
    }

    public String getConsigneeTel()
    {
        return consigneeTel;
    }

    public void setConsigneeTel(String consigneeTel)
    {
        this.consigneeTel = consigneeTel;
    }

    public String getConsigneeProvince()
    {
        return consigneeProvince;
    }

    public void setConsigneeProvince(String consigneeProvince)
    {
        this.consigneeProvince = consigneeProvince;
    }

    public String getConsigneeCity()
    {
        return consigneeCity;
    }

    public void setConsigneeCity(String consigneeCity)
    {
        this.consigneeCity = consigneeCity;
    }

    public String getConsigneeCounty()
    {
        return consigneeCounty;
    }

    public void setConsigneeCounty(String consigneeCounty)
    {
        this.consigneeCounty = consigneeCounty;
    }

    public String getConsigneeCountry()
    {
        return consigneeCountry;
    }

    public void setConsigneeCountry(String consigneeCountry)
    {
        this.consigneeCountry = consigneeCountry;
    }

    private Integer filterType;
    private String orderId;
    private String consigneeAddress;
    private String deliverCustId;
    private String deliverTel;
    private String deliverAddress;
    private String deliverProvince;
    private String deliverCity;
    private String deliverCounty;
    private String deliverCountry;
    private String consigneeTel;
    private String consigneeProvince;
    private String consigneeCity;
    private String consigneeCounty;
    private String consigneeCountry;
}
