package com.soffid.iam.addons.recertification.web;

import java.io.IOException;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

import com.soffid.iam.addons.recertification.common.RecertifiedRole;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;
import com.soffid.iam.api.User;
import com.soffid.iam.web.component.FrameHandler;
import com.soffid.iam.web.component.Identity;
import com.soffid.iam.web.popup.IdentityHandler;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.component.DataTable;
import es.caib.zkib.datamodel.DataModelCollection;
import es.caib.zkib.datamodel.DataNode;
import es.caib.zkib.datasource.CommitException;
import es.caib.zkib.datasource.XPathUtils;

public class RecertificateFrameHandler extends FrameHandler {

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
		c.setVisible(dt.getSelectedIndexes() != null && dt.getSelectedIndexes().length > 0);
	}

	public void accept(Event event) {
		DataTable dt = (DataTable) getFellow("rolesGrid");
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
		DataTable dt = (DataTable) getFellow("rolesGrid");
		if ( XPathUtils.eval(dt, "step1Date") == null)
			XPathUtils.setValue(dt, "check1", false);
		else if ( XPathUtils.eval(dt, "step2Date") == null)
			XPathUtils.setValue(dt, "check2", false);
		else if ( XPathUtils.eval(dt, "step3Date") == null)
			XPathUtils.setValue(dt, "check3", false);
		else if ( XPathUtils.eval(dt, "step4Date") == null)
			XPathUtils.setValue(dt, "check4", false);
	}
	
	public void comment(Event event) {
		DataTable dt = (DataTable) getFellow("rolesGrid");
		RecertifiedRole rr = (RecertifiedRole) XPathUtils.eval(dt, "instance");
		String[] data = (String[]) event.getData();
		rr.setNewComments(data[0]);
	}
	
	public void undo(Event event) throws CommitException {
		hideDetails();
		getModel().refresh();
	}

	public void applyNow(Event event) throws Exception {
		applyChanges();
		DataModelCollection coll = (DataModelCollection) XPathUtils.eval(getListbox(), "/role");
		coll.refresh();
	}

	public DataModelCollection applyChanges() throws NamingException, InternalErrorException {
		RecertificationService svc = (RecertificationService) new InitialContext().lookup(RecertificationServiceHome.JNDI_NAME);
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
	
	public void delegate(Event event) throws IOException {
		DataTable dt = (DataTable) getFellow("rolesGrid");
		int[] selected = dt.getSelectedIndexes();
		IdentityHandler.selectIdentity(Labels.getLabel("recertification.SelectUser"), 
				new com.soffid.iam.web.component.Identity.Type[] {
						com.soffid.iam.web.component.Identity.Type.USER
				}, 
				this, "onFinishDelegation");
	}

	public void finishDelegation(Event event) throws NamingException, InternalErrorException, CommitException {
		RecertificationService svc = (RecertificationService) new InitialContext().lookup(RecertificationServiceHome.JNDI_NAME);
		List<Identity> identities = (List<Identity>) event.getData();
		DataTable dt = (DataTable) getFellow("rolesGrid");
		if (identities.size() > 0) {
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
				dt.setSelectedIndex(rows);
			}
		}		
	}
}
