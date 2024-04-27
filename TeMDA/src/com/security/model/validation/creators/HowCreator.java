package com.security.model.validation.creators;

import org.aspectj.lang.JoinPoint;

import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.ReadTypeByAttribute;

import privacyModel.How;
import privacyModel.PrivacyPolicy;
import utility.PrivacyModelRepository;

public class HowCreator {

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
		if(!createPolicyStatement.howConsent().equals(Constants.Empty))
		{
			setConsentFromObject(createPolicyStatement, originalObject, originalObjectClass, model, jp, how);
		}
		if(!createPolicyStatement.howConsentId().equals(Constants.Empty))
		{
			var consentId = FieldFinder.getObjectToReadFrom(originalObjectClass, originalObject, createPolicyStatement.parametersLocation(), createPolicyStatement.howConsentId(), jp);
			if(consentId.isPresent())
			{
				setConsentById(model, how, (String)consentId.get());
			}
			
		}
		return how;
	}
	private static void setConsentFromObject(CreatePolicyStatementAnnotation createPolicyStatement, Object originalObject,
			Class<? extends Object> originalObjectClass, PrivacyPolicy model, JoinPoint jp, How how) {
		var consentId = ReadTypeByAttribute.getConsentIdFromObject(originalObjectClass, originalObject, createPolicyStatement.howConsent(), createPolicyStatement.parametersLocation(), jp);
		if(consentId.isPresent())
		{
			var consentName = consentId.get();
			setConsentById(model, how, consentName);
		}
	}
	private static void setConsentById(PrivacyPolicy model, How how, String consentId) {
		var consent = ObjectFinder.checkIfConsentExists(consentId, model);
		if(consent.isPresent())
		{
			how.setConsent(consent.get());
		}
	}
}
