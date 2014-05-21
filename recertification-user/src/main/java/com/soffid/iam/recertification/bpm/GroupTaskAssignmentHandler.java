package com.soffid.iam.recertification.bpm;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;

import com.soffid.iam.addons.recertification.Constants;

public class GroupTaskAssignmentHandler implements AssignmentHandler {

	public void assign(Assignable assignable, ExecutionContext executionContext)
			throws Exception {
		assignable.setPooledActors(new String[] { (String) executionContext.getVariable(Constants.BOSS_ROLE)});
	}

}
