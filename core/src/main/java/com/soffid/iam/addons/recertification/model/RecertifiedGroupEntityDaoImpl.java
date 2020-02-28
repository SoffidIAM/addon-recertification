//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.recertification.model;

import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;

/**
 * DAO RecertifiedGroupEntity implementation
 */
public class RecertifiedGroupEntityDaoImpl extends RecertifiedGroupEntityDaoBase
{

	@Override
	public void toRecertifiedGroup(RecertifiedGroupEntity source,
			RecertifiedGroup target) {
		// Incompatible types source.group and target.group
		// Missing attribute pctDone on entity
		super.toRecertifiedGroup(source, target);
		
		if (source.getStatus() == ProcessStatus.PREPARATION)
			target.setPctDone(0);
		else if (source.getStatus() != ProcessStatus.ACTIVE)
			target.setPctDone(100);
		else
		{
			long total = 0;
			long done = 0;
			for (RecertifiedUserEntity user: source.getUsers())
			{
				for ( RecertifiedRoleEntity role: user.getRoles())
				{
					if (role.getStep1Users() != null && !role.getStep1Users().trim().isEmpty())
						total ++;
					if (role.getStep2Users() != null && !role.getStep2Users().trim().isEmpty())
						total ++;
					if (role.getStep3Users() != null && !role.getStep3Users().trim().isEmpty())
						total ++;
					if (role.getStep4Users() != null && !role.getStep4Users().trim().isEmpty())
						total ++;
					if (role.getCheck1() != null)
						done ++;
					if (role.getCheck2() != null)
						done ++;
					if (role.getCheck3() != null)
						done ++;
					if (role.getCheck4() != null)
						done ++;
				}
			}
			if (total == 0)
				target.setPctDone(100);
			else
				target.setPctDone((int) (100 * done / total));
		}
		target.setProcessId(source.getProcess().getId());
		target.setGroup(source.getGroup().getName());
	}
	
	@Override
	public void remove(RecertifiedGroupEntity entity) {
		getRecertifiedUserEntityDao().remove(entity.getUsers());
		super.remove(entity);
	}
	/* (non-Javadoc)
	 * @see com.soffid.iam.addons.recertification.model.RecertifiedGroupEntityDaoBase#recertifiedGroupToEntity(com.soffid.iam.addons.recertification.common.RecertifiedGroup, com.soffid.iam.addons.recertification.model.RecertifiedGroupEntity, boolean)
	 */
	@Override
	public void recertifiedGroupToEntity(RecertifiedGroup source,
			RecertifiedGroupEntity target, boolean copyIfNull) {
		super.recertifiedGroupToEntity(source, target, copyIfNull);
		if (source.getProcessId() == null)
			target.setProcess(null);
		else
			target.setProcess(getRecertificationProcessEntityDao().load(source.getProcessId()));
		
		if (source.getGroup() == null)
			target.setGroup(null);
		else
			target.setGroup (getGroupEntityDao().findByName(source.getGroup()));
	}
}
