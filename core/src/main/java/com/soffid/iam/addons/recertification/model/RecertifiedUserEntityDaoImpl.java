//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.recertification.model;

import com.soffid.iam.addons.recertification.common.RecertifiedUser;

/**
 * DAO RecertifiedUserEntity implementation
 */
public class RecertifiedUserEntityDaoImpl extends RecertifiedUserEntityDaoBase
{

	@Override
	public void toRecertifiedUser(RecertifiedUserEntity source,
			RecertifiedUser target) {
		super.toRecertifiedUser(source, target);
		// Incompatible types source.user and target.user
		// Missing attribute recertifiedGroupId on entity
		target.setUser(source.getUser().getUserName());
		target.setRecertifiedGroupId(source.getGroup().getId());
	}
	
	@Override
	public void remove(RecertifiedUserEntity entity) {
		getRecertifiedRoleEntityDao().remove(entity.getRoles());
		super.remove(entity);
	}

}
