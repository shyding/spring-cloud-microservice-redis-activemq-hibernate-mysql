// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LscmRequestDto.java

package com.sf.openapi.express.sample.wms.common.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class LscmRequestDto extends BaseEntity
{

    public LscmRequestDto()
    {
    }

    public String getCheckword()
    {
        return checkword;
    }

    public void setCheckword(String checkword)
    {
        this.checkword = checkword;
    }

    private String checkword;
}
