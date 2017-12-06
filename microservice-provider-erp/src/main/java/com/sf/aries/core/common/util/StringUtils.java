// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StringUtils.java

package com.sf.aries.core.common.util;


public class StringUtils
{

    public StringUtils()
    {
    }

    public static String stringFill(String source, int fillLength, char fillChar, boolean isLeftFill)
    {
        if(source == null || source.length() >= fillLength)
            return source;
        StringBuilder result = new StringBuilder(fillLength);
        int len = fillLength - source.length();
        if(isLeftFill)
        {
            for(; len > 0; len--)
                result.append(fillChar);

            result.append(source);
        } else
        {
            result.append(source);
            for(; len > 0; len--)
                result.append(fillChar);

        }
        return result.toString();
    }
}
