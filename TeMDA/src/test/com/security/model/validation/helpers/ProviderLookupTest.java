package test.com.security.model.validation.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.ObjectManager;
import com.security.model.validation.helpers.ReadTypeByAttribute;
import com.security.model.validation.helpers.interfaces.ILogger;
import com.security.model.validation.models.CreationModel;

import privacyModel.PrivacyModelFactory;
import privacyModel.PrivacyPolicy;
import privacyModel.Provider;

/**
 * Lookup of providers by name, used for the providers of a policy statement and for the provider of a contract.
 */
public class ProviderLookupTest {

	public static class ContractForm {
		@SuppressWarnings("unused")
		private String providerName;

		public ContractForm(String providerName) {
			this.providerName = providerName;
		}
	}

	private ILogger logger;
	private ObjectFinder objectFinder;
	private ReadTypeByAttribute readTypeByAttribute;
	private ObjectManager objectManager;
	private PrivacyPolicy model;
	private Provider euCloud;
	private Provider usCloud;

	@Before
	public void setUp() {
		logger = mock(ILogger.class);
		var fieldFinder = new FieldFinder(logger);
		objectFinder = new ObjectFinder(logger);
		readTypeByAttribute = new ReadTypeByAttribute(logger, fieldFinder, objectFinder);
		objectManager = new ObjectManager(fieldFinder, readTypeByAttribute, objectFinder);
		model = PrivacyModelFactory.eINSTANCE.createPrivacyPolicy();
		euCloud = addProvider("EU cloud");
		usCloud = addProvider("US cloud");
	}

	private Provider addProvider(String name) {
		var provider = PrivacyModelFactory.eINSTANCE.createProvider();
		provider.setName(name);
		model.getAllProviders().add(provider);
		return provider;
	}

	private CreationModel creationModelWith(Object object) {
		return new CreationModel(null, object, null, CreatedObjectLocation.Return, ParametersObjectsLocation.Property, Constants.Empty);
	}

	@Test
	public void providerIsFoundByName() {
		assertSame(euCloud, objectFinder.checkIfProviderExists("EU cloud", model).get());
	}

	@Test
	public void missingProviderIsReported() {
		assertTrue(objectFinder.checkIfProviderExists("Mars cloud", model).isEmpty());
		verify(logger).LogErrorMessage("There is no provider with id Mars cloud");
	}

	@Test
	public void nullProviderIdGivesNoProvider() {
		assertTrue(objectFinder.checkIfProviderExists(null, model).isEmpty());
		verifyNoInteractions(logger);
	}

	@Test
	public void providersOfStatementSkipUnknownNames() {
		var providers = readTypeByAttribute.getProvidersById(new String[] {"EU cloud", "Mars cloud", "US cloud"}, model);
		assertEquals(List.of(euCloud, usCloud), providers);
		verify(logger).LogErrorMessage("There is no provider with id Mars cloud");
	}

	@Test
	public void noProvidersForEmptyArray() {
		assertTrue(readTypeByAttribute.getProvidersById(new String[0], model).isEmpty());
	}

	@Test
	public void contractProviderIsFoundByName() {
		assertSame(usCloud, objectManager.tryGetProvider("US cloud", model).get());
	}

	@Test
	public void contractProviderIsReadFromProperty() {
		var provider = objectManager.tryGetProviderById(creationModelWith(new ContractForm("EU cloud")), "providerName", model);
		assertSame(euCloud, provider.get());
	}

	@Test
	public void emptyPropertyGivesNoProvider() {
		var provider = objectManager.tryGetProviderById(creationModelWith(new ContractForm(null)), "providerName", model);
		assertTrue(provider.isEmpty());
	}
}
