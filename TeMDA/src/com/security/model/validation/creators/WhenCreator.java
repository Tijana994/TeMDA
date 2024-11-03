package com.security.model.validation.creators;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Optional;

import com.security.model.validation.annotations.TimeStatementAnnotation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.interfaces.ILogger;
import com.security.model.validation.models.CreationModel;
import com.security.model.validation.models.ParametersAnnotations;

import privacyModel.AbstractTime;
import privacyModel.PrivacyModelFactory;
import privacyModel.TimeStatement;

public class WhenCreator {
	public static AbstractTime createWhen(CreationModel creationModel, String when, PrivacyModelFactory factory, 
			ParametersAnnotations parametersAnnotation, FieldFinder fieldFinder, ILogger logger)
	{
		try
		{
			var whens = when.split(",", 2);
			if(whens.length == 1)
			{
				var time = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), whens[0]);
				return createTimeStatement(time, factory, parametersAnnotation, creationModel, whens[0], logger);
			}
			else if(whens.length == 2)
			{
				var interval = factory.createTimeInterval();
				var start = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), whens[0]);
				interval.setStart(createTimeStatement(start, factory, parametersAnnotation, creationModel, whens[0], logger));
				
				var end = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), whens[1]);
				interval.setEnd(createTimeStatement(end, factory, parametersAnnotation, creationModel, whens[1], logger));
				
				return interval;
			}
			logger.LogErrorMessage("When should have 1 or 2 parameters");
			return null;
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return null;
	}
	
	private static TimeStatement createTimeStatement(Optional<Object> dateObject, PrivacyModelFactory factory, 
			ParametersAnnotations parametersAnnotation, CreationModel creationModel, 
			String propertyName, ILogger logger)
	{
		if(dateObject.isEmpty() || !(dateObject.get() instanceof  Date))
		{
			logger.LogErrorMessage("When object is not instantiated.");
			return null;
		}
		TimeStatementAnnotation timeAnnotation = getTimeAnnotation(dateObject, parametersAnnotation, 
				creationModel, propertyName);
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
			ParametersAnnotations parametersAnnotation, CreationModel creationModel,
			String propertyName) {
		TimeStatementAnnotation timeAnnotation = null;
		if(creationModel.getParametersLocation().equals(ParametersObjectsLocation.Property))
		{
			timeAnnotation = dateObject.get().getClass().getAnnotation(TimeStatementAnnotation.class);
		}
		else if(creationModel.getParametersLocation().equals(ParametersObjectsLocation.Parameter))
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
		else if(creationModel.getParametersLocation().equals(ParametersObjectsLocation.PropertyInReturnedObject))
		{
	        Field[] fields = creationModel.getReturnedObjectClass().getDeclaredFields();
	        for (Field field : fields) {
	            if (field.getName().equals(propertyName) && field.isAnnotationPresent(TimeStatementAnnotation.class)) {
	                timeAnnotation = (TimeStatementAnnotation)field.getAnnotation(TimeStatementAnnotation.class);
	                break;
	            }
	        }
		}
		else if(creationModel.getParametersLocation().equals(ParametersObjectsLocation.PropertyInParameterObject))
		{
	        Field[] fields = creationModel.getParameterObjectClass().getDeclaredFields();
	        for (Field field : fields) {
	            if (field.getName().equals(propertyName) && field.isAnnotationPresent(TimeStatementAnnotation.class)) {
	                timeAnnotation = (TimeStatementAnnotation)field.getAnnotation(TimeStatementAnnotation.class);
	                break;
	            }
	        }
		}
		return timeAnnotation;
	}
}
