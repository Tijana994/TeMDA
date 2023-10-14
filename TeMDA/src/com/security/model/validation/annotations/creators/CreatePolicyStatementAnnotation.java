package com.security.model.validation.annotations.creators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;

import privacyModel.Action;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CreatePolicyStatementAnnotation {
	CreatedObjectLocation createdObjectLocation() default CreatedObjectLocation.Return;
	String name() default Constants.Empty;
	String who();
	String whose();
	String why();
	String when();
	Action[] actions();
	String[] datas();
	String howDocuments() default Constants.Empty;
	String howDocumentsIds() default Constants.Empty;
	String howConsent()  default Constants.Empty;
	String howConsentId()  default Constants.Empty;
}
