package com.soffid.iam.addons.recertification.model;

import com.soffid.iam.addons.recertification.common.RecertificationTemplate;
import com.soffid.iam.addons.recertification.common.RecertificationType;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.Column;
import com.soffid.mda.annotation.Depends;
import com.soffid.mda.annotation.Entity;
import com.soffid.mda.annotation.Identifier;
import com.soffid.mda.annotation.Index;
import com.soffid.mda.annotation.Nullable;

@Entity(table = "SCV_TEMPLA")
@Depends ({ RecertificationTemplate.class})
public class RecertificationTemplateEntity {
	@Column(name="SCT_ID")
	@Identifier Long id;
	
	@Column(name="SCT_NAME")
	String name;
	
	@Column(name="SCT_TYPE")
	RecertificationType type;
	
	@Column(name="SCT_FILTER", length = 8192)
	@Nullable String filterScript;
	
	@Column(name="SCT_STEP1", length = 8192)
	@Nullable String step1Script;
	
	@Column(name="SCT_STEP2", length = 8192)
	@Nullable String step2Script;
	
	@Column(name="SCT_STEP3", length = 8192)
	@Nullable String step3Script;
	
	@Column(name="SCT_STEP4", length = 8192)
	@Nullable String step4Script;
	
	@Column(name="SCT_MESSAG", length = 32000)
	@Nullable String message;

	@Column (name="SCT_TEN_ID")
	TenantEntity tenant;

	@Column (name="SCT_REMIND")
	@Nullable
	Integer reminder;

	@Column(name="SCT_REMMES", length = 32000)
	@Nullable String reminderMessage;

	@Column (name="SCT_ESCALA")
	@Nullable
	Integer escalation;

	@Column(name="SCT_ESCADR", length = 32000)
	@Nullable String escalationAddress;

	@Column(name="SCT_ESCMES", length = 32000)
	@Nullable String escalationMessage;

	@Column(name="SCT_NOTMES", length = 32000)
	@Nullable String notificationMessage;

	RecertificationTemplateEntity findByName (String name) {return null;}
}

@Index(name="SCR_TEMPLA_UK", entity = RecertificationTemplateEntity.class, columns = {"SCT_TEN_ID", "SCT_NAME"}, unique = true )
class RecertificationTemplateEntityUniqueKey {}
