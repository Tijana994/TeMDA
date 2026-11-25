package com.security.model.validation.creators;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.security.model.validation.annotations.PurposeAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.interfaces.ILogger;
import com.security.model.validation.models.CreationModel;

import privacyModel.PrivacyModelFactory;
import privacyModel.ProcessingReason;
import privacyModel.ProcessingReasonSubtype;
import privacyModel.Purpose;

public class PurposeCreator {
	
    // Mapping enum to enum id
    private static final Map<Integer, ProcessingReason> _reasons = new HashMap<Integer, ProcessingReason>();
    static
    {
        for (ProcessingReason reason : ProcessingReason.values())
        	_reasons.put(reason.getValue(), reason);
    }
    private static final Map<Integer, ProcessingReasonSubtype> _subtypes = new HashMap<Integer, ProcessingReasonSubtype>();
    static
    {
        for (ProcessingReasonSubtype reason : ProcessingReasonSubtype.values())
        	_subtypes.put(reason.getValue(), reason);
    }

	public static Purpose createPurpose(CreationModel creationModel, Class<?> originalObjectClass, Object originalObject,
			String propertyName, PrivacyModelFactory factory, FieldFinder fieldFinder, ILogger logger) {
		if(originalObject == null)
		{
			logger.LogErrorMessage("Object is not instantiated.");
			return null;
		}
		try
		{
			var purposeObject = fieldFinder.getObjectToReadFrom(creationModel, originalObjectClass, originalObject, propertyName);
			if(purposeObject.isEmpty())
			{
				return null;
			}
			PurposeAnnotation purposeAnnotation = purposeObject.get().getClass().getAnnotation(PurposeAnnotation.class);
			if(purposeAnnotation == null)
			{
				logger.LogErrorMessage("There is no purpose annotation");
				return null;
			}
			else
			{
				return readComplexType(purposeObject.get(), purposeAnnotation, factory, fieldFinder, logger);
			}
		}
		catch(Exception e)
		{
			System.out.println("Field with name " + propertyName + " in purpose attribute caused an exception " + e);
		}
		return null;
	}

	private static Purpose readComplexType(Object originalObject, 
			PurposeAnnotation purposeAnnotation, PrivacyModelFactory factory, FieldFinder fieldFinder, ILogger logger)
			throws NoSuchFieldException, IllegalAccessException {
		String details = (String)fieldFinder.getFieldValue(purposeAnnotation.details(), originalObject, originalObject.getClass());
		var purposeObject = factory.createPurpose();
		purposeObject.setDetails(details);
		int reason = (int)fieldFinder.getFieldValue(purposeAnnotation.reason(), originalObject, originalObject.getClass());
		purposeObject.setProcessingReason(_reasons.getOrDefault(reason, null));
		int reasonSubtype = (int)fieldFinder.getFieldValue(purposeAnnotation.reasonSubtype(), originalObject, originalObject.getClass());
		purposeObject.setProcessingReasonSubtype(_subtypes.getOrDefault(reasonSubtype, null));
		if(purposeAnnotation.subPurposes().equals(Constants.Empty)) return purposeObject;
		
		Field sub = originalObject.getClass().getDeclaredField(purposeAnnotation.subPurposes());
		if(!sub.getType().equals(List.class)) 
		{
			logger.LogErrorMessage("Subpurpose should be a list type.");
			return purposeObject;
		}

		Object value = sub.get(originalObject);
		if(value == null)
		{
			return purposeObject;
		}
		var list = (List<?>) value;
		
		for(var purpose : list)
		{
			PurposeAnnotation purposeAnnotation1 = purpose.getClass().getAnnotation(PurposeAnnotation.class);
			var subPurposeObject = readComplexType(purpose, purposeAnnotation1, factory, fieldFinder, logger);
			purposeObject.getSubPurposes().add(subPurposeObject);
		}
		return purposeObject;
	}
}
