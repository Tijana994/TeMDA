package test.com.security.model.validation.helpers;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;

import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.interfaces.ILogger;

import test.com.security.model.validation.models.TestUtility;

public class ObjectFinderTest {
	
	private String invalidId = "Tommo";
	
	@Test
	public void checkIfPrincipalExists_FieldIdIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfPrincipalExists(null, null);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfPrincipalExists_ListIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfPrincipalExists(invalidId, TestUtility.createPrivacyModel());
		
		assertTrue(result.isEmpty());
	}

	@Test
	public void checkIfLocationExists_FieldIdIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfLocationExists(null, null);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfLocationExists_ListIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfLocationExists(invalidId, TestUtility.createPrivacyModel());
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfConsentExists_FieldIdIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfConsentExists(null, null);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfConsentExists_ListIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfConsentExists(invalidId, TestUtility.createPrivacyModel());
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfPolicyStatementExists_FieldIdIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfPolicyStatementExists(null, null);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfPolicyStatementExists_ListIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfPolicyStatementExists(invalidId, TestUtility.createPrivacyModel());
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfSharedPrivacyDataExists_FieldIdIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfSharedPrivacyDataExists(null, null);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfSharedPrivacyDataExists_ListIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfSharedPrivacyDataExists(invalidId, TestUtility.createPrivacyModel());
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfPrivacyDataExists_FieldIdIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfPrivacyDataExists(null, null);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfPrivacyDataExists_ListIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfPrivacyDataExists(invalidId, TestUtility.createPrivacyModel());
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfComplaintExists_FieldIdIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfComplaintExists(null, null);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfComplaintExists_ListIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfComplaintExists(invalidId, TestUtility.createPrivacyModel());
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfDocumentExists_FieldIdIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfComplaintExists(null, null);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfDocumentExists_ListIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfComplaintExists(invalidId, TestUtility.createPrivacyModel());
		
		assertTrue(result.isEmpty());
	}
}
