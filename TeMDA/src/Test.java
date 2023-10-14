import privacyModel.Principal;
import privacyModel.PrivacyModelFactory;
import privacyModel.PrivacyModelPackage;
import privacyModel.PrivacyPolicy;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		var p = PrivacyModelPackage.eINSTANCE;
        // Retrieve the default factory singleton
		PrivacyModelFactory factory = PrivacyModelFactory.eINSTANCE;
        // create an instance of myWeb
		PrivacyPolicy model = factory.createPrivacyPolicy();
        Principal myWeb = factory.createPrincipal();
        myWeb.setName("Hallo");
        // create a page
        model.getAllPrincipals().add(myWeb);
        var test = "";
	}
}