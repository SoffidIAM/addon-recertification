package com.soffid.iam.web.wheel;

import java.util.Date;
import java.util.LinkedList;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.addons.recertification.common.RecertificationTemplate;
import com.soffid.iam.addons.recertification.common.RecertificationType;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.zkiblaf.Application;
import es.caib.zkib.zkiblaf.Missatgebox;

public class ActualIrc03Sector {
	RecertificationService service ;

	public ActualIrc03Sector() throws NamingException {
		service = (RecertificationService) new InitialContext().lookup(RecertificationServiceHome.JNDI_NAME);
	}
	
	public boolean isDone() throws InternalErrorException, NamingException {
		if (service.findTemplates().isEmpty() &&
				service.findRecertificationProcessesByTextAndQuery(null, null, null, 1).getResources().isEmpty())  
			return false;
		
		return service.findRecertificationProcessesByTextAndQuery(null, null, 0, 1).getResources().isEmpty();
	}
	
	public void activate() {
		Missatgebox.confirmaOK_CANCEL(Labels.getLabel("recertification.wizard.explanation"), e -> {
			if (e.getName().equals("onOK")) {
				RecertificationTemplate template = new RecertificationTemplate();
				template.setEscalation(null);
				template.setName("Critical access review");
				template.setType(RecertificationType.ENTITLEMENTS);
				template.setFilterScript("// Select only permissions with a risk category\n"
						+ "return grant.sodRisk != null &&\n"
						+ "  grant.sodRisk != es.caib.seycon.ng.comu.SoDRisk.SOD_NA;");
				template.setStep1Script("// First the manager of the group manager\n"
						+ "userGroupCode == null ? \"SOFFID_ADMIN\": \"SOFFID_APP_OWNER\"+grant.userGroupCode");
				template.setStep2Script("// Next the manager of the application owner\n"
						+ "\"SOFFID_GROUP_MGR/\"+grant.informationSystemName");
				createTemplateIfDoesNotExist(template);
				
				template = new RecertificationTemplate();
				template.setEscalation(null);
				template.setName("Complete access review");
				template.setType(RecertificationType.ENTITLEMENTS);
				template.setFilterScript("// Select any permission\n"
						+ "true;");
				template.setStep1Script("// First the manager of the group manager\n"
						+ "userGroupCode == null ? \"SOFFID_ADMIN\": \"SOFFID_APP_OWNER\"+grant.userGroupCode");
				template.setStep2Script("// Next the manager of the application owner\n"
						+ "\"SOFFID_GROUP_MGR/\"+grant.informationSystemName");
				createTemplateIfDoesNotExist(template);
				Application.jumpTo("/addon/recertification/recertification_info.zul?wizard=new");
			}
		});
	}

	private void createTemplateIfDoesNotExist(RecertificationTemplate template) throws InternalErrorException {
		for (RecertificationTemplate t: service.findTemplates()) {
			if (t.getName().equals(template.getName()))
				return;
		}
		service.createTemplate(template );
	}
}
