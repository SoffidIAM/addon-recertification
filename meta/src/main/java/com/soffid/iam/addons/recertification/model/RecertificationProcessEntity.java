//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.model;
import com.soffid.mda.annotation.*;

@Entity (table="SCR_RECPROC" )
@Depends ({com.soffid.iam.addons.recertification.common.RecertificationProcess.class,
	com.soffid.iam.addons.recertification.model.RecertifiedInformationSystemEntity.class,
	com.soffid.iam.addons.recertification.model.RecertifiedGroupEntity.class})
public abstract class RecertificationProcessEntity {

	@Column (name="RPR_NAME")
	public java.lang.String name;

	@Column (name="RPR_STARTD")
	public java.util.Date startDate;

	@Column (name="RPR_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="RPR_WORKID")
	@Nullable
	public java.lang.Long workflowId;

	@Column (name="RPR_ENDDATE")
	@Nullable
	public java.util.Date finishDate;

	@Column (name="RPR_STATUS")
	public com.soffid.iam.addons.recertification.common.ProcessStatus status;
	
	@Column (name="RPR_TYPE", defaultValue="com.soffid.iam.addons.recertification.common.RecertificationType.ENTITLEMENTS")
	@Nullable
	public com.soffid.iam.addons.recertification.common.RecertificationType type;

	@Column (name="RPR_USER")
	@Nullable
	Boolean userReview;

	@Column (name="RPR_APOWRV")
	@Nullable
	Boolean appOwnerReview;

	@Column (name="RPR_CISORV")
	@Nullable
	Boolean cisoReview;

	@Column (name="RPR_CISORO")
	@Nullable
	String cisoRole;
	
	@ForeignKey (foreignColumn="RIS_PROCES")
	public java.util.Collection<com.soffid.iam.addons.recertification.model.RecertifiedInformationSystemEntity> informationSystems;

	@ForeignKey (foreignColumn="RGR_PROCES")
	public java.util.Collection<com.soffid.iam.addons.recertification.model.RecertifiedGroupEntity> groups;

	@Column (name="RPR_MNGROL")
	public java.lang.String managerRole;

	@DaoFinder
	public java.util.List<com.soffid.iam.addons.recertification.model.RecertificationProcessEntity> findByCriteria(
		com.soffid.iam.addons.recertification.common.RecertificationProcessCriteria criteria) {
	 return null;
	}
}
