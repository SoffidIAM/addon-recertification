package com.soffid.iam.addons.recertification.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;

import com.soffid.iam.addons.recertification.common.RecertifiedRole;

import es.caib.zkib.component.DataListener;
import es.caib.zkib.datamodel.DataNode;
import es.caib.zkib.datasource.XPathUtils;
import es.caib.zkib.events.XPathEvent;

public class CheckListcell extends DataListener {
	Log log = LogFactory.getLog(getClass());
	public CheckListcell() {
		setBind("/");
	}

	@Override
	public void onUpdate(XPathEvent event) {
		while ( getNextSibling() != null)
			getNextSibling().detach();
		
		DataNode dn = (DataNode) getValue();
		if (dn != null)
		{
			RecertifiedRole rr = (RecertifiedRole) dn.getInstance();
			if (rr != null)
			{
				Component c = getParent();
				Checkbox cb = new Checkbox();
				cb.addEventListener("onCheck", onApprove);
				c.appendChild(cb);
				cb.setSclass("custom-checkbox-green custom-checkbox");
		
				cb = new Checkbox();
				cb.addEventListener("onCheck", onDeny);
				c.appendChild(cb);
				cb.setSclass("custom-checkbox-red custom-checkbox");
			}			
		}
	}

	static EventListener onApprove = new EventListener() {
		public void onEvent(Event event) throws Exception {
			CheckListcell lc = (CheckListcell) event.getTarget().getPreviousSibling();
			XPathUtils.setValue(lc, "/check", Boolean.TRUE);
			Checkbox cb = (Checkbox) event.getTarget().getNextSibling();
			cb.setChecked(false);
		}
	};

	static EventListener onDeny = new EventListener() {
		public void onEvent(Event event) throws Exception {
			CheckListcell lc = (CheckListcell) event.getTarget().getPreviousSibling().getPreviousSibling();
			XPathUtils.setValue(lc, "/check", Boolean.FALSE);
			Checkbox cb = (Checkbox) event.getTarget().getPreviousSibling();
			cb.setChecked(false);
		}
	};

}
