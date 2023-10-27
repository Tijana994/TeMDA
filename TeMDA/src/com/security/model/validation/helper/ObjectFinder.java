package com.security.model.validation.helper;

import java.util.Optional;

import privacyModel.Location;
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
}
