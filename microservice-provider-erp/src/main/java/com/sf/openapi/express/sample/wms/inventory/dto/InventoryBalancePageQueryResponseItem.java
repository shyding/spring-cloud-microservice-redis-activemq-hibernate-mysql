// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InventoryBalancePageQueryResponseItem.java

package com.sf.openapi.express.sample.wms.inventory.dto;

import com.sf.openapi.common.entity.BaseEntity;
import java.math.BigDecimal;

public class InventoryBalancePageQueryResponseItem extends BaseEntity
{

    public InventoryBalancePageQueryResponseItem()
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

    public String getWarehouse()
    {
        return warehouse;
    }

    public void setWarehouse(String warehouse)
    {
        this.warehouse = warehouse;
    }

    public String getItem()
    {
        return item;
    }

    public void setItem(String item)
    {
        this.item = item;
    }

    public BigDecimal getOn_hand_qty()
    {
        return on_hand_qty;
    }

    public void setOn_hand_qty(BigDecimal on_hand_qty)
    {
        this.on_hand_qty = on_hand_qty;
    }

    public String getLot()
    {
        return lot;
    }

    public void setLot(String lot)
    {
        this.lot = lot;
    }

    public String getExpiration_date()
    {
        return expiration_date;
    }

    public void setExpiration_date(String expiration_date)
    {
        this.expiration_date = expiration_date;
    }

    public BigDecimal getInventory_sts()
    {
        return inventory_sts;
    }

    public void setInventory_sts(BigDecimal inventory_sts)
    {
        this.inventory_sts = inventory_sts;
    }

    public String getUser_def1()
    {
        return user_def1;
    }

    public void setUser_def1(String user_def1)
    {
        this.user_def1 = user_def1;
    }

    public String getUser_def2()
    {
        return user_def2;
    }

    public void setUser_def2(String user_def2)
    {
        this.user_def2 = user_def2;
    }

    public String getUser_def3()
    {
        return user_def3;
    }

    public void setUser_def3(String user_def3)
    {
        this.user_def3 = user_def3;
    }

    public String getUser_def4()
    {
        return user_def4;
    }

    public void setUser_def4(String user_def4)
    {
        this.user_def4 = user_def4;
    }

    public String getUser_def5()
    {
        return user_def5;
    }

    public void setUser_def5(String user_def5)
    {
        this.user_def5 = user_def5;
    }

    public String getUser_def6()
    {
        return user_def6;
    }

    public void setUser_def6(String user_def6)
    {
        this.user_def6 = user_def6;
    }

    public String getUser_def7()
    {
        return user_def7;
    }

    public void setUser_def7(String user_def7)
    {
        this.user_def7 = user_def7;
    }

    public String getUser_def8()
    {
        return user_def8;
    }

    public void setUser_def8(String user_def8)
    {
        this.user_def8 = user_def8;
    }

    private String company;
    private String warehouse;
    private String item;
    private BigDecimal on_hand_qty;
    private String lot;
    private String expiration_date;
    private BigDecimal inventory_sts;
    private String user_def1;
    private String user_def2;
    private String user_def3;
    private String user_def4;
    private String user_def5;
    private String user_def6;
    private String user_def7;
    private String user_def8;
}
