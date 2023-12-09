package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Date;

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
			principalObject.setName((String)FieldFinder.getFieldValue(principal.id(), retFromObj, retClass));
			principalObject.setScope(createPrincipal.scope());
			principalObject.setType(createPrincipal.type());
			if(!principal.birthday().equals(Constants.Undefined))
			{
				principalObject.setBirthdate((Date)FieldFinder.getFieldValue(principal.birthday(), retFromObj, retClass));
			}
			if(!principal.parent().equals(Constants.Undefined)) 
			{
				
			}
			if(!principal.parentId().equals(Constants.Undefined))
			{
				setParentById(retFromObj, retClass, principal.parentId(), principalObject, model);
			}
			if(!principal.childrens().equals(Constants.Undefined))
			{
				
			}
			if(!principal.childrensIds().equals(Constants.Undefined))
			{
				var childrens = ReadTypeByAttribute.getPrincipalsById(retFromObj, retClass, principal.childrensIds(), model);
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
		
		return ret;
	}
	private void setParentById(Object retFromObj, Class<? extends Object> retClass, String propertyName,
			Principal principalObject, PrivacyPolicy model) {
		var parentId = (String)FieldFinder.getFieldValue(propertyName, retFromObj, retClass);
		var parent = ObjectFinder.checkIfPrincipalExists(parentId, model);
		if(parent.isPresent())
		{
			parent.get().getSubPrincipals().add(principalObject);
		}
	}
}
