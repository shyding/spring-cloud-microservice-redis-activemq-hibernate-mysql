// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InventoryBalancePageQueryResponseDto.java

package com.sf.openapi.express.sample.wms.inventory.dto;

import com.sf.openapi.common.entity.BaseEntity;
import java.math.BigDecimal;
import java.util.List;

public class InventoryBalancePageQueryResponseDto extends BaseEntity
{

    public InventoryBalancePageQueryResponseDto()
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

    public BigDecimal getTotal_size()
    {
        return total_size;
    }

    public void setTotal_size(BigDecimal total_size)
    {
        this.total_size = total_size;
    }

    public BigDecimal getTotal_page()
    {
        return total_page;
    }

    public void setTotal_page(BigDecimal total_page)
    {
        this.total_page = total_page;
    }

    public String getPage_index()
    {
        return page_index;
    }

    public void setPage_index(String page_index)
    {
        this.page_index = page_index;
    }

    public BigDecimal getPage_size()
    {
        return page_size;
    }

    public void setPage_size(BigDecimal page_size)
    {
        this.page_size = page_size;
    }

    public BigDecimal getCurrent_page_size()
    {
        return current_page_size;
    }

    public void setCurrent_page_size(BigDecimal current_page_size)
    {
        this.current_page_size = current_page_size;
    }

    public List getItemList()
    {
        return itemList;
    }

    public void setItemList(List itemList)
    {
        this.itemList = itemList;
    }

    private String result;
    private String remark;
    private BigDecimal total_size;
    private BigDecimal total_page;
    private String page_index;
    private BigDecimal page_size;
    private BigDecimal current_page_size;
    private List itemList;
}
