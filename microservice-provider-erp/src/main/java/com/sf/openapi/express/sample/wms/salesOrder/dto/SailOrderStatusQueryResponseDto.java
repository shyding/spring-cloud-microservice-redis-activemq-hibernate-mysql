// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderStatusQueryResponseDto.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.common.entity.BaseEntity;

// Referenced classes of package com.sf.openapi.express.sample.wms.salesOrder.dto:
//            SailOrderStatusQueryResponseOrder

public class SailOrderStatusQueryResponseDto extends BaseEntity
{

    public SailOrderStatusQueryResponseDto()
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

    public SailOrderStatusQueryResponseOrder getOrder()
    {
        return order;
    }

    public void setOrder(SailOrderStatusQueryResponseOrder order)
    {
        this.order = order;
    }

    private String result;
    private String remark;
    private SailOrderStatusQueryResponseOrder order;
}
