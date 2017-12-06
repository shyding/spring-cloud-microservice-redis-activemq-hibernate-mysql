// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CargoInfoDto.java

package com.sf.openapi.express.sample.waybill.dto;

import java.math.BigDecimal;

public class CargoInfoDto
{

    public CargoInfoDto()
    {
    }

    public String getCargo()
    {
        return cargo;
    }

    public Integer getParcelQuantity()
    {
        return parcelQuantity;
    }

    public Integer getCargoCount()
    {
        return cargoCount;
    }

    public String getCargoUnit()
    {
        return cargoUnit;
    }

    public BigDecimal getCargoWeight()
    {
        return cargoWeight;
    }

    public BigDecimal getCargoAmount()
    {
        return cargoAmount;
    }

    public BigDecimal getCargoTotalWeight()
    {
        return cargoTotalWeight;
    }

    public void setCargo(String cargo)
    {
        this.cargo = cargo;
    }

    public void setParcelQuantity(Integer parcelQuantity)
    {
        this.parcelQuantity = parcelQuantity;
    }

    public void setCargoCount(Integer cargoCount)
    {
        this.cargoCount = cargoCount;
    }

    public void setCargoUnit(String cargoUnit)
    {
        this.cargoUnit = cargoUnit;
    }

    public void setCargoWeight(BigDecimal cargoWeight)
    {
        this.cargoWeight = cargoWeight;
    }

    public void setCargoAmount(BigDecimal cargoAmount)
    {
        this.cargoAmount = cargoAmount;
    }

    public void setCargoTotalWeight(BigDecimal cargoTotalWeight)
    {
        this.cargoTotalWeight = cargoTotalWeight;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getSku()
    {
        return sku;
    }

    public void setSku(String sku)
    {
        this.sku = sku;
    }

    private String cargo;
    private Integer parcelQuantity;
    private Integer cargoCount;
    private String cargoUnit;
    private BigDecimal cargoWeight;
    private BigDecimal cargoAmount;
    private BigDecimal cargoTotalWeight;
    private String remark;
    private String sku;
}
