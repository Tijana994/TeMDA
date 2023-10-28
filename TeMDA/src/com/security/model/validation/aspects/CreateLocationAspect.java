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
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;

import privacyModel.Location;
import privacyModel.PrivacyPolicy;
import utility.PrivacyModelRepository;

@Aspect
public class CreateLocationAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateLocationAnnotation)")
	void function() {}
	@Around("function()")
	public Object createLocation(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object ret = thisJoinPoint.proceed(args);
		Object obj = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
		CreateLocationAnnotation createLocation = method.getAnnotation(CreateLocationAnnotation.class);
		if(createLocation == null)
		{
			System.out.println("There is no create location statement annotation");
			return ret;
		}
		Object retFromObj = FieldFinder.getObjectToReadFrom(ret, obj, createLocation.createdObjectLocation(), createLocation.name(), thisJoinPoint);
		if(retFromObj == null)
		{
			System.out.println("Read from object is null - CreateLocationAspect");
			return ret;
		}
		Class<? extends Object> retClass = retFromObj.getClass();
		LocationAnnotation location = retClass.getAnnotation(LocationAnnotation.class);
		
		if(location == null)
		{
			System.out.println("There is no location annotation");
			return ret;
		}
		
		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var locationObject = repo.getFactory().createLocation();
			var name = (String)FieldFinder.getFieldValue(location.id(), retFromObj, retClass);
			locationObject.setName(name);
			locationObject.setType(createLocation.locationType());
			locationObject.setIsEUMember(createLocation.isEUMember());
			locationObject.setLegalAgeLimit(createLocation.legalAgeLimit());
			if(!location.parent().equals(Constants.Undefined)) 
			{
				
			}
			if(!location.parentId().equals(Constants.Undefined))
			{
				setParentById(retFromObj, retClass, location, locationObject, model);
			}
			if(!location.childrens().equals(Constants.Undefined))
			{
				
			}
			if(!location.childrensIds().equals(Constants.Undefined))
			{
				
			}
			
			model.getLocations().add(locationObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return ret;
	}
	
	private void setParentById(Object retFromObj, Class<? extends Object> retClass, LocationAnnotation location,
			Location locationObject, PrivacyPolicy model) {
		var parentId = (String)FieldFinder.getFieldValue(location.parentId(), retFromObj, retClass);
		var parent = ObjectFinder.checkIfLocationExists(parentId, model);
		if(parent.isPresent())
		{
			parent.get().getSubLocations().add(locationObject);
		}
	}
}
