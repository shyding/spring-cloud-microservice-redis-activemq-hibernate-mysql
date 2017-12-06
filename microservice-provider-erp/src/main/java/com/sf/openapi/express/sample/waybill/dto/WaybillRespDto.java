// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   WaybillRespDto.java

package com.sf.openapi.express.sample.waybill.dto;

import com.sf.openapi.common.entity.BaseEntity;

public class WaybillRespDto extends BaseEntity
{

    public WaybillRespDto()
    {
    }

    public String[] getImages()
    {
        return images;
    }

    public void setImages(String images[])
    {
        this.images = images;
    }

    public int getPageWidth()
    {
        return pageWidth;
    }

    public void setPageWidth(int pageWidth)
    {
        this.pageWidth = pageWidth;
    }

    public int getPageHeight()
    {
        return pageHeight;
    }

    public void setPageHeight(int pageHeight)
    {
        this.pageHeight = pageHeight;
    }

    public int getOrientation()
    {
        return orientation;
    }

    public void setOrientation(int orientation)
    {
        this.orientation = orientation;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getResultMessage()
    {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage)
    {
        this.resultMessage = resultMessage;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    private String images[];
    private int pageWidth;
    private int pageHeight;
    private int orientation;
    private int type;
    private String resultMessage;
    private String imagePath;
}
