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
		com.soffid.iam.addons.recertification.core.ejb.RecertificationService ejb =
				(RecertificationService) new InitialContext()
					.lookup(RecertificationServiceHome.JNDI_NAME);

		Long processId =
				(Long) executionContext.getVariable(Constants.RECERTIFICATION_ID_VAR);

		RecertificationProcess process = ejb.getRecertificationProcess(processId);

		ejb.startRecertificationProcess(process);

		executionContext.leaveNode();
	}
}
