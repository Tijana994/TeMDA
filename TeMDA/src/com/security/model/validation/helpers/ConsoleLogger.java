package com.security.model.validation.helpers;

import com.security.model.validation.helpers.interfaces.ILogger;

public class ConsoleLogger implements ILogger {

	@Override
	public void LogErrorMessage(String message) {
		System.out.println(message);
	}

	@Override
	public void LogExceptionMessage(Object exception) {
		System.out.println(exception);
	}

}
