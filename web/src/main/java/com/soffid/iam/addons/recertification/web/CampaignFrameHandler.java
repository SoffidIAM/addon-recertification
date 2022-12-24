package com.soffid.iam.addons.recertification.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Window;

import com.soffid.iam.addons.recertification.common.RecertificationType;
import com.soffid.iam.web.component.FrameHandler;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.binder.BindContext;

public class CampaignFrameHandler extends FrameHandler {

	private String wizard;

	public CampaignFrameHandler() throws InternalErrorException {
		super();
		HttpServletRequest req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
		wizard = req.getParameter("wizard");
	}

	@Override
	public void showDetails() {
		BindContext form = getForm();
		RecertificationType type = (RecertificationType) es.caib.zkib.datasource.XPathUtils.eval(form, "@type");
		if (type == RecertificationType.ENTITLEMENTS)
		{
			getFellow ("groupSection").setVisible(true);
			getFellow ("usersSection").setVisible(false);
		} else {
			getFellow ("groupSection").setVisible(false);
			getFellow ("usersSection").setVisible(false);
		}
		super.showDetails();
	}

	public void showUsers() throws Exception {
		getFellow ("usersSection").setVisible(true);
	}

	@Override
	public void addNew() throws Exception {
		NewProcessHandler h = (NewProcessHandler) getFellow("newProcessWindow");
		h.start();
	}
	
	public void openRoles() {
		RecertificationType type = (RecertificationType) es.caib.zkib.datasource.XPathUtils.eval(getForm(), "@type");
		if (type == RecertificationType.ROLEDEFINITIONS) {
			Window w = (Window) getFellow("roleDefWindow");
			w.doHighlighted();
		} else {
			Window w = (Window) getFellow("rolesWindow");
			w.doHighlighted();
		}
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		setVariable("searchDictionary", new RecertificationProcessDictionary(), true);
	}

	@Override
	public void afterCompose() {
		super.afterCompose();
		if ("new".equals(wizard))
			try {
				addNew();
			} catch (Exception e) {
				throw new UiException(e);
			}
	}
	
}
