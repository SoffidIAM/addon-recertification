//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.recertification.model;

import org.hibernate.HibernateException;

import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationType;
import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;
import com.soffid.iam.model.InformationSystemEntity;

/**
 * DAO RecertifiedInformationSystemEntity implementation
 */
public class RecertifiedInformationSystemEntityDaoImpl extends RecertifiedInformationSystemEntityDaoBase
{

	@Override
	public void toRecertifiedInformationSystem(
			RecertifiedInformationSystemEntity source,
			RecertifiedInformationSystem target) {
		super.toRecertifiedInformationSystem(source, target);
		// Information System
		target.setInformationSystem( source.getInformationSystem().getName() );
		// Process ID
		target.setProcessId(source.getProcess().getId());
		// Calculate progress
		long done = 0;
		long total = 0;
		if (source.getProcess().getType().equals(RecertificationType.ENTITLEMENTS))
			target.setPctDone(null);
		else if (source.getStatus() == ProcessStatus.PREPARATION)
			target.setPctDone(0);
		else if (source.getStatus() != ProcessStatus.ACTIVE)
			target.setPctDone(100);
		else if (source.getProcess().getType().equals(RecertificationType.ACCOUNTS))
		{
			for (RecertifiedRoleEntity role: source.getGrants())
			{
				if (role.getStep1Users() != null)
				{
					total ++;
					if (role.getCheck1() != null) done++;
				}
				if (role.getStep2Users() != null)
				{
					total ++;
					if (role.getCheck2() != null) done++;
				}
				if (role.getStep3Users() != null)
				{
					total ++;
					if (role.getCheck3() != null) done++;
				}
				if (role.getStep4Users() != null)
				{
					total ++;
					if (role.getCheck4() != null) done++;
				}
			}
			if (total == 0)
				target.setPctDone(100);
			else
				target.setPctDone((int) (100 * done / total));			
		}
		else 
		{
			for (RecertifiedRoleDefinitionEntity role: source.getRoles())
			{
				total ++;
				if (role.isCheckedByOwner())
					done ++;
				if (source.getProcess().getCisoReview())
				{
					total ++;
					if (role.isCheckedByCiso())
						done++;
				}
			}
			if (total == 0)
				target.setPctDone(100);
			else
				target.setPctDone((int) (100 * done / total));
		}

	}

	@Override
	public void recertifiedInformationSystemToEntity(
			RecertifiedInformationSystem source,
			RecertifiedInformationSystemEntity target, boolean copyIfNull) {

		super.recertifiedInformationSystemToEntity(source, target, copyIfNull);
		
		if (target.getInformationSystem( ) == null || ! target.getInformationSystem().getName().equals(source.getInformationSystem()))
		{
			InformationSystemEntity aplicacio = getInformationSystemEntityDao().findByCode(source.getInformationSystem());
			if (aplicacio == null)
				throw new HibernateException (String.format(Messages.getString("RecertifiedInformationSystemEntityDaoImpl.InvalidIS"), source.getInformationSystem())); //$NON-NLS-1$
			target.setInformationSystem(aplicacio);
		}
		
		target.setProcess(getRecertificationProcessEntityDao().load(source.getProcessId()));
	}
	
	
}
