// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BaseEntity.java

package com.sf.openapi.common.entity;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

public class BaseEntity
    implements Serializable
{

    public BaseEntity()
    {
    }

    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    private static final long serialVersionUID = 0xa40649797d8860f0L;
}
