package com.security.model.validation.aspects;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.creators.CreateContractAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.models.CreationModel;

import utility.PrivacyModelRepository;

@Aspect
public class CreateContractAspect extends BaseAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreateContractAnnotation)")
	void function() {}
	@Around("function()")
	public Object createContract(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		SetUp(thisJoinPoint);
	    CreateContractAnnotation createContract = method.getAnnotation(CreateContractAnnotation.class);
		if(createContract == null)
		{
			Logger.LogErrorMessage("There is no create contract annotation");
			return returnedObject;
		}
		CreationModel originalObjectModel = new CreationModel(returnedObject, originalObject, thisJoinPoint, createContract.createdObjectLocation(), 
				createContract.parametersLocation(), createContract.propertyObjectName());
		CreationModel createdObjectModel = new CreationModel(returnedObject, thisJoinPoint, createContract.createdObjectLocation(), ParametersObjectsLocation.Property);
		createdObjectModel.setObject(FieldFinder.getCreatedObjectToReadFrom(createdObjectModel, originalObject, createContract.name()));
		if(createdObjectModel.getObject() == null)
		{
			Logger.LogErrorMessage("Read from object is null - CreateContractAspect");
			return returnedObject;
		}
		PaperAnnotation paper = createdObjectModel.getObjectClass().getAnnotation(PaperAnnotation.class);
		if(paper == null)
		{
			Logger.LogErrorMessage("There is no paper annotation");
			return returnedObject;
		}

		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var contractObject = repo.getFactory().createContract();
			contractObject.setName((String)FieldFinder.getFieldValue(paper.id(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			contractObject.setStartDate((Date)FieldFinder.getFieldValue(paper.startDate(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			contractObject.setLocation((String)FieldFinder.getFieldValue(paper.location(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			if(!paper.terminationExplanation().equals(Constants.Empty))
			{
				contractObject.setTerminationExplanation((String)FieldFinder.getFieldValue(paper.terminationExplanation(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			}
			if(!paper.terminationDate().equals(Constants.Empty))
			{
				contractObject.setTerminationDate((Date)FieldFinder.getFieldValue(paper.terminationDate(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			}
			if(!paper.description().equals(Constants.Empty))
			{
				contractObject.setDescription((String)FieldFinder.getFieldValue(paper.description(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			}
			if(!paper.providedBy().equals(Constants.Empty)) 
			{
				var principal = ObjectManager.tryGetPrincipalByFromObject(createdObjectModel, paper.providedBy(), model);
				if(principal.isPresent())
				{
					contractObject.setProvidedBy(principal.get());
				}
			}
			if(!paper.providedById().equals(Constants.Empty))
			{
				var principal = ObjectManager.tryGetPrincipalByById(createdObjectModel, paper.providedById(), model);
				if(principal.isPresent())
				{
					contractObject.setProvidedBy(principal.get());
				}
			}
			if(!createContract.providedBy().equals(Constants.Empty)) 
			{
				var principal = ObjectManager.tryGetPrincipalByFromObject(originalObjectModel, createContract.providedBy(), model);
				if(principal.isPresent())
				{
					contractObject.setProvidedBy(principal.get());
				}
			}
			if(!createContract.providedById().equals(Constants.Empty))
			{
				var principal = ObjectManager.tryGetPrincipalByById(originalObjectModel, createContract.providedById(), model);
				if(principal.isPresent())
				{
					contractObject.setProvidedBy(principal.get());
				}
			}
			if(!createContract.provider().equals(Constants.Empty))
			{
				var provider = ObjectManager.tryGetProvider(createContract.provider(), model);
				if(provider.isPresent())
				{
					contractObject.setProvider(provider.get());
				}
			}
			if(!createContract.providerId().equals(Constants.Empty))
			{
				var provider = ObjectManager.tryGetProviderById(originalObjectModel, createContract.providerId(), model);
				if(provider.isPresent())
				{
					contractObject.setProvider(provider.get());
				}
			}
			if(!createContract.documents().equals(Constants.Empty))
			{
				contractObject.getDocuments().addAll(ReadTypeByAttribute.getDocumentsFromObject(originalObjectModel, createContract.documents(), model));
			}
			if(!createContract.documentsIds().equals(Constants.Empty))
			{
				contractObject.getDocuments().addAll(ReadTypeByAttribute.getDocumentsById(originalObjectModel, createContract.documentsIds(), model));
			}
			if(contractObject.getProvidedBy() == null && contractObject.getProvider() == null)
			{
				Logger.LogErrorMessage("Contract " + contractObject.getName() + " has neither a principal nor a provider as a party");
			}
			model.getAllContracts().add(contractObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			Logger.LogExceptionMessage(ex);
		}
		
		return returnedObject;
	}
}

