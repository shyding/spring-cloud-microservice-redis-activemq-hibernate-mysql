// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ClassUtils.java

package com.sf.aries.core.common.util;


public class ClassUtils
{

    public ClassUtils()
    {
    }

    public static Class[] getArgsTypes(Object args[])
    {
        Class argsTypes[] = new Class[args.length];
        for(int i = 0; i < args.length; i++)
            argsTypes[i] = args[i].getClass();

        return argsTypes;
    }
}
