package com.security.model.validation.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.security.model.validation.annotations.ComplaintAnnotation;
import com.security.model.validation.annotations.LocationAnnotation;
import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.PolicyStatementAnnotation;
import com.security.model.validation.annotations.PrincipalAnnotation;
import com.security.model.validation.annotations.PrivacyDataAnnotation;
import com.security.model.validation.helpers.interfaces.ILogger;
import com.security.model.validation.models.CreationModel;

import privacyModel.Document;
import privacyModel.Location;
import privacyModel.PolicyStatement;
import privacyModel.Principal;
import privacyModel.PrivacyData;
import privacyModel.PrivacyPolicy;
import privacyModel.SharedPrivacyData;

public class ReadTypeByAttribute {

	private ILogger logger;
	private FieldFinder fieldFinder;
	private ObjectFinder objectFinder;
	
	public ReadTypeByAttribute(ILogger logger, FieldFinder fieldFinder, ObjectFinder objectFinder)
	{
		this.logger = logger;
		this.fieldFinder = fieldFinder;
		this.objectFinder = objectFinder;
	}
	
	public Optional<String> getPaperIdFromObject(CreationModel creationModel, String propertyName)
	{
		if(creationModel.getObject() == null)
		{
			System.out.println("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
			if(!value.isPresent())
			{
				return Optional.empty();
			}
			PaperAnnotation paper = value.get().getClass().getAnnotation(PaperAnnotation.class);
			if(paper == null)
			{
				logger.LogErrorMessage("There is no paper annotation");
			}
			else
			{
				var Id = (String)fieldFinder.getFieldValue(paper.id(), value.get(), value.get().getClass());
				return Optional.of(Id);
			}
		}
		catch(Exception ex)
		{
			logger.LogExceptionMessage(ex);
		}

		return Optional.empty();
	}
	
	public Optional<String> getComplaintIdFromObject(CreationModel creationModel, String propertyName)
	{
		if(creationModel.getObject() == null)
		{
			logger.LogErrorMessage("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
			if(!value.isPresent())
			{
				return Optional.empty();
			}
			ComplaintAnnotation complaint = value.get().getClass().getAnnotation(ComplaintAnnotation.class);
			if(complaint == null)
			{
				logger.LogErrorMessage("There is no complaint annotation");
			}
			else
			{
				var Id = (String)fieldFinder.getFieldValue(complaint.id(), value.get(), value.get().getClass());
				return Optional.of(Id);
			}
		}
		catch(Exception ex)
		{
			logger.LogExceptionMessage(ex);
		}

		return Optional.empty();
	}
	
	public Optional<String> getPolicyStatementIdFromObject(CreationModel creationModel, String propertyName)
	{
		if(creationModel.getObject() == null)
		{
			logger.LogErrorMessage("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
			if(!value.isPresent())
			{
				return Optional.empty();
			}
			PolicyStatementAnnotation policyStatement = value.get().getClass().getAnnotation(PolicyStatementAnnotation.class);
			if(policyStatement == null)
			{
				logger.LogErrorMessage("There is no policy statement annotation");
			}
			else
			{
				var Id = (String)fieldFinder.getFieldValue(policyStatement.id(), value.get(), value.get().getClass());
				return Optional.of(Id);
			}
		}
		catch(Exception ex)
		{
			logger.LogExceptionMessage(ex);
		}

		return Optional.empty();
	}
	
	public Optional<String> getLocationIdFromObject(CreationModel creationModel, String propertyName)
	{
		if(creationModel.getObject() == null)
		{
			System.out.println("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
			if(!value.isPresent())
			{
				return Optional.empty();
			}
			LocationAnnotation location = value.get().getClass().getAnnotation(LocationAnnotation.class);
			if(location == null)
			{
				logger.LogErrorMessage("There is no location annotation");
			}
			else
			{
				var Id = (String)fieldFinder.getFieldValue(location.id(), value.get(), value.get().getClass());
				return Optional.of(Id);
			}
		}
		catch(Exception ex)
		{
			logger.LogExceptionMessage(ex);
		}

		return Optional.empty();
	}
	
	public Optional<String> getPrivacyDataIdFromObject(CreationModel creationModel, String propertyName)
	{
		if(creationModel.getObject() == null)
		{
			logger.LogErrorMessage("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
			if(!value.isPresent())
			{
				return Optional.empty();
			}
			PrivacyDataAnnotation privacyData = value.getClass().getAnnotation(PrivacyDataAnnotation.class);
			if(privacyData == null)
			{
				logger.LogErrorMessage("There is no privacy data annotation");
			}
			else
			{
				var Id = (String)fieldFinder.getFieldValue(privacyData.id(), value, value.getClass());
				return Optional.of(Id);
			}
		}
		catch(Exception ex)
		{
			logger.LogExceptionMessage(ex);
		}

		return Optional.empty();
	}
	
	public Optional<String> getPrincipalIdFromObject(CreationModel creationModel, String propertyName)
	{
		if(creationModel.getObject() == null)
		{
			logger.LogErrorMessage("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
			if(!value.isPresent())
			{
				return Optional.empty();
			}
			PrincipalAnnotation principal = value.get().getClass().getAnnotation(PrincipalAnnotation.class);
			if(principal == null)
			{
				logger.LogErrorMessage("There is no principal annotation");
			}
			else
			{
				var Id = (String)fieldFinder.getFieldValue(principal.id(), value.get(), value.get().getClass());
				return Optional.of(Id);
			}
		}
		catch(Exception ex)
		{
			logger.LogExceptionMessage(ex);
		}

		return Optional.empty();
	}
	
	public List<Principal> getPrincipalsFromObject(CreationModel creationModel, String propertyName, PrivacyPolicy model)
	{
		var principals = new ArrayList<Principal>();
		if(creationModel.getObject() == null)
		{
			logger.LogErrorMessage("Object is not instantiated.");
			return principals;
		}
		try
		{
			var value = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
			if(!value.isPresent())
			{
				return principals;
			}
			if(!(value.get() instanceof List))
			{
				logger.LogErrorMessage("Property" + propertyName + "should be type of List.");
				return principals;
			}
			var list = (List<?>) value.get();
			for(var principalObject : list)
			{
				PrincipalAnnotation principal = principalObject.getClass().getAnnotation(PrincipalAnnotation.class);
				if(principal == null)
				{
					logger.LogErrorMessage("There is no principal annotation");
				}
				else
				{
					var id = (String)fieldFinder.getFieldValue(principal.id(), principalObject, principalObject.getClass());
					if(id != null)
					{
						addPrincipalIfExists(model, principals, id);
					}
				}

			}
			return principals;
		}
		catch(Exception ex)
		{
			logger.LogExceptionMessage(ex);
		}

		return principals;
	}

	public List<Principal> getPrincipalsById(CreationModel creationModel, String propertyName, PrivacyPolicy model)
	{
		var principals = new ArrayList<Principal>();
		var principalsIds = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
		if(!principalsIds.isPresent())
		{
			return principals;
		}
		if(!(principalsIds.get() instanceof List))
		{
			logger.LogErrorMessage("Property" + propertyName + "should be type of List.");
			return principals;
		}
		var list = (List<?>) principalsIds.get();
		for(var principalId : list)
		{
			addPrincipalIfExists(model, principals, (String)principalId);
		}
		return principals;
	}
	
	public List<SharedPrivacyData> getSharedPrivacyDataById(String[] datasId, PrivacyPolicy model)
	{
		var datas = new ArrayList<SharedPrivacyData>();
		for(var dataId : datasId)
		{
			var data = objectFinder.checkIfSharedPrivacyDataExists(dataId, model);
			if(data.isPresent())
			{
				datas.add(data.get());
			}
		}
		return datas;
	}
	
	private void addPrincipalIfExists(PrivacyPolicy model, ArrayList<Principal> principals, String Id) {
		var principal = objectFinder.checkIfPrincipalExists((String)Id, model);
		if(principal.isPresent())
		{
			principals.add(principal.get());
		}
	}
	
	public List<PrivacyData> getPrivacyDatasFromObject(CreationModel creationModel, String propertyName, PrivacyPolicy model)
	{
		var datas = new ArrayList<PrivacyData>();
		if(creationModel.getObject() == null)
		{
			logger.LogErrorMessage("Object is not instantiated.");
			return datas;
		}
		try
		{
			var value = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
			if(!value.isPresent())
			{
				return datas;
			}
			if(!(value.get() instanceof List))
			{
				logger.LogErrorMessage("Property" + propertyName + "should be type of List.");
				return datas;
			}
			var list = (List<?>) value.get();
			for(var dataObject : list)
			{
				PrivacyDataAnnotation privacyData = dataObject.getClass().getAnnotation(PrivacyDataAnnotation.class);
				if(privacyData == null)
				{
					logger.LogErrorMessage("There is no privacy data annotation");
				}
				else
				{
					var Id = (String)fieldFinder.getFieldValue(privacyData.id(), dataObject, dataObject.getClass());
					if(Id != null)
					{
						addPrivacyDataIfExists(model, datas, Id);
					}
				}
			}
			return datas;
		}
		catch(Exception ex)
		{
			logger.LogExceptionMessage(ex);
		}

		return datas;
	}

	public List<PrivacyData> getPrivacyDatasById(CreationModel creationModel, String propertyName, PrivacyPolicy model)
	{
		var datas = new ArrayList<PrivacyData>();
		var dataIds = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
		if(!dataIds.isPresent())
		{
			return datas;
		}
		if(!(dataIds.get() instanceof List))
		{
			logger.LogErrorMessage("Property" + propertyName + "should be type of List.");
			return datas;
		}
		var list = (List<?>) dataIds.get();
		for(var id : list)
		{
			addPrivacyDataIfExists(model, datas, (String)id);
		}
		return datas;
	}
	
	private void addPrivacyDataIfExists(PrivacyPolicy model, ArrayList<PrivacyData> datas, String Id) {
		var data = objectFinder.checkIfPrivacyDataExists(Id, model);
		if(data.isPresent())
		{
			datas.add(data.get());
		}
	}
	
	public List<Location> getLocationsFromObject(CreationModel creationModel, String propertyName, PrivacyPolicy model)
	{
		var locations = new ArrayList<Location>();
		if(creationModel.getObject() == null)
		{
			logger.LogErrorMessage("Object is not instantiated.");
			return locations;
		}
		try
		{
			var value = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
			if(!value.isPresent())
			{
				return locations;
			}
			if(!(value.get() instanceof List))
			{
				logger.LogErrorMessage("Property" + propertyName + "should be type of List.");
				return locations;
			}
			var list = (List<?>) value.get();
			for(var locationObject : list)
			{
				LocationAnnotation location = locationObject.getClass().getAnnotation(LocationAnnotation.class);
				if(location == null)
				{
					logger.LogErrorMessage("There is no location annotation");
				}
				else
				{
					var id = (String)fieldFinder.getFieldValue(location.id(), locationObject, locationObject.getClass());
					if(id != null)
					{
						addLocationIfExists(model, locations, id);
					}
				}

			}
			return locations;
		}
		catch(Exception ex)
		{
			logger.LogExceptionMessage(ex);
		}

		return locations;
	}
	
	public List<Location> getLocationsById(CreationModel creationModel, String propertyName, PrivacyPolicy model)
	{
		var locations = new ArrayList<Location>();
		var locationIds = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
		if(!locationIds.isPresent())
		{
			return locations;
		}
		if(!(locationIds.get() instanceof List))
		{
			logger.LogErrorMessage("Property" + propertyName + "should be type of List.");
			return locations;
		}
		var list = (List<?>) locationIds.get();
		for(var locationId : list)
		{
			addLocationIfExists(model, locations, (String)locationId);
		}
		return locations;
	}

	private void addLocationIfExists(PrivacyPolicy model, ArrayList<Location> locations, String id) {
		var location = objectFinder.checkIfLocationExists(id, model);
		if(location.isPresent())
		{
			locations.add(location.get());
		}
	}
	
	public List<PolicyStatement> getPolicyStatementsFromObject(CreationModel creationModel, String propertyName, 
			PrivacyPolicy model)
	{
		var policyStatments = new ArrayList<PolicyStatement>();
		if(creationModel.getObject() == null)
		{
			logger.LogErrorMessage("Object is not instantiated.");
			return policyStatments;
		}
		try
		{
			var value = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
			if(!value.isPresent())
			{
				return policyStatments;
			}
			if(!(value.get() instanceof List))
			{
				logger.LogErrorMessage("Property" + propertyName + "should be type of List.");
				return policyStatments;
			}
			var list = (List<?>) value.get();
			for(var policyStatementObject : list)
			{
				PolicyStatementAnnotation policyStatement = policyStatementObject.getClass().getAnnotation(PolicyStatementAnnotation.class);
				if(policyStatement == null)
				{
					logger.LogErrorMessage("There is no policy statement annotation");
				}
				else
				{
					var id = (String)fieldFinder.getFieldValue(policyStatement.id(), policyStatementObject, policyStatementObject.getClass());
					if(id != null)
					{
						addPolicyStatementIfExists(model, policyStatments, id);
					}
				}

			}
			return policyStatments;
		}
		catch(Exception ex)
		{
			logger.LogExceptionMessage(ex);
		}

		return policyStatments;
	}

	public List<PolicyStatement> getPolicyStatementsById(CreationModel creationModel, String propertyName, PrivacyPolicy model)
	{
		var policyStatments = new ArrayList<PolicyStatement>();
		var policyStatmentIds = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
		if(!policyStatmentIds.isPresent())
		{
			return policyStatments;
		}
		if(!(policyStatmentIds.get() instanceof List))
		{
			logger.LogErrorMessage("Property" + propertyName + "should be type of List.");
			return policyStatments;
		}
		var list = (List<?>) policyStatmentIds.get();
		for(var id : list)
		{
			addPolicyStatementIfExists(model, policyStatments, (String)id);
		}
		return policyStatments;
	}
	
	private void addPolicyStatementIfExists(PrivacyPolicy model, ArrayList<PolicyStatement> policyStatments,
			String Id) {
		var policyStatement = objectFinder.checkIfPolicyStatementExists(Id, model);
		if(policyStatement.isPresent())
		{
			policyStatments.add(policyStatement.get());
		}
	}
	
	public List<Document> getDocumentsFromObject(CreationModel creationModel, String propertyName, PrivacyPolicy model)
	{
		var documents = new ArrayList<Document>();
		if(creationModel.getObject() == null)
		{
			logger.LogErrorMessage("Object is not instantiated.");
			return documents;
		}
		try
		{
			var value = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
			if(!value.isPresent())
			{
				return documents;
			}
			if(!(value.get() instanceof List))
			{
				logger.LogErrorMessage("Property" + propertyName + "should be type of List.");
				return documents;
			}
			var list = (List<?>) value.get();
			for(var documentObject : list)
			{
				PaperAnnotation paper = documentObject.getClass().getAnnotation(PaperAnnotation.class);
				if(paper == null)
				{
					logger.LogErrorMessage("There is no paper annotation");
				}
				else
				{
					var id = (String)fieldFinder.getFieldValue(paper.id(), documentObject, documentObject.getClass());
					if(id != null)
					{
						addDocumentIfExists(model, documents, id);
					}
				}

			}
			return documents;
		}
		catch(Exception ex)
		{
			logger.LogExceptionMessage(ex);
		}

		return documents;
	}
	
	public List<Document> getDocumentsById(CreationModel creationModel, String propertyName, PrivacyPolicy model)
	{
		var documents = new ArrayList<Document>();
		var documentIds = fieldFinder.getObjectToReadFrom(creationModel, creationModel.getObjectClass(), creationModel.getObject(), propertyName);
		if(!documentIds.isPresent())
		{
			return documents;
		}
		if(!(documentIds.get() instanceof List))
		{
			logger.LogErrorMessage("Property " + propertyName + " should be type of List.");
			return documents;
		}
		var list = (List<?>) documentIds.get();
		for(var documentId : list)
		{
			addDocumentIfExists(model, documents, (String)documentId);
		}
		return documents;
	}
	
	private void addDocumentIfExists(PrivacyPolicy model, ArrayList<Document> documents, String id) {
		var document = objectFinder.checkIfDocumentExists(id, model);
		if(document.isPresent())
		{
			documents.add(document.get());
		}
	}
}
