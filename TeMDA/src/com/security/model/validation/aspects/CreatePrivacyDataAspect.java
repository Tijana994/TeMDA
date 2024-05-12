package com.security.model.validation.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.PrivacyDataAnnotation;
import com.security.model.validation.annotations.creators.CreatePrivacyDataAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class CreatePrivacyDataAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreatePrivacyDataAnnotation)")
	void function() {}
	@Around("function()")
	public Object createPrincipal(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object returnedObject = thisJoinPoint.proceed(args);
		Object originalObject = thisJoinPoint.getThis();
		Class<? extends Object> originalObjectClass = originalObject.getClass();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreatePrivacyDataAnnotation createPrivacyData = method.getAnnotation(CreatePrivacyDataAnnotation.class);
		if(createPrivacyData == null)
		{
			System.out.println("There is no create privacy data annotation");
			return returnedObject;
		}
		CreationModel originalObjectModel = new CreationModel(returnedObject, originalObject, thisJoinPoint, createPrivacyData.createdObjectLocation(), createPrivacyData.parametersLocation());
		Object createdObject = FieldFinder.getCreatedObjectToReadFrom(originalObjectModel, originalObject, createPrivacyData.name());
		if(createdObject == null)
		{
			System.out.println("Read from object is null = CreatePrivacyDataAspect");
			return returnedObject;
		}
		Class<? extends Object> createdObjectClass = createdObject.getClass();
		PrivacyDataAnnotation privacyData = createdObjectClass.getAnnotation(PrivacyDataAnnotation.class);
		if(privacyData == null)
		{
			System.out.println("There is no principal annotation");
			return returnedObject;
		}

		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var privacyDataObject = repo.getFactory().createPrivacyData();
			privacyDataObject.setName((String)FieldFinder.getFieldValue(privacyData.id(), createdObject, createdObjectClass));
			privacyDataObject.setType(createPrivacyData.type());
			model.getAllDatas().add(privacyDataObject);
			if(createPrivacyData.createSharedPrivacyData())
			{
				var sharedPrivacyDataObject = repo.getFactory().createSharedPrivacyData();
				sharedPrivacyDataObject.setName(privacyDataObject.getName());
				sharedPrivacyDataObject.setCollectedFromSubject(createPrivacyData.collectedFromSubject());
				if(!createPrivacyData.privacyDataSource().equals(Constants.Empty))
				{
					var privacyDataSource = FieldFinder.getObjectToReadFrom(originalObjectModel, originalObjectClass, originalObject, createPrivacyData.privacyDataSource());
					if(privacyDataSource.isPresent())
					{
						sharedPrivacyDataObject.setDataSource((String)privacyDataSource.get());
					}
				}

				sharedPrivacyDataObject.setPrivacydata(privacyDataObject);
				model.getAllSharedPrivacyData().add(sharedPrivacyDataObject);
			}
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return returnedObject;
	}
}
