/**
 * 
 */
package com.soffid.iam.recertification.bpm;

import java.util.Calendar;

import javax.naming.InitialContext;

import net.sf.jasperreports.components.barbecue.BarcodeProviders.NW7Provider;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

/**
 * @author (C) Soffid 2013
 * 
 */
public class CreateRecertificationProcessHandler implements ActionHandler
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * @see
	 * org.jbpm.graph.def.ActionHandler#execute(org.jbpm.graph.exe.ExecutionContext
	 * )
	 */
	public void execute (ExecutionContext executionContext) throws Exception
	{
		RecertificationServiceHome home =
				(RecertificationServiceHome) new InitialContext()
						.lookup(RecertificationServiceHome.JNDI_NAME);
		com.soffid.iam.addons.recertification.core.ejb.RecertificationService ejb =
				home.create();

		RecertificationProcess newRecertProcess = new RecertificationProcess();
		newRecertProcess.setManagerRole("SOFFID_BOSS");
		newRecertProcess.setName("Recertification Process");
		newRecertProcess.setStartDate(Calendar.getInstance());
		newRecertProcess.setStatus(ProcessStatus.PREPARATION);
		newRecertProcess.setWorkflowId(new Long(executionContext
				.getProcessInstance().getId()));

		newRecertProcess = ejb.create(newRecertProcess);
		
		executionContext.setVariable(Constants.RECERTIFICATION_ID_VAR, newRecertProcess.getId());
		executionContext.setVariable(Constants.USER_REVIEW_VAR, Boolean.TRUE);
	}
}
