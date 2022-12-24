//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.common;
import java.util.Date;

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

	@Description("Current check")
	@Nullable
	public Boolean check;

	@Nullable
	public Boolean check1;

	@Nullable
	public Boolean check2;

	@Nullable
	public Boolean check3;

	@Nullable
	public Boolean check4;

	@Nullable
	public String step1Users;

	@Nullable
	public String step2Users;

	@Nullable
	public String step3Users;

	@Nullable
	public String step4Users;

	@Nullable
	public String step1UsersOrig;

	@Nullable
	public String step2UsersOrig;

	@Nullable
	public String step3UsersOrig;

	@Nullable
	public String step4UsersOrig;

	@Nullable
	public String step1Author;

	@Nullable
	public String step2Author;

	@Nullable
	public String step3Author;

	@Nullable
	public String step4Author;

	@Nullable
	public Date step1Date;

	@Nullable
	public Date step2Date;

	@Nullable
	public Date step3Date;

	@Nullable
	public Date step4Date;

	@Nullable
	public String comments;
	
	@Nullable
	public String newComments;

	public boolean finished;

}
