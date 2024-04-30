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
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.annotations.enums.TargetType;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectManager;
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
			var parametersLocation = ParametersObjectsLocation.Property;
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var notificationObject = repo.getFactory().createNotification();
			notificationObject.setName((String)FieldFinder.getFieldValue(notification.id(), createdObject, createdObjectClass));
			notificationObject.setWhen((Date)FieldFinder.getFieldValue(notification.when(), createdObject, createdObjectClass));
			notificationObject.setType(createNotification.type());
			if(!notification.causedBy().equals(Constants.Empty))
			{
				if(createNotification.causedByType() == TargetType.PolicyStatement)
				{
					var policyStatement = ObjectManager.tryGetPolicyStatementFromObject(createdObject, createdObjectClass, 
							notification.causedBy(), model, parametersLocation, thisJoinPoint);
					if(policyStatement.isPresent())
					{
						notificationObject.setCausedBy(policyStatement.get());
					}
				}
				else
				{
					System.out.println("TODO");
				}
			}
			if(!notification.causedById().equals(Constants.Empty))
			{
				if(createNotification.causedByType() == TargetType.PolicyStatement)
				{
					var policyStatement = ObjectManager.tryGetPolicyStatementById(createdObject, createdObjectClass, 
							notification.causedById(), model, parametersLocation, thisJoinPoint);
					if(policyStatement.isPresent())
					{
						notificationObject.setCausedBy(policyStatement.get());
					}
				}
				else
				{
					System.out.println("TODO");
				}
			}
			if(!notification.receivers().equals(Constants.Empty))
			{
				var reveivers = ReadTypeByAttribute.getPrincipalsFromObject(createdObjectClass, createdObject, notification.receivers(), 
						parametersLocation, thisJoinPoint, model);
				if(!reveivers.isEmpty())
				{
					notificationObject.getReceivers().addAll(reveivers);
				}
			}
			if(!notification.receiversIds().equals(Constants.Empty))
			{
				var reveivers = ReadTypeByAttribute.getPrincipalsById(createdObjectClass, createdObject, notification.receiversIds(), 
						parametersLocation, thisJoinPoint, model);
				if(!reveivers.isEmpty())
				{
					notificationObject.getReceivers().addAll(reveivers);
				}
			}
			if(!notification.notifiers().equals(Constants.Empty))
			{
				var notifiers = ReadTypeByAttribute.getPrincipalsFromObject(createdObjectClass, createdObject, notification.notifiers(), 
						parametersLocation, thisJoinPoint, model);
				if(!notifiers.isEmpty())
				{
					notificationObject.getNotifiers().addAll(notifiers);
				}
			}
			if(!notification.notifiersIds().equals(Constants.Empty))
			{
				var notifiers = ReadTypeByAttribute.getPrincipalsById(createdObjectClass, createdObject, notification.notifiersIds(), 
						parametersLocation, thisJoinPoint, model);
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

