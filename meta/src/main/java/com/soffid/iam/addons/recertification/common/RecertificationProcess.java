//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class RecertificationProcess {

	public java.lang.String name;

	@Nullable
	public String template;
	
	public java.util.Calendar startDate;

	@Nullable
	public java.lang.Long id;

	@Nullable
	public java.lang.Long workflowId;

	@Nullable
	public java.util.Calendar finishDate;

	public com.soffid.iam.addons.recertification.common.ProcessStatus status;

	@Nullable
	public java.lang.Integer pctDone;

	public java.lang.String managerRole;
	
	@Attribute(defaultValue="RecertificationType.ENTITLEMENTS")
	RecertificationType type;

	@Nullable
	Boolean appOwnerReview;
	
	@Nullable
	Boolean cisoReview;

	@Nullable
	Boolean userReview;

	@Nullable
	String cisoRole;
}
