//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.recertification.model;

import com.soffid.iam.addons.recertification.common.RecertifiedRole;

import es.caib.seycon.ng.comu.RolAccount;
import es.caib.seycon.ng.model.RolAccountEntity;

/**
 * DAO RecertifiedRoleEntity implementation
 */
public class RecertifiedRoleEntityDaoImpl extends RecertifiedRoleEntityDaoBase
{

	@Override
	public void toRecertifiedRole(RecertifiedRoleEntity source,
			RecertifiedRole target) {
		super.toRecertifiedRole(source, target);
		// Incompatible types source.rol and target.rol
		// Missing attribute processId on entity
		RolAccountEntity rae = getRolAccountEntityDao().load(source.getRolAccountId());
		RolAccount rolAccount = getRolAccountEntityDao().toRolAccount(rae);
		target.setRol(rolAccount);
		target.setRecertifiedUserId(source.getUser().getId());
	}

	@Override
	public void recertifiedRoleToEntity(RecertifiedRole source,
			RecertifiedRoleEntity target, boolean copyIfNull) {
		super.recertifiedRoleToEntity(source, target, copyIfNull);
	}
}
