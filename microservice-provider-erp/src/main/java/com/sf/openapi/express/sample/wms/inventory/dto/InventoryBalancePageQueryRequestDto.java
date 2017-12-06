// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InventoryBalancePageQueryRequestDto.java

package com.sf.openapi.express.sample.wms.inventory.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class InventoryBalancePageQueryRequestDto extends BaseEntity
{

    public InventoryBalancePageQueryRequestDto()
    {
    }

    public String getCheckword()
    {
        return checkword;
    }

    public void setCheckword(String checkword)
    {
        this.checkword = checkword;
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getItem()
    {
        return item;
    }

    public void setItem(String item)
    {
        this.item = item;
    }

    public String getWarehouse()
    {
        return warehouse;
    }

    public void setWarehouse(String warehouse)
    {
        this.warehouse = warehouse;
    }

    public String getInventory_sts()
    {
        return inventory_sts;
    }

    public void setInventory_sts(String inventory_sts)
    {
        this.inventory_sts = inventory_sts;
    }

    public String getPage_index()
    {
        return page_index;
    }

    public void setPage_index(String page_index)
    {
        this.page_index = page_index;
    }

    private String checkword;
    private String company;
    private String item;
    private String warehouse;
    private String inventory_sts;
    private String page_index;
}
