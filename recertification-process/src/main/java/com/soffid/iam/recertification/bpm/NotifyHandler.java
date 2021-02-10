package com.soffid.iam.recertification.bpm;

import java.util.List;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.job.Timer;

import com.soffid.iam.ServiceLocator;
import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedUser;
import com.soffid.iam.addons.recertification.core.RecertificationService;
import com.soffid.iam.utils.ConfigurationCache;

import es.caib.seycon.ng.utils.Security;

public class NotifyHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {
		Security.nestedLogin("anonymuos", new String []{ "recertification:manage"} );
		try {
			RecertificationService svc = (RecertificationService) ServiceLocator.instance().getService( RecertificationService.SERVICE_NAME );
			Long id = (Long) executionContext.getVariable(Constants.RECERTIFICATION_ID_VAR);
			RecertificationProcess rp = svc.getRecertificationProcess( id );
			List<String> pending = svc.getUsersToNotify(rp);
			if (pending == null || pending.isEmpty())
			{
				Timer t = executionContext.getTimer();
				if (t != null)
					t.setTransitionName("End");
				else
					executionContext.leaveNode();
			}
			else
			{
				String url = ConfigurationCache.getProperty("soffid.externalURL")+"/selfservice/index.zul?target=addon/recertification/recertification.zul%3fid="+id;
				svc.notifyUsers(rp, url);
			}
		} finally {
			Security.nestedLogoff();
		}
	}

}
