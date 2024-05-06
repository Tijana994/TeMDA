package com.security.model.validation.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;

import com.security.model.validation.annotations.ComplaintAnnotation;
import com.security.model.validation.annotations.LocationAnnotation;
import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.PolicyStatementAnnotation;
import com.security.model.validation.annotations.PrincipalAnnotation;
import com.security.model.validation.annotations.PrivacyDataAnnotation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

import privacyModel.Document;
import privacyModel.Location;
import privacyModel.PolicyStatement;
import privacyModel.Principal;
import privacyModel.PrivacyData;
import privacyModel.PrivacyPolicy;
import privacyModel.SharedPrivacyData;

public class ReadTypeByAttribute {

	public static Optional<String> getConsentIdFromObject(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp)
	{
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
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
				var Id = (String)FieldFinder.getFieldValue(paper.id(), value.get(), value.get().getClass());
				return Optional.of(Id);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return Optional.empty();
	}
	
	public static Optional<String> getComplaintIdFromObject(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp)
	{
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
			if(!value.isPresent())
			{
				return Optional.empty();
			}
			ComplaintAnnotation complaint = value.get().getClass().getAnnotation(ComplaintAnnotation.class);
			if(complaint == null)
			{
				System.out.println("There is no complaint annotation");
			}
			else
			{
				var Id = (String)FieldFinder.getFieldValue(complaint.id(), value.get(), value.get().getClass());
				return Optional.of(Id);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return Optional.empty();
	}
	
	public static Optional<String> getPolicyStatementIdFromObject(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp)
	{
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
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
				var Id = (String)FieldFinder.getFieldValue(policyStatement.id(), value.get(), value.get().getClass());
				return Optional.of(Id);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return Optional.empty();
	}
	
	public static Optional<String> getLocationIdFromObject(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp)
	{
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
			if(!value.isPresent())
			{
				return Optional.empty();
			}
			LocationAnnotation location = value.get().getClass().getAnnotation(LocationAnnotation.class);
			if(location == null)
			{
				System.out.println("There is no location annotation");
			}
			else
			{
				var Id = (String)FieldFinder.getFieldValue(location.id(), value.get(), value.get().getClass());
				return Optional.of(Id);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return Optional.empty();
	}
	
	public static Optional<String> getPrivacyDataIdFromObject(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp)
	{
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
			if(!value.isPresent())
			{
				return Optional.empty();
			}
			PrivacyDataAnnotation privacyData = value.getClass().getAnnotation(PrivacyDataAnnotation.class);
			if(privacyData == null)
			{
				System.out.println("There is no privacy data annotation");
			}
			else
			{
				var Id = (String)FieldFinder.getFieldValue(privacyData.id(), value, value.getClass());
				return Optional.of(Id);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return Optional.empty();
	}
	
	public static Optional<String> getPrincipalIdFromObject(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp)
	{
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
			if(!value.isPresent())
			{
				return Optional.empty();
			}
			PrincipalAnnotation principal = value.get().getClass().getAnnotation(PrincipalAnnotation.class);
			if(principal == null)
			{
				System.out.println("There is no principal annotation");
			}
			else
			{
				var Id = (String)FieldFinder.getFieldValue(principal.id(), value.get(), value.get().getClass());
				return Optional.of(Id);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return Optional.empty();
	}
	
	public static List<Principal> getPrincipalsFromObject(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp, PrivacyPolicy model)
	{
		var principals = new ArrayList<Principal>();
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return principals;
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
			if(!value.isPresent())
			{
				return principals;
			}
			if(!(value.get() instanceof List))
			{
				System.out.println("Property" + propertyName + "should be type of List.");
				return principals;
			}
			var list = (List<?>) value.get();
			for(var principalObject : list)
			{
				PrincipalAnnotation principal = principalObject.getClass().getAnnotation(PrincipalAnnotation.class);
				if(principal == null)
				{
					System.out.println("There is no principal annotation");
				}
				else
				{
					var id = (String)FieldFinder.getFieldValue(principal.id(), principalObject, principalObject.getClass());
					if(id != null)
					{
						addPrincipalIfExists(model, principals, id);
					}
				}

			}
			return principals;
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return principals;
	}

	public static List<Principal> getPrincipalsById(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp, PrivacyPolicy model)
	{
		var principals = new ArrayList<Principal>();
		var principalsIds = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
		if(!principalsIds.isPresent())
		{
			return principals;
		}
		if(!(principalsIds.get() instanceof List))
		{
			System.out.println("Property" + propertyName + "should be type of List.");
			return principals;
		}
		var list = (List<?>) principalsIds.get();
		for(var principalId : list)
		{
			addPrincipalIfExists(model, principals, (String)principalId);
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
	
	private static void addPrincipalIfExists(PrivacyPolicy model, ArrayList<Principal> principals, String Id) {
		var principal = ObjectFinder.checkIfPrincipalExists((String)Id, model);
		if(principal.isPresent())
		{
			principals.add(principal.get());
		}
	}
	
	public static List<PrivacyData> getPrivacyDatasFromObject(Class<?> objectClass, Object obj, String propertyName,
			ParametersObjectsLocation parametersLocation, JoinPoint jp, PrivacyPolicy model)
	{
		var datas = new ArrayList<PrivacyData>();
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return datas;
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
			if(!value.isPresent())
			{
				return datas;
			}
			if(!(value.get() instanceof List))
			{
				System.out.println("Property" + propertyName + "should be type of List.");
				return datas;
			}
			var list = (List<?>) value.get();
			for(var dataObject : list)
			{
				PrivacyDataAnnotation privacyData = dataObject.getClass().getAnnotation(PrivacyDataAnnotation.class);
				if(privacyData == null)
				{
					System.out.println("There is no privacy data annotation");
				}
				else
				{
					var Id = (String)FieldFinder.getFieldValue(privacyData.id(), dataObject, dataObject.getClass());
					if(Id != null)
					{
						addPrivacyDataIfExists(model, datas, Id);
					}
				}
			}
			return datas;
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return datas;
	}

	public static List<PrivacyData> getPrivacyDatasById(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp, PrivacyPolicy model)
	{
		var datas = new ArrayList<PrivacyData>();
		var dataIds = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
		if(!dataIds.isPresent())
		{
			return datas;
		}
		if(!(dataIds.get() instanceof List))
		{
			System.out.println("Property" + propertyName + "should be type of List.");
			return datas;
		}
		var list = (List<?>) dataIds.get();
		for(var id : list)
		{
			addPrivacyDataIfExists(model, datas, (String)id);
		}
		return datas;
	}
	
	private static void addPrivacyDataIfExists(PrivacyPolicy model, ArrayList<PrivacyData> datas, String Id) {
		var data = ObjectFinder.checkIfPrivacyDataExists(Id, model);
		if(data.isPresent())
		{
			datas.add(data.get());
		}
	}
	
	public static List<Location> getLocationsFromObject(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp, PrivacyPolicy model)
	{
		var locations = new ArrayList<Location>();
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return locations;
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
			if(!value.isPresent())
			{
				return locations;
			}
			if(!(value.get() instanceof List))
			{
				System.out.println("Property" + propertyName + "should be type of List.");
				return locations;
			}
			var list = (List<?>) value.get();
			for(var locationObject : list)
			{
				LocationAnnotation location = locationObject.getClass().getAnnotation(LocationAnnotation.class);
				if(location == null)
				{
					System.out.println("There is no location annotation");
				}
				else
				{
					var id = (String)FieldFinder.getFieldValue(location.id(), locationObject, locationObject.getClass());
					if(id != null)
					{
						addLocationIfExists(model, locations, id);
					}
				}

			}
			return locations;
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return locations;
	}
	
	public static List<Location> getLocationsById(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp, PrivacyPolicy model)
	{
		var locations = new ArrayList<Location>();
		var locationIds = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
		if(!locationIds.isPresent())
		{
			return locations;
		}
		if(!(locationIds.get() instanceof List))
		{
			System.out.println("Property" + propertyName + "should be type of List.");
			return locations;
		}
		var list = (List<?>) locationIds.get();
		for(var locationId : list)
		{
			addLocationIfExists(model, locations, (String)locationId);
		}
		return locations;
	}

	private static void addLocationIfExists(PrivacyPolicy model, ArrayList<Location> locations, String id) {
		var location = ObjectFinder.checkIfLocationExists(id, model);
		if(location.isPresent())
		{
			locations.add(location.get());
		}
	}
	
	public static List<PolicyStatement> getPolicyStatementsFromObject(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp, PrivacyPolicy model)
	{
		var policyStatments = new ArrayList<PolicyStatement>();
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return policyStatments;
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
			if(!value.isPresent())
			{
				return policyStatments;
			}
			if(!(value.get() instanceof List))
			{
				System.out.println("Property" + propertyName + "should be type of List.");
				return policyStatments;
			}
			var list = (List<?>) value.get();
			for(var policyStatementObject : list)
			{
				PolicyStatementAnnotation policyStatement = policyStatementObject.getClass().getAnnotation(PolicyStatementAnnotation.class);
				if(policyStatement == null)
				{
					System.out.println("There is no policy statement annotation");
				}
				else
				{
					var id = (String)FieldFinder.getFieldValue(policyStatement.id(), policyStatementObject, policyStatementObject.getClass());
					if(id != null)
					{
						addPolicyStatementIfExists(model, policyStatments, id);
					}
				}

			}
			return policyStatments;
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return policyStatments;
	}

	public static List<PolicyStatement> getPolicyStatementsById(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp, PrivacyPolicy model)
	{
		var policyStatments = new ArrayList<PolicyStatement>();
		var policyStatmentIds = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
		if(!policyStatmentIds.isPresent())
		{
			return policyStatments;
		}
		if(!(policyStatmentIds.get() instanceof List))
		{
			System.out.println("Property" + propertyName + "should be type of List.");
			return policyStatments;
		}
		var list = (List<?>) policyStatmentIds.get();
		for(var id : list)
		{
			addPolicyStatementIfExists(model, policyStatments, (String)id);
		}
		return policyStatments;
	}
	
	private static void addPolicyStatementIfExists(PrivacyPolicy model, ArrayList<PolicyStatement> policyStatments,
			String Id) {
		var policyStatement = ObjectFinder.checkIfPolicyStatementExists(Id, model);
		if(policyStatement.isPresent())
		{
			policyStatments.add(policyStatement.get());
		}
	}
	
	public static List<Document> getDocumentsFromObject(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp, PrivacyPolicy model)
	{
		var documents = new ArrayList<Document>();
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return documents;
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
			if(!value.isPresent())
			{
				return documents;
			}
			if(!(value.get() instanceof List))
			{
				System.out.println("Property" + propertyName + "should be type of List.");
				return documents;
			}
			var list = (List<?>) value.get();
			for(var documentObject : list)
			{
				PaperAnnotation paper = documentObject.getClass().getAnnotation(PaperAnnotation.class);
				if(paper == null)
				{
					System.out.println("There is no paper annotation");
				}
				else
				{
					var id = (String)FieldFinder.getFieldValue(paper.id(), documentObject, documentObject.getClass());
					if(id != null)
					{
						addDocumentIfExists(model, documents, id);
					}
				}

			}
			return documents;
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return documents;
	}
	
	public static List<Document> getDocumentsById(Class<?> objectClass, Object obj, String propertyName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp, PrivacyPolicy model)
	{
		var documents = new ArrayList<Document>();
		var documentIds = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, propertyName, jp);
		if(!documentIds.isPresent())
		{
			return documents;
		}
		if(!(documentIds.get() instanceof List))
		{
			System.out.println("Property " + propertyName + " should be type of List.");
			return documents;
		}
		var list = (List<?>) documentIds.get();
		for(var documentId : list)
		{
			addDocumentIfExists(model, documents, (String)documentId);
		}
		return documents;
	}
	
	private static void addDocumentIfExists(PrivacyPolicy model, ArrayList<Document> documents, String id) {
		var document = ObjectFinder.checkIfDocumentExists(id, model);
		if(document.isPresent())
		{
			documents.add(document.get());
		}
	}
}
