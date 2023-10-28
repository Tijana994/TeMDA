package com.security.model.validation.helpers;

import java.lang.reflect.Field;
import java.util.Optional;

import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

public class ReadTypeByAttribute {

	public static Optional<String> getConsentIdFromObject(Class<?> objectClass, Object obj, String consentName, ParametersObjectsLocation parametersLocation)
	{
		if(parametersLocation == ParametersObjectsLocation.Property)
		{
			if(obj == null)
			{
				System.out.println("Object is not instantiated.");
				return Optional.empty();
			}
			try
			{
				Field consent = objectClass.getDeclaredField(consentName);
				consent.setAccessible(true);
				Object value = consent.get(obj);
				PaperAnnotation paper = consent.getType().getAnnotation(PaperAnnotation.class);
				if(paper == null)
				{
					System.out.println("There is no paper annotation");
				}
				else
				{
					var consentId = (String)FieldFinder.getFieldValue(paper.id(), value, consent.getType());
					return Optional.of(consentId);
				}
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
		else if(parametersLocation == ParametersObjectsLocation.Parameter)
		{
			
		}
		return Optional.empty();
	}
}
