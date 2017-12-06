// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PurchaseOrderPushInfoHeader.java

package com.sf.openapi.express.sample.wms.purchaseOrder.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class PurchaseOrderPushInfoHeader extends BaseEntity
{

    public PurchaseOrderPushInfoHeader()
    {
    }

    public String getErpOrderNum()
    {
        return erpOrderNum;
    }

    public void setErpOrderNum(String erpOrderNum)
    {
        this.erpOrderNum = erpOrderNum;
    }

    public String getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptId(String receiptId)
    {
        this.receiptId = receiptId;
    }

    public String getCloseDate()
    {
        return closeDate;
    }

    public void setCloseDate(String closeDate)
    {
        this.closeDate = closeDate;
    }

    private String erpOrderNum;
    private String receiptId;
    private String closeDate;
}
