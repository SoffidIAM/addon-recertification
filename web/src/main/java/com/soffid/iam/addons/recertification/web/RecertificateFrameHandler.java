package com.soffid.iam.addons.recertification.web;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.addons.recertification.common.RecertificationType;
import com.soffid.iam.addons.recertification.common.RecertifiedRole;
import com.soffid.iam.addons.recertification.common.RecertifiedRoleDefinition;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;
import com.soffid.iam.api.Role;
import com.soffid.iam.api.RoleDependencyStatus;
import com.soffid.iam.api.RoleGrant;
import com.soffid.iam.api.User;
import com.soffid.iam.web.component.FrameHandler;
import com.soffid.iam.web.component.Identity;
import com.soffid.iam.web.popup.IdentityHandler;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.component.DataTable;
import es.caib.zkib.component.Div;
import es.caib.zkib.datamodel.DataModelCollection;
import es.caib.zkib.datamodel.DataModelNode;
import es.caib.zkib.datamodel.DataNode;
import es.caib.zkib.datamodel.DataNodeCollection;
import es.caib.zkib.datasource.CommitException;
import es.caib.zkib.datasource.XPathUtils;
import es.caib.zkib.jxpath.JXPathNotFoundException;
import es.caib.zkib.zkiblaf.Missatgebox;

public class RecertificateFrameHandler extends FrameHandler {

	private LinkedList<RolePointer> roleColumns;

	public RecertificateFrameHandler() throws InternalErrorException {
		super();
	}

	@Override
	public void showDetails() {
		super.showDetails();
	}

	public void onSelectRole(Event event) {
		DataTable dt = (DataTable) getFellow("rolesGrid");
		Component c = getFellow("delegateButton");
		final boolean selected = dt.getSelectedIndexes() != null && dt.getSelectedIndexes().length > 0;
		c.setVisible(selected);
		Div data = (Div) getFellow("dades");
		data.setSclass(dt.getSelectedIndexes() != null && dt.getSelectedIndexes().length == 1? 
				"data_open": "data_closed");
	}

	public void onSelectRoleDefinition(Event event) {
		DataTable dt = (DataTable) getFellow("roleDefsGrid");
		Component c = getFellow("delegateButton");
		c.setVisible(dt.getSelectedIndexes() != null && dt.getSelectedIndexes().length > 0);
	}

	public void accept(Event event) {
		RecertificationType type = (RecertificationType) XPathUtils.eval(getForm(), "type");
		DataTable dt;
		if (type == RecertificationType.ROLEDEFINITIONS) 
			dt = (DataTable) getFellow("roleDefsGrid");
		else
			dt = (DataTable) getFellow("rolesGrid");
		if ( XPathUtils.eval(dt, "step1Date") == null)
			XPathUtils.setValue(dt, "check1", true);
		else if ( XPathUtils.eval(dt, "step2Date") == null)
			XPathUtils.setValue(dt, "check2", true);
		else if ( XPathUtils.eval(dt, "step3Date") == null)
			XPathUtils.setValue(dt, "check3", true);
		else if ( XPathUtils.eval(dt, "step4Date") == null)
			XPathUtils.setValue(dt, "check4", true);
	}

	public void reject(Event event) {
		Missatgebox.confirmaYES_NO(Labels.getLabel("recertification.confirm"), (ev2)->{
			RecertificationType type = (RecertificationType) XPathUtils.eval(getForm(), "type");
			DataTable dt;
			if (type == RecertificationType.ROLEDEFINITIONS) 
				dt = (DataTable) getFellow("roleDefsGrid");
			else
				dt = (DataTable) getFellow("rolesGrid");
			if (ev2.getName().equals("onYes")) {
				if ( XPathUtils.eval(dt, "step1Date") == null)
					XPathUtils.setValue(dt, "check1", false);
				else if ( XPathUtils.eval(dt, "step2Date") == null)
					XPathUtils.setValue(dt, "check2", false);
				else if ( XPathUtils.eval(dt, "step3Date") == null)
					XPathUtils.setValue(dt, "check3", false);
				else if ( XPathUtils.eval(dt, "step4Date") == null)
					XPathUtils.setValue(dt, "check4", false);
			}
			dt.updateClientRow(dt.getSelectedIndex());
			dt.setSelectedIndex(-1);
		});
	}
	
