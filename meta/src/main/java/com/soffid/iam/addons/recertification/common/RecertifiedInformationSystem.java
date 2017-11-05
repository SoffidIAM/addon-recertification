//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class RecertifiedInformationSystem {

	@Nullable
	public java.lang.Long id;

	public java.lang.String informationSystem;

	@Nullable
	public java.lang.Long processId;

	@Nullable
	public String appOwnerRole;

	@Nullable
	public com.soffid.iam.addons.recertification.common.ProcessStatus status;

	@Nullable
	public java.lang.Integer pctDone;
}
