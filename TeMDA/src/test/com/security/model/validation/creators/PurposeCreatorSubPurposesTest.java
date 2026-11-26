package test.com.security.model.validation.creators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.security.model.validation.annotations.PurposeAnnotation;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.creators.PurposeCreator;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.interfaces.ILogger;
import com.security.model.validation.models.CreationModel;

import privacyModel.PrivacyModelFactory;
import privacyModel.ProcessingReason;
import privacyModel.ProcessingReasonSubtype;
import privacyModel.Purpose;

/**
 * Sub-purposes are read recursively, from a field of any List type that may be private or inherited.
 * A list element without a purpose annotation is skipped and reported.
 */
public class PurposeCreatorSubPurposesTest {

	private static final int MARKETING = ProcessingReason.MARKETING_VALUE;
	private static final int PROFILING = ProcessingReason.PROFILING_VALUE;
	private static final int LEGITIMATE_INTERESTS = ProcessingReason.LEGITIMATE_INTERESTS_VALUE;
	private static final int NONE = ProcessingReasonSubtype.NONE_VALUE;

	@PurposeAnnotation(details = "text", reason = "reason", reasonSubtype = "subtype", subPurposes = "subPurposes")
	static class ArrayListPurpose {
		private String text;
		private int reason;
		private int subtype;
		private ArrayList<Object> subPurposes = new ArrayList<>();
		ArrayListPurpose(String text, int reason, int subtype) { this.text = text; this.reason = reason; this.subtype = subtype; }
		ArrayListPurpose add(Object purpose) { subPurposes.add(purpose); return this; }
	}

	@PurposeAnnotation(details = "text", reason = "reason", reasonSubtype = "subtype", subPurposes = "subPurposes")
	static class InheritedListPurpose extends ArrayListPurpose {
		InheritedListPurpose(String text, int reason, int subtype) { super(text, reason, subtype); }
	}

	@PurposeAnnotation(details = "text", reason = "reason", reasonSubtype = "subtype", subPurposes = "subPurposes")
	static class LinkedListPurpose {
		private String text = "linked";
		private int reason = MARKETING;
		private int subtype = NONE;
		private List<Object> subPurposes = new LinkedList<>();
	}

	@PurposeAnnotation(details = "text", reason = "reason", reasonSubtype = "subtype", subPurposes = "subPurposes")
	static class NotAListPurpose {
		private String text = "not a list";
		private int reason = MARKETING;
		private int subtype = NONE;
		private String subPurposes = "marketing";
	}

	static class NotAnnotated {
	}

	static class Holder {
		private Object purpose;
		Holder(Object purpose) { this.purpose = purpose; }
	}

	private final List<String> messages = new ArrayList<>();
	private ILogger logger;
	private FieldFinder fieldFinder;

	@Before
	public void setUp() {
		messages.clear();
		logger = new ILogger() {
			@Override
			public void LogErrorMessage(String message) { messages.add(message); }
			@Override
			public void LogExceptionMessage(Object exception) { messages.add(String.valueOf(exception)); }
		};
		fieldFinder = new FieldFinder(logger);
	}

	private Purpose create(Object purpose) {
		Holder holder = new Holder(purpose);
		CreationModel creationModel = new CreationModel(null, null, CreatedObjectLocation.Return, ParametersObjectsLocation.Property);
		return PurposeCreator.createPurpose(creationModel, Holder.class, holder, "purpose", PrivacyModelFactory.eINSTANCE, fieldFinder, logger);
	}

	@Test
	public void readsSubPurposesOnEveryLevel() {
		Purpose purpose = create(new ArrayListPurpose("marketing", MARKETING, NONE)
				.add(new ArrayListPurpose("segmentation", PROFILING, NONE)
						.add(new ArrayListPurpose("balancing test", LEGITIMATE_INTERESTS, NONE))));
		assertNotNull(purpose);
		assertEquals(ProcessingReason.MARKETING, purpose.getProcessingReason());
		assertEquals(1, purpose.getSubPurposes().size());
		Purpose profiling = purpose.getSubPurposes().get(0);
		assertEquals(ProcessingReason.PROFILING, profiling.getProcessingReason());
		assertEquals(1, profiling.getSubPurposes().size());
		assertEquals(ProcessingReason.LEGITIMATE_INTERESTS, profiling.getSubPurposes().get(0).getProcessingReason());
		assertTrue(messages.isEmpty());
	}

	@Test
	public void readsInheritedPrivateField() {
		Purpose purpose = create(new InheritedListPurpose("marketing", MARKETING, NONE)
				.add(new ArrayListPurpose("segmentation", PROFILING, NONE)));
		assertNotNull(purpose);
		assertEquals(1, purpose.getSubPurposes().size());
		assertEquals(ProcessingReason.PROFILING, purpose.getSubPurposes().get(0).getProcessingReason());
	}

	@Test
	public void acceptsAnyListType() {
		LinkedListPurpose linked = new LinkedListPurpose();
		linked.subPurposes.add(new ArrayListPurpose("segmentation", PROFILING, NONE));
		Purpose purpose = create(linked);
		assertNotNull(purpose);
		assertEquals(1, purpose.getSubPurposes().size());
	}

	@Test
	public void skipsElementsWithoutAnnotationAndNullElements() {
		Purpose purpose = create(new ArrayListPurpose("marketing", MARKETING, NONE)
				.add(new NotAnnotated())
				.add(null)
				.add(new ArrayListPurpose("segmentation", PROFILING, NONE)));
		assertNotNull(purpose);
		assertEquals(1, purpose.getSubPurposes().size());
		assertEquals(ProcessingReason.PROFILING, purpose.getSubPurposes().get(0).getProcessingReason());
		assertEquals(1, messages.size());
		assertTrue(messages.get(0).contains("no purpose annotation"));
	}

	@Test
	public void keepsPurposeWhenSubPurposesAreNotAList() {
		Purpose purpose = create(new NotAListPurpose());
		assertNotNull(purpose);
		assertEquals(ProcessingReason.MARKETING, purpose.getProcessingReason());
		assertTrue(purpose.getSubPurposes().isEmpty());
		assertTrue(messages.contains("Subpurpose should be a list type."));
	}
}
