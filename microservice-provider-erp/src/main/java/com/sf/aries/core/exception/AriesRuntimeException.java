// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AriesRuntimeException.java

package com.sf.aries.core.exception;

import org.springframework.core.NestedRuntimeException;

// Referenced classes of package com.sf.aries.core.exception:
//            AriesException

public class AriesRuntimeException extends NestedRuntimeException
    implements AriesException
{

    public AriesRuntimeException(String code)
    {
        super(code);
        this.code = code;
    }

    public AriesRuntimeException(String message, Throwable throwable)
    {
        super(message, throwable);
        code = code;
        this.message = message;
    }

    public AriesRuntimeException(String code, String message, Throwable throwable)
    {
        super(message, throwable);
        this.code = code;
        this.message = message;
    }

    public String getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    private static final long serialVersionUID = 0x2ca1840617be2c81L;
    private String code;
    private String message;
}
