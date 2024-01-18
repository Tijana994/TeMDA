package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.WithdrawAnnotation;
import com.security.model.validation.annotations.creators.CreateWithdrawAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.ReadTypeByAttribute;

import privacyModel.PrivacyPolicy;
import privacyModel.Withdraw;
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
		Object createdObject = FieldFinder.getObjectToReadFrom(returnedObject, originalObject, createWithdraw.createdObjectLocation(), createWithdraw.name(), thisJoinPoint);
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
			if(!createWithdraw.consent().equals(Constants.Undefined))
			{
				setConsentFromObject(originalObject, originalObjectClass, createWithdraw, model, withdrawObject, thisJoinPoint);
			}
			if(!createWithdraw.consentId().equals(Constants.Undefined))
			{
				var consentId = (String)FieldFinder.getFieldValue(createWithdraw.consentId(), originalObject, originalObjectClass);
				setConsentById(model, withdrawObject, consentId);
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
	
	private void setConsentFromObject(Object obj, Class<? extends Object> objectClass,
			CreateWithdrawAnnotation createWithdraw, PrivacyPolicy model, 
			Withdraw withdrawObject, JoinPoint jp) {
		var consentId = ReadTypeByAttribute.getConsentIdFromObject(objectClass, obj, createWithdraw.consent(), createWithdraw.parametersLocation(), jp);
		if(consentId.isPresent())
		{
			var consentName = consentId.get();
			setConsentById(model, withdrawObject, consentName);
		}
	}
	private void setConsentById(PrivacyPolicy model, Withdraw withdrawObject, String id) {
		var consent = ObjectFinder.checkIfConsentExists(id, model);
		if(consent.isPresent())
		{
			withdrawObject.setSubject(consent.get());
		}
	}
	
	
}