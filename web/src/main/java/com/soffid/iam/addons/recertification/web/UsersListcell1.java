package com.soffid.iam.addons.recertification.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.addons.recertification.common.RecertifiedRole;
import com.soffid.iam.api.User;
import com.soffid.iam.utils.Security;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.component.DataListener;
import es.caib.zkib.datamodel.DataNode;
import es.caib.zkib.events.XPathEvent;

public class UsersListcell1 extends DataListener {
	Log log = LogFactory.getLog(getClass());
	public UsersListcell1() {
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
				addUser ( rr.getStep1Author(), rr.getCheck1(), rr.getStep1Date() );
				addUser ( rr.getStep2Author(), rr.getCheck2(), rr.getStep2Date() );
				addUser ( rr.getStep3Author(), rr.getCheck3(), rr.getStep3Date() );
				addUser ( rr.getStep4Author(), rr.getCheck4(), rr.getStep4Date() );
			}			
		}
	}

	private void addUser(String author, Boolean approved, Date date) {
		if (author != null && ! author.isEmpty())
		{
			Security.nestedLogin(Security.ALL_PERMISSIONS);
			try { 
				User user = EJBLocator.getUserService().findUserByUserName(author);
				Div d = new Div();
				getParent().appendChild(d);
				if (approved != null)
				{
					Checkbox cb = new Checkbox();
					cb.setChecked(true);
					cb.setDisabled(true);
					if (approved.booleanValue())
						cb.setSclass("custom-checkbox-green custom-checkbox");
					else
						cb.setSclass("custom-checkbox-red custom-checkbox");
					d.appendChild(cb);
				}
				d.appendChild(new Label (user.getUserName()+" - "+user.getFullName()+" "));
				if (date != null)
				{
					String l = new SimpleDateFormat(Labels.getLabel("accounts.dateFormat")).format(date);
					d.appendChild(new Label(l));
				}
			} catch (InternalErrorException e) {
				log.warn ("Error fetching user information", e);
			} catch (NamingException e) {
				log.warn ("Error fetching user information", e);
			} catch (CreateException e) {
				log.warn ("Error fetching user information", e);
			} finally {
				Security.nestedLogoff();
			}
		}		
	}

}
