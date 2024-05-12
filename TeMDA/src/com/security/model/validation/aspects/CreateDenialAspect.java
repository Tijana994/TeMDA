package com.security.model.validation.aspects;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

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
public class CreateDenialAspect extends BaseAspect{

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateDenialAnnotation)")
	void function() {}
	@Around("function()")
	public Object createDenial(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		SetUp(thisJoinPoint);
	    CreateDenialAnnotation createDenial = method.getAnnotation(CreateDenialAnnotation.class);
		if(createDenial == null)
		{
			System.out.println("There is no create denial annotation");
			return returnedObject;
		}
		CreationModel originalObjectModel = new CreationModel(returnedObject, originalObject, thisJoinPoint, createDenial.createdObjectLocation(), createDenial.parametersLocation());
		Object createdObject = FieldFinder.getCreatedObjectToReadFrom(originalObjectModel, originalObject, createDenial.name());
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
				var policyStatements = ReadTypeByAttribute.getPolicyStatementsFromObject(originalObjectModel, createDenial.basedOnStatements(), model);
				if(!policyStatements.isEmpty())
				{
					denialObject.getBasedOnStatements().addAll(policyStatements);
				}
			}
			if(!createDenial.basedOnStatementsIds().equals(Constants.Empty))
			{
				var policyStatements = ReadTypeByAttribute.getPolicyStatementsById(originalObjectModel, createDenial.basedOnStatementsIds(), model);
				if(!policyStatements.isEmpty())
				{
					denialObject.getBasedOnStatements().addAll(policyStatements);
				}
			}
			if(!createDenial.basedOnStatement().equals(Constants.Empty))
			{
				var policyStatement = ObjectManager.tryGetPolicyStatementFromObject(originalObjectModel, createDenial.basedOnStatement(), model);
				if(!policyStatement.isEmpty())
				{
					denialObject.getBasedOnStatements().add(policyStatement.get());
				}
			}
			if(!createDenial.basedOnStatementId().equals(Constants.Empty))
			{
				var policyStatement = ObjectManager.tryGetPolicyStatementById(originalObjectModel, createDenial.basedOnStatementId(), model);
				if(!policyStatement.isEmpty())
				{
					denialObject.getBasedOnStatements().add(policyStatement.get());
				}
			}
			if(!createDenial.forComplaint().equals(Constants.Empty))
			{
				var complaint = ObjectManager.tryGetComplaintFromObject(originalObjectModel, createDenial.forComplaint(), model);
				if(complaint.isPresent() && complaint.get().getAction() instanceof AbstractComplaint)
				{
					((AbstractComplaint)complaint.get().getAction()).setDenialReason(denialObject);
				}
			}
			if(!createDenial.forComplaintId().equals(Constants.Empty))
			{
				var complaint = ObjectManager.tryGetComplaintById(originalObjectModel, createDenial.forComplaintId(), model);
				if(complaint.isPresent() && complaint.get().getAction() instanceof AbstractComplaint)
				{
					((AbstractComplaint)complaint.get().getAction()).setDenialReason(denialObject);
				}
			}
			if(!createDenial.approvedBy().equals(Constants.Empty)) 
			{
				var principal = ObjectManager.tryGetPrincipalByFromObject(originalObjectModel, createDenial.approvedBy(), model);
				if(principal.isPresent())
				{
					denialObject.setApprovedBy(principal.get());
				}
			}
			if(!createDenial.approvedById().equals(Constants.Empty))
			{
				var principal = ObjectManager.tryGetPrincipalByById(originalObjectModel, createDenial.approvedById(), model);
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

