package com.security.model.validation.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.DenialAnnotation;
import com.security.model.validation.annotations.creators.CreateDenialAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;

@Aspect
public class CreateDenialAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateDenialAnnotation)")
	void function() {}
	@Around("function()")
	public Object aroundOkoF(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object ret = thisJoinPoint.proceed(args);
		Object obj = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreateDenialAnnotation createDenial = method.getAnnotation(CreateDenialAnnotation.class);
		if(createDenial == null)
		{
			System.out.println("There is no create denial annotation");
			return ret;
		}
		Object retFromObj = FieldFinder.getObjectToReadFrom(ret, obj, createDenial.createdObjectLocation(), createDenial.name(), thisJoinPoint);
		if(retFromObj == null)
		{
			System.out.println("Read from object is null - CreateDenialAspect");
			return ret;
		}
		Class<? extends Object> retClass = retFromObj.getClass();
		DenialAnnotation denial = retClass.getAnnotation(DenialAnnotation.class);
		
		if(denial == null)
		{
			System.out.println("There is no denial annotation");
			return ret;
		}

		try 
		{
			System.out.println("DenialId: " + FieldFinder.getFieldValue(denial.id(), retFromObj, retClass));
			System.out.println("Date: " + FieldFinder.getFieldValue(denial.when(), retFromObj, retClass));
			
			if(denial.reason() != Constants.Empty)
			{
				System.out.println("Reason: " + FieldFinder.getFieldValue(denial.reason(), retFromObj, retClass));
			}
			if(createDenial.basedOnStatemets() != Constants.Empty)
			{
				
			}
			if(createDenial.basedOnStatemetsIds() != Constants.Empty)
			{
				
			}
			if(createDenial.forComplaint() != Constants.Empty)
			{
				
			}
			if(createDenial.forComplaintId() != Constants.Empty)
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

