// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderPushInfoReqDto.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.express.sample.wms.common.dto.LscmRequestDto;
import java.util.List;

// Referenced classes of package com.sf.openapi.express.sample.wms.salesOrder.dto:
//            SailOrderPushInfoHeader

public class SailOrderPushInfoReqDto extends LscmRequestDto
{

    public SailOrderPushInfoReqDto()
    {
    }

    public SailOrderPushInfoHeader getHeader()
    {
        return header;
    }

    public void setHeader(SailOrderPushInfoHeader header)
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

    public List getContainerList()
    {
        return containerList;
    }

    public void setContainerList(List containerList)
    {
        this.containerList = containerList;
    }

    private SailOrderPushInfoHeader header;
    private List detailList;
    private List containerList;
}
