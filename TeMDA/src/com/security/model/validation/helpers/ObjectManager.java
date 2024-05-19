package com.security.model.validation.helpers;

import java.util.Optional;

import com.security.model.validation.models.CreationModel;

import privacyModel.Complaint;
import privacyModel.Consent;
import privacyModel.Document;
import privacyModel.Location;
import privacyModel.PolicyStatement;
import privacyModel.Principal;
import privacyModel.PrivacyData;
import privacyModel.PrivacyPolicy;

public class ObjectManager {
	
	private FieldFinder fieldFinder;
	private ReadTypeByAttribute readTypeAttribute;
	private ObjectFinder objectFinder;
	
	public ObjectManager(FieldFinder fieldFinder, ReadTypeByAttribute readTypeAttribute, ObjectFinder objectFinder)
	{
		this.fieldFinder = fieldFinder;
		this.readTypeAttribute = readTypeAttribute;
		this.objectFinder = objectFinder;
	}
	
	public Optional<PolicyStatement> tryGetPolicyStatementFromObject(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var policyStatementId = readTypeAttribute.getPolicyStatementIdFromObject(creationModel, propertyName);
		if(policyStatementId.isPresent())
		{
			return objectFinder.checkIfPolicyStatementExists(policyStatementId.get(), model);
		}
		return Optional.empty();
	}
	
	public Optional<PolicyStatement> tryGetPolicyStatementById(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var policyStatementId = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
		if(policyStatementId.isPresent())
		{
			return objectFinder.checkIfPolicyStatementExists((String)policyStatementId.get(), model);
		}
		return Optional.empty();
	}

	public Optional<Principal> tryGetPrincipalByFromObject(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var principalId = readTypeAttribute.getPrincipalIdFromObject(creationModel, propertyName);
		if(principalId.isPresent())
		{
			return objectFinder.checkIfPrincipalExists(principalId.get(), model);
		}
		return Optional.empty();
	}
	
	public Optional<Principal> tryGetPrincipalByById(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var principalId = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
		if(principalId.isPresent())
		{
			return objectFinder.checkIfPrincipalExists((String)principalId.get(), model);
		}
		return Optional.empty();
	}
	
	public Optional<PrivacyData> tryGetPrivacyDataByFromObject(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var privacyDataId = readTypeAttribute.getPrivacyDataIdFromObject(creationModel, propertyName);
		if(privacyDataId.isPresent())
		{
			return objectFinder.checkIfPrivacyDataExists(privacyDataId.get(), model);
		}
		return Optional.empty();
	}
	
	public Optional<PrivacyData> tryGetPrivacyDataByById(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var privacyDataId = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
		if(privacyDataId.isPresent())
		{
			return objectFinder.checkIfPrivacyDataExists((String)privacyDataId.get(), model);
		}
		return Optional.empty();
	}
	
	public Optional<Complaint> tryGetComplaintFromObject(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var complaintId = readTypeAttribute.getComplaintIdFromObject(creationModel, propertyName);
		if(complaintId.isPresent())
		{
			return objectFinder.checkIfComplaintExists(complaintId.get(), model);
		}
		return Optional.empty();
	}
	
	public Optional<Complaint> tryGetComplaintById(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var complaintId = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
		if(complaintId.isPresent())
		{
			return objectFinder.checkIfComplaintExists((String)complaintId.get(), model);
		}
		return Optional.empty();
	}
	
	public Optional<Location> tryGetLocationFromObject(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var locationId = readTypeAttribute.getLocationIdFromObject(creationModel, propertyName);
		if(locationId.isPresent())
		{
			return objectFinder.checkIfLocationExists(locationId.get(), model);
		}
		return Optional.empty();
	}
	
	public Optional<Location> tryGetLocationById(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var locationId = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
		if(locationId.isPresent())
		{
			return objectFinder.checkIfLocationExists((String)locationId.get(), model);
		}
		return Optional.empty();
	}
	
	public Optional<Consent> tryGetConsentFromObject(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var consentId = readTypeAttribute.getPaperIdFromObject(creationModel, propertyName);
		if(consentId.isPresent())
		{
			return objectFinder.checkIfConsentExists(consentId.get(), model);
		}
		return Optional.empty();
	}
	
	public Optional<Consent> tryGetConsentById(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var consentId = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
		if(consentId.isPresent())
		{
			return objectFinder.checkIfConsentExists((String)consentId.get(), model);
		}
		return Optional.empty();
	}
	
	public Optional<Document> tryGetDocumentFromObject(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var documentId = readTypeAttribute.getPaperIdFromObject(creationModel, propertyName);
		if(documentId.isPresent())
		{ 
			return objectFinder.checkIfDocumentExists(documentId.get(), model);
		}
		return Optional.empty();
	}
	
	public Optional<Document> tryGetDocumentById(CreationModel creationModel, String propertyName, PrivacyPolicy model) {
		var documentId = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
		if(documentId.isPresent())
		{
			return objectFinder.checkIfDocumentExists((String)documentId.get(), model);
		}
		return Optional.empty();
	}
}
