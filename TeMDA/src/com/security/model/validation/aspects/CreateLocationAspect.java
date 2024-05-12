package com.security.model.validation.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.LocationAnnotation;
import com.security.model.validation.annotations.creators.CreateLocationAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.helpers.ReadTypeByAttribute;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class CreateLocationAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateLocationAnnotation)")
	void function() {}
	@Around("function()")
	public Object createLocation(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object returnedObject = thisJoinPoint.proceed(args);
		Object originalObject = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
		CreateLocationAnnotation createLocation = method.getAnnotation(CreateLocationAnnotation.class);
		if(createLocation == null)
		{
			System.out.println("There is no create location statement annotation");
			return returnedObject;
		}
		CreationModel createdObjectModel = new CreationModel(returnedObject, thisJoinPoint, createLocation.createdObjectLocation(), ParametersObjectsLocation.Property);
		Object createdObject = FieldFinder.getObjectToReadFrom(createdObjectModel, originalObject, createLocation.name());
		if(createdObject == null)
		{
			System.out.println("Read from object is null - CreateLocationAspect");
			return returnedObject;
		}
		Class<? extends Object> createdObjectClass = createdObject.getClass();
		LocationAnnotation location = createdObjectClass.getAnnotation(LocationAnnotation.class);
		
		if(location == null)
		{
			System.out.println("There is no location annotation");
			return returnedObject;
		}
		
		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var locationObject = repo.getFactory().createLocation();
			locationObject.setName((String)FieldFinder.getFieldValue(location.id(), createdObject, createdObjectClass));
			locationObject.setType(createLocation.locationType());
			locationObject.setIsEUMember(createLocation.isEUMember());
			locationObject.setLegalAgeLimit(createLocation.legalAgeLimit());
			if(!location.parent().equals(Constants.Empty)) 
			{
				var parent = ObjectManager.tryGetLocationFromObject(createdObjectModel, createdObject, createdObjectClass, location.parent(), model);
				if(parent.isPresent())
				{
					parent.get().getSubLocations().add(locationObject);
				}
			}
			if(!location.parentId().equals(Constants.Empty))
			{
				var parent = ObjectManager.tryGetLocationById(createdObjectModel, createdObject, createdObjectClass, location.parentId(), model);
				if(parent.isPresent())
				{
					parent.get().getSubLocations().add(locationObject);
				}
			}
			if(!location.subLocations().equals(Constants.Empty))
			{
				var locations = ReadTypeByAttribute.getLocationsFromObject(createdObjectModel, createdObjectClass, createdObject, location.subLocations(), model);
				if(!locations.isEmpty())
				{
					locationObject.getSubLocations().addAll(locations);
				}
			}
			if(!location.subLocationsIds().equals(Constants.Empty))
			{
				var locations = ReadTypeByAttribute.getLocationsById(createdObjectModel, createdObjectClass, createdObject, location.subLocationsIds(), model);
				if(!locations.isEmpty())
				{
					locationObject.getSubLocations().addAll(locations);
				}
			}
			
			if(!location.subLocation().equals(Constants.Empty)) 
			{
				var subLocation = ObjectManager.tryGetLocationFromObject(createdObjectModel, createdObject, createdObjectClass, location.subLocation(), model);
				if(subLocation.isPresent())
				{
					locationObject.getSubLocations().add(subLocation.get());
				}
			}
			if(!location.subLocationId().equals(Constants.Empty))
			{
				var subLocation = ObjectManager.tryGetLocationById(createdObjectModel, createdObject, createdObjectClass, location.subLocationId(), model);
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
			System.out.println(ex);
		}
		
		return returnedObject;
	}
}
