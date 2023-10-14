package com.security.model.validation.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.NotificationAnnotation;
import com.security.model.validation.annotations.creators.CreateNotificationAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.creators.FieldCreator;

@Aspect
public class CreateNotificationAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateNotificationAnnotation)")
	void function() {}
	@Around("function()")
	public Object aroundOkoF(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object ret = thisJoinPoint.proceed(args);
		Object obj = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreateNotificationAnnotation createNotification = method.getAnnotation(CreateNotificationAnnotation.class);
		if(createNotification == null)
		{
			System.out.println("There is no create notification statement annotation");
			return ret;
		}
		Object retFromObj = FieldCreator.getObjectToReadFrom(ret, obj, createNotification.createdObjectLocation(), createNotification.name(), thisJoinPoint);
		if(retFromObj == null)
		{
			System.out.println("Read from object is null = CreateNotificationAspect");
			return ret;
		}
		Class<? extends Object> retClass = retFromObj.getClass();
		NotificationAnnotation notification = retClass.getAnnotation(NotificationAnnotation.class);
		
		if(notification == null)
		{
			System.out.println("There is no notification annotation");
			return ret;
		}

		try 
		{
			System.out.println("NotificationId: " + FieldCreator.getFieldValue(notification.id(), retFromObj, retClass));
			System.out.println("Date: " + FieldCreator.getFieldValue(notification.when(), retFromObj, retClass));
			System.out.println("NotificationType: " + createNotification.type());

			if(createNotification.causedBy() != Constants.Empty)
			{
				createNotification.causedByType();
			}
			if(createNotification.causedById() != Constants.Empty)
			{
				createNotification.causedByType();
			}
			if(createNotification.receivers() != Constants.Empty)
			{
				
			}
			if(createNotification.receiversIds() != Constants.Empty)
			{
				
			}
			if(createNotification.notifiers() != Constants.Empty)
			{
				
			}
			if(createNotification.notifiersIds() != Constants.Empty)
			{
				
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return ret;
	}
}

