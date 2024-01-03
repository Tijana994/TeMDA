package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.security.model.validation.annotations.PolicyStatementAnnotation;
import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.creators.PurposeCreator;
import com.security.model.validation.creators.WhenCreator;
import com.security.model.validation.helpers.FieldFinder;
import com.security.model.validation.helpers.ObjectFinder;
import com.security.model.validation.helpers.ReadTypeByAttribute;

import privacyModel.How;
import privacyModel.PrivacyPolicy;
import privacyModel.What;
import utility.PrivacyModelRepository;

@Aspect
public class CreatePolicyStatementAspect {
	
	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation)")
	void function() {}
	@Around("function()")
	public Object createPolicyStatement(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		Object ret = thisJoinPoint.proceed(args);
		Object obj = thisJoinPoint.getThis();
		Class<? extends Object> objectClass = obj.getClass();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
		CreatePolicyStatementAnnotation createPolicyStatement = method.getAnnotation(CreatePolicyStatementAnnotation.class);
		if(createPolicyStatement == null)
		{
			System.out.println("There is no create policy statement annotation");
			return ret;
		}
		Object retFromObj = FieldFinder.getObjectToReadFrom(ret, obj, createPolicyStatement.createdObjectLocation(), createPolicyStatement.name(), thisJoinPoint);
		if(retFromObj == null)
		{
			System.out.println("Read from object is null = CreatePolicyStatementAspect");
			return ret;
		}
		Class<? extends Object> retClass = retFromObj.getClass();
		PolicyStatementAnnotation policyStatement = retClass.getAnnotation(PolicyStatementAnnotation.class);
		
		if(policyStatement == null)
		{
			System.out.println("There is no policy statement annotation");
			return ret;
		}
		
		try
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var policyStatementObject = repo.getFactory().createPolicyStatement();
			policyStatementObject.setName((String)FieldFinder.getFieldValue(policyStatement.id(), ret, retClass));
			var whoId = (String)FieldFinder.getFieldValue(createPolicyStatement.who(), obj, objectClass);
			var whoPrincipal = ObjectFinder.checkIfPrincipalExists(whoId, model);
			if(whoPrincipal.isPresent())
			{
				policyStatementObject.setWho(whoPrincipal.get());
			}
			var whoseId = (String)FieldFinder.getFieldValue(createPolicyStatement.whose(), obj, objectClass);
			var whosePrincipal = ObjectFinder.checkIfPrincipalExists(whoseId, model);
			if(whosePrincipal.isPresent())
			{
				policyStatementObject.setWhose(whosePrincipal.get());
			}
			var whomId = (String)FieldFinder.getFieldValue(createPolicyStatement.whom(), obj, objectClass);
			var whomPrincipal = ObjectFinder.checkIfPrincipalExists(whomId, model);
			if(whomPrincipal.isPresent())
			{
				policyStatementObject.setWhom(whomPrincipal.get());
			}
			
			var purpose = PurposeCreator.createPurpose(objectClass, obj, createPolicyStatement.why(),repo.getFactory());
			policyStatementObject.setWhy(purpose);
			var when = WhenCreator.createWhen(objectClass, obj, createPolicyStatement.when(), repo.getFactory());
			policyStatementObject.setWhen(when);
			
		    var what = createWhat(createPolicyStatement, repo, model);
		    policyStatementObject.setWhat(what);
		    
		    var how = createHow(createPolicyStatement, retFromObj, retClass, repo, model);
			policyStatementObject.setHow(how);
			
			model.getPolicyStatements().add(policyStatementObject);
			repo.saveModel(model);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		return ret;
	}
	private How createHow(CreatePolicyStatementAnnotation createPolicyStatement, Object retFromObj,
			Class<? extends Object> retClass, PrivacyModelRepository repo, PrivacyPolicy model) {
		var how = repo.getFactory().createHow();
		
		if(createPolicyStatement.howDocuments() != Constants.Empty)
		{
			
		}
		if(createPolicyStatement.howDocumentsIds() != Constants.Empty)
		{
			var documents = ReadTypeByAttribute.getDocumentsById(retFromObj, retClass, createPolicyStatement.howDocumentsIds(), model);
			if(!documents.isEmpty())
			{
				how.getDocuments().addAll(documents);
			}
		}
		if(createPolicyStatement.howConsent() != Constants.Empty)
		{
			
		}
		if(createPolicyStatement.howConsentId() != Constants.Empty)
		{
			var consentId = (String)FieldFinder.getFieldValue(createPolicyStatement.howConsentId(), retFromObj, retClass);
			var consent = ObjectFinder.checkIfConsentExists(consentId, model);
			if(consent.isPresent())
			{
				how.setConsent(consent.get());
			}
		}
		return how;
	}
	private What createWhat(CreatePolicyStatementAnnotation createPolicyStatement, PrivacyModelRepository repo,
			PrivacyPolicy model) {
		var what = repo.getFactory().createWhat();
		if(createPolicyStatement.actions().length != 0)
		{
		  what.getActions().addAll(Arrays.asList(createPolicyStatement.actions()));
		}

		var datas = ReadTypeByAttribute.getSharedPrivacyDataById(createPolicyStatement.datas(), model);
		what.getDatas().addAll(datas);
		return what;
	}
}
