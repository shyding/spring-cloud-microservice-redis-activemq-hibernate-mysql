// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BeanValidatorAspect.java

package com.sf.aries.common.validation.aspect;

import com.sf.aries.common.validation.annotation.BeanValidator;
import com.sf.aries.common.validation.util.AriesValidationUtils;
import com.sf.aries.core.common.util.ClassUtils;
import com.sf.aries.core.exception.AriesBussinessException;
import com.sf.aries.core.exception.AriesRuntimeException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;

public class BeanValidatorAspect
{

    public BeanValidatorAspect()
    {
    }

    public Object invoker(ProceedingJoinPoint jp)
    {
        Object targetObj = jp.getTarget();
        Class c = targetObj.getClass();
        String methodName = jp.getSignature().getName();
        System.out.println("=============");
        try
        {
            Method method = c.getMethod(methodName, ClassUtils.getArgsTypes(jp.getArgs()));
            if(method.isAnnotationPresent(BeanValidator.class)) {
                BeanValidator beanValidator = this.getBeanValidatorAnnotation(method);
                String message = AriesValidationUtils.validation(jp.getArgs(), this.validator);
                if(StringUtils.hasText(message) && beanValidator.isthrowException()) {
                    throw new AriesBussinessException("EX_CODE_500", message);
                }
            }

            return jp.proceed();
        }
        catch(Exception e)
        {
            throw new AriesRuntimeException("BeanValidator\u5F02\u5E38", e);
        }
        catch(Throwable e)
        {
            throw new AriesRuntimeException("BeanValidator\u5F02\u5E38", e);
        }
    }

    private BeanValidator getBeanValidatorAnnotation(Method method)
    {
        java.lang.annotation.Annotation annotations[] = method.getAnnotations();
        java.lang.annotation.Annotation aannotation[];
        int j = (aannotation = annotations).length;
        for(int i = 0; i < j; i++)
        {
            java.lang.annotation.Annotation annotation = aannotation[i];
            if(annotation instanceof BeanValidator)
                return (BeanValidator)annotation;
        }

        throw new AriesRuntimeException("\u6CA1\u80FD\u83B7\u53D6\u8FDC\u7A0B\u8C03\u7528annotation");
    }

    private Validator validator;
}
