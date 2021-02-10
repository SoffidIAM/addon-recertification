package com.soffid.iam.addons.recertification.web;

import org.zkoss.zul.Listbox;

import com.soffid.iam.addons.recertification.common.RecertificationType;
import com.soffid.iam.web.component.FrameHandler;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.binder.BindContext;

public class PolicyFrameHandler extends FrameHandler {

	public PolicyFrameHandler() throws InternalErrorException {
		super();
	}

	/*
	 * 
	 
	void selectGroup()
	{
		if((esquema.getFellow("dades").getFellow("form")
				.getFellow("listboxGroups").selectedItem != null) &&
			(esquema.getFellow("dades").getFellow("form")
				.getFellow("listboxGroups").selectedItem.value != null))
		{
			showDetallGroup();
		}
	}
	
	void showDetallGroup()
	{
		esquema.hideCriteris();
		esquema.getFellow("dades").getFellow("form")
				.getFellow("listboxGroups").setRows(5);
//		esquema.showFormulari();
	}
	
	void openEditor ( Event event) {
	    Events.sendEvent(new Event ("onEdit", 
	    		desktop.getPage("editor").getFellow("top"),
	    		new Object[] {
					    event.getTarget().getPreviousSibling(),
					    scriptVars
				}
	    ));

	}

	void openHtmlEditor ( Event event) {
	    Events.sendEvent(new Event ("onEdit", 
	    		page.getFellow("top"),
	    		new Object[] {
					    event.getTarget().getPreviousSibling(),
					    scriptVars
				}
	    ));

	}
	
	void delegate () {
		Listbox lb = esquema.getFellow("dades").getFellow("rolesGrid");
		Toolbarbutton delegateButton = esquema.getFellow("dades").getFellow("delegateButton");
		if (! lb.getSelectedItems().isEmpty())
		{
			Page p = desktop.getPage("identity");
			p.setVariable("types",
						new com.soffid.iam.web.component.Identity.Type[] {
								com.soffid.iam.web.component.Identity.Type.USER
						}
					);
			p.setVariable("title", org.zkoss.util.resource.Labels.getLabel("seleccionUsuario.btnDelegar"));
			p.setVariable("invoker", delegateButton);
			Events.sendEvent(new Event("onDisplay", p.getFellow("identityWindow")));
		}
	}
	void delegate2(List identities) {
		StringBuffer sb = new StringBuffer();
		for (com.soffid.iam.web.component.Identity id: identities)
		{
			if (sb.length() > 0) sb .append(" ");
			com.soffid.iam.api.User user = id.getObject();
			sb.append (user.userName);
		}
		
		com.soffid.iam.addons.recertification.core.ejb.RecertificationService ejb =
				new javax.naming.InitialContext()
				.lookup (com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome.JNDI_NAME);
		if ( sb.length() > 0)
		{
			Listbox rolesGrid = esquema.getFellow("dades").getFellow("rolesGrid");
			for (item: rolesGrid.getSelectedItems())
			{
				RecertifiedRole rr = item.getValue().getInstance();
				ejb.delegate(rr, sb.toString());
			}
			es.caib.zkib.binder.BindContext ctx = es.caib.zkib.datasource.XPathUtils.getComponentContext(rolesGrid);
			es.caib.zkib.datasource.XPathUtils.getValue(ctx.getDataSource(), ctx.getXPath()).refresh();
		}
	}
	*/
}
