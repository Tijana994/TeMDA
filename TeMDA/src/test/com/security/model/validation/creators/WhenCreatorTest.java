package test.com.security.model.validation.creators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.security.model.validation.annotations.TimeStatementAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.creators.WhenCreator;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.interfaces.ILogger;
import com.security.model.validation.models.CreationModel;
import com.security.model.validation.models.ParametersAnnotations;

import privacyModel.PrivacyModelFactory;
import privacyModel.TimeInterval;
import privacyModel.TimePreposition;
import privacyModel.TimeStatement;
import test.com.security.model.validation.models.Fixtures;
import test.com.security.model.validation.models.Fixtures.Holder;
import test.com.security.model.validation.models.Fixtures.Period;

/**
 * WhenCreator turns one date ("start") into a time statement and two dates ("start,end")
 * into a time interval. The preposition comes from @TimeStatementAnnotation, found on the
 * method parameter or on the field, depending on where the dates are read from.
 */
public class WhenCreatorTest {

	private static final Date START = new Date(1_700_000_000_000L);
	private static final Date END = new Date(1_710_000_000_000L);
	private static final String[] PARAMETER_NAMES = { "start", "end", "notAnnotated" };

	/** A method like the ones TeMDA intercepts; only its parameter annotations are used. */
	@SuppressWarnings("unused")
	private static void schedule(@TimeStatementAnnotation(preposition = TimePreposition.FROM) Date start,
			@TimeStatementAnnotation(preposition = TimePreposition.TO) Date end, Date notAnnotated) {
	}

	private ILogger logger;
	private FieldFinder fieldFinder;
	private ParametersAnnotations parameters;

	@Before
	public void setUp() throws Exception {
		logger = Mockito.mock(ILogger.class);
		fieldFinder = new FieldFinder(logger);
		var method = WhenCreatorTest.class.getDeclaredMethod("schedule", Date.class, Date.class, Date.class);
		parameters = new ParametersAnnotations(method.getParameterAnnotations(), PARAMETER_NAMES);
	}

	private Object create(CreationModel creationModel, String when) {
		return WhenCreator.createWhen(creationModel, when, PrivacyModelFactory.eINSTANCE, parameters, fieldFinder, logger);
	}

	private static void assertStatement(Object result, TimePreposition preposition, Date date) {
		assertTrue("Expected a time statement but was " + result, result instanceof TimeStatement);
		var statement = (TimeStatement) result;
		assertEquals(preposition, statement.getPreposition());
		assertEquals(date, statement.getDateTime());
	}

	private static void assertInterval(Object result) {
		assertTrue("Expected a time interval but was " + result, result instanceof TimeInterval);
		var interval = (TimeInterval) result;
		assertStatement(interval.getStart(), TimePreposition.FROM, START);
		assertStatement(interval.getEnd(), TimePreposition.TO, END);
	}

	// ------------------------------------------------------------------ dates passed as method parameters

	private CreationModel parameterModel(Object... args) {
		var joinPoint = Fixtures.joinPoint(PARAMETER_NAMES, args);
		return new CreationModel(null, joinPoint, CreatedObjectLocation.Return, ParametersObjectsLocation.Parameter);
	}

	@Test
	public void createWhen_OneParameter_CreatesTimeStatement() {
		assertStatement(create(parameterModel(START, END, START), "start"), TimePreposition.FROM, START);
	}

	@Test
	public void createWhen_TwoParameters_CreatesTimeInterval() {
		assertInterval(create(parameterModel(START, END, START), "start,end"));
	}

	@Test
	public void createWhen_ParameterNameIgnoresCase() {
		// Both the parameter value and its annotation are found regardless of case.
		assertStatement(create(parameterModel(START, END, START), "END"), TimePreposition.TO, END);
		assertInterval(create(parameterModel(START, END, START), "Start,End"));
	}

	@Test
	public void createWhen_ParameterWithoutAnnotation_ReturnsNull() {
		assertNull(create(parameterModel(START, END, START), "notAnnotated"));
	}

	@Test
	public void createWhen_ParameterIsNull_LoggedError() {
		assertNull(create(parameterModel(null, END, START), "start"));
		verify(logger).LogErrorMessage("When object is not instantiated.");
	}