	public void comment(Event event) {
		RecertificationType type = (RecertificationType) XPathUtils.eval(getForm(), "type");
		getFellow("rolesGrid").setVisible(type != RecertificationType.ROLEDEFINITIONS);
		getFellow("roleDefsGrid").setVisible(type == RecertificationType.ROLEDEFINITIONS);
		if (type == RecertificationType.ROLEDEFINITIONS) {
			DataTable dt = (DataTable) getFellow("roleDefsGrid");
			
			RecertifiedRoleDefinition rr = (RecertifiedRoleDefinition) XPathUtils.eval(dt, "instance");
			String[] data = (String[]) event.getData();
			rr.setNewComments(data[0]);
		
		} else {
			DataTable dt = (DataTable) getFellow("rolesGrid");
		
			RecertifiedRole rr = (RecertifiedRole) XPathUtils.eval(dt, "instance");
			String[] data = (String[]) event.getData();
			rr.setNewComments(data[0]);
		}
	}
	
	public void undo(Event event) throws CommitException {
		hideDetails();
		getModel().refresh();
	}

	public void applyNow(Event event) throws Exception {
		applyChanges();
		RecertificationType type = (RecertificationType) XPathUtils.eval(getForm(), "type");
		if (type == RecertificationType.ROLEDEFINITIONS) {
			DataModelCollection coll = (DataModelCollection) XPathUtils.eval(getListbox(), "/roleDefinition");
			coll.refresh();
		} else {
			DataModelCollection coll = (DataModelCollection) XPathUtils.eval(getListbox(), "/role");
			coll.refresh();
		}
	}

	public DataModelCollection applyChanges() throws NamingException, InternalErrorException {
		RecertificationType type = (RecertificationType) XPathUtils.eval(getForm(), "type");
		RecertificationService svc = (RecertificationService) new InitialContext().lookup(RecertificationServiceHome.JNDI_NAME);
		if (type == RecertificationType.ROLEDEFINITIONS) {
			DataModelCollection coll = (DataModelCollection) XPathUtils.eval(getListbox(), "/roleDefinition");
			for (int i = 0; i < coll.getSize(); i++ ) {
				DataNode dn = (DataNode) coll.getDataModel(i);
				if (dn != null) {
					RecertifiedRoleDefinition rr = (RecertifiedRoleDefinition) dn.getInstance();
					if (rr.getStep1Date() == null && rr.getCheck1() != null)
						svc.check(rr, rr.getCheck1(), rr.getNewComments());
					else if (rr.getStep2Date() == null && rr.getCheck2() != null)
						svc.check(rr, rr.getCheck2(), rr.getNewComments());
					else if (rr.getStep3Date() == null && rr.getCheck3() != null)
						svc.check(rr, rr.getCheck3(), rr.getNewComments());
					else if (rr.getStep4Date() == null && rr.getCheck4() != null)
						svc.check(rr, rr.getCheck4(), rr.getNewComments());
				}
			}
			return coll;
		} else {
			DataModelCollection coll = (DataModelCollection) XPathUtils.eval(getListbox(), "/role");
			for (int i = 0; i < coll.getSize(); i++ ) {
				DataNode dn = (DataNode) coll.getDataModel(i);
				if (dn != null) {
					RecertifiedRole rr = (RecertifiedRole) dn.getInstance();
					if (rr.getStep1Date() == null && rr.getCheck1() != null)
						svc.check(rr, rr.getCheck1(), rr.getNewComments());
					else if (rr.getStep2Date() == null && rr.getCheck2() != null)
						svc.check(rr, rr.getCheck2(), rr.getNewComments());
					else if (rr.getStep3Date() == null && rr.getCheck3() != null)
						svc.check(rr, rr.getCheck3(), rr.getNewComments());
					else if (rr.getStep4Date() == null && rr.getCheck4() != null)
						svc.check(rr, rr.getCheck4(), rr.getNewComments());
				}
			}
			return coll;
		}
	}
	
