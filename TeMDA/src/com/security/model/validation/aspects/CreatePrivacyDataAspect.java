package com.security.model.validation.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.security.model.validation.annotations.PrivacyDataAnnotation;
import com.security.model.validation.annotations.creators.CreatePrivacyDataAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class CreatePrivacyDataAspect extends BaseAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreatePrivacyDataAnnotation)")
	void function() {}
	@Around("function()")
	public Object createPrincipal(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		SetUp(thisJoinPoint);
	    CreatePrivacyDataAnnotation createPrivacyData = method.getAnnotation(CreatePrivacyDataAnnotation.class);
		if(createPrivacyData == null)
		{
			Logger.LogErrorMessage("There is no create privacy data annotation");
			return returnedObject;
		}
		CreationModel originalObjectModel = new CreationModel(returnedObject, originalObject, thisJoinPoint, createPrivacyData.createdObjectLocation(),
				createPrivacyData.parametersLocation(), createPrivacyData.propertyObjectName());
		Object createdObject = FieldFinder.getCreatedObjectToReadFrom(originalObjectModel, originalObject, createPrivacyData.name());
		if(createdObject == null)
		{
			Logger.LogErrorMessage("Read from object is null = CreatePrivacyDataAspect");
			return returnedObject;
		}
		Class<? extends Object> createdObjectClass = createdObject.getClass();
		PrivacyDataAnnotation privacyData = createdObjectClass.getAnnotation(PrivacyDataAnnotation.class);
		if(privacyData == null)
		{
			Logger.LogErrorMessage("There is no principal annotation");
			return returnedObject;
		}

		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var privacyDataObject = repo.getFactory().createPrivacyData();
			privacyDataObject.setName((String)FieldFinder.getFieldValue(privacyData.id(), createdObject, createdObjectClass));
			privacyDataObject.setType(createPrivacyData.type());
			model.getAllDatas().add(privacyDataObject);
			if(createPrivacyData.createSharedPrivacyData())
			{
				var sharedPrivacyDataObject = repo.getFactory().createSharedPrivacyData();
				sharedPrivacyDataObject.setName(privacyDataObject.getName());
				sharedPrivacyDataObject.setCollectedFromSubject(createPrivacyData.collectedFromSubject());
				if(!createPrivacyData.privacyDataSource().equals(Constants.Empty))
				{
					var privacyDataSource = FieldFinder.getObjectToReadFrom(originalObjectModel, originalObjectClass, originalObject, createPrivacyData.privacyDataSource());
					if(privacyDataSource.isPresent())
					{
						sharedPrivacyDataObject.setDataSource((String)privacyDataSource.get());
					}
				}

				sharedPrivacyDataObject.setPrivacydata(privacyDataObject);
				model.getAllSharedPrivacyData().add(sharedPrivacyDataObject);
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
