// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderInvoiceReqDto.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.express.sample.wms.common.dto.LscmRequestDto;
import java.util.List;

public class SailOrderInvoiceReqDto extends LscmRequestDto
{

    public SailOrderInvoiceReqDto()
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

    public String getErpOrder()
    {
        return erpOrder;
    }

    public void setErpOrder(String erpOrder)
    {
        this.erpOrder = erpOrder;
    }

    public List getInvoiceList()
    {
        return invoiceList;
    }

    public void setInvoiceList(List invoiceList)
    {
        this.invoiceList = invoiceList;
    }

    private static final long serialVersionUID = 0x5bf5a85d0e23d092L;
    private String company;
    private String erpOrder;
    private List invoiceList;
}
