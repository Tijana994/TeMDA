package test.com.security.model.validation.creators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.creators.HowCreator;
import com.security.model.validation.creators.WhatCreator;
import com.security.model.validation.creators.WhereCreator;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.helpers.ReadTypeByAttribute;
import com.security.model.validation.helpers.interfaces.ILogger;

import privacyModel.Action;
import privacyModel.NamedElement;
import privacyModel.PrivacyModelFactory;
import privacyModel.PrivacyPolicy;
import test.com.security.model.validation.models.Fixtures;
import test.com.security.model.validation.models.Fixtures.Country;
import test.com.security.model.validation.models.Fixtures.Paper;
import utility.PrivacyModelRepository;

/** WhatCreator, WhereCreator and HowCreator build the what/where/how parts of a policy statement. */
public class PolicyStatementPartsCreatorTest {

	private static final String SECOND_DOCUMENT = "Legal provision";

	/** The object TeMDA reads values from (e.g. a service class), with every kind of value. */
	public static class Service {
		public String destinationId = Fixtures.LOCATION;
		public Country source = new Country(Fixtures.LOCATION);
		public String unknownId = Fixtures.UNKNOWN;
		public String consentId = Fixtures.CONSENT;
		public Paper consent = new Paper(Fixtures.CONSENT);
		public String contractId = Fixtures.CONTRACT;
		public Paper contract = new Paper(Fixtures.CONTRACT);
		public String documentId = SECOND_DOCUMENT;
		public Paper document = new Paper(SECOND_DOCUMENT);
		public List<String> documentIds = Arrays.asList(Fixtures.DOCUMENT, Fixtures.UNKNOWN);
		public List<Paper> documents = Collections.singletonList(new Paper(Fixtures.DOCUMENT));
	}

	// Methods only used for their annotations, like the methods TeMDA intercepts.
	@CreatePolicyStatementAnnotation(why = "", when = "", actions = { Action.STORE, Action.ACCESS },
			datas = { Fixtures.SHARED_DATA, Fixtures.UNKNOWN })
	void withActionsAndData() {}

	@CreatePolicyStatementAnnotation(why = "", when = "", actions = {}, datas = {})
	void minimal() {}

	@CreatePolicyStatementAnnotation(why = "", when = "", actions = {}, datas = {},
			whereDestinationId = "destinationId", whereSource = "source")
	void whereDestinationIdAndSourceObject() {}

	@CreatePolicyStatementAnnotation(why = "", when = "", actions = {}, datas = {},
			whereDestination = "source", whereSourceId = "destinationId")
	void whereDestinationObjectAndSourceId() {}

	@CreatePolicyStatementAnnotation(why = "", when = "", actions = {}, datas = {},
			whereDestinationId = "unknownId")
	void whereUnknownLocation() {}

	@CreatePolicyStatementAnnotation(why = "", when = "", actions = {}, datas = {},
			howConsentId = "consentId", howContractId = "contractId", howDocumentId = "documentId",
			howDocumentsIds = "documentIds")
	void howByIds() {}

	@CreatePolicyStatementAnnotation(why = "", when = "", actions = {}, datas = {},
			howConsent = "consent", howContract = "contract", howDocument = "document", howDocuments = "documents")
	void howByObjects() {}

	@CreatePolicyStatementAnnotation(why = "", when = "", actions = {}, datas = {},
			howConsentId = "unknownId", howContractId = "unknownId", howDocumentId = "unknownId")
	void howUnknownPapers() {}

	private static CreatePolicyStatementAnnotation annotation(String method) throws Exception {
		return PolicyStatementPartsCreatorTest.class.getDeclaredMethod(method).getAnnotation(CreatePolicyStatementAnnotation.class);
	}

	private PrivacyModelRepository repo;
	private PrivacyPolicy model;
	private ReadTypeByAttribute reader;
	private ObjectManager objectManager;

	@Before
	public void setUp() {
		var logger = Mockito.mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		var objectFinder = new ObjectFinder(logger);
		reader = new ReadTypeByAttribute(logger, fieldFinder, objectFinder);
		objectManager = new ObjectManager(fieldFinder, reader, objectFinder);
		repo = Mockito.mock(PrivacyModelRepository.class);
		Mockito.when(repo.getFactory()).thenReturn(PrivacyModelFactory.eINSTANCE);
		model = Fixtures.filledModel();
		var second = PrivacyModelFactory.eINSTANCE.createDocument();
		second.setName(SECOND_DOCUMENT);
		model.getAllDocuments().add(second);
	}

