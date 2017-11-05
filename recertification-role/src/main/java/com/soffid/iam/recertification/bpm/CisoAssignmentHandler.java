package com.soffid.iam.recertification.bpm;

import javax.naming.InitialContext;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.jbpm.taskmgmt.exe.TaskInstance;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

public class CisoAssignmentHandler implements AssignmentHandler {

	public void assign(Assignable assignable, ExecutionContext executionContext)
			throws Exception {
		Long id = (Long) executionContext.getVariable(Constants.RECERTIFICATION_ID_VAR);
		
		com.soffid.iam.addons.recertification.core.ejb.RecertificationService ejb =
				(com.soffid.iam.addons.recertification.core.ejb.RecertificationService) new InitialContext()
					.lookup(RecertificationServiceHome.JNDI_NAME);

		RecertificationProcess ru = ejb.getRecertificationProcess(id);
		assignable.setPooledActors(new String[] { ru.getCisoRole()});
	}

}
