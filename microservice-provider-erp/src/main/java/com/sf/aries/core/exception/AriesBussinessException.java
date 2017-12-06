// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AriesBussinessException.java

package com.sf.aries.core.exception;

import org.springframework.core.NestedCheckedException;

// Referenced classes of package com.sf.aries.core.exception:
//            AriesException

public class AriesBussinessException extends NestedCheckedException
    implements AriesException
{

    public AriesBussinessException(String code)
    {
        super(code);
        this.code = code;
    }

    public AriesBussinessException(String message, Throwable throwable)
    {
        super(message, throwable);
        code = message;
        this.message = message;
    }

    public AriesBussinessException(String code, String message)
    {
        super(message);
        this.code = code;
        this.message = message;
    }

    public AriesBussinessException(String code, String message, Throwable throwable)
    {
        super(message, throwable);
        this.code = code;
        this.message = message;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    private static final long serialVersionUID = 0x3a322e24e04922f0L;
    protected String code;
    protected String message;
}
