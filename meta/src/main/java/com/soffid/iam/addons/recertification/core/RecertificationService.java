//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.core;
import com.soffid.iam.addons.recertification.tothom;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( translatedName="RecertificationService",
	 translatedPackage="com.soffid.iam.addons.recertification.core")
@Depends ({com.soffid.iam.addons.recertification.model.RecertificationProcessEntity.class,
	com.soffid.iam.addons.recertification.model.RecertifiedGroupEntity.class,
	com.soffid.iam.addons.recertification.model.RecertifiedUserEntity.class,
	com.soffid.iam.addons.recertification.model.RecertifiedRoleEntity.class,
	com.soffid.iam.addons.recertification.model.RecertifiedInformationSystemEntity.class,
	es.caib.bpm.servei.BpmEngine.class,
	es.caib.seycon.ng.servei.GrupService.class,
	es.caib.seycon.ng.servei.UsuariService.class,
	es.caib.seycon.ng.servei.AplicacioService.class,
	es.caib.seycon.ng.servei.InternalPasswordService.class,
	es.caib.seycon.ng.servei.AccountService.class})
public abstract class RecertificationService {

	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.recertification.common.RecertificationProcess create(
		com.soffid.iam.addons.recertification.common.RecertificationProcess rp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.recertification.common.RecertificationProcess update(
		com.soffid.iam.addons.recertification.common.RecertificationProcess rp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void remove(
		com.soffid.iam.addons.recertification.common.RecertificationProcess rp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.recertification.common.RecertifiedGroup create(
		com.soffid.iam.addons.recertification.common.RecertifiedGroup rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.recertification.common.RecertifiedGroup update(
		com.soffid.iam.addons.recertification.common.RecertifiedGroup rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void remove(
		com.soffid.iam.addons.recertification.common.RecertifiedGroup rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem create(
		com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem update(
		com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void remove(
		com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void startGroupRecertificationProcess(
		com.soffid.iam.addons.recertification.common.RecertificationProcess rp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<com.soffid.iam.addons.recertification.common.RecertificationProcess> findRecertificationProcesses(
		com.soffid.iam.addons.recertification.common.RecertificationProcessCriteria q)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_query.class, tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.recertification.common.RecertificationProcess getRecertificationProcess(
		java.lang.Long id)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<com.soffid.iam.addons.recertification.common.RecertifiedGroup> getRecertifiedGroups(
		com.soffid.iam.addons.recertification.common.RecertificationProcess rp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_query.class,tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem> getRecertifiedInformationSystems(
		com.soffid.iam.addons.recertification.common.RecertificationProcess rp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_query.class, tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<com.soffid.iam.addons.recertification.common.RecertifiedUser> getRecertifiedUsers(
		com.soffid.iam.addons.recertification.common.RecertifiedGroup rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_query.class,com.soffid.iam.addons.recertification.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<com.soffid.iam.addons.recertification.common.RecertifiedRole> getRecertifiedRoles(
		com.soffid.iam.addons.recertification.common.RecertifiedUser ru)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void applyUserRecertification(
		com.soffid.iam.addons.recertification.common.RecertifiedUser ru)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void checkByUser(
		com.soffid.iam.addons.recertification.common.RecertifiedRole rr, 
		boolean check)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void checkByBoss(
		com.soffid.iam.addons.recertification.common.RecertifiedRole rr, 
		boolean check)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void enableUser(
		com.soffid.iam.addons.recertification.common.RecertifiedUser ru, 
		boolean disabled)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void cancelUserRecertification(
		com.soffid.iam.addons.recertification.common.RecertifiedUser ru)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void cancelRecertification(
		com.soffid.iam.addons.recertification.common.RecertificationProcess rp)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.recertification_manage.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void cancelGroupRecertification(
		com.soffid.iam.addons.recertification.common.RecertifiedGroup rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void startUserRecertification(
		com.soffid.iam.addons.recertification.common.RecertifiedGroup rg)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.recertification.common.RecertifiedGroup getRecertifiedGroup(
		java.lang.Long id)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.recertification.common.RecertifiedUser getRecertifiedUser(
		java.lang.Long id)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void setRecertifiedUserWorkflowId(
		com.soffid.iam.addons.recertification.common.RecertifiedUser ru, 
		java.lang.Long processId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.recertification.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void setRecertifiedGroupWorkflowId(
		com.soffid.iam.addons.recertification.common.RecertifiedGroup rg, 
		java.lang.Long processId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
