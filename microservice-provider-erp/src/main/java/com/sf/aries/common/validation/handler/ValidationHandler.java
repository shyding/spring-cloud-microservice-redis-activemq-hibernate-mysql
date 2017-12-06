// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ValidationHandler.java

package com.sf.aries.common.validation.handler;

import com.sf.aries.common.validation.util.AriesValidationUtils;
import org.springframework.validation.Validator;

public class ValidationHandler
{

    public ValidationHandler()
    {
    }

    public String validator(Object o)
    {
        return AriesValidationUtils.validation(o, validator);
    }

    private Validator validator;
}
