package test.com.security.model.validation.helpers;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.security.model.validation.helpers.ConsoleLogger;

public class ConsoleLoggerTest {

	private final PrintStream originalOut = System.out;
	private final ByteArrayOutputStream output = new ByteArrayOutputStream();

	@Before
	public void captureOutput() {
		System.setOut(new PrintStream(output, true));
	}

	@After
	public void restoreOutput() {
		System.setOut(originalOut);
	}

	@Test
	public void LogErrorMessage_PrintsMessage() {
		new ConsoleLogger().LogErrorMessage("Something went wrong");
		assertEquals("Something went wrong", output.toString().trim());
	}

	@Test
	public void LogExceptionMessage_PrintsException() {
		new ConsoleLogger().LogExceptionMessage(new IllegalStateException("broken"));
		assertEquals("java.lang.IllegalStateException: broken", output.toString().trim());
	}
}
