//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.core;
import org.springframework.transaction.annotation.Transactional;

import com.soffid.iam.addons.recertification.recertification_manage;
import com.soffid.iam.addons.recertification.recertification_query;
import com.soffid.iam.addons.recertification.tothom;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertificationProcessCriteria;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;
import com.soffid.iam.addons.recertification.common.RecertifiedRole;
import com.soffid.iam.addons.recertification.common.RecertifiedRoleDefinition;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.model.RecertificationProcessEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedGroupEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedInformationSystemEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedRoleDefinitionEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedRoleEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedUserEntity;
import com.soffid.mda.annotation.Depends;
import com.soffid.mda.annotation.Operation;
import com.soffid.mda.annotation.Service;

import es.caib.bpm.servei.BpmEngine;
import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.model.RolEntity;
import es.caib.seycon.ng.servei.AccountService;
import es.caib.seycon.ng.servei.AplicacioService;
import es.caib.seycon.ng.servei.GrupService;
import es.caib.seycon.ng.servei.InternalPasswordService;
import es.caib.seycon.ng.servei.UsuariService;

@Service ( translatedName="RecertificationService",
	 translatedPackage="com.soffid.iam.addons.recertification.core")
@Depends ({RecertificationProcessEntity.class,
	RecertifiedGroupEntity.class,
	RecertifiedUserEntity.class,
	RecertifiedRoleEntity.class,
	RecertifiedInformationSystemEntity.class,
	RecertifiedRoleDefinitionEntity.class,
	BpmEngine.class,
	GrupService.class,
	UsuariService.class,
	AplicacioService.class,
	InternalPasswordService.class,
	RolEntity.class,
	AccountService.class})
public abstract class RecertificationService {

	@Operation ( grantees={recertification_manage.class})
	@Transactional(rollbackFor={Exception.class})
	public RecertificationProcess create(
		RecertificationProcess rp)
		throws InternalErrorException {
	 return null;
	}
	
	@Operation ( grantees={recertification_manage.class})
	@Transactional(rollbackFor={Exception.class})
	public RecertificationProcess update(
		RecertificationProcess rp)
		throws InternalErrorException {
	 return null;
	}
	@Operation ( grantees={recertification_manage.class})
	@Transactional(rollbackFor={Exception.class})
	public void remove(
		RecertificationProcess rp)
		throws InternalErrorException {
	}
	@Operation ( grantees={recertification_manage.class})
	@Transactional(rollbackFor={Exception.class})
	public RecertifiedGroup create(
		RecertifiedGroup rg)
		throws InternalErrorException {
	 return null;
	}
	@Operation ( grantees={recertification_manage.class, tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public RecertifiedGroup update(
		RecertifiedGroup rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}

	@Operation ( grantees={recertification_manage.class, tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public RecertifiedUser update(
		RecertifiedUser ru)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}

	@Operation ( grantees={recertification_manage.class})
	@Transactional(rollbackFor={Exception.class})
	public void remove(
		RecertifiedGroup rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_manage.class})
	@Transactional(rollbackFor={Exception.class})
	public RecertifiedInformationSystem create(
		RecertifiedInformationSystem rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={recertification_manage.class, tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public RecertifiedInformationSystem update(
		RecertifiedInformationSystem rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={recertification_manage.class})
	@Transactional(rollbackFor={Exception.class})
	public void remove(
		RecertifiedInformationSystem rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}

	@Operation ( grantees={recertification_manage.class})
	@Transactional(rollbackFor={Exception.class})
	public void startGroupRecertificationProcess(
		RecertificationProcess rp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}

	@Operation ( grantees={recertification_query.class})
	@Transactional(rollbackFor={Exception.class})
	public java.util.List<RecertificationProcess> findRecertificationProcesses(
		RecertificationProcessCriteria q)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={recertification_query.class, tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public RecertificationProcess getRecertificationProcess(
		Long id)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}

	@Operation ( grantees={recertification_query.class})
	@Transactional(rollbackFor={Exception.class})
	public java.util.List<RecertifiedGroup> getRecertifiedGroups(
		RecertificationProcess rp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}

	@Operation ( grantees={recertification_query.class,tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public java.util.List<RecertifiedInformationSystem> getRecertifiedInformationSystems(
		RecertificationProcess rp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={recertification_query.class, tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public java.util.List<RecertifiedUser> getRecertifiedUsers(
		RecertifiedGroup rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={recertification_query.class, tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public java.util.List<RecertifiedUser> getRecertifiedUsers(
		RecertifiedGroup rg, RecertifiedInformationSystem is)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={recertification_query.class,tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public java.util.List<RecertifiedRole> getRecertifiedRoles(
		RecertifiedUser ru)
		throws InternalErrorException {
	 return null;
	}
	@Operation ( grantees={recertification_query.class,tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public java.util.List<RecertifiedRole> getRecertifiedRoles(
		RecertifiedUser ru, RecertifiedInformationSystem is)
		throws InternalErrorException {
	 return null;
	}
	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void applyUserRecertification(
		RecertifiedUser ru)
		throws InternalErrorException {
	}
	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void checkByUser(
		RecertifiedRole rr, 
		boolean check)
		throws InternalErrorException {
	}
	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void checkByBoss(
		RecertifiedRole rr, 
		boolean check)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void checkByAppOwner(
		RecertifiedRole rr, 
		boolean check)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void checkByCiso(
		RecertifiedRole rr, 
		boolean check)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void enableUser(
		RecertifiedUser ru, 
		boolean disabled)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void cancelUserRecertification(
		RecertifiedUser ru)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={recertification_manage.class})
	@Transactional(rollbackFor={Exception.class})
	public void cancelRecertification(
		RecertificationProcess rp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={recertification_manage.class})
	@Transactional(rollbackFor={Exception.class})
	public void cancelGroupRecertification(
		RecertifiedGroup rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void startUserRecertification(
		RecertifiedGroup rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}

	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public RecertifiedInformationSystem getRecertifiedInformationSystem(
		Long id)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}

	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public RecertifiedGroup getRecertifiedGroup(
		Long id)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}

	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public RecertifiedUser getRecertifiedUser(
		Long id)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}

	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void setRecertifiedUserWorkflowId(
		RecertifiedUser ru, 
		Long processId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	
	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void setRecertifiedGroupWorkflowId(
		RecertifiedGroup rg, 
		Long processId)
		throws InternalErrorException {
	}
	
	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void setRecertifiedInformationSystemWorkflowId(
		RecertifiedInformationSystem ris, 
		Long processId)
		throws InternalErrorException {
	}
	
	// Methods for role definition recertification
	@Operation ( grantees={recertification_query.class,tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public java.util.List<RecertifiedRoleDefinition> getRecertifiedRoles(
		RecertifiedInformationSystem ris)
		throws InternalErrorException {
	 return null;
	}

	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void checkByAppOwner(
			RecertifiedRoleDefinition rr, 
		boolean check)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}

	@Operation ( grantees={tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void checkByCiso(
		RecertifiedRoleDefinition rr, 
		boolean check)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}

	@Operation ( grantees={recertification_manage.class})
	@Transactional(rollbackFor={Exception.class})
	public void startInformationSystemRecertificationProcess(
		RecertificationProcess rp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}

	@Operation ( grantees={recertification_manage.class, tothom.class})
	@Transactional(rollbackFor={Exception.class})
	public void applyInformationSystemRecertificationProcess(
		RecertifiedInformationSystem rp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
