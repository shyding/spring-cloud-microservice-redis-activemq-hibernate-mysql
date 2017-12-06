// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ItemRequestDto.java

package com.sf.openapi.express.sample.wms.goods.dto;

import com.sf.openapi.express.sample.wms.common.dto.LscmRequestDto;
import java.util.List;

public class ItemRequestDto extends LscmRequestDto
{

    public ItemRequestDto()
    {
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getInterfaceActionCode()
    {
        return interfaceActionCode;
    }

    public void setInterfaceActionCode(String interfaceActionCode)
    {
        this.interfaceActionCode = interfaceActionCode;
    }

    public List getItemlist()
    {
        return itemlist;
    }

    public void setItemlist(List itemlist)
    {
        this.itemlist = itemlist;
    }

    private String company;
    private String interfaceActionCode;
    private List itemlist;
}
