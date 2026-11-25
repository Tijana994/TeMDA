package com.security.model.validation.aspects;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.security.model.validation.annotations.updaters.UpdateComplaintAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class UpdateComplaintAspect extends BaseAspect {
	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.updaters.UpdateComplaintAnnotation)")
	void function() {}
	@Around("function()")
	public Object updateComplaint(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		SetUp(thisJoinPoint);
		UpdateComplaintAnnotation updatedComplaint = method.getAnnotation(UpdateComplaintAnnotation.class);
		if(updatedComplaint == null)
		{
			Logger.LogErrorMessage("There is no update complaint annotation");
			return returnedObject;
		}
		CreationModel originalObjectModel = new CreationModel(returnedObject, originalObject, thisJoinPoint, CreatedObjectLocation.Return,
				updatedComplaint.parametersLocation(), updatedComplaint.propertyObjectName());
		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			Date responseDeadline;
			var obj = FieldFinder.getObjectToReadFrom(originalObjectModel, originalObjectModel.getObjectClass(), originalObjectModel.getObject(), updatedComplaint.responseDeadline());
			if(obj.isPresent())
			{
				responseDeadline = (Date)obj.get();
			}
			else
			{
				return returnedObject;
			}
			if(!updatedComplaint.complaint().equals(Constants.Empty))
			{
				var complaint = ObjectManager.tryGetComplaintFromObject(originalObjectModel, updatedComplaint.complaint(), model);
				if(complaint.isPresent())
				{
					complaint.get().setResponseDeadline(responseDeadline);
				}
			}
			if(!updatedComplaint.complaintId().equals(Constants.Empty))
			{
				var complaint = ObjectManager.tryGetComplaintById(originalObjectModel, updatedComplaint.complaintId(), model);
				if(complaint.isPresent())
				{
					complaint.get().setResponseDeadline(responseDeadline);
				}
			}
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			Logger.LogExceptionMessage(ex);
		}
		
		return returnedObject;
	}
}
