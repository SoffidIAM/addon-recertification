package com.soffid.iam.addons.recertification.core;

import java.io.InputStream;

import org.apache.commons.logging.LogFactory;

import com.soffid.iam.api.Tenant;
import com.soffid.iam.bpm.api.ProcessDefinition;
import com.soffid.iam.bpm.service.BpmEngine;
import com.soffid.iam.utils.Security;

import es.caib.seycon.ng.exception.InternalErrorException;

public class RecertificationServiceBootImpl extends RecertificationServiceBootBase {

	@Override
	protected void handleConsoleBoot() throws Exception {
	}

	@Override
	protected void handleSyncServerBoot() throws Exception {
	}

	@Override
	protected void handleTenantBoot(Tenant tenant) throws Exception {
		Security.nestedLogin(tenant.getName(), "anonymous", Security.ALL_PERMISSIONS);
		try {
			uploadFile("recertification-process-wf.par", "Recertification process");
			
			uploadFile("recertification-group-process-wf.par", "Group Recertification process");
			
			uploadFile("recertification-role-process-wf.par", "Information system Recertification process");
		} finally {
			Security.nestedLogoff();
		}
	}

	private void uploadFile(String parFile, String bpmName) throws InternalErrorException {
		BpmEngine engine = getBpmEngine();
		boolean found = false;
		for ( ProcessDefinition def: engine.findProcessDefinitions(bpmName, false) )
		{
			if (def.getName().equals(bpmName))
			{
				found = true;
				break;
			}
		}
		if (! found)
		{
			InputStream in = getClass().getClassLoader().getResourceAsStream(parFile);
			if (in != null)
				engine.upgradeParFile(in);
			else
				LogFactory.getLog(getClass()).info("Missing resource "+parFile);
		}
	}

}
