package com.soffid.iam.addons.recertification.web;

import java.io.IOException;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;

import com.soffid.iam.addons.recertification.common.RecertifiedRole;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;
import com.soffid.iam.api.User;
import com.soffid.iam.web.component.Identity;
import com.soffid.iam.web.popup.IdentityHandler;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.component.DataTable;
import es.caib.zkib.datasource.XPathUtils;

public class RecertificationDetailsHandler extends Window {
	public void back (Event event) {
		setVisible(false);
	}
	
	
	public void onSelect(Event event) {
		DataTable dt = (DataTable) getFellow("rolesGrid");
		Component c = getFellow("delegateButton");
		c.setVisible(dt.getSelectedIndexes() != null && dt.getSelectedIndexes().length > 0);
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

	public void finishDelegation(Event event) throws NamingException, InternalErrorException {
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
			} finally {
				dt.setSelectedIndex(rows);
			}
		}
	}
}
