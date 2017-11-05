package com.soffid.iam.recertification.bpm;

import java.util.Calendar;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.RecertificationService;

import es.caib.seycon.ng.ServiceLocator;
import es.caib.seycon.ng.utils.Security;

public class EndProcessHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {
		Security.nestedLogin("anonymous", new String []{ "recertification:manage"} );
		try {
			RecertificationService svc = (RecertificationService)

			ServiceLocator.instance().getService(com.soffid.iam.addons.recertification.core.RecertificationService.SERVICE_NAME);
		
			Long id = (Long) executionContext.getVariable(Constants.RECERTIFICATION_ID_VAR);
			RecertificationProcess rp = svc.getRecertificationProcess(id);

			boolean allFinished = true;
			for (RecertifiedInformationSystem rg : svc.getRecertifiedInformationSystems(rp))
			{
				if (rg.getStatus() == ProcessStatus.ACTIVE || rg.getStatus() == ProcessStatus.PREPARATION)
				{
					allFinished = false;
					break;
				}
			}
			if (allFinished)
			{
				rp.setStatus(ProcessStatus.FINISHED);
				rp.setFinishDate(Calendar.getInstance());
				svc.update(rp);

				ProcessInstance pi = executionContext.getJbpmContext().getProcessInstance(rp.getWorkflowId());
				if (! pi.hasEnded())
					pi.signal("End");
			}
		} finally {
			Security.nestedLogoff();
		}
	}

}
