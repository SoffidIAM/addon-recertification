package com.soffid.iam.recertification.bpm;

import javax.naming.InitialContext;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

public class BossAssignmentHandler implements AssignmentHandler {

	public void assign(Assignable assignable, ExecutionContext executionContext)
			throws Exception {
		Long id = (Long) executionContext.getVariable(Constants.RECERTIFICATION_GROUP_ID_VAR);
		
		com.soffid.iam.addons.recertification.core.ejb.RecertificationService ejb =
				(RecertificationService) new InitialContext()
					.lookup(RecertificationServiceHome.JNDI_NAME);

		RecertifiedGroup ru = ejb.getRecertifiedGroup(id);
		
		assignable.setPooledActors(new String [] {ru.getManagerRole()});
	}

}
