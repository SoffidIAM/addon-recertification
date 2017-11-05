//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class RecertifiedUser {

	@Nullable
	public java.lang.Long id;

	public java.lang.Boolean activeAccount;

	@Nullable
	public java.util.Calendar createdOn;

	@Nullable
	public java.util.Calendar userReview;

	@Nullable
	public java.util.Calendar bossReview;

	@Nullable
	public java.util.Calendar appOwnerReview;

	@Nullable
	public java.util.Calendar cisoReview;

	public com.soffid.iam.addons.recertification.common.ProcessStatus status;

	public java.lang.String user;

	public java.lang.Long recertifiedGroupId;

	@Nullable
	public java.lang.Long workflowId;

}
