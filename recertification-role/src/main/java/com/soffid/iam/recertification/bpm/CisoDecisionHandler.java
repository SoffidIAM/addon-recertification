package com.soffid.iam.recertification.bpm;

import javax.naming.InitialContext;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

public class CisoDecisionHandler implements DecisionHandler {

	@Override
	public String decide(ExecutionContext executionContext) throws Exception {
		com.soffid.iam.addons.recertification.core.ejb.RecertificationService ejb =
				(com.soffid.iam.addons.recertification.core.ejb.RecertificationService) new InitialContext()
					.lookup(RecertificationServiceHome.JNDI_NAME);

		Long processId =
				(Long) executionContext.getVariable(Constants.RECERTIFICATION_ID_VAR);
		
		RecertificationProcess process = ejb.getRecertificationProcess(processId);
		if (process.getCisoReview() != null && process.getCisoReview().booleanValue())
			return "yes";
		else
			return "no";
	}

}
