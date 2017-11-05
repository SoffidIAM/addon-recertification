//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.recertification.model;

import com.soffid.iam.addons.recertification.common.RecertifiedRole;
import com.soffid.iam.api.RoleAccount;
import com.soffid.iam.model.RoleAccountEntity;


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
		RoleAccountEntity rae = getRoleAccountEntityDao().load(source.getRolAccountId());
		if (rae != null)
		{
			RoleAccount roleAccount = getRoleAccountEntityDao().toRoleAccount(rae);
			target.setRol(roleAccount);
		}
		target.setRecertifiedUserId(source.getUser().getId());
	}

	@Override
	public void recertifiedRoleToEntity(RecertifiedRole source,
			RecertifiedRoleEntity target, boolean copyIfNull) {
		super.recertifiedRoleToEntity(source, target, copyIfNull);
	}
}
