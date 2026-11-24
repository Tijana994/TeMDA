package test.com.security.model.validation.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.helpers.ReadTypeByAttribute;
import com.security.model.validation.helpers.interfaces.ILogger;
import com.security.model.validation.models.CreationModel;

import privacyModel.NamedElement;
import privacyModel.PrivacyPolicy;
import test.com.security.model.validation.models.Fixtures;
import test.com.security.model.validation.models.Fixtures.Country;
import test.com.security.model.validation.models.Fixtures.Data;
import test.com.security.model.validation.models.Fixtures.Holder;
import test.com.security.model.validation.models.Fixtures.Paper;
import test.com.security.model.validation.models.Fixtures.Plain;
import test.com.security.model.validation.models.Fixtures.Request;
import test.com.security.model.validation.models.Fixtures.Statement;
import test.com.security.model.validation.models.Fixtures.User;

/**
 * Every tryGet...FromObject method reads an annotated object, every tryGet...ById method reads
 * an id. Each is checked with an object/id that exists in the model, one that does not and
 * a missing value.
 */
public class ObjectManagerTest {

	private ObjectManager manager;
	private PrivacyPolicy model;

	@Before
	public void setUp() {
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var objectFinder = new ObjectFinder(logger);
		manager = new ObjectManager(fieldFinder, new ReadTypeByAttribute(logger, fieldFinder, objectFinder), objectFinder);
		model = Fixtures.filledModel();
	}

	private interface Getter {
		Optional<? extends NamedElement> get(CreationModel creationModel, String propertyName, PrivacyPolicy model);
	}

	private void assertFound(Getter getter, Object value, String expectedName) {
		var result = getter.get(Fixtures.propertyModel(new Holder(value)), "value", model);
		assertTrue("Expected " + expectedName, result.isPresent());
		assertEquals(expectedName, result.get().getName());
	}

	private void assertNotFound(Getter getter, Object value) {
		var result = getter.get(Fixtures.propertyModel(new Holder(value)), "value", model);
		assertTrue(result.isEmpty());
	}

	/** Found / unknown / no annotation / missing for a FromObject getter. */
	private void checkFromObject(Getter getter, Object existing, Object unknown, String expectedName) {
		assertFound(getter, existing, expectedName);
		assertNotFound(getter, unknown);
		assertNotFound(getter, new Plain());
		assertNotFound(getter, null);
	}

	/** Found / unknown / missing for a ById getter. */
	private void checkById(Getter getter, String existingId) {
		assertFound(getter, existingId, existingId);
		assertNotFound(getter, Fixtures.UNKNOWN);
		assertNotFound(getter, null);
	}

	@Test
	public void tryGetPolicyStatementFromObject() {
		checkFromObject(manager::tryGetPolicyStatementFromObject, new Statement(Fixtures.STATEMENT),
				new Statement(Fixtures.UNKNOWN), Fixtures.STATEMENT);
	}

	@Test
	public void tryGetPolicyStatementById() {
		checkById(manager::tryGetPolicyStatementById, Fixtures.STATEMENT);
	}

	@Test
	public void tryGetPrincipalByFromObject() {
		checkFromObject(manager::tryGetPrincipalByFromObject, new User(Fixtures.PRINCIPAL),
				new User(Fixtures.UNKNOWN), Fixtures.PRINCIPAL);
	}

	@Test
	public void tryGetPrincipalByById() {
		checkById(manager::tryGetPrincipalByById, Fixtures.PRINCIPAL);
	}

	@Test
	public void tryGetPrivacyDataByFromObject() {
		checkFromObject(manager::tryGetPrivacyDataByFromObject, new Data(Fixtures.DATA),
				new Data(Fixtures.UNKNOWN), Fixtures.DATA);
	}

	@Test
	public void tryGetPrivacyDataByById() {
		checkById(manager::tryGetPrivacyDataByById, Fixtures.DATA);
	}

	@Test
	public void tryGetComplaintFromObject() {
		checkFromObject(manager::tryGetComplaintFromObject, new Request(Fixtures.COMPLAINT),
				new Request(Fixtures.UNKNOWN), Fixtures.COMPLAINT);
	}

	@Test
	public void tryGetComplaintById() {
		checkById(manager::tryGetComplaintById, Fixtures.COMPLAINT);
	}

	@Test
	public void tryGetLocationFromObject() {
		checkFromObject(manager::tryGetLocationFromObject, new Country(Fixtures.LOCATION),
				new Country(Fixtures.UNKNOWN), Fixtures.LOCATION);
	}

	@Test
	public void tryGetLocationById() {
		checkById(manager::tryGetLocationById, Fixtures.LOCATION);
	}

	@Test
	public void tryGetConsentFromObject() {
		checkFromObject(manager::tryGetConsentFromObject, new Paper(Fixtures.CONSENT),
				new Paper(Fixtures.UNKNOWN), Fixtures.CONSENT);
	}

	@Test
	public void tryGetConsentById() {
		checkById(manager::tryGetConsentById, Fixtures.CONSENT);
	}

	@Test
	public void tryGetContractFromObject() {
		checkFromObject(manager::tryGetContractFromObject, new Paper(Fixtures.CONTRACT),
				new Paper(Fixtures.UNKNOWN), Fixtures.CONTRACT);
	}

	@Test
	public void tryGetContractById() {
		checkById(manager::tryGetContractById, Fixtures.CONTRACT);
	}

	@Test
	public void tryGetDocumentFromObject() {
		checkFromObject(manager::tryGetDocumentFromObject, new Paper(Fixtures.DOCUMENT),
				new Paper(Fixtures.UNKNOWN), Fixtures.DOCUMENT);
	}

	@Test
	public void tryGetDocumentById() {
		checkById(manager::tryGetDocumentById, Fixtures.DOCUMENT);
	}

	@Test
	public void paperGetters_PaperOfOtherKind_NotFound() {
		// A paper is looked up only among objects of the requested kind.
		assertNotFound(manager::tryGetConsentFromObject, new Paper(Fixtures.CONTRACT));
		assertNotFound(manager::tryGetContractById, Fixtures.DOCUMENT);
		assertNotFound(manager::tryGetDocumentById, Fixtures.CONSENT);
	}
}
