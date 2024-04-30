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
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectManager;

import utility.PrivacyModelRepository;

@Aspect
public class CreateDocumentAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateDocumentAnnotation)")
	void function() {}
	@Around("function()")
	public Object createDocument(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object returnedObject = thisJoinPoint.proceed(args);
		Object originalObject = thisJoinPoint.getThis();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
	    CreateDocumentAnnotation createDocument = method.getAnnotation(CreateDocumentAnnotation.class);
		if(createDocument == null)
		{
			System.out.println("There is no create document statement annotation");
			return returnedObject;
		}
		Object createdObject = FieldFinder.getObjectToReadFrom(returnedObject, originalObject, createDocument.createdObjectLocation(), createDocument.name(), thisJoinPoint);
		if(createdObject == null)
		{
			System.out.println("Read from object is null - CreateDocumentAspect");
			return returnedObject;
		}
		Class<? extends Object> createdObjectClass = createdObject.getClass();
		PaperAnnotation paper = createdObjectClass.getAnnotation(PaperAnnotation.class);
		
		if(paper == null)
		{
			System.out.println("There is no paper annotation");
			return returnedObject;
		}
		
		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var documentObject = repo.getFactory().createDocument();
			documentObject.setName((String)FieldFinder.getFieldValue(paper.id(), createdObject, createdObjectClass));
			documentObject.setDocumentType(createDocument.documentType());
			documentObject.setStartDate((Date)FieldFinder.getFieldValue(paper.startDate(), createdObject, createdObjectClass));
			documentObject.setLocation((String)FieldFinder.getFieldValue(paper.location(), createdObject, createdObjectClass));
			if(!paper.terminantionExplanation().equals(Constants.Empty))
			{
				documentObject.setTerminationExplanation((String)FieldFinder.getFieldValue(paper.terminantionExplanation(), createdObject, createdObjectClass));
			}
			if(!paper.terminantionDate().equals(Constants.Empty))
			{
				documentObject.setTerminationDate((Date)FieldFinder.getFieldValue(paper.terminantionDate(), createdObject, createdObjectClass));
			}
			if(!paper.description().equals(Constants.Empty))
			{
				documentObject.setDescription((String)FieldFinder.getFieldValue(paper.description(), createdObject, createdObjectClass));
			}
			if(!paper.providedBy().equals(Constants.Empty)) 
			{
				var principal = ObjectManager.tryGetPrincipalByFromObject(createdObject, createdObjectClass, paper.providedBy(), model, ParametersObjectsLocation.Property, thisJoinPoint);
				if(principal.isPresent())
				{
					documentObject.setProvidedBy(principal.get());
				}
			}
			if(!paper.providedById().equals(Constants.Empty))
			{
				var principal = ObjectManager.tryGetPrincipalByById(createdObject, createdObjectClass, paper.providedById(), model, ParametersObjectsLocation.Property, thisJoinPoint);
				if(principal.isPresent())
				{
					documentObject.setProvidedBy(principal.get());
				}
			}
			model.getAllDocuments().add(documentObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		return returnedObject;
	}
}
