// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PurchaseOrderPushInfoItem.java

package com.sf.openapi.express.sample.wms.purchaseOrder.dto;

import com.sf.openapi.common.entity.BaseEntity;
import java.util.List;

public class PurchaseOrderPushInfoItem extends BaseEntity
{

    public PurchaseOrderPushInfoItem()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getErpOrderLineNum()
    {
        return erpOrderLineNum;
    }

    public void setErpOrderLineNum(String erpOrderLineNum)
    {
        this.erpOrderLineNum = erpOrderLineNum;
    }

    public String getSkuNo()
    {
        return skuNo;
    }

    public void setSkuNo(String skuNo)
    {
        this.skuNo = skuNo;
    }

    public String getQty()
    {
        return qty;
    }

    public void setQty(String qty)
    {
        this.qty = qty;
    }

    public String getLot()
    {
        return lot;
    }

    public void setLot(String lot)
    {
        this.lot = lot;
    }

    public String getExpirationDate()
    {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate)
    {
        this.expirationDate = expirationDate;
    }

    public String getInventorySts()
    {
        return inventorySts;
    }

    public void setInventorySts(String inventorySts)
    {
        this.inventorySts = inventorySts;
    }

    public String getVendor()
    {
        return vendor;
    }

    public void setVendor(String vendor)
    {
        this.vendor = vendor;
    }

    public List getSerialNumberList()
    {
        return serialNumberList;
    }

    public void setSerialNumberList(List serialNumberList)
    {
        this.serialNumberList = serialNumberList;
    }

    private String id;
    private String erpOrderLineNum;
    private String skuNo;
    private String qty;
    private String lot;
    private String expirationDate;
    private String inventorySts;
    private String vendor;
    private List serialNumberList;
}
