package com.security.model.validation.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.helpers.*;
import com.security.model.validation.helpers.interfaces.ILogger;

public class BaseAspect {
	
	protected Object[] args;
	protected Object returnedObject;
	protected Object originalObject;
	protected Class<? extends Object> originalObjectClass;
	protected MethodSignature signature;
	protected Method method;
	protected ILogger Logger;
	protected FieldFinder FieldFinder;
	protected ObjectManager ObjectManager;
	protected ReadTypeByAttribute ReadTypeByAttribute;
	
	private ObjectFinder objectFinder;
	
	public void SetUp(ProceedingJoinPoint thisJoinPoint) throws Throwable 
	{
		args = thisJoinPoint.getArgs();
		returnedObject = thisJoinPoint.proceed(args);
		originalObject = thisJoinPoint.getThis();
		originalObjectClass = originalObject.getClass();
	    signature = (MethodSignature) thisJoinPoint.getSignature();
	    method = signature.getMethod();
	    Logger = new ConsoleLogger();
	    FieldFinder = new FieldFinder(Logger);
	    objectFinder = new ObjectFinder(Logger);
	    ReadTypeByAttribute = new ReadTypeByAttribute(Logger, FieldFinder, objectFinder);
	    ObjectManager = new ObjectManager(FieldFinder, ReadTypeByAttribute, objectFinder);
	}

}
