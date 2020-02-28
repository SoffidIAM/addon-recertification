package com.soffid.iam.addons.recertification.web;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zul.Checkbox;
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

public class UsersListcell2 extends DataListener {
	Log log = LogFactory.getLog(getClass());
	public UsersListcell2() {
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
				if (rr.getCheck1() == null)
					addUser ( rr.getStep1Author(), rr.getStep1Users() );
				else if (rr.getCheck2() == null)
					addUser ( rr.getStep2Author(), rr.getStep2Users() );
				else if (rr.getCheck3() == null)
					addUser ( rr.getStep3Author(), rr.getStep3Users() );
				else if (rr.getCheck4() == null)
					addUser ( rr.getStep4Author(), rr.getStep4Users() );
			}			
		}
	}

	private void addUser(String author, String userName) {
		if (author == null &&  userName != null && ! userName.isEmpty())
		{
			Security.nestedLogin(Security.ALL_PERMISSIONS);
			try { 
				for (String s: userName.split(" +"))
				{
					User user = EJBLocator.getUserService().findUserByUserName(s);
					if (user != null)
					{
						Div d = new Div();
						getParent().appendChild(d);
						d.appendChild(new Label (user.getUserName()+" - "+user.getFullName()));
					}
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
