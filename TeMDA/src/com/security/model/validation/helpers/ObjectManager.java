package com.security.model.validation.helpers;

import java.util.Optional;

import org.aspectj.lang.JoinPoint;

import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

import privacyModel.Complaint;
import privacyModel.Consent;
import privacyModel.Document;
import privacyModel.Location;
import privacyModel.PolicyStatement;
import privacyModel.Principal;
import privacyModel.PrivacyData;
import privacyModel.PrivacyPolicy;

public class ObjectManager {
	
	public static Optional<PolicyStatement> tryGetPolicyStatementFromObject(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var policyStatementId = ReadTypeByAttribute.getPolicyStatementIdFromObject(objectClass, obj, propertyName, 
				parametersLocation, jp);
		if(policyStatementId.isPresent())
		{
			return ObjectFinder.checkIfPolicyStatementExists(policyStatementId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<PolicyStatement> tryGetPolicyStatementById(Object obj, Class<? extends Object> objectClass, 
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var policyStatementId = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
		if(policyStatementId.isPresent())
		{
			return ObjectFinder.checkIfPolicyStatementExists((String)policyStatementId.get(), model);
		}
		return Optional.empty();
	}

	public static Optional<Principal> tryGetPrincipalByFromObject(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var principalId = ReadTypeByAttribute.getPrincipalIdFromObject(objectClass, obj, propertyName, parametersLocation, jp);
		if(principalId.isPresent())
		{
			return ObjectFinder.checkIfPrincipalExists(principalId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Principal> tryGetPrincipalByById(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var principalId = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
		if(principalId.isPresent())
		{
			return ObjectFinder.checkIfPrincipalExists((String)principalId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<PrivacyData> tryGetPrivacyDataByFromObject(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var privacyDataId = ReadTypeByAttribute.getPrivacyDataIdFromObject(objectClass, obj, propertyName, parametersLocation, jp);
		if(privacyDataId.isPresent())
		{
			return ObjectFinder.checkIfPrivacyDataExists(privacyDataId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<PrivacyData> tryGetPrivacyDataByById(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var privacyDataId = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
		if(privacyDataId.isPresent())
		{
			return ObjectFinder.checkIfPrivacyDataExists((String)privacyDataId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Complaint> tryGetComplaintFromObject(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var complaintId = ReadTypeByAttribute.getComplaintIdFromObject(objectClass, obj, propertyName, parametersLocation, jp);
		if(complaintId.isPresent())
		{
			return ObjectFinder.checkIfComplaintExists(complaintId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Complaint> tryGetComplaintById(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var complaintId = FieldFinder.getObjectToReadFrom(objectClass, obj,parametersLocation, propertyName, jp);
		if(complaintId.isPresent())
		{
			return ObjectFinder.checkIfComplaintExists((String)complaintId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Location> tryGetLocationFromObject(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var locationId = ReadTypeByAttribute.getLocationIdFromObject(objectClass, obj, propertyName, parametersLocation, jp);
		if(locationId.isPresent())
		{
			return ObjectFinder.checkIfLocationExists(locationId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Location> tryGetLocationById(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var locationId = FieldFinder.getObjectToReadFrom(objectClass, obj,parametersLocation, propertyName, jp);
		if(locationId.isPresent())
		{
			return ObjectFinder.checkIfLocationExists((String)locationId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Consent> tryGetConsentFromObject(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var consentId = ReadTypeByAttribute.getPaperIdFromObject(objectClass, obj, propertyName, parametersLocation, jp);
		if(consentId.isPresent())
		{
			return ObjectFinder.checkIfConsentExists(consentId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Consent> tryGetConsentById(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var consentId = FieldFinder.getObjectToReadFrom(objectClass, obj,parametersLocation, propertyName, jp);
		if(consentId.isPresent())
		{
			return ObjectFinder.checkIfConsentExists((String)consentId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Document> tryGetDocumentFromObject(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var documentId = ReadTypeByAttribute.getPaperIdFromObject(objectClass, obj, propertyName, parametersLocation, jp);
		if(documentId.isPresent())
		{
			return ObjectFinder.checkIfDocumentExists(documentId.get(), model);
		}
		return Optional.empty();
	}
	
	public static Optional<Document> tryGetDocumentById(Object obj, Class<? extends Object> objectClass,
			String propertyName, PrivacyPolicy model, ParametersObjectsLocation parametersLocation, JoinPoint jp) {
		var documentId = FieldFinder.getObjectToReadFrom(objectClass, obj,parametersLocation, propertyName, jp);
		if(documentId.isPresent())
		{
			return ObjectFinder.checkIfDocumentExists((String)documentId.get(), model);
		}
		return Optional.empty();
	}
}
