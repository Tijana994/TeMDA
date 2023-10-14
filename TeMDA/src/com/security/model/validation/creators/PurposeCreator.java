package com.security.model.validation.creators;

import java.lang.reflect.Field;
import java.util.List;

import com.security.model.validation.annotations.PurposeAnnotation;
import com.security.model.validation.annotations.enums.Constants;

public class PurposeCreator {

	public static void CreatePurpose(Class<?> objectClass, Object obj, String why)
	{
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return;
		}
		try
		{
			Field purpose = objectClass.getDeclaredField(why);
			PurposeAnnotation purposeAnnotation = purpose.getType().getAnnotation(PurposeAnnotation.class);
			if(purposeAnnotation == null)
			{
				System.out.println("There is no purpose annotation");
			}
			else
			{

				ReadComplexType(purpose, obj, purposeAnnotation);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	private static void ReadComplexType(Field field, Object obj, PurposeAnnotation purposeAnnotation)
			throws NoSuchFieldException, IllegalAccessException {
		Field details = field.getType().getDeclaredField(purposeAnnotation.details());
		System.out.println("Details: " + details.get(obj));
		if(purposeAnnotation.subPurposes().equals(Constants.Empty)) return;
		
		Field sub = field.getType().getDeclaredField(purposeAnnotation.subPurposes());
		if(!sub.getType().equals(List.class)) return;

		Object value = sub.get(obj);
		if(value == null) return;
		var list = (List<?>) value;
		
		for(var purpose : list)
		{
			System.out.println("List item");
			PurposeAnnotation purposeAnnotation1 = purpose.getClass().getAnnotation(PurposeAnnotation.class);
			ReadComplexType(field, purpose, purposeAnnotation1);
		}
	}
}
