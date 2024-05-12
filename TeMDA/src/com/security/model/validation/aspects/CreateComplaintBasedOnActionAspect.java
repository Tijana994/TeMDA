package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.ComplaintAnnotation;
import com.security.model.validation.annotations.creators.CreateComplaintBasedOnActionAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class CreateComplaintBasedOnActionAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateComplaintBasedOnActionAnnotation)")
	void function() {}
	@Around("function()")
	public Object createComplaintBasedOnAction(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		
		Object[] args = thisJoinPoint.getArgs();
		Object returnedObject = thisJoinPoint.proceed(args);
		Object originalObject = thisJoinPoint.getThis();
		Class<? extends Object> originalObjectClass = originalObject.getClass();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreateComplaintBasedOnActionAnnotation createComplaintBasedOnAction = method.getAnnotation(CreateComplaintBasedOnActionAnnotation.class);
		if(createComplaintBasedOnAction == null)
		{
			System.out.println("There is no create complaint based on action annotation");
			return returnedObject;
		}
		CreationModel originalObjectModel = new CreationModel(returnedObject, thisJoinPoint, createComplaintBasedOnAction.createdObjectLocation(), createComplaintBasedOnAction.parametersLocation());
		CreationModel createdObjectModel = new CreationModel(returnedObject, thisJoinPoint, createComplaintBasedOnAction.createdObjectLocation(), ParametersObjectsLocation.Property);
		Object createdObject = FieldFinder.getObjectToReadFrom(originalObjectModel, originalObject, createComplaintBasedOnAction.name());
		if(createdObject == null)
		{
			System.out.println("Read from object is null - CreateComplaintBasedOnActionAspect");
			return returnedObject;
		}
		Class<? extends Object> createdObjectClass = createdObject.getClass();
		ComplaintAnnotation complaint = createdObjectClass.getAnnotation(ComplaintAnnotation.class);
		
		if(complaint == null)
		{
			System.out.println("There is no complaint annotation");
			return returnedObject;
		}
		
		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var complaintTypeObject = repo.getFactory().createComplaintBasedOnAction();
			var complaintObject = repo.getFactory().createComplaint();
			complaintObject.setName((String)FieldFinder.getFieldValue(complaint.id(), createdObject, createdObjectClass));
			complaintObject.setWhen((Date)FieldFinder.getFieldValue(complaint.when(), createdObject, createdObjectClass));
			
			if(!complaint.reason().equals(Constants.Empty))
			{
				complaintObject.setReason((String)FieldFinder.getFieldValue(complaint.reason(), createdObject, createdObjectClass));
			}
			if(!createComplaintBasedOnAction.policyStatement().equals(Constants.Empty))
			{
				var policyStatement = ObjectManager.tryGetPolicyStatementFromObject(originalObjectModel, originalObject, originalObjectClass, 
						createComplaintBasedOnAction.policyStatement(), model);
				if(policyStatement.isPresent())
				{
					complaintTypeObject.setStatement(policyStatement.get());
				}
			}
			if(!createComplaintBasedOnAction.policyStatementId().equals(Constants.Empty))
			{
				var policyStatement = ObjectManager.tryGetPolicyStatementById(originalObjectModel, originalObject, originalObjectClass, 
						createComplaintBasedOnAction.policyStatementId(), model);
				if(policyStatement.isPresent())
				{
					complaintTypeObject.setStatement(policyStatement.get());
				}
			}
			if(!complaint.who().equals(Constants.Empty)) 
			{
				var principal = ObjectManager.tryGetPrincipalByFromObject(createdObjectModel, createdObject, createdObjectClass, complaint.who(), model);
				if(principal.isPresent())
				{
					complaintObject.setWho(principal.get());
				}
			}
			if(!complaint.whoId().equals(Constants.Empty))
			{
				var principal = ObjectManager.tryGetPrincipalByById(createdObjectModel, createdObject, createdObjectClass, complaint.whoId(), model);
				if(principal.isPresent())
				{
					complaintObject.setWho(principal.get());
				}
			}
			complaintObject.setAction(complaintTypeObject);
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
