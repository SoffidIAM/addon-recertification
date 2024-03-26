package com.soffid.iam.addons.recertification.core;

import com.soffid.iam.api.ScheduledTask;
import com.soffid.iam.api.ScheduledTaskHandler;
import com.soffid.iam.api.Tenant;

public class RecertificationServiceBootImpl extends RecertificationServiceBootBase {

	@Override
	protected void handleConsoleBoot() throws Exception {
	}

	@Override
	protected void handleSyncServerBoot() throws Exception {
		ScheduledTask task = getScheduledTaskService().findScheduledTaskByHandlerAndParams(RecertificationNotificationTask.HANDLER_NAME, "-");
		if (task == null ) {
			// Ensure handler exists
			boolean handlerExists = false;
			for ( ScheduledTaskHandler handler: getScheduledTaskService().listHandlers())
			{
				if (handler.getName().equals(RecertificationNotificationTask.HANDLER_NAME))
				{
					handlerExists = true;
					break;
				}
			}
			if ( ! handlerExists)
			{
				ScheduledTaskHandler handler = new ScheduledTaskHandler();
				handler.setClassName(RecertificationNotificationTask.class.getName());
				handler.setName(RecertificationNotificationTask.HANDLER_NAME);
				getScheduledTaskService().create(handler);
			}
			task = new ScheduledTask();
			task.setName("Recertification escalation task");
			task.setEnabled(false);
			task.setHandlerName(RecertificationNotificationTask.HANDLER_NAME);
			task.setParams("-");
			task.setServerName("*");
			task.setDayPattern("*");
			task.setDayOfWeekPattern("*");
			task.setHoursPattern("8");
			task.setMinutesPattern("*");
			task.setMonthsPattern("*");
			task.setServerName("*");
			task.setActive(false);
			getScheduledTaskService().create(task);
		}
	}

	@Override
	protected void handleTenantBoot(Tenant tenant) throws Exception {
	}

}


