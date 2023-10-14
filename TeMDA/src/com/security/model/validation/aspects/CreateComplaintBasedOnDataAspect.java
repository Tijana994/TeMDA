package com.security.model.validation.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.ComplaintAnnotation;
import com.security.model.validation.annotations.creators.CreateComplaintBasedOnDataAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.creators.FieldCreator;

@Aspect
public class CreateComplaintBasedOnDataAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateComplaintBasedOnDataAnnotation)")
	void function() {}
	@Around("function()")
	public Object aroundOkoF(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object ret = thisJoinPoint.proceed(args);
		Object obj = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreateComplaintBasedOnDataAnnotation createComplaintBasedOnData = method.getAnnotation(CreateComplaintBasedOnDataAnnotation.class);
		if(createComplaintBasedOnData == null)
		{
			System.out.println("There is no create complaint based on data annotation");
			return ret;
		}
		Object retFromObj = FieldCreator.getObjectToReadFrom(ret, obj, createComplaintBasedOnData.createdObjectLocation(), createComplaintBasedOnData.name(), thisJoinPoint);
		if(retFromObj == null)
		{
			System.out.println("Read from object is null - CreateComplaintBasedOnDataAspect");
			return ret;
		}
		Class<? extends Object> retClass = retFromObj.getClass();
		ComplaintAnnotation complaint = retClass.getAnnotation(ComplaintAnnotation.class);
		
		if(complaint == null)
		{
			System.out.println("There is no complaint annotation");
			return ret;
		}
		
		try 
		{
			System.out.println("ComplaintId: " + FieldCreator.getFieldValue(complaint.id(), retFromObj, retClass));
			System.out.println("Date: " + FieldCreator.getFieldValue(complaint.when(), retFromObj, retClass));
			
			if(complaint.reason() != Constants.Empty)
			{
				System.out.println("Reason: " + FieldCreator.getFieldValue(complaint.reason(), retFromObj, retClass));
			}
			System.out.println("Type: " + createComplaintBasedOnData.type());
			if(createComplaintBasedOnData.subjects() != Constants.Empty)
			{
				
			}
			if(createComplaintBasedOnData.subjectsIds() != Constants.Empty)
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