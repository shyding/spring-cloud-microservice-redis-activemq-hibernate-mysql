// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RouteQueryReqDto.java

package com.sf.openapi.express.sample.route.dto;


public class RouteQueryReqDto
{

    public RouteQueryReqDto()
    {
    }

    public Integer getTrackingType()
    {
        return trackingType;
    }

    public void setTrackingType(Integer trackingType)
    {
        this.trackingType = trackingType;
    }

    public String getTrackingNumber()
    {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber)
    {
        this.trackingNumber = trackingNumber;
    }

    public Integer getMethodType()
    {
        return methodType;
    }

    public void setMethodType(Integer methodType)
    {
        this.methodType = methodType;
    }

    private Integer trackingType;
    private String trackingNumber;
    private Integer methodType;
}
