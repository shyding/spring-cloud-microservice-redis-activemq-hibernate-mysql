// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BeanValidator.java

package com.sf.aries.common.validation.annotation;

import java.lang.annotation.Annotation;

public interface BeanValidator
    extends Annotation
{

    public abstract boolean isthrowException();
}
