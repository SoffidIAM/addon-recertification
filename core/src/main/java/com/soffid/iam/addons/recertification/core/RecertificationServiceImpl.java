/**
 * 
 */
package com.soffid.iam.addons.recertification.core;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.jbpm.jpdl.el.FunctionMapper;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.ExpressionEvaluatorImpl;
import org.json.JSONException;

import com.soffid.iam.ServiceLocator;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertificationProcessCriteria;
import com.soffid.iam.addons.recertification.common.RecertificationTemplate;
import com.soffid.iam.addons.recertification.common.RecertificationType;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;
import com.soffid.iam.addons.recertification.common.RecertifiedRole;
import com.soffid.iam.addons.recertification.common.RecertifiedRoleDefinition;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.model.RecertificationProcessEntity;
import com.soffid.iam.addons.recertification.model.RecertificationProcessEntityDao;
import com.soffid.iam.addons.recertification.model.RecertificationTemplateEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedGroupEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedGroupEntityDao;
import com.soffid.iam.addons.recertification.model.RecertifiedInformationSystemEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedInformationSystemEntityDao;
import com.soffid.iam.addons.recertification.model.RecertifiedRoleDefinitionEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedRoleEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedUserEntity;
import com.soffid.iam.addons.recertification.model.RecertifiedUserEntityDao;
import com.soffid.iam.api.AsyncList;
import com.soffid.iam.api.BpmUserProcess;
import com.soffid.iam.api.PagedResult;
import com.soffid.iam.api.Role;
import com.soffid.iam.api.RoleAccount;
import com.soffid.iam.api.RoleDependencyStatus;
import com.soffid.iam.api.RoleGrant;
import com.soffid.iam.bpm.service.scim.ScimHelper;
import com.soffid.iam.common.security.SoffidPrincipal;
import com.soffid.iam.interp.Evaluator;
import com.soffid.iam.model.GroupEntity;
import com.soffid.iam.model.Parameter;
import com.soffid.iam.model.RoleAccountEntity;
import com.soffid.iam.model.RoleDependencyEntity;
import com.soffid.iam.model.RoleEntity;
import com.soffid.iam.model.UserEntity;
import com.soffid.iam.model.criteria.CriteriaSearchConfiguration;
import com.soffid.iam.service.impl.bshjail.SecureInterpreter;
import com.soffid.iam.utils.ConfigurationCache;
import com.soffid.scimquery.EvalException;
import com.soffid.scimquery.parser.ParseException;
import com.soffid.scimquery.parser.TokenMgrError;

import bsh.Modifiers;
import bsh.NameSpace;
import bsh.TargetError;
import bsh.UtilEvalError;
import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.utils.Security;

/**
 * @author bubu
 *
 */
public class RecertificationServiceImpl extends RecertificationServiceBase {
	Log log = LogFactory.getLog(getClass());
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
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleCheckByUser(com.soffid.iam.recertification.common.RecertifiedRole)
	 */
	@Override
	protected void handleCheck(RecertifiedRole rr, boolean check) throws Exception {
		RecertifiedRoleEntity rre = getRecertifiedRoleEntityDao().load(rr.getId());
		com.soffid.iam.common.security.SoffidPrincipal p = Security.getSoffidPrincipal();
		boolean success = false;
		if (rre.getCheck1() == null)
		{
			if (isValidToCheck (rre.getStep1Users() ))
			{
				rre.setCheck1(check);
				rre.setStep1Author(p.getUserName());
				rre.setStep1Date(new Date());
				success = true;
			}
			else
				throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
		}
		if (rre.getCheck2() == null)
		{
			if (isValidToCheck (rre.getStep2Users() ))
			{
				rre.setCheck2(check);
				rre.setStep2Author(p.getUserName());
				rre.setStep2Date(new Date());
				success = true;
			}
			else if (!success)
				throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
		}
		if (rre.getCheck3() == null)
		{
			if (isValidToCheck (rre.getStep3Users() ))
			{
				rre.setCheck3(check);
				rre.setStep3Author(p.getUserName());
				rre.setStep3Date(new Date());
				success = true;
			}
			else if (!success)
				throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
		}
		if (rre.getCheck4() == null)
		{
			if (isValidToCheck (rre.getStep4Users() ))
			{
				rre.setCheck4(check);
				rre.setStep4Author(p.getUserName());
				rre.setStep4Date(new Date());
				success = true;
			}
			else if (!success)
				throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
		}
		if (success)
		{
			rre.setAssignationDate(new Date());
			rre.setReminderDate(null);
			rre.setEscalationDate(null);
			getRecertifiedRoleEntityDao().update(rre);
			if ( isFinished (rre))
			{
				Security.nestedLogin(Security.getCurrentAccount(), new String[] {
						Security.AUTO_USER_QUERY+Security.AUTO_ALL,
						Security.AUTO_USER_UPDATE+Security.AUTO_ALL,
						Security.AUTO_USER_ROLE_DELETE+Security.AUTO_ALL,
						Security.AUTO_USER_ROLE_CREATE+Security.AUTO_ALL,
						Security.AUTO_USER_ROLE_QUERY+Security.AUTO_ALL
					});
				try {
					RoleAccountEntity entity = getRoleAccountEntityDao().load(rre.getRolAccountId());
					RoleAccount ra = getRoleAccountEntityDao().toRoleAccount(entity);
					ra.setCertificationDate(new Date());
					if (!check)
					{
						ra.setEndDate(new Date());
					}
					getApplicationService().update(ra);
				} finally {
					Security.nestedLogoff();
				}
			}
		}
		if ( rre.getUser() != null)
			checkUserStatus(rre.getUser());
		if ( rre.getInformationSystem() != null)
			checkISStatus(rre.getInformationSystem());
	}

	public void checkUserStatus(RecertifiedUserEntity u) {
		int total = 0;
		int done = 0;
		for ( RecertifiedRoleEntity rre2: u.getRoles())
		{
			total ++;
			if (isFinished(rre2))
			{
				done ++;
			}
		}
		if (total == 0) {
			u.setStatus(ProcessStatus.CANCELLED);
			u.setPctDone(100);
		}
		else if (done == total)
		{
			u.setStatus(ProcessStatus.FINISHED);
			u.setPctDone(100);
		}
		else
		{
			u.setStatus(ProcessStatus.ACTIVE);
			u.setPctDone(100 * done / total);
		}
		getRecertifiedUserEntityDao().update(u);
		RecertifiedGroupEntity g = u.getGroup();
		if (u.getStatus() == ProcessStatus.FINISHED)
			checkGroupStatus(g);
	}

	public void checkISStatus(RecertifiedInformationSystemEntity is) {
		RecertifiedInformationSystem vo = getRecertifiedInformationSystemEntityDao().toRecertifiedInformationSystem(is);
		if (vo.getPctDone().intValue() == 100)
		{
			is.setStatus(ProcessStatus.FINISHED);
			getRecertifiedInformationSystemEntityDao().update(is);
			checkProcessStatus(is.getProcess());
		}
	}

	public void checkGroupStatus(RecertifiedGroupEntity g) {
		boolean done;
		done = true;
		for ( RecertifiedUserEntity u2: g.getUsers())
		{
			if ( u2.getStatus() != ProcessStatus.FINISHED)
			{
				done = false;
				break;
			}
		}
		if (done)
		{
			g.setStatus(ProcessStatus.FINISHED);
			getRecertifiedGroupEntityDao().update(g);
			RecertificationProcessEntity proc = g.getProcess();
			checkProcessStatus(proc);
		}
	}

