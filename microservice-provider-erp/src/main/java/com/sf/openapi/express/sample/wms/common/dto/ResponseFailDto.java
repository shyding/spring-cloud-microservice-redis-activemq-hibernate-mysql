// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ResponseFailDto.java

package com.sf.openapi.express.sample.wms.common.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class ResponseFailDto extends BaseEntity
{

    public ResponseFailDto()
    {
    }

    public String getReasoncode()
    {
        return reasoncode;
    }

    public void setReasoncode(String reasoncode)
    {
        this.reasoncode = reasoncode;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    private String reasoncode;
    private String remark;
}
