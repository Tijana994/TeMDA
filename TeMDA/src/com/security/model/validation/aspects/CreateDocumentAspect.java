package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Date;

import org.aspectj.lang.JoinPoint;
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
import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.ReadTypeByAttribute;

import privacyModel.Document;
import privacyModel.PrivacyPolicy;
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
			if(!paper.providedBy().equals(Constants.Undefined)) 
			{
				setProvidedByFromObject(createdObject, createdObjectClass, paper.providedBy(), model, documentObject, thisJoinPoint);
			}
			if(!paper.providedById().equals(Constants.Undefined))
			{
				var parentId = (String)FieldFinder.getFieldValue(paper.providedById(), createdObject, createdObjectClass);
				setProvidedByById(model, documentObject, parentId);
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
	
	private void setProvidedByFromObject(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, 
			Document documentObject, JoinPoint jp) {
		var principalId = ReadTypeByAttribute.getPrincipalIdFromObject(objectClass, obj, propertyName, ParametersObjectsLocation.Property, jp);
		if(principalId.isPresent())
		{
			var principalName = principalId.get();
			setProvidedByById(model, documentObject, principalName);
		}
	}
	
	private void setProvidedByById(PrivacyPolicy model, Document documentObject, String id) {
		var principal = ObjectFinder.checkIfPrincipalExists(id, model);
		if(principal.isPresent())
		{
			documentObject.setProvidedBy(principal.get());
		}
	}
}