	public void checkProcessStatus(RecertificationProcessEntity proc) {
		boolean done;
		done = true;
		if (proc.getType() == RecertificationType.ENTITLEMENTS)
		{
			for (RecertifiedGroupEntity g2: proc.getGroups()) {
				if (g2.getStatus() != ProcessStatus.FINISHED && g2.getStatus() != ProcessStatus.CANCELLED)
				{
					done = false;
					break;
				}
			}
		} 
		else
		{
			for (RecertifiedInformationSystemEntity g2: proc.getInformationSystems()) {
				if (g2.getStatus() != ProcessStatus.FINISHED && g2.getStatus() != ProcessStatus.CANCELLED)
				{
					done = false;
					break;
				}
			}
		} 
		if (done)
		{
			proc.setStatus(ProcessStatus.FINISHED);
			proc.setFinishDate(new Date());
			getRecertificationProcessEntityDao().update(proc);
		}
	}

	private boolean isFinished(RecertifiedRoleEntity rre) {
		if (Boolean.FALSE.equals(rre.getCheck1())) {
			rre.setStep2Users(null);
			rre.setStep3Users(null);
			rre.setStep4Users(null);
			return true;
		}
		else if (Boolean.FALSE.equals(rre.getCheck2())) {
			rre.setStep3Users(null);
			rre.setStep4Users(null);
			return true;
		} 
		else if (Boolean.FALSE.equals(rre.getCheck3())) {
			rre.setStep4Users(null);
			return true;
		} 
		else if (Boolean.FALSE.equals(rre.getCheck4()))
			return true;
		else if  ( rre.getStep1Users() != null && ! rre.getStep1Users().trim().isEmpty() && rre.getCheck1() == null ||
				 rre.getStep2Users() != null && ! rre.getStep2Users().trim().isEmpty() && rre.getCheck2() == null ||
				 rre.getStep3Users() != null && ! rre.getStep3Users().trim().isEmpty() && rre.getCheck3() == null ||
				 rre.getStep4Users() != null && ! rre.getStep4Users().trim().isEmpty() && rre.getCheck4() == null )
			return false;
		else
			return true;
	}

	private boolean isValidToCheck(String step1Author) {
		if (step1Author == null || step1Author.trim().isEmpty())
			return false;
		
		com.soffid.iam.common.security.SoffidPrincipal p = Security.getSoffidPrincipal();
		for (String part: step1Author.split(" +"))
		{
			if (p.getUserName().equals(part))
				return true;
			for (String role: p.getRoles())
				if (role.equals(part))
					return true;
		}
		return false;
	}

