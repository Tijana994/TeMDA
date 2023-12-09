package com.security.model.validation.helpers;

import java.util.Optional;

import privacyModel.Consent;
import privacyModel.Location;
import privacyModel.PolicyStatement;
import privacyModel.Principal;
import privacyModel.PrivacyPolicy;

public class ObjectFinder {

	public static Optional<Principal> checkIfPrincipalExists(String fieldId, PrivacyPolicy model) 
	{
		if(fieldId == null)
		{
			return Optional.empty();
		}
		var principal = findPrincipal(fieldId, model);
		if(principal.isEmpty())
		{
			System.out.println("There is no prinipal with id " + fieldId);
		}
		return principal;
	}
	
	public static Optional<Location> checkIfLocationExists(String locationId, PrivacyPolicy model) 
	{
		if(locationId == null)
		{
			return Optional.empty();
		}
		var location = findLocation(locationId, model);
		if(location.isEmpty())
		{
			System.out.println("There is no location with id " + locationId);
		}
		return location;
	}
	
	public static Optional<Consent> checkIfConsentExists(String consentId, PrivacyPolicy model) 
	{
		if(consentId == null)
		{
			return Optional.empty();
		}
		var consent = findConsent(consentId, model);
		if(consent.isEmpty())
		{
			System.out.println("There is no consent with id " + consentId);
		}
		return consent;
	}
	
	public static Optional<PolicyStatement> checkIfPolicyStatementExists(String fieldId, PrivacyPolicy model) 
	{
		if(fieldId == null)
		{
			return Optional.empty();
		}
		var policyStatement = findPolicyStatement(fieldId, model);
		if(policyStatement.isEmpty())
		{
			System.out.println("There is no policy statement with id " + fieldId);
		}
		return policyStatement;
	}
	
	private static Optional<Principal> findPrincipal(String principalId, PrivacyPolicy model)
	{
		return model.getAllPrincipals().stream()
		   .filter(principal -> principal.getName().equals(principalId)).findFirst();
	}
	
	private static Optional<Location> findLocation(String locationId, PrivacyPolicy model)
	{
		return model.getLocations().stream()
		   .filter(location -> location.getName().equals(locationId)).findFirst();
	}
	
	private static Optional<Consent> findConsent(String consentId, PrivacyPolicy model)
	{
		return model.getAllConsents().stream()
		   .filter(consent -> consent.getName().equals(consentId)).findFirst();
	}
	
	private static Optional<PolicyStatement> findPolicyStatement(String policyStatementId, PrivacyPolicy model)
	{
		return model.getPolicyStatements().stream()
		   .filter(policyStatement -> policyStatement.getName().equals(policyStatementId)).findFirst();
	}
}
