package com.security.model.validation.aspects;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.creators.CreateDocumentAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class CreateDocumentAspect extends BaseAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateDocumentAnnotation)")
	void function() {}
	@Around("function()")
	public Object createDocument(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		SetUp(thisJoinPoint);
	    CreateDocumentAnnotation createDocument = method.getAnnotation(CreateDocumentAnnotation.class);
		if(createDocument == null)
		{
			System.out.println("There is no create document statement annotation");
			return returnedObject;
		}
		CreationModel createdObjectModel = new CreationModel(returnedObject, thisJoinPoint, createDocument.createdObjectLocation(), ParametersObjectsLocation.Property);
		createdObjectModel.setObject(FieldFinder.getCreatedObjectToReadFrom(createdObjectModel, originalObject, createDocument.name()));
		if(createdObjectModel.getObject() == null)
		{
			System.out.println("Read from object is null - CreateDocumentAspect");
			return returnedObject;
		}
		PaperAnnotation paper = createdObjectModel.getObjectClass().getAnnotation(PaperAnnotation.class);
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
			documentObject.setName((String)FieldFinder.getFieldValue(paper.id(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			documentObject.setDocumentType(createDocument.documentType());
			documentObject.setStartDate((Date)FieldFinder.getFieldValue(paper.startDate(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			documentObject.setLocation((String)FieldFinder.getFieldValue(paper.location(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			if(!paper.terminantionExplanation().equals(Constants.Empty))
			{
				documentObject.setTerminationExplanation((String)FieldFinder.getFieldValue(paper.terminantionExplanation(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			}
			if(!paper.terminantionDate().equals(Constants.Empty))
			{
				documentObject.setTerminationDate((Date)FieldFinder.getFieldValue(paper.terminantionDate(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			}
			if(!paper.description().equals(Constants.Empty))
			{
				documentObject.setDescription((String)FieldFinder.getFieldValue(paper.description(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			}
			if(!paper.providedBy().equals(Constants.Empty)) 
			{
				var principal = ObjectManager.tryGetPrincipalByFromObject(createdObjectModel, paper.providedBy(), model);
				if(principal.isPresent())
				{
					documentObject.setProvidedBy(principal.get());
				}
			}
			if(!paper.providedById().equals(Constants.Empty))
			{
				var principal = ObjectManager.tryGetPrincipalByById(createdObjectModel, paper.providedById(), model);
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
