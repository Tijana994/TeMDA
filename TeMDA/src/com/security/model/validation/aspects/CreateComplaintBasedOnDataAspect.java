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
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.helpers.ReadTypeByAttribute;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class CreateComplaintBasedOnDataAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateComplaintBasedOnDataAnnotation)")
	void function() {}
	@Around("function()")
	public Object createComplaintBasedOnData(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object returnedObject = thisJoinPoint.proceed(args);
		Object originalObject = thisJoinPoint.getThis();
		Class<? extends Object> originalObjectClass = originalObject.getClass();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreateComplaintBasedOnDataAnnotation createComplaintBasedOnData = method.getAnnotation(CreateComplaintBasedOnDataAnnotation.class);
		if(createComplaintBasedOnData == null)
		{
			System.out.println("There is no create complaint based on data annotation");
			return returnedObject;
		}
		CreationModel originalObjectModel = new CreationModel(returnedObject, thisJoinPoint, createComplaintBasedOnData.createdObjectLocation(), createComplaintBasedOnData.parametersLocation());
		CreationModel createdObjectModel = new CreationModel(returnedObject, thisJoinPoint, createComplaintBasedOnData.createdObjectLocation(), ParametersObjectsLocation.Property);
		Object createdObject = FieldFinder.getObjectToReadFrom(originalObjectModel, originalObject, createComplaintBasedOnData.name());
		if(createdObject == null)
		{
			System.out.println("Read from object is null - CreateComplaintBasedOnDataAspect");
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
			var complaintTypeObject = repo.getFactory().createComplaintBasedOnData();
			var complaintObject = repo.getFactory().createComplaint();
			complaintObject.setName((String)FieldFinder.getFieldValue(complaint.id(), createdObject, createdObjectClass));
			complaintObject.setWhen((Date)FieldFinder.getFieldValue(complaint.when(), createdObject, createdObjectClass));
			complaintTypeObject.setType(createComplaintBasedOnData.type());
			if(!complaint.reason().equals(Constants.Empty))
			{
				complaintObject.setReason((String)FieldFinder.getFieldValue(complaint.reason(), createdObject, createdObjectClass));
			}
			if(!createComplaintBasedOnData.subjects().equals(Constants.Empty))
			{
				var datas = ReadTypeByAttribute.getPrivacyDatasFromObject(originalObjectModel, originalObjectClass, originalObject, createComplaintBasedOnData.subjects(), model);
				if(!datas.isEmpty())
				{
					complaintTypeObject.getSubject().addAll(datas);
				}
			}
			if(!createComplaintBasedOnData.subjectsIds().equals(Constants.Empty))
			{
				var datas = ReadTypeByAttribute.getPrivacyDatasById(originalObjectModel, originalObjectClass, originalObject, createComplaintBasedOnData.subjectsIds(), model);
				if(!datas.isEmpty())
				{
					complaintTypeObject.getSubject().addAll(datas);
				}
			}
			if(!createComplaintBasedOnData.subject().equals(Constants.Empty))
			{
				var data = ObjectManager.tryGetPrivacyDataByFromObject(originalObjectModel, originalObject, originalObjectClass, createComplaintBasedOnData.subject(), model);
				if(!data.isEmpty())
				{
					complaintTypeObject.getSubject().add(data.get());
				}
			}
			if(!createComplaintBasedOnData.subjectId().equals(Constants.Empty))
			{
				var data = ObjectManager.tryGetPrivacyDataByById(originalObjectModel, originalObject, originalObjectClass, createComplaintBasedOnData.subjectId(), model);
				if(!data.isEmpty())
				{
					complaintTypeObject.getSubject().add(data.get());
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