package com.security.model.validation.annotations.creators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

import privacyModel.ComplaintBasedOnDataType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CreateComplaintBasedOnDataAnnotation {
	CreatedObjectLocation createdObjectLocation() default CreatedObjectLocation.Return;
	String name() default Constants.Empty;
	ComplaintBasedOnDataType type();
	ParametersObjectsLocation parametersLocation() default ParametersObjectsLocation.Parameter;
	String subjects() default Constants.Empty;
	String subjectsIds() default Constants.Empty;
}
