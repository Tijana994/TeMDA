package test.com.security.model.validation.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;
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

public class ReadTypeByAttributeTest {

	private ILogger logger;
	private ReadTypeByAttribute reader;
	private PrivacyPolicy model;

	@Before
	public void setUp() {
		logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		reader = new ReadTypeByAttribute(logger, fieldFinder, new ObjectFinder(logger));
		model = Fixtures.filledModel();
	}

	// ================================================================== get...IdFromObject

	private void assertIdRead(BiFunction<CreationModel, String, Optional<String>> read, Object annotated, String expectedId) {
		var result = read.apply(Fixtures.propertyModel(new Holder(annotated)), "value");
		assertEquals(Optional.of(expectedId), result);
		verify(logger, never()).LogErrorMessage(anyString());
	}

	private void assertNoAnnotation(BiFunction<CreationModel, String, Optional<String>> read) {
		var result = read.apply(Fixtures.propertyModel(new Holder(new Plain())), "value");
		assertTrue(result.isEmpty());
		verify(logger).LogErrorMessage(Mockito.contains("annotation"));
	}

	private void assertEmptyWhenPropertyIsNull(BiFunction<CreationModel, String, Optional<String>> read) {
		var result = read.apply(Fixtures.propertyModel(new Holder(null)), "value");
		assertTrue(result.isEmpty());
	}

	private void assertEmptyWhenObjectIsNull(BiFunction<CreationModel, String, Optional<String>> read) {
		var result = read.apply(Fixtures.propertyModel(null), "value");
		assertTrue(result.isEmpty());
	}

	@Test
	public void getPaperIdFromObject_AnnotatedObject_ReturnsId() {
		assertIdRead(reader::getPaperIdFromObject, new Paper(Fixtures.CONSENT), Fixtures.CONSENT);
	}

	@Test
	public void getPaperIdFromObject_ObjectWithoutAnnotation_LoggedError() {
		assertNoAnnotation(reader::getPaperIdFromObject);
	}

	@Test
	public void getPaperIdFromObject_PropertyIsNull_ReturnsEmpty() {
		assertEmptyWhenPropertyIsNull(reader::getPaperIdFromObject);
	}

	@Test
	public void getPaperIdFromObject_ObjectNotInstantiated_ReturnsEmpty() {
		assertEmptyWhenObjectIsNull(reader::getPaperIdFromObject);
	}

	@Test
	public void getComplaintIdFromObject_AnnotatedObject_ReturnsId() {
		assertIdRead(reader::getComplaintIdFromObject, new Request(Fixtures.COMPLAINT), Fixtures.COMPLAINT);
	}

	@Test
	public void getComplaintIdFromObject_ObjectWithoutAnnotation_LoggedError() {
		assertNoAnnotation(reader::getComplaintIdFromObject);
	}

	@Test
	public void getComplaintIdFromObject_PropertyIsNull_ReturnsEmpty() {
		assertEmptyWhenPropertyIsNull(reader::getComplaintIdFromObject);
	}

	@Test
	public void getComplaintIdFromObject_ObjectNotInstantiated_ReturnsEmpty() {
		assertEmptyWhenObjectIsNull(reader::getComplaintIdFromObject);
	}

	@Test
	public void getPolicyStatementIdFromObject_AnnotatedObject_ReturnsId() {
		assertIdRead(reader::getPolicyStatementIdFromObject, new Statement(Fixtures.STATEMENT), Fixtures.STATEMENT);
	}

	@Test
	public void getPolicyStatementIdFromObject_ObjectWithoutAnnotation_LoggedError() {
		assertNoAnnotation(reader::getPolicyStatementIdFromObject);
	}

	@Test
	public void getPolicyStatementIdFromObject_PropertyIsNull_ReturnsEmpty() {
		assertEmptyWhenPropertyIsNull(reader::getPolicyStatementIdFromObject);
	}

	@Test
	public void getPolicyStatementIdFromObject_ObjectNotInstantiated_ReturnsEmpty() {
		assertEmptyWhenObjectIsNull(reader::getPolicyStatementIdFromObject);
	}

