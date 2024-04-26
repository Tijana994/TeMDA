package com.security.model.validation.creators;

import java.util.Date;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;

import com.security.model.validation.annotations.TimeStatementAnnotation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;

import privacyModel.AbstractTime;
import privacyModel.PrivacyModelFactory;
import privacyModel.TimeStatement;

public class WhenCreator {
	public static AbstractTime createWhen(Class<?> originalObjectClass, Object originalObject, 
			String when, ParametersObjectsLocation parametersLocation, PrivacyModelFactory factory, 
			JoinPoint jp, ParametersAnnotations parametersAnnotation)
	{
		try
		{
			var whens = when.split(",", 2);
			if(whens.length == 1)
			{
				var time = FieldFinder.getObjectToReadFrom(originalObjectClass, originalObject, parametersLocation, whens[0], jp);
				return createTimeStatement(time, factory, parametersAnnotation, parametersLocation, whens[0]);
			}
			else if(whens.length == 2)
			{
				var interval = factory.createTimeInterval();
				var start = FieldFinder.getObjectToReadFrom(originalObjectClass, originalObject, parametersLocation, whens[0], jp);
				interval.setStart(createTimeStatement(start, factory, parametersAnnotation, parametersLocation, whens[0]));
				
				var end = FieldFinder.getObjectToReadFrom(originalObjectClass, originalObject, parametersLocation, whens[1], jp);
				interval.setEnd(createTimeStatement(end, factory, parametersAnnotation, parametersLocation, whens[1]));
				
				return interval;
			}
			System.out.println("When should have 1 or 2 parameters");
			return null;
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return null;
	}
	
	private static TimeStatement createTimeStatement(Optional<Object> dateObject, PrivacyModelFactory factory, 
			ParametersAnnotations parametersAnnotation, ParametersObjectsLocation parametersLocation, String propertyName)
	{
		if(dateObject.isEmpty() || !(dateObject.get() instanceof  Date))
		{
			System.out.println("When object is not instantiated.");
			return null;
		}
		TimeStatementAnnotation timeAnnotation = getTimeAnnotation(dateObject, parametersAnnotation, parametersLocation,
				propertyName);
		if(timeAnnotation == null)
		{
			System.out.println("There is no time statement annotation");
			return null;
		}
		var time = factory.createTimeStatement();
		time.setDateTime((Date)dateObject.get());
		time.setPreposition(timeAnnotation.preposition());

		return time;
	}

	private static TimeStatementAnnotation getTimeAnnotation(Optional<Object> dateObject,
			ParametersAnnotations parametersAnnotation, ParametersObjectsLocation parametersLocation,
			String propertyName) {
		TimeStatementAnnotation timeAnnotation = null;
		if(parametersLocation.equals(ParametersObjectsLocation.Property))
		{
			timeAnnotation = dateObject.get().getClass().getAnnotation(TimeStatementAnnotation.class);
		}
		else if(parametersLocation.equals(ParametersObjectsLocation.Parameter))
		{
			for(int i = 0; i < parametersAnnotation.getParameterNames().length; i++)
			{
				if(parametersAnnotation.getParameterNames()[i].equals(propertyName))
				{
					for(int column = 0; column < parametersAnnotation.getAnnotations()[i].length; column++)
					{
						if(parametersAnnotation.getAnnotations()[i][column].annotationType().equals(TimeStatementAnnotation.class))
						{
							timeAnnotation = (TimeStatementAnnotation)parametersAnnotation.getAnnotations()[i][column];
						}
					}
				}
			}
		}
		return timeAnnotation;
	}
}
