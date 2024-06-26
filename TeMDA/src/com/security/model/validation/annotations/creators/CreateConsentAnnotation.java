package com.security.model.validation.annotations.creators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;

import privacyModel.ConsentFormat;
import privacyModel.ConsentType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CreateConsentAnnotation {
	CreatedObjectLocation createdObjectLocation() default CreatedObjectLocation.Return;
	String name() default Constants.Empty;
	ConsentFormat consentFormat();
	ConsentType consentType();
}