	public void delegate(Event event) throws IOException {
		RecertificationType type = (RecertificationType) XPathUtils.eval(getForm(), "type");
		DataTable dt;
		if (type == RecertificationType.ROLEDEFINITIONS) 
			dt = (DataTable) getFellow("roleDefsGrid");
		else
			dt = (DataTable) getFellow("rolesGrid");
		IdentityHandler.selectIdentity(Labels.getLabel("recertification.SelectUser"), 
				new com.soffid.iam.web.component.Identity.Type[] {
						com.soffid.iam.web.component.Identity.Type.USER
				}, 
				this, "onFinishDelegation");
	}

	public void finishDelegation(Event event) throws NamingException, InternalErrorException, CommitException {
		RecertificationType type = (RecertificationType) XPathUtils.eval(getForm(), "type");
		DataTable dt;
		RecertificationService svc = (RecertificationService) new InitialContext().lookup(RecertificationServiceHome.JNDI_NAME);
		List<Identity> identities = (List<Identity>) event.getData();
		if (identities.size() > 0) {
			if (type == RecertificationType.ROLEDEFINITIONS)  {
				dt = (DataTable) getFellow("roleDefsGrid");
				
				StringBuffer users = new StringBuffer();
				for (Identity identity: identities) {
					if (users.length() > 0) users.append(" ");
					users.append(((User)identity.getObject()).getUserName());
				}
				int[] rows = dt.getSelectedIndexes();
				try {
					for (int row: rows) {
						dt.setSelectedIndex(row);
						RecertifiedRoleDefinition r = (RecertifiedRoleDefinition) XPathUtils.eval(dt, "instance");
						for (int j = 1; j <= 4; j++) {
							String usersOrig = (String) XPathUtils.eval(dt, "step"+j+"Users");
							if ( usersOrig == null) break;
							String author = (String) XPathUtils.eval(dt, "step"+j+"Author");
							if (author == null) {
								XPathUtils.setValue(dt,  "step"+j+"Users", users.toString());
								break;
							}
						}
						svc.delegate(r, users.toString());
					}
					dt.setSelectedIndex(rows);
					dt.deleteSelectedItem();
				} finally {
					dt.setSelectedIndex(new int[0]);
				}
			}
			else {
				dt = (DataTable) getFellow("rolesGrid");
				StringBuffer users = new StringBuffer();
				for (Identity identity: identities) {
					if (users.length() > 0) users.append(" ");
					users.append(((User)identity.getObject()).getUserName());
				}
				int[] rows = dt.getSelectedIndexes();
				try {
					for (int row: rows) {
						dt.setSelectedIndex(row);
						RecertifiedRole r = (RecertifiedRole) XPathUtils.eval(dt, "instance");
						for (int j = 1; j <= 4; j++) {
							String usersOrig = (String) XPathUtils.eval(dt, "step"+j+"Users");
							if ( usersOrig == null) break;
							String author = (String) XPathUtils.eval(dt, "step"+j+"Author");
							if (author == null) {
								XPathUtils.setValue(dt,  "step"+j+"Users", users.toString());
								break;
							}
						}
						svc.delegate(r, users.toString());
					}
					dt.setSelectedIndex(rows);
					dt.deleteSelectedItem();
				} finally {
					dt.setSelectedIndex(new int[0]);
				}
				
			}
		}		
	}

