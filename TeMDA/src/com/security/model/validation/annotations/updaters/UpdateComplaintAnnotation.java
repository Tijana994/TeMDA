package com.security.model.validation.annotations.updaters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UpdateComplaintAnnotation {
	ParametersObjectsLocation parametersLocation() default ParametersObjectsLocation.Parameter;
	String propertyObjectName() default Constants.Empty;
	String complaintId() default Constants.Empty;
	String complaint() default Constants.Empty;
	String responseDeadline();
}
