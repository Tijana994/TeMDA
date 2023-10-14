package com.security.model.validation.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.LocationAnnotation;
import com.security.model.validation.annotations.creators.CreateLocationAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.creators.FieldCreator;

@Aspect
public class CreateLocationAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateLocationAnnotation)")
	void function() {}
	@Around("function()")
	public Object aroundOkoF(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object ret = thisJoinPoint.proceed(args);
		Object obj = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
		CreateLocationAnnotation createLocation = method.getAnnotation(CreateLocationAnnotation.class);
		if(createLocation == null)
		{
			System.out.println("There is no create location statement annotation");
			return ret;
		}
		Object retFromObj = FieldCreator.getObjectToReadFrom(ret, obj, createLocation.createdObjectLocation(), createLocation.name(), thisJoinPoint);
		if(retFromObj == null)
		{
			System.out.println("Read from object is null - CreateLocationAspect");
			return ret;
		}
		Class<? extends Object> retClass = retFromObj.getClass();
		LocationAnnotation location = retClass.getAnnotation(LocationAnnotation.class);
		
		if(location == null)
		{
			System.out.println("There is no location annotation");
			return ret;
		}
		
		try 
		{
			System.out.println("LocationId: " + FieldCreator.getFieldValue(location.id(), retFromObj, retClass));
			System.out.println("Type " + createLocation.locationType());
			if(!location.parent().equals(Constants.Undefined)) 
			{
				
			}
			if(!location.parentId().equals(Constants.Undefined))
			{
				
			}
			if(!location.childrens().equals(Constants.Undefined))
			{
				
			}
			if(!location.childrensIds().equals(Constants.Undefined))
			{
				
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return ret;
	}
}
