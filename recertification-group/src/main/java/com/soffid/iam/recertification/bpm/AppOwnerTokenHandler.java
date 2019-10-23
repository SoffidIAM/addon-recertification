/**
 * 
 */
package com.soffid.iam.recertification.bpm;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.naming.InitialContext;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.RecertificationServiceLocator;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;
import com.soffid.iam.addons.recertification.common.RecertifiedRole;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

import es.caib.bpm.toolkit.exception.UserWorkflowException;
import es.caib.seycon.ng.EJBLocator;
import es.caib.seycon.ng.ServiceLocator;
import es.caib.seycon.ng.comu.Dispatcher;
import es.caib.seycon.ng.comu.Grup;
import es.caib.seycon.ng.comu.RolGrant;
import es.caib.seycon.ng.comu.Usuari;
import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.exception.UnknownUserException;
import es.caib.seycon.ng.servei.AplicacioService;
import es.caib.seycon.ng.servei.UsuariService;
import es.caib.seycon.ng.utils.Security;

/**
 * @author (C) Soffid 2013
 * 
 */
public class AppOwnerTokenHandler implements ActionHandler
{

	/*
	 * (non-Javadoc)
	 * @see
	 * org.jbpm.graph.def.ActionHandler#execute(org.jbpm.graph.exe.ExecutionContext
	 * )
	 */
	public void execute (ExecutionContext executionContext) throws Exception
	{
		Security.nestedLogin(Security.getCurrentAccount(), new String [] {
				Security.AUTO_USER_QUERY+Security.AUTO_ALL,
				Security.AUTO_APPLICATION_QUERY+Security.AUTO_ALL,
				Security.AUTO_USER_ROLE_QUERY+Security.AUTO_ALL,
				Security.AUTO_GROUP_QUERY+Security.AUTO_ALL,
				"recertification:manage"
		});
		try
		{
			RecertificationService ejb = RecertificationServiceLocator.instance().getRecertificationService();
	
			Long processId =
					(Long) executionContext.getVariable(Constants.RECERTIFICATION_ID_VAR);
			
			Long groupId = (Long) executionContext.getVariable(Constants.RECERTIFICATION_GROUP_ID_VAR);
	
			RecertificationProcess process = ejb.getRecertificationProcess(processId);
			RecertifiedGroup rg = ejb.getRecertifiedGroup(groupId);
			List<RecertifiedInformationSystem> is = ejb.getRecertifiedInformationSystems(process);
			
			
			// Fills appToRecertieidIS map
			HashMap<String, RecertifiedInformationSystem> appToRecertifiedIS = new HashMap<String, RecertifiedInformationSystem>();
			for (RecertifiedInformationSystem ris: is)
			{
				appToRecertifiedIS.put(ris.getInformationSystem(), ris);
			}
	
			// Calculate system ids to recertify by app owner
			HashSet<Long> affectedSystemIds = new HashSet<Long>();
			for (RecertifiedUser ru: ejb.getRecertifiedUsers(rg))
			{
				if (ru.getBossReview() == null)
					throw new UserWorkflowException("Recertification for user "+ru.getUser()+" is not done yet");
				boolean pending = false;
				for (RecertifiedRole rr: ejb.getRecertifiedRoles(ru))
				{
					String appName = rr.getRol().getInformationSystemName();
					RecertifiedInformationSystem ris = appToRecertifiedIS.get(appName);
					if (isUserInRole(ris.getAppOwnerRole()))
					{
						ejb.checkByAppOwner(rr, rr.isCheckedByBoss());
					}
					else
					{
						pending = true;
						affectedSystemIds.add(ris.getId());
					}
				}
				ejb.update(ru);
			}
	
			// Launc tokens
			if (affectedSystemIds.isEmpty())
				executionContext.getToken().signal("No further review needed");
			else
			{
				for (Long id: affectedSystemIds)
				{
					Token t = new Token(executionContext.getToken(), id.toString());
					ExecutionContext ctx = new ExecutionContext(t);
					ctx.setVariable(Constants.RECERTIFICATION_SYSTEM_ID_VAR, id);
					t.signal("App owner review");
				}
			}
		} finally {
			Security.nestedLogoff();
		}
	}
	
	private boolean isUserInRole (String roleName) throws InternalErrorException, UnknownUserException
	{
		UsuariService usuariService = ServiceLocator.instance().getUsuariService();
		AplicacioService appService = ServiceLocator.instance().getAplicacioService();
		
		Usuari userData = usuariService.getCurrentUsuari();
		LinkedList<String> userGroups = new LinkedList<String>();

		if (userData != null)
		{
			Dispatcher defaultDispatcher = ServiceLocator.instance().getDispatcherService().findSoffidDispatcher();
			userGroups.add(userData.getCodi());
			Collection<Grup> grups = usuariService
					.getUserGroupsHierarchy(userData.getId());
			for (Grup grup : grups) {
				userGroups.add(grup.getCodi());
			}
			
			Collection<RolGrant> roles = appService.findEffectiveRolGrantByUser(userData.getId());
			for (RolGrant role : roles) {
				String name = role.getRolName();
				if (role.getDispatcher().equals(defaultDispatcher.getCodi())) //$NON-NLS-1$
					userGroups.add(name);
				name = name + "@" + role.getDispatcher(); //$NON-NLS-1$
				userGroups.add(name);
				// Now without domain
				if (role.getDomainValue() != null)
				{
					name = role.getRolName();
					name = name + "/" + role.getDomainValue(); //$NON-NLS-1$
					if (role.getDispatcher().equals(defaultDispatcher.getCodi())) //$NON-NLS-1$
						userGroups.add(name);
					name = name + "@" + role.getDispatcher(); //$NON-NLS-1$
					userGroups.add(name);
				}
			}
			
			for (String auth: Security.getAuthorizations())
			{
				userGroups.add ("auth:"+auth); //$NON-NLS-1$
			}
		}
		userGroups.add("tothom"); //$NON-NLS-1$
		return userGroups.contains(roleName);
	}
}
