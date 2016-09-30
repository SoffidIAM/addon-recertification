//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public abstract class RecertifiedRole {

	@Nullable
	public java.lang.Long id;

	public boolean checkedByUser;

	public boolean checkedByBoss;

	public boolean checkedByAppOwner;

	public boolean checkedByCiso;

	public es.caib.seycon.ng.comu.RolAccount rol;

	public Long recertifiedUserId;

}
