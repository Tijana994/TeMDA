package com.security.model.validation.helpers.interfaces;

public interface ILogger {
	
	public void LogErrorMessage(String message);
	
	public void LogExceptionMessage(Object exception);
}
