//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCR_RECROD" )
@Depends ({com.soffid.iam.addons.recertification.common.RecertifiedRoleDefinition.class,
	es.caib.seycon.ng.model.RolEntity.class})
public abstract class RecertifiedRoleDefinitionEntity {

	@Column (name="RRD_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="RRD_TEN_ID")
	@Nullable
	TenantEntity tenant;
	
	@Column (name="RRD_RIS_ID")
	RecertifiedInformationSystemEntity informationSystem;
	
	@Column (name="RRD_CHOWN")
	public boolean checkedByOwner;

	@Column (name="RRD_CHCISO")
	public boolean checkedByCiso;

	@Column (name="RRO_ROLE")
	public Long roleId;

}
