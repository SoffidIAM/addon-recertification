package com.soffid.iam.recertification.bpm;

import javax.naming.InitialContext;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

public class ChangeTaskNameHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {
		Long id = (Long) executionContext.getVariable(Constants.RECERTIFICATION_USER_ID_VAR);
		
		RecertificationServiceHome home =
				(RecertificationServiceHome) new InitialContext()
						.lookup(RecertificationServiceHome.JNDI_NAME);
		com.soffid.iam.addons.recertification.core.ejb.RecertificationService ejb =
				home.create();

		RecertifiedUser ru = ejb.getRecertifiedUser(id);
		
		TaskInstance ti =  executionContext.getTaskInstance();
		ti.setName(String.format(Messages.getString("ChangeTaskNameHandler.Name"), ti.getName(), ru.getUser())); //$NON-NLS-1$
	}

}
