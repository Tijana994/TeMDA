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
			String when, ParametersObjectsLocation parametersLocation, PrivacyModelFactory factory, JoinPoint jp)
	{
		try
		{
			var whens = when.split(",", 2);
			if(whens.length == 1)
			{
				var time = FieldFinder.getObjectToReadFrom(originalObjectClass, originalObject, parametersLocation, whens[0], jp);
				return createTimeStatement(time, factory);
			}
			else if(whens.length == 2)
			{
				var interval = factory.createTimeInterval();
				var start = FieldFinder.getObjectToReadFrom(originalObjectClass, originalObject, parametersLocation, whens[0], jp);
				interval.setStart(createTimeStatement(start, factory));
				
				var end = FieldFinder.getObjectToReadFrom(originalObjectClass, originalObject, parametersLocation, whens[1], jp);
				interval.setEnd(createTimeStatement(end, factory));
				
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
	
	private static TimeStatement createTimeStatement(Optional<Object> dateObject, PrivacyModelFactory factory)
	{
		if(dateObject.isEmpty() || !(dateObject.get() instanceof  Date))
		{
			System.out.println("When object is not instantiated.");
			return null;
		}
		TimeStatementAnnotation timeAnnotation = dateObject.get().getClass().getAnnotation(TimeStatementAnnotation.class);
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
}
