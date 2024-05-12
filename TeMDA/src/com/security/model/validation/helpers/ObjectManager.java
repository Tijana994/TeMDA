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
	
	public static Optional<PolicyStatement> tryGetPolicyStatementFromObject(CreationModel creationModel, Object obj, 
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var policyStatementId = ReadTypeByAttribute.getPolicyStatementIdFromObject(creationModel, objectClass, obj, propertyName);
		if(policyStatementId.isPresent())
		{
			return ObjectFinder.checkIfPolicyStatementExists(policyStatementId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<PolicyStatement> tryGetPolicyStatementById(CreationModel creationModel, Object obj, 
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var policyStatementId = FieldFinder.getObjectToReadFrom(creationModel, objectClass, obj, propertyName);
		if(policyStatementId.isPresent())
		{
			return ObjectFinder.checkIfPolicyStatementExists((String)policyStatementId.get(), model);
		}
		return Optional.empty();
	}

	public static Optional<Principal> tryGetPrincipalByFromObject(CreationModel creationModel, Object obj, 
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var principalId = ReadTypeByAttribute.getPrincipalIdFromObject(creationModel, objectClass, obj, propertyName);
		if(principalId.isPresent())
		{
			return ObjectFinder.checkIfPrincipalExists(principalId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Principal> tryGetPrincipalByById(CreationModel creationModel, Object obj, 
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var principalId = FieldFinder.getObjectToReadFrom(creationModel, objectClass, obj, propertyName);
		if(principalId.isPresent())
		{
			return ObjectFinder.checkIfPrincipalExists((String)principalId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<PrivacyData> tryGetPrivacyDataByFromObject(CreationModel creationModel, Object obj, 
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var privacyDataId = ReadTypeByAttribute.getPrivacyDataIdFromObject(creationModel, objectClass, obj, propertyName);
		if(privacyDataId.isPresent())
		{
			return ObjectFinder.checkIfPrivacyDataExists(privacyDataId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<PrivacyData> tryGetPrivacyDataByById(CreationModel creationModel, Object obj, 
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var privacyDataId = FieldFinder.getObjectToReadFrom(creationModel, objectClass, obj, propertyName);
		if(privacyDataId.isPresent())
		{
			return ObjectFinder.checkIfPrivacyDataExists((String)privacyDataId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Complaint> tryGetComplaintFromObject(CreationModel creationModel, Object obj, 
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var complaintId = ReadTypeByAttribute.getComplaintIdFromObject(creationModel, objectClass, obj, propertyName);
		if(complaintId.isPresent())
		{
			return ObjectFinder.checkIfComplaintExists(complaintId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Complaint> tryGetComplaintById(CreationModel creationModel, Object obj, 
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var complaintId = FieldFinder.getObjectToReadFrom(creationModel, objectClass, obj, propertyName);
		if(complaintId.isPresent())
		{
			return ObjectFinder.checkIfComplaintExists((String)complaintId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Location> tryGetLocationFromObject(CreationModel creationModel, Object obj, 
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var locationId = ReadTypeByAttribute.getLocationIdFromObject(creationModel, objectClass, obj, propertyName);
		if(locationId.isPresent())
		{
			return ObjectFinder.checkIfLocationExists(locationId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Location> tryGetLocationById(CreationModel creationModel, Object obj,
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var locationId = FieldFinder.getObjectToReadFrom(creationModel, objectClass, obj, propertyName);
		if(locationId.isPresent())
		{
			return ObjectFinder.checkIfLocationExists((String)locationId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Consent> tryGetConsentFromObject(CreationModel creationModel, Object obj,
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var consentId = ReadTypeByAttribute.getPaperIdFromObject(creationModel, objectClass, obj, propertyName);
		if(consentId.isPresent())
		{
			return ObjectFinder.checkIfConsentExists(consentId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Consent> tryGetConsentById(CreationModel creationModel, Object obj,
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var consentId = FieldFinder.getObjectToReadFrom(creationModel, objectClass, obj, propertyName);
		if(consentId.isPresent())
		{
			return ObjectFinder.checkIfConsentExists((String)consentId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Document> tryGetDocumentFromObject(CreationModel creationModel, Object obj,
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var documentId = ReadTypeByAttribute.getPaperIdFromObject(creationModel, objectClass, obj, propertyName);
		if(documentId.isPresent())
		{ 
			return ObjectFinder.checkIfDocumentExists(documentId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Document> tryGetDocumentById(CreationModel creationModel, Object obj,
			Class<? extends Object> objectClass, String propertyName, PrivacyPolicy model) {
		var documentId = FieldFinder.getObjectToReadFrom(creationModel, objectClass, obj, propertyName);
		if(documentId.isPresent())
		{
			return ObjectFinder.checkIfDocumentExists((String)documentId.get(), model);
		}
		return Optional.empty();
	}
}
