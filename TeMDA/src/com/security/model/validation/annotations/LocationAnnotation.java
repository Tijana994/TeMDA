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
	String parent() default Constants.Undefined;
	String parentId() default Constants.Undefined;
	String childrens() default Constants.Undefined;
	String childrensIds() default Constants.Undefined;
}
