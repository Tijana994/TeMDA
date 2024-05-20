package test.com.security.model.validation.helpers;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;
import org.mockito.Mockito;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.interfaces.ILogger;
import com.security.model.validation.models.CreationModel;

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
	
	@Test
	public void getCreatedObjectToReadFrom_ReturnObject_ReturnsReturnObject()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var testModel = new TestModel();
		var resultFromMethod = testModel.Test();
		var creationModel = new CreationModel(resultFromMethod, mock(ProceedingJoinPoint.class), CreatedObjectLocation.Return, ParametersObjectsLocation.Parameter);
		
		var result = fieldFinder.getCreatedObjectToReadFrom(creationModel, testModel, "");
		
		assertTrue(result.equals(resultFromMethod));
	}
	
	@Test
	public void getCreatedObjectToReadFrom_PropertyWithEmptyName_ReturnsNull()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var testModel = new TestModel();
		var resultFromMethod = testModel.Test();
		var creationModel = new CreationModel(resultFromMethod, mock(ProceedingJoinPoint.class), CreatedObjectLocation.Property, ParametersObjectsLocation.Parameter);
		
		var result = fieldFinder.getCreatedObjectToReadFrom(creationModel, testModel, Constants.Empty);
		
		verify(logger).LogErrorMessage("Property name is empty");
		assertNull(result);
	}
	
	@Test
	public void getCreatedObjectToReadFrom_PropertyWithNonExistingName_ReturnsNull()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var testModel = new TestModel();
		var resultFromMethod = testModel.Test();
		var creationModel = new CreationModel(resultFromMethod, mock(ProceedingJoinPoint.class), CreatedObjectLocation.Property, ParametersObjectsLocation.Parameter);
		
		var result = fieldFinder.getCreatedObjectToReadFrom(creationModel, testModel, test);
		
		verify(logger).LogExceptionMessage(any(Exception.class));
		assertNull(result);
	}
	
	@Test
	public void getCreatedObjectToReadFrom_ParameterWithEmptyName_ReturnsNull()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var testModel = new TestModel();
		var resultFromMethod = testModel.Test();
		var creationModel = new CreationModel(resultFromMethod, mock(ProceedingJoinPoint.class), CreatedObjectLocation.Parameter, ParametersObjectsLocation.Parameter);
		
		var result = fieldFinder.getCreatedObjectToReadFrom(creationModel, testModel, Constants.Empty);
		
		verify(logger).LogErrorMessage("Parameter name is empty");
		assertNull(result);
	}
	
	@Test
	public void getObjectToReadFrom_PropertyWithEmptyName_ReturnsNull()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var testModel = new TestModel();
		var resultFromMethod = testModel.Test();
		var creationModel = new CreationModel(resultFromMethod, mock(ProceedingJoinPoint.class), CreatedObjectLocation.Parameter, ParametersObjectsLocation.Property);
		
		var result = fieldFinder.getObjectToReadFrom(creationModel, testModel.getClass(), testModel, Constants.Empty);
		
		verify(logger).LogErrorMessage("Property name is empty");
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void getObjectToReadFrom_PropertyWithNonExistingName_ReturnsNull()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var testModel = new TestModel();
		var resultFromMethod = testModel.Test();
		var creationModel = new CreationModel(resultFromMethod, mock(ProceedingJoinPoint.class), CreatedObjectLocation.Parameter, ParametersObjectsLocation.Property);
		
		var result = fieldFinder.getObjectToReadFrom(creationModel, testModel.getClass(), testModel, test);
		
		verify(logger).LogErrorMessage("Property " + test + " should be instatiate");
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void getObjectToReadFrom_PropertyWithExistingName_ReturnsObject()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var testModel = new TestModel();
		var resultFromMethod = testModel.Test();
		var creationModel = new CreationModel(resultFromMethod, mock(ProceedingJoinPoint.class), CreatedObjectLocation.Parameter, ParametersObjectsLocation.Property);
		
		var result = fieldFinder.getObjectToReadFrom(creationModel, testModel.getClass(), testModel, "test");
		
		verify(logger, never()).LogErrorMessage(anyString());
		assertTrue(resultFromMethod.equals(result.get()));
	}
	
	@Test
	public void getObjectToReadFrom_ParameterWithEmptyName_ReturnsNull()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var testModel = new TestModel();
		var resultFromMethod = testModel.Test();
		var creationModel = new CreationModel(resultFromMethod, mock(ProceedingJoinPoint.class), CreatedObjectLocation.Parameter, ParametersObjectsLocation.Parameter);
		
		var result = fieldFinder.getObjectToReadFrom(creationModel, testModel.getClass(), testModel, Constants.Empty);
		
		verify(logger).LogErrorMessage("Parameter name is empty");
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void getObjectToReadFrom_PropertyInReturnedObjectWithEmptyName_ReturnsNull()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var testModel = new TestModel();
		var resultFromMethod = testModel.Test();
		var creationModel = new CreationModel(resultFromMethod, mock(ProceedingJoinPoint.class), CreatedObjectLocation.Parameter, ParametersObjectsLocation.PropertyInReturnedObject);
		
		var result = fieldFinder.getObjectToReadFrom(creationModel, testModel.getClass(), testModel, Constants.Empty);
		
		verify(logger).LogErrorMessage("Property name is empty");
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void getObjectToReadFrom_PropertyInReturnedObjectWithNonExistingName_ReturnsNull()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var testModel = new TestModel();
		var creationModel = new CreationModel(testModel, mock(ProceedingJoinPoint.class), CreatedObjectLocation.Parameter, ParametersObjectsLocation.PropertyInReturnedObject);
		
		var result = fieldFinder.getObjectToReadFrom(creationModel, testModel.getClass(), testModel, test);
		
		verify(logger).LogErrorMessage("Property " + test + " should be instatiate");
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void getObjectToReadFrom_PropertyInReturnedObjectWithExistingName_ReturnsNull()
	{
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var testModel = new TestModel();
		var creationModel = new CreationModel(testModel, mock(ProceedingJoinPoint.class), CreatedObjectLocation.Parameter, ParametersObjectsLocation.PropertyInReturnedObject);
		
		var result = fieldFinder.getObjectToReadFrom(creationModel, testModel.getClass(), testModel, "test");
		
		verify(logger, never()).LogErrorMessage(anyString());
		assertTrue(testModel.Test().equals(result.get()));
	}
}
