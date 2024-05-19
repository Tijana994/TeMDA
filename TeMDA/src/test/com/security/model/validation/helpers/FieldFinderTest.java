package test.com.security.model.validation.helpers;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mockito;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.interfaces.ILogger;

import test.com.security.model.validation.models.TestModel;

public class FieldFinderTest {
	
	private String test = "Tommo";
	
	@Test
	public void getFieldValue_FieldNameIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		
		var result = fieldFinder.getFieldValue(Constants.Empty, getClass(), getClass());
		
		verify(logger).LogErrorMessage("Parameter name is empty");
		assertNull(result);
	}
	
	@Test
	public void getFieldValue_FieldNameIsNull_LoggedException()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		
		var result = fieldFinder.getFieldValue(null, getClass(), getClass());
		
		verify(logger).LogExceptionMessage(any(Exception.class));
		assertNull(result);
	}

	@Test
	public void getFieldValue_FieldNameNotExistsInClass_LoggedException()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		
		var result = fieldFinder.getFieldValue(test, getClass(), getClass());
		
		verify(logger).LogExceptionMessage(any(Exception.class));
		assertNull(result);
	}
	
	@Test
	public void getFieldValue_FieldNameExistsInClass_LoggedException()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var testModel = new TestModel();
		
		var result = fieldFinder.getFieldValue("test", testModel, testModel.getClass());
		
		assertTrue(result.equals("Test"));
	}
}