	@Test
	public void getLocationIdFromObject_AnnotatedObject_ReturnsId() {
		assertIdRead(reader::getLocationIdFromObject, new Country(Fixtures.LOCATION), Fixtures.LOCATION);
	}

	@Test
	public void getLocationIdFromObject_ObjectWithoutAnnotation_LoggedError() {
		assertNoAnnotation(reader::getLocationIdFromObject);
	}

	@Test
	public void getLocationIdFromObject_PropertyIsNull_ReturnsEmpty() {
		assertEmptyWhenPropertyIsNull(reader::getLocationIdFromObject);
	}

	@Test
	public void getLocationIdFromObject_ObjectNotInstantiated_ReturnsEmpty() {
		assertEmptyWhenObjectIsNull(reader::getLocationIdFromObject);
	}

	@Test
	public void getPrivacyDataIdFromObject_AnnotatedObject_ReturnsId() {
		assertIdRead(reader::getPrivacyDataIdFromObject, new Data(Fixtures.DATA), Fixtures.DATA);
	}

	@Test
	public void getPrivacyDataIdFromObject_ObjectWithoutAnnotation_LoggedError() {
		assertNoAnnotation(reader::getPrivacyDataIdFromObject);
	}

	@Test
	public void getPrivacyDataIdFromObject_PropertyIsNull_ReturnsEmpty() {
		assertEmptyWhenPropertyIsNull(reader::getPrivacyDataIdFromObject);
	}

	@Test
	public void getPrivacyDataIdFromObject_ObjectNotInstantiated_ReturnsEmpty() {
		assertEmptyWhenObjectIsNull(reader::getPrivacyDataIdFromObject);
	}

	@Test
	public void getPrincipalIdFromObject_AnnotatedObject_ReturnsId() {
		assertIdRead(reader::getPrincipalIdFromObject, new User(Fixtures.PRINCIPAL), Fixtures.PRINCIPAL);
	}

	@Test
	public void getPrincipalIdFromObject_ObjectWithoutAnnotation_LoggedError() {
		assertNoAnnotation(reader::getPrincipalIdFromObject);
	}

	@Test
	public void getPrincipalIdFromObject_PropertyIsNull_ReturnsEmpty() {
		assertEmptyWhenPropertyIsNull(reader::getPrincipalIdFromObject);
	}

	@Test
	public void getPrincipalIdFromObject_ObjectNotInstantiated_ReturnsEmpty() {
		assertEmptyWhenObjectIsNull(reader::getPrincipalIdFromObject);
	}

	// ================================================================== get...sFromObject (lists of annotated objects)

	private interface ListReader {
		List<? extends EObject> read(CreationModel creationModel, String propertyName, PrivacyPolicy model);
	}

	private static List<String> names(List<? extends EObject> objects) {
		return objects.stream().map(o -> ((NamedElement) o).getName()).collect(Collectors.toList());
	}

	/** The list contains an object that exists in the model, one that does not and one without annotation. */
	private void assertListFromObject(ListReader read, Object existing, Object unknown, String expectedName) {
		var holder = new Holder(Arrays.asList(existing, unknown, new Plain()));
		var result = read.read(Fixtures.propertyModel(holder), "value", model);
		assertEquals(Collections.singletonList(expectedName), names(result));
		verify(logger, atLeastOnce()).LogErrorMessage(Mockito.contains("annotation"));
		verify(logger, atLeastOnce()).LogErrorMessage(Mockito.contains(Fixtures.UNKNOWN));
	}

	private void assertListNotAList(ListReader read) {
		var result = read.read(Fixtures.propertyModel(new Holder("not a list")), "value", model);
		assertTrue(result.isEmpty());
		verify(logger).LogErrorMessage(Mockito.contains("should be type of List"));
	}

	private void assertListObjectIsNull(ListReader read) {
		var result = read.read(Fixtures.propertyModel(null), "value", model);
		assertTrue(result.isEmpty());
		verify(logger).LogErrorMessage("Object is not instantiated.");
	}

