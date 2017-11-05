package com.soffid.iam.recertification.bpm;

import javax.naming.InitialContext;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.jbpm.taskmgmt.exe.TaskInstance;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.RecertificationServiceLocator;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

import es.caib.seycon.ng.utils.Security;

public class CisoAssignmentHandler implements AssignmentHandler {

	public void assign(Assignable assignable, ExecutionContext executionContext)
			throws Exception {
		Long id = (Long) executionContext.getVariable(Constants.RECERTIFICATION_ID_VAR);
		
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

			RecertificationProcess ru = ejb.getRecertificationProcess(id);
			assignable.setPooledActors(new String[] { ru.getCisoRole()});
		} finally {
			Security.nestedLogoff();
		}
	}

}
