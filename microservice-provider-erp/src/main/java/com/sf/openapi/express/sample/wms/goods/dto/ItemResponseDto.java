// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ItemResponseDto.java

package com.sf.openapi.express.sample.wms.goods.dto;

import com.sf.openapi.express.sample.wms.common.dto.LscmResponseDto;
import java.util.List;

public class ItemResponseDto extends LscmResponseDto
{

    public ItemResponseDto()
    {
    }

    public List getItemlist()
    {
        return itemlist;
    }

    public void setItemlist(List itemlist)
    {
        this.itemlist = itemlist;
    }

    private List itemlist;
}