	private void assertListPropertyIsNull(ListReader read) {
		var result = read.read(Fixtures.propertyModel(new Holder(null)), "value", model);
		assertTrue(result.isEmpty());
	}

	@Test
	public void getPrincipalsFromObject_MixedList_ReturnsOnlyExistingPrincipals() {
		assertListFromObject(reader::getPrincipalsFromObject, new User(Fixtures.PRINCIPAL), new User(Fixtures.UNKNOWN), Fixtures.PRINCIPAL);
	}

	@Test
	public void getPrincipalsFromObject_PropertyIsNotList_LoggedError() {
		assertListNotAList(reader::getPrincipalsFromObject);
	}

	@Test
	public void getPrincipalsFromObject_ObjectNotInstantiated_LoggedError() {
		assertListObjectIsNull(reader::getPrincipalsFromObject);
	}

	@Test
	public void getPrincipalsFromObject_PropertyIsNull_ReturnsEmpty() {
		assertListPropertyIsNull(reader::getPrincipalsFromObject);
	}

	@Test
	public void getPrivacyDatasFromObject_MixedList_ReturnsOnlyExistingData() {
		assertListFromObject(reader::getPrivacyDatasFromObject, new Data(Fixtures.DATA), new Data(Fixtures.UNKNOWN), Fixtures.DATA);
	}

	@Test
	public void getPrivacyDatasFromObject_PropertyIsNotList_LoggedError() {
		assertListNotAList(reader::getPrivacyDatasFromObject);
	}

	@Test
	public void getPrivacyDatasFromObject_ObjectNotInstantiated_LoggedError() {
		assertListObjectIsNull(reader::getPrivacyDatasFromObject);
	}

	@Test
	public void getPrivacyDatasFromObject_PropertyIsNull_ReturnsEmpty() {
		assertListPropertyIsNull(reader::getPrivacyDatasFromObject);
	}

	@Test
	public void getLocationsFromObject_MixedList_ReturnsOnlyExistingLocations() {
		assertListFromObject(reader::getLocationsFromObject, new Country(Fixtures.LOCATION), new Country(Fixtures.UNKNOWN), Fixtures.LOCATION);
	}

	@Test
	public void getLocationsFromObject_PropertyIsNotList_LoggedError() {
		assertListNotAList(reader::getLocationsFromObject);
	}

	@Test
	public void getLocationsFromObject_ObjectNotInstantiated_LoggedError() {
		assertListObjectIsNull(reader::getLocationsFromObject);
	}

	@Test
	public void getLocationsFromObject_PropertyIsNull_ReturnsEmpty() {
		assertListPropertyIsNull(reader::getLocationsFromObject);
	}

	@Test
	public void getPolicyStatementsFromObject_MixedList_ReturnsOnlyExistingStatements() {
		assertListFromObject(reader::getPolicyStatementsFromObject, new Statement(Fixtures.STATEMENT), new Statement(Fixtures.UNKNOWN), Fixtures.STATEMENT);
	}

	@Test
	public void getPolicyStatementsFromObject_PropertyIsNotList_LoggedError() {
		assertListNotAList(reader::getPolicyStatementsFromObject);
	}

	@Test
	public void getPolicyStatementsFromObject_ObjectNotInstantiated_LoggedError() {
		assertListObjectIsNull(reader::getPolicyStatementsFromObject);
	}

	@Test
	public void getPolicyStatementsFromObject_PropertyIsNull_ReturnsEmpty() {
		assertListPropertyIsNull(reader::getPolicyStatementsFromObject);
	}

	@Test
	public void getDocumentsFromObject_MixedList_ReturnsOnlyExistingDocuments() {
		assertListFromObject(reader::getDocumentsFromObject, new Paper(Fixtures.DOCUMENT), new Paper(Fixtures.UNKNOWN), Fixtures.DOCUMENT);
	}

	@Test
	public void getDocumentsFromObject_PropertyIsNotList_LoggedError() {
		assertListNotAList(reader::getDocumentsFromObject);
	}

	@Test
	public void getDocumentsFromObject_ObjectNotInstantiated_LoggedError() {
		assertListObjectIsNull(reader::getDocumentsFromObject);
	}

