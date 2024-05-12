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
import com.security.model.validation.models.CreationModel;

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
		CreationModel createdObjectModel = new CreationModel(returnedObject, thisJoinPoint, createNotification.createdObjectLocation(), ParametersObjectsLocation.Property);
		Object createdObject = FieldFinder.getObjectToReadFrom(createdObjectModel, originalObject, createNotification.name());
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
			if(!notification.causedBy().equals(Constants.Empty))
			{
				if(createNotification.causedByType() == TargetType.PolicyStatement)
				{
					var policyStatement = ObjectManager.tryGetPolicyStatementFromObject(createdObjectModel, createdObject, createdObjectClass, 
							notification.causedBy(), model);
					if(policyStatement.isPresent())
					{
						notificationObject.setCausedBy(policyStatement.get());
					}
				}
				else if(createNotification.causedByType() == TargetType.ComplaintBasedOnData 
						|| createNotification.causedByType() == TargetType.ComplaintBasedOnAction
						|| createNotification.causedByType() == TargetType.Withdraw)
				{
					var complaint = ObjectManager.tryGetComplaintFromObject(createdObjectModel, createdObject, createdObjectClass, notification.causedBy(), model);
					if(complaint.isPresent())
					{
						notificationObject.setCausedBy(complaint.get());
					}
				}
			}
			if(!notification.causedById().equals(Constants.Empty))
			{
				if(createNotification.causedByType() == TargetType.PolicyStatement)
				{
					var policyStatement = ObjectManager.tryGetPolicyStatementById(createdObjectModel, createdObject, createdObjectClass, 
							notification.causedById(), model);
					if(policyStatement.isPresent())
					{
						notificationObject.setCausedBy(policyStatement.get());
					}
				}
				else if(createNotification.causedByType() == TargetType.ComplaintBasedOnData 
						|| createNotification.causedByType() == TargetType.ComplaintBasedOnAction
						|| createNotification.causedByType() == TargetType.Withdraw)
				{
					var complaint = ObjectManager.tryGetComplaintById(createdObjectModel, createdObject, createdObjectClass, notification.causedById(), model);
					if(complaint.isPresent())
					{
						notificationObject.setCausedBy(complaint.get());
					}
				}
			}
			if(!notification.receivers().equals(Constants.Empty))
			{
				var reveivers = ReadTypeByAttribute.getPrincipalsFromObject(createdObjectModel, createdObjectClass, createdObject, notification.receivers(), model);
				if(!reveivers.isEmpty())
				{
					notificationObject.getReceivers().addAll(reveivers);
				}
			}
			if(!notification.receiversIds().equals(Constants.Empty))
			{
				var reveivers = ReadTypeByAttribute.getPrincipalsById(createdObjectModel, createdObjectClass, createdObject, notification.receiversIds(), model);
				if(!reveivers.isEmpty())
				{
					notificationObject.getReceivers().addAll(reveivers);
				}
			}
			if(!notification.receiver().equals(Constants.Empty))
			{
				var reveiver = ObjectManager.tryGetPrincipalByFromObject(createdObjectModel, createdObject, createdObjectClass, notification.receiver(), model);
				if(!reveiver.isEmpty())
				{
					notificationObject.getReceivers().add(reveiver.get());
				}
			}
			if(!notification.receiverId().equals(Constants.Empty))
			{
				var reveiver = ObjectManager.tryGetPrincipalByById(createdObjectModel, createdObject, createdObjectClass, notification.receiverId(), model);
				if(!reveiver.isEmpty())
				{
					notificationObject.getReceivers().add(reveiver.get());
				}
			}
			if(!notification.notifiers().equals(Constants.Empty))
			{
				var notifiers = ReadTypeByAttribute.getPrincipalsFromObject(createdObjectModel, createdObjectClass, createdObject, notification.notifiers(), model);
				if(!notifiers.isEmpty())
				{
					notificationObject.getNotifiers().addAll(notifiers);
				}
			}
			if(!notification.notifiersIds().equals(Constants.Empty))
			{
				var notifiers = ReadTypeByAttribute.getPrincipalsById(createdObjectModel, createdObjectClass, createdObject, notification.notifiersIds(), model);
				if(!notifiers.isEmpty())
				{
					notificationObject.getNotifiers().addAll(notifiers);
				}
			}
			if(!notification.notifier().equals(Constants.Empty))
			{
				var notifier = ObjectManager.tryGetPrincipalByFromObject(createdObjectModel, createdObject, createdObjectClass, notification.notifier(), model);
				if(!notifier.isEmpty())
				{
					notificationObject.getNotifiers().add(notifier.get());
				}
			}
			if(!notification.notifierId().equals(Constants.Empty))
			{
				var notifier = ObjectManager.tryGetPrincipalByById(createdObjectModel, createdObject, createdObjectClass, notification.notifierId(), model);
				if(!notifier.isEmpty())
				{
					notificationObject.getNotifiers().add(notifier.get());
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

