// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OrderReqDto.java

package com.sf.openapi.express.sample.order.dto;

import com.sf.aries.core.model.BaseEntity;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.sf.openapi.express.sample.order.dto:
//            DeliverConsigneeInfoDto, CargoInfoDto

public class OrderReqDto extends BaseEntity
{

    public OrderReqDto()
    {
        needReturnTrackingNo = 0;
        isDoCall = 0;
        isGenBillNo = 1;
        isGenEletricPic = 1;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public short getExpressType()
    {
        return expressType;
    }

    public void setExpressType(short expressType)
    {
        this.expressType = expressType;
    }

    public String getPayArea()
    {
        return payArea;
    }

    public void setPayArea(String payArea)
    {
        this.payArea = payArea;
    }

    public short getPayMethod()
    {
        return payMethod;
    }

    public void setPayMethod(short payMethod)
    {
        this.payMethod = payMethod;
    }

    public String getCustId()
    {
        return custId;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public String getSendStartTime()
    {
        return sendStartTime;
    }

    public void setSendStartTime(String sendStartTime)
    {
        this.sendStartTime = sendStartTime;
    }

    public short getNeedReturnTrackingNo()
    {
        return needReturnTrackingNo;
    }

    public void setNeedReturnTrackingNo(short needReturnTrackingNo)
    {
        this.needReturnTrackingNo = needReturnTrackingNo;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public short getIsDoCall()
    {
        return isDoCall;
    }

    public void setIsDoCall(short isDoCall)
    {
        this.isDoCall = isDoCall;
    }

    public short getIsGenBillNo()
    {
        return isGenBillNo;
    }

    public void setIsGenBillNo(short isGenBillNo)
    {
        this.isGenBillNo = isGenBillNo;
    }

    public short getIsGenEletricPic()
    {
        return isGenEletricPic;
    }

    public void setIsGenEletricPic(short isGenEletricPic)
    {
        this.isGenEletricPic = isGenEletricPic;
    }

    public DeliverConsigneeInfoDto getDeliverInfo()
    {
        return deliverInfo;
    }

    public void setDeliverInfo(DeliverConsigneeInfoDto deliverInfo)
    {
        this.deliverInfo = deliverInfo;
    }

    public DeliverConsigneeInfoDto getConsigneeInfo()
    {
        return consigneeInfo;
    }

    public void setConsigneeInfo(DeliverConsigneeInfoDto consigneeInfo)
    {
        this.consigneeInfo = consigneeInfo;
    }

    public CargoInfoDto getCargoInfo()
    {
        return cargoInfo;
    }

    public void setCargoInfo(CargoInfoDto cargoInfo)
    {
        this.cargoInfo = cargoInfo;
    }

    public List getAddedServices()
    {
        if(addedServices == null)
            return new ArrayList();
        else
            return addedServices;
    }

    public void setAddedServices(List addedServices)
    {
        this.addedServices = addedServices;
    }

    private static final long serialVersionUID = 0xa75ca9701f41011bL;
    private String orderId;
    private short expressType;
    private short payMethod;
    private String payArea;
    private String custId;
    private String sendStartTime;
    private short needReturnTrackingNo;
    private String remark;
    private short isDoCall;
    private short isGenBillNo;
    private short isGenEletricPic;
    private DeliverConsigneeInfoDto deliverInfo;
    private DeliverConsigneeInfoDto consigneeInfo;
    private CargoInfoDto cargoInfo;
    private List addedServices;
}