	private static List<String> names(List<? extends NamedElement> elements) {
		return elements.stream().map(NamedElement::getName).collect(Collectors.toList());
	}

	// ------------------------------------------------------------------ What

	@Test
	public void createWhat_ActionsAndData_OnlyExistingDataIsAdded() throws Exception {
		var what = WhatCreator.createWhat(annotation("withActionsAndData"), repo, model, reader);
		assertEquals(Arrays.asList(Action.STORE, Action.ACCESS), what.getActions());
		assertEquals(Collections.singletonList(Fixtures.SHARED_DATA), names(what.getDatas()));
	}

	@Test
	public void createWhat_NoActionsNoData_EmptyWhat() throws Exception {
		var what = WhatCreator.createWhat(annotation("minimal"), repo, model, reader);
		assertTrue(what.getActions().isEmpty());
		assertTrue(what.getDatas().isEmpty());
	}

	// ------------------------------------------------------------------ Where

	@Test
	public void whereShouldCreate() throws Exception {
		assertFalse(WhereCreator.shouldCreate(annotation("minimal")));
		assertTrue(WhereCreator.shouldCreate(annotation("whereDestinationIdAndSourceObject")));
		assertTrue(WhereCreator.shouldCreate(annotation("whereDestinationObjectAndSourceId")));
		assertTrue(WhereCreator.shouldCreate(annotation("whereUnknownLocation")));
	}

	@Test
	public void createWhere_DestinationByIdSourceByObject() throws Exception {
		var where = WhereCreator.createWhere(Fixtures.propertyModel(new Service()),
				annotation("whereDestinationIdAndSourceObject"), repo, model, objectManager);
		assertEquals(Fixtures.LOCATION, where.getDestination().getName());
		assertEquals(Fixtures.LOCATION, where.getSource().getName());
	}

	@Test
	public void createWhere_DestinationByObjectSourceById() throws Exception {
		var where = WhereCreator.createWhere(Fixtures.propertyModel(new Service()),
				annotation("whereDestinationObjectAndSourceId"), repo, model, objectManager);
		assertEquals(Fixtures.LOCATION, where.getDestination().getName());
		assertEquals(Fixtures.LOCATION, where.getSource().getName());
	}

	@Test
	public void createWhere_UnknownLocation_NotSet() throws Exception {
		var where = WhereCreator.createWhere(Fixtures.propertyModel(new Service()),
				annotation("whereUnknownLocation"), repo, model, objectManager);
		assertNull(where.getDestination());
		assertNull(where.getSource());
	}

	// ------------------------------------------------------------------ How

	@Test
	public void howShouldCreate() throws Exception {
		assertFalse(HowCreator.shouldCreate(annotation("minimal")));
		assertTrue(HowCreator.shouldCreate(annotation("howByIds")));
		assertTrue(HowCreator.shouldCreate(annotation("howByObjects")));
		assertTrue(HowCreator.shouldCreate(annotation("howUnknownPapers")));
	}

	@Test
	public void createHow_ByIds() throws Exception {
		var how = HowCreator.createHow(Fixtures.propertyModel(new Service()), annotation("howByIds"), repo, model,
				objectManager, reader);
		assertEquals(Fixtures.CONSENT, how.getConsent().getName());
		assertEquals(Fixtures.CONTRACT, how.getContract().getName());
		// documentIds (one existing, one unknown) + documentId
		assertEquals(Arrays.asList(Fixtures.DOCUMENT, SECOND_DOCUMENT), names(how.getDocuments()));
	}

	@Test
	public void createHow_ByObjects() throws Exception {
		var how = HowCreator.createHow(Fixtures.propertyModel(new Service()), annotation("howByObjects"), repo, model,
				objectManager, reader);
		assertEquals(Fixtures.CONSENT, how.getConsent().getName());
		assertEquals(Fixtures.CONTRACT, how.getContract().getName());
		// documents + document
		assertEquals(Arrays.asList(Fixtures.DOCUMENT, SECOND_DOCUMENT), names(how.getDocuments()));
	}

	@Test
	public void createHow_UnknownPapers_NothingSet() throws Exception {
		var how = HowCreator.createHow(Fixtures.propertyModel(new Service()), annotation("howUnknownPapers"), repo, model,
				objectManager, reader);
		assertNull(how.getConsent());
		assertNull(how.getContract());
		assertTrue(how.getDocuments().isEmpty());
	}
}
