// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LogUtils.java

package com.sf.aries.core.common.util;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;

public class LogUtils
{

    public LogUtils()
    {
    }

    public static final String toString(Object o)
    {
        return ToStringBuilder.reflectionToString(o);
    }

    public static final String listToString(List os)
    {
        StringBuilder sb = new StringBuilder();
        if(os == null || os.size() == 0)
        {
            sb.append(" list is null or size is 0");
        } else
        {
            sb.append("{");
            for(Iterator i$ = os.iterator(); i$.hasNext(); sb.append("]"))
            {
                Object o = i$.next();
                sb.append("[");
                sb.append(sb.append(toString(o)));
            }

            sb.append("}");
        }
        return sb.toString();
    }
}