	@Override
	public void onChangeForm(Event ev) throws Exception {
		super.onChangeForm(ev);
		try {
			RecertificationType type = (RecertificationType) XPathUtils.eval(getForm(), "type");
			getFellow("rolesGrid").setVisible(type != RecertificationType.ROLEDEFINITIONS);
			getFellow("roleDefsGrid").setVisible(type == RecertificationType.ROLEDEFINITIONS);
			if (type == RecertificationType.ROLEDEFINITIONS) {
				DataTable dt = (DataTable) getFellow("roleDefsGrid");
				String s = dt.getColumns();
				JSONArray cols = new JSONArray(s);
				JSONObject last = cols.getJSONObject(cols.length()-1);
				while (cols.length() > 3)
					cols.remove(3);
				
				HashMap<Long, RolePointer> roleHash = new HashMap<Long,RolePointer>();
				DataNodeCollection coll = (DataNodeCollection) XPathUtils.eval(getForm(), "roleDefinition");
				for (int i =  0; i < coll.size(); i++) {
					DataNode node = (DataNode) coll.get(i);
					if (node != null) {
						DataModelCollection rolesNode = node.getListModel("role");
						if (rolesNode != null && rolesNode.getSize() > 0) {
							DataNode roleNode = (DataNode) rolesNode.getDataModel(0);
							if (roleNode != null && ! roleNode.isDeleted()) {
								Role role = (Role) roleNode.getInstance();
								for (RoleGrant grant: role.getOwnedRoles()) {
									if (!roleHash.containsKey(grant.getRoleId())) {
										RolePointer p = new RolePointer();
										p.id = grant.getRoleId();
										p.name = grant.getRoleName();
										p.description = grant.getRoleDescription();
										roleHash.put(p.id, p);
									}
								}
								
							}
						}
					}
				}
				roleColumns = new LinkedList<RolePointer>(roleHash.values());
				Collections.sort(roleColumns);
				
				for (int i = 0; i < roleColumns.size(); i++) {
					RolePointer rp = roleColumns.get(i);
					JSONObject col = new JSONObject();
					col.put("name", rp.name);
					col.put("filter", false);
					col.put("sort", false);
					col.put("vertical", true);
					col.put("className", "minicolumn");
					col.put("template", "<img #{r"+i+"_hidden} class='imageclic' title='#{r"+i+"_title}' "
							+ "onClick='zkDatatable.sendClientAction(this, \"onChangeLevel\", ["+rp.id+", event.clientX, event.clientY]);event.stopPropagation()' src='"+ getDesktop().getExecution().getContextPath()+ "/img/#{r"+i+"}.svg'/>");
					cols.put(col);
				}
				cols.put(last);
				dt.setColumns(cols.toString());
				dt.setAttribute("columns", roleColumns);
				dt.invalidate();
			}
		} catch (JXPathNotFoundException e) {}
	}
	
