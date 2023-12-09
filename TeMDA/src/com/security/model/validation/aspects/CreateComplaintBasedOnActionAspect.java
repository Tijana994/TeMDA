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
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;

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
		Object ret = thisJoinPoint.proceed(args);
		Object obj = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreateComplaintBasedOnActionAnnotation createComplaintBasedOnAction = method.getAnnotation(CreateComplaintBasedOnActionAnnotation.class);
		if(createComplaintBasedOnAction == null)
		{
			System.out.println("There is no create complaint based on action annotation");
			return ret;
		}
		Object retFromObj = FieldFinder.getObjectToReadFrom(ret, obj, createComplaintBasedOnAction.createdObjectLocation(), createComplaintBasedOnAction.name(), thisJoinPoint);
		if(retFromObj == null)
		{
			System.out.println("Read from object is null - CreateComplaintBasedOnActionAspect");
			return ret;
		}
		Class<? extends Object> retClass = retFromObj.getClass();
		ComplaintAnnotation complaint = retClass.getAnnotation(ComplaintAnnotation.class);
		
		if(complaint == null)
		{
			System.out.println("There is no complaint annotation");
			return ret;
		}
		
		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var complaintTypeObject = repo.getFactory().createComplaintBasedOnAction();
			var complaintObject = repo.getFactory().createComplaint();
			complaintObject.setName((String)FieldFinder.getFieldValue(complaint.id(), retFromObj, retClass));
			complaintObject.setWhen((Date)FieldFinder.getFieldValue(complaint.when(), retFromObj, retClass));
			
			if(complaint.reason() != Constants.Empty)
			{
				complaintObject.setReason((String)FieldFinder.getFieldValue(complaint.reason(), retFromObj, retClass));
			}
			if(createComplaintBasedOnAction.policyStatemet() != Constants.Empty)
			{
				
			}
			if(createComplaintBasedOnAction.policyStatemetId() != Constants.Empty)
			{
				setPolicyStatemetById(retFromObj, retClass, createComplaintBasedOnAction.policyStatemetId(), complaintTypeObject, model);
			}
			complaintObject.setAction(complaintTypeObject);
			model.getAllComplaints().add(complaintObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return ret;
	}
	
	private void setPolicyStatemetById(Object retFromObj, Class<? extends Object> retClass, String propertyName,
			ComplaintBasedOnAction complaintTypeObject, PrivacyPolicy model) {
		var policyStatementId = (String)FieldFinder.getFieldValue(propertyName, retFromObj, retClass);
		var policyStatement = ObjectFinder.checkIfPolicyStatementExists(policyStatementId, model);
		if(policyStatement.isPresent())
		{
			complaintTypeObject.setStatement(policyStatement.get());
		}
	}
}
