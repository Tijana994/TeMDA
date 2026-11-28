package com.security.model.validation.helpers;

import com.security.model.validation.helpers.interfaces.ILogger;

import utility.TemdaLog;

/** Writes the messages of the aspects to the console or to the log file, as set in the TeMDA configuration. */
public class TemdaLogger implements ILogger {

	@Override
	public void LogErrorMessage(String message) {
		TemdaLog.log(message);
	}

	@Override
	public void LogExceptionMessage(Object exception) {
		TemdaLog.log(String.valueOf(exception));
	}

}
