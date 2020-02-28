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

@Entity (table="SCV_RECROL" )
@Depends ({com.soffid.iam.addons.recertification.common.RecertifiedRole.class,
	es.caib.seycon.ng.model.RolAccountEntity.class,
	com.soffid.iam.addons.recertification.model.RecertifiedUserEntity.class})
public abstract class RecertifiedRoleEntity {

	@Column (name="RRO_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="RRO_TEN_ID")
	TenantEntity tenant;

	@Column (name="RRO_CHUSER")
	@Nullable
	public Boolean check1;

	@Column (name="RRO_CHBOSS")
	@Nullable
	public Boolean check2;

	@Column (name="RRO_CHAPOW")
	@Nullable
	public Boolean check3;

	@Column (name="RRO_CHCISO")
	@Nullable
	public Boolean check4;

	@Nullable @Column (name="RRO_USER")
	public com.soffid.iam.addons.recertification.model.RecertifiedUserEntity user;

	@Nullable @Column (name="RRO_RIS_ID", reverseAttribute = "grants")
	public com.soffid.iam.addons.recertification.model.RecertifiedInformationSystemEntity informationSystem;

	@Column (name="RRO_ROLEID")
	@Description("This is a weak reference to RolAccount")
	public java.lang.Long rolAccountId;

	@Column (name="RRO_STEP1", length=255) @Nullable
	public String step1Users;

	@Column (name="RRO_STEP2", length=255) @Nullable
	public String step2Users;

	@Column (name="RRO_STEP3", length=255) @Nullable
	public String step3Users;

	@Column (name="RRO_STEP4", length=255) @Nullable
	public String step4Users;

	@Column (name="RRO_STEP1O", length=255) @Nullable
	public String step1UsersOrig;

	@Column (name="RRO_STEP2O", length=255) @Nullable
	public String step2UsersOrig;

	@Column (name="RRO_STEP3O", length=255) @Nullable
	public String step3UsersOrig;

	@Column (name="RRO_STEP4O", length=255) @Nullable
	public String step4UsersOrig;

	@Column (name="RRO_AUTHOR1", length=255) @Nullable
	public String step1Author;

	@Column (name="RRO_AUTHOR2", length=255) @Nullable
	public String step2Author;

	@Column (name="RRO_AUTHOR3", length=255) @Nullable
	public String step3Author;

	@Column (name="RRO_AUTHOR4", length=255) @Nullable
	public String step4Author;

	@Column (name="RRO_DATE1", length=255) @Nullable
	public Date step1Date;

	@Column (name="RRO_DATE2", length=255) @Nullable
	public Date step2Date;

	@Column (name="RRO_DATE3", length=255) @Nullable
	public Date step3Date;

	@Column (name="RRO_DATE4", length=255) @Nullable
	public Date step4Date;

	@Column (name="RRO_COMMEN", length=1024) @Nullable
	public String comments;

	@DaoFinder("select a "
			+ "from com.soffid.iam.addons.recertification.model.RecertifiedRoleEntity as a "
			+ "where a.user.group.process.id=:id")
	Collection<RecertifiedRoleEntity> findByProcessIdAndGroup (long id) { return null; }

	@DaoFinder("select a "
			+ "from com.soffid.iam.addons.recertification.model.RecertifiedRoleEntity as a "
			+ "where a.informationSystem.process.id=:id")
	Collection<RecertifiedRoleEntity> findByProcessIdAndIS (long id) { return null; }

	@DaoFinder("select a "
			+ "from com.soffid.iam.addons.recertification.model.RecertifiedRoleEntity as a "
			+ "where a.user.group.process.id=:id")
	Collection<RecertifiedRoleEntity> findByProcessId (long id) { return null; }
}