	@Test
	public void getDocumentsFromObject_PropertyIsNull_ReturnsEmpty() {
		assertListPropertyIsNull(reader::getDocumentsFromObject);
	}

	// ================================================================== get...sById (lists of ids)

	private void assertListById(ListReader read, String existingId, String expectedName) {
		var holder = new Holder(Arrays.asList(existingId, Fixtures.UNKNOWN));
		var result = read.read(Fixtures.propertyModel(holder), "value", model);
		assertEquals(Collections.singletonList(expectedName), names(result));
		verify(logger).LogErrorMessage(Mockito.contains(Fixtures.UNKNOWN));
	}

	@Test
	public void getPrincipalsById_MixedIds_ReturnsOnlyExistingPrincipals() {
		assertListById(reader::getPrincipalsById, Fixtures.PRINCIPAL, Fixtures.PRINCIPAL);
	}

	@Test
	public void getPrincipalsById_PropertyIsNotList_LoggedError() {
		assertListNotAList(reader::getPrincipalsById);
	}

	@Test
	public void getPrincipalsById_PropertyIsNull_ReturnsEmpty() {
		assertListPropertyIsNull(reader::getPrincipalsById);
	}

	@Test
	public void getPrivacyDatasById_MixedIds_ReturnsOnlyExistingData() {
		assertListById(reader::getPrivacyDatasById, Fixtures.DATA, Fixtures.DATA);
	}

	@Test
	public void getPrivacyDatasById_PropertyIsNotList_LoggedError() {
		assertListNotAList(reader::getPrivacyDatasById);
	}

	@Test
	public void getPrivacyDatasById_PropertyIsNull_ReturnsEmpty() {
		assertListPropertyIsNull(reader::getPrivacyDatasById);
	}

	@Test
	public void getLocationsById_MixedIds_ReturnsOnlyExistingLocations() {
		assertListById(reader::getLocationsById, Fixtures.LOCATION, Fixtures.LOCATION);
	}

	@Test
	public void getLocationsById_PropertyIsNotList_LoggedError() {
		assertListNotAList(reader::getLocationsById);
	}

	@Test
	public void getLocationsById_PropertyIsNull_ReturnsEmpty() {
		assertListPropertyIsNull(reader::getLocationsById);
	}

	@Test
	public void getPolicyStatementsById_MixedIds_ReturnsOnlyExistingStatements() {
		assertListById(reader::getPolicyStatementsById, Fixtures.STATEMENT, Fixtures.STATEMENT);
	}

	@Test
	public void getPolicyStatementsById_PropertyIsNotList_LoggedError() {
		assertListNotAList(reader::getPolicyStatementsById);
	}

	@Test
	public void getPolicyStatementsById_PropertyIsNull_ReturnsEmpty() {
		assertListPropertyIsNull(reader::getPolicyStatementsById);
	}

	@Test
	public void getDocumentsById_MixedIds_ReturnsOnlyExistingDocuments() {
		assertListById(reader::getDocumentsById, Fixtures.DOCUMENT, Fixtures.DOCUMENT);
	}

	@Test
	public void getDocumentsById_PropertyIsNotList_LoggedError() {
		assertListNotAList(reader::getDocumentsById);
	}

	@Test
	public void getDocumentsById_PropertyIsNull_ReturnsEmpty() {
		assertListPropertyIsNull(reader::getDocumentsById);
	}

	// ================================================================== getSharedPrivacyDataById

	@Test
	public void getSharedPrivacyDataById_MixedIds_ReturnsOnlyExistingData() {
		var result = reader.getSharedPrivacyDataById(new String[] { Fixtures.SHARED_DATA, Fixtures.UNKNOWN }, model);
		assertEquals(Collections.singletonList(Fixtures.SHARED_DATA), names(result));
		verify(logger).LogErrorMessage(Mockito.contains(Fixtures.UNKNOWN));
	}

	@Test
	public void getSharedPrivacyDataById_NoIds_ReturnsEmpty() {
		var result = reader.getSharedPrivacyDataById(new String[0], model);
		assertTrue(result.isEmpty());
	}
}
