// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RoutePushRespDto.java

package com.sf.openapi.express.sample.route.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class RoutePushRespDto extends BaseEntity
{

    public RoutePushRespDto()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getIdError()
    {
        return idError;
    }

    public void setIdError(String idError)
    {
        this.idError = idError;
    }

    private String id;
    private String idError;
}
