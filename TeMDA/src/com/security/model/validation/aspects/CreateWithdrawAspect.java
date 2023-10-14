package com.security.model.validation.aspects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.WithdrawAnnotation;
import com.security.model.validation.annotations.creators.CreateWithdrawAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.creators.FieldCreator;

@Aspect
public class CreateWithdrawAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateWithdrawAnnotation)")
	void function() {}
	@Around("function()")
	public Object aroundOkoF(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object ret = thisJoinPoint.proceed(args);
		Object obj = thisJoinPoint.getThis();
		Class<? extends Object> objectClass = obj.getClass();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
		CreateWithdrawAnnotation createWithdraw = method.getAnnotation(CreateWithdrawAnnotation.class);
		if(createWithdraw == null)
		{
			System.out.println("There is no create withdraw statement annotation");
			return ret;
		}
		Object retFromObj = FieldCreator.getObjectToReadFrom(ret, obj, createWithdraw.createdObjectLocation(), createWithdraw.name(), thisJoinPoint);
		if(retFromObj == null)
		{
			System.out.println("Read from object is null = CreateWithdrawAspect");
			return ret;
		}
		Class<? extends Object> retClass = retFromObj.getClass();
		WithdrawAnnotation withdraw = retClass.getAnnotation(WithdrawAnnotation.class);
		
		if(withdraw == null)
		{
			System.out.println("There is no withdraw annotation");
			return ret;
		}
		
		try 
		{
			System.out.println("WithdrawId: " + FieldCreator.getFieldValue(withdraw.id(), retFromObj, retClass));
			System.out.println("Date: " + FieldCreator.getFieldValue(withdraw.when(), retFromObj, retClass));
			
			if(withdraw.reason() != Constants.Empty)
			{
				System.out.println("Reason: " + FieldCreator.getFieldValue(withdraw.reason(), retFromObj, retClass));
			}
			if(createWithdraw.consent() != Constants.Empty)
			{
				readWithdrawAsObject(objectClass, obj, createWithdraw.consent(), createWithdraw.parametersLocation());
			}
			if(createWithdraw.consentId() != Constants.Empty)
			{
				
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return ret;
	}
	
	private void readWithdrawAsObject(Class<?> objectClass, Object obj, String consentName, ParametersObjectsLocation parametersLocation)
	{
		if(parametersLocation == ParametersObjectsLocation.Property)
		{
			if(obj == null)
			{
				System.out.println("Object is not instantiated.");
				return;
			}
			try
			{
				Field consent = objectClass.getDeclaredField(consentName);
				consent.setAccessible(true);
				Object value = consent.get(obj);
				PaperAnnotation paper = consent.getType().getAnnotation(PaperAnnotation.class);
				if(paper == null)
				{
					System.out.println("There is no paper annotation");
				}
				else
				{
					System.out.println("DocumentId: " + FieldCreator.getFieldValue(paper.id(), value, consent.getType()));
				}
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
		else if(parametersLocation == ParametersObjectsLocation.Parameter)
		{
			
		}
	}
}