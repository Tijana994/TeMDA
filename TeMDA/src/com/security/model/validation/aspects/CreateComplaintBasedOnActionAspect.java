package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.ComplaintAnnotation;
import com.security.model.validation.annotations.creators.CreateComplaintBasedOnActionAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.ReadTypeByAttribute;

import privacyModel.ComplaintBasedOnAction;
import privacyModel.PrivacyPolicy;
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
		Object createdObject = FieldFinder.getObjectToReadFrom(returnedObject, originalObject, createComplaintBasedOnAction.createdObjectLocation(), createComplaintBasedOnAction.name(), thisJoinPoint);
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
				setPolicyStatementFromObject(originalObject, originalObjectClass, createComplaintBasedOnAction, model, complaintTypeObject, thisJoinPoint);
			}
			if(!createComplaintBasedOnAction.policyStatementId().equals(Constants.Empty))
			{
				var policyStatementId = (String)FieldFinder.getFieldValue(createComplaintBasedOnAction.policyStatementId(), originalObject, originalObjectClass);
				setPolicyStatementById(model, complaintTypeObject, policyStatementId);
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
	
	private void setPolicyStatementFromObject(Object obj, Class<? extends Object> objectClass,
			CreateComplaintBasedOnActionAnnotation createComplaintBasedOnAction, PrivacyPolicy model, 
			ComplaintBasedOnAction complaintTypeObject, JoinPoint jp) {
		var policyStatementId = ReadTypeByAttribute.getPolicyStatementIdFromObject(objectClass, obj, createComplaintBasedOnAction.policyStatement(), createComplaintBasedOnAction.parametersLocation(), jp);
		if(policyStatementId.isPresent())
		{
			var policyStatementName = policyStatementId.get();
			setPolicyStatementById(model, complaintTypeObject, policyStatementName);
		}
	}
	
	private void setPolicyStatementById(PrivacyPolicy model, ComplaintBasedOnAction complaintTypeObject, String id) {
		
		var policyStatement = ObjectFinder.checkIfPolicyStatementExists(id, model);
		if(policyStatement.isPresent())
		{
			complaintTypeObject.setStatement(policyStatement.get());
		}
	}
}
