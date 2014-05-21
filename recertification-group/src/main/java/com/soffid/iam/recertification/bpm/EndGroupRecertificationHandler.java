package com.soffid.iam.recertification.bpm;
import java.util.Calendar;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.RecertificationService;

import es.caib.seycon.ng.ServiceLocator;
import es.caib.seycon.ng.utils.Security;


public class EndGroupRecertificationHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {
		Security.nestedLogin(Security.getCurrentUser(), new String []{ "recertification:manage"} );
		try {
			RecertificationService svc = (RecertificationService)
					ServiceLocator.instance().getService(com.soffid.iam.addons.recertification.core.RecertificationService.SERVICE_NAME);
			
			Long id = (Long) executionContext.getVariable(Constants.RECERTIFICATION_ID_VAR);
			RecertificationProcess rp = svc.getRecertificationProcess(id);

			boolean allFinished = true;
			for (RecertifiedGroup rg : svc.getRecertifiedGroups(rp))
			{
				if (rg.getStatus() == ProcessStatus.ACTIVE)
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
