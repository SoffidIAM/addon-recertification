//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.recertification.model;

import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertificationType;

/**
 * DAO RecertificationProcessEntity implementation
 */
public class RecertificationProcessEntityDaoImpl extends RecertificationProcessEntityDaoBase
{

	@Override
	public void toRecertificationProcess(RecertificationProcessEntity source,
			RecertificationProcess target) {
		super.toRecertificationProcess(source, target);
		// Incompatible types source.informationSystems and target.informationSystems
		// Incompatible types source.groups and target.groups
		// Missing attribute pctDone on entity
		long done = 0;
		long total = 0;
		if (source.getStatus() == ProcessStatus.PREPARATION)
			target.setPctDone(0);
		else if (source.getStatus() != ProcessStatus.ACTIVE)
			target.setPctDone(100);
		else if (source.getType().equals (RecertificationType.ROLEDEFINITIONS))
		{
			for (RecertifiedInformationSystemEntity is: source.getInformationSystems())
			{
				for (RecertifiedRoleDefinitionEntity role: is.getRoles())
				{
					total ++;
					if (role.isCheckedByOwner())
						done ++;
					if (source.getCisoReview())
					{
						total ++;
						if (role.isCheckedByCiso())
							done++;
					}
				}
			}
			if (total == 0)
				target.setPctDone(100);
			else
				target.setPctDone((int) (100 * done / total));
		}
		else
		{
			for (RecertifiedGroupEntity group: source.getGroups())
			{
				for (RecertifiedUserEntity user: group.getUsers())
				{
					total ++;
					if (group.getStatus() != ProcessStatus.ACTIVE && user.getStatus() != ProcessStatus.ACTIVE )
					{
						done ++;
					}
				}
			}
			if (total == 0)
				target.setPctDone(100);
			else
				target.setPctDone((int) (100 * done / total));
		}
	}

	@Override
	public void remove(RecertificationProcessEntity entity) {
		getRecertifiedInformationSystemEntityDao().remove(entity.getInformationSystems());
		getRecertifiedGroupEntityDao().remove(entity.getGroups());
		super.remove(entity);
	}
	
	
}
