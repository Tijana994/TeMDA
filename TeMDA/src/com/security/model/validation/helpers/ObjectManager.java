package com.security.model.validation.helpers;

import java.util.Optional;

import org.aspectj.lang.JoinPoint;

import com.security.model.validation.annotations.creators.CreateDenialAnnotation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

import privacyModel.AbstractComplaint;
import privacyModel.Complaint;
import privacyModel.Denial;
import privacyModel.PolicyStatement;
import privacyModel.Principal;
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
}
