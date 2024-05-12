package com.security.model.validation.aspects;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.security.model.validation.annotations.WithdrawAnnotation;
import com.security.model.validation.annotations.creators.CreateWithdrawAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class CreateWithdrawAspect extends BaseAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateWithdrawAnnotation)")
	void function() {}
	@Around("function()")
	public Object createWithdraw(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		SetUp(thisJoinPoint);
		CreateWithdrawAnnotation createWithdraw = method.getAnnotation(CreateWithdrawAnnotation.class);
		if(createWithdraw == null)
		{
			System.out.println("There is no create withdraw statement annotation");
			return returnedObject;
		}
		CreationModel originalObjectModel = new CreationModel(returnedObject, originalObject, thisJoinPoint, createWithdraw.createdObjectLocation(), createWithdraw.parametersLocation());
		CreationModel createdObjectModel = new CreationModel(returnedObject, thisJoinPoint, createWithdraw.createdObjectLocation(), ParametersObjectsLocation.Property);
		createdObjectModel.setObject(FieldFinder.getCreatedObjectToReadFrom(originalObjectModel, originalObject, createWithdraw.name()));
		if(createdObjectModel.getObject() == null)
		{
			System.out.println("Read from object is null = CreateWithdrawAspect");
			return returnedObject;
		}
		WithdrawAnnotation withdraw = createdObjectModel.getObjectClass().getAnnotation(WithdrawAnnotation.class);
		if(withdraw == null)
		{
			System.out.println("There is no withdraw annotation");
			return returnedObject;
		}
		
		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var withdrawObject = repo.getFactory().createWithdraw();
			var complaintObject = repo.getFactory().createComplaint();
			complaintObject.setAction(withdrawObject);
			complaintObject.setName((String)FieldFinder.getFieldValue(withdraw.id(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			complaintObject.setWhen((Date)FieldFinder.getFieldValue(withdraw.when(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			if(!withdraw.reason().equals(Constants.Empty))
			{
				complaintObject.setReason((String)FieldFinder.getFieldValue(withdraw.reason(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			}
			if(!createWithdraw.consent().equals(Constants.Empty))
			{
				var consent = ObjectManager.tryGetConsentFromObject(originalObjectModel, createWithdraw.consent(), model);
				if(consent.isPresent())
				{
					withdrawObject.setSubject(consent.get());
				}
			}
			if(!createWithdraw.consentId().equals(Constants.Empty))
			{
				var consent = ObjectManager.tryGetConsentById(originalObjectModel, createWithdraw.consentId(), model);
				if(consent.isPresent())
				{
					withdrawObject.setSubject(consent.get());
				}
			}
			if(!withdraw.who().equals(Constants.Empty)) 
			{
				var principal = ObjectManager.tryGetPrincipalByFromObject(createdObjectModel, withdraw.who(), model);
				if(principal.isPresent())
				{
					complaintObject.setWho(principal.get());
				}
			}
			if(!withdraw.whoId().equals(Constants.Empty))
			{
				var principal = ObjectManager.tryGetPrincipalByById(createdObjectModel, withdraw.whoId(), model);
				if(principal.isPresent())
				{
					complaintObject.setWho(principal.get());
				}
			}
			model.getAllComplaints().add(complaintObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return returnedObject;
	}
}