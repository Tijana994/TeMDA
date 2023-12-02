package com.security.model.validation.creators;

import java.lang.reflect.Field;
import java.util.Date;

import com.security.model.validation.annotations.TimeStatementAnnotation;

import privacyModel.AbstractTime;
import privacyModel.PrivacyModelFactory;
import privacyModel.TimeStatement;

public class WhenCreator {
	public static AbstractTime createWhen(Class<?> c, Object obj, String when, PrivacyModelFactory factory)
	{
		try
		{
			var whens = when.split(",", 2);
			if(whens.length == 1)
			{
				Field time = c.getDeclaredField(whens[0]);
				return createTimeStatement(time, time.get(obj), factory);
			}
			else if(whens.length == 2)
			{
				var interval = factory.createTimeInterval();
				Field start = c.getDeclaredField(whens[0]);
				interval.setStart(createTimeStatement(start, start.get(obj), factory));
				
				Field end = c.getDeclaredField(whens[1]);
				interval.setEnd(createTimeStatement(end, end.get(obj), factory));
				
				return interval;
			}
			System.out.println("Object is not instantiated.");
			return null;
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return null;
	}
	
	private static TimeStatement createTimeStatement(Field field, Object obj, PrivacyModelFactory factory)
	{
		if(obj == null || !(obj instanceof  Date))
		{
			System.out.println("Object is not instantiated.");
			return null;
		}
		TimeStatementAnnotation timeAnnotation = field.getAnnotation(TimeStatementAnnotation.class);
		if(timeAnnotation == null)
		{
			System.out.println("There is no time statement annotation");
			return null;
		}
		var time = factory.createTimeStatement();
		time.setDateTime((Date)obj);
		time.setPreposition(timeAnnotation.preposition());

		return time;
	}
}
