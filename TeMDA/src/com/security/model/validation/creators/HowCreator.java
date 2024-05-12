package com.security.model.validation.creators;

import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.helpers.ReadTypeByAttribute;
import com.security.model.validation.models.CreationModel;

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

	public static How createHow(CreationModel creationModel, CreatePolicyStatementAnnotation createPolicyStatement, PrivacyModelRepository repo, PrivacyPolicy model) {
		var how = repo.getFactory().createHow();
		
		if(!createPolicyStatement.howDocuments().equals(Constants.Empty))
		{
			var documents = ReadTypeByAttribute.getDocumentsFromObject(creationModel, createPolicyStatement.howDocuments(), model);
			if(!documents.isEmpty())
			{
				how.getDocuments().addAll(documents);
			}
		}
		if(!createPolicyStatement.howDocumentsIds().equals(Constants.Empty))
		{
			var documents = ReadTypeByAttribute.getDocumentsById(creationModel, createPolicyStatement.howDocumentsIds(), model);
			if(!documents.isEmpty())
			{
				how.getDocuments().addAll(documents);
			}
		}
		if(!createPolicyStatement.howDocument().equals(Constants.Empty))
		{
			var document = ObjectManager.tryGetDocumentFromObject(creationModel, createPolicyStatement.howDocument(), model);
			if(!document.isEmpty())
			{
				how.getDocuments().add(document.get());
			}
		}
		if(!createPolicyStatement.howDocumentId().equals(Constants.Empty))
		{
			var document = ObjectManager.tryGetDocumentById(creationModel, createPolicyStatement.howDocumentId(), model);
			if(!document.isEmpty())
			{
				how.getDocuments().add(document.get());
			}
		}
		if(!createPolicyStatement.howConsent().equals(Constants.Empty))
		{
			var consent = ObjectManager.tryGetConsentFromObject(creationModel, createPolicyStatement.howConsent(), model);
			if(consent.isPresent())
			{
				how.setConsent(consent.get());
			}
		}
		if(!createPolicyStatement.howConsentId().equals(Constants.Empty))
		{
			var consent = ObjectManager.tryGetConsentById(creationModel, createPolicyStatement.howConsentId(), model);
			if(consent.isPresent())
			{
				how.setConsent(consent.get());
			}
		}
		return how;
	}
}
