package com.security.model.validation.models;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

public class CreationModel {
	
	public CreationModel(Object returnedObject, Object object, ProceedingJoinPoint joinPoint, CreatedObjectLocation createdLocation,
			ParametersObjectsLocation parametersLocation, String parameterObjectName){
		this(returnedObject, joinPoint, createdLocation, parametersLocation);
		this.object = object;
		if(parametersLocation == ParametersObjectsLocation.PropertyInParameterObject)
		{
			parameterObject = readObject(parameterObjectName);
		}
		this.parameterObjectClass = parameterObject == null ? null : parameterObject.getClass();
	}
	public CreationModel(Object returnedObject, ProceedingJoinPoint joinPoint, CreatedObjectLocation createdLocation,
			ParametersObjectsLocation parametersLocation) {
		super();
		this.returnedObject = returnedObject;
		this.returnedObjectClass = returnedObject == null ? null : returnedObject.getClass();
		this.joinPoint = joinPoint;
		this.createdLocation = createdLocation;
		this.parametersLocation = parametersLocation;
	}
	private Object returnedObject;
	private Class<? extends Object> returnedObjectClass;
	private ProceedingJoinPoint joinPoint;
	private CreatedObjectLocation createdLocation;
	private ParametersObjectsLocation parametersLocation;
	private Object object;
	private Class<? extends Object> objectClass;
	private Object parameterObject;
	private Class<? extends Object> parameterObjectClass;
	public Object getReturnedObject() {
		return returnedObject;
	}
	public Class<? extends Object> getReturnedObjectClass() {
		return returnedObjectClass;
	}
	public ProceedingJoinPoint getJoinPoint() {
		return joinPoint;
	}
	public CreatedObjectLocation getCreatedLocation() {
		return createdLocation;
	}
	public ParametersObjectsLocation getParametersLocation() {
		return parametersLocation;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object originalObject) {
		this.object = originalObject;
		this.objectClass = originalObject.getClass();
	}
	public Class<? extends Object> getObjectClass() {
		return objectClass;
	}
	public Object getParameterObject() {
		return parameterObject;
	}
	public Class<? extends Object> getParameterObjectClass() {
		return parameterObjectClass;
	}
	
	private Object readObject(String name)
	{
		Object retFromObj = null;
		if(name.equals(Constants.Empty))
		{
			//logger.LogErrorMessage("Parameter name is empty");
			return retFromObj;
		}
		
		MethodSignature signature = (MethodSignature)getJoinPoint().getSignature();
		String[] argNames = signature.getParameterNames();
        Object[] values = getJoinPoint().getArgs();
        for (int i = 0; i < argNames.length; i++)
		{
			if(argNames[i].toLowerCase().equals(name.toLowerCase()))
			{
				retFromObj = values[i];
				break;
			}
		}
	
        return retFromObj;
	}
}
