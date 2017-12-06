// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LscmResponseDto.java

package com.sf.openapi.express.sample.wms.common.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class LscmResponseDto extends BaseEntity
{

    public LscmResponseDto()
    {
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

    private String result;
    private String remark;
}
