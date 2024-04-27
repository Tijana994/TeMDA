package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.NotificationAnnotation;
import com.security.model.validation.annotations.creators.CreateNotificationAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ReadTypeByAttribute;

import utility.PrivacyModelRepository;

@Aspect
public class CreateNotificationAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateNotificationAnnotation)")
	void function() {}
	@Around("function()")
	public Object createNotification(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object returnedObject = thisJoinPoint.proceed(args);
		Object originalObject = thisJoinPoint.getThis();
		Class<? extends Object> originalObjectClass = originalObject.getClass();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreateNotificationAnnotation createNotification = method.getAnnotation(CreateNotificationAnnotation.class);
		if(createNotification == null)
		{
			System.out.println("There is no create notification statement annotation");
			return returnedObject;
		}
		Object createdObject = FieldFinder.getObjectToReadFrom(returnedObject, originalObject, createNotification.createdObjectLocation(), createNotification.name(), thisJoinPoint);
		if(createdObject == null)
		{
			System.out.println("Read from object is null = CreateNotificationAspect");
			return returnedObject;
		}
		Class<? extends Object> createdObjectClass = createdObject.getClass();
		NotificationAnnotation notification = createdObjectClass.getAnnotation(NotificationAnnotation.class);
		
		if(notification == null)
		{
			System.out.println("There is no notification annotation");
			return returnedObject;
		}

		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var notificationObject = repo.getFactory().createNotification();
			notificationObject.setName((String)FieldFinder.getFieldValue(notification.id(), createdObject, createdObjectClass));
			notificationObject.setWhen((Date)FieldFinder.getFieldValue(notification.when(), createdObject, createdObjectClass));
			notificationObject.setType(createNotification.type());
			if(!createNotification.causedBy().equals(Constants.Empty))
			{
				createNotification.causedByType();
			}
			if(!createNotification.causedById().equals(Constants.Empty))
			{
				createNotification.causedByType();
			}
			if(!createNotification.receivers().equals(Constants.Empty))
			{
				var reveivers = ReadTypeByAttribute.getPrincipalsFromObject(createdObjectClass, createdObject, createNotification.receivers(), 
						createNotification.parametersLocation(), thisJoinPoint, model);
				if(!reveivers.isEmpty())
				{
					notificationObject.getReceivers().addAll(reveivers);
				}
			}
			if(!createNotification.receiversIds().equals(Constants.Empty))
			{
				var reveivers = ReadTypeByAttribute.getPrincipalsById(createdObjectClass, createdObject, createNotification.receiversIds(), 
						createNotification.parametersLocation(), thisJoinPoint, model);
				if(!reveivers.isEmpty())
				{
					notificationObject.getReceivers().addAll(reveivers);
				}
			}
			if(!createNotification.notifiers().equals(Constants.Empty))
			{
				var notifiers = ReadTypeByAttribute.getPrincipalsFromObject(createdObjectClass, createdObject, createNotification.notifiers(), 
						createNotification.parametersLocation(), thisJoinPoint, model);
				if(!notifiers.isEmpty())
				{
					notificationObject.getNotifiers().addAll(notifiers);
				}
			}
			if(!createNotification.notifiersIds().equals(Constants.Empty))
			{
				var notifiers = ReadTypeByAttribute.getPrincipalsById(createdObjectClass, createdObject, createNotification.notifiersIds(), 
						createNotification.parametersLocation(), thisJoinPoint, model);
				if(!notifiers.isEmpty())
				{
					notificationObject.getNotifiers().addAll(notifiers);
				}
			}
			model.getNotifications().add(notificationObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return returnedObject;
	}
}

