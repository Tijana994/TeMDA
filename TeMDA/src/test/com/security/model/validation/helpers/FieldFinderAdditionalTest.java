package test.com.security.model.validation.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.interfaces.ILogger;
import com.security.model.validation.models.CreationModel;

import test.com.security.model.validation.models.Fixtures;
import test.com.security.model.validation.models.Fixtures.Holder;
import test.com.security.model.validation.models.Fixtures.User;

/** Branches of FieldFinder that FieldFinderTest does not cover. */
public class FieldFinderAdditionalTest {

	public static class Admin extends User {
		public String role = "admin";
		public Admin(String name) { super(name); }
	}

	private ILogger logger;
	private FieldFinder fieldFinder;

	@Before
	public void setUp() {
		logger = Mockito.mock(ILogger.class);
		fieldFinder = new FieldFinder(logger);
	}

	// ------------------------------------------------------------------ getFieldValue

	@Test
	public void getFieldValue_FieldInSuperclass_ReturnsValue() {
		assertEquals("Ana", fieldFinder.getFieldValue("name", new Admin("Ana"), Admin.class));
	}

	@Test
	public void getFieldValue_ObjectOfOtherClass_LoggedException() {
		var result = fieldFinder.getFieldValue("name", "not a user", User.class);
		assertNull(result);
		verify(logger).LogExceptionMessage(any());
	}

	// ------------------------------------------------------------------ getCreatedObjectToReadFrom

	@Test
	public void getCreatedObjectToReadFrom_PropertyWithExistingName_ReturnsValue() {
		var model = new CreationModel(null, null, CreatedObjectLocation.Property, ParametersObjectsLocation.Property);
		var user = new User("Ana");
		assertSame(user, fieldFinder.getCreatedObjectToReadFrom(model, new Holder(user), "value"));
	}

	@Test
	public void getCreatedObjectToReadFrom_ParameterWithExistingName_ReturnsArgumentIgnoringCase() {
		var user = new User("Ana");
		var joinPoint = Fixtures.joinPoint(new String[] { "id", "user" }, "1", user);
		var model = new CreationModel(null, joinPoint, CreatedObjectLocation.Parameter, ParametersObjectsLocation.Parameter);
		assertSame(user, fieldFinder.getCreatedObjectToReadFrom(model, null, "USER"));
	}

	@Test
	public void getCreatedObjectToReadFrom_ParameterWithNonExistingName_ReturnsNull() {
		var joinPoint = Fixtures.joinPoint(new String[] { "id" }, "1");
		var model = new CreationModel(null, joinPoint, CreatedObjectLocation.Parameter, ParametersObjectsLocation.Parameter);
		assertNull(fieldFinder.getCreatedObjectToReadFrom(model, null, "user"));
	}

	// ------------------------------------------------------------------ getObjectToReadFrom: Property

	@Test
	public void getObjectToReadFrom_PropertyIsNull_LoggedError() {
		var holder = new Holder(null);
		var result = fieldFinder.getObjectToReadFrom(Fixtures.propertyModel(holder), Holder.class, holder, "value");
		assertTrue(result.isEmpty());
		verify(logger).LogErrorMessage("Property value should be instatiate");
	}

	// ------------------------------------------------------------------ getObjectToReadFrom: Parameter

	private CreationModel parameterModel(Object... args) {
		var joinPoint = Fixtures.joinPoint(new String[] { "id", "user" }, args);
		return new CreationModel(null, joinPoint, CreatedObjectLocation.Return, ParametersObjectsLocation.Parameter);
	}

	@Test
	public void getObjectToReadFrom_ParameterWithExistingName_ReturnsArgument() {
		var user = new User("Ana");
		assertEquals(Optional.of(user), fieldFinder.getObjectToReadFrom(parameterModel("1", user), null, null, "user"));
	}

	@Test
	public void getObjectToReadFrom_ParameterNameIgnoresCase() {
		assertEquals(Optional.of("1"), fieldFinder.getObjectToReadFrom(parameterModel("1", null), null, null, "ID"));
	}

	@Test
	public void getObjectToReadFrom_ParameterIsNull_LoggedError() {
		var result = fieldFinder.getObjectToReadFrom(parameterModel("1", null), null, null, "user");
		assertTrue(result.isEmpty());
		verify(logger).LogErrorMessage("Parameter user should be instatiate");
	}

	@Test
	public void getObjectToReadFrom_ParameterDoesNotExist_LoggedError() {
		var result = fieldFinder.getObjectToReadFrom(parameterModel("1", null), null, null, "missing");
		assertTrue(result.isEmpty());
		verify(logger).LogErrorMessage("Parameter missing does not exist.");
	}

	// ------------------------------------------------------------------ getObjectToReadFrom: PropertyInReturnedObject

	@Test
	public void getObjectToReadFrom_PropertyInReturnedObjectWithValue_ReturnsValue() {
		var model = new CreationModel(new User("Ana"), null, CreatedObjectLocation.Return,
				ParametersObjectsLocation.PropertyInReturnedObject);
		assertEquals(Optional.of("Ana"), fieldFinder.getObjectToReadFrom(model, null, null, "name"));
	}

	// ------------------------------------------------------------------ getObjectToReadFrom: PropertyInParameterObject

	private CreationModel parameterObjectModel(User user) {
		var joinPoint = Fixtures.joinPoint(new String[] { "user" }, user);
		return new CreationModel(null, new Holder(null), joinPoint, CreatedObjectLocation.Return,
				ParametersObjectsLocation.PropertyInParameterObject, "user");
	}

	@Test
	public void getObjectToReadFrom_PropertyInParameterObjectWithValue_ReturnsValue() {
		assertEquals(Optional.of("Ana"), fieldFinder.getObjectToReadFrom(parameterObjectModel(new User("Ana")), null, null, "name"));
	}

	@Test
	public void getObjectToReadFrom_PropertyInParameterObjectIsNull_LoggedError() {
		var result = fieldFinder.getObjectToReadFrom(parameterObjectModel(new User(null)), null, null, "name");
		assertTrue(result.isEmpty());
		verify(logger).LogErrorMessage("Property name should be instatiate");
	}

	@Test
	public void getObjectToReadFrom_PropertyInParameterObjectWithEmptyName_LoggedError() {
		var result = fieldFinder.getObjectToReadFrom(parameterObjectModel(new User("Ana")), null, null, Constants.Empty);
		assertTrue(result.isEmpty());
		verify(logger).LogErrorMessage("Property name is empty");
	}
}
