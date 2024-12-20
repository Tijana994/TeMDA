package com.security.model.validation.annotations.creators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CreateDenialAnnotation {
	CreatedObjectLocation createdObjectLocation() default CreatedObjectLocation.Return;
	String name() default Constants.Empty;
	ParametersObjectsLocation parametersLocation() default ParametersObjectsLocation.Parameter;
	String propertyObjectName() default Constants.Empty;
	String basedOnStatements() default Constants.Empty;
	String basedOnStatementsIds() default Constants.Empty;
	String basedOnStatement() default Constants.Empty;
	String basedOnStatementId() default Constants.Empty;
	String forComplaint() default Constants.Empty;
	String forComplaintId() default Constants.Empty;
	String approvedBy() default Constants.Empty;
	String approvedById() default Constants.Empty;
}
