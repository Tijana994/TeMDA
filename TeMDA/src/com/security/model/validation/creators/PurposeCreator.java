package com.security.model.validation.creators;

import java.lang.reflect.Field;
import java.util.List;

import org.aspectj.lang.JoinPoint;

import com.security.model.validation.annotations.PurposeAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;

import privacyModel.PrivacyModelFactory;
import privacyModel.Purpose;

public class PurposeCreator {

	public static Purpose createPurpose(Class<?> originalObjectClass, Object originalObject,
			String propertyName, ParametersObjectsLocation parametersLocation, PrivacyModelFactory factory, JoinPoint jp) {
		if(originalObject == null)
		{
			System.out.println("Object is not instantiated.");
			return null;
		}
		try
		{
			var purposeObject = FieldFinder.getObjectToReadFrom(originalObjectClass, originalObject, parametersLocation, propertyName, jp);
			if(purposeObject.isEmpty())
			{
				return null;
			}
			PurposeAnnotation purposeAnnotation = purposeObject.get().getClass().getAnnotation(PurposeAnnotation.class);
			if(purposeAnnotation == null)
			{
				System.out.println("There is no purpose annotation");
				return null;
			}
			else
			{
				return readComplexType(purposeObject.get(), purposeAnnotation, factory);
			}
		}
		catch(Exception e)
		{
			System.out.println("Field with name " + propertyName + " in purpose attribute caused an exception " + e);
		}
		return null;
	}

	private static Purpose readComplexType(Object originalObject, 
			PurposeAnnotation purposeAnnotation, PrivacyModelFactory factory)
			throws NoSuchFieldException, IllegalAccessException {
		String details = (String)FieldFinder.getFieldValue(purposeAnnotation.details(), originalObject, originalObject.getClass());
		var purposeObject = factory.createPurpose();
		purposeObject.setDetails(details);
		if(purposeAnnotation.subPurposes().equals(Constants.Empty)) return purposeObject;
		
		Field sub = originalObject.getClass().getDeclaredField(purposeAnnotation.subPurposes());
		if(!sub.getType().equals(List.class)) 
		{
			System.out.println("Subpurpose should be a list type.");
			return purposeObject;
		}

		Object value = sub.get(originalObject);
		if(value == null)
		{
			return purposeObject;
		}
		var list = (List<?>) value;
		
		for(var purpose : list)
		{
			PurposeAnnotation purposeAnnotation1 = purpose.getClass().getAnnotation(PurposeAnnotation.class);
			var subPurposeObject = readComplexType(purpose, purposeAnnotation1, factory);
			purposeObject.getSubPurposes().add(subPurposeObject);
		}
		return purposeObject;
	}
}
