package test.com.security.model.validation.models;

import privacyModel.PrivacyModelFactory;
import privacyModel.PrivacyPolicy;

public class TestUtility {
	
	public static PrivacyPolicy createPrivacyModel()
	{
		//var p = PrivacyModelPackage.eINSTANCE;
		PrivacyModelFactory factory = PrivacyModelFactory.eINSTANCE;
		//var repo = new PrivacyModelRepository();
		var model = factory.createPrivacyPolicy();
		model.setConfigurationManager(factory.createConfigurationManager());
		model.setPrivacyPolicyHelper(factory.createPrivacyPolicyHelper());
	
		return model;
	}

}
