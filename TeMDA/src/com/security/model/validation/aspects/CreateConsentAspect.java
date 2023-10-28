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
			var name = (String)FieldFinder.getFieldValue(paper.id(), retFromObj, retClass);
			consentObject.setName(name);
			consentObject.setType(createConsent.consentType());
			consentObject.setFormat(createConsent.consentFormat());
			var startDate = (Date)FieldFinder.getFieldValue(paper.startDate(), retFromObj, retClass);
			consentObject.setStartDate(startDate);
			var location = (String)FieldFinder.getFieldValue(paper.location(), retFromObj, retClass);
			consentObject.setLocation(location);
			if(!paper.terminantionExplanation().equals(Constants.Empty))
			{
				var terminantionExplanation = (String)FieldFinder.getFieldValue(paper.terminantionExplanation(), retFromObj, retClass);
				consentObject.setTerminationExplanation(terminantionExplanation);
			}
			if(!paper.terminantionDate().equals(Constants.Empty))
			{
				var terminantionDate = (Date)FieldFinder.getFieldValue(paper.terminantionDate(), retFromObj, retClass);
				consentObject.setTerminationDate(terminantionDate);
			}
			if(!paper.description().equals(Constants.Empty))
			{
				var description = (String)FieldFinder.getFieldValue(paper.description(), retFromObj, retClass);
				consentObject.setDescription(description);
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

