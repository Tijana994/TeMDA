package com.security.model.validation.annotations.creators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.annotations.enums.TargetType;

import privacyModel.NotificationType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CreateNotificationAnnotation {
	CreatedObjectLocation createdObjectLocation() default CreatedObjectLocation.Return;
	String name() default Constants.Empty;
	NotificationType type();
	TargetType causedByType();
	ParametersObjectsLocation parametersLocation() default ParametersObjectsLocation.Parameter;
	String causedBy() default Constants.Undefined;
	String causedById() default Constants.Undefined;
	String receivers() default Constants.Undefined;
	String receiversIds() default Constants.Undefined;
	String notifiers() default Constants.Undefined;
	String notifiersIds() default Constants.Undefined;
}