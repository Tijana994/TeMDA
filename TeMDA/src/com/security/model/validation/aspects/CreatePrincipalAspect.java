package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Date;

import org.aspectj.lang.JoinPoint;
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
import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.ReadTypeByAttribute;

import privacyModel.Principal;
import privacyModel.PrivacyPolicy;
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
			if(!principal.birthday().equals(Constants.Undefined))
			{
				principalObject.setBirthdate((Date)FieldFinder.getFieldValue(principal.birthday(), createdObject, createdObjectClass));
			}
			if(!principal.parent().equals(Constants.Undefined)) 
			{
				setParentFromObject(createdObject, createdObjectClass, principal, model, principalObject, thisJoinPoint);
			}
			if(!principal.parentId().equals(Constants.Undefined))
			{
				var parentId = (String)FieldFinder.getFieldValue(principal.parentId(), createdObject, createdObjectClass);
				setParentById(model, principalObject, parentId);
			}
			if(!principal.childrens().equals(Constants.Undefined))
			{
				var childrens = ReadTypeByAttribute.getPrincipalsFromObject(createdObjectClass, createdObject, principal.childrens(), 
						ParametersObjectsLocation.Property, thisJoinPoint, model);
				if(!childrens.isEmpty())
				{
					principalObject.getSubPrincipals().addAll(childrens);
				}
			}
			if(!principal.childrensIds().equals(Constants.Undefined))
			{
				var childrens = ReadTypeByAttribute.getPrincipalsById(createdObject, createdObjectClass, principal.childrensIds(), model);
				if(!childrens.isEmpty())
				{
					principalObject.getSubPrincipals().addAll(childrens);
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
	
	private void setParentFromObject(Object obj, Class<? extends Object> objectClass,
			PrincipalAnnotation principal, PrivacyPolicy model, 
			Principal principalObject, JoinPoint jp) {
		var principalId = ReadTypeByAttribute.getPrincipalIdFromObject(objectClass, obj, principal.parent(), ParametersObjectsLocation.Property, jp);
		if(principalId.isPresent())
		{
			var principalName = principalId.get();
			setParentById(model, principalObject, principalName);
		}
	}
	
	private void setParentById(PrivacyPolicy model, Principal principalObject, String id) {
		var parent = ObjectFinder.checkIfPrincipalExists(id, model);
		if(parent.isPresent())
		{
			parent.get().getSubPrincipals().add(principalObject);
		}
	}
}
