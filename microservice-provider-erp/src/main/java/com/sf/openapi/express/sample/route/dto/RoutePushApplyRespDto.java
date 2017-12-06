// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RoutePushApplyRespDto.java

package com.sf.openapi.express.sample.route.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class RoutePushApplyRespDto extends BaseEntity
{

    public RoutePushApplyRespDto()
    {
    }

    public String getMark()
    {
        return mark;
    }

    public void setMark(String mark)
    {
        this.mark = mark;
    }

    private String mark;
}
