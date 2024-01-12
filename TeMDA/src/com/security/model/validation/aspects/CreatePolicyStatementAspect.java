package com.security.model.validation.aspects;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
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
		Object returnedObject = thisJoinPoint.proceed(args);
		Object originalObject = thisJoinPoint.getThis();
		Class<? extends Object> originalObjectClass = originalObject.getClass();
	    MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();
	    Method method = signature.getMethod();
		CreatePolicyStatementAnnotation createPolicyStatement = method.getAnnotation(CreatePolicyStatementAnnotation.class);
		if(createPolicyStatement == null)
		{
			System.out.println("There is no create policy statement annotation");
			return returnedObject;
		}
		Object createdObject = FieldFinder.getObjectToReadFrom(returnedObject, originalObject, createPolicyStatement.createdObjectLocation(), createPolicyStatement.name(), thisJoinPoint);
		if(createdObject == null)
		{
			System.out.println("Read from object is null = CreatePolicyStatementAspect");
			return returnedObject;
		}
		Class<? extends Object> createdObjectClass = createdObject.getClass();
		PolicyStatementAnnotation policyStatement = createdObjectClass.getAnnotation(PolicyStatementAnnotation.class);
		
		if(policyStatement == null)
		{
			System.out.println("There is no policy statement annotation");
			return returnedObject;
		}
		
		try
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var policyStatementObject = repo.getFactory().createPolicyStatement();
			policyStatementObject.setName((String)FieldFinder.getFieldValue(policyStatement.id(), returnedObject, createdObjectClass));
			var whoId = (String)FieldFinder.getFieldValue(createPolicyStatement.who(), originalObject, originalObjectClass);
			var whoPrincipal = ObjectFinder.checkIfPrincipalExists(whoId, model);
			if(whoPrincipal.isPresent())
			{
				policyStatementObject.setWho(whoPrincipal.get());
			}
			var whoseId = (String)FieldFinder.getFieldValue(createPolicyStatement.whose(), originalObject, originalObjectClass);
			var whosePrincipal = ObjectFinder.checkIfPrincipalExists(whoseId, model);
			if(whosePrincipal.isPresent())
			{
				policyStatementObject.setWhose(whosePrincipal.get());
			}
			var whomId = (String)FieldFinder.getFieldValue(createPolicyStatement.whom(), originalObject, originalObjectClass);
			var whomPrincipal = ObjectFinder.checkIfPrincipalExists(whomId, model);
			if(whomPrincipal.isPresent())
			{
				policyStatementObject.setWhom(whomPrincipal.get());
			}
			
			var purpose = PurposeCreator.createPurpose(originalObjectClass, originalObject, createPolicyStatement.why(),repo.getFactory());
			policyStatementObject.setWhy(purpose);
			var when = WhenCreator.createWhen(originalObjectClass, originalObject, createPolicyStatement.when(), repo.getFactory());
			policyStatementObject.setWhen(when);
			
		    var what = createWhat(createPolicyStatement, repo, model);
		    policyStatementObject.setWhat(what);
		    
		    var how = createHow(createPolicyStatement, originalObject, originalObjectClass, repo, model, thisJoinPoint);
			policyStatementObject.setHow(how);
			
			model.getPolicyStatements().add(policyStatementObject);
			repo.saveModel(model);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		return returnedObject;
	}
	private How createHow(CreatePolicyStatementAnnotation createPolicyStatement, Object obj,
			Class<? extends Object> objectClass, PrivacyModelRepository repo, PrivacyPolicy model, JoinPoint jp) {
		var how = repo.getFactory().createHow();
		
		if(createPolicyStatement.howDocuments() != Constants.Empty)
		{
			
		}
		if(createPolicyStatement.howDocumentsIds() != Constants.Empty)
		{
			var documents = ReadTypeByAttribute.getDocumentsById(obj, objectClass, createPolicyStatement.howDocumentsIds(), model);
			if(!documents.isEmpty())
			{
				how.getDocuments().addAll(documents);
			}
		}
		if(createPolicyStatement.howConsent() != Constants.Empty)
		{
			var consentId = ReadTypeByAttribute.getConsentIdFromObject(objectClass, obj, createPolicyStatement.howConsent(), createPolicyStatement.parametersLocation(), jp);
			if(consentId.isPresent())
			{
				var consentName = consentId.get();
				trySetConsentId(model, how, consentName);
			}
		}
		if(createPolicyStatement.howConsentId() != Constants.Empty)
		{
			var consentId = (String)FieldFinder.getFieldValue(createPolicyStatement.howConsentId(), obj, objectClass);
			trySetConsentId(model, how, consentId);
		}
		return how;
	}
	private void trySetConsentId(PrivacyPolicy model, How how, String consentId) {
		var consent = ObjectFinder.checkIfConsentExists(consentId, model);
		if(consent.isPresent())
		{
			how.setConsent(consent.get());
		}
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
