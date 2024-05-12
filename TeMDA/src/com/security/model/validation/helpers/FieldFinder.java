package com.security.model.validation.helpers;

import java.lang.reflect.Field;
import java.util.Optional;

import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.models.CreationModel;

public class FieldFinder {

	public static Object getFieldValue(String fieldName, Object object, Class<? extends Object> objectClass)
	{
		try 
		{
			if(fieldName.equals(Constants.Empty))
			{
				System.out.println("Parameter name is empty");
				return null;
			}
			Field field = objectClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(object);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return null;
	}
	
	public static Object getCreatedObjectToReadFrom(CreationModel creationModel, Object object, String name)
	{
		Object retFromObj = null;
		if(creationModel.getCreatedLocation() == CreatedObjectLocation.Return)
		{
			retFromObj = creationModel.getReturnedObject();
		}
		else if(creationModel.getCreatedLocation() == CreatedObjectLocation.Property)
		{
			if(name.equals(Constants.Empty))
			{
				System.out.println("Property name is empty");
				return retFromObj;
			}
			retFromObj = getFieldValue(name, object, object.getClass());
		}
		else if(creationModel.getCreatedLocation() == CreatedObjectLocation.Parameter)
		{
			if(name.equals(Constants.Empty))
			{
				System.out.println("Parameter name is empty");
				return retFromObj;
			}
			
			MethodSignature signature = (MethodSignature)creationModel.getJoinPoint().getSignature();
			String[] argNames = signature.getParameterNames();
	        Object[] values = creationModel.getJoinPoint().getArgs();
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
	
	public static Optional<Object> getObjectToReadFrom(CreationModel creationModel, Class<? extends Object> objectClass, Object obj, String name)
	{
		if(creationModel.getParametersLocation() == ParametersObjectsLocation.Property)
		{
			if(name.equals(Constants.Empty))
			{
				System.out.println("Property name is empty");
				return Optional.empty();
			}
			var value = getFieldValue(name, obj, objectClass);
			if(value == null)
			{
				System.out.println("Property " + name + " should be instatiate");
				return Optional.empty();
			}
			return Optional.of(value);
		}
		else if(creationModel.getParametersLocation() == ParametersObjectsLocation.Parameter)
		{
			if(name.equals(Constants.Empty))
			{
				System.out.println("Parameter name is empty");
				return Optional.empty();
			}
			MethodSignature signature = (MethodSignature)creationModel.getJoinPoint().getSignature();
			String[] argNames = signature.getParameterNames();
	        Object[] values = creationModel.getJoinPoint().getArgs();
	        for (int i = 0; i < argNames.length; i++)
			{
				if(argNames[i].toLowerCase().equals(name.toLowerCase()))
				{
					if(values[i] == null)
					{
						System.out.println("Parameter " + name + " should be instatiate");
						return Optional.empty();
					}
					return Optional.of(values[i]);
				}
			}

			System.out.println("Parameter " + name + " does not exist.");
			return Optional.empty();
		}
		else if (creationModel.getParametersLocation() == ParametersObjectsLocation.PropertyInReturnedObject)
		{
			if(name.equals(Constants.Empty))
			{
				System.out.println("Property name is empty");
				return Optional.empty();
			}
			var value = getFieldValue(name, creationModel.getReturnedObject(), creationModel.getReturnedObjectClass());
			if(value == null)
			{
				System.out.println("Property " + name + " should be instatiate");
				return Optional.empty();
			}
			return Optional.of(value);
		}
		
		return Optional.empty();
	}
}

