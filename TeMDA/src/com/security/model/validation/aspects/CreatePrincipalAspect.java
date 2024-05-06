package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.PrincipalAnnotation;
import com.security.model.validation.annotations.creators.CreatePrincipalAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.helpers.ReadTypeByAttribute;

import privacyModel.Principal;
import utility.PrivacyModelRepository;

@Aspect
public class CreatePrincipalAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreatePrincipalAnnotation)")
	void function() {}
	@Around("function()")
	public Object createPrincipal(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object returnedObject = thisJoinPoint.proceed(args);
		Object originalObject = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
		CreatePrincipalAnnotation createPrincipal = method.getAnnotation(CreatePrincipalAnnotation.class);
		if(createPrincipal == null)
		{
			System.out.println("There is no create principal statement annotation");
			return returnedObject;
		}
		Object createdObject = FieldFinder.getObjectToReadFrom(returnedObject, originalObject, createPrincipal.createdObjectLocation(), createPrincipal.name(), thisJoinPoint);
		if(createdObject == null)
		{
			System.out.println("Read from object is null = CreatePrincipalAspect");
			return returnedObject;
		}
		Class<? extends Object> createdObjectClass = createdObject.getClass();
		PrincipalAnnotation principal = createdObjectClass.getAnnotation(PrincipalAnnotation.class);
		if(principal == null)
		{
			System.out.println("There is no principal annotation");
			return returnedObject;
		}

		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var principalObject = repo.getFactory().createPrincipal();
			principalObject.setName((String)FieldFinder.getFieldValue(principal.id(), createdObject, createdObjectClass));
			principalObject.setScope(createPrincipal.scope());
			principalObject.setType(createPrincipal.type());
			if(!principal.birthday().equals(Constants.Empty) && createPrincipal.shouldSetBirtday())
			{
				var birthday = (Date)FieldFinder.getFieldValue(principal.birthday(), createdObject, createdObjectClass);
				principalObject.setBirthdate(birthday);
		        calculateAge(principalObject, birthday);
			}
			if(!createPrincipal.shouldSetBirtday() && createPrincipal.isLegalAge())
			{
				principalObject.setAge(model.getOwner().getInhabits().getLegalAgeLimit());;
			}
			if(!principal.parent().equals(Constants.Empty)) 
			{
				var parent = ObjectManager.tryGetPrincipalByFromObject(createdObject, createdObjectClass, principal.parent(), model, 
						ParametersObjectsLocation.Property, thisJoinPoint);
				if(parent.isPresent())
				{
					parent.get().getSubPrincipals().add(principalObject);
				}
			}
			if(!principal.parentId().equals(Constants.Empty))
			{
				var parent = ObjectManager.tryGetPrincipalByById(createdObject, createdObjectClass, principal.parentId(), model, 
						ParametersObjectsLocation.Property, thisJoinPoint);
				if(parent.isPresent())
				{
					parent.get().getSubPrincipals().add(principalObject);
				}
			}
			if(!principal.responsiblePersons().equals(Constants.Empty)) 
			{
				var parents = ReadTypeByAttribute.getPrincipalsFromObject(createdObjectClass, createdObject, principal.responsiblePersons(), 
						ParametersObjectsLocation.Property, thisJoinPoint, model);
				if(!parents.isEmpty())
				{
					principalObject.getResponsiblePersons().addAll(parents);
				}
			}
			if(!principal.responsiblePersonIds().equals(Constants.Empty))
			{
				var parents = ReadTypeByAttribute.getPrincipalsById(createdObjectClass, createdObject, principal.responsiblePersonIds(), 
						ParametersObjectsLocation.Property, thisJoinPoint, model);
				if(!parents.isEmpty())
				{
					principalObject.getResponsiblePersons().addAll(parents);
				}
			}
			if(!principal.childrens().equals(Constants.Empty))
			{
				var childrens = ReadTypeByAttribute.getPrincipalsFromObject(createdObjectClass, createdObject, principal.childrens(), 
						ParametersObjectsLocation.Property, thisJoinPoint, model);
				if(!childrens.isEmpty())
				{
					principalObject.getSubPrincipals().addAll(childrens);
				}
			}
			if(!principal.childrensIds().equals(Constants.Empty))
			{
				var childrens = ReadTypeByAttribute.getPrincipalsById(createdObjectClass, createdObject, principal.childrensIds(), 
						ParametersObjectsLocation.Property, thisJoinPoint, model);
				if(!childrens.isEmpty())
				{
					principalObject.getSubPrincipals().addAll(childrens);
				}
			}
			if(!principal.inhabits().equals(Constants.Empty) && createPrincipal.shouldSetLocation()) 
			{
				var location = ObjectManager.tryGetLocationFromObject(createdObject, createdObjectClass, principal.inhabits(), model, ParametersObjectsLocation.Property, thisJoinPoint);
				if(location.isPresent())
				{
					principalObject.setInhabits(location.get());
				}
			}
			if(!principal.inhabitsId().equals(Constants.Empty) && createPrincipal.shouldSetLocation())
			{
				var location = ObjectManager.tryGetLocationById(createdObject, createdObjectClass, principal.inhabitsId(), model, ParametersObjectsLocation.Property, thisJoinPoint);
				if(location.isPresent())
				{
					principalObject.setInhabits(location.get());
				}
			}
			
			model.getAllPrincipals().add(principalObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return returnedObject;
	}
	private void calculateAge(Principal principalObject, Date birthday) {
		LocalDate date = birthday.toInstant()
			      .atZone(ZoneId.systemDefault())
			      .toLocalDate();
		var age = LocalDate.now().minusYears(date.getYear());
		principalObject.setAge(age.getYear());
	}
}
