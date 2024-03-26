//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.recertification.model;

import java.util.Calendar;
import java.util.Collection;

import com.soffid.iam.addons.recertification.common.RecertifiedRole;
import com.soffid.iam.api.RoleAccount;
import com.soffid.iam.model.RoleAccountEntity;

import es.caib.seycon.ng.comu.AccountType;


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
			roleAccount.setGroupDescription("");
			if (roleAccount.getUserGroupCode() != null)
			{
				if (rae.getAccount().getType().equals(AccountType.USER)) {
					for (com.soffid.iam.model.UserAccountEntity usu : rae
							.getAccount().getUsers()) {
						roleAccount.setGroupDescription( usu.getUser().getPrimaryGroup().getDescription() );
 					}
				}				
			}
		}
		boolean finished = true;
		if ( source.getStep1Users() != null && ! source.getStep1Users().trim().isEmpty() )
		{
			if (source.getCheck1() == null)
				finished = false;
			else if ( source.getStep2Users() != null && ! source.getStep2Users().trim().isEmpty() )
			{
				if (source.getCheck2() == null)
					finished = false;
				else if ( source.getStep3Users() != null && ! source.getStep3Users().trim().isEmpty() )
				{
					if (source.getCheck3() == null)
						finished = false;
					else if ( source.getStep4Users() != null && ! source.getStep4Users().trim().isEmpty() )
					{
						if (source.getCheck4() == null)
							finished = false;						
					}
				}
			}
		}
		if (!finished && 
				source.getAssignationDate() != null &&  
				source.getUser().getGroup().getProcess().getTemplate().getEscalation() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(source.getAssignationDate());
			c.add(Calendar.DAY_OF_MONTH, source.getUser().getGroup().getProcess().getTemplate().getEscalation().intValue());
			target.setLimitDate(c.getTime());
		}
		target.setFinished(finished);
		if (source.getUser() != null)
			target.setRecertifiedUserId(source.getUser().getId());
		if (source.getInformationSystem() != null)
			target.setRecertifiedISId(source.getInformationSystem().getId());
	}

	@Override
	public void recertifiedRoleToEntity(RecertifiedRole source,
			RecertifiedRoleEntity target, boolean copyIfNull) {
		super.recertifiedRoleToEntity(source, target, copyIfNull);
	}

	@Override
	public Collection<RecertifiedRoleEntity> findByProcessId(long id) {
		Collection<RecertifiedRoleEntity> l = findByProcessIdAndGroup(id);
		if (l == null || l.isEmpty())
			l = findByProcessIdAndIS(id);
		return l;
	}
}
