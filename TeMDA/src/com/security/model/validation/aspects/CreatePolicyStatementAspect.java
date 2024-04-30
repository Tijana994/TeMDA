package com.security.model.validation.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.PolicyStatementAnnotation;
import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.creators.HowCreator;
import com.security.model.validation.creators.ParametersAnnotations;
import com.security.model.validation.creators.PurposeCreator;
import com.security.model.validation.creators.WhatCreator;
import com.security.model.validation.creators.WhenCreator;
import com.security.model.validation.creators.WhereCreator;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectManager;

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
				var who = ObjectManager.tryGetPrincipalByById(originalObject, originalObjectClass, createPolicyStatement.whoId(), model, parametersLocation, thisJoinPoint);
				if(who.isPresent())
				{
					policyStatementObject.setWho(who.get());
				}
			}
			if(!createPolicyStatement.who().equals(Constants.Empty))
			{
				var who = ObjectManager.tryGetPrincipalByFromObject(originalObject, originalObjectClass, createPolicyStatement.who(), model, parametersLocation, thisJoinPoint);
				if(who.isPresent())
				{
					policyStatementObject.setWho(who.get());
				}
			}
			if(!createPolicyStatement.whoseId().equals(Constants.Empty))
			{
				var whose = ObjectManager.tryGetPrincipalByById(originalObject, originalObjectClass, createPolicyStatement.whoseId(), model, parametersLocation, thisJoinPoint);
				if(whose.isPresent())
				{
					policyStatementObject.setWhose(whose.get());
				}
			}
			if(!createPolicyStatement.whose().equals(Constants.Empty))
			{
				var whose = ObjectManager.tryGetPrincipalByFromObject(originalObject, originalObjectClass, createPolicyStatement.whose(), model, parametersLocation, thisJoinPoint);
				if(whose.isPresent())
				{
					policyStatementObject.setWhose(whose.get());
				}
			}
			if(!createPolicyStatement.whomId().equals(Constants.Empty))
			{
				var whom = ObjectManager.tryGetPrincipalByById(originalObject, originalObjectClass, createPolicyStatement.whomId(), model, parametersLocation, thisJoinPoint);
				if(whom.isPresent())
				{
					policyStatementObject.setWhom(whom.get());
				}
			}
			if(!createPolicyStatement.whom().equals(Constants.Empty))
			{
				var whom = ObjectManager.tryGetPrincipalByFromObject(originalObject, originalObjectClass, createPolicyStatement.whom(), model, parametersLocation, thisJoinPoint);
				if(whom.isPresent())
				{
					policyStatementObject.setWhom(whom.get());
				}
			}
			
			var purpose = PurposeCreator.createPurpose(originalObjectClass, originalObject, createPolicyStatement.why(), 
					parametersLocation, repo.getFactory(), thisJoinPoint);
			policyStatementObject.setWhy(purpose);
			var when = WhenCreator.createWhen(originalObjectClass, originalObject, createPolicyStatement.when(), 
					parametersLocation, repo.getFactory(), thisJoinPoint, annotations);
			policyStatementObject.setWhen(when);
			
		    var what = WhatCreator.createWhat(createPolicyStatement, repo, model);
		    policyStatementObject.setWhat(what);
		    
		    if(HowCreator.shouldCreate(createPolicyStatement))
		    {
			    var how = HowCreator.createHow(createPolicyStatement, originalObject, originalObjectClass, repo, model, thisJoinPoint);
				policyStatementObject.setHow(how);
		    }

		    if(WhereCreator.shouldCreate(createPolicyStatement))
		    {
				var where = WhereCreator.createWhere(createPolicyStatement, originalObject, originalObjectClass, repo, model, thisJoinPoint);
				policyStatementObject.setWhere(where);
		    }
			
			model.getPolicyStatements().add(policyStatementObject);
			repo.saveModel(model);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		return returnedObject;
	}
}
