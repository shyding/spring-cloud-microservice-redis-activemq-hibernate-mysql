// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PurchaseOrderPushReqDto.java

package com.sf.openapi.express.sample.wms.purchaseOrder.dto;

import com.sf.openapi.express.sample.wms.common.dto.LscmRequestDto;

public class PurchaseOrderPushReqDto extends LscmRequestDto
{

    public PurchaseOrderPushReqDto()
    {
    }

    public String getErpOrderNum()
    {
        return erpOrderNum;
    }

    public void setErpOrderNum(String erpOrderNum)
    {
        this.erpOrderNum = erpOrderNum;
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    private String erpOrderNum;
    private String company;
    private String url;
}
