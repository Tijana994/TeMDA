package com.security.model.validation.creators;

import org.aspectj.lang.JoinPoint;

import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.ObjectManager;

import privacyModel.PrivacyPolicy;
import privacyModel.Where;
import utility.PrivacyModelRepository;

public class WhereCreator {

	public static Boolean shouldCreate(CreatePolicyStatementAnnotation createPolicyStatement)
	{
		return !createPolicyStatement.whereDestinationId().equals(Constants.Empty) ||
				!createPolicyStatement.whereDestination().equals(Constants.Empty) ||
				!createPolicyStatement.whereSourceId().equals(Constants.Empty) ||
				!createPolicyStatement.whereSource().equals(Constants.Empty);
	}
	
	public static Where createWhere(CreatePolicyStatementAnnotation createPolicyStatement, Object originalObject,
			Class<? extends Object> originalObjectClass, PrivacyModelRepository repo, PrivacyPolicy model, 
			JoinPoint jp) {
		var where = repo.getFactory().createWhere();
		
		if(!createPolicyStatement.whereDestinationId().equals(Constants.Empty))
		{
			var location = ObjectManager.tryGetLocationById(originalObject, originalObjectClass, createPolicyStatement.whereDestinationId(), model, 
					createPolicyStatement.parametersLocation(), jp);
			if(location.isPresent())
			{
				where.setDestination(location.get());
			}
		}
		if(!createPolicyStatement.whereDestination().equals(Constants.Empty))
		{
			var location = ObjectManager.tryGetLocationFromObject(originalObject, originalObjectClass, createPolicyStatement.whereDestination(), model, 
					createPolicyStatement.parametersLocation(), jp);
			if(location.isPresent())
			{
				where.setDestination(location.get());
			}
		}
		if(!createPolicyStatement.whereSourceId().equals(Constants.Empty))
		{
			var location = ObjectManager.tryGetLocationById(originalObject, originalObjectClass, createPolicyStatement.whereSourceId(), model, 
					createPolicyStatement.parametersLocation(), jp);
			if(location.isPresent())
			{
				where.setSource(location.get());
			}
		}
		if(!createPolicyStatement.whereSource().equals(Constants.Empty))
		{
			var location = ObjectManager.tryGetLocationFromObject(originalObject, originalObjectClass, createPolicyStatement.whereSource(), model, 
					createPolicyStatement.parametersLocation(), jp);
			if(location.isPresent())
			{
				where.setSource(location.get());
			}
		}
		
		return where;
	}
}
