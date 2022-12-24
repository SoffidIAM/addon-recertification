package com.soffid.iam.addons.recertification.web;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.common.RecertificationTemplate;
import com.soffid.iam.addons.recertification.common.RecertificationType;
import com.soffid.iam.addons.recertification.common.RecertifiedGroup;
import com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;
import com.soffid.iam.utils.Security;
import com.soffid.iam.web.component.CustomField3;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.component.DataModel;
import es.caib.zkib.component.Wizard;
import es.caib.zkib.datamodel.DataModelCollection;
import es.caib.zkib.datamodel.DataNode;
import es.caib.zkib.datamodel.DataNodeCollection;
import es.caib.zkib.datasource.CommitException;
import es.caib.zkib.jxpath.Pointer;

public class NewProcessHandler extends Window {

	private CustomField3 templates;
	private CustomField3 groups;
	private CustomField3 applications;
	private DataModel model;
	private Map<String,RecertificationTemplate> templatesMap;
	private CustomField3 name;
	private RecertificationProcess current = null;
	private String currentPath;
	
	public void start() throws NamingException, CreateException, InternalErrorException, IOException {
		getWizard().setSelected(0);
		doHighlighted();
		templates = (CustomField3) getFellow("templates");
		groups = (CustomField3) getFellow("groups");
		applications = (CustomField3) getFellow("applications");
		name = (CustomField3) getFellow("name");
		model = (DataModel) getParent().getFellow("model");

		templatesMap = new HashMap<String,RecertificationTemplate>();
		DataNodeCollection coll = (DataNodeCollection) model.getJXPathContext().getValue("template");
		for (int i = 0; i < coll.getSize(); i++) {
			DataNode dn = (DataNode) coll.getDataModel(i);
			if (dn != null && ! dn.isDeleted()) {
				RecertificationTemplate t = (RecertificationTemplate) dn.getInstance();
				templatesMap.put(t.getName(), t);
			}
		}
//		templates.setListOfValues(templatesMap.keySet().toArray(new String[templatesMap.size()]));
		templates.setValue("");
		templates.createField();

		name.setValue("");
		
		groups.setValues(new LinkedList<String>());
		applications.setValues(new LinkedList<String>());
		
		current = null;
		currentPath = null;
	}
	
	public void undoAdd() {
		setVisible(false);
	}
	
	public void selectGroups (Event event) {
		if ( groups.attributeValidateAll())
			getWizard().next();
	}
	
	public void back (Event event) {
		getWizard().previous();
		if ( getWizard().getSelected() == 1) {
			RecertificationTemplate t = templatesMap.get(templates.getValue().toString());
			if (t == null || t.getType() != RecertificationType.ENTITLEMENTS) {
				getWizard().previous();
			}
		}
	}
	
	public void selectTemplate(Event event) {
		if (templates.attributeValidateAll() && name.attributeValidateAll() ) {
			RecertificationTemplate t = templatesMap.get(templates.getValue().toString());
			if (t != null) {
				getWizard().next();
				if (t.getType() != RecertificationType.ENTITLEMENTS) {
					getWizard().next();
				}
			}
		}
	}
	
	Wizard getWizard() {
		return (Wizard) getFellow("wizard");
	}

	public void selectApplications (Event event) throws Exception {
		if (applications.attributeValidateAll()) {
			try {
				if (current == null)
					current = new RecertificationProcess();
				current.setTemplate((String) templates.getValue());
				current.setName((String) name.getValue());
				current.setStatus(ProcessStatus.PREPARATION);
				current.setStartDate(Calendar.getInstance());
				current.setManagerRole(Security.getCurrentUser());
				
				DataNodeCollection coll = (DataNodeCollection) model.getJXPathContext().getValue("/process");
				if (current.getId() == null) {
					coll.add(current);
					currentPath = "/process["+coll.getSize()+"]";
					model.commit();
				} else {
					model.commit();
				}
				
				RecertificationTemplate t = templatesMap.get(templates.getValue().toString());
				if (t != null && t.getType() == RecertificationType.ENTITLEMENTS) {
					List<String> g = (List<String>) groups.getValue();
					if (g != null) {
						DataNodeCollection coll2 = (DataNodeCollection) model.getJXPathContext().getValue(currentPath+"/group");
						for (String groupName: g) {
							RecertifiedGroup rg = new RecertifiedGroup();
							rg.setGroup(groupName);
							rg.setProcessId(current.getId());
							rg.setStatus(ProcessStatus.PREPARATION);
							coll2.add(rg);
						}
					}
				}
				
				List<String> is = (List<String>) applications.getValue();
				if (is != null) {
					DataNodeCollection coll2 = (DataNodeCollection) model.getJXPathContext().getValue(currentPath+"/is");
					for (String isName: is) {
						RecertifiedInformationSystem ri = new RecertifiedInformationSystem();
						ri.setInformationSystem(isName);
						ri.setProcessId(current.getId());
						ri.setStatus(ProcessStatus.PREPARATION);
						coll2.add(ri);
					}
				}
	
				model.commit();
				
				RecertificationService svc = (RecertificationService) new InitialContext().lookup(RecertificationServiceHome.JNDI_NAME);

				svc.startRecertificationProcess(current);
				model.getJXPathContext().setValue(currentPath+"/status", current.getStatus());
				DataNodeCollection c = (DataNodeCollection) model.getJXPathContext().getValue(currentPath+"/group");
				c.refresh();
				c = (DataNodeCollection) model.getJXPathContext().getValue(currentPath+"/is");
				c.refresh();
				setVisible(false);
			} catch (Exception e) {
				DataNodeCollection coll2 = (DataNodeCollection) model.getJXPathContext().getValue(currentPath+"/group");
				for (int i = 0; i < coll2.getSize(); i++) {
					DataNode dn = (DataNode) coll2.get(i);
					if (dn != null && !dn.isDeleted())
						dn.delete();
				}
				coll2 = (DataNodeCollection) model.getJXPathContext().getValue(currentPath+"/is");
				for (int i = 0; i < coll2.getSize(); i++) {
					DataNode dn = (DataNode) coll2.get(i);
					if (dn != null && !dn.isDeleted())
						dn.delete();
				}
				DataNode dn = (DataNode) model.getJXPathContext().getValue(currentPath);
				dn.delete();
				model.commit();
				current = null;
				throw e;
			}
		}
	}
}
