package com.soffid.iam.addons.recertification.common;

import com.soffid.iam.addons.recertification.common.RecertificationType;
import com.soffid.mda.annotation.Attribute;
import com.soffid.mda.annotation.Column;
import com.soffid.mda.annotation.Identifier;
import com.soffid.mda.annotation.Nullable;
import com.soffid.mda.annotation.ValueObject;

@ValueObject
public class RecertificationTemplate {
	@Identifier @Nullable Long id;
	
	String name;
	
	@Attribute( defaultValue = "com.soffid.iam.addons.recertification.common.RecertificationType.ENTITLEMENTS")
	RecertificationType type;
	
	@Nullable String filterScript;
	
	@Nullable String step1Script;
	
	@Nullable String step2Script;
	
	@Nullable String step3Script;
	
	@Nullable String step4Script;

	@Attribute(defaultValue = "\"<p>Dear ${fullName}</p><p>Follow this link to complete the <a href='${url}'>review process</a>\"")
	@Nullable String message;
	

	@Nullable
	Integer reminder;

	@Nullable String reminderMessage;

	@Nullable
	Integer escalation;

	@Nullable String escalationAddress;

}
