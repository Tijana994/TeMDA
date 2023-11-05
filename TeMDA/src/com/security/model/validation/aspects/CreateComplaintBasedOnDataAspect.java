package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.ComplaintAnnotation;
import com.security.model.validation.annotations.creators.CreateComplaintBasedOnDataAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;

import utility.PrivacyModelRepository;

@Aspect
public class CreateComplaintBasedOnDataAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateComplaintBasedOnDataAnnotation)")
	void function() {}
	@Around("function()")
	public Object aroundOkoF(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object ret = thisJoinPoint.proceed(args);
		Object obj = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreateComplaintBasedOnDataAnnotation createComplaintBasedOnData = method.getAnnotation(CreateComplaintBasedOnDataAnnotation.class);
		if(createComplaintBasedOnData == null)
		{
			System.out.println("There is no create complaint based on data annotation");
			return ret;
		}
		Object retFromObj = FieldFinder.getObjectToReadFrom(ret, obj, createComplaintBasedOnData.createdObjectLocation(), createComplaintBasedOnData.name(), thisJoinPoint);
		if(retFromObj == null)
		{
			System.out.println("Read from object is null - CreateComplaintBasedOnDataAspect");
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
			var complaintTypeObject = repo.getFactory().createComplaintBasedOnData();
			var complaintObject = repo.getFactory().createComplaint();
			complaintObject.setName((String)FieldFinder.getFieldValue(complaint.id(), retFromObj, retClass));
			complaintObject.setWhen((Date)FieldFinder.getFieldValue(complaint.when(), retFromObj, retClass));
			complaintTypeObject.setType(createComplaintBasedOnData.type());
			if(complaint.reason() != Constants.Empty)
			{
				complaintObject.setReason((String)FieldFinder.getFieldValue(complaint.reason(), retFromObj, retClass));
			}
			if(createComplaintBasedOnData.subjects() != Constants.Empty)
			{
				
			}
			if(createComplaintBasedOnData.subjectsIds() != Constants.Empty)
			{
				
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
}