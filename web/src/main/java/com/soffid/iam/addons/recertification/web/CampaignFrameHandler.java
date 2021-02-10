package com.soffid.iam.addons.recertification.web;

import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Window;

import com.soffid.iam.addons.recertification.common.RecertificationType;
import com.soffid.iam.web.component.FrameHandler;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.binder.BindContext;

public class CampaignFrameHandler extends FrameHandler {

	public CampaignFrameHandler() throws InternalErrorException {
		super();
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
		Window w = (Window) getFellow("rolesWindow");
		w.doHighlighted();
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		setVariable("searchDictionary", new RecertificationProcessDictionary(), true);
	}
	
}
