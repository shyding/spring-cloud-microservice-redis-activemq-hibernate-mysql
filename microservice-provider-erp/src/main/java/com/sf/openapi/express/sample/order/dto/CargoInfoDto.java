// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CargoInfoDto.java

package com.sf.openapi.express.sample.order.dto;

import java.math.BigDecimal;

public class CargoInfoDto
{

    public CargoInfoDto()
    {
        parcelQuantity = Integer.valueOf(1);
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public Integer getCargoIndex()
    {
        return cargoIndex;
    }

    public void setCargoIndex(Integer cargoIndex)
    {
        this.cargoIndex = cargoIndex;
    }

    public String getCargo()
    {
        return cargo;
    }

    public void setCargo(String cargo)
    {
        this.cargo = cargo;
    }

    public String getCargoUnit()
    {
        return cargoUnit;
    }

    public void setCargoUnit(String cargoUnit)
    {
        this.cargoUnit = cargoUnit;
    }

    public String getCargoCount()
    {
        return cargoCount;
    }

    public void setCargoCount(String cargoCount)
    {
        this.cargoCount = cargoCount;
    }

    public String getCargoWeight()
    {
        return cargoWeight;
    }

    public void setCargoWeight(String cargoWeight)
    {
        this.cargoWeight = cargoWeight;
    }

    public String getCargoAmount()
    {
        return cargoAmount;
    }

    public void setCargoAmount(String cargoAmount)
    {
        this.cargoAmount = cargoAmount;
    }

    public BigDecimal getCargoTotalWeight()
    {
        return cargoTotalWeight;
    }

    public void setCargoTotalWeight(BigDecimal cargoTotalWeight)
    {
        this.cargoTotalWeight = cargoTotalWeight;
    }

    public Integer getParcelQuantity()
    {
        return parcelQuantity;
    }

    public void setParcelQuantity(Integer parcelQuantity)
    {
        this.parcelQuantity = parcelQuantity;
    }

    private String orderId;
    private Integer cargoIndex;
    private String cargo;
    private String cargoUnit;
    private String cargoCount;
    private String cargoWeight;
    private String cargoAmount;
    private Integer parcelQuantity;
    private BigDecimal cargoTotalWeight;
}