	public void onChangeLevel(Event event) {
		String data[] = (String[]) event.getData();
		final Long roleId = Long.parseLong(data[0]);
		final DataTable dt = (DataTable) getFellow("roleDefsGrid");
		final RecertifiedRoleDefinition def = (RecertifiedRoleDefinition) XPathUtils.eval(dt, "/instance");
		final Role role = (Role) XPathUtils.eval(dt, "role[1]/instance");
		RoleDependencyStatus current = null;
		RoleGrant currentGrant = null;
		RolePointer currentCol = null;
		
		for (RolePointer col: roleColumns) {
			if (col.id.equals(roleId))
				currentCol = col;
		}
		
		if (currentCol == null)
			return;
		
		for (RoleGrant grant: role.getOwnedRoles()) {
			if (grant.getRoleId().equals(roleId)) {
				current = grant.getStatus();
				currentGrant = grant;
			}
		}

		final RoleGrant currentGrant2 = currentGrant;
		
		final Menupopup menu = new Menupopup();
		Menuitem item;
		if (current == RoleDependencyStatus.STATUS_TOAPPROVE || current == null) {
			item = new Menuitem(String.format(Labels.getLabel("recertification.addPermission"), currentCol.name, role.getName()),
					"/img/add.svg");
			item.setSclass("nocheck");
			menu.appendChild(item);
			if (current == null)
				item.addEventListener("onClick", new EventListener() {
					public void onEvent(Event event) throws Exception {
						Role childRole = EJBLocator.getApplicationService().findRoleById(roleId);
						RoleGrant rg = new RoleGrant();
						rg.setEnabled(false);
						rg.setInformationSystem(childRole.getInformationSystemName());
						rg.setMandatory(true);
						rg.setOwnerRole(role.getId());
						rg.setOwnerRoleDescription(role.getDescription());
						rg.setOwnerRoleName(role.getName());
						rg.setOwnerSystem(role.getSystem());
						rg.setRoleName(childRole.getName());
						rg.setRoleDescription(childRole.getDescription());
						rg.setRoleId(childRole.getId());
						rg.setStartDate(new Date());
						rg.setStatus(RoleDependencyStatus.STATUS_TOAPPROVE);
						rg.setSystem(childRole.getSystem());
						
						RecertificationService ejb = (RecertificationService) new InitialContext().lookup(RecertificationServiceHome.JNDI_NAME);
						
						ejb.addDependency(def, rg);
						role.getOwnedRoles().add(rg);
						dt.updateClientRow(dt.getSelectedIndex());
					}
				} );
			item = new Menuitem(String.format(Labels.getLabel("recertification.leavePermission"), currentCol.name, role.getName()),
					"/img/blank.svg");
			item.setSclass("nocheck");
			if (current != null)
				item.addEventListener("onClick", new EventListener() {
					public void onEvent(Event event) throws Exception {
						RecertificationService ejb = (RecertificationService) new InitialContext().lookup(RecertificationServiceHome.JNDI_NAME);
						
						ejb.removeDependency(def, currentGrant2);
						role.getOwnedRoles().remove(currentGrant2);
						dt.updateClientRow(dt.getSelectedIndex());
						dt.setSelectedIndex(-1);
					}
				} );
			menu.appendChild(item);
		}
		if (current == RoleDependencyStatus.STATUS_TOREMOVE || current == RoleDependencyStatus.STATUS_ACTIVE) {
			item = new Menuitem(String.format(Labels.getLabel("recertification.removePermission"), currentCol.name, role.getName()),
					"/img/remove.svg");
			item.setSclass("nocheck");
			if (current == RoleDependencyStatus.STATUS_ACTIVE)
				item.addEventListener("onClick", new EventListener() {
					public void onEvent(Event event) throws Exception {
						RecertificationService ejb = (RecertificationService) new InitialContext().lookup(RecertificationServiceHome.JNDI_NAME);
						
						ejb.removeDependency(def, currentGrant2);
						currentGrant2.setStatus( RoleDependencyStatus.STATUS_TOREMOVE );
						dt.updateClientRow(dt.getSelectedIndex());
						dt.setSelectedIndex(-1);
					}
				} );
			menu.appendChild(item);
			item = new Menuitem(String.format(Labels.getLabel("recertification.keepPermission"), currentCol.name, role.getName()),
					"/img/ok.svg");
			item.setSclass("nocheck");
			if (current == RoleDependencyStatus.STATUS_TOREMOVE)
				item.addEventListener("onClick", new EventListener() {
					public void onEvent(Event event) throws Exception {
						RecertificationService ejb = (RecertificationService) new InitialContext().lookup(RecertificationServiceHome.JNDI_NAME);
						
						ejb.addDependency(def, currentGrant2);
						currentGrant2.setStatus( RoleDependencyStatus.STATUS_ACTIVE );
						dt.updateClientRow(dt.getSelectedIndex());
						dt.setSelectedIndex(-1);
					}
				} );
			menu.appendChild(item);
		}
		appendChild(menu);
		menu.open(data[1], data[2]);
		menu.addEventListener("onOpen", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (! ((OpenEvent)event).isOpen())
					menu.detach();
			}
		});
	}

	@Override
	public void afterCompose() {
		super.afterCompose();
		
	}
}


class RolePointer implements Comparable<RolePointer>{
	Long id;
	String name;
	String system;
	String description;
	@Override
	public int compareTo(RolePointer o) {
		int i = name.compareTo(o.name);
		if (i == 0)
			i = system.compareTo(o.system);
		return i;
	}
}