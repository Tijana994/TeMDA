package test.com.security.model.validation.aspects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedConstruction;

import com.security.model.validation.annotations.PaperAnnotation;
import com.security.model.validation.annotations.creators.CreateContractAnnotation;
import com.security.model.validation.aspects.CreateContractAspect;

import privacyModel.Contract;
import privacyModel.Document;
import privacyModel.DocumentType;
import privacyModel.PrivacyModelFactory;
import privacyModel.PrivacyPolicy;
import privacyModel.Provider;
import utility.PrivacyModelRepository;

/**
 * A contract can be concluded with a provider and can carry documents, such as a transfer certificate.
 * The join point is mocked, and the repository works on an in-memory model.
 */
public class CreateContractAspectTest {

	@PaperAnnotation(id = "name", startDate = "startDate", location = "location")
	public static class Agreement {
		@SuppressWarnings("unused")
		private String name;
		@SuppressWarnings("unused")
		private Date startDate = new Date();
		@SuppressWarnings("unused")
		private String location = "Belgrade";

		public Agreement(String name) {
			this.name = name;
		}
	}

	public static class Contracts {
		@CreateContractAnnotation(provider = "US cloud", documentsIds = "certificateIds")
		public Agreement signWithCloud(List<String> certificateIds) {
			return new Agreement("Hosting agreement");
		}

		@CreateContractAnnotation(providerId = "providerName")
		public Agreement signWith(String providerName) {
			return new Agreement("Backup agreement");
		}

		@CreateContractAnnotation
		public Agreement signWithNobody() {
			return new Agreement("Empty agreement");
		}
	}

	private PrivacyPolicy model;
	private Provider usCloud;
	private Provider euBackup;
	private Document certificate;

	@Before
	public void setUp() {
		var factory = PrivacyModelFactory.eINSTANCE;
		model = factory.createPrivacyPolicy();
		usCloud = factory.createProvider();
		usCloud.setName("US cloud");
		euBackup = factory.createProvider();
		euBackup.setName("EU backup");
		model.getAllProviders().addAll(List.of(usCloud, euBackup));
		certificate = factory.createDocument();
		certificate.setName("Standard contractual clauses");
		certificate.setDocumentType(DocumentType.TRANSFER_CERTIFICATE);
		model.getAllDocuments().add(certificate);
	}

	private Contract run(String methodName, Class<?>[] types, Object[] args, String[] parameterNames, String contractName) throws Throwable {
		var target = new Contracts();
		Method method = Contracts.class.getMethod(methodName, types);
		var joinPoint = mock(ProceedingJoinPoint.class);
		var signature = mock(MethodSignature.class);
		when(joinPoint.getArgs()).thenReturn(args);
		when(joinPoint.proceed(args)).thenReturn(method.invoke(target, args));
		when(joinPoint.getThis()).thenReturn(target);
		when(joinPoint.getSignature()).thenReturn(signature);
		when(signature.getMethod()).thenReturn(method);
		when(signature.getParameterNames()).thenReturn(parameterNames);
		try (MockedConstruction<PrivacyModelRepository> repositories = mockConstruction(PrivacyModelRepository.class, (repository, context) -> {
			when(repository.getModel()).thenReturn(model);
			when(repository.getFactory()).thenReturn(PrivacyModelFactory.eINSTANCE);
		})) {
			new CreateContractAspect().createContract(joinPoint);
		}
		return model.getAllContracts().stream().filter(contract -> contract.getName().equals(contractName)).findFirst().orElse(null);
	}

	@Test
	public void contractWithProviderCarriesTheTransferCertificate() throws Throwable {
		var contract = run("signWithCloud", new Class<?>[] {List.class}, new Object[] {List.of("Standard contractual clauses")},
				new String[] {"certificateIds"}, "Hosting agreement");
		assertNotNull(contract);
		assertSame(usCloud, contract.getProvider());
		assertNull(contract.getProvidedBy());
		assertEquals(List.of(certificate), contract.getDocuments());
	}

	@Test
	public void providerIsReadFromParameter() throws Throwable {
		var contract = run("signWith", new Class<?>[] {String.class}, new Object[] {"EU backup"},
				new String[] {"providerName"}, "Backup agreement");
		assertSame(euBackup, contract.getProvider());
		assertTrue(contract.getDocuments().isEmpty());
	}

	@Test
	public void contractWithoutPartyIsStillRecorded() throws Throwable {
		var contract = run("signWithNobody", new Class<?>[0], new Object[0], new String[0], "Empty agreement");
		assertNotNull(contract);
		assertNull(contract.getProvider());
		assertNull(contract.getProvidedBy());
	}
}
