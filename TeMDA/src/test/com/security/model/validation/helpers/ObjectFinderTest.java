package test.com.security.model.validation.helpers;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;

import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.interfaces.ILogger;

public class ObjectFinderTest {
	
	@Test
	public void checkIfPrincipalExists_FieldIdIsEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfPrincipalExists(null, null);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void checkIfPrincipalExists_PrincipalsListISEmpty_LoggedError()
	{
		var logger = Mockito.mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		
		var result = objectFinder.checkIfPrincipalExists("test", );
		
		assertTrue(result.isEmpty());
	}

}
