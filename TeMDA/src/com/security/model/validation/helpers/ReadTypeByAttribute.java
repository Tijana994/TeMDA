package com.security.model.validation.helpers;

import java.util.Optional;

import org.aspectj.lang.JoinPoint;

import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

public class ReadTypeByAttribute {

	public static Optional<String> getConsentIdFromObject(Class<?> objectClass, Object obj, String consentName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp)
	{
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, consentName, jp);
			if(!value.isPresent())
			{
				return Optional.empty();
			}
			PaperAnnotation paper = value.get().getClass().getAnnotation(PaperAnnotation.class);
			if(paper == null)
			{
				System.out.println("There is no paper annotation");
			}
			else
			{
				var consentId = (String)FieldFinder.getFieldValue(paper.id(), value.get(), value.get().getClass());
				return Optional.of(consentId);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return Optional.empty();
	}
}
