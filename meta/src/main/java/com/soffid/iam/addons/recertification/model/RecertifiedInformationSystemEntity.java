//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCR_RECIS" )
@Depends ({com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem.class,
	com.soffid.iam.addons.recertification.model.RecertificationProcessEntity.class,
	es.caib.seycon.ng.model.AplicacioEntity.class})
public abstract class RecertifiedInformationSystemEntity {

	@Column (name="RIS_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="RIS_TEN_ID")
	@Nullable
	TenantEntity tenant;

	@Column (name="RIS_PROCES")
	public com.soffid.iam.addons.recertification.model.RecertificationProcessEntity process;

	@Column (name="RIS_IS")
	public es.caib.seycon.ng.model.AplicacioEntity informationSystem;

	@Column (name="RGR_ROLE")
	@Nullable
	public String appOwnerRole;

	@Column (name="RGR_STATUS")
	@Nullable
	public com.soffid.iam.addons.recertification.common.ProcessStatus status;

	@Column (name="RGR_WFID")
	@Nullable
	public java.lang.Long processId;


	@ForeignKey (foreignColumn="RRD_RIS_ID")
	public java.util.Collection<com.soffid.iam.addons.recertification.model.RecertifiedRoleDefinitionEntity> roles;

}
