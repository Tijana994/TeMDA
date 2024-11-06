package com.security.model.validation.annotations.creators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

import privacyModel.ConsentFormat;
import privacyModel.ConsentType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CreateConsentAnnotation {
	CreatedObjectLocation createdObjectLocation() default CreatedObjectLocation.Return;
	String name() default Constants.Empty;
	ConsentFormat consentFormat();
	ConsentType consentType();
	ParametersObjectsLocation parametersLocation() default ParametersObjectsLocation.Parameter;
	String propertyObjectName() default Constants.Empty;
	String providedById() default Constants.Empty;
	String providedBy() default Constants.Empty;
}
