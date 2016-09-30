//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.model;
import com.soffid.mda.annotation.*;

@Entity (table="SCR_RECUSR" )
@Depends ({com.soffid.iam.addons.recertification.common.RecertifiedUser.class,
	com.soffid.iam.addons.recertification.model.RecertifiedGroupEntity.class,
	com.soffid.iam.addons.recertification.model.RecertifiedRoleEntity.class,
	es.caib.seycon.ng.model.UsuariEntity.class})
public abstract class RecertifiedUserEntity {

	@Column (name="RUS_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="RUS_ACTACC")
	public java.lang.Boolean activeAccount;

	@Column (name="RUS_CREON")
	@Nullable
	public java.util.Date createdOn;

	@Column (name="RUS_USERRW")
	@Nullable
	public java.util.Date userReview;

	@Column (name="RUS_BOSSRW")
	@Nullable
	public java.util.Date bossReview;

	@Column (name="RUS_CISORW")
	@Nullable
	public java.util.Date cisoReview;

	@Column (name="RUS_APOWRW")
	@Nullable
	public java.util.Date appOwnerReview;


	@Column (name="RUS_STATUS")
	public com.soffid.iam.addons.recertification.common.ProcessStatus status;

	@Column (name="RUS_GROUP")
	public com.soffid.iam.addons.recertification.model.RecertifiedGroupEntity group;

	@ForeignKey (foreignColumn="RRO_USER")
	public java.util.Collection<com.soffid.iam.addons.recertification.model.RecertifiedRoleEntity> roles;

	@Column (name="RUS_USER")
	public es.caib.seycon.ng.model.UsuariEntity user;

	@Column (name="RUS_WORKID")
	@Nullable
	public java.lang.Long workflowId;

}
