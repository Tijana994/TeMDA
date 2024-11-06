package com.security.model.validation.aspects;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.creators.CreateConsentAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class CreateConsentAspect extends BaseAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateConsentAnnotation)")
	void function() {}
	@Around("function()")
	public Object createConsent(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		SetUp(thisJoinPoint);
	    CreateConsentAnnotation createConsent = method.getAnnotation(CreateConsentAnnotation.class);
		if(createConsent == null)
		{
			Logger.LogErrorMessage("There is no create consent statement annotation");
			return returnedObject;
		}
		CreationModel originalObjectModel = new CreationModel(returnedObject, originalObject, thisJoinPoint, createConsent.createdObjectLocation(), 
				createConsent.parametersLocation(), createConsent.propertyObjectName());
		CreationModel createdObjectModel = new CreationModel(returnedObject, thisJoinPoint, createConsent.createdObjectLocation(), ParametersObjectsLocation.Property);
		createdObjectModel.setObject(FieldFinder.getCreatedObjectToReadFrom(createdObjectModel, originalObject, createConsent.name()));
		if(createdObjectModel.getObject() == null)
		{
			Logger.LogErrorMessage("Read from object is null - CreateConsentAspect");
			return returnedObject;
		}
		PaperAnnotation paper = createdObjectModel.getObjectClass().getAnnotation(PaperAnnotation.class);
		if(paper == null)
		{
			Logger.LogErrorMessage("There is no paper annotation");
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
			if(!createConsent.providedBy().equals(Constants.Empty)) 
			{
				var principal = ObjectManager.tryGetPrincipalByFromObject(originalObjectModel, createConsent.providedBy(), model);
				if(principal.isPresent())
				{
					consentObject.setProvidedBy(principal.get());
				}
			}
			if(!createConsent.providedById().equals(Constants.Empty))
			{
				var principal = ObjectManager.tryGetPrincipalByById(originalObjectModel, createConsent.providedById(), model);
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
			Logger.LogExceptionMessage(ex);
		}
		
		return returnedObject;
	}
}

