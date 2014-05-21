//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.recertification.model;

import org.hibernate.HibernateException;

import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;

import es.caib.seycon.ng.model.AplicacioEntity;

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
		target.setInformationSystem( source.getInformationSystem().getCodi() );
		// Process ID
		target.setProcessId(source.getProcess().getId());
	}

	@Override
	public void recertifiedInformationSystemToEntity(
			RecertifiedInformationSystem source,
			RecertifiedInformationSystemEntity target, boolean copyIfNull) {

		super.recertifiedInformationSystemToEntity(source, target, copyIfNull);
		
		if (target.getInformationSystem( ) == null || ! target.getInformationSystem().getCodi().equals(source.getInformationSystem()))
		{
			AplicacioEntity aplicacio = getAplicacioEntityDao().findByCodi(source.getInformationSystem());
			if (aplicacio == null)
				throw new HibernateException (String.format(Messages.getString("RecertifiedInformationSystemEntityDaoImpl.InvalidIS"), source.getInformationSystem())); //$NON-NLS-1$
			target.setInformationSystem(aplicacio);
		}
		
		target.setProcess(getRecertificationProcessEntityDao().load(source.getProcessId()));
	}
	
	
}
