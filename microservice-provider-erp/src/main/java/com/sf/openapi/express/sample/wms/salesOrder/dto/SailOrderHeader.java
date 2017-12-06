// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SailOrderHeader.java

package com.sf.openapi.express.sample.wms.salesOrder.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class SailOrderHeader extends BaseEntity
{

    public SailOrderHeader()
    {
        warehouse = "CODE_CK001";
        orderType = "\u9500\u552E\u8BA2\u5355";
        carrier = "\u987A\u4E30\u901F\u8FD0";
        carrierService = "\u6807\u51C6\u5FEB\u9012";
        completeDelivery = "Y";
        paymentOfCharge = "\u5BC4\u4ED8";
        cod = "N";
        valueInsured = "N";
        returnReceiptService = "N";
        invoice = "N";
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

    public String getShopName()
    {
        return shopName;
    }

    public void setShopName(String shopName)
    {
        this.shopName = shopName;
    }

    public String getErpOrder()
    {
        return erpOrder;
    }

    public void setErpOrder(String erpOrder)
    {
        this.erpOrder = erpOrder;
    }

    public String getOrderType()
    {
        return orderType;
    }

    public void setOrderType(String orderType)
    {
        this.orderType = orderType;
    }

    public String getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate(String orderDate)
    {
        this.orderDate = orderDate;
    }

    public String getShipToName()
    {
        return shipToName;
    }

    public void setShipToName(String shipToName)
    {
        this.shipToName = shipToName;
    }

    public String getShipToAttentionTo()
    {
        return shipToAttentionTo;
    }

    public void setShipToAttentionTo(String shipToAttentionTo)
    {
        this.shipToAttentionTo = shipToAttentionTo;
    }

    public String getShipToCountry()
    {
        return shipToCountry;
    }

    public void setShipToCountry(String shipToCountry)
    {
        this.shipToCountry = shipToCountry;
    }

    public String getShipToProvince()
    {
        return shipToProvince;
    }

    public void setShipToProvince(String shipToProvince)
    {
        this.shipToProvince = shipToProvince;
    }

    public String getShipToCity()
    {
        return shipToCity;
    }

    public void setShipToCity(String shipToCity)
    {
        this.shipToCity = shipToCity;
    }

    public String getShipToArea()
    {
        return shipToArea;
    }

    public void setShipToArea(String shipToArea)
    {
        this.shipToArea = shipToArea;
    }

    public String getShipToAddress()
    {
        return shipToAddress;
    }

    public void setShipToAddress(String shipToAddress)
    {
        this.shipToAddress = shipToAddress;
    }

    public String getShipToPostalCode()
    {
        return shipToPostalCode;
    }

    public void setShipToPostalCode(String shipToPostalCode)
    {
        this.shipToPostalCode = shipToPostalCode;
    }

    public String getShipToPhoneNum()
    {
        return shipToPhoneNum;
    }

    public void setShipToPhoneNum(String shipToPhoneNum)
    {
        this.shipToPhoneNum = shipToPhoneNum;
    }

    public String getShipToTelNum()
    {
        return shipToTelNum;
    }

    public void setShipToTelNum(String shipToTelNum)
    {
        this.shipToTelNum = shipToTelNum;
    }

    public String getShipToEmailAddress()
    {
        return shipToEmailAddress;
    }

    public void setShipToEmailAddress(String shipToEmailAddress)
    {
        this.shipToEmailAddress = shipToEmailAddress;
    }

    public String getCarrier()
    {
        return carrier;
    }

    public void setCarrier(String carrier)
    {
        this.carrier = carrier;
    }

    public String getCarrierService()
    {
        return carrierService;
    }

    public void setCarrierService(String carrierService)
    {
        this.carrierService = carrierService;
    }

    public String getRouteNumbers()
    {
        return routeNumbers;
    }

    public void setRouteNumbers(String routeNumbers)
    {
        this.routeNumbers = routeNumbers;
    }

    public String getPackingNote()
    {
        return packingNote;
    }

    public void setPackingNote(String packingNote)
    {
        this.packingNote = packingNote;
    }

    public String getCompleteDelivery()
    {
        return completeDelivery;
    }

    public void setCompleteDelivery(String completeDelivery)
    {
        this.completeDelivery = completeDelivery;
    }

    public String getFreight()
    {
        return freight;
    }

    public void setFreight(String freight)
    {
        this.freight = freight;
    }

    public String getPaymentOfCharge()
    {
        return paymentOfCharge;
    }

    public void setPaymentOfCharge(String paymentOfCharge)
    {
        this.paymentOfCharge = paymentOfCharge;
    }

    public String getPaymentDistrict()
    {
        return paymentDistrict;
    }

    public void setPaymentDistrict(String paymentDistrict)
    {
        this.paymentDistrict = paymentDistrict;
    }

    public String getCod()
    {
        return cod;
    }

    public void setCod(String cod)
    {
        this.cod = cod;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getSelfPickup()
    {
        return selfPickup;
    }

    public void setSelfPickup(String selfPickup)
    {
        this.selfPickup = selfPickup;
    }

    public String getValueInsured()
    {
        return valueInsured;
    }

    public void setValueInsured(String valueInsured)
    {
        this.valueInsured = valueInsured;
    }

    public String getDeclaredValue()
    {
        return declaredValue;
    }

    public void setDeclaredValue(String declaredValue)
    {
        this.declaredValue = declaredValue;
    }

    public String getReturnReceiptService()
    {
        return returnReceiptService;
    }

    public void setReturnReceiptService(String returnReceiptService)
    {
        this.returnReceiptService = returnReceiptService;
    }

    public String getDeliveryDate()
    {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate)
    {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryRequested()
    {
        return deliveryRequested;
    }

    public void setDeliveryRequested(String deliveryRequested)
    {
        this.deliveryRequested = deliveryRequested;
    }

    public String getInvoice()
    {
        return invoice;
    }

    public void setInvoice(String invoice)
    {
        this.invoice = invoice;
    }

    public String getInvoiceType()
    {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType)
    {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceTitle()
    {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle)
    {
        this.invoiceTitle = invoiceTitle;
    }

    public String getInvoiceContent()
    {
        return invoiceContent;
    }

    public void setInvoiceContent(String invoiceContent)
    {
        this.invoiceContent = invoiceContent;
    }

    public String getOrderNote()
    {
        return orderNote;
    }

    public void setOrderNote(String orderNote)
    {
        this.orderNote = orderNote;
    }

    public String getCompanyNote()
    {
        return companyNote;
    }

    public void setCompanyNote(String companyNote)
    {
        this.companyNote = companyNote;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getOrderTotalAmount()
    {
        return orderTotalAmount;
    }

    public void setOrderTotalAmount(String orderTotalAmount)
    {
        this.orderTotalAmount = orderTotalAmount;
    }

    public String getOrderDiscount()
    {
        return orderDiscount;
    }

    public void setOrderDiscount(String orderDiscount)
    {
        this.orderDiscount = orderDiscount;
    }

    public String getBalanceAmount()
    {
        return balanceAmount;
    }

    public void setBalanceAmount(String balanceAmount)
    {
        this.balanceAmount = balanceAmount;
    }

    public String getCouponsAmount()
    {
        return couponsAmount;
    }

    public void setCouponsAmount(String couponsAmount)
    {
        this.couponsAmount = couponsAmount;
    }

    public String getGiftCardAmount()
    {
        return giftCardAmount;
    }

    public void setGiftCardAmount(String giftCardAmount)
    {
        this.giftCardAmount = giftCardAmount;
    }

    public String getOtherCharge()
    {
        return otherCharge;
    }

    public void setOtherCharge(String otherCharge)
    {
        this.otherCharge = otherCharge;
    }

    public String getActualAmount()
    {
        return actualAmount;
    }

    public void setActualAmount(String actualAmount)
    {
        this.actualAmount = actualAmount;
    }

    public String getCustomerPaymentMethod()
    {
        return customerPaymentMethod;
    }

    public void setCustomerPaymentMethod(String customerPaymentMethod)
    {
        this.customerPaymentMethod = customerPaymentMethod;
    }

    public String getMonthlyAccount()
    {
        return monthlyAccount;
    }

    public void setMonthlyAccount(String monthlyAccount)
    {
        this.monthlyAccount = monthlyAccount;
    }

    public String getFromFlag()
    {
        return fromFlag;
    }

    public void setFromFlag(String fromFlag)
    {
        this.fromFlag = fromFlag;
    }

    public String getIsEasy()
    {
        return isEasy;
    }

    public void setIsEasy(String isEasy)
    {
        this.isEasy = isEasy;
    }

    public String getIsAppointDelivery()
    {
        return isAppointDelivery;
    }

    public void setIsAppointDelivery(String isAppointDelivery)
    {
        this.isAppointDelivery = isAppointDelivery;
    }

    public String getAppointDeliveryStatus()
    {
        return appointDeliveryStatus;
    }

    public void setAppointDeliveryStatus(String appointDeliveryStatus)
    {
        this.appointDeliveryStatus = appointDeliveryStatus;
    }

    public String getAppointDeliveryRemark()
    {
        return appointDeliveryRemark;
    }

    public void setAppointDeliveryRemark(String appointDeliveryRemark)
    {
        this.appointDeliveryRemark = appointDeliveryRemark;
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

    public String getUserDef9()
    {
        return userDef9;
    }

    public void setUserDef9(String userDef9)
    {
        this.userDef9 = userDef9;
    }

    public String getUserDef10()
    {
        return userDef10;
    }

    public void setUserDef10(String userDef10)
    {
        this.userDef10 = userDef10;
    }

    public String getUserDef11()
    {
        return userDef11;
    }

    public void setUserDef11(String userDef11)
    {
        this.userDef11 = userDef11;
    }

    public String getUserDef12()
    {
        return userDef12;
    }

    public void setUserDef12(String userDef12)
    {
        this.userDef12 = userDef12;
    }

    public String getUserDef13()
    {
        return userDef13;
    }

    public void setUserDef13(String userDef13)
    {
        this.userDef13 = userDef13;
    }

    public String getUserDef14()
    {
        return userDef14;
    }

    public void setUserDef14(String userDef14)
    {
        this.userDef14 = userDef14;
    }

    public String getUserDef15()
    {
        return userDef15;
    }

    public void setUserDef15(String userDef15)
    {
        this.userDef15 = userDef15;
    }

    public String getUserDef16()
    {
        return userDef16;
    }

    public void setUserDef16(String userDef16)
    {
        this.userDef16 = userDef16;
    }

    public String getUserDef17()
    {
        return userDef17;
    }

    public void setUserDef17(String userDef17)
    {
        this.userDef17 = userDef17;
    }

    public String getUserDef18()
    {
        return userDef18;
    }

    public void setUserDef18(String userDef18)
    {
        this.userDef18 = userDef18;
    }

    public String getUserDef19()
    {
        return userDef19;
    }

    public void setUserDef19(String userDef19)
    {
        this.userDef19 = userDef19;
    }

    public String getUserDef20()
    {
        return userDef20;
    }

    public void setUserDef20(String userDef20)
    {
        this.userDef20 = userDef20;
    }

    public String getShipFromName()
    {
        return shipFromName;
    }

    public void setShipFromName(String shipFromName)
    {
        this.shipFromName = shipFromName;
    }

    public String getShipFromAttentionTo()
    {
        return shipFromAttentionTo;
    }

    public void setShipFromAttentionTo(String shipFromAttentionTo)
    {
        this.shipFromAttentionTo = shipFromAttentionTo;
    }

    public String getShipFromCountry()
    {
        return shipFromCountry;
    }

    public void setShipFromCountry(String shipFromCountry)
    {
        this.shipFromCountry = shipFromCountry;
    }

    public String getShipFromProvince()
    {
        return shipFromProvince;
    }

    public void setShipFromProvince(String shipFromProvince)
    {
        this.shipFromProvince = shipFromProvince;
    }

    public String getShipFromCity()
    {
        return shipFromCity;
    }

    public void setShipFromCity(String shipFromCity)
    {
        this.shipFromCity = shipFromCity;
    }

    public String getShipFromArea()
    {
        return shipFromArea;
    }

    public void setShipFromArea(String shipFromArea)
    {
        this.shipFromArea = shipFromArea;
    }

    public String getShipFromAddress()
    {
        return shipFromAddress;
    }

    public void setShipFromAddress(String shipFromAddress)
    {
        this.shipFromAddress = shipFromAddress;
    }

    public String getShipFromPostalCode()
    {
        return shipFromPostalCode;
    }

    public void setShipFromPostalCode(String shipFromPostalCode)
    {
        this.shipFromPostalCode = shipFromPostalCode;
    }

    public String getShipFromPhoneNum()
    {
        return shipFromPhoneNum;
    }

    public void setShipFromPhoneNum(String shipFromPhoneNum)
    {
        this.shipFromPhoneNum = shipFromPhoneNum;
    }

    public String getShipFromTelNum()
    {
        return shipFromTelNum;
    }

    public void setShipFromTelNum(String shipFromTelNum)
    {
        this.shipFromTelNum = shipFromTelNum;
    }

    public String getShipFromEmailAddress()
    {
        return shipFromEmailAddress;
    }

    public void setShipFromEmailAddress(String shipFromEmailAddress)
    {
        this.shipFromEmailAddress = shipFromEmailAddress;
    }

    private String company;
    private String warehouse;
    private String shopName;
    private String erpOrder;
    private String orderType;
    private String orderDate;
    private String shipToName;
    private String shipToAttentionTo;
    private String shipToCountry;
    private String shipToProvince;
    private String shipToCity;
    private String shipToArea;
    private String shipToAddress;
    private String shipToPostalCode;
    private String shipToPhoneNum;
    private String shipToTelNum;
    private String shipToEmailAddress;
    private String carrier;
    private String carrierService;
    private String routeNumbers;
    private String packingNote;
    private String completeDelivery;
    private String freight;
    private String paymentOfCharge;
    private String paymentDistrict;
    private String cod;
    private String amount;
    private String selfPickup;
    private String valueInsured;
    private String declaredValue;
    private String returnReceiptService;
    private String deliveryDate;
    private String deliveryRequested;
    private String invoice;
    private String invoiceType;
    private String invoiceTitle;
    private String invoiceContent;
    private String orderNote;
    private String companyNote;
    private String priority;
    private String orderTotalAmount;
    private String orderDiscount;
    private String balanceAmount;
    private String couponsAmount;
    private String giftCardAmount;
    private String otherCharge;
    private String actualAmount;
    private String customerPaymentMethod;
    private String monthlyAccount;
    private String fromFlag;
    private String isEasy;
    private String isAppointDelivery;
    private String appointDeliveryStatus;
    private String appointDeliveryRemark;
    private String userDef1;
    private String userDef2;
    private String userDef3;
    private String userDef4;
    private String userDef5;
    private String userDef6;
    private String userDef7;
    private String userDef8;
    private String userDef9;
    private String userDef10;
    private String userDef11;
    private String userDef12;
    private String userDef13;
    private String userDef14;
    private String userDef15;
    private String userDef16;
    private String userDef17;
    private String userDef18;
    private String userDef19;
    private String userDef20;
    private String shipFromName;
    private String shipFromAttentionTo;
    private String shipFromCountry;
    private String shipFromProvince;
    private String shipFromCity;
    private String shipFromArea;
    private String shipFromAddress;
    private String shipFromPostalCode;
    private String shipFromPhoneNum;
    private String shipFromTelNum;
    private String shipFromEmailAddress;
}
