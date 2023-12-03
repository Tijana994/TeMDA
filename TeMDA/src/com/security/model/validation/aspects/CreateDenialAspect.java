package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.DenialAnnotation;
import com.security.model.validation.annotations.creators.CreateDenialAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;

import utility.PrivacyModelRepository;

@Aspect
public class CreateDenialAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateDenialAnnotation)")
	void function() {}
	@Around("function()")
	public Object createDenial(ProceedingJoinPoint thisJoinPoint) throws Throwable {
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
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var denialObject = repo.getFactory().createDenial();
			denialObject.setName((String)FieldFinder.getFieldValue(denial.id(), retFromObj, retClass));
			denialObject.setWhen((Date)FieldFinder.getFieldValue(denial.when(), retFromObj, retClass));
			if(denial.reason() != Constants.Empty)
			{
				denialObject.setReason((String)FieldFinder.getFieldValue(denial.reason(), retFromObj, retClass));
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
			model.getAllDenials().add(denialObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return ret;
	}
}

