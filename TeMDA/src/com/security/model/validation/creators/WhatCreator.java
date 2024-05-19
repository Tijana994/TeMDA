package com.security.model.validation.creators;

import java.util.Arrays;

import com.security.model.validation.annotations.creators.CreatePolicyStatementAnnotation;
import com.security.model.validation.helpers.ReadTypeByAttribute;

import privacyModel.PrivacyPolicy;
import privacyModel.What;
import utility.PrivacyModelRepository;

public class WhatCreator {

	public static What createWhat(CreatePolicyStatementAnnotation createPolicyStatement, PrivacyModelRepository repo,
			PrivacyPolicy model, ReadTypeByAttribute readTypeByAttribute) {
		var what = repo.getFactory().createWhat();
		if(createPolicyStatement.actions().length != 0)
		{
		  what.getActions().addAll(Arrays.asList(createPolicyStatement.actions()));
		}

		var datas = readTypeByAttribute.getSharedPrivacyDataById(createPolicyStatement.datas(), model);
		what.getDatas().addAll(datas);
		return what;
	}
}
