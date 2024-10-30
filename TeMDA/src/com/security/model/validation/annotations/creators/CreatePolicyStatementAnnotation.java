package com.security.model.validation.annotations.creators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

import privacyModel.Action;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CreatePolicyStatementAnnotation {
	CreatedObjectLocation createdObjectLocation() default CreatedObjectLocation.Return;
	String name() default Constants.Empty;
	String systemActionId() default Constants.Empty;
	String why();
	String when();
	Action[] actions();
	String[] datas();
	ParametersObjectsLocation parametersLocation() default ParametersObjectsLocation.Parameter;
	String whereSource() default Constants.Empty;
	String whereDestination() default Constants.Empty;
	String whereSourceId() default Constants.Empty;
	String whereDestinationId() default Constants.Empty;
	String whoId() default Constants.Empty;
	String who() default Constants.Empty;
	String whoseId() default Constants.Empty;
	String whose() default Constants.Empty;
	String whomId() default Constants.Empty;
	String whom() default Constants.Empty;
	String howDocuments() default Constants.Empty;
	String howDocumentsIds() default Constants.Empty;
	String howDocument() default Constants.Empty;
	String howDocumentId() default Constants.Empty;
	String howConsent()  default Constants.Empty;
	String howConsentId()  default Constants.Empty;
	String causedBy() default Constants.Empty;
	String causedById() default Constants.Empty;
}
