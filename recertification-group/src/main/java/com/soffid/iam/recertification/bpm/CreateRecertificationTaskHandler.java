/**
 * 
 */
package com.soffid.iam.recertification.bpm;

import java.util.Calendar;

import javax.naming.InitialContext;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

import es.caib.seycon.ng.servei.SeyconServiceLocator;

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
		RecertificationServiceHome home =
				(RecertificationServiceHome) new InitialContext()
						.lookup(RecertificationServiceHome.JNDI_NAME);
		com.soffid.iam.addons.recertification.core.ejb.RecertificationService ejb =
				home.create();

		Long processId =
				(Long) executionContext.getVariable(Constants.RECERTIFICATION_ID_VAR);
		
		Long grupId = (Long) executionContext.getVariable(Constants.RECERTIFICATION_GROUP_ID_VAR);

		RecertificationProcess process = ejb.getRecertificationProcess(processId);
		RecertifiedGroup rg = ejb.getRecertifiedGroup(grupId);
		
		ejb.startUserRecertification(rg);

		boolean anyTaskToDo = false;
		for (RecertifiedUser ru : ejb.getRecertifiedUsers(rg))
		{
			if (ru.getActiveAccount().booleanValue())
			{
				ProcessDefinition def =
						executionContext.getJbpmContext().getGraphSession()
								.findLatestProcessDefinition(Constants.userProcessName);
				ProcessInstance pi = def.createProcessInstance();
				pi.getContextInstance().setVariable(Constants.RECERTIFICATION_ID_VAR,
						processId);
				pi.getContextInstance().setVariable(
						Constants.RECERTIFICATION_GROUP_ID_VAR, grupId);
				pi.getContextInstance().setVariable(
						Constants.RECERTIFICATION_USER_ID_VAR, ru.getId());
				pi.getContextInstance().setVariable(Constants.BOSS_ROLE,
						rg.getManagerRole());
				executionContext.getJbpmContext().save(pi);
				pi.fireStartEvent(def.getStartState());
				ru.setWorkflowId(pi.getId());
				ejb.setRecertifiedUserWorkflowId(ru, pi.getId());
				pi.signal();
				anyTaskToDo = true;
			}
		}
		
		executionContext.getToken().signal();
		if (!anyTaskToDo)
			executionContext.getToken().signal();

	}
}
