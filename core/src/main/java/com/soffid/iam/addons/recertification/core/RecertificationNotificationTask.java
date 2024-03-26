package com.soffid.iam.addons.recertification.core;

import java.io.PrintWriter;

import com.soffid.iam.api.ScheduledTask;
import com.soffid.iam.service.TaskHandler;

import es.caib.seycon.ng.ServiceLocator;

public class RecertificationNotificationTask implements TaskHandler {
	public static final String HANDLER_NAME = "RecertificationNotificationTask";
	ScheduledTask task;
	
	@Override
	public ScheduledTask getTask() {
		return task;
	}

	@Override
	public void run(PrintWriter out) throws Exception {
		out.println("Checking recertification processes");
		try {
			RecertificationService svc = (RecertificationService) ServiceLocator.instance().getService(RecertificationService.SERVICE_NAME);
			svc.triggerScalations(out);
		} catch (Exception e) {
			out.println("Error processing recertification processes");
			e.printStackTrace(out);
		}
	}

	@Override
	public void setTask(ScheduledTask arg0) {
		task = arg0;
	}

}
