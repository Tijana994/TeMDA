package com.security.model.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.security.model.validation.annotations.enums.Constants;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LocationAnnotation {
	String id();
	String parent() default Constants.Empty;
	String parentId() default Constants.Empty;
	String subLocations() default Constants.Empty;
	String subLocationsIds() default Constants.Empty;
}
