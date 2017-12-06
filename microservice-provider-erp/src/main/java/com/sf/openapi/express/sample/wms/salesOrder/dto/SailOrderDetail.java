// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderDetail.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class SailOrderDetail extends BaseEntity
{

    public SailOrderDetail()
    {
        bomAction = "N";
    }

    public String getErpOrderLineNum()
    {
        return erpOrderLineNum;
    }

    public void setErpOrderLineNum(String erpOrderLineNum)
    {
        this.erpOrderLineNum = erpOrderLineNum;
    }

    public String getItem()
    {
        return item;
    }

    public void setItem(String item)
    {
        this.item = item;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getUom()
    {
        return uom;
    }

    public void setUom(String uom)
    {
        this.uom = uom;
    }

    public String getLot()
    {
        return lot;
    }

    public void setLot(String lot)
    {
        this.lot = lot;
    }

    public String getQty()
    {
        return qty;
    }

    public void setQty(String qty)
    {
        this.qty = qty;
    }

    public String getItemPrice()
    {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice)
    {
        this.itemPrice = itemPrice;
    }

    public String getItemDiscount()
    {
        return itemDiscount;
    }

    public void setItemDiscount(String itemDiscount)
    {
        this.itemDiscount = itemDiscount;
    }

    public String getBomAction()
    {
        return bomAction;
    }

    public void setBomAction(String bomAction)
    {
        this.bomAction = bomAction;
    }

    public String getUserDef1()
    {
        return userDef1;
    }

    public void setUserDef1(String userDef1)
    {
        this.userDef1 = userDef1;
    }

    public String getUserDef2()
    {
        return userDef2;
    }

    public void setUserDef2(String userDef2)
    {
        this.userDef2 = userDef2;
    }

    public String getUserDef3()
    {
        return userDef3;
    }

    public void setUserDef3(String userDef3)
    {
        this.userDef3 = userDef3;
    }

    public String getUserDef4()
    {
        return userDef4;
    }

    public void setUserDef4(String userDef4)
    {
        this.userDef4 = userDef4;
    }

    public String getUserDef5()
    {
        return userDef5;
    }

    public void setUserDef5(String userDef5)
    {
        this.userDef5 = userDef5;
    }

    public String getUserDef6()
    {
        return userDef6;
    }

    public void setUserDef6(String userDef6)
    {
        this.userDef6 = userDef6;
    }

    public String getUserDef7()
    {
        return userDef7;
    }

    public void setUserDef7(String userDef7)
    {
        this.userDef7 = userDef7;
    }

    public String getUserDef8()
    {
        return userDef8;
    }

    public void setUserDef8(String userDef8)
    {
        this.userDef8 = userDef8;
    }

    private String erpOrderLineNum;
    private String item;
    private String itemName;
    private String uom;
    private String lot;
    private String qty;
    private String itemPrice;
    private String itemDiscount;
    private String bomAction;
    private String userDef1;
    private String userDef2;
    private String userDef3;
    private String userDef4;
    private String userDef5;
    private String userDef6;
    private String userDef7;
    private String userDef8;
}
