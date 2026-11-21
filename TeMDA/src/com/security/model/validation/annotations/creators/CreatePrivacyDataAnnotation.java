package com.security.model.validation.annotations.creators;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;

import privacyModel.DataOrigin;
import privacyModel.DataType;

public @interface CreatePrivacyDataAnnotation {
	CreatedObjectLocation createdObjectLocation() default CreatedObjectLocation.Return;
	String name() default Constants.Empty;
	DataType type();
	boolean createSharedPrivacyData() default false;
	DataOrigin origin() default DataOrigin.DATA_SUBJECT;
	ParametersObjectsLocation parametersLocation() default ParametersObjectsLocation.Parameter;
	String propertyObjectName() default Constants.Empty;
	String privacyDataSource() default Constants.Empty;
}
