// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PurchaseOrderRequestDto.java

package com.sf.openapi.express.sample.wms.purchaseOrder.dto;

import com.sf.openapi.express.sample.wms.common.dto.LscmRequestDto;
import java.util.List;

// Referenced classes of package com.sf.openapi.express.sample.wms.purchaseOrder.dto:
//            PurchaseHeader

public class PurchaseOrderRequestDto extends LscmRequestDto
{

    public PurchaseOrderRequestDto()
    {
    }

    public PurchaseHeader getHeader()
    {
        return header;
    }

    public void setHeader(PurchaseHeader header)
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

    private PurchaseHeader header;
    private List detailList;
}
