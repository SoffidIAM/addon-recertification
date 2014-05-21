package com.soffid.iam.recertification.bpm;

import javax.naming.InitialContext;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

public class UserTaskAssignmentHandler implements AssignmentHandler {

	public void assign(Assignable assignable, ExecutionContext executionContext)
			throws Exception {
		Long id = (Long) executionContext.getVariable(Constants.RECERTIFICATION_USER_ID_VAR);
		
		RecertificationServiceHome home =
				(RecertificationServiceHome) new InitialContext()
						.lookup(RecertificationServiceHome.JNDI_NAME);
		com.soffid.iam.addons.recertification.core.ejb.RecertificationService ejb =
				home.create();

		RecertifiedUser ru = ejb.getRecertifiedUser(id);
		
		assignable.setActorId( ru.getUser() );
	}

}
