package com.soffid.iam.recertification.bpm;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.RecertificationService;

import es.caib.seycon.ng.ServiceLocator;
import es.caib.seycon.ng.utils.Security;

public class EndProcessHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {
		Security.nestedLogin(Security.getCurrentUser(), new String []{ "recertification:manage"} );
		try {
			RecertificationService svc = (RecertificationService)
					ServiceLocator.instance().getService(com.soffid.iam.addons.recertification.core.RecertificationService.SERVICE_NAME);
			
			Long groupId = (Long) executionContext.getVariable(Constants.RECERTIFICATION_GROUP_ID_VAR);

			RecertifiedGroup rg = svc.getRecertifiedGroup(groupId);
			boolean allFinished = true;
			for (RecertifiedUser ru : svc.getRecertifiedUsers(rg))
			{
				if (ru.getStatus().equals ( ProcessStatus.ACTIVE) ||
						ru.getStatus().equals ( ProcessStatus.PREPARATION))
				{
					allFinished = false;
					break;
				}
			}

			if (allFinished)
			{
				rg.setStatus(ProcessStatus.FINISHED);
				svc.update(rg);
				ProcessInstance pi = executionContext.getJbpmContext().getProcessInstance(rg.getWorkflowId());
				if (!pi.hasEnded())
					pi.signal("End");
			}
		} finally {
			Security.nestedLogoff();
		}
	}

}
