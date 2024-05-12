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
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.helpers.ReadTypeByAttribute;
import com.security.model.validation.models.CreationModel;

import privacyModel.AbstractComplaint;
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
		CreationModel originalObjectModel = new CreationModel(returnedObject, thisJoinPoint, createDenial.createdObjectLocation(), createDenial.parametersLocation());
		Object createdObject = FieldFinder.getObjectToReadFrom(originalObjectModel, originalObject, createDenial.name());
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
			if(!denial.reason().equals(Constants.Empty))
			{
				denialObject.setReason((String)FieldFinder.getFieldValue(denial.reason(), createdObject, createdObjectClass));
			}
			if(!createDenial.basedOnStatements().equals(Constants.Empty))
			{
				var policyStatements = ReadTypeByAttribute.getPolicyStatementsFromObject(originalObjectModel, originalObjectClass, originalObject, 
						createDenial.basedOnStatements(), model);
				if(!policyStatements.isEmpty())
				{
					denialObject.getBasedOnStatements().addAll(policyStatements);
				}
			}
			if(!createDenial.basedOnStatementsIds().equals(Constants.Empty))
			{
				var policyStatements = ReadTypeByAttribute.getPolicyStatementsById(originalObjectModel, originalObjectClass, originalObject, 
						createDenial.basedOnStatementsIds(), model);
				if(!policyStatements.isEmpty())
				{
					denialObject.getBasedOnStatements().addAll(policyStatements);
				}
			}
			if(!createDenial.basedOnStatement().equals(Constants.Empty))
			{
				var policyStatement = ObjectManager.tryGetPolicyStatementFromObject(originalObjectModel, originalObject, originalObjectClass, 
						createDenial.basedOnStatement(), model);
				if(!policyStatement.isEmpty())
				{
					denialObject.getBasedOnStatements().add(policyStatement.get());
				}
			}
			if(!createDenial.basedOnStatementId().equals(Constants.Empty))
			{
				var policyStatement = ObjectManager.tryGetPolicyStatementById(originalObjectModel, originalObject, originalObjectClass, 
						createDenial.basedOnStatementId(), model);
				if(!policyStatement.isEmpty())
				{
					denialObject.getBasedOnStatements().add(policyStatement.get());
				}
			}
			if(!createDenial.forComplaint().equals(Constants.Empty))
			{
				var complaint = ObjectManager.tryGetComplaintFromObject(originalObjectModel, originalObject, originalObjectClass, 
						createDenial.forComplaint(), model);
				if(complaint.isPresent() && complaint.get().getAction() instanceof AbstractComplaint)
				{
					((AbstractComplaint)complaint.get().getAction()).setDenialReason(denialObject);
				}
			}
			if(!createDenial.forComplaintId().equals(Constants.Empty))
			{
				var complaint = ObjectManager.tryGetComplaintById(originalObjectModel, originalObject, originalObjectClass, 
						createDenial.forComplaintId(), model);
				if(complaint.isPresent() && complaint.get().getAction() instanceof AbstractComplaint)
				{
					((AbstractComplaint)complaint.get().getAction()).setDenialReason(denialObject);
				}
			}
			if(!createDenial.approvedBy().equals(Constants.Empty)) 
			{
				var principal = ObjectManager.tryGetPrincipalByFromObject(originalObjectModel, originalObject, originalObjectClass,createDenial.approvedBy(), model);
				if(principal.isPresent())
				{
					denialObject.setApprovedBy(principal.get());
				}
			}
			if(!createDenial.approvedById().equals(Constants.Empty))
			{
				var principal = ObjectManager.tryGetPrincipalByById(originalObjectModel, originalObject, originalObjectClass, createDenial.approvedById(), model);
				if(principal.isPresent())
				{
					denialObject.setApprovedBy(principal.get());
				}
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
}

