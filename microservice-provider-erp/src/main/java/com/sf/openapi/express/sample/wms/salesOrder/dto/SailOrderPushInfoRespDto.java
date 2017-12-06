// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderPushInfoRespDto.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class SailOrderPushInfoRespDto extends BaseEntity
{

    public SailOrderPushInfoRespDto()
    {
    }

    public String getSuccess()
    {
        return success;
    }

    public void setSuccess(String success)
    {
        this.success = success;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    private String success;
    private String reason;
}
