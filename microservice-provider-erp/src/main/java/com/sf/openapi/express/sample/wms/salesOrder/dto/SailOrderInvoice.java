// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderInvoice.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.common.entity.BaseEntity;
import java.util.List;

// Referenced classes of package com.sf.openapi.express.sample.wms.salesOrder.dto:
//            SailOrderInvoiceHeader

public class SailOrderInvoice extends BaseEntity
{

    public SailOrderInvoice()
    {
    }

    public SailOrderInvoiceHeader getHeader()
    {
        return header;
    }

    public void setHeader(SailOrderInvoiceHeader header)
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

    private static final long serialVersionUID = 0xad2ea69a016ff2dfL;
    private SailOrderInvoiceHeader header;
    private List detailList;
}
