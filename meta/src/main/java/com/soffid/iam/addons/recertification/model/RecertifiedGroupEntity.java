//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCR_RECGRO" )
@Depends ({com.soffid.iam.addons.recertification.common.RecertifiedGroup.class,
	com.soffid.iam.addons.recertification.model.RecertificationProcessEntity.class,
	com.soffid.iam.addons.recertification.model.RecertifiedUserEntity.class,
	es.caib.seycon.ng.model.GrupEntity.class})
public abstract class RecertifiedGroupEntity {

	@Column (name="RGR_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="RGR_TEN_ID")
	@Nullable
	TenantEntity tenant;

	@Column (name="RGR_STATUS")
	@Nullable
	public com.soffid.iam.addons.recertification.common.ProcessStatus status;

	@Column (name="RGR_PROCES")
	public com.soffid.iam.addons.recertification.model.RecertificationProcessEntity process;

	@ForeignKey (foreignColumn="RUS_GROUP")
	public java.util.Collection<com.soffid.iam.addons.recertification.model.RecertifiedUserEntity> users;

	@Column (name="RGR_GROUP")
	public es.caib.seycon.ng.model.GrupEntity group;

	@Column (name="RGR_MNGROL")
	public java.lang.String managerRole;

	@Column (name="RGR_WROKID")
	@Nullable
	public java.lang.Long workflowId;

}
