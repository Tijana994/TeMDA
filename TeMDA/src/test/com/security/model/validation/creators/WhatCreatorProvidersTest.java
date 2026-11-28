package test.com.security.model.validation.creators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.creators.WhatCreator;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.ReadTypeByAttribute;
import com.security.model.validation.helpers.interfaces.ILogger;

import privacyModel.Action;
import privacyModel.PrivacyModelFactory;
import privacyModel.PrivacyPolicy;
import privacyModel.Provider;
import utility.PrivacyModelRepository;

/**
 * The providers listed in the annotation of a policy statement are added to its What part.
 */
public class WhatCreatorProvidersTest {

	public static class Statements {
		@CreatePolicyStatementAnnotation(why = "purpose", when = "time", actions = {Action.STORE}, datas = {"email"},
				providers = {"EU cloud", "Mars cloud"})
		public void storeInCloud() {
		}

		@CreatePolicyStatementAnnotation(why = "purpose", when = "time", actions = {Action.STORE}, datas = {"email"})
		public void storeLocally() {
		}
	}

	private ILogger logger;
	private ReadTypeByAttribute readTypeByAttribute;
	private PrivacyPolicy model;
	private Provider euCloud;

	@Before
	public void setUp() {
		logger = mock(ILogger.class);
		var objectFinder = new ObjectFinder(logger);
		readTypeByAttribute = new ReadTypeByAttribute(logger, new FieldFinder(logger), objectFinder);
		model = PrivacyModelFactory.eINSTANCE.createPrivacyPolicy();
		euCloud = PrivacyModelFactory.eINSTANCE.createProvider();
		euCloud.setName("EU cloud");
		model.getAllProviders().add(euCloud);
	}

	private static CreatePolicyStatementAnnotation annotation(String methodName) throws NoSuchMethodException {
		return Statements.class.getMethod(methodName).getAnnotation(CreatePolicyStatementAnnotation.class);
	}

	@Test
	public void statementGetsTheConfiguredProviders() throws NoSuchMethodException {
		var what = WhatCreator.createWhat(annotation("storeInCloud"), new PrivacyModelRepository(), model, readTypeByAttribute);
		assertEquals(List.of(euCloud), what.getProviders());
		verify(logger).LogErrorMessage("There is no provider with id Mars cloud");
	}

	@Test
	public void statementWithoutProvidersHasNone() throws NoSuchMethodException {
		var what = WhatCreator.createWhat(annotation("storeLocally"), new PrivacyModelRepository(), model, readTypeByAttribute);
		assertTrue(what.getProviders().isEmpty());
	}
}
