package com.security.model.validation.models;

import org.aspectj.lang.ProceedingJoinPoint;

import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

public class CreationModel {
	
	public CreationModel(Object returnedObject, ProceedingJoinPoint joinPoint, CreatedObjectLocation createdLocation,
			ParametersObjectsLocation parametersLocation) {
		super();
		this.returnedObject = returnedObject;
		this.joinPoint = joinPoint;
		this.createdLocation = createdLocation;
		this.parametersLocation = parametersLocation;
	}
	private Object returnedObject;
	private ProceedingJoinPoint joinPoint;
	private CreatedObjectLocation createdLocation;
	private ParametersObjectsLocation parametersLocation;
	public Object getReturnedObject() {
		return returnedObject;
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
}
