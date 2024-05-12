package com.security.model.validation.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class BaseAspect {
	
	protected Object[] args;
	protected Object returnedObject;
	protected Object originalObject;
	protected Class<? extends Object> originalObjectClass;
	protected MethodSignature signature;
	protected Method method;
	
	public void SetUp(ProceedingJoinPoint thisJoinPoint) throws Throwable 
	{
		args = thisJoinPoint.getArgs();
		returnedObject = thisJoinPoint.proceed(args);
		originalObject = thisJoinPoint.getThis();
		originalObjectClass = originalObject.getClass();
	    signature = (MethodSignature) thisJoinPoint.getSignature();
	    method = signature.getMethod();
	}

}
