/**
 * 
 */
package com.soffid.iam.recertification.bpm;

import javax.naming.InitialContext;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertificationType;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

/**
 * @author (C) Soffid 2013
 * 
 */
public class CreateRecertificationTaskHandler implements ActionHandler
{

	/*
	 * (non-Javadoc)
	 * @see
	 * org.jbpm.graph.def.ActionHandler#execute(org.jbpm.graph.exe.ExecutionContext
	 * )
	 */
	public void execute (ExecutionContext executionContext) throws Exception
	{
		boolean usersToRecetify = false;	// Recertified users to check
		com.soffid.iam.addons.recertification.core.ejb.RecertificationService ejb =
				(RecertificationService) new InitialContext()
					.lookup(RecertificationServiceHome.JNDI_NAME);

		Long processId =
				(Long) executionContext.getVariable(Constants.RECERTIFICATION_ID_VAR);

		RecertificationProcess process = ejb.getRecertificationProcess(processId);

		
		if (process.getType().equals(RecertificationType.ENTITLEMENTS))
		{
			ejb.startGroupRecertificationProcess(process);
			for (RecertifiedGroup rg : ejb.getRecertifiedGroups(process))
			{
				// Check recertified users to group
				if (!ejb.getRecertifiedUsers(rg).isEmpty())
				{
					usersToRecetify = true;
					
					ProcessDefinition def = executionContext
							.getJbpmContext()
							.getGraphSession()
							.findLatestProcessDefinition(Constants.groupProcessName);
					ProcessInstance pi = def.createProcessInstance();
					pi.getContextInstance().setVariable(
							Constants.RECERTIFICATION_ID_VAR, processId);
					pi.getContextInstance().setVariable(
							Constants.RECERTIFICATION_GROUP_ID_VAR, rg.getId());
					pi.getContextInstance().setVariable(Constants.BOSS_ROLE,
							rg.getManagerRole());
					executionContext.getJbpmContext().save(pi);
					pi.fireStartEvent(def.getStartState());
					pi.signal();
					rg.setProcessId(pi.getId());
					ejb.setRecertifiedGroupWorkflowId(rg, pi.getId());
				}
			}
		} else {
			ejb.startInformationSystemRecertificationProcess(process);
			for (RecertifiedInformationSystem rg : ejb.getRecertifiedInformationSystems(process))
			{
				// Check recertified users to group
				usersToRecetify = true;
				
				ProcessDefinition def = executionContext
						.getJbpmContext()
						.getGraphSession()
						.findLatestProcessDefinition(Constants.isProcessName );
				ProcessInstance pi = def.createProcessInstance();
				pi.getContextInstance().setVariable(
						Constants.RECERTIFICATION_ID_VAR, processId);
				pi.getContextInstance().setVariable(
						Constants.RECERTIFICATION_SYSTEM_ID_VAR, rg.getId());
				pi.getContextInstance().setVariable(Constants.BOSS_ROLE,
						rg.getAppOwnerRole());
				executionContext.getJbpmContext().save(pi);
				pi.fireStartEvent(def.getStartState());
				pi.signal();
				rg.setProcessId(pi.getId());
				ejb.setRecertifiedInformationSystemWorkflowId(rg, pi.getId());
			}
		}
		
		// Check users to recertify
		if (usersToRecetify)
			executionContext.getToken().signal();
		
		else
			executionContext.getToken().end();
	}
}
