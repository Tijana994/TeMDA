package com.security.model.validation.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.security.model.validation.annotations.PolicyStatementAnnotation;
import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.creators.HowCreator;
import com.security.model.validation.creators.PurposeCreator;
import com.security.model.validation.creators.WhatCreator;
import com.security.model.validation.creators.WhenCreator;
import com.security.model.validation.creators.WhereCreator;
import com.security.model.validation.models.CreationModel;
import com.security.model.validation.models.ParametersAnnotations;

import privacyModel.Purpose;
import utility.PrivacyModelRepository;

@Aspect
public class CreatePolicyStatementAspect extends BaseAspect {
	
	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation)")
	void function() {}
	@Around("function()")
	public Object createPolicyStatement(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		SetUp(thisJoinPoint);
		CreatePolicyStatementAnnotation createPolicyStatement = method.getAnnotation(CreatePolicyStatementAnnotation.class);
		if(createPolicyStatement == null)
		{
			Logger.LogErrorMessage("There is no create policy statement annotation");
			return returnedObject;
		}
		CreationModel originalObjectModel = new CreationModel(returnedObject, originalObject, thisJoinPoint, createPolicyStatement.createdObjectLocation(),
				createPolicyStatement.parametersLocation(), createPolicyStatement.propertyObjectName());
		String logId = null;
		if(createPolicyStatement.systemActionId().equals(Constants.Empty))
		{
			Object createdObject = FieldFinder.getCreatedObjectToReadFrom(originalObjectModel, originalObject, createPolicyStatement.name());
			if(createdObject == null)
			{
				Logger.LogErrorMessage("Read from object is null = CreatePolicyStatementAspect. Cannot create policy statement.");
				return returnedObject;
			}
			Class<? extends Object> createdObjectClass = createdObject.getClass();
			PolicyStatementAnnotation policyStatement = createdObjectClass.getAnnotation(PolicyStatementAnnotation.class);
			if(policyStatement == null)
			{
				Logger.LogErrorMessage("There is no policy statement annotation. Cannot create policy statement.");
				return returnedObject;
			}
			logId = (String)FieldFinder.getFieldValue(policyStatement.id(), returnedObject, createdObjectClass);
		}
		else
		{
			var obj = FieldFinder.getObjectToReadFrom(originalObjectModel, originalObjectModel.getObjectClass(), originalObjectModel.getObject(), createPolicyStatement.systemActionId());
			if(obj.isPresent())
			{
				logId = (String)obj.get();
			}
			else
			{
				return returnedObject;
			}
		}

		try
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var policyStatementObject = repo.getFactory().createPolicyStatement();
			policyStatementObject.setName(logId);
			if(!createPolicyStatement.whoId().equals(Constants.Empty))
			{
				var who = ObjectManager.tryGetPrincipalByById(originalObjectModel, createPolicyStatement.whoId(), model);
				if(who.isPresent())
				{
					policyStatementObject.setWho(who.get());
				}
			}
			if(!createPolicyStatement.who().equals(Constants.Empty))
			{
				var who = ObjectManager.tryGetPrincipalByFromObject(originalObjectModel, createPolicyStatement.who(), model);
				if(who.isPresent())
				{
					policyStatementObject.setWho(who.get());
				}
			}
			if(!createPolicyStatement.whoseId().equals(Constants.Empty))
			{
				var whose = ObjectManager.tryGetPrincipalByById(originalObjectModel, createPolicyStatement.whoseId(), model);
				if(whose.isPresent())
				{
					policyStatementObject.setWhose(whose.get());
				}
			}
			if(!createPolicyStatement.whose().equals(Constants.Empty))
			{
				var whose = ObjectManager.tryGetPrincipalByFromObject(originalObjectModel, createPolicyStatement.whose(), model);
				if(whose.isPresent())
				{
					policyStatementObject.setWhose(whose.get());
				}
			}
			if(!createPolicyStatement.whomId().equals(Constants.Empty))
			{
				var whom = ObjectManager.tryGetPrincipalByById(originalObjectModel, createPolicyStatement.whomId(), model);
				if(whom.isPresent())
				{
					policyStatementObject.setWhom(whom.get());
				}
			}
			if(!createPolicyStatement.whom().equals(Constants.Empty))
			{
				var whom = ObjectManager.tryGetPrincipalByFromObject(originalObjectModel, createPolicyStatement.whom(), model);
				if(whom.isPresent())
				{
					policyStatementObject.setWhom(whom.get());
				}
			}
			if(!createPolicyStatement.causedBy().equals(Constants.Empty))
			{
				var complaint = ObjectManager.tryGetComplaintFromObject(originalObjectModel, createPolicyStatement.causedBy(), model);
				if(complaint.isPresent())
				{
					policyStatementObject.setCausedBy(complaint.get());
				}
			}
			if(!createPolicyStatement.causedById().equals(Constants.Empty))
			{
				var complaint = ObjectManager.tryGetComplaintById(originalObjectModel, createPolicyStatement.causedById(), model);
				if(complaint.isPresent())
				{
					policyStatementObject.setCausedBy(complaint.get());
				}
			}
			
			if(createPolicyStatement.isPurposeObject())
			{
				var purposeObject = FieldFinder.getObjectToReadFrom(originalObjectModel, originalObjectClass, originalObject, createPolicyStatement.why());
				if(purposeObject.isEmpty())
				{
					return returnedObject;
				}
				policyStatementObject.setWhy((Purpose)purposeObject.get());
			}
			else
			{
				var purpose = PurposeCreator.createPurpose(originalObjectModel, originalObjectClass, originalObject, createPolicyStatement.why(), repo.getFactory(), FieldFinder, Logger);
				policyStatementObject.setWhy(purpose);
			}

			ParametersAnnotations annotations = new ParametersAnnotations(method.getParameterAnnotations(), signature.getParameterNames());
			var when = WhenCreator.createWhen(originalObjectModel, createPolicyStatement.when(), repo.getFactory(), annotations, FieldFinder, Logger);
			policyStatementObject.setWhen(when);
			
		    var what = WhatCreator.createWhat(createPolicyStatement, repo, model, ReadTypeByAttribute);
		    policyStatementObject.setWhat(what);
		    
		    if(HowCreator.shouldCreate(createPolicyStatement))
		    {
			    var how = HowCreator.createHow(originalObjectModel, createPolicyStatement, repo, model, ObjectManager, ReadTypeByAttribute);
				policyStatementObject.setHow(how);
		    }

		    if(WhereCreator.shouldCreate(createPolicyStatement))
		    {
				var where = WhereCreator.createWhere(originalObjectModel, createPolicyStatement, repo, model, ObjectManager);
				policyStatementObject.setWhere(where);
		    }
			
			model.getPolicyStatements().add(policyStatementObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			Logger.LogExceptionMessage(ex);
		}
		
		return returnedObject;
	}
}
