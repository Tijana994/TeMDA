package com.security.model.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.security.model.validation.annotations.enums.Constants;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PaperAnnotation{
	String id();
	String terminantionExplanation() default Constants.Empty;
	String startDate();
	String terminantionDate() default Constants.Empty;
	String location();
	String description() default Constants.Empty;
}
