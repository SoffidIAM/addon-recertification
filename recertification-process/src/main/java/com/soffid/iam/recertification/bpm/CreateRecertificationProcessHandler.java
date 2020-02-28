/**
 * 
 */
package com.soffid.iam.recertification.bpm;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.naming.InitialContext;

import net.sf.jasperreports.components.barbecue.BarcodeProviders.NW7Provider;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.soffid.iam.addons.recertification.Constants;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertificationTemplate;
import com.soffid.iam.addons.recertification.common.RecertificationType;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationService;
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
		com.soffid.iam.addons.recertification.core.ejb.RecertificationService ejb =
				(RecertificationService) new InitialContext()
					.lookup(RecertificationServiceHome.JNDI_NAME);

		RecertificationProcess newRecertProcess = new RecertificationProcess();
		newRecertProcess.setManagerRole("SOFFID_OU_MANAGER");
		newRecertProcess.setName("Recertification Process "+DateFormat.getDateTimeInstance().format(new Date()));
		newRecertProcess.setStartDate(Calendar.getInstance());
		newRecertProcess.setStatus(ProcessStatus.PREPARATION);
		newRecertProcess.setWorkflowId(new Long(executionContext
				.getProcessInstance().getId()));
		newRecertProcess.setCisoReview(false);
		newRecertProcess.setCisoRole("SOFFID_CISO");
		newRecertProcess.setUserReview(false);
		newRecertProcess.setType(RecertificationType.ENTITLEMENTS);
		for ( RecertificationTemplate t: ejb.findTemplates()) 
		{
			newRecertProcess.setTemplate(t.getName());
			break;
		}

		newRecertProcess = ejb.create(newRecertProcess);
		
		executionContext.setVariable(Constants.RECERTIFICATION_ID_VAR, newRecertProcess.getId());
		executionContext.setVariable(Constants.USER_REVIEW_VAR, Boolean.TRUE);
		
//		executionContext.leaveNode();
	}
}
