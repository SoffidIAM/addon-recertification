//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.recertification.model;

import com.soffid.iam.addons.recertification.common.RecertifiedRoleDefinition;

import es.caib.seycon.ng.model.RolEntity;

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
		RolEntity re = getRolEntityDao().load(source.getRoleId());
		if (re == null)
		{
			target.setRoleName("#"+source.getRoleId());
			target.setDescription("#"+source.getRoleId());
			target.setSystem("??");
		}
		else
		{
			target.setRoleName( re.getNom());
			target.setDescription(re.getDescripcio());
			target.setSystem(re.getBaseDeDades().getCodi());
		}
	}
}
