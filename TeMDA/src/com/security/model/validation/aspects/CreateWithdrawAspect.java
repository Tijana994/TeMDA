package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.WithdrawAnnotation;
import com.security.model.validation.annotations.creators.CreateWithdrawAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class CreateWithdrawAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateWithdrawAnnotation)")
	void function() {}
	@Around("function()")
	public Object createWithdraw(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object returnedObject = thisJoinPoint.proceed(args);
		Object originalObject = thisJoinPoint.getThis();
		Class<? extends Object> originalObjectClass = originalObject.getClass();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
		CreateWithdrawAnnotation createWithdraw = method.getAnnotation(CreateWithdrawAnnotation.class);
		if(createWithdraw == null)
		{
			System.out.println("There is no create withdraw statement annotation");
			return returnedObject;
		}
		CreationModel originalObjectModel = new CreationModel(returnedObject, thisJoinPoint, createWithdraw.createdObjectLocation(), createWithdraw.parametersLocation());
		CreationModel createdObjectModel = new CreationModel(returnedObject, thisJoinPoint, createWithdraw.createdObjectLocation(), ParametersObjectsLocation.Property);
		Object createdObject = FieldFinder.getObjectToReadFrom(originalObjectModel, originalObject, createWithdraw.name());
		if(createdObject == null)
		{
			System.out.println("Read from object is null = CreateWithdrawAspect");
			return returnedObject;
		}
		Class<? extends Object> createdObjectClass = createdObject.getClass();
		WithdrawAnnotation withdraw = createdObjectClass.getAnnotation(WithdrawAnnotation.class);
		
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
			complaintObject.setName((String)FieldFinder.getFieldValue(withdraw.id(), createdObject, createdObjectClass));
			complaintObject.setWhen((Date)FieldFinder.getFieldValue(withdraw.when(), createdObject, createdObjectClass));
			if(!withdraw.reason().equals(Constants.Empty))
			{
				complaintObject.setReason((String)FieldFinder.getFieldValue(withdraw.reason(), createdObject, createdObjectClass));
			}
			if(!createWithdraw.consent().equals(Constants.Empty))
			{
				var consent = ObjectManager.tryGetConsentFromObject(originalObjectModel, originalObject, originalObjectClass, createWithdraw.consent(), model);
				if(consent.isPresent())
				{
					withdrawObject.setSubject(consent.get());
				}
			}
			if(!createWithdraw.consentId().equals(Constants.Empty))
			{
				var consent = ObjectManager.tryGetConsentById(originalObjectModel, originalObject, originalObjectClass, createWithdraw.consentId(), model);
				if(consent.isPresent())
				{
					withdrawObject.setSubject(consent.get());
				}
			}
			if(!withdraw.who().equals(Constants.Empty)) 
			{
				var principal = ObjectManager.tryGetPrincipalByFromObject(createdObjectModel, createdObject, createdObjectClass, withdraw.who(), model);
				if(principal.isPresent())
				{
					complaintObject.setWho(principal.get());
				}
			}
			if(!withdraw.whoId().equals(Constants.Empty))
			{
				var principal = ObjectManager.tryGetPrincipalByById(createdObjectModel, createdObject, createdObjectClass, withdraw.whoId(), model);
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