//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.common;
import com.soffid.iam.addons.recertification.core.RecertificationService;
import com.soffid.mda.annotation.*;

@ValueObject 
public class RecertifiedRoleDefinition {

	@Nullable
	public java.lang.Long id;

	public java.lang.String roleName;
	
	public String system;
	
	public String description;
	
	public Long roleId;

	public boolean checkedByOwner;

	public boolean checkedByCiso;

}
