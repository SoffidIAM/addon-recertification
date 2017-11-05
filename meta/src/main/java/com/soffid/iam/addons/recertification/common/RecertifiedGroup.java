//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class RecertifiedGroup {

	@Nullable
	public java.lang.Long id;

	public com.soffid.iam.addons.recertification.common.ProcessStatus status;

	public java.lang.String group;

	@Nullable
	public java.lang.Integer pctDone;

	public java.lang.Long processId;

	public java.lang.String managerRole;

	@Nullable
	public java.lang.Long workflowId;

}
