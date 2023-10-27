package com.security.model.validation.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.creators.CreateConsentAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.creators.FieldCreator;

@Aspect
public class CreateConsentAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateConsentAnnotation)")
	void function() {}
	@Around("function()")
	public Object createConsent(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object ret = thisJoinPoint.proceed(args);
		Object obj = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreateConsentAnnotation createConsent = method.getAnnotation(CreateConsentAnnotation.class);
		if(createConsent == null)
		{
			System.out.println("There is no create consent statement annotation");
			return ret;
		}
		Object retFromObj = FieldCreator.getObjectToReadFrom(ret, obj, createConsent.createdObjectLocation(), createConsent.name(), thisJoinPoint);
		if(retFromObj == null)
		{
			System.out.println("Read from object is null - CreateConsentAspect");
			return ret;
		}
		Class<? extends Object> retClass = retFromObj.getClass();
		PaperAnnotation paper = retClass.getAnnotation(PaperAnnotation.class);
		
		if(paper == null)
		{
			System.out.println("There is no paper annotation");
			return ret;
		}

		try 
		{
			System.out.println("DocumentId: " + FieldCreator.getFieldValue(paper.id(), retFromObj, retClass));
			System.out.println("Type " + createConsent.consentType());
			System.out.println("Format " + createConsent.consentFormat());
			System.out.println("StartDate: " + FieldCreator.getFieldValue(paper.startDate(), retFromObj, retClass));
			System.out.println("Location: " + FieldCreator.getFieldValue(paper.location(), retFromObj, retClass));
			if(paper.terminantionExplanation() != Constants.Empty)
			{
				
			}
			if(paper.terminantionDate() != Constants.Empty)
			{
				
			}
			if(paper.description() != Constants.Empty)
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

