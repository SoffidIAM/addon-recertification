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
	}

}
