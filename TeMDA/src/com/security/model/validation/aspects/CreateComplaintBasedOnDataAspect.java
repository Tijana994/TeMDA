package com.security.model.validation.aspects;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.security.model.validation.annotations.ComplaintAnnotation;
import com.security.model.validation.annotations.creators.CreateComplaintBasedOnDataAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class CreateComplaintBasedOnDataAspect extends BaseAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateComplaintBasedOnDataAnnotation)")
	void function() {}
	@Around("function()")
	public Object createComplaintBasedOnData(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		SetUp(thisJoinPoint);
	    CreateComplaintBasedOnDataAnnotation createComplaintBasedOnData = method.getAnnotation(CreateComplaintBasedOnDataAnnotation.class);
		if(createComplaintBasedOnData == null)
		{
			Logger.LogErrorMessage("There is no create complaint based on data annotation");
			return returnedObject;
		}
		CreationModel originalObjectModel = new CreationModel(returnedObject, originalObject, thisJoinPoint, createComplaintBasedOnData.createdObjectLocation(), createComplaintBasedOnData.parametersLocation());
		CreationModel createdObjectModel = new CreationModel(returnedObject, thisJoinPoint, createComplaintBasedOnData.createdObjectLocation(), ParametersObjectsLocation.Property);
		createdObjectModel.setObject(FieldFinder.getCreatedObjectToReadFrom(originalObjectModel, originalObject, createComplaintBasedOnData.name()));
		if(createdObjectModel.getObject() == null)
		{
			Logger.LogErrorMessage("Read from object is null - CreateComplaintBasedOnDataAspect");
			return returnedObject;
		}
		ComplaintAnnotation complaint = createdObjectModel.getObjectClass().getAnnotation(ComplaintAnnotation.class);
		if(complaint == null)
		{
			Logger.LogErrorMessage("There is no complaint annotation");
			return returnedObject;
		}
		
		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var complaintTypeObject = repo.getFactory().createComplaintBasedOnData();
			var complaintObject = repo.getFactory().createComplaint();
			complaintObject.setName((String)FieldFinder.getFieldValue(complaint.id(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			complaintObject.setWhen((Date)FieldFinder.getFieldValue(complaint.when(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			complaintTypeObject.setType(createComplaintBasedOnData.type());
			if(!complaint.reason().equals(Constants.Empty))
			{
				complaintObject.setReason((String)FieldFinder.getFieldValue(complaint.reason(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			}
			if(!createComplaintBasedOnData.subjects().equals(Constants.Empty))
			{
				var datas = ReadTypeByAttribute.getPrivacyDatasFromObject(originalObjectModel, createComplaintBasedOnData.subjects(), model);
				if(!datas.isEmpty())
				{
					complaintTypeObject.getSubject().addAll(datas);
				}
			}
			if(!createComplaintBasedOnData.subjectsIds().equals(Constants.Empty))
			{
				var datas = ReadTypeByAttribute.getPrivacyDatasById(originalObjectModel, createComplaintBasedOnData.subjectsIds(), model);
				if(!datas.isEmpty())
				{
					complaintTypeObject.getSubject().addAll(datas);
				}
			}
			if(!createComplaintBasedOnData.subject().equals(Constants.Empty))
			{
				var data = ObjectManager.tryGetPrivacyDataByFromObject(originalObjectModel, createComplaintBasedOnData.subject(), model);
				if(!data.isEmpty())
				{
					complaintTypeObject.getSubject().add(data.get());
				}
			}
			if(!createComplaintBasedOnData.subjectId().equals(Constants.Empty))
			{
				var data = ObjectManager.tryGetPrivacyDataByById(originalObjectModel, createComplaintBasedOnData.subjectId(), model);
				if(!data.isEmpty())
				{
					complaintTypeObject.getSubject().add(data.get());
				}
			}
			if(!complaint.who().equals(Constants.Empty)) 
			{
				var principal = ObjectManager.tryGetPrincipalByFromObject(createdObjectModel, complaint.who(), model);
				if(principal.isPresent())
				{
					complaintObject.setWho(principal.get());
				}
			}
			if(!complaint.whoId().equals(Constants.Empty))
			{
				var principal = ObjectManager.tryGetPrincipalByById(createdObjectModel, complaint.whoId(), model);
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
			Logger.LogExceptionMessage(ex);
		}
		
		return returnedObject;
	}
}