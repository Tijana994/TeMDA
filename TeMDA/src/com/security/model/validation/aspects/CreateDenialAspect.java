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
import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.ReadTypeByAttribute;

import privacyModel.AbstractComplaint;
import privacyModel.Denial;
import privacyModel.PrivacyPolicy;
import utility.PrivacyModelRepository;

@Aspect
public class CreateDenialAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateDenialAnnotation)")
	void function() {}
	@Around("function()")
	public Object createDenial(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object returnedObject = thisJoinPoint.proceed(args);
		Object originalObject = thisJoinPoint.getThis();
		Class<? extends Object> originalObjectClass = originalObject.getClass();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreateDenialAnnotation createDenial = method.getAnnotation(CreateDenialAnnotation.class);
		if(createDenial == null)
		{
			System.out.println("There is no create denial annotation");
			return returnedObject;
		}
		Object createdObject = FieldFinder.getObjectToReadFrom(returnedObject, originalObject, createDenial.createdObjectLocation(), createDenial.name(), thisJoinPoint);
		if(createdObject == null)
		{
			System.out.println("Read from object is null - CreateDenialAspect");
			return returnedObject;
		}
		Class<? extends Object> createdObjectClass = createdObject.getClass();
		DenialAnnotation denial = createdObjectClass.getAnnotation(DenialAnnotation.class);
		
		if(denial == null)
		{
			System.out.println("There is no denial annotation");
			return returnedObject;
		}

		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var denialObject = repo.getFactory().createDenial();
			denialObject.setName((String)FieldFinder.getFieldValue(denial.id(), createdObject, createdObjectClass));
			denialObject.setWhen((Date)FieldFinder.getFieldValue(denial.when(), createdObject, createdObjectClass));
			if(denial.reason() != Constants.Empty)
			{
				denialObject.setReason((String)FieldFinder.getFieldValue(denial.reason(), createdObject, createdObjectClass));
			}
			if(createDenial.basedOnStatemets() != Constants.Empty)
			{
				
			}
			if(createDenial.basedOnStatemetsIds() != Constants.Empty)
			{
				var policyStatements = ReadTypeByAttribute.getPolicyStatementsById(originalObject, originalObjectClass, createDenial.basedOnStatemetsIds(), model);
				if(!policyStatements.isEmpty())
				{
					denialObject.getBasedOnStatements().addAll(policyStatements);
				}
			}
			if(createDenial.forComplaint() != Constants.Empty)
			{
				
			}
			if(createDenial.forComplaintId() != Constants.Empty)
			{
				setComplaintById(originalObject, originalObjectClass, createDenial.forComplaintId(), denialObject, model);
			}
			model.getAllDenials().add(denialObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return returnedObject;
	}
	
	private void setComplaintById(Object createdobject, Class<? extends Object> createdobjectClass, String  propertyName,
			Denial denialObject, PrivacyPolicy model) {
		var complaintId = (String)FieldFinder.getFieldValue(propertyName, createdobject, createdobjectClass);
		var complaint = ObjectFinder.checkIfComplaintExists(complaintId, model);
		if(complaint.isPresent())
		{
			if(complaint.get().getAction() instanceof AbstractComplaint)
			{
				((AbstractComplaint)complaint.get().getAction()).setDenialReason(denialObject);
			}
		}
	}
}

