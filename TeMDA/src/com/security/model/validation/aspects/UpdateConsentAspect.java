package com.security.model.validation.aspects;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.security.model.validation.annotations.updaters.UpdateConsentAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class UpdateConsentAspect extends BaseAspect {
	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.updaters.UpdateConsentAnnotation)")
	void function() {}
	@Around("function()")
	public Object updateConsent(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		SetUp(thisJoinPoint);
		UpdateConsentAnnotation updatedConsent = method.getAnnotation(UpdateConsentAnnotation.class);
		if(updatedConsent == null)
		{
			Logger.LogErrorMessage("There is no update consent statement annotation");
			return returnedObject;
		}
		CreationModel originalObjectModel = new CreationModel(returnedObject, originalObject, thisJoinPoint, CreatedObjectLocation.Return,
				updatedConsent.parametersLocation(), updatedConsent.propertyObjectName());
		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			Date terminationDate;
			var obj = FieldFinder.getObjectToReadFrom(originalObjectModel, originalObjectModel.getObjectClass(), originalObjectModel.getObject(), updatedConsent.terminationDate());
			if(obj.isPresent())
			{
				terminationDate = (Date)obj.get();
			}
			else
			{
				return returnedObject;
			}
			if(!updatedConsent.consent().equals(Constants.Empty))
			{
				var consent = ObjectManager.tryGetConsentFromObject(originalObjectModel, updatedConsent.consent(), model);
				if(consent.isPresent())
				{
					consent.get().setTerminationDate(terminationDate);
				}
			}
			if(!updatedConsent.consentId().equals(Constants.Empty))
			{
				var consent = ObjectManager.tryGetConsentById(originalObjectModel, updatedConsent.consentId(), model);
				if(consent.isPresent())
				{
					consent.get().setTerminationDate(terminationDate);
				}
			}
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			Logger.LogExceptionMessage(ex);
		}
		
		return returnedObject;
	}
}
