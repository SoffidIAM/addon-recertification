//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.model;
import java.util.Collection;
import java.util.Date;

import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCV_RECROD" )
@Depends ({com.soffid.iam.addons.recertification.common.RecertifiedRoleDefinition.class,
	es.caib.seycon.ng.model.RolEntity.class})
public abstract class RecertifiedRoleDefinitionEntity {

	@Column (name="RRD_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="RRD_TEN_ID")
	TenantEntity tenant;
	
	@Column (name="RRD_RIS_ID")
	RecertifiedInformationSystemEntity informationSystem;
	
	@Column (name="RRD_CHUSER")
	@Nullable
	public Boolean check1;

	@Column (name="RRD_CHBOSS")
	@Nullable
	public Boolean check2;

	@Column (name="RRD_CHAPOW")
	@Nullable
	public Boolean check3;

	@Column (name="RRD_CHCISO")
	@Nullable
	public Boolean check4;

	@Column (name="RRD_ROLE")
	public Long roleId;
	
	@Column (name="RRD_STEP1", length=255) @Nullable
	public String step1Users;

	@Column (name="RRD_STEP2", length=255) @Nullable
	public String step2Users;

	@Column (name="RRD_STEP3", length=255) @Nullable
	public String step3Users;

	@Column (name="RRD_STEP4", length=255) @Nullable
	public String step4Users;

	@Column (name="RRD_STEP1O", length=255) @Nullable
	public String step1UsersOrig;

	@Column (name="RRD_STEP2O", length=255) @Nullable
	public String step2UsersOrig;


	@Column (name="RRD_STEP3O", length=255) @Nullable
	public String step3UsersOrig;

	@Column (name="RRD_STEP4O", length=255) @Nullable
	public String step4UsersOrig;

	@Column (name="RRD_AUTHOR1", length=255) @Nullable
	public String step1Author;

	@Column (name="RRD_AUTHOR2", length=255) @Nullable
	public String step2Author;

	@Column (name="RRD_AUTHOR3", length=255) @Nullable
	public String step3Author;

	@Column (name="RRD_AUTHOR4", length=255) @Nullable
	public String step4Author;

	@Column (name="RRD_DATE1", length=255) @Nullable
	public Date step1Date;

	@Column (name="RRD_DATE2", length=255) @Nullable
	public Date step2Date;

	@Column (name="RRD_DATE3", length=255) @Nullable
	public Date step3Date;

	@Column (name="RRD_DATE4", length=255) @Nullable
	public Date step4Date;

	@Column (name="RRD_COMMEN", length=1024) @Nullable
	public String comments;

	@DaoFinder("select a "
			+ "from com.soffid.iam.addons.recertification.model.RecertifiedRoleDefinitionEntity as a "
			+ "where a.informationSystem.process.id=:id")
	Collection<RecertifiedRoleDefinitionEntity> findByProcessId (long id) { return null; }

}
