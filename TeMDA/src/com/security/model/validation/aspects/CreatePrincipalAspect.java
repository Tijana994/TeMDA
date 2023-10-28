package com.security.model.validation.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.PrincipalAnnotation;
import com.security.model.validation.annotations.creators.CreatePrincipalAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;

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
		Object ret = thisJoinPoint.proceed(args);
		Object obj = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
		CreatePrincipalAnnotation createPrincipal = method.getAnnotation(CreatePrincipalAnnotation.class);
		if(createPrincipal == null)
		{
			System.out.println("There is no create principal statement annotation");
			return ret;
		}
		Object retFromObj = FieldFinder.getObjectToReadFrom(ret, obj, createPrincipal.createdObjectLocation(), createPrincipal.name(), thisJoinPoint);
		if(retFromObj == null)
		{
			System.out.println("Read from object is null = CreatePrincipalAspect");
			return ret;
		}
		Class<? extends Object> retClass = retFromObj.getClass();
		PrincipalAnnotation principal = retClass.getAnnotation(PrincipalAnnotation.class);
		if(principal == null)
		{
			System.out.println("There is no principal annotation");
			return ret;
		}

		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var principalObject = repo.getFactory().createPrincipal();
			var name = (String)FieldFinder.getFieldValue(principal.id(), retFromObj, retClass);
			principalObject.setName(name);
			principalObject.setScope(createPrincipal.scope());
			principalObject.setType(createPrincipal.type());
			if(!principal.birthday().equals(Constants.Unassigned))
			{
				
			}
			if(!principal.parent().equals(Constants.Undefined)) 
			{
				
			}
			if(!principal.parentId().equals(Constants.Undefined))
			{
				setParentById(retFromObj, retClass, principal, principalObject, model);
			}
			if(!principal.childrens().equals(Constants.Undefined))
			{
				
			}
			if(!principal.childrensIds().equals(Constants.Undefined))
			{
				
			}
			model.getAllPrincipals().add(principalObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return ret;
	}
	private void setParentById(Object retFromObj, Class<? extends Object> retClass, PrincipalAnnotation principal,
			Principal principalObject, PrivacyPolicy model) {
		var parentId = (String)FieldFinder.getFieldValue(principal.parentId(), retFromObj, retClass);
		var parent = ObjectFinder.checkIfPrincipalExists(parentId, model);
		if(parent.isPresent())
		{
			parent.get().getSubPrincipals().add(principalObject);
		}
	}
}
