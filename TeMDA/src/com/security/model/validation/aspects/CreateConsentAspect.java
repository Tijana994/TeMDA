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
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class CreateConsentAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateConsentAnnotation)")
	void function() {}
	@Around("function()")
	public Object createConsent(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object returnedObject = thisJoinPoint.proceed(args);
		Object originalObject = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreateConsentAnnotation createConsent = method.getAnnotation(CreateConsentAnnotation.class);
		if(createConsent == null)
		{
			System.out.println("There is no create consent statement annotation");
			return returnedObject;
		}
		CreationModel createdObjectModel = new CreationModel(returnedObject, thisJoinPoint, createConsent.createdObjectLocation(), ParametersObjectsLocation.Property);
		createdObjectModel.setObject(FieldFinder.getCreatedObjectToReadFrom(createdObjectModel, originalObject, createConsent.name()));
		if(createdObjectModel.getObject() == null)
		{
			System.out.println("Read from object is null - CreateConsentAspect");
			return returnedObject;
		}
		PaperAnnotation paper = createdObjectModel.getObjectClass().getAnnotation(PaperAnnotation.class);
		if(paper == null)
		{
			System.out.println("There is no paper annotation");
			return returnedObject;
		}

		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var consentObject = repo.getFactory().createConsent();
			consentObject.setName((String)FieldFinder.getFieldValue(paper.id(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			consentObject.setType(createConsent.consentType());
			consentObject.setFormat(createConsent.consentFormat());
			consentObject.setStartDate((Date)FieldFinder.getFieldValue(paper.startDate(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			consentObject.setLocation((String)FieldFinder.getFieldValue(paper.location(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			if(!paper.terminantionExplanation().equals(Constants.Empty))
			{
				consentObject.setTerminationExplanation((String)FieldFinder.getFieldValue(paper.terminantionExplanation(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			}
			if(!paper.terminantionDate().equals(Constants.Empty))
			{
				consentObject.setTerminationDate((Date)FieldFinder.getFieldValue(paper.terminantionDate(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			}
			if(!paper.description().equals(Constants.Empty))
			{
				consentObject.setDescription((String)FieldFinder.getFieldValue(paper.description(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			}
			if(!paper.providedBy().equals(Constants.Empty)) 
			{
				var principal = ObjectManager.tryGetPrincipalByFromObject(createdObjectModel, paper.providedBy(), model);
				if(principal.isPresent())
				{
					consentObject.setProvidedBy(principal.get());
				}
			}
			if(!paper.providedById().equals(Constants.Empty))
			{
				var principal = ObjectManager.tryGetPrincipalByById(createdObjectModel, paper.providedById(), model);
				if(principal.isPresent())
				{
					consentObject.setProvidedBy(principal.get());
				}
			}
			model.getAllConsents().add(consentObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return returnedObject;
	}
}

