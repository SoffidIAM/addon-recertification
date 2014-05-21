//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public abstract class RecertifiedInformationSystem {

	@Nullable
	public java.lang.Long id;

	public java.lang.String informationSystem;

	public java.lang.Long processId;

}
