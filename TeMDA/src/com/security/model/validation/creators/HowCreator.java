package com.security.model.validation.creators;

import org.aspectj.lang.JoinPoint;

import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.helpers.ReadTypeByAttribute;

import privacyModel.How;
import privacyModel.PrivacyPolicy;
import utility.PrivacyModelRepository;

public class HowCreator {
	
	public static Boolean shouldCreate(CreatePolicyStatementAnnotation createPolicyStatement)
	{
		return !createPolicyStatement.howDocuments().equals(Constants.Empty) ||
				!createPolicyStatement.howDocumentsIds().equals(Constants.Empty) ||
				!createPolicyStatement.howConsent().equals(Constants.Empty) ||
				!createPolicyStatement.howConsentId().equals(Constants.Empty) ||
				!createPolicyStatement.howDocument().equals(Constants.Empty) ||
				!createPolicyStatement.howDocumentId().equals(Constants.Empty);
	}

	public static How createHow(CreatePolicyStatementAnnotation createPolicyStatement, Object originalObject,
			Class<? extends Object> originalObjectClass, PrivacyModelRepository repo, PrivacyPolicy model, 
			JoinPoint jp) {
		var how = repo.getFactory().createHow();
		
		if(!createPolicyStatement.howDocuments().equals(Constants.Empty))
		{
			var documents = ReadTypeByAttribute.getDocumentsFromObject(originalObjectClass, originalObject, createPolicyStatement.howDocuments(), 
					createPolicyStatement.parametersLocation(), jp, model);
			if(!documents.isEmpty())
			{
				how.getDocuments().addAll(documents);
			}
		}
		if(!createPolicyStatement.howDocumentsIds().equals(Constants.Empty))
		{
			var documents = ReadTypeByAttribute.getDocumentsById(originalObjectClass, originalObject, createPolicyStatement.howDocumentsIds(), 
					createPolicyStatement.parametersLocation(), jp, model);
			if(!documents.isEmpty())
			{
				how.getDocuments().addAll(documents);
			}
		}
		if(!createPolicyStatement.howDocument().equals(Constants.Empty))
		{
			var document = ObjectManager.tryGetDocumentFromObject(originalObject, originalObjectClass, createPolicyStatement.howDocument(), 
					model, createPolicyStatement.parametersLocation(), jp);
			if(!document.isEmpty())
			{
				how.getDocuments().add(document.get());
			}
		}
		if(!createPolicyStatement.howDocumentId().equals(Constants.Empty))
		{
			var document = ObjectManager.tryGetDocumentById(originalObject, originalObjectClass, createPolicyStatement.howDocumentId(), 
					model, createPolicyStatement.parametersLocation(), jp);
			if(!document.isEmpty())
			{
				how.getDocuments().add(document.get());
			}
		}
		if(!createPolicyStatement.howConsent().equals(Constants.Empty))
		{
			var consent = ObjectManager.tryGetConsentFromObject(originalObject, originalObjectClass, createPolicyStatement.howConsent(), 
					model, createPolicyStatement.parametersLocation(), jp);
			if(consent.isPresent())
			{
				how.setConsent(consent.get());
			}
		}
		if(!createPolicyStatement.howConsentId().equals(Constants.Empty))
		{
			var consent = ObjectManager.tryGetConsentById(originalObject, originalObjectClass, createPolicyStatement.howConsentId(), 
					model, createPolicyStatement.parametersLocation(), jp);
			if(consent.isPresent())
			{
				how.setConsent(consent.get());
			}
		}
		return how;
	}
}
