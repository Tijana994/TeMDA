package com.security.model.validation.creators;

import java.lang.reflect.Field;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;

public class FieldCreator {

	public static Object getFieldValue(String fieldName, Object annotationObject, Class<? extends Object> c)
	{
		try 
		{
			Field field = c.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(annotationObject);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return null;
	}
	
	public static Object getObjectToReadFrom(Object ret, Object obj, CreatedObjectLocation location, String name, JoinPoint jp)
	{
		Object retFromObj = null;
		if(location == CreatedObjectLocation.Return)
		{
			retFromObj = ret;
		}
		else if(location == CreatedObjectLocation.Property)
		{
			if(name.equals(Constants.Empty))
			{
				System.out.println("Property name is empty");
				return retFromObj;
			}
			retFromObj = getFieldValue(name, obj, obj.getClass());
		}
		else if(location == CreatedObjectLocation.Parameter)
		{
			if(name.equals(Constants.Empty))
			{
				System.out.println("Parameter name is empty");
				return retFromObj;
			}
			
			MethodSignature signature = (MethodSignature)jp.getSignature();
			String[] argNames = signature.getParameterNames();
	        Object[] values = jp.getArgs();
	        for (int i = 0; i < argNames.length; i++)
			{
				if(argNames[i].toLowerCase().equals(name.toLowerCase()))
				{
					retFromObj = values[i];
					break;
				}
			}
		}
		
		return retFromObj;
	}
}

