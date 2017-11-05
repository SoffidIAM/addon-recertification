package com.soffid.iam.recertification.bpm;

import javax.naming.InitialContext;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.RecertificationServiceLocator;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

import es.caib.seycon.ng.utils.Security;

public class ChangeTaskNameHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {
		Long id = (Long) executionContext.getVariable(Constants.RECERTIFICATION_GROUP_ID_VAR);
		
		Security.nestedLogin("nobody", new String [] {
				Security.AUTO_USER_QUERY+Security.AUTO_ALL,
				Security.AUTO_APPLICATION_QUERY+Security.AUTO_ALL,
				Security.AUTO_USER_ROLE_QUERY+Security.AUTO_ALL,
				Security.AUTO_GROUP_QUERY+Security.AUTO_ALL,
				"recertification:manage"
		});
		try
		{
			RecertificationService ejb = RecertificationServiceLocator.instance().getRecertificationService();

			RecertifiedGroup ru = ejb.getRecertifiedGroup(id);
			
			TaskInstance ti =  executionContext.getTaskInstance();
			ti.setName(String.format(Messages.getString("ChangeTaskNameHandler.Name"), ti.getName(), ru.getGroup())); //$NON-NLS-1$
		} finally {
			Security.nestedLogoff();
		}
	}

}
