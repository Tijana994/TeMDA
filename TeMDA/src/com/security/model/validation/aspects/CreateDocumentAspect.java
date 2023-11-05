package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.creators.CreateDocumentAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;

import utility.PrivacyModelRepository;

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
		Object retFromObj = FieldFinder.getObjectToReadFrom(ret, obj, createDocument.createdObjectLocation(), createDocument.name(), thisJoinPoint);
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
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var documentObject = repo.getFactory().createDocument();
			documentObject.setName((String)FieldFinder.getFieldValue(paper.id(), retFromObj, retClass));
			documentObject.setDocumentType(createDocument.documentType());
			documentObject.setStartDate((Date)FieldFinder.getFieldValue(paper.startDate(), retFromObj, retClass));
			documentObject.setLocation((String)FieldFinder.getFieldValue(paper.location(), retFromObj, retClass));
			if(!paper.terminantionExplanation().equals(Constants.Empty))
			{
				documentObject.setTerminationExplanation((String)FieldFinder.getFieldValue(paper.terminantionExplanation(), retFromObj, retClass));
			}
			if(!paper.terminantionDate().equals(Constants.Empty))
			{
				documentObject.setTerminationDate((Date)FieldFinder.getFieldValue(paper.terminantionDate(), retFromObj, retClass));
			}
			if(!paper.description().equals(Constants.Empty))
			{
				documentObject.setDescription((String)FieldFinder.getFieldValue(paper.description(), retFromObj, retClass));
			}
			model.getAllDocuments().add(documentObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return ret;
	}
}
