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

	@Attribute(defaultValue = "\"<p>Dear ${fullName}</p><p>This is a reminder to complete the <a href='${url}'>review process</a>\"")
	@Nullable String reminderMessage;

	@Nullable
	Integer escalation;

	@Nullable String escalationAddress;
	
	@Attribute(defaultValue = "\"<p>Dear ${fullName}</p><p>"+
			"Some recertification process has reached the deadline. "+
			"Please, follow link to complete the <a href='${url}'>review process</a>\"")
	@Nullable String escalationMessage;

	@Attribute(defaultValue = "\"<p>Dear ${userFullName}</p>"
			+ "<p>"
			+ "The system administrator has performed a timely permission review of your permissions.</p>"
			+ "<p>As a result of this review, the permission ${roleDescription} has been removed.</p>\"")
	@Nullable String notificationMessage;



}
