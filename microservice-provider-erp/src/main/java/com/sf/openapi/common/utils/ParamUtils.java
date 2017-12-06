// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ParamUtils.java

package com.sf.openapi.common.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamUtils
{

    public ParamUtils()
    {
    }

    public static String initParamToURL(String url, Map paramMap)
    {
        return initCommonParam(initPathParam(url, paramMap), paramMap);
    }

    private static String initPathParam(String url, Map paramMap)
    {
        if(url != null && !"".equals(url) && paramMap != null && !paramMap.isEmpty())
        {
            Iterator it = paramMap.keySet().iterator();
            String usedParam[] = new String[paramMap.size()];
            int index = 0;
            while(it.hasNext()) 
            {
                String key = (String)it.next();
                Pattern pattern = Pattern.compile((new StringBuilder("\\{[")).append(key).append("]+\\}").toString());
                Matcher matcher = pattern.matcher(url);
                if(matcher.find())
                {
                    url = matcher.replaceAll(paramMap.get(key) != null ? (String)paramMap.get(key) : "");
                    usedParam[index++] = key;
                }
            }
            String as[];
            int j = (as = usedParam).length;
            for(int i = 0; i < j; i++)
            {
                String usedKey = as[i];
                if(usedKey != null && !"".equals(usedKey))
                    paramMap.remove(usedKey);
            }

        }
        return url;
    }

    private static String initCommonParam(String url, Map paramMap)
    {
        if(url != null && !"".equals(url) && paramMap != null && !paramMap.isEmpty())
        {
            Iterator it = paramMap.keySet().iterator();
            String paramName;
            String paramValue;
            for(url = (new StringBuilder(String.valueOf(url))).append("?").toString(); it.hasNext(); url = (new StringBuilder(String.valueOf(url))).append(paramName).append("=").append(paramValue).append("&").toString())
            {
                paramName = (String)it.next();
                paramValue = (String)paramMap.get(paramName);
            }

            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
