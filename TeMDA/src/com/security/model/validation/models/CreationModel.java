package com.security.model.validation.models;

import org.aspectj.lang.ProceedingJoinPoint;

import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

public class CreationModel {
	
	public CreationModel(Object returnedObject, Object object, ProceedingJoinPoint joinPoint, CreatedObjectLocation createdLocation,
			ParametersObjectsLocation parametersLocation){
		this(returnedObject, joinPoint, createdLocation, parametersLocation);
		this.object = object;
	}
	public CreationModel(Object returnedObject, ProceedingJoinPoint joinPoint, CreatedObjectLocation createdLocation,
			ParametersObjectsLocation parametersLocation) {
		super();
		this.returnedObject = returnedObject;
		this.returnedObjectClass = returnedObject.getClass();
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
}
