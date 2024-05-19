package com.security.model.validation.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.security.model.validation.annotations.LocationAnnotation;
import com.security.model.validation.annotations.creators.CreateLocationAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class CreateLocationAspect extends BaseAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateLocationAnnotation)")
	void function() {}
	@Around("function()")
	public Object createLocation(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		SetUp(thisJoinPoint);
		CreateLocationAnnotation createLocation = method.getAnnotation(CreateLocationAnnotation.class);
		if(createLocation == null)
		{
			Logger.LogErrorMessage("There is no create location statement annotation");
			return returnedObject;
		}
		CreationModel createdObjectModel = new CreationModel(returnedObject, thisJoinPoint, createLocation.createdObjectLocation(), ParametersObjectsLocation.Property);
		createdObjectModel.setObject(FieldFinder.getCreatedObjectToReadFrom(createdObjectModel, originalObject, createLocation.name()));
		if(createdObjectModel.getObject() == null)
		{
			Logger.LogErrorMessage("Read from object is null - CreateLocationAspect");
			return returnedObject;
		}
		LocationAnnotation location = createdObjectModel.getObjectClass().getAnnotation(LocationAnnotation.class);
		if(location == null)
		{
			Logger.LogErrorMessage("There is no location annotation");
			return returnedObject;
		}
		
		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var locationObject = repo.getFactory().createLocation();
			locationObject.setName((String)FieldFinder.getFieldValue(location.id(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			locationObject.setType(createLocation.locationType());
			locationObject.setIsEUMember(createLocation.isEUMember());
			locationObject.setLegalAgeLimit(createLocation.legalAgeLimit());
			if(!location.parent().equals(Constants.Empty)) 
			{
				var parent = ObjectManager.tryGetLocationFromObject(createdObjectModel, location.parent(), model);
				if(parent.isPresent())
				{
					parent.get().getSubLocations().add(locationObject);
				}
			}
			if(!location.parentId().equals(Constants.Empty))
			{
				var parent = ObjectManager.tryGetLocationById(createdObjectModel, location.parentId(), model);
				if(parent.isPresent())
				{
					parent.get().getSubLocations().add(locationObject);
				}
			}
			if(!location.subLocations().equals(Constants.Empty))
			{
				var locations = ReadTypeByAttribute.getLocationsFromObject(createdObjectModel, location.subLocations(), model);
				if(!locations.isEmpty())
				{
					locationObject.getSubLocations().addAll(locations);
				}
			}
			if(!location.subLocationsIds().equals(Constants.Empty))
			{
				var locations = ReadTypeByAttribute.getLocationsById(createdObjectModel, location.subLocationsIds(), model);
				if(!locations.isEmpty())
				{
					locationObject.getSubLocations().addAll(locations);
				}
			}
			
			if(!location.subLocation().equals(Constants.Empty)) 
			{
				var subLocation = ObjectManager.tryGetLocationFromObject(createdObjectModel, location.subLocation(), model);
				if(subLocation.isPresent())
				{
					locationObject.getSubLocations().add(subLocation.get());
				}
			}
			if(!location.subLocationId().equals(Constants.Empty))
			{
				var subLocation = ObjectManager.tryGetLocationById(createdObjectModel, location.subLocationId(), model);
				if(subLocation.isPresent())
				{
					locationObject.getSubLocations().add(subLocation.get());
				}
			}
			
			model.getLocations().add(locationObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			Logger.LogExceptionMessage(ex);
		}
		
		return returnedObject;
	}
}