	private boolean isRole (String roleName) throws InternalErrorException
	{
		return getBpmEngine().isUserInRole(roleName);
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
	protected List<RecertifiedRoleDefinition> handleGetRecertifiedRoleDefinitions(
			RecertifiedInformationSystem ris) throws Exception {
		RecertifiedInformationSystemEntity entity = getRecertifiedInformationSystemEntityDao().load(ris.getId());
		List<RecertifiedRoleDefinition> result = getRecertifiedRoleDefinitionEntityDao().toRecertifiedRoleDefinitionList(entity.getRoles());
		return result;
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
	protected void handleStartRecertificationProcess(
			RecertificationProcess rp) throws Exception {
		RecertificationProcessEntity rpe = getRecertificationProcessEntityDao().load(rp.getId());
		if (rpe.getStatus() != ProcessStatus.PREPARATION)
			throw new InternalErrorException (Messages.getString("RecertificationServiceImpl.AlreadyStarted")); //$NON-NLS-1$

		if (rpe.getTemplate() == null)
			throw new InternalErrorException("Please, select a template to start");

		rpe.setStartDate( new Date ());
		rpe.setStatus(ProcessStatus.ACTIVE);
		rpe.setType(rpe.getTemplate().getType());
		getRecertificationProcessEntityDao().update(rpe);
		
		
		if (rpe.getType().equals(RecertificationType.ENTITLEMENTS))
		{
			startEntitlementProcess(rp, rpe);
		}
		if (rpe.getType().equals(RecertificationType.ACCOUNTS))
		{
			startAccountProcess(rp, rpe);
		}
		if (rpe.getType().equals(RecertificationType.ROLEDEFINITIONS))
		{
			startRoleDefinitionsProcess(rpe);
		}
		
		getRecertificationProcessEntityDao().toRecertificationProcess(rpe, rp);
		
		String url = getBaseUrl();
		
		handleNotifyUsers(rp, url);
	}

	private String getBaseUrl() {
		String externalURL = System.getProperty("soffid.externalURL");
		if (externalURL == null)
			externalURL = ConfigurationCache.getProperty("soffid.externalURL");
		if (externalURL == null)
			externalURL = ConfigurationCache.getProperty("AutoSSOURL");
		if (externalURL == null)
			externalURL = "${soffid.externalURL}";
		if (!externalURL.endsWith("/"))
			externalURL = externalURL + "/";
		if (!externalURL.endsWith("/soffid/"))
			externalURL += "soffid/";
		return externalURL + "addon/recertification/self.zul";
	}

	public boolean startRoleDefinitionsProcess(RecertificationProcessEntity rpe) throws Exception {
		if (rpe.getInformationSystems().isEmpty())
			throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NoSystemSelected")); //$NON-NLS-1$
			
		boolean any = false;
		HashMap<String, Object> vars = new HashMap<String, Object>();
		
		for (RecertifiedInformationSystemEntity ris: rpe.getInformationSystems())
		{
			if (startIsRoleRecertification(rpe, vars, ris))
				any = true;
		}
		if (!any)
			throw new InternalErrorException("No role definitian matches the template filter criteria");
		return any;
	}

	public void startAccountProcess(RecertificationProcess rp, RecertificationProcessEntity rpe)
			throws Exception {
		JbpmContext ctx = getBpmEngine().getContext();
		try {
			rpe.setStartDate( new Date ());
			rpe.setStatus(ProcessStatus.ACTIVE);
			getRecertificationProcessEntityDao().update(rpe);
			
			HashMap<String, Object> vars = new HashMap<String, Object>();
			
			if (rpe.getInformationSystems().isEmpty())
				throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NoSystemSelected")); //$NON-NLS-1$
			
			boolean any = false;
			for (RecertifiedInformationSystemEntity rge: rpe.getInformationSystems())
			{
				if (startIsAccountsRecertification(rpe, rge, vars))
					any = true;
			}
			if (!any)
				throw new InternalErrorException("No account to recertify");
		} finally {
			ctx.close ();
		}
	}

	private boolean startIsAccountsRecertification(RecertificationProcessEntity rpe, 
			RecertifiedInformationSystemEntity rie, Map<String, Object> vars) throws UtilEvalError, Exception {
		String filterScript = rpe.getTemplate().getFilterScript();

		log.info("Starting account recertification for information system "+rie.getInformationSystem().getName());
		boolean any = false;

		List<RoleAccountEntity> q = getRoleAccountEntityDao().query("select ra "
				+ "from com.soffid.iam.model.RoleAccountEntity as ra, "
				+ "     com.soffid.iam.model.AccountEntity as acc, "
				+ "     com.soffid.iam.model.InformationSystemEntity as app "
				+ "where app.id=:id and "
				+ "      ra.role.informationSystem=app and"
				+ "      ra.account = acc and"
				+ "      acc.disabled = :disabled and "
				+ "      (acc.type = 'S' or acc.type='P' or acc.type='I')", 
				new Parameter[] { 
						new Parameter("id", rie.getInformationSystem().getId()),
						new Parameter("disabled", Boolean.FALSE)
						});
		HashMap<Long,List<RoleAccountEntity>> grantsByAccount = new HashMap<Long,List<RoleAccountEntity>>();
		for (RoleAccountEntity rae: q)
		{
			Long accountId = rae.getAccount().getId();
			List<RoleAccountEntity> l = grantsByAccount.get(accountId);
			if (l == null)
			{
				l = new LinkedList<RoleAccountEntity>();
				grantsByAccount.put(accountId, l);
			}
			l.add(rae);
		}
		
		for ( List<RoleAccountEntity> rael: grantsByAccount.values())
		{
			List<RoleAccount> grants = getRoleAccountEntityDao().toRoleAccountList(rael);
			getSoDRuleService().qualifyRolAccountList(grants);
			for (RoleAccount grant: grants)
			{
				boolean skip = false;
				if (filterScript != null && ! filterScript.trim().isEmpty())
				{
					Modifiers m = new Modifiers();
					vars.put("grant", grant);
					if ( Boolean.FALSE.equals( eval(filterScript, vars, "filter script")))
						skip = true;
				}
				if (! skip)
				{
					any = true;
					RecertifiedRoleEntity rre = getRecertifiedRoleEntityDao().newRecertifiedRoleEntity();
					rre.setRolAccountId(grant.getId());
					rre.setInformationSystem(rie);
					setReviewers(rpe, rre, vars, grant);
					getRecertifiedRoleEntityDao().create(rre);
				}
			}
		}

		if (any)
		{
			rie.setStatus(ProcessStatus.ACTIVE);
			getRecertifiedInformationSystemEntityDao().update(rie);
		} else {
			rie.setStatus(ProcessStatus.CANCELLED);
			getRecertifiedInformationSystemEntityDao().update(rie);
		}
		return any;
	}

	private boolean startIsRoleRecertification(RecertificationProcessEntity rpe, 
			Map<String,Object> vars,
			RecertifiedInformationSystemEntity rie) throws UtilEvalError, Exception {
		String filterScript = rpe.getTemplate().getFilterScript();

		log.info("Starting account recertification for information system "+rie.getInformationSystem().getName());
		boolean any = false;
		for ( RoleEntity roleEntity: rie.getInformationSystem().getRoles())
		{
			if (roleEntity.getContainedRoles() != null && ! roleEntity.getContainedRoles().isEmpty()) {
				Role role = getRoleEntityDao().toRole(roleEntity);
				boolean skip = false;
				if (filterScript != null && ! filterScript.trim().isEmpty())
				{
					vars.put("role", role);
					if ( Boolean.FALSE.equals( eval(filterScript, vars, "filter script")))
						skip = true;
				}
				if (! skip)
				{
					any = true;
					RecertifiedRoleDefinitionEntity rre = getRecertifiedRoleDefinitionEntityDao().newRecertifiedRoleDefinitionEntity();
					rre.setRoleId(roleEntity.getId());
					rre.setInformationSystem(rie);
					setReviewers(rpe, rre, vars, role);
					getRecertifiedRoleDefinitionEntityDao().create(rre);
				}
			}
		}

		if (any)
		{
			rie.setStatus(ProcessStatus.ACTIVE);
			getRecertifiedInformationSystemEntityDao().update(rie);
		} else {
			rie.setStatus(ProcessStatus.CANCELLED);
			getRecertifiedInformationSystemEntityDao().update(rie);
		}
		return any;
	}


	public void startEntitlementProcess(RecertificationProcess rp, RecertificationProcessEntity rpe)
			throws Exception {
		JbpmContext ctx = getBpmEngine().getContext();
		try {
			rpe.setStartDate( new Date ());
			rpe.setStatus(ProcessStatus.ACTIVE);
			getRecertificationProcessEntityDao().update(rpe);
			
			HashMap<String, Object> vars = new HashMap<String, Object>();
			
			if (rpe.getGroups().isEmpty())
				throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NotSelected")); //$NON-NLS-1$
			
			if (rpe.getInformationSystems().isEmpty())
				throw new InternalErrorException(Messages.getString("RecertificationServiceImpl.NoSystemSelected")); //$NON-NLS-1$
			
			Set<String> groups = new HashSet<String>();
			List<GroupEntity> pendingGroups = new LinkedList<GroupEntity>();
			for (RecertifiedGroupEntity rge: rpe.getGroups())
			{
				if (!groups.contains(rge.getGroup().getName())) {
					groups.add(rge.getGroup().getName());
					startGroupRecertification(rpe, vars, rge);
					for (GroupEntity child: rge.getGroup().getChildren())
						pendingGroups.add(child);
				}
			}
			while ( ! pendingGroups.isEmpty())
			{
				GroupEntity g = pendingGroups.get(0);
				pendingGroups.remove(0);
				if ( ! groups.contains(g.getName()))
				{
					RecertifiedGroupEntity rge = getRecertifiedGroupEntityDao().newRecertifiedGroupEntity();
					rge.setGroup(g);
					rge.setProcess(rpe);
					rge.setStatus(ProcessStatus.ACTIVE);
					getRecertifiedGroupEntityDao().create(rge);
					groups.add(rge.getGroup().getName());
					startGroupRecertification(rpe, vars, rge);
					for (GroupEntity child: rge.getGroup().getChildren())
						pendingGroups.add(child);
				}
			}
			if (rpe.getGroups().isEmpty())
				throw new InternalErrorException("No permission matches the template filter criteria");
		} finally {
			ctx.close ();
		}
	}

	public void startGroupRecertification(RecertificationProcessEntity rpe,
			Map<String, Object> vars, RecertifiedGroupEntity rge)
			throws InternalErrorException, UtilEvalError, Exception {
		String filterScript = rpe.getTemplate().getFilterScript();
		
		log.info("Starting recertification for group "+rge.getGroup().getName());
		boolean any = false;

		for (UserEntity ue: rge.getGroup().getPrimaryGroupUsers())
		{
			if ("S".equals(ue.getActive())) //$NON-NLS-1$
			{
				// Test if this user has any role on the applied Information Systems.
				Collection<RoleAccount> grants = getRecertifiedRoles (ue, rpe);
				if (filterScript != null && ! filterScript.trim().isEmpty())
				{
					for (Iterator<RoleAccount> iterator = grants.iterator(); iterator.hasNext();)
					{
						RoleAccount grant = iterator.next();
						Modifiers m = new Modifiers();
						vars.put("grant", grant);
						Object skip = eval(filterScript, vars, "filter script");
						if ( Boolean.FALSE.equals(skip))
							iterator.remove();
					}
				}
				if (! grants.isEmpty())
				{
					any = true;
					RecertifiedUserEntity rue = getRecertifiedUserEntityDao().newRecertifiedUserEntity();
					rue.setActiveAccount(Boolean.TRUE);
					rue.setCreatedOn(new Date());
					rue.setGroup(rge);
					rue.setStatus(ProcessStatus.ACTIVE);
					rue.setUser(ue);
					rue.setPctDone(0);
					getRecertifiedUserEntityDao().create(rue);
					rge.getUsers().add(rue);
					if (rpe.getWorkflowId() != null)
					{
						BpmUserProcess uwp = new BpmUserProcess();
						uwp.setUserCode(ue.getUserName());
						uwp.setTerminated(new Boolean(true));
						uwp.setProcessId(rpe.getWorkflowId());
						getUserService().create(uwp);
					}
					for (RoleAccount grant: grants)
					{
						RecertifiedRoleEntity rre = getRecertifiedRoleEntityDao().newRecertifiedRoleEntity();
						rre.setRolAccountId(grant.getId());
						rre.setUser(rue);
						setReviewers(rpe, rre, vars, grant);
						getRecertifiedRoleEntityDao().create(rre);
					}
				}
			}
		}
		if (any)
		{
			rge.setStatus(ProcessStatus.ACTIVE);
			getRecertifiedGroupEntityDao().update(rge);
		} else {
			getRecertifiedGroupEntityDao().remove(rge);
		}
	}

	public void setReviewers(RecertificationProcessEntity rpe, RecertifiedRoleEntity rre, 
			Map<String,Object> vars, RoleAccount grant) throws UtilEvalError, Exception {
		vars.put("grant", grant);
		if ( rpe.getTemplate().getStep1Script() != null && 
				!rpe.getTemplate().getStep1Script().trim().isEmpty())
		{
			rre.setStep1Users( (String) eval (rpe.getTemplate().getStep1Script(), vars, "step 1"));
			if ( rpe.getTemplate().getStep2Script() != null && 
					!rpe.getTemplate().getStep2Script().trim().isEmpty())
			{
				rre.setStep2Users( (String) eval (rpe.getTemplate().getStep2Script(), vars, "step 2"));
				if ( rpe.getTemplate().getStep3Script() != null && 
						!rpe.getTemplate().getStep3Script().trim().isEmpty())
				{
					rre.setStep3Users( (String) eval (rpe.getTemplate().getStep3Script(), vars, "step 3"));
					if ( rpe.getTemplate().getStep4Script() != null && 
							!rpe.getTemplate().getStep4Script().trim().isEmpty())
					{
						rre.setStep4Users( (String) eval (rpe.getTemplate().getStep4Script(), vars, "step 4"));
					}
				}
			}
		}
		rre.setAssignationDate(new Date());
		rre.setEscalationDate(null);
		rre.setReminderDate(null);
	}

	//Method for role dependecies
	private void setReviewers(RecertificationProcessEntity rpe, RecertifiedRoleDefinitionEntity rre,
			Map<String,Object> vars, Role role) throws Exception {
		vars.put("role", role);
		if ( rpe.getTemplate().getStep1Script() != null && 
				!rpe.getTemplate().getStep1Script().trim().isEmpty())
		{
			rre.setStep1Users( (String) eval (rpe.getTemplate().getStep1Script(), vars, "step 1"));
			if ( rpe.getTemplate().getStep2Script() != null && 
					!rpe.getTemplate().getStep2Script().trim().isEmpty())
			{
				rre.setStep2Users( (String) eval (rpe.getTemplate().getStep2Script(), vars, "step 2"));
				if ( rpe.getTemplate().getStep3Script() != null && 
						!rpe.getTemplate().getStep3Script().trim().isEmpty())
				{
					rre.setStep3Users( (String) eval (rpe.getTemplate().getStep3Script(), vars, "step 3"));
					if ( rpe.getTemplate().getStep4Script() != null && 
							!rpe.getTemplate().getStep4Script().trim().isEmpty())
					{
						rre.setStep4Users( (String) eval (rpe.getTemplate().getStep4Script(), vars, "step 4"));
					}
				}
			}
		}
	}

	public Object eval(String filterScript, Map<String, Object> vars, String ctx) throws Exception {
		try {
			return Evaluator.instance().evaluate(filterScript, vars, ctx);
		} catch ( TargetError ex) {
			log.warn("BSH Script error: "+ex.getMessage()+" at "+ex.getErrorSourceFile()+":"+ex.getErrorLineNumber());
			log.warn("Stack trace: "+ex.getErrorText()+" at "+ex.getScriptStackTrace());
			log.info(String.format(
					Messages.getString("BSHInterpreter.BadScript"), filterScript)); //$NON-NLS-1$
			if (ex.getTarget() instanceof Exception)
				throw ((Exception) ex.getTarget());
			else
				throw ex;
		}
	}

	@Override
	protected void handleApplyInformationSystemRecertificationProcess(
			RecertifiedInformationSystem ris) throws Exception {
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
	protected RecertificationTemplate handleCreateTemplate(RecertificationTemplate template) throws Exception {
		RecertificationTemplateEntity entity = getRecertificationTemplateEntityDao().newRecertificationTemplateEntity();
		getRecertificationTemplateEntityDao().recertificationTemplateToEntity(template, entity, true);
		
		getRecertificationTemplateEntityDao().create(entity);
		
		return getRecertificationTemplateEntityDao().toRecertificationTemplate(entity);
	}

	@Override
	protected RecertificationTemplate handleUpdateTemplate(RecertificationTemplate template) throws Exception {
		RecertificationTemplateEntity entity = getRecertificationTemplateEntityDao().load(template.getId());
		if (entity == null)
			throw new InternalErrorException ("Template "+template.getId()+" does not exist");
		getRecertificationTemplateEntityDao().recertificationTemplateToEntity(template, entity, true);
		
		getRecertificationTemplateEntityDao().create(entity);
		
		return getRecertificationTemplateEntityDao().toRecertificationTemplate(entity);
	}

	@Override
	protected List<RecertificationTemplate> handleFindTemplates() throws Exception {
		List<RecertificationTemplateEntity> entities = getRecertificationTemplateEntityDao().loadAll();
		List<RecertificationTemplate> l = getRecertificationTemplateEntityDao().toRecertificationTemplateList(entities);
		l.sort(new Comparator<RecertificationTemplate>() {
			@Override
			public int compare(RecertificationTemplate o1, RecertificationTemplate o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return l;
	}

	@Override
	protected List<RecertifiedRole> handleGetRecertifiedRoles(RecertificationProcess rp) throws Exception {
		Collection<RecertifiedRoleEntity> roles = getRecertifiedRoleEntityDao().findByProcessId(rp.getId());
		LinkedList<RecertifiedRole> result = new LinkedList<RecertifiedRole>( getRecertifiedRoleEntityDao().toRecertifiedRoleList(roles));
		Collections.sort(result, new Comparator<RecertifiedRole>() {
			@Override
			public int compare(RecertifiedRole o1, RecertifiedRole o2) {
				if (o1.getRol() == null && o2.getRol() == null)
					return 0;
				else if (o1.getRol() == null)
					return 1;
				else if (o2.getRol() == null)
					return -1;
				int i = o1.getRol().getGroupDescription().compareTo(o2.getRol().getGroupDescription());
				if (i == 0)
					i = o1.getRol().getUserFullName().compareTo(o2.getRol().getUserFullName());
				if (i == 0)
					i = o1.getRol().getRoleDescription().compareTo(o2.getRol().getRoleDescription());
				return i;
			}
		});
		return result;
	}

	@Override
	protected List<RecertifiedRoleDefinition> handleGetRecertifiedRoleDefinitions(RecertificationProcess rp) throws Exception {
		Collection<RecertifiedRoleDefinitionEntity> roles = getRecertifiedRoleDefinitionEntityDao().findByProcessId(rp.getId());
		LinkedList<RecertifiedRoleDefinition> result = new LinkedList<RecertifiedRoleDefinition>( getRecertifiedRoleDefinitionEntityDao().toRecertifiedRoleDefinitionList(roles));
		Collections.sort(result, new Comparator<RecertifiedRoleDefinition>() {
			@Override
			public int compare(RecertifiedRoleDefinition o1, RecertifiedRoleDefinition o2) {
				if (o1.getRoleId() == null && o2.getRoleId() == null)
					return 0;
				else if (o1.getRoleName() == null)
					return 1;
				else if (o2.getRoleId() == null)
					return -1;
				int i = o1.getDescription().compareTo(o2.getDescription());
				if (i == 0) 
					i = o1.getRoleName().compareTo(o2.getRoleName());
				if (i == 0) 
					i = o1.getSystem().compareTo(o2.getSystem());
				return i;
			}
		});
		return result;
	}

	@Override
	protected void handleDeleteTemplate(RecertificationTemplate template) throws Exception {
		RecertificationTemplateEntity entity = getRecertificationTemplateEntityDao().load(template.getId());
		if (entity == null)
			throw new InternalErrorException ("Template "+template.getId()+" does not exist");
		getRecertificationTemplateEntityDao().recertificationTemplateToEntity(template, entity, true);
		
		getRecertificationTemplateEntityDao().remove(entity);
	}

	@Override
	protected List<RecertifiedRole> handleGetPendingRecertifiedRoles(RecertificationProcess rp) throws Exception {
		Collection<RecertifiedRoleEntity> roles = getRecertifiedRoleEntityDao().findByProcessId(rp.getId());
		LinkedList<RecertifiedRole> result = new LinkedList<RecertifiedRole>( getRecertifiedRoleEntityDao().toRecertifiedRoleList(roles));
		for (Iterator<RecertifiedRole> it = result.iterator(); it.hasNext();)
		{
			RecertifiedRole rre = it.next();
			boolean discard = true;
			if (rre.getRol() == null)
			{
				discard = true;
			}
			else if (rre.getCheck1() == null)
			{
				if (isValidToCheck (rre.getStep1Users() ))
					discard = false;
			}
			else if (rre.getCheck2() == null)
			{
				if (isValidToCheck (rre.getStep2Users() ))
					discard = false;
			}
			else if (rre.getCheck3() == null)
			{
				if (isValidToCheck (rre.getStep3Users() ))
					discard = false;
			}
			else if (rre.getCheck4() == null)
			{
				if (isValidToCheck (rre.getStep4Users() ))
					discard = false;
			}
			if (discard)
				it.remove();
		}
		Collections.sort(result, new Comparator<RecertifiedRole>() {
			@Override
			public int compare(RecertifiedRole o1, RecertifiedRole o2) {
				int i = (o1.getRol().getGroupDescription() == null ? "": o1.getRol().getGroupDescription()).
						compareTo(o2.getRol().getGroupDescription() == null ? "": o2.getRol().getGroupDescription());
				if (i == 0)
					i = o1.getRol().getUserFullName().compareTo(o2.getRol().getUserFullName());
				if (i == 0)
					i = o1.getRol().getRoleDescription().compareTo(o2.getRol().getRoleDescription());
				return i;
			}
		});
		return result;
	}

	@Override
	protected List<RecertifiedRoleDefinition> handleGetPendingRecertifiedRoleDefinitions(RecertificationProcess rp) throws Exception {
		Collection<RecertifiedRoleDefinitionEntity> roles = getRecertifiedRoleDefinitionEntityDao().findByProcessId(rp.getId());
		LinkedList<RecertifiedRoleDefinition> result = new LinkedList<RecertifiedRoleDefinition>( getRecertifiedRoleDefinitionEntityDao().toRecertifiedRoleDefinitionList(roles));
		for (Iterator<RecertifiedRoleDefinition> it = result.iterator(); it.hasNext();)
		{
			RecertifiedRoleDefinition rre = it.next();
			boolean discard = true;
			if (rre.getRoleName() == null)
			{
				discard = true;
			}
			else if (rre.getCheck1() == null)
			{
				if (isValidToCheck (rre.getStep1Users() ))
					discard = false;
			}
			else if (rre.getCheck2() == null)
			{
				if (isValidToCheck (rre.getStep2Users() ))
					discard = false;
			}
			else if (rre.getCheck3() == null)
			{
				if (isValidToCheck (rre.getStep3Users() ))
					discard = false;
			}
			else if (rre.getCheck4() == null)
			{
				if (isValidToCheck (rre.getStep4Users() ))
					discard = false;
			}
			if (discard)
				it.remove();
		}
		Collections.sort(result, new Comparator<RecertifiedRoleDefinition>() {
			@Override
			public int compare(RecertifiedRoleDefinition o1, RecertifiedRoleDefinition o2) {
				if (o1.getRoleId() == null && o2.getRoleId() == null)
					return 0;
				else if (o1.getRoleName() == null)
					return 1;
				else if (o2.getRoleId() == null)
					return -1;
				int i = o1.getDescription().compareTo(o2.getDescription());
				if (i == 0) 
					i = o1.getRoleName().compareTo(o2.getRoleName());
				if (i == 0) 
					i = o1.getSystem().compareTo(o2.getSystem());
				return i;
			}
		});
		return result;
	}

	@Override
	protected List<String> handleGetUsersToNotify(RecertificationProcess rp) throws Exception {
		Set<String> s = new HashSet<String>();
		for (RecertifiedRoleEntity role: getRecertifiedRoleEntityDao().findByProcessId(rp.getId()))
		{
			String r = role.getCheck1() == null ? role.getStep1Users() :
				role.getCheck2() == null ? role.getStep2Users() :
				role.getCheck3() == null ? role.getStep3Users() :
				role.getCheck4() == null ? role.getStep4Users():
				null;
			if ( r != null && ! r.trim().isEmpty())
			{
				for ( String rr: r.split(" +"))
					s.add (rr);
			}
		}
		return new LinkedList<String> (s);
	}

	@Override
	protected void handleNotifyUsers(RecertificationProcess rp, String url) throws Exception {
		RecertificationProcessEntity proc = getRecertificationProcessEntityDao().load(rp.getId());
		String msg = proc.getTemplate().getMessage();
		
		for ( String s: handleGetUsersToNotify(rp))
		{
			notifyUser(rp, s, url, msg);
		}
	}

	protected void notifyUser(RecertificationProcess rp, String user, String url, String msg)
			throws InternalErrorException {
		VariableResolver pResolver = new CustomVariableResolver (user, url);
		ExpressionEvaluatorImpl ee = new ExpressionEvaluatorImpl();
		FunctionMapper functions  = null;
		String txt = (String) ee.evaluate(msg, String.class, pResolver , functions);
				
		if (msg.contains("<"))
			getMailService().sendHtmlMailToActors(new String[] {user}, rp.getName(), txt);
		else
			getMailService().sendTextMailToActors(new String[] {user}, rp.getName(), txt);
	}

	@Override
	protected void handleDelegate(RecertifiedRole rr, String users) throws Exception {
		RecertifiedRoleEntity rre = getRecertifiedRoleEntityDao().load(rr.getId());
		com.soffid.iam.common.security.SoffidPrincipal p = Security.getSoffidPrincipal();
		if (rre.getCheck1() == null)
		{
			if (rre.getStep1Users() != null)
				rre.setStep1Users(users);
		}
		else if (rre.getCheck2() == null)
		{
			if (rre.getStep2Users() != null)
				rre.setStep2Users(users);
		}
		else if (rre.getCheck3() == null)
		{
			if (rre.getStep3Users() != null)
				rre.setStep3Users(users);
		}
		else if (rre.getCheck4() == null)
		{
			if (rre.getStep4Users() != null)
				rre.setStep4Users(users);
		}
		getRecertifiedRoleEntityDao().update(rre);
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleCheckByUser(com.soffid.iam.recertification.common.RecertifiedRole)
	 */
	@Override
	protected void handleCheck(RecertifiedRole rr, Boolean check, String comments) throws Exception {
		RecertifiedRoleEntity rre = getRecertifiedRoleEntityDao().load(rr.getId());
		if (comments != null && ! comments.trim().isEmpty()) {
			SoffidPrincipal p = Security.getSoffidPrincipal();
			if (rre.getComments() == null)
			{
				rre.setComments(p.getFullName()+":" +comments);
				getRecertifiedRoleEntityDao().update(rre);
			} else {
				if (comments.startsWith(rre.getComments()))
					rre.setComments( rre.getComments()+"\n"+p.getFullName()+":" +comments.substring(rre.getComments().length()));
				else
					rre.setComments( rre.getComments()+"\n"+p.getFullName()+":" +comments);
				getRecertifiedRoleEntityDao().update(rre);
			}
		}
		if (check != null)
			handleCheck(rr, check.booleanValue());
	}

	@Override
	protected AsyncList<RecertificationProcess> handleFindRecertificationProcessesByTextAndQueryAsync(final String text, final String query)
			throws Exception {
		final AsyncList<RecertificationProcess> result = new AsyncList<RecertificationProcess>();
		getAsyncRunnerService().run(new Runnable() {
			@Override
			public void run() {
				try {
					doFindReceritficationProcessByJsonQuery(text, query, null, null, result);
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}				
			}
		}, result);

		return result;
	}

	@Override
	protected PagedResult<RecertificationProcess> handleFindRecertificationProcessesByTextAndQuery(String text, String query, Integer first,
			Integer pageSize) throws Exception {
		final LinkedList<RecertificationProcess> result = new LinkedList<RecertificationProcess>();
		PagedResult<RecertificationProcess> pr = doFindReceritficationProcessByJsonQuery(text, query, first, pageSize, result);
		return pr;
	}

	private PagedResult<RecertificationProcess> doFindReceritficationProcessByJsonQuery(
			String text, String query, Integer first,
			Integer pageSize, List<RecertificationProcess> result) throws UnsupportedEncodingException, ClassNotFoundException, JSONException, InternalErrorException, EvalException, ParseException, TokenMgrError {
		final RecertificationProcessEntityDao dao = getRecertificationProcessEntityDao();
		ScimHelper h = new ScimHelper(RecertificationProcess.class);
		h.setPrimaryAttributes(new String[] { "name"});
		CriteriaSearchConfiguration config = new CriteriaSearchConfiguration();
		config.setFirstResult(first);
		config.setMaximumResultSize(pageSize);
		h.setConfig(config);
		h.setTenantFilter("tenant.id");

		h.setGenerator((entity) -> {
			RecertificationProcessEntity ne = (RecertificationProcessEntity) entity;
			return dao.toRecertificationProcess((RecertificationProcessEntity) entity);
		}); 
		
		h.search(text, query, (Collection) result); 

		PagedResult<RecertificationProcess> pagedResult = new PagedResult<RecertificationProcess>();
		pagedResult.setResources(result);
		pagedResult.setStartIndex( first != null ? first: 0);
		pagedResult.setItemsPerPage( pageSize );
		pagedResult.setTotalResults(h.count());
		return pagedResult;
	}

	@Override
	protected List<RecertificationProcess> handleFindActiveRecertificationProcesses ()
			throws Exception {
		final SoffidPrincipal p = Security.getSoffidPrincipal();
		final List<RecertificationProcess> result = new LinkedList<RecertificationProcess>();

		HashSet<Long> ids = new HashSet<Long>();
		List<String> roles = new LinkedList<>();
		roles.add(p.getUserName());
		for (String role: p.getSoffidRoles()) roles.add(role);
		doFindActiveRecertificationProcesses(result, ids, roles);

		return result;
	}


	private void doFindActiveRecertificationProcesses(List<RecertificationProcess> result,
			HashSet<Long> ids, List<String> roles) {
		StringBuffer sb = new StringBuffer();
		List<Parameter> params = new LinkedList<Parameter>();
		for (int i = 0; i < roles.size(); i++) {
			String r = roles.get(i);
			params.add(new Parameter ("s"+i, r+" %"));
			params.add(new Parameter ("e"+i, "% "+r));
			params.add(new Parameter ("m"+i, "% "+r+" %"));
			params.add(new Parameter ("c"+i, r));
			if (sb.length() > 0) sb.append(" or ");
			sb.append("r.check1 is null and r.step1Users like :s"+i+" or "
				+ " r.check2 is null and r.check1 is not null and r.step2Users like :s"+i+" or "
				+ " r.check3 is null and r.check2 is not null and r.step3Users like :s"+i+" or "
				+ " r.check4 is null and r.check3 is not null and r.step4Users like :s"+i+" or "
				+ " r.check1 is null and r.step1Users like :e"+i+" or "
				+ " r.check2 is null and r.check1 is not null and r.step2Users like :e"+i+" or "
				+ " r.check3 is null and r.check2 is not null and r.step3Users like :e"+i+" or "
				+ " r.check4 is null and r.check3 is not null and r.step4Users like :e"+i+" or "
				+ " r.check1 is null and r.step1Users like :m"+i+" or "
				+ " r.check2 is null and r.check1 is not null and r.step2Users like :m"+i+" or "
				+ " r.check3 is null and r.check2 is not null and r.step3Users like :m"+i+" or "
				+ " r.check4 is null and r.check3 is not null and r.step4Users like :m"+i+" or "
				+ " r.check1 is null and r.step1Users = :c"+i+" or "
				+ " r.check2 is null and r.check1 is not null and r.step2Users = :c"+i+" or "
				+ " r.check3 is null and r.check2 is not null and r.step3Users = :c"+i+" or "
				+ " r.check4 is null and r.check3 is not null and r.step4Users = :c"+i+" ");
		}
		
		Parameter[] paramArray = params.toArray(new Parameter[params.size()]);
		for (RecertificationProcessEntity o: getRecertificationProcessEntityDao().query(
				"select distinct o "
				+ "from com.soffid.iam.addons.recertification.model.RecertificationProcessEntityImpl as o "
				+ "join o.informationSystems as app "
				+ "join app.grants as r "
				+ "where o.status = 'A' and (" + sb.toString() + ")",
				paramArray))
		{
			if (!ids.contains(o.getId())) {
				ids.add(o.getId());
				result.add(getRecertificationProcessEntityDao().toRecertificationProcess(o));
			}
		}

		for (RecertificationProcessEntity o: getRecertificationProcessEntityDao().query(
				"select distinct o "
				+ "from com.soffid.iam.addons.recertification.model.RecertificationProcessEntityImpl as o "
				+ "join o.groups as g "
				+ "join g.users as u "
				+ "join u.roles as r "
				+ "where o.status = 'A' and (" + sb.toString() + ")",
				paramArray))
		{
			if (!ids.contains(o.getId())) {
				ids.add(o.getId());
				result.add(getRecertificationProcessEntityDao().toRecertificationProcess(o));
			}

		}
		
		for (RecertificationProcessEntity o: getRecertificationProcessEntityDao().query(
				"select distinct o "
				+ "from com.soffid.iam.addons.recertification.model.RecertificationProcessEntityImpl as o "
				+ "join o.informationSystems as app "
				+ "join app.roles as r "
				+ "where o.status = 'A' and (" + sb.toString() + ")",
				paramArray))
		{
			if (!ids.contains(o.getId())) {
				ids.add(o.getId());
				result.add(getRecertificationProcessEntityDao().toRecertificationProcess(o));
			}
		}


	}
	
	/* (non-Javadoc)
	 * @see com.soffid.iam.recertification.core.RecertificationServiceBase#handleCheckByUser(com.soffid.iam.recertification.common.RecertifiedRole)
	 */
	@Override
	protected void handleCheck(RecertifiedRoleDefinition rr, boolean check) throws Exception {
		RecertifiedRoleDefinitionEntity rre = getRecertifiedRoleDefinitionEntityDao().load(rr.getId());
		com.soffid.iam.common.security.SoffidPrincipal p = Security.getSoffidPrincipal();
		boolean success = false;
		if (rre.getCheck1() == null)
		{
			if (isValidToCheck (rre.getStep1Users() ))
			{
				rre.setCheck1(check);
				rre.setStep1Author(p.getUserName());
				rre.setStep1Date(new Date());
				success = true;
			}
			else
				throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
		}
		if (rre.getCheck2() == null)
		{
			if (isValidToCheck (rre.getStep2Users() ))
			{
				rre.setCheck2(check);
				rre.setStep2Author(p.getUserName());
				rre.setStep2Date(new Date());
				success = true;
			}
			else if (!success)
				throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
		}
		if (rre.getCheck3() == null)
		{
			if (isValidToCheck (rre.getStep3Users() ))
			{
				rre.setCheck3(check);
				rre.setStep3Author(p.getUserName());
				rre.setStep3Date(new Date());
				success = true;
			}
			else if (!success)
				throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
		}
		if (rre.getCheck4() == null)
		{
			if (isValidToCheck (rre.getStep4Users() ))
			{
				rre.setCheck4(check);
				rre.setStep4Author(p.getUserName());
				rre.setStep4Date(new Date());
				success = true;
			}
			else if (!success)
				throw new SecurityException (Messages.getString("RecertificationServiceImpl.NotAllowed")); //$NON-NLS-1$
		}
		if (success)
		{
			getRecertifiedRoleDefinitionEntityDao().update(rre);
			if ( isFinished (rre))
			{
				Security.nestedLogin(Security.getCurrentAccount(), new String[] {
						Security.AUTO_USER_QUERY+Security.AUTO_ALL,
						Security.AUTO_USER_UPDATE+Security.AUTO_ALL,
						Security.AUTO_USER_ROLE_DELETE+Security.AUTO_ALL,
						Security.AUTO_USER_ROLE_CREATE+Security.AUTO_ALL,
						Security.AUTO_USER_ROLE_QUERY+Security.AUTO_ALL,
						Security.AUTO_ROLE_UPDATE+Security.AUTO_ALL,
						Security.AUTO_APPLICATION_UPDATE+Security.AUTO_ALL,
						"internal:recertification-process"
					});
				try {
					RoleEntity rol = getRoleEntityDao().load(rre.getRoleId());
					Role role = getRoleEntityDao().toRole(rol);
					if (!check)
					{
						role.getOwnerRoles().clear();
						getApplicationService().update2(role);
					}
					getApplicationService().approveRoleDefinition(role);
				} finally {
					Security.nestedLogoff();
				}
				
				if ( rre.getInformationSystem() != null)
					checkISStatus(rre.getInformationSystem());
			}
		}
	}
	
	private boolean isFinished(RecertifiedRoleDefinitionEntity rre) {
		if (Boolean.FALSE.equals(rre.getCheck1())) {
			rre.setStep2Users(null);
			rre.setStep3Users(null);
			rre.setStep4Users(null);
			return true;
		}
		else if (Boolean.FALSE.equals(rre.getCheck2())) {
			rre.setStep3Users(null);
			rre.setStep4Users(null);
			return true;
		} 
		else if (Boolean.FALSE.equals(rre.getCheck3())) {
			rre.setStep4Users(null);
			return true;
		} 
		else if (Boolean.FALSE.equals(rre.getCheck4()))
			return true;
		else if  ( rre.getStep1Users() != null && ! rre.getStep1Users().trim().isEmpty() && rre.getCheck1() == null ||
				 rre.getStep2Users() != null && ! rre.getStep2Users().trim().isEmpty() && rre.getCheck2() == null ||
				 rre.getStep3Users() != null && ! rre.getStep3Users().trim().isEmpty() && rre.getCheck3() == null ||
				 rre.getStep4Users() != null && ! rre.getStep4Users().trim().isEmpty() && rre.getCheck4() == null )
			return false;
		else
			return true;
	}

	@Override
	protected void handleAddDependency(RecertifiedRoleDefinition rr, RoleGrant grant) throws Exception {
		Role role = getApplicationService().findRoleById(rr.getRoleId());

		boolean found = false;
		for (RoleGrant grant2: role.getOwnedRoles()) {
			if (grant2.getRoleId().equals(grant.getRoleId()) &&
					(grant2.getDomainValue() == null? grant.getDomainValue() == null : grant2.getDomainValue().equals(grant.getDomainValue()))) {
				grant2.setStatus(RoleDependencyStatus.STATUS_ACTIVE);
				found = true;
			}
		}
		if (!found) {
			grant.setStatus(RoleDependencyStatus.STATUS_TOAPPROVE);
			role.getOwnedRoles().add(grant);
		}
		Security.nestedLogin(new String[] {"internal:recertification-process",
			Security.AUTO_APPLICATION_UPDATE+Security.AUTO_ALL,
			Security.AUTO_ROLE_UPDATE+Security.AUTO_ALL});
		try {
			getApplicationService().update2(role);
		} finally {
			Security.nestedLogoff();
		}
	}

	@Override
	protected void handleApplyRoleDefinitionRecertification(RecertifiedRoleDefinition ru) throws Exception {
	}

	@Override
	protected void handleCheck(RecertifiedRoleDefinition rr, Boolean check, String comments) throws Exception {
		RecertifiedRoleDefinitionEntity rre = getRecertifiedRoleDefinitionEntityDao().load(rr.getId());
		if (comments != null && ! comments.trim().isEmpty()) {
			SoffidPrincipal p = Security.getSoffidPrincipal();
			if (rre.getComments() == null)
			{
				rre.setComments(p.getFullName()+":" +comments);
				getRecertifiedRoleDefinitionEntityDao().update(rre);
			} else {
				if (comments.startsWith(rre.getComments()))
					rre.setComments( rre.getComments()+"\n"+p.getFullName()+":" +comments.substring(rre.getComments().length()));
				else
					rre.setComments( rre.getComments()+"\n"+p.getFullName()+":" +comments);
				getRecertifiedRoleDefinitionEntityDao().update(rre);
			}
		}
		if (check != null)
			handleCheck(rr, check.booleanValue());
	}

	@Override
	protected void handleDelegate(RecertifiedRoleDefinition rr, String users) throws Exception {
		RecertifiedRoleDefinitionEntity rre = getRecertifiedRoleDefinitionEntityDao().load(rr.getId());
		com.soffid.iam.common.security.SoffidPrincipal p = Security.getSoffidPrincipal();
		if (rre.getCheck1() == null)
		{
			if (rre.getStep1Users() != null)
				rre.setStep1Users(users);
		}
		else if (rre.getCheck2() == null)
		{
			if (rre.getStep2Users() != null)
				rre.setStep2Users(users);
		}
		else if (rre.getCheck3() == null)
		{
			if (rre.getStep3Users() != null)
				rre.setStep3Users(users);
		}
		else if (rre.getCheck4() == null)
		{
			if (rre.getStep4Users() != null)
				rre.setStep4Users(users);
		}
		getRecertifiedRoleDefinitionEntityDao().update(rre);
	}

	@Override
	protected void handleRemoveDependency(RecertifiedRoleDefinition rr, RoleGrant g) throws Exception {
		Role role = getApplicationService().findRoleById(rr.getRoleId());
		boolean found = true;
		for (Iterator<RoleGrant> it = role.getOwnedRoles().iterator(); it.hasNext();) {
			RoleGrant g2 = it.next();
			if (g2.getRoleId().equals(g.getRoleId()) &&
					(g2.getDomainValue() == null? g.getDomainValue() == null : g2.getDomainValue().equals(g.getDomainValue()))) {
				if (g2.getStatus() == RoleDependencyStatus.STATUS_TOAPPROVE)
					it.remove();
				else
					g2.setStatus(RoleDependencyStatus.STATUS_TOREMOVE);
			}
		}
		Security.nestedLogin(new String[] {"internal:recertification-process", 
				Security.AUTO_APPLICATION_UPDATE+Security.AUTO_ALL,
				Security.AUTO_ROLE_UPDATE+Security.AUTO_ALL});
		try {
			getApplicationService().update2(role);
		} finally {
			Security.nestedLogoff();
		}
	}

	@Override
	protected void handleTriggerScalations(PrintWriter out) throws Exception {
		for (RecertificationProcessEntity process: getRecertificationProcessEntityDao().query("select a "
				+ "from com.soffid.iam.addons.recertification.model.RecertificationProcessEntity as a "
				+ "where type=:type and status=:status", 
				new Parameter[] {
						new Parameter("type", RecertificationType.ENTITLEMENTS),
						new Parameter("status", ProcessStatus.ACTIVE)
				})) {
			triggerScalations(process, out);
		}
	}

	private void triggerScalations(RecertificationProcessEntity process, PrintWriter out) throws InternalErrorException {
		RecertificationTemplateEntity template = process.getTemplate();
		HashSet<String> usersToNotify = new HashSet<>();
		HashSet<String> usersToReminder = new HashSet<>();
		
		for (RecertifiedRoleEntity e: getRecertifiedRoleEntityDao().findByProcessId(process.getId())) {
			if (! isFinished(e)) {
				boolean escalated = false;
				if (e.getAssignationDate() == null)
				{
					e.setAssignationDate(new Date());
					getRecertifiedRoleEntityDao().update(e);
				}
				if (e.getEscalationDate() == null && 
					template.getEscalation()!= null && template.getEscalation().intValue() > 0) {
					Calendar c = Calendar.getInstance();
					c.setTime(e.getAssignationDate());
					c.add(Calendar.DAY_OF_MONTH, template.getEscalation());
					if (c.getTime().before(new Date())) { 
						// Escalate
						escalate (template, process, e, usersToNotify, out);
						getRecertifiedRoleEntityDao().update(e);
					}
				}
				if ( template.getReminder() != null && template.getReminder().intValue() > 0) {
					Calendar c = Calendar.getInstance();
					if (e.getReminderDate() != null)
						c.setTime(e.getAssignationDate());
					else
						c.setTime(e.getReminderDate());
					c.add(Calendar.DAY_OF_MONTH, template.getEscalation());
					if (c.getTime().before(new Date())) { 
						// Escalate
						reminder (template, process, e, usersToReminder);
						e.setReminderDate(new Date());
						getRecertifiedRoleEntityDao().update(e);
					}
				}
			}
		}
		
		RecertificationProcess proc = getRecertificationProcessEntityDao().toRecertificationProcess(process);
		String url = getBaseUrl();
		for ( String s: usersToNotify)
		{
			out.println("Escalating process "+process.getName()+" to "+s);
			notifyUser(proc, s, url, template.getEscalationMessage());
		}
		for ( String s: usersToReminder)
		{
			if (! usersToNotify.contains(s)) {
				out.println("Sending reminder for process "+process.getName()+" to "+s);
				notifyUser(proc, s, url, template.getReminderMessage());
			}
		}
	}

	private void escalate(RecertificationTemplateEntity template, 
			RecertificationProcessEntity process, 
			RecertifiedRoleEntity e, 
			HashSet<String> usersToNotify, PrintWriter out) {
		Map<String, Object> vars = new HashMap<>();
		RoleAccountEntity ra = getRoleAccountEntityDao().load(e.getRolAccountId());
		vars.put("grant", getRoleAccountEntityDao().toRoleGrant(ra));
		String newUser;
		try {
			newUser = (String) eval (template.getEscalationAddress(), vars, "escalation script");
			if (newUser != null && ! newUser.trim().isEmpty()) {
				for (String user: newUser.split(" +"))
					usersToNotify.add(newUser);
				if (e.getStep1Users() != null && e.getStep1Author() == null)
					e.setStep1Users(newUser);
				else if (e.getStep2Users() != null && e.getStep2Author() == null)
					e.setStep2Users(newUser);
				else if (e.getStep3Users() != null && e.getStep3Author() == null)
					e.setStep3Users(newUser);
				else if (e.getStep4Users() != null && e.getStep4Author() == null)
					e.setStep4Users(newUser);
				e.setEscalationDate(new Date());
				e.setReminderDate(new Date());
			}
		} catch (Exception e1) {
			out.println("Error evaluating escalation script for process "+process.getName());
			e1.printStackTrace(out);
		}
		
	}

	private void reminder(RecertificationTemplateEntity template, RecertificationProcessEntity process, RecertifiedRoleEntity e, HashSet<String> usersToNotify) {
		if (e.getStep1Users() != null && e.getStep1Author() == null)
			for (String s: e.getStep1Users().split(" +"))
				usersToNotify.add(s);
		else if (e.getStep2Users() != null && e.getStep2Author() == null)
			for (String s: e.getStep2Users().split(" +"))
				usersToNotify.add(s);
		else if (e.getStep3Users() != null && e.getStep3Author() == null)
			for (String s: e.getStep3Users().split(" +"))
				usersToNotify.add(s);
		else if (e.getStep4Users() != null && e.getStep4Author() == null)
			for (String s: e.getStep4Users().split(" +"))
				usersToNotify.add(s);
	}
}

