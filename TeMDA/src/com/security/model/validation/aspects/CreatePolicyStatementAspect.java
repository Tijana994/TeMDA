package com.security.model.validation.aspects;

import java.lang.reflect.Method;

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

@Aspect
public class CreatePolicyStatementAspect {
	
	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation)")
	void function() {}
	@Around("function()")
	public Object aroundOkoF(ProceedingJoinPoint thisJoinPoint) throws Throwable {
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
			System.out.println("PolicyStatementId: " + FieldCreator.getFieldValue(policyStatement.id(), ret, retClass));
			System.out.println("Who: " + FieldCreator.getFieldValue(createPolicyStatement.who(), obj, objectClass));
			System.out.println("Whose: " + FieldCreator.getFieldValue(createPolicyStatement.whose(), obj, objectClass));
			PurposeCreator.CreatePurpose(objectClass, obj, createPolicyStatement.why()); //Ovako mogu procitati kompozicije
			WhenCreator.CreateWhen(objectClass, obj, createPolicyStatement.when());
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		return ret;
	}
}
