package com.security.model.validation.aspects;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.security.model.validation.annotations.PrincipalAnnotation;
import com.security.model.validation.annotations.creators.CreatePrincipalAnnotation;
import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.models.CreationModel;

import privacyModel.Principal;
import utility.PrivacyModelRepository;

@Aspect
public class CreatePrincipalAspect extends BaseAspect {

	@Pointcut("execution(* *..*(..)) && @annotation(com.security.model.validation.annotations.creators.CreatePrincipalAnnotation)")
	void function() {}
	@Around("function()")
	public Object createPrincipal(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		SetUp(thisJoinPoint);
		CreatePrincipalAnnotation createPrincipal = method.getAnnotation(CreatePrincipalAnnotation.class);
		if(createPrincipal == null)
		{
			Logger.LogErrorMessage("There is no create principal statement annotation");
			return returnedObject;
		}
		CreationModel createdObjectModel = new CreationModel(returnedObject, thisJoinPoint, createPrincipal.createdObjectLocation(), ParametersObjectsLocation.Property);
		createdObjectModel.setObject(FieldFinder.getCreatedObjectToReadFrom(createdObjectModel, originalObject, createPrincipal.name()));
		if(createdObjectModel.getObject() == null)
		{
			Logger.LogErrorMessage("Read from object is null = CreatePrincipalAspect");
			return returnedObject;
		}
		PrincipalAnnotation principal = createdObjectModel.getObjectClass().getAnnotation(PrincipalAnnotation.class);
		if(principal == null)
		{
			Logger.LogErrorMessage("There is no principal annotation");
			return returnedObject;
		}

		try 
		{
			PrivacyModelRepository repo = new PrivacyModelRepository();
			var model = repo.getModel();
			var principalObject = repo.getFactory().createPrincipal();
			principalObject.setName((String)FieldFinder.getFieldValue(principal.id(), createdObjectModel.getObject(), createdObjectModel.getObjectClass()));
			principalObject.setScope(createPrincipal.scope());
			principalObject.setType(createPrincipal.type());
			if(!principal.birthday().equals(Constants.Empty) && createPrincipal.shouldSetBirtday())
			{
				var birthday = (Date)FieldFinder.getFieldValue(principal.birthday(), createdObjectModel.getObject(), createdObjectModel.getObjectClass());
				principalObject.setBirthdate(birthday);
		        calculateAge(principalObject, birthday);
			}
			if(!createPrincipal.shouldSetBirtday() && createPrincipal.isLegalAge())
			{
				principalObject.setAge(model.getOwner().getInhabits().getLegalAgeLimit());;
			}
			if(!principal.parent().equals(Constants.Empty)) 
			{
				var parent = ObjectManager.tryGetPrincipalByFromObject(createdObjectModel, principal.parent(), model);
				if(parent.isPresent())
				{
					parent.get().getSubPrincipals().add(principalObject);
				}
			}
			if(!principal.parentId().equals(Constants.Empty))
			{
				var parent = ObjectManager.tryGetPrincipalByById(createdObjectModel, principal.parentId(), model);
				if(parent.isPresent())
				{
					parent.get().getSubPrincipals().add(principalObject);
				}
			}
			if(!principal.responsiblePersons().equals(Constants.Empty)) 
			{
				var parents = ReadTypeByAttribute.getPrincipalsFromObject(createdObjectModel, principal.responsiblePersons(), model);
				if(!parents.isEmpty())
				{
					principalObject.getResponsiblePersons().addAll(parents);
				}
			}
			if(!principal.responsiblePersonIds().equals(Constants.Empty))
			{
				var parents = ReadTypeByAttribute.getPrincipalsById(createdObjectModel, principal.responsiblePersonIds(), model);
				if(!parents.isEmpty())
				{
					principalObject.getResponsiblePersons().addAll(parents);
				}
			}
			if(!principal.childrens().equals(Constants.Empty))
			{
				var childrens = ReadTypeByAttribute.getPrincipalsFromObject(createdObjectModel, principal.childrens(), model);
				if(!childrens.isEmpty())
				{
					principalObject.getSubPrincipals().addAll(childrens);
				}
			}
			if(!principal.childrensIds().equals(Constants.Empty))
			{
				var childrens = ReadTypeByAttribute.getPrincipalsById(createdObjectModel, principal.childrensIds(), model);
				if(!childrens.isEmpty())
				{
					principalObject.getSubPrincipals().addAll(childrens);
				}
			}
			if(!principal.inhabits().equals(Constants.Empty) && createPrincipal.shouldSetLocation()) 
			{
				var location = ObjectManager.tryGetLocationFromObject(createdObjectModel, principal.inhabits(), model);
				if(location.isPresent())
				{
					principalObject.setInhabits(location.get());
				}
			}
			if(!principal.inhabitsId().equals(Constants.Empty) && createPrincipal.shouldSetLocation())
			{
				var location = ObjectManager.tryGetLocationById(createdObjectModel, principal.inhabitsId(), model);
				if(location.isPresent())
				{
					principalObject.setInhabits(location.get());
				}
			}
			
			model.getAllPrincipals().add(principalObject);
			repo.saveModel(model);
		}
		catch(Exception ex)
		{
			Logger.LogExceptionMessage(ex);
		}
		
		return returnedObject;
	}
	private void calculateAge(Principal principalObject, Date birthday) {
		LocalDate date = birthday.toInstant()
			      .atZone(ZoneId.systemDefault())
			      .toLocalDate();
		var age = LocalDate.now().minusYears(date.getYear());
		principalObject.setAge(age.getYear());
	}
}
