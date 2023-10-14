package com.security.model.validation.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.creators.CreateDocumentAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.creators.FieldCreator;

@Aspect
public class CreateDocumentAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateDocumentAnnotation)")
	void function() {}
	@Around("function()")
	public Object aroundOkoF(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object ret = thisJoinPoint.proceed(args);
		Object obj = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreateDocumentAnnotation createDocument = method.getAnnotation(CreateDocumentAnnotation.class);
		if(createDocument == null)
		{
			System.out.println("There is no create document statement annotation");
			return ret;
		}
		Object retFromObj = FieldCreator.getObjectToReadFrom(ret, obj, createDocument.createdObjectLocation(), createDocument.name(), thisJoinPoint);
		if(retFromObj == null)
		{
			System.out.println("Read from object is null - CreateDocumentAspect");
			return ret;
		}
		Class<? extends Object> retClass = retFromObj.getClass();
		PaperAnnotation paper = retClass.getAnnotation(PaperAnnotation.class);
		
		if(paper == null)
		{
			System.out.println("There is no paper annotation");
			return ret;
		}
		
		try 
		{
			System.out.println("DocumentId: " + FieldCreator.getFieldValue(paper.id(), retFromObj, retClass));
			System.out.println("Type " + createDocument.documentType());
			System.out.println("StartDate: " + FieldCreator.getFieldValue(paper.startDate(), retFromObj, retClass));
			System.out.println("Location: " + FieldCreator.getFieldValue(paper.location(), retFromObj, retClass));
			if(paper.terminantionExplanation() != Constants.Empty)
			{
				
			}
			if(paper.terminantionDate() != Constants.Empty)
			{
				
			}
			if(paper.description() != Constants.Empty)
			{
				
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return ret;
	}
}
