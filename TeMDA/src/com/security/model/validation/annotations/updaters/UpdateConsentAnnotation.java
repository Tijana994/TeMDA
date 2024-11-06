package com.security.model.validation.annotations.updaters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UpdateConsentAnnotation {
	ParametersObjectsLocation parametersLocation() default ParametersObjectsLocation.Parameter;
	String propertyObjectName() default Constants.Empty;
	String consentId() default Constants.Empty;
	String consent() default Constants.Empty;
	String terminationDate();
}
