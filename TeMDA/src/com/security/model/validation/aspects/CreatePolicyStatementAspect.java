package com.security.model.validation.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.PolicyStatementAnnotation;
import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.creators.HowCreator;
import com.security.model.validation.creators.ParametersAnnotations;
import com.security.model.validation.creators.PurposeCreator;
import com.security.model.validation.creators.WhatCreator;
import com.security.model.validation.creators.WhenCreator;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.ReadTypeByAttribute;

import privacyModel.PolicyStatement;
import privacyModel.PrivacyPolicy;
import utility.PrivacyModelRepository;

@Aspect
public class CreatePolicyStatementAspect {
	
	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation)")
	void function() {}
	@Around("function()")
	public Object createPolicyStatement(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object returnedObject = thisJoinPoint.proceed(args);
		Object originalObject = thisJoinPoint.getThis();
		Class<? extends Object> originalObjectClass = originalObject.getClass();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    ParametersAnnotations annotations = new ParametersAnnotations(method.getParameterAnnotations(),
	    		signature.getParameterNames());
		CreatePolicyStatementAnnotation createPolicyStatement = method.getAnnotation(CreatePolicyStatementAnnotation.class);
		if(createPolicyStatement == null)
		{
			System.out.println("There is no create policy statement annotation");
			return returnedObject;
		}
		Object createdObject = FieldFinder.getObjectToReadFrom(returnedObject, originalObject, createPolicyStatement.createdObjectLocation(), createPolicyStatement.name(), thisJoinPoint);
		if(createdObject == null)
		{
			System.out.println("Read from object is null = CreatePolicyStatementAspect");
			return returnedObject;
		}
		Class<? extends Object> createdObjectClass = createdObject.getClass();
		PolicyStatementAnnotation policyStatement = createdObjectClass.getAnnotation(PolicyStatementAnnotation.class);
		
		if(policyStatement == null)
		{
			System.out.println("There is no policy statement annotation");
			return returnedObject;
		}
		
		try
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var parametersLocation = createPolicyStatement.parametersLocation();
			var policyStatementObject = repo.getFactory().createPolicyStatement();
			policyStatementObject.setName((String)FieldFinder.getFieldValue(policyStatement.id(), returnedObject, createdObjectClass));
			if(!createPolicyStatement.whoId().equals(Constants.Empty))
			{
				var whoId = FieldFinder.getObjectToReadFrom(originalObjectClass, originalObject, parametersLocation, createPolicyStatement.whoId(), thisJoinPoint);
				if(whoId.isPresent())
				{
					setWhoById(model, policyStatementObject, (String)whoId.get());
				}
			}
			if(!createPolicyStatement.who().equals(Constants.Empty))
			{
				setWhoFromObject(originalObject, originalObjectClass, createPolicyStatement.who(), parametersLocation, policyStatementObject, model, thisJoinPoint);
			}
			if(!createPolicyStatement.whoseId().equals(Constants.Empty))
			{
				var whoseId = FieldFinder.getObjectToReadFrom(originalObjectClass, originalObject, parametersLocation, createPolicyStatement.whoseId(), thisJoinPoint);
				if(whoseId.isPresent())
				{
					setWhoseById(model, policyStatementObject, (String)whoseId.get());
				}
			}
			if(!createPolicyStatement.whose().equals(Constants.Empty))
			{
				setWhoseFromObject(originalObject, originalObjectClass, createPolicyStatement.whose(), parametersLocation, policyStatementObject, model, thisJoinPoint);
			}
			if(!createPolicyStatement.whomId().equals(Constants.Empty))
			{
				var whomId = FieldFinder.getObjectToReadFrom(originalObjectClass, originalObject, parametersLocation, createPolicyStatement.whomId(), thisJoinPoint);
				if(whomId.isPresent())
				{
					setWhomById(model, policyStatementObject, (String)whomId.get());
				}
			}
			if(!createPolicyStatement.whom().equals(Constants.Empty))
			{
				setWhomFromObject(originalObject, originalObjectClass, createPolicyStatement.whom(), parametersLocation, 
						policyStatementObject, model, thisJoinPoint);
			}
			
			var purpose = PurposeCreator.createPurpose(originalObjectClass, originalObject, createPolicyStatement.why(), 
					parametersLocation, repo.getFactory(), thisJoinPoint);
			policyStatementObject.setWhy(purpose);
			var when = WhenCreator.createWhen(originalObjectClass, originalObject, createPolicyStatement.when(), 
					parametersLocation, repo.getFactory(), thisJoinPoint, annotations);
			policyStatementObject.setWhen(when);
			
		    var what = WhatCreator.createWhat(createPolicyStatement, repo, model);
		    policyStatementObject.setWhat(what);
		    
		    var how = HowCreator.createHow(createPolicyStatement, originalObject, originalObjectClass, repo, model, thisJoinPoint);
			policyStatementObject.setHow(how);
			
			model.getPolicyStatements().add(policyStatementObject);
			repo.saveModel(model);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		return returnedObject;
	}
	private void setWhoFromObject(Object originalObject, Class<? extends Object> originalObjectClass,
			String propertyName, ParametersObjectsLocation parametersLocation,
			PolicyStatement policyStatementObject, PrivacyPolicy model, JoinPoint jp) {
		var principalId = ReadTypeByAttribute.getPrincipalIdFromObject(originalObjectClass, originalObject, propertyName, parametersLocation, jp);
		if(principalId.isPresent())
		{
			var principalName = principalId.get();
			setWhoById(model, policyStatementObject, principalName);
		}
	}
	private void setWhoById(PrivacyPolicy model, PolicyStatement policyStatementObject, String id) {
		var whoPrincipal = ObjectFinder.checkIfPrincipalExists(id, model);
		if(whoPrincipal.isPresent())
		{
			policyStatementObject.setWho(whoPrincipal.get());
		}
	}
	private void setWhoseFromObject(Object originalObject, Class<? extends Object> originalObjectClass,
			String propertyName, ParametersObjectsLocation parametersLocation,
			PolicyStatement policyStatementObject, PrivacyPolicy model, JoinPoint jp) {
		var principalId = ReadTypeByAttribute.getPrincipalIdFromObject(originalObjectClass, originalObject, propertyName, parametersLocation, jp);
		if(principalId.isPresent())
		{
			var principalName = principalId.get();
			setWhoseById(model, policyStatementObject, principalName);
		}
	}
	private void setWhoseById(PrivacyPolicy model, PolicyStatement policyStatementObject, String id) {
		var whosePrincipal = ObjectFinder.checkIfPrincipalExists(id, model);
		if(whosePrincipal.isPresent())
		{
			policyStatementObject.setWhose(whosePrincipal.get());
		}
	}
	private void setWhomFromObject(Object originalObject, Class<? extends Object> originalObjectClass,
			String propertyName, ParametersObjectsLocation parametersLocation,
			PolicyStatement policyStatementObject, PrivacyPolicy model, JoinPoint jp) {
		var principalId = ReadTypeByAttribute.getPrincipalIdFromObject(originalObjectClass, originalObject, propertyName, parametersLocation, jp);
		if(principalId.isPresent())
		{
			var principalName = principalId.get();
			setWhomById(model, policyStatementObject, principalName);
		}
	}
	private void setWhomById(PrivacyPolicy model, PolicyStatement policyStatementObject, String id) {
		var whomPrincipal = ObjectFinder.checkIfPrincipalExists(id, model);
		if(whomPrincipal.isPresent())
		{
			policyStatementObject.setWhom(whomPrincipal.get());
		}
	}
}
