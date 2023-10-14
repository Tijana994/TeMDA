package com.security.model.validation.creators;

import java.lang.reflect.Field;
import java.util.Date;

import com.security.model.validation.annotations.TimeStatementAnnotation;

public class WhenCreator {
	public static void CreateWhen(Class<?> c, Object obj, String when)
	{
		try
		{
			var whens = when.split(",", 2);
			for (String date : whens) 
			{ 
				Field start = c.getDeclaredField(date);
				CreateTimeStatement(start, start.get(obj));
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	private static void CreateTimeStatement(Field field, Object obj)
	{
		if(obj == null || !(obj instanceof  Date))
		{
			System.out.println("Object is not instantiated.");
			return;
		}
		TimeStatementAnnotation timeAnnotation = field.getAnnotation(TimeStatementAnnotation.class);
		if(timeAnnotation == null)
		{
			System.out.println("There is no time statement annotation");
		}
		else
		{
			System.out.println("Preposition " + timeAnnotation.preposition() + ((Date)obj));
		}
	}
}
