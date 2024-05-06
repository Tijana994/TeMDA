package com.security.model.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.security.model.validation.annotations.enums.Constants;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NotificationAnnotation {
	String id();
	String when();
	String causedBy() default Constants.Empty;
	String causedById() default Constants.Empty;
	String receivers() default Constants.Empty;
	String receiversIds() default Constants.Empty;
	String notifiers() default Constants.Empty;
	String notifiersIds() default Constants.Empty;
	String receiver() default Constants.Empty;
	String receiverId() default Constants.Empty;
	String notifier() default Constants.Empty;
	String notifierId() default Constants.Empty;
}
