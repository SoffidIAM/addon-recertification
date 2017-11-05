package com.soffid.iam.recertification.bpm;

import javax.naming.InitialContext;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

public class StartGroupRecertificationHandler implements ActionHandler {

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		com.soffid.iam.addons.recertification.core.ejb.RecertificationService ejb =
				(RecertificationService) new InitialContext()
					.lookup(RecertificationServiceHome.JNDI_NAME);

		Long processId =
				(Long) executionContext.getVariable(Constants.RECERTIFICATION_ID_VAR);
		
		Long systemId = (Long) executionContext.getVariable(Constants.RECERTIFICATION_GROUP_ID_VAR);

		RecertificationProcess process = ejb.getRecertificationProcess(processId);
		RecertifiedGroup rg = ejb.getRecertifiedGroup(systemId);
		ejb.startUserRecertification(rg);
	}

}
