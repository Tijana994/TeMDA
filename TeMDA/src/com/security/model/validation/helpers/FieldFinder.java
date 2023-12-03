package com.security.model.validation.helpers;

import java.lang.reflect.Field;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

public class FieldFinder {

	public static Object getFieldValue(String fieldName, Object annotationObject, Class<? extends Object> c)
	{
		try 
		{
			if(fieldName.equals(Constants.Empty))
			{
				System.out.println("Parameter name is empty");
				return null;
			}
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
	
	public static Optional<Object> getObjectToReadFrom(Object ret, Object obj, ParametersObjectsLocation location, 
			String name, JoinPoint jp)
	{
		if(location == ParametersObjectsLocation.Property)
		{
			if(name.equals(Constants.Empty))
			{
				System.out.println("Property name is empty");
				return Optional.empty();
			}
			return Optional.of(getFieldValue(name, obj, obj.getClass()));
		}
		else if(location == ParametersObjectsLocation.Parameter)
		{
			if(name.equals(Constants.Empty))
			{
				System.out.println("Parameter name is empty");
				return Optional.empty();
			}
			MethodSignature signature = (MethodSignature)jp.getSignature();
			String[] argNames = signature.getParameterNames();
	        Object[] values = jp.getArgs();
	        for (int i = 0; i < argNames.length; i++)
			{
				if(argNames[i].toLowerCase().equals(name.toLowerCase()))
				{
					return Optional.of(values[i]);
				}
			}
		}
		
		return Optional.empty();
	}
}

