//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public abstract class RecertificationProcess {

	public java.lang.String name;

	public java.util.Calendar startDate;

	@Nullable
	public java.lang.Long id;

	@Nullable
	public java.lang.Long workflowId;

	@Nullable
	public java.util.Calendar finishDate;

	public com.soffid.iam.addons.recertification.common.ProcessStatus status;

	public java.lang.Integer pctDone;

	public java.lang.String managerRole;

}
