package test.com.security.model.validation.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.security.model.validation.helpers.TemdaLogger;

import utility.LogOutput;
import utility.ProjectConfiguration.Configuration;

/** The logger of the aspects follows the log output set in the TeMDA configuration. */
public class TemdaLoggerTest {

	private Path file;

	@After
	public void tearDown() throws IOException {
		Configuration.setLogOutput(LogOutput.CONSOLE);
		Configuration.setLogFilePath("temda-validation.log");
		if (file != null) {
			Files.deleteIfExists(file);
		}
	}

	@Test
	public void errorAndExceptionMessagesAreWrittenToTheLogFile() throws IOException {
		file = Files.createTempFile("temda-logger", ".log");
		Configuration.setLogFilePath(file.toString());
		Configuration.setLogOutput(LogOutput.FILE);

		var logger = new TemdaLogger();
		logger.LogErrorMessage("There is no paper annotation");
		logger.LogExceptionMessage(new IllegalStateException("broken"));

		List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
		assertEquals(2, lines.size());
		assertTrue(lines.get(0).endsWith("] There is no paper annotation"));
		assertTrue(lines.get(1).endsWith("] java.lang.IllegalStateException: broken"));
	}
}
