package com.security.model.validation.helpers;

import java.util.Optional;

import privacyModel.Complaint;
import privacyModel.Consent;
import privacyModel.Document;
import privacyModel.Location;
import privacyModel.PolicyStatement;
import privacyModel.Principal;
import privacyModel.PrivacyData;
import privacyModel.PrivacyPolicy;
import privacyModel.SharedPrivacyData;

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
	
	public static Optional<SharedPrivacyData> checkIfSharedPrivacyDataExists(String fieldId, PrivacyPolicy model) 
	{
		if(fieldId == null)
		{
			return Optional.empty();
		}
		var sharedPrivacyData = findSharedPrivacyData(fieldId, model);
		if(sharedPrivacyData.isEmpty())
		{
			System.out.println("There is no shared privacy data with id " + fieldId);
		}
		return sharedPrivacyData;
	}
	
	public static Optional<PrivacyData> checkIfPrivacyDataExists(String fieldId, PrivacyPolicy model) 
	{
		if(fieldId == null)
		{
			return Optional.empty();
		}
		var privacyData = findPrivacyData(fieldId, model);
		if(privacyData.isEmpty())
		{
			System.out.println("There is no privacy data with id " + fieldId);
		}
		return privacyData;
	}
	
	public static Optional<Complaint> checkIfComplaintExists(String fieldId, PrivacyPolicy model) 
	{
		if(fieldId == null)
		{
			return Optional.empty();
		}
		var complaint = findComplaint(fieldId, model);
		if(complaint.isEmpty())
		{
			System.out.println("There is no complaint with id " + fieldId);
		}
		return complaint;
	}
	
	public static Optional<Document> checkIfDocumentExists(String fieldId, PrivacyPolicy model) 
	{
		if(fieldId == null)
		{
			return Optional.empty();
		}
		var document = findDocument(fieldId, model);
		if(document.isEmpty())
		{
			System.out.println("There is no document with id " + fieldId);
		}
		return document;
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
	
	private static Optional<SharedPrivacyData> findSharedPrivacyData(String sharedPrivacyDataId, PrivacyPolicy model)
	{
		return model.getAllSharedPrivacyData().stream()
		   .filter(sharedPrivacyData -> sharedPrivacyData.getName().equals(sharedPrivacyDataId)).findFirst();
	}
	
	private static Optional<PrivacyData> findPrivacyData(String privacyDataId, PrivacyPolicy model)
	{
		return model.getAllDatas().stream()
		   .filter(privacyData -> privacyData.getName().equals(privacyDataId)).findFirst();
	}
	
	private static Optional<Complaint> findComplaint(String complaintId, PrivacyPolicy model)
	{
		return model.getAllComplaints().stream()
		   .filter(complaint -> complaint.getName().equals(complaintId)).findFirst();
	}
	
	private static Optional<Document> findDocument(String documentId, PrivacyPolicy model)
	{
		return model.getAllDocuments().stream()
		   .filter(complaint -> complaint.getName().equals(documentId)).findFirst();
	}
}
