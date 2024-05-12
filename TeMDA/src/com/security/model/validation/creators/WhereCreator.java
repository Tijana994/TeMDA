package com.security.model.validation.creators;

import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.models.CreationModel;

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
	
	public static Where createWhere(CreationModel creationModel, CreatePolicyStatementAnnotation createPolicyStatement, 
			PrivacyModelRepository repo, PrivacyPolicy model) {
		var where = repo.getFactory().createWhere();
		
		if(!createPolicyStatement.whereDestinationId().equals(Constants.Empty))
		{
			var location = ObjectManager.tryGetLocationById(creationModel, createPolicyStatement.whereDestinationId(), model);
			if(location.isPresent())
			{
				where.setDestination(location.get());
			}
		}
		if(!createPolicyStatement.whereDestination().equals(Constants.Empty))
		{
			var location = ObjectManager.tryGetLocationFromObject(creationModel, createPolicyStatement.whereDestination(), model);
			if(location.isPresent())
			{
				where.setDestination(location.get());
			}
		}
		if(!createPolicyStatement.whereSourceId().equals(Constants.Empty))
		{
			var location = ObjectManager.tryGetLocationById(creationModel, createPolicyStatement.whereSourceId(), model);
			if(location.isPresent())
			{
				where.setSource(location.get());
			}
		}
		if(!createPolicyStatement.whereSource().equals(Constants.Empty))
		{
			var location = ObjectManager.tryGetLocationFromObject(creationModel, createPolicyStatement.whereSource(), model);
			if(location.isPresent())
			{
				where.setSource(location.get());
			}
		}
		
		return where;
	}
}
