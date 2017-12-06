// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   VendorResponseDto.java

package com.sf.openapi.express.sample.wms.vendor.dto;

import com.sf.openapi.express.sample.wms.common.dto.LscmResponseDto;

public class VendorResponseDto extends LscmResponseDto
{

    public VendorResponseDto()
    {
    }

    public String getVendor()
    {
        return vendor;
    }

    public void setVendor(String vendor)
    {
        this.vendor = vendor;
    }

    private String vendor;
}
