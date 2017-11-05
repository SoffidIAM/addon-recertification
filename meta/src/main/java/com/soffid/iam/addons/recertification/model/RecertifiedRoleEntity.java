//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SCR_RECROL" )
@Depends ({com.soffid.iam.addons.recertification.common.RecertifiedRole.class,
	es.caib.seycon.ng.model.RolAccountEntity.class,
	com.soffid.iam.addons.recertification.model.RecertifiedUserEntity.class})
public abstract class RecertifiedRoleEntity {

	@Column (name="RRO_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="RRO_TEN_ID")
	@Nullable
	TenantEntity tenant;

	@Column (name="RRO_CHUSER")
	@Nullable
	public boolean checkedByUser;

	@Column (name="RRO_CHBOSS")
	@Nullable
	public boolean checkedByBoss;

	@Column (name="RRO_CHAPOW")
	@Nullable
	public boolean checkedByAppOwner;

	@Column (name="RRO_CHCISO")
	@Nullable
	public boolean checkedByCiso;

	@Column (name="RRO_USER")
	public com.soffid.iam.addons.recertification.model.RecertifiedUserEntity user;

	@Column (name="RRO_ROLEID")
	@Description("This is a weak reference to RolAccount")
	public java.lang.Long rolAccountId;

}