	@Test
	public void createWhen_ParameterIsNotDate_LoggedError() {
		assertNull(create(parameterModel("2024-01-01", END, START), "start"));
		verify(logger).LogErrorMessage("When object is not instantiated.");
	}

	@Test
	public void createWhen_UnknownParameter_LoggedError() {
		assertNull(create(parameterModel(START, END, START), "missing"));
		verify(logger).LogErrorMessage("Parameter missing does not exist.");
	}

	@Test
	public void createWhen_IntervalWithInvalidEnd_IntervalHasNoEnd() {
		var result = create(parameterModel(START, null, START), "start,end");
		assertTrue(result instanceof TimeInterval);
		assertNotNull(((TimeInterval) result).getStart());
		assertNull(((TimeInterval) result).getEnd());
	}

	// ------------------------------------------------------------------ dates as fields of the returned object

	@Test
	public void createWhen_PropertyInReturnedObject_OneDate() {
		var model = new CreationModel(new Period(START, END), null, CreatedObjectLocation.Return,
				ParametersObjectsLocation.PropertyInReturnedObject);
		assertStatement(create(model, "start"), TimePreposition.FROM, START);
	}

	@Test
	public void createWhen_PropertyInReturnedObject_TwoDates() {
		var model = new CreationModel(new Period(START, END), null, CreatedObjectLocation.Return,
				ParametersObjectsLocation.PropertyInReturnedObject);
		assertInterval(create(model, "start,end"));
	}

	@Test
	public void createWhen_PropertyInReturnedObjectWithoutAnnotation_ReturnsNull() {
		var model = new CreationModel(new Period(START, END), null, CreatedObjectLocation.Return,
				ParametersObjectsLocation.PropertyInReturnedObject);
		assertNull(create(model, "notAnnotated"));
	}

	// ------------------------------------------------------------------ dates as fields of a parameter object

	private CreationModel parameterObjectModel() {
		var joinPoint = Fixtures.joinPoint(new String[] { "period" }, new Period(START, END));
		return new CreationModel(null, new Holder(null), joinPoint, CreatedObjectLocation.Return,
				ParametersObjectsLocation.PropertyInParameterObject, "period");
	}

	@Test
	public void createWhen_PropertyInParameterObject_OneDate() {
		assertStatement(create(parameterObjectModel(), "end"), TimePreposition.TO, END);
	}

	@Test
	public void createWhen_PropertyInParameterObject_TwoDates() {
		assertInterval(create(parameterObjectModel(), "start,end"));
	}

	// ------------------------------------------------------------------ dates as fields of the intercepted object

	@Test
	public void createWhen_Property_OneDate() {
		assertStatement(create(Fixtures.propertyModel(new Period(START, END)), "start"), TimePreposition.FROM, START);
	}

	@Test
	public void createWhen_Property_TwoDates() {
		assertInterval(create(Fixtures.propertyModel(new Period(START, END)), "start,end"));
	}

	@Test
	public void createWhen_PropertyWithoutAnnotation_ReturnsNull() {
		assertNull(create(Fixtures.propertyModel(new Period(START, END)), "notAnnotated"));
	}

	@Test
	public void createWhen_PropertyInSuperclass_AnnotationFound() {
		assertStatement(create(Fixtures.propertyModel(new LongPeriod(START, END)), "end"), TimePreposition.TO, END);
	}

	@Test
	public void createWhen_Property_ModelCreatedAsInPolicyStatementAspect() {
		// CreatePolicyStatementAspect passes the intercepted object to the CreationModel constructor.
		var model = new CreationModel(null, new Period(START, END), null, CreatedObjectLocation.Return,
				ParametersObjectsLocation.Property, Constants.Empty);
		assertInterval(create(model, "start,end"));
	}

	public static class LongPeriod extends Period {
		public LongPeriod(Date start, Date end) { super(start, end); }
	}

	@Test
	public void createWhen_EmptyPropertyName_LoggedError() {
		var model = Fixtures.propertyModel(new Holder(START));
		assertNull(create(model, Constants.Empty));
		verify(logger).LogErrorMessage("Property name is empty");
	}
}
