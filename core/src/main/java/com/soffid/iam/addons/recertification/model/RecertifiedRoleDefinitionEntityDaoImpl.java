//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.recertification.model;

import com.soffid.iam.addons.recertification.common.RecertifiedRoleDefinition;
import com.soffid.iam.model.RoleEntity;

/**
 * DAO RecertifiedRoleDefinitionEntity implementation
 */
public class RecertifiedRoleDefinitionEntityDaoImpl extends RecertifiedRoleDefinitionEntityDaoBase
{

	@Override
	public void toRecertifiedRoleDefinition(
			RecertifiedRoleDefinitionEntity source,
			RecertifiedRoleDefinition target) {
		super.toRecertifiedRoleDefinition(source, target);
		RoleEntity re = getRoleEntityDao().load(source.getRoleId());
		if (re == null)
		{
			target.setRoleName("#"+source.getRoleId());
			target.setDescription("#"+source.getRoleId());
			target.setSystem("??");
		}
		else
		{
			target.setRoleName( re.getName());
			target.setDescription(re.getDescription());
			target.setSystem(re.getSystem().getName());
		}
	}
}
