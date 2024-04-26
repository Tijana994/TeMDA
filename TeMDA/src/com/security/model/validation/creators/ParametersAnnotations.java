package com.security.model.validation.creators;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

public class ParametersAnnotations {

	private Annotation[][] annotations;
	private String[] parameterNames;
	
	public ParametersAnnotations(Annotation[][] annotations, String[] parameterNames)
	{
		this.annotations = annotations;
		this.parameterNames = parameterNames;
	}

	public Annotation[][] getAnnotations() {
		return annotations;
	}

	public String[] getParameterNames() {
		return parameterNames;
	}
}
