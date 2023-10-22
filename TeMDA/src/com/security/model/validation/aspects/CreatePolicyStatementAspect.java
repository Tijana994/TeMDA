package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.PolicyStatementAnnotation;
import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.creators.FieldCreator;
import com.security.model.validation.creators.PurposeCreator;
import com.security.model.validation.creators.WhenCreator;

import privacyModel.Principal;
import utility.PrivacyModelRepository;

@Aspect
public class CreatePolicyStatementAspect {
	
	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation)")
	void function() {}
	@Around("function()")
	public Object createPolicyStatement(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object ret = thisJoinPoint.proceed(args);
		Object obj = thisJoinPoint.getThis();
		Class<? extends Object> objectClass = obj.getClass();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
		CreatePolicyStatementAnnotation createPolicyStatement = method.getAnnotation(CreatePolicyStatementAnnotation.class);
		if(createPolicyStatement == null)
		{
			System.out.println("There is no create policy statement annotation");
			return ret;
		}
		Object retFromObj = FieldCreator.getObjectToReadFrom(ret, obj, createPolicyStatement.createdObjectLocation(), createPolicyStatement.name(), thisJoinPoint);
		if(retFromObj == null)
		{
			System.out.println("Read from object is null = CreatePolicyStatementAspect");
			return ret;
		}
		Class<? extends Object> retClass = retFromObj.getClass();
		PolicyStatementAnnotation policyStatement = retClass.getAnnotation(PolicyStatementAnnotation.class);
		
		if(policyStatement == null)
		{
			System.out.println("There is no policy statement annotation");
			return ret;
		}
		
		try
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var policyStatementObject = repo.getFactory().createPolicyStatement();
			var name = (String)FieldCreator.getFieldValue(policyStatement.id(), ret, retClass);
			policyStatementObject.setName(name);
			var whoId = (String)FieldCreator.getFieldValue(createPolicyStatement.who(), obj, objectClass);
			var whoPrincipal = checkIfPrincipalExists(whoId,repo);
			if(whoPrincipal.isPresent())
			{
				policyStatementObject.setWho(whoPrincipal.get());
				System.out.println("There is principal with id " + whoId +" who");
			}
			var whoseId = (String)FieldCreator.getFieldValue(createPolicyStatement.whose(), obj, objectClass);
			var whosePrincipal = checkIfPrincipalExists(whoseId,repo);
			if(whosePrincipal.isPresent())
			{
				policyStatementObject.setWho(whosePrincipal.get());
				System.out.println("There is principal with id " + whoId +" whose");
			}
			var whomId = (String)FieldCreator.getFieldValue(createPolicyStatement.whom(), obj, objectClass);
			var whomPrincipal = checkIfPrincipalExists(whomId,repo);
			if(whomPrincipal.isPresent())
			{
				policyStatementObject.setWho(whomPrincipal.get());
				System.out.println("There is principal with id " + whoId +" whom");
			}
			
			PurposeCreator.CreatePurpose(objectClass, obj, createPolicyStatement.why()); //Ovako mogu procitati kompozicije
			WhenCreator.CreateWhen(objectClass, obj, createPolicyStatement.when());
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		return ret;
	}
	
	private Optional<Principal> findPrincipal(String principalId, PrivacyModelRepository repo)
	{
		return repo.getModel().getAllPrincipals().stream()
		   .filter(principal -> principal.getName().equals(principalId)).findFirst();
	}
	
	private Optional<Principal> checkIfPrincipalExists(String fieldId, PrivacyModelRepository repo) 
	{
		if(fieldId == null)
		{
			return Optional.empty();
		}
		var principal = findPrincipal(fieldId, repo);
		if(principal.isEmpty())
		{
			System.out.println("There is no prinipal with id " + fieldId);
		}
		return principal;
	}
}
