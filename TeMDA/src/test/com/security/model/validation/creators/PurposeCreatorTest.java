package test.com.security.model.validation.creators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.security.model.validation.creators.PurposeCreator;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.interfaces.ILogger;

import privacyModel.PrivacyModelFactory;
import privacyModel.ProcessingReason;
import privacyModel.ProcessingReasonSubtype;
import privacyModel.Purpose;
import test.com.security.model.validation.models.Fixtures;
import test.com.security.model.validation.models.Fixtures.ComplexPurpose;
import test.com.security.model.validation.models.Fixtures.Holder;
import test.com.security.model.validation.models.Fixtures.Plain;
import test.com.security.model.validation.models.Fixtures.PurposeWithWrongSubPurposes;
import test.com.security.model.validation.models.Fixtures.SimplePurpose;

/** PurposeCreator reads an object annotated with @PurposeAnnotation (reason and sub-reason are enum values). */
public class PurposeCreatorTest {

	private ILogger logger;
	private FieldFinder fieldFinder;

	@Before
	public void setUp() {
		logger = Mockito.mock(ILogger.class);
		fieldFinder = new FieldFinder(logger);
	}

	private Purpose create(Object purposeObject) {
		var holder = new Holder(purposeObject);
		return PurposeCreator.createPurpose(Fixtures.propertyModel(holder), Holder.class, holder, "value",
				PrivacyModelFactory.eINSTANCE, fieldFinder, logger);
	}

	@Test
	public void createPurpose_SimplePurpose_ReadsReasonSubReasonAndDetails() {
		var purpose = create(new SimplePurpose("Newsletter", ProcessingReason.MARKETING_VALUE, ProcessingReasonSubtype.NONE_VALUE));
		assertEquals(ProcessingReason.MARKETING, purpose.getProcessingReason());
		assertEquals(ProcessingReasonSubtype.NONE, purpose.getProcessingReasonSubtype());
		assertEquals("Newsletter", purpose.getDetails());
		assertTrue(purpose.getSubPurposes().isEmpty());
	}

	@Test
	public void createPurpose_EveryReasonAndSubReason_AreMapped() {
		for (ProcessingReason reason : ProcessingReason.VALUES) {
			for (ProcessingReasonSubtype subtype : ProcessingReasonSubtype.VALUES) {
				var purpose = create(new SimplePurpose("x", reason.getValue(), subtype.getValue()));
				assertEquals(reason, purpose.getProcessingReason());
				assertEquals(subtype, purpose.getProcessingReasonSubtype());
			}
		}
	}

	@Test
	public void createPurpose_UnknownReasonValue_ReasonIsDefault() {
		// An int that is not an enum value cannot be mapped; EMF then keeps its default (the first literal).
		var purpose = create(new SimplePurpose("x", 999, 999));
		assertEquals(ProcessingReason.get(0), purpose.getProcessingReason());
		assertEquals(ProcessingReasonSubtype.get(0), purpose.getProcessingReasonSubtype());
	}

	@Test
	public void createPurpose_WithSubPurposes() {
		var complex = new ComplexPurpose();
		complex.reason = ProcessingReason.RESEARCH_VALUE;
		complex.subtype = ProcessingReasonSubtype.SCIENTIFIC_VALUE;
		complex.subPurposes = Arrays.asList(
				new SimplePurpose("Stats", ProcessingReason.STATISTICAL_PURPOSES_VALUE, ProcessingReasonSubtype.NONE_VALUE),
				new SimplePurpose("Ads", ProcessingReason.MARKETING_VALUE, ProcessingReasonSubtype.NONE_VALUE));

		var purpose = create(complex);

		assertEquals(ProcessingReason.RESEARCH, purpose.getProcessingReason());
		assertEquals(ProcessingReasonSubtype.SCIENTIFIC, purpose.getProcessingReasonSubtype());
		assertEquals(2, purpose.getSubPurposes().size());
		assertEquals(ProcessingReason.STATISTICAL_PURPOSES, purpose.getSubPurposes().get(0).getProcessingReason());
		assertEquals("Ads", purpose.getSubPurposes().get(1).getDetails());
	}

	@Test
	public void createPurpose_SubPurposesListIsNull_NoSubPurposes() {
		var complex = new ComplexPurpose();
		var purpose = create(complex);
		assertNotNull(purpose);
		assertTrue(purpose.getSubPurposes().isEmpty());
	}

	@Test
	public void createPurpose_SubPurposesIsNotList_LoggedError() {
		var purpose = create(new PurposeWithWrongSubPurposes());
		assertNotNull(purpose);
		assertTrue(purpose.getSubPurposes().isEmpty());
		verify(logger).LogErrorMessage("Subpurpose should be a list type.");
	}

	@Test
	public void createPurpose_ObjectWithoutAnnotation_LoggedError() {
		assertNull(create(new Plain()));
		verify(logger).LogErrorMessage("There is no purpose annotation");
	}

	@Test
	public void createPurpose_PropertyIsNull_ReturnsNull() {
		assertNull(create(null));
	}

	@Test
	public void createPurpose_OriginalObjectIsNull_LoggedError() {
		var purpose = PurposeCreator.createPurpose(Fixtures.propertyModel(null), Holder.class, null, "value",
				PrivacyModelFactory.eINSTANCE, fieldFinder, logger);
		assertNull(purpose);
		verify(logger).LogErrorMessage("Object is not instantiated.");
	}
}
