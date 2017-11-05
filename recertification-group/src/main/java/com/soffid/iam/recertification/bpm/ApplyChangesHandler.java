/**
 * 
 */
package com.soffid.iam.recertification.bpm;

import java.util.Calendar;

import javax.naming.InitialContext;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.RecertificationServiceLocator;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

import es.caib.seycon.ng.utils.Security;

/**
 * @author (C) Soffid 2013
 * 
 */
public class ApplyChangesHandler implements ActionHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jbpm.graph.def.ActionHandler#execute(org.jbpm.graph.exe.ExecutionContext
	 * )
	 */
	public void execute(ExecutionContext executionContext) throws Exception {
		Security.nestedLogin("nobody", new String[] {
				Security.AUTO_USER_QUERY + Security.AUTO_ALL,
				Security.AUTO_APPLICATION_QUERY + Security.AUTO_ALL,
				Security.AUTO_USER_ROLE_QUERY + Security.AUTO_ALL,
				Security.AUTO_GROUP_QUERY + Security.AUTO_ALL,
				"recertification:manage" });
		try {
			RecertificationService ejb = RecertificationServiceLocator
					.instance().getRecertificationService();

			Long processId = (Long) executionContext
					.getVariable(Constants.RECERTIFICATION_ID_VAR);

			Long systemId = (Long) executionContext
					.getVariable(Constants.RECERTIFICATION_GROUP_ID_VAR);

			RecertificationProcess process = ejb
					.getRecertificationProcess(processId);
			RecertifiedGroup rg = ejb.getRecertifiedGroup(systemId);
			rg.setStatus(ProcessStatus.FINISHED);
			ejb.update(rg);
			for (RecertifiedUser ru : ejb.getRecertifiedUsers(rg)) {
				ejb.applyUserRecertification(ru);
			}
			executionContext.getToken().signal();
		} finally {
			Security.nestedLogoff();
		}
	}
}
