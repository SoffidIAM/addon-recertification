/**
 * 
 */
package com.soffid.iam.addons.recertification.core;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jbpm.JbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ProcessInstance;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.model.RecertifiedRoleEntity;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertificationProcessCriteria;
import com.soffid.iam.addons.recertification.common.RecertificationType;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;
import com.soffid.iam.addons.recertification.common.RecertifiedRole;
import com.soffid.iam.addons.recertification.common.RecertifiedRoleDefinition;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.model.RecertificationProcessEntity;
import com.soffid.iam.addons.recertification.model.RecertificationProcessEntityDao;
import com.soffid.iam.addons.recertification.model.RecertifiedGroupEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedGroupEntityDao;
import com.soffid.iam.addons.recertification.model.RecertifiedInformationSystemEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedInformationSystemEntityDao;
import com.soffid.iam.addons.recertification.model.RecertifiedRoleDefinitionEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedUserEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedUserEntityDao;
import com.soffid.iam.api.BpmUserProcess;
import com.soffid.iam.api.RoleAccount;
import com.soffid.iam.api.RoleGrant;
import com.soffid.iam.api.User;
import com.soffid.iam.model.GroupEntity;
import com.soffid.iam.model.RoleEntity;
import com.soffid.iam.model.UserEntity;

import es.caib.seycon.ng.comu.Account;
import es.caib.seycon.ng.comu.RolAccount;
import es.caib.seycon.ng.comu.RolGrant;
import es.caib.seycon.ng.comu.Usuari;
import es.caib.seycon.ng.comu.UsuariWFProcess;
import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.utils.Security;

/**
 * @author bubu
 *
 */
