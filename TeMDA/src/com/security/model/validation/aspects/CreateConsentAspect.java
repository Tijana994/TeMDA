package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.creators.CreateConsentAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;

import utility.PrivacyModelRepository;

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
		Object retFromObj = FieldFinder.getObjectToReadFrom(ret, obj, createConsent.createdObjectLocation(), createConsent.name(), thisJoinPoint);
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
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var consentObject = repo.getFactory().createConsent();
			consentObject.setName((String)FieldFinder.getFieldValue(paper.id(), retFromObj, retClass));
			consentObject.setType(createConsent.consentType());
			consentObject.setFormat(createConsent.consentFormat());
			consentObject.setStartDate((Date)FieldFinder.getFieldValue(paper.startDate(), retFromObj, retClass));
			consentObject.setLocation((String)FieldFinder.getFieldValue(paper.location(), retFromObj, retClass));
			if(!paper.terminantionExplanation().equals(Constants.Empty))
			{
				consentObject.setTerminationExplanation((String)FieldFinder.getFieldValue(paper.terminantionExplanation(), retFromObj, retClass));
			}
			if(!paper.terminantionDate().equals(Constants.Empty))
			{
				consentObject.setTerminationDate((Date)FieldFinder.getFieldValue(paper.terminantionDate(), retFromObj, retClass));
			}
			if(!paper.description().equals(Constants.Empty))
			{
				consentObject.setDescription((String)FieldFinder.getFieldValue(paper.description(), retFromObj, retClass));
			}
			model.getAllConsents().add(consentObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return ret;
	}
}

