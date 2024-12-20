package com.security.model.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.security.model.validation.annotations.enums.Constants;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PrincipalAnnotation {
	String id();
	String birthday() default Constants.Empty;
	String parent() default Constants.Empty;
	String parentId() default Constants.Empty;
	String children() default Constants.Empty;
	String childrenIds() default Constants.Empty;
	String inhabits() default Constants.Empty;
	String inhabitsId() default Constants.Empty;
	String responsiblePersons() default Constants.Empty;
	String responsiblePersonIds() default Constants.Empty;
}