public class RecertificationServiceImpl extends RecertificationServiceBase {

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleCreate(com.soffid.iam.recertification.common.RecertificationProcess)
	 */
	@Override
	protected RecertificationProcess handleCreate(RecertificationProcess rp)
			throws Exception {
		RecertificationProcessEntityDao dao = getRecertificationProcessEntityDao();
		RecertificationProcessEntity e = dao.recertificationProcessToEntity(rp);
		dao.create(e);
		return dao.toRecertificationProcess(e);
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleUpdate(com.soffid.iam.recertification.common.RecertificationProcess)
	 */
	@Override
	protected RecertificationProcess handleUpdate(RecertificationProcess rp)
			throws Exception {
		RecertificationProcessEntityDao dao = getRecertificationProcessEntityDao();
		RecertificationProcessEntity e = dao.recertificationProcessToEntity(rp);
		dao.update(e);
		return dao.toRecertificationProcess(e);
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleRemove(com.soffid.iam.recertification.common.RecertificationProcess)
	 */
	@Override
	protected void handleRemove(RecertificationProcess rp) throws Exception {
		RecertificationProcessEntityDao dao = getRecertificationProcessEntityDao();
		RecertificationProcessEntity e = dao.load(rp.getId());
		if (e.getStatus() != ProcessStatus.PREPARATION)
		{
			throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NotRemove")); //$NON-NLS-1$
		}
		dao.remove(e);
		
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleCreate(com.soffid.iam.recertification.common.RecertifiedGroup)
	 */
	@Override
	protected RecertifiedGroup handleCreate(RecertifiedGroup rg)
			throws Exception {
		RecertificationProcessEntity process = getRecertificationProcessEntityDao().load(rg.getProcessId());
		if (process.getStatus() != ProcessStatus.PREPARATION)
		{
			throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NotRemove")); //$NON-NLS-1$
		}

		RecertifiedGroupEntityDao dao = getRecertifiedGroupEntityDao();
		RecertifiedGroupEntity e = dao.recertifiedGroupToEntity(rg);
		dao.create(e);
		return dao.toRecertifiedGroup(e);
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleUpdate(com.soffid.iam.recertification.common.RecertifiedGroup)
	 */
	@Override
	protected RecertifiedGroup handleUpdate(RecertifiedGroup rg)
			throws Exception {
		RecertifiedGroupEntityDao dao = getRecertifiedGroupEntityDao();
		RecertifiedGroupEntity e = dao.load(rg.getId());
		if (e.getProcess().getStatus() == ProcessStatus.PREPARATION)
		{
			e = dao.recertifiedGroupToEntity(rg);
			dao.update(e);
		} else if (e.getProcess().getStatus() == ProcessStatus.ACTIVE) {
			e.setStatus(rg.getStatus());
		} else if (e.getProcess().getStatus() == ProcessStatus.FINISHED) {
			// Ignore
		} else {
			throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NotUpdate")); //$NON-NLS-1$
		}
		dao.update(e);
		return dao.toRecertifiedGroup(e);
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleRemove(com.soffid.iam.recertification.common.RecertifiedGroup)
	 */
	@Override
	protected void handleRemove(RecertifiedGroup rg) throws Exception {
		RecertifiedGroupEntityDao dao = getRecertifiedGroupEntityDao();
		RecertifiedGroupEntity e = dao.load(rg.getId());
		
		if (e.getProcess().getStatus() != ProcessStatus.PREPARATION)
		{
			throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NotRemove")); //$NON-NLS-1$
		}
		dao.remove(rg.getId());
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleCreate(com.soffid.iam.recertification.common.RecertifiedInformationSystem)
	 */
	@Override
	protected RecertifiedInformationSystem handleCreate(
			RecertifiedInformationSystem rg) throws Exception {
		RecertificationProcessEntity process = getRecertificationProcessEntityDao().load(rg.getProcessId());
		if (process.getStatus() != ProcessStatus.PREPARATION)
		{
			throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NotRemove")); //$NON-NLS-1$
		}
		RecertifiedInformationSystemEntityDao dao = getRecertifiedInformationSystemEntityDao();
		RecertifiedInformationSystemEntity e = dao.recertifiedInformationSystemToEntity(rg);
		dao.create(e);
		return dao.toRecertifiedInformationSystem(e);
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleUpdate(com.soffid.iam.recertification.common.RecertifiedInformationSystem)
	 */
	@Override
	protected RecertifiedInformationSystem handleUpdate(
			RecertifiedInformationSystem rg) throws Exception {
		RecertifiedInformationSystemEntityDao dao = getRecertifiedInformationSystemEntityDao();
		RecertifiedInformationSystemEntity e = dao.recertifiedInformationSystemToEntity(rg);
		if (e.getProcess().getStatus() == ProcessStatus.FINISHED)
		{
			throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NotRemove")); //$NON-NLS-1$
		}
		dao.update(e);
		return dao.toRecertifiedInformationSystem(e);
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleRemove(com.soffid.iam.recertification.common.RecertifiedInformationSystem)
	 */
	@Override
	protected void handleRemove(RecertifiedInformationSystem rg)
			throws Exception {
		RecertifiedInformationSystemEntityDao dao = getRecertifiedInformationSystemEntityDao();
		dao.remove (rg.getId());
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleStartRecertificationProcess(com.soffid.iam.recertification.common.RecertificationProcess)
	 */
	@Override
	protected void handleStartGroupRecertificationProcess(RecertificationProcess rp)
			throws Exception {
		RecertificationProcessEntity rpe = getRecertificationProcessEntityDao().load(rp.getId());
		if (rpe.getStatus() != ProcessStatus.PREPARATION)
			throw new InternalErrorException (Messages.getString("RecertificationServiceImpl.AlreadyStarted")); //$NON-NLS-1$
		JbpmContext ctx = getBpmEngine().getContext();
		try {
			rpe.setStartDate( new Date ());
			rpe.setStatus(ProcessStatus.ACTIVE);
			getRecertificationProcessEntityDao().update(rpe);
			
			if (rpe.getGroups().isEmpty())
				throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NotSelected")); //$NON-NLS-1$
			
			if (rpe.getInformationSystems().isEmpty())
				throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NoSystemSelected")); //$NON-NLS-1$
			
			for (RecertifiedGroupEntity rge: rpe.getGroups())
			{
				for (UserEntity ue: rge.getGroup().getPrimaryGroupUsers())
				{
					if ("S".equals(ue.getActive())) //$NON-NLS-1$
					{
						// Test if this user has any role on the applied Information Systems.
						Collection<RoleAccount> grants = getRecertifiedRoles (ue, rpe);
						if (! grants.isEmpty())
						{
							RecertifiedUserEntity rue = getRecertifiedUserEntityDao().newRecertifiedUserEntity();
							rue.setActiveAccount(Boolean.TRUE);
							rue.setCreatedOn(new Date());
							rue.setGroup(rge);
							rue.setStatus(ProcessStatus.PREPARATION);
							rue.setUser(ue);
							getRecertifiedUserEntityDao().create(rue);
							rge.getUsers().add(rue);
							if (rp.getWorkflowId() != null)
							{
								BpmUserProcess uwp = new BpmUserProcess();
								uwp.setUserCode(ue.getUserName());
								uwp.setTerminated(new Boolean(true));
								uwp.setProcessId(rp.getWorkflowId());
								getUserService().create(uwp);
							}
						}
					}
				}
				rge.setStatus(ProcessStatus.PREPARATION);
				getRecertifiedGroupEntityDao().update(rge);
			}
		} finally {
			ctx.close ();
		}
	}
	
	private Collection<RoleAccount> getRecertifiedRoles(UserEntity ue,
			RecertificationProcessEntity rpe) throws InternalErrorException {

		Collection<RoleAccount> rols = null;	// Roles granted
		
		Security.nestedLogin(Security.getCurrentAccount(),
			new String[]
			{ 
				Security.AUTO_APPLICATION_QUERY + Security.AUTO_ALL,
				Security.AUTO_USER_ROLE_QUERY + Security.AUTO_ALL
			});
		
		try
		{
			rols = getApplicationService()
					.findUserRolesByUserName(ue.getUserName());
		}
		
		finally
		{
			Security.nestedLogoff();
		}

		Iterator<RoleAccount> it = rols.iterator();
		while (it.hasNext()) {
			RoleAccount ra = it.next();
			boolean found = false;
			if (ra.getRuleId() == null)
			{
				for (RecertifiedInformationSystemEntity ris : rpe
						.getInformationSystems()) {
					if (ris.getInformationSystem().getName()
							.equals(ra.getInformationSystemName())) {
						found = true;
						break;
					}
				}
			}
			if (!found)
				it.remove();
		}
		return rols;
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleFindRecertificationProcesses(com.soffid.iam.recertification.common.RecertificationProcessCriteria)
	 */
	@Override
	protected List<RecertificationProcess> handleFindRecertificationProcesses(
			RecertificationProcessCriteria q) throws Exception {
		RecertificationProcessEntityDao dao = getRecertificationProcessEntityDao();
		List<RecertificationProcessEntity> list = dao.findByCriteria(q);
		return dao.toRecertificationProcessList(list);
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleGetRecretificationProcess(java.lang.Long)
	 */
	@Override
	protected RecertificationProcess handleGetRecertificationProcess(Long id)
			throws Exception {
		RecertificationProcessEntityDao dao = getRecertificationProcessEntityDao();
		return dao.toRecertificationProcess(dao.load(id));
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleGetRecertifiedGroups(com.soffid.iam.recertification.common.RecertificationProcess)
	 */
	@Override
	protected List<RecertifiedGroup> handleGetRecertifiedGroups(
			RecertificationProcess rp) throws Exception {
		RecertificationProcessEntityDao daoP = getRecertificationProcessEntityDao();
		RecertifiedGroupEntityDao dao = getRecertifiedGroupEntityDao();
		return dao.toRecertifiedGroupList(daoP.load(rp.getId()).getGroups());
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleGetRecertifiedInformationSystems(com.soffid.iam.recertification.common.RecertificationProcess)
	 */
	@Override
	protected List<RecertifiedInformationSystem> handleGetRecertifiedInformationSystems(
			RecertificationProcess rp) throws Exception {
		RecertificationProcessEntityDao daoP = getRecertificationProcessEntityDao();
		RecertifiedInformationSystemEntityDao dao = getRecertifiedInformationSystemEntityDao();
		return dao.toRecertifiedInformationSystemList(daoP.load(rp.getId()).getInformationSystems());
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleGetRecertifiedUsers(com.soffid.iam.recertification.common.RecertifiedGroup)
	 */
	@Override
	protected List<RecertifiedUser> handleGetRecertifiedUsers(
			RecertifiedGroup rg) throws Exception {
		RecertifiedGroupEntity rge = getRecertifiedGroupEntityDao().load(rg.getId());
		return getRecertifiedUserEntityDao().toRecertifiedUserList(rge.getUsers());
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleGetRecertifiedUsers(com.soffid.iam.recertification.common.RecertifiedGroup)
	 */
	@Override
	protected List<RecertifiedUser> handleGetRecertifiedUsers(
			RecertifiedGroup rg, RecertifiedInformationSystem ri) throws Exception {
		RecertifiedGroupEntity rge = getRecertifiedGroupEntityDao().load(rg.getId());
		List<RecertifiedUser> result = new LinkedList<RecertifiedUser>();
		for ( RecertifiedUserEntity ru : rge.getUsers())
		{
			boolean found = false;
			for ( RecertifiedRoleEntity rre: ru.getRoles())
			{
				RecertifiedRole rr = getRecertifiedRoleEntityDao().toRecertifiedRole(rre);
				if (rr.getRol().getInformationSystemName().equals(ri.getInformationSystem()))
				{
					found = true;
					break;
				}
			}
			if (found) 
				result.add ( getRecertifiedUserEntityDao().toRecertifiedUser(ru));
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleGetRecertifiedRoles(com.soffid.iam.recertification.common.RecertifiedUser)
	 */
	@Override
	protected List<RecertifiedRole> handleGetRecertifiedRoles(RecertifiedUser ru, RecertifiedInformationSystem ri)
			throws Exception {
		RecertifiedUserEntity rue = getRecertifiedUserEntityDao().load(ru.getId());
		LinkedList<RecertifiedRole> result = new LinkedList<RecertifiedRole>( getRecertifiedRoleEntityDao().toRecertifiedRoleList(rue.getRoles()));
		for (Iterator<RecertifiedRole> it= result.iterator(); it.hasNext();)
		{
			RecertifiedRole rr = it.next();
			if (rr.getRol() == null || 
					!rr.getRol().getInformationSystemName().equals(ri.getInformationSystem()))
			{
				it.remove();
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleGetRecertifiedRoles(com.soffid.iam.recertification.common.RecertifiedUser)
	 */
	@Override
	protected List<RecertifiedRole> handleGetRecertifiedRoles(RecertifiedUser ru)
			throws Exception {
		RecertifiedUserEntity rue = getRecertifiedUserEntityDao().load(ru.getId());
		List<RecertifiedRole> result = getRecertifiedRoleEntityDao().toRecertifiedRoleList(rue.getRoles());
		for (Iterator<RecertifiedRole> it = result.iterator(); it.hasNext();)
		{
			RecertifiedRole rr = it.next();
			if (rr.getRol() == null)
				it.remove();
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleFinishUserRecertification(com.soffid.iam.recertification.common.RecertifiedUser)
	 */
	@Override
	protected void handleApplyUserRecertification(RecertifiedUser ru)
			throws Exception {
		RecertifiedUserEntity rue = getRecertifiedUserEntityDao().load(ru.getId());
		applyUserRecertification(rue);
	}

	private void applyUserRecertification(RecertifiedUserEntity rue)
			throws InternalErrorException {
		if (rue.getBossReview() == null)
			rue.setBossReview(new Date());
		rue.setStatus(ProcessStatus.FINISHED);
		getRecertifiedUserEntityDao().update(rue);
		if (rue.getActiveAccount().booleanValue())
		{
			Security.nestedLogin(Security.getCurrentAccount(), new String[] {
				Security.AUTO_USER_QUERY+Security.AUTO_ALL,
				Security.AUTO_USER_UPDATE+Security.AUTO_ALL,
				Security.AUTO_USER_ROLE_DELETE+Security.AUTO_ALL,
				Security.AUTO_USER_ROLE_QUERY+Security.AUTO_ALL
			});
			try {
				User usuari = getUserService().findUserByUserName(rue.getUser().getUserName());
				Collection<RoleAccount> roles = getRecertifiedRoles (rue.getUser(), rue.getGroup().getProcess());
				for (RecertifiedRoleEntity rre: rue.getRoles())
				{
					if (! rre.isCheckedByBoss() || ! rre.isCheckedByAppOwner() ||
							( rue.getGroup().getProcess().getCisoReview() && ! rre.isCheckedByCiso()))
					{
						// Disable RolAccount.
						for (RoleAccount rolAccount: roles)
						{
							if (rolAccount.getId().equals (rre.getRolAccountId()))
							{
								getApplicationService().delete(rolAccount);
							}
						}
					}
				}
			} finally {
				Security.nestedLogoff();
			}
		}
		else
		{
			Security.nestedLogin(Security.getCurrentAccount(), new String[] {
				Security.AUTO_USER_QUERY+Security.AUTO_ALL,
				Security.AUTO_USER_UPDATE+Security.AUTO_ALL
			});
			try {
				User usuari = getUserService().findUserByUserName(rue.getUser().getUserName());
				usuari.setActive(Boolean.FALSE);
				getUserService().update(usuari);
			} finally {
				Security.nestedLogoff();
			}
		}
		if (rue.getWorkflowId() != null)
		{
			Collection<BpmUserProcess> uwps = getUserService().findBpmUserProcessByProcessId(rue.getWorkflowId());
			if (uwps == null || uwps.isEmpty())
			{
				BpmUserProcess uwp = new BpmUserProcess();
				uwp.setUserCode(rue.getUser().getUserName());
				uwp.setTerminated(new Boolean(false));
				uwp.setProcessId(rue.getWorkflowId());
				getUserService().create(uwp);
			} else {
				for (BpmUserProcess uwp: uwps)
				{
					uwp.setTerminated(Boolean.TRUE);
					getUserService().update(uwp);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleCheckByUser(com.soffid.iam.recertification.common.RecertifiedRole)
	 */
	@Override
	protected void handleCheckByUser(RecertifiedRole rr, boolean check) throws Exception {
		RecertifiedRoleEntity rre = getRecertifiedRoleEntityDao().load(rr.getId());
		String affectedUser = rre.getUser().getUser().getUserName();
		if (affectedUser.equals(Security.getPrincipal().getName()))
		{
			rre.setCheckedByBoss(check);
			rre.setCheckedByUser(check);
			getRecertifiedRoleEntityDao().update(rre);
			RecertifiedUserEntity rue = rre.getUser();
			rue.setUserReview(new Date());
			getRecertifiedUserEntityDao().update(rue);
		}
		else
			throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleCheckByBos(com.soffid.iam.recertification.common.RecertifiedRole)
	 */
	@Override
	protected void handleCheckByBoss(RecertifiedRole rr, boolean check) throws Exception {
		RecertifiedRoleEntity rre = getRecertifiedRoleEntityDao().load(rr.getId());
		GroupEntity ge = rre.getUser().getGroup().getGroup();
		String roleName = rre.getUser().getGroup().getManagerRole();
		
		if (! isRole (roleName))
			throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
		
		rre.setCheckedByBoss(check);
		getRecertifiedRoleEntityDao().update(rre);
		return;
	}
	
	private boolean isRole (String roleName) throws InternalErrorException
	{
		return getBpmEngine().isUserInRole(roleName);
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleDisableUser(com.soffid.iam.recertification.common.RecertifiedUser)
	 */
	@Override
	protected void handleEnableUser(RecertifiedUser ru, boolean checked) throws Exception {
		RecertifiedUserEntity rue = getRecertifiedUserEntityDao().load(ru.getId());
		GroupEntity ge = rue.getGroup().getGroup();
		String roleName = rue.getGroup().getManagerRole();
		
		if (! isRole (roleName))
			throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
		
		rue.setActiveAccount(new Boolean(checked));
		getRecertifiedUserEntityDao().update(rue);
		return;
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleCancelUserRecertification(com.soffid.iam.recertification.common.RecertifiedUser)
	 */
	@Override
	protected void handleCancelUserRecertification(RecertifiedUser ru)
			throws Exception {
		RecertifiedUserEntity rue = getRecertifiedUserEntityDao().load(ru.getId());
		cancelRecertifiedUser(rue);
	
	}

	private void cancelRecertifiedUser(RecertifiedUserEntity rue)
			throws InternalErrorException {
		if (rue.getStatus() == ProcessStatus.ACTIVE)
		{
			if (rue.getWorkflowId() != null)
			{
				JbpmContext ctx = getBpmEngine().getContext();
				try {
					ProcessInstance pi = ctx.getProcessInstance(rue.getWorkflowId().longValue());
					pi.signal(Constants.CANCEL_TRANSITION);
				} finally {
					ctx.close ();
				}
			}
			rue.setStatus(ProcessStatus.CANCELLED);
			getRecertifiedUserEntityDao().update(rue);
		}
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleCancelRecertification(com.soffid.iam.recertification.common.RecertificationProcess)
	 */
	@Override
	protected void handleCancelRecertification(RecertificationProcess rp)
			throws Exception {
		RecertificationProcessEntity rpe = getRecertificationProcessEntityDao().load(rp.getId());
		cancelRecertification (rpe);
	}

	private void cancelRecertification(RecertificationProcessEntity rpe) throws InternalErrorException {
		if (rpe.getStatus() == ProcessStatus.ACTIVE)
		{
			if (rpe.getWorkflowId() != null)
			{
				JbpmContext ctx = getBpmEngine().getContext();
				try {
					ProcessInstance pi = ctx.getProcessInstance(rpe.getWorkflowId().longValue());
					pi.signal(Constants.CANCEL_TRANSITION);
				} finally {
					ctx.close ();
				}
			}
			rpe.setStatus(ProcessStatus.CANCELLED);
			getRecertificationProcessEntityDao().update(rpe);
			for (RecertifiedGroupEntity rge: rpe.getGroups())
				cancelGroupRecertification (rge);
		}
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleCancelGroupRecertification(com.soffid.iam.recertification.common.RecertifiedGroup)
	 */
	@Override
	protected void handleCancelGroupRecertification(RecertifiedGroup rg)
			throws Exception {
		RecertifiedGroupEntity rge = getRecertifiedGroupEntityDao().load(rg.getId());
		cancelGroupRecertification (rge);

	}

	private void cancelGroupRecertification(RecertifiedGroupEntity rge) throws InternalErrorException {
		if (rge.getStatus() == ProcessStatus.ACTIVE)
		{
			if (rge.getWorkflowId() != null)
			{
				JbpmContext ctx = getBpmEngine().getContext();
				try {
					ProcessInstance pi = ctx.getProcessInstance(rge.getWorkflowId().longValue());
					pi.signal(Constants.CANCEL_TRANSITION);
				} finally {
					ctx.close ();
				}
			}
			rge.setStatus(ProcessStatus.CANCELLED);
			getRecertifiedGroupEntityDao().update(rge);
			for (RecertifiedUserEntity rue: rge.getUsers())
				cancelRecertifiedUser(rue);
		}
	}

	@Override
	protected void handleStartUserRecertification(RecertifiedGroup rg)
			throws Exception {
		RecertifiedGroupEntity rge = getRecertifiedGroupEntityDao().load(rg.getId());
		if (rge.getStatus() != ProcessStatus.PREPARATION)
			throw new InternalErrorException (Messages.getString("RecertificationServiceImpl.AlreadyStarted")); //$NON-NLS-1$
		JbpmContext ctx = getBpmEngine().getContext();
		try {
			rge.setStatus(ProcessStatus.ACTIVE);
			getRecertifiedGroupEntityDao().update(rge);
			for (RecertifiedUserEntity rue: rge.getUsers())
			{
				if (rue.getActiveAccount().booleanValue())
				{
					// Test if this user has any role on the applied Information Systems.
					Collection<RoleAccount> grants = getRecertifiedRoles (rue.getUser(), rge.getProcess());
					for (RoleAccount grant: grants)
					{
						RecertifiedRoleEntity rre = getRecertifiedRoleEntityDao().newRecertifiedRoleEntity();
						rre.setCheckedByBoss(false);
						rre.setCheckedByUser(false);
						rre.setRolAccountId(grant.getId());
						rre.setUser(rue);
						getRecertifiedRoleEntityDao().create(rre);
					}
					rue.setStatus(ProcessStatus.ACTIVE);
					getRecertifiedUserEntityDao().update(rue);
				} else {
					applyUserRecertification(rue);
				}
			}
		} finally {
			ctx.close ();
		}
	}

	@Override
	protected RecertifiedGroup handleGetRecertifiedGroup(Long id)
			throws Exception {
		RecertifiedGroupEntity group = getRecertifiedGroupEntityDao().load(id);
		if (isRole(group.getManagerRole()) || canManageRecertification()
				 || canQueryRecertification())
			return getRecertifiedGroupEntityDao().toRecertifiedGroup(group);
		
		// Check access to any audited I.S.

		for (RecertifiedInformationSystemEntity is: group.getProcess().getInformationSystems())
		{
			if (isRole(is.getAppOwnerRole()))
				return getRecertifiedGroupEntityDao().toRecertifiedGroup(group);
		}
		
		if (isRole (group.getProcess().getCisoRole()))
			return getRecertifiedGroupEntityDao().toRecertifiedGroup(group);

		return null;
	}

	private boolean canManageRecertification() {
		return Security.isUserInRole("recertification:manage"); //$NON-NLS-1$
	}

	private boolean canQueryRecertification() {
		return Security.isUserInRole("recertification:query"); //$NON-NLS-1$
	}

	@Override
	protected RecertifiedUser handleGetRecertifiedUser(Long id)
			throws Exception {
		RecertifiedUserEntity user = getRecertifiedUserEntityDao().load(id);
		if (Security.getCurrentUser().equals(user.getUser().getUserName())
				|| isRole(user.getGroup().getManagerRole()) 
				|| canManageRecertification()
				|| canQueryRecertification())
			return getRecertifiedUserEntityDao().toRecertifiedUser(user);
		else
			return null;
	}

	@Override
	protected void handleSetRecertifiedUserWorkflowId(RecertifiedUser ru,
			Long processId) throws Exception {
		RecertifiedUserEntityDao dao = getRecertifiedUserEntityDao();
		RecertifiedUserEntity e = dao.load(ru.getId());
		if (e.getWorkflowId() != null)
		{
			throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.AlreadyBound")); //$NON-NLS-1$
		}
		e.setWorkflowId(processId);
		dao.update(e);
		
		BpmUserProcess uwp = new BpmUserProcess();
		
		uwp.setUserCode(e.getUser().getUserName());
		uwp.setTerminated(new Boolean(false));
		uwp.setProcessId(processId);
		getUserService().create(uwp);
	}

	@Override
	protected void handleSetRecertifiedGroupWorkflowId(RecertifiedGroup rg,
			Long processId) throws Exception {
		RecertifiedGroupEntityDao dao = getRecertifiedGroupEntityDao();
		RecertifiedGroupEntity e = dao.load(rg.getId());
		if (e.getWorkflowId() != null)
		{
			throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.GroupAlreadyBound")); //$NON-NLS-1$
		}
		e.setWorkflowId(processId);
		dao.update(e);
		for (RecertifiedUserEntity ru: e.getUsers())
		{
			BpmUserProcess uwp = new BpmUserProcess();
			uwp.setUserCode(ru.getUser().getUserName());
			uwp.setTerminated(new Boolean(true));
			uwp.setProcessId(processId);
			getUserService().create(uwp);
		}
	}

	@Override
	protected List<RecertifiedRoleDefinition> handleGetRecertifiedRoles(
			RecertifiedInformationSystem ris) throws Exception {
		RecertifiedInformationSystemEntity entity = getRecertifiedInformationSystemEntityDao().load(ris.getId());
		List<RecertifiedRoleDefinition> result = getRecertifiedRoleDefinitionEntityDao().toRecertifiedRoleDefinitionList(entity.getRoles());
		return result;
	}

	@Override
	protected void handleCheckByAppOwner(RecertifiedRole rr, boolean check)
			throws Exception {
		RecertifiedRoleEntity rre = getRecertifiedRoleEntityDao().load(rr.getId());
		RecertificationProcessEntity process = rre.getUser().getGroup().getProcess();
		String app = rr.getRol().getInformationSystemName();
		for (RecertifiedInformationSystemEntity is: process.getInformationSystems())
		{
			if (is.getInformationSystem().getName().equals (app))
			{
				if (! isRole (is.getAppOwnerRole()))
					throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
				
			}
		}
		
		
		rre.setCheckedByAppOwner(check);
		getRecertifiedRoleEntityDao().update(rre);
		return;
	}

	@Override
	protected void handleCheckByAppOwner(RecertifiedRoleDefinition rr,
			boolean check) throws Exception {
		RecertifiedRoleDefinitionEntity rre = getRecertifiedRoleDefinitionEntityDao().load(rr.getId());
		String app = rre.getInformationSystem().getInformationSystem().getName();
		if (! isRole (rre.getInformationSystem().getAppOwnerRole()))
			throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
		
		
		rre.setCheckedByOwner(check);
		getRecertifiedRoleDefinitionEntityDao().update(rre);
	}

	@Override
	protected void handleCheckByCiso(RecertifiedRoleDefinition rr, boolean check)
			throws Exception {
		RecertifiedRoleDefinitionEntity rre = getRecertifiedRoleDefinitionEntityDao().load(rr.getId());
		String app = rre.getInformationSystem().getInformationSystem().getName();
		if (! isRole (rre.getInformationSystem().getProcess().getCisoRole()))
			throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
		
		rre.setCheckedByCiso(check);
		getRecertifiedRoleDefinitionEntityDao().update(rre);
	}

	@Override
	protected void handleCheckByCiso(RecertifiedRole rr, boolean check)
			throws Exception {
		RecertifiedRoleEntity rre = getRecertifiedRoleEntityDao().load(rr.getId());
		RecertificationProcessEntity process = rre.getUser().getGroup().getProcess();
		String app = rr.getRol().getInformationSystemName();
		if (! isRole ( process.getCisoRole()))
			throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
		
		rre.setCheckedByCiso(check);
		getRecertifiedRoleEntityDao().update(rre);
		return;
		
	}

	@Override
	protected void handleSetRecertifiedInformationSystemWorkflowId(
			RecertifiedInformationSystem ris, Long processId) throws Exception {
		RecertifiedInformationSystemEntity rise = getRecertifiedInformationSystemEntityDao().load(ris.getId());
		rise.setProcessId(processId);
		getRecertifiedInformationSystemEntityDao().update(rise);
		return;
		
	}

	@Override
	protected void handleStartInformationSystemRecertificationProcess(
			RecertificationProcess rp) throws Exception {
		RecertificationProcessEntity rpe = getRecertificationProcessEntityDao().load(rp.getId());
		if (rpe.getStatus() != ProcessStatus.PREPARATION)
			throw new InternalErrorException (Messages.getString("RecertificationServiceImpl.AlreadyStarted")); //$NON-NLS-1$

		rpe.setStartDate( new Date ());
		rpe.setStatus(ProcessStatus.ACTIVE);
		getRecertificationProcessEntityDao().update(rpe);
		
		if (rpe.getType().equals(RecertificationType.ENTITLEMENTS))
			return;
		
		if (rpe.getInformationSystems().isEmpty())
			throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NoSystemSelected")); //$NON-NLS-1$
			
		for (RecertifiedInformationSystemEntity ris: rpe.getInformationSystems())
		{
			for (RoleEntity role: ris.getInformationSystem().getRoles())
			{
				if (!role.getContainedRoles().isEmpty())
				{
					RecertifiedRoleDefinitionEntity rde = getRecertifiedRoleDefinitionEntityDao().newRecertifiedRoleDefinitionEntity();
					rde.setCheckedByCiso(false);
					rde.setCheckedByOwner(false);
					rde.setInformationSystem(ris);
					rde.setRoleId(role.getId());
					getRecertifiedRoleDefinitionEntityDao().create(rde);
				}
				ris.setStatus(ProcessStatus.ACTIVE);
				getRecertifiedInformationSystemEntityDao().update(ris);
			}
		}
	}

	@Override
	protected void handleApplyInformationSystemRecertificationProcess(
			RecertifiedInformationSystem ris) throws Exception {
		RecertifiedInformationSystemEntity rise = getRecertifiedInformationSystemEntityDao().load(ris.getId());
		for ( RecertifiedRoleDefinitionEntity rd: rise.getRoles())
		{
			if (rd.isCheckedByOwner() &&
					( ! rise.getProcess().getCisoReview() || rd.isCheckedByCiso()))
			{
				RoleEntity re = getRoleEntityDao().load (rd.getRoleId());
				re.setApprovalEnd(new Date());
				getRoleEntityDao().update(re);
			}
		}
	}

	@Override
	protected RecertifiedInformationSystem handleGetRecertifiedInformationSystem(
			Long id) throws Exception {
		RecertifiedInformationSystemEntity is = getRecertifiedInformationSystemEntityDao().load(id);
		if (isRole(is.getAppOwnerRole()) || canManageRecertification()
				 || canQueryRecertification())
			return getRecertifiedInformationSystemEntityDao().toRecertifiedInformationSystem(is);
		else
			return null;
	}

	@Override
	protected RecertifiedUser handleUpdate(RecertifiedUser ru)
			throws Exception {
		RecertifiedUserEntityDao dao = getRecertifiedUserEntityDao();
		RecertifiedUserEntity e = dao.load(ru.getId());
		if (e.getStatus() == ProcessStatus.PREPARATION ||
				e.getStatus() == ProcessStatus.ACTIVE)
		{
			e.setActiveAccount(ru.getActiveAccount());
			if (ru.getBossReview() == null)
				e.setBossReview(null);
			else
				e.setBossReview(ru.getBossReview().getTime());

			if (ru.getAppOwnerReview() == null)
				e.setAppOwnerReview(null);
			else
				e.setAppOwnerReview(ru.getAppOwnerReview().getTime());

			if (ru.getCisoReview() == null)
				e.setCisoReview(null);
			else
				e.setCisoReview(ru.getCisoReview().getTime());

			dao.update(e);
		} else {
			throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NotUpdate")); //$NON-NLS-1$
		}
		dao.update(e);
		return dao.toRecertifiedUser(e);
	}

}

