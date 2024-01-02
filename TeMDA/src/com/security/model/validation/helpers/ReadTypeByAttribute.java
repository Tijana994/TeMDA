package com.security.model.validation.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;

import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

import privacyModel.Principal;
import privacyModel.PrivacyData;
import privacyModel.PrivacyPolicy;
import privacyModel.SharedPrivacyData;

public class ReadTypeByAttribute {

	public static Optional<String> getConsentIdFromObject(Class<?> objectClass, Object obj, String consentName, 
			ParametersObjectsLocation parametersLocation, JoinPoint jp)
	{
		if(obj == null)
		{
			System.out.println("Object is not instantiated.");
			return Optional.empty();
		}
		try
		{
			var value = FieldFinder.getObjectToReadFrom(objectClass, obj, parametersLocation, consentName, jp);
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

	public static List<Principal> getPrincipalsById(Object retFromObj, Class<? extends Object> retClass, 
			String propertyName, PrivacyPolicy model)
	{
		var principals = new ArrayList<Principal>();
		var principalsIds = FieldFinder.getFieldValue(propertyName, retFromObj, retClass);
		var list = (List<String>) principalsIds;
		for(var principalId : list)
		{
			var principal = ObjectFinder.checkIfPrincipalExists(principalId, model);
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
		var datasIds = FieldFinder.getFieldValue(propertyName, retFromObj, retClass);
		var list = (List<String>) datasIds;
		for(var dataId : list)
		{
			var data = ObjectFinder.checkIfPrivacyDataExists(dataId, model);
			if(data.isPresent())
			{
				datas.add(data.get());
			}
		}
		return datas;
	}
}