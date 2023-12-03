package com.security.model.validation.creators;

import java.lang.reflect.Field;
import java.util.List;

import com.security.model.validation.annotations.PurposeAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;

import privacyModel.PrivacyModelFactory;
import privacyModel.Purpose;

public class PurposeCreator {

	public static Purpose createPurpose(Class<?> objectClass, Object obj,
			String why, PrivacyModelFactory factory)
	{
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return null;
		}
		try
		{
			//Field purpose = objectClass.getDeclaredField(why);
			Object purposeObject = FieldFinder.getFieldValue(why,obj,objectClass);//purpose.get(obj);
			PurposeAnnotation purposeAnnotation = purposeObject.getClass().getAnnotation(PurposeAnnotation.class);
			if(purposeAnnotation == null)
			{
				System.out.println("There is no purpose annotation");
				return null;
			}
			else
			{
				return readComplexType(purposeObject, purposeAnnotation, factory);
			}
		}
		catch(Exception e)
		{
			System.out.println("Field with name " + why + " in purpose attribute caused an exception " + e);
		}
		return null;
	}

	private static Purpose readComplexType(Object obj, 
			PurposeAnnotation purposeAnnotation, PrivacyModelFactory factory)
			throws NoSuchFieldException, IllegalAccessException {
		String details = (String)FieldFinder.getFieldValue(purposeAnnotation.details(), obj, obj.getClass());
		var purposeObject = factory.createPurpose();
		purposeObject.setDetails(details);
		if(purposeAnnotation.subPurposes().equals(Constants.Empty)) return purposeObject;
		
		Field sub = obj.getClass().getDeclaredField(purposeAnnotation.subPurposes());
		if(!sub.getType().equals(List.class)) 
		{
			System.out.println("Subpurpose should be a list type.");
			return purposeObject;
		}

		Object value = sub.get(obj);
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
