// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProductRespDto.java

package com.sf.openapi.express.sample.product.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class ProductRespDto extends BaseEntity
{

    public ProductRespDto()
    {
    }

    public String getSeries()
    {
        return series;
    }

    public void setSeries(String series)
    {
        this.series = series;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    private String series;
    private String name;
    private String code;
}
