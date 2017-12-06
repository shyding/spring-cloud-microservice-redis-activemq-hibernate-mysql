// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PurchaseOrderPushInfoReqDto.java

package com.sf.openapi.express.sample.wms.purchaseOrder.dto;

import com.sf.openapi.express.sample.wms.common.dto.LscmRequestDto;
import java.util.List;

// Referenced classes of package com.sf.openapi.express.sample.wms.purchaseOrder.dto:
//            PurchaseOrderPushInfoHeader

public class PurchaseOrderPushInfoReqDto extends LscmRequestDto
{

    public PurchaseOrderPushInfoReqDto()
    {
    }

    public PurchaseOrderPushInfoHeader getHeader()
    {
        return header;
    }

    public void setHeader(PurchaseOrderPushInfoHeader header)
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

    private PurchaseOrderPushInfoHeader header;
    private List detailList;
}
