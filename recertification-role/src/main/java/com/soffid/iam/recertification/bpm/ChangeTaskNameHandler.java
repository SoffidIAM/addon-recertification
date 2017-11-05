package com.soffid.iam.recertification.bpm;

import javax.naming.InitialContext;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

public class ChangeTaskNameHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {
		Long id = (Long) executionContext.getVariable(Constants.RECERTIFICATION_SYSTEM_ID_VAR);
		
		com.soffid.iam.addons.recertification.core.ejb.RecertificationService ejb =
				(com.soffid.iam.addons.recertification.core.ejb.RecertificationService) new InitialContext()
					.lookup(RecertificationServiceHome.JNDI_NAME);

		RecertifiedInformationSystem ru = ejb.getRecertifiedInformationSystem(id);
		
		TaskInstance ti =  executionContext.getTaskInstance();
		ti.setName(String.format(Messages.getString("ChangeTaskNameHandler.Name"), ti.getName(), ru.getInformationSystem())); //$NON-NLS-1$
	}

}
