//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.common;
import com.soffid.mda.annotation.*;

@Criteria 
public abstract class RecertificationProcessCriteria {

	@Nullable
	public java.lang.String name;

	@Nullable
	public java.util.Calendar fromDate;

	@Nullable
	public java.lang.Long workflowId;

	@Nullable
	public java.util.Calendar toDate;

	@Nullable
	public com.soffid.iam.addons.recertification.common.ProcessStatus status;

	@Nullable
	public java.lang.String group;

	@Nullable
	public java.lang.String informationSystem;

}
