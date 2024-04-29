package com.security.model.validation.creators;

import org.aspectj.lang.JoinPoint;

import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.ReadTypeByAttribute;

import privacyModel.PrivacyPolicy;
import privacyModel.Where;
import utility.PrivacyModelRepository;

public class WhereCreator {

	public static Boolean shouldCreate(CreatePolicyStatementAnnotation createPolicyStatement)
	{
		return !createPolicyStatement.whereDestinationId().equals(Constants.Empty) ||
				!createPolicyStatement.whereDestination().equals(Constants.Empty) ||
				!createPolicyStatement.whereSourceId().equals(Constants.Empty) ||
				!createPolicyStatement.whereSource().equals(Constants.Empty);
	}
	
	public static Where createWhere(CreatePolicyStatementAnnotation createPolicyStatement, Object originalObject,
			Class<? extends Object> originalObjectClass, PrivacyModelRepository repo, PrivacyPolicy model, 
			JoinPoint jp) {
		var where = repo.getFactory().createWhere();
		
		if(!createPolicyStatement.whereDestinationId().equals(Constants.Empty))
		{
			var locationId = FieldFinder.getObjectToReadFrom(originalObjectClass, originalObject, createPolicyStatement.parametersLocation(), createPolicyStatement.whereDestinationId(), jp);
			if(locationId.isPresent())
			{
				setLocationById(model, where, (String)locationId.get(), false);
			}
		}
		if(!createPolicyStatement.whereDestination().equals(Constants.Empty))
		{
			setLocationFromObject(originalObject, originalObjectClass, createPolicyStatement.whereDestination(), model, where, jp, createPolicyStatement.parametersLocation(), false);
		}
		if(!createPolicyStatement.whereSourceId().equals(Constants.Empty))
		{
			var locationId = FieldFinder.getObjectToReadFrom(originalObjectClass, originalObject, createPolicyStatement.parametersLocation(), createPolicyStatement.whereSourceId(), jp);
			if(locationId.isPresent())
			{
				setLocationById(model, where, (String)locationId.get(), true);
			}
		}
		if(!createPolicyStatement.whereSource().equals(Constants.Empty))
		{
			setLocationFromObject(originalObject, originalObjectClass, createPolicyStatement.whereSource(), model, where, jp, createPolicyStatement.parametersLocation(), true);
		}
		
		return where;
	}
	
	private static void setLocationFromObject(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, Where whereObject, JoinPoint jp, ParametersObjectsLocation parametersLocation,  Boolean source) {
		var locationId = ReadTypeByAttribute.getLocationIdFromObject(objectClass, obj, propertyName, parametersLocation, jp);
		if(locationId.isPresent())
		{
			var locationName = locationId.get();
			setLocationById(model, whereObject, locationName, source);
		}
	}
	
	private static void setLocationById(PrivacyPolicy model, Where whereObject, String id, Boolean source) {
		var location = ObjectFinder.checkIfLocationExists(id, model);
		if(location.isPresent())
		{
			if(source){
				whereObject.setSource(location.get());
			}
			else {
				whereObject.setDestination(location.get());
			}
		}
	}
}
