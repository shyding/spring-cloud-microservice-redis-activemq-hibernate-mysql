// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderQueryResponseDto.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.common.entity.BaseEntity;
import java.util.List;

// Referenced classes of package com.sf.openapi.express.sample.wms.salesOrder.dto:
//            SailOrderQueryResponseHeader

public class SailOrderQueryResponseDto extends BaseEntity
{

    public SailOrderQueryResponseDto()
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

    public SailOrderQueryResponseHeader getHeader()
    {
        return header;
    }

    public void setHeader(SailOrderQueryResponseHeader header)
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

    private String result;
    private String remark;
    private SailOrderQueryResponseHeader header;
    private List detailList;
    private List containerList;
}
