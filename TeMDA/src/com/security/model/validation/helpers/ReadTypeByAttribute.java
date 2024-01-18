package com.security.model.validation.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;

import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.PolicyStatementAnnotation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

import privacyModel.Document;
import privacyModel.Location;
import privacyModel.PolicyStatement;
import privacyModel.Principal;
import privacyModel.PrivacyData;
import privacyModel.PrivacyPolicy;
import privacyModel.SharedPrivacyData;

public class ReadTypeByAttribute {

	public static Optional<String> getConsentIdFromObject(Class<?> objectClass, Object obj, String propertyame, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp)
	{
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyame, jp);
			if(!value.isPresent())
			{
				return Optional.empty();
			}
			PaperAnnotation paper = value.get().getClass().getAnnotation(PaperAnnotation.class);
			if(paper == null)
			{
				System.out.println("There is no paper annotation");
			}
			else
			{
				var consentId = (String)FieldFinder.getFieldValue(paper.id(), value.get(), value.get().getClass());
				return Optional.of(consentId);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return Optional.empty();
	}
	
	public static Optional<String> getPolicyStatementIdFromObject(Class<?> objectClass, Object obj, String propertyame, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp)
	{
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyame, jp);
			if(!value.isPresent())
			{
				return Optional.empty();
			}
			PolicyStatementAnnotation policyStatement = value.get().getClass().getAnnotation(PolicyStatementAnnotation.class);
			if(policyStatement == null)
			{
				System.out.println("There is no policy statement annotation");
			}
			else
			{
				var consentId = (String)FieldFinder.getFieldValue(policyStatement.id(), value.get(), value.get().getClass());
				return Optional.of(consentId);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return Optional.empty();
	}

	public static List<Principal> getPrincipalsById(Object retFromObj, Class<? extends Object> retClass, 
			String propertyName, PrivacyPolicy model)
	{
		var principals = new ArrayList<Principal>();
		var principalsIds = FieldFinder.getFieldValue(propertyName, retFromObj, retClass);
		if(!(principalsIds instanceof List))
		{
			System.out.println("Property" + propertyName + "should be type of List.");
			return principals;
		}
		var list = (List<?>) principalsIds;
		for(var principalId : list)
		{
			var principal = ObjectFinder.checkIfPrincipalExists((String)principalId, model);
			if(principal.isPresent())
			{
				principals.add(principal.get());
			}
		}
		return principals;
	}
	
	public static List<SharedPrivacyData> getSharedPrivacyDataById(String[] datasId, PrivacyPolicy model)
	{
		var datas = new ArrayList<SharedPrivacyData>();
		for(var dataId : datasId)
		{
			var data = ObjectFinder.checkIfSharedPrivacyDataExists(dataId, model);
			if(data.isPresent())
			{
				datas.add(data.get());
			}
		}
		return datas;
	}
	
	public static List<PrivacyData> getPrivacyDatasById(Object retFromObj, Class<? extends Object> retClass, 
			String propertyName, PrivacyPolicy model)
	{
		var datas = new ArrayList<PrivacyData>();
		var dataIds = FieldFinder.getFieldValue(propertyName, retFromObj, retClass);
		if(!(dataIds instanceof List))
		{
			System.out.println("Property" + propertyName + "should be type of List.");
			return datas;
		}
		var list = (List<?>) dataIds;
		for(var dataId : list)
		{
			var data = ObjectFinder.checkIfPrivacyDataExists((String)dataId, model);
			if(data.isPresent())
			{
				datas.add(data.get());
			}
		}
		return datas;
	}
	
	public static List<Location> getLocationsById(Object retFromObj, Class<? extends Object> retClass, 
			String propertyName, PrivacyPolicy model)
	{
		var locations = new ArrayList<Location>();
		var locationIds = FieldFinder.getFieldValue(propertyName, retFromObj, retClass);
		if(!(locationIds instanceof List))
		{
			System.out.println("Property" + propertyName + "should be type of List.");
			return locations;
		}
		var list = (List<?>) locationIds;
		for(var locationId : list)
		{
			var location = ObjectFinder.checkIfLocationExists((String)locationId, model);
			if(location.isPresent())
			{
				locations.add(location.get());
			}
		}
		return locations;
	}
	
	public static List<PolicyStatement> getPolicyStatementsById(Object retFromObj, Class<? extends Object> retClass, 
			String propertyName, PrivacyPolicy model)
	{
		var policyStatments = new ArrayList<PolicyStatement>();
		var policyStatmentIds = FieldFinder.getFieldValue(propertyName, retFromObj, retClass);
		if(!(policyStatmentIds instanceof List))
		{
			System.out.println("Property" + propertyName + "should be type of List.");
			return policyStatments;
		}
		var list = (List<?>) policyStatmentIds;
		for(var policyStatmentId : list)
		{
			var location = ObjectFinder.checkIfPolicyStatementExists((String)policyStatmentId, model);
			if(location.isPresent())
			{
				policyStatments.add(location.get());
			}
		}
		return policyStatments;
	}
	
	public static List<Document> getDocumentsById(Object retFromObj, Class<? extends Object> retClass, 
			String propertyName, PrivacyPolicy model)
	{
		var documents = new ArrayList<Document>();
		var documentIds = FieldFinder.getFieldValue(propertyName, retFromObj, retClass);
		if(!(documentIds instanceof List))
		{
			System.out.println("Property" + propertyName + "should be type of List.");
			return documents;
		}
		var list = (List<?>) documentIds;
		for(var documentId : list)
		{
			var document = ObjectFinder.checkIfDocumentExists((String)documentId, model);
			if(document.isPresent())
			{
				documents.add(document.get());
			}
		}
		return documents;
	}
}
