package com.soffid.iam.recertification.bpm;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.RecertificationService;

import es.caib.seycon.ng.ServiceLocator;
import es.caib.seycon.ng.utils.Security;

public class ProcessFinishedHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {
		Security.nestedLogin(Security.getCurrentUser(), new String []{ "recertification:manage"} );
		try {
			
		} finally {
			Security.nestedLogoff();
		}
	}

}
