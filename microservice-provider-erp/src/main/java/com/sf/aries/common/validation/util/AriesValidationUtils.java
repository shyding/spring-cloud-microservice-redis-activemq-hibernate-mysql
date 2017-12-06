// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AriesValidationUtils.java

package com.sf.aries.common.validation.util;

import java.util.Iterator;
import java.util.List;
import org.springframework.validation.*;

public class AriesValidationUtils
{

    public AriesValidationUtils()
    {
    }

    public static final String validation(Object o, Validator validator)
    {
        BindingResult result = new BeanPropertyBindingResult(o, o.toString());
        validator.validate(o, result);
        StringBuilder sb = new StringBuilder();
        if(result.getAllErrors().size() > 0)
        {
            for(Iterator iterator = result.getAllErrors().iterator(); iterator.hasNext();)
            {
                ObjectError e = (ObjectError)iterator.next();
                if(e instanceof FieldError)
                {
                    sb.append(((FieldError)e).getField());
                    sb.append(":");
                    sb.append(e.getDefaultMessage());
                    sb.append(",");
                }
            }

            sb = sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static final String validation(Object os[], Validator validator)
    {
        StringBuilder sb = new StringBuilder();
        Object aobj[];
        int j = (aobj = os).length;
        for(int i = 0; i < j; i++)
        {
            Object o = aobj[i];
            sb.append(o.getClass().getName());
            sb.append("{");
            validation(o, validator);
            sb.append("}");
        }

        return sb.toString();
    }
}
