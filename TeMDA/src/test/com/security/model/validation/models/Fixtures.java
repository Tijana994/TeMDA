package test.com.security.model.validation.models;

import java.util.Date;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.mockito.Mockito;

import com.security.model.validation.annotations.ComplaintAnnotation;
import com.security.model.validation.annotations.LocationAnnotation;
import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.PolicyStatementAnnotation;
import com.security.model.validation.annotations.PrincipalAnnotation;
import com.security.model.validation.annotations.PrivacyDataAnnotation;
import com.security.model.validation.annotations.PurposeAnnotation;
import com.security.model.validation.annotations.TimeStatementAnnotation;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.models.CreationModel;

import privacyModel.PrivacyModelFactory;
import privacyModel.PrivacyPolicy;
import privacyModel.TimePreposition;

/** Annotated application-like classes and a filled privacy model, shared by the tests. */
public class Fixtures {

	public static final String PRINCIPAL = "Ana";
	public static final String LOCATION = "Serbia";
	public static final String DATA = "email";
	public static final String SHARED_DATA = "Ana email";
	public static final String STATEMENT = "Store email";
	public static final String COMPLAINT = "Erase email";
	public static final String CONSENT = "Marketing consent";
	public static final String CONTRACT = "Account contract";
	public static final String DOCUMENT = "Court approval";
	public static final String UNKNOWN = "Unknown";

	// ------------------------------------------------------------------ annotated classes

	@PrincipalAnnotation(id = "name")
	public static class User {
		public String name;
		public User(String name) { this.name = name; }
	}

	@LocationAnnotation(id = "name")
	public static class Country {
		public String name;
		public Country(String name) { this.name = name; }
	}

	@PrivacyDataAnnotation(id = "name")
	public static class Data {
		public String name;
		public Data(String name) { this.name = name; }
	}

	@PolicyStatementAnnotation(id = "name")
	public static class Statement {
		public String name;
		public Statement(String name) { this.name = name; }
	}

	@ComplaintAnnotation(id = "name", when = "date")
	public static class Request {
		public String name;
		public Date date = new Date();
		public Request(String name) { this.name = name; }
	}

	@PaperAnnotation(id = "name", startDate = "start", location = "city")
	public static class Paper {
		public String name;
		public Date start = new Date();
		public String city = "Belgrade";
		public Paper(String name) { this.name = name; }
	}

	/** A class without any TeMDA annotation. */
	public static class Plain {
		public String name = "plain";
	}

	/** Dates annotated on fields, as in an object returned by a method or passed as a parameter. */
	public static class Period {
		@TimeStatementAnnotation(preposition = TimePreposition.FROM)
		public Date start;
		@TimeStatementAnnotation(preposition = TimePreposition.TO)
		public Date end;
		public Date notAnnotated;
		public Period(Date start, Date end) { this.start = start; this.end = end; this.notAnnotated = start; }
	}

	@PurposeAnnotation(details = "details", reason = "reason", reasonSubtype = "subtype")
	public static class SimplePurpose {
		public String details;
		public int reason;
		public int subtype;
		public SimplePurpose(String details, int reason, int subtype) {
			this.details = details; this.reason = reason; this.subtype = subtype;
		}
	}

	@PurposeAnnotation(details = "details", reason = "reason", reasonSubtype = "subtype",
			subPurposes = "subPurposes")
	public static class ComplexPurpose {
		public String details = "main";
		public int reason;
		public int subtype;
		public List<SimplePurpose> subPurposes;
	}

	@PurposeAnnotation(details = "details", reason = "reason", reasonSubtype = "subtype", subPurposes = "notAList")
	public static class PurposeWithWrongSubPurposes {
		public String details = "main";
		public int reason;
		public int subtype;
		public String notAList = "x";
	}

	/** The object whose properties TeMDA reads (e.g. a service class with fields). */
	public static class Holder {
		public Object value;
		public Object other;
		public Holder(Object value) { this.value = value; }
		public Holder(Object value, Object other) { this.value = value; this.other = other; }
	}

	// ------------------------------------------------------------------ creation models

	/** A creation model that reads properties of the given holder object. */
	public static CreationModel propertyModel(Object holder) {
		var model = new CreationModel(null, null, CreatedObjectLocation.Return, ParametersObjectsLocation.Property);
		if (holder != null) {
			model.setObject(holder);
		}
		return model;
	}

	/** A join point for a method with the given parameter names and argument values. */
	public static ProceedingJoinPoint joinPoint(String[] names, Object... args) {
		var signature = Mockito.mock(MethodSignature.class);
		Mockito.when(signature.getParameterNames()).thenReturn(names);
		var joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		Mockito.when(joinPoint.getSignature()).thenReturn(signature);
		Mockito.when(joinPoint.getArgs()).thenReturn(args);
		return joinPoint;
	}

	// ------------------------------------------------------------------ privacy model

	/** A privacy model that contains one object of every kind, named with the constants above. */
	public static PrivacyPolicy filledModel() {
		var f = PrivacyModelFactory.eINSTANCE;
		var model = TestUtility.createPrivacyModel();

		var principal = f.createPrincipal();
		principal.setName(PRINCIPAL);
		model.getAllPrincipals().add(principal);

		var location = f.createLocation();
		location.setName(LOCATION);
		model.getLocations().add(location);

		var data = f.createPrivacyData();
		data.setName(DATA);
		model.getAllDatas().add(data);

		var shared = f.createSharedPrivacyData();
		shared.setName(SHARED_DATA);
		shared.setPrivacydata(data);
		model.getAllSharedPrivacyData().add(shared);

		var statement = f.createPolicyStatement();
		statement.setName(STATEMENT);
		model.getPolicyStatements().add(statement);

		var complaint = f.createComplaint();
		complaint.setName(COMPLAINT);
		model.getAllComplaints().add(complaint);

		var consent = f.createConsent();
		consent.setName(CONSENT);
		model.getAllConsents().add(consent);

		var contract = f.createContract();
		contract.setName(CONTRACT);
		model.getAllContracts().add(contract);

		var document = f.createDocument();
		document.setName(DOCUMENT);
		model.getAllDocuments().add(document);

		return model;
	}
}
