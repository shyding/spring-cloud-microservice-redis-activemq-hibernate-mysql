// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PurchaseOrderStatusQueryResponseDto.java

package com.sf.openapi.express.sample.wms.purchaseOrder.dto;

import com.sf.openapi.common.entity.BaseEntity;
import java.util.List;

// Referenced classes of package com.sf.openapi.express.sample.wms.purchaseOrder.dto:
//            PurchaseOrderStatusQueryResponseHeader

public class PurchaseOrderStatusQueryResponseDto extends BaseEntity
{

    public PurchaseOrderStatusQueryResponseDto()
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

    public PurchaseOrderStatusQueryResponseHeader getHeader()
    {
        return header;
    }

    public void setHeader(PurchaseOrderStatusQueryResponseHeader header)
    {
        this.header = header;
    }

    public List getDetailList()
    {
        return detailList;
    }

    public void setDetailList(List detailList)
    {
        this.detailList = detailList;
    }

    private String result;
    private String remark;
    private PurchaseOrderStatusQueryResponseHeader header;
    private List detailList;
}
