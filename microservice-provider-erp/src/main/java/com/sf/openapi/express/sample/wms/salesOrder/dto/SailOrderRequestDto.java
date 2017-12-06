// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderRequestDto.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.express.sample.wms.common.dto.LscmRequestDto;
import java.util.List;

// Referenced classes of package com.sf.openapi.express.sample.wms.salesOrder.dto:
//            SailOrderHeader

public class SailOrderRequestDto extends LscmRequestDto
{

    public SailOrderRequestDto()
    {
    }

    public SailOrderHeader getHeader()
    {
        return header;
    }

    public void setHeader(SailOrderHeader header)
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

    private SailOrderHeader header;
    private List detailList;
}
