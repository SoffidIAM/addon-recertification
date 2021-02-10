package com.soffid.iam.addons.recertification.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

import com.soffid.iam.EJBLocator;
import com.soffid.iam.addons.recertification.common.RecertifiedRole;
import com.soffid.iam.api.User;
import com.soffid.iam.utils.Security;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.component.DataTable;
import es.caib.zkib.component.DateFormats;
import es.caib.zkib.datamodel.DataNode;

public class RolesDatatable extends DataTable {

	@Override
	protected JSONObject getClientValue(Object element) throws JSONException {
		JSONObject o = wrap(element);
		DataNode dn = (DataNode) element;
		if (dn != null)
		{
			RecertifiedRole rr = (RecertifiedRole) dn.getInstance();
			if (rr != null)
			{
				StringBuffer sb = new StringBuffer();
				addUser ( sb, rr.getStep1Author(), rr.getCheck1(), rr.getStep1Date() );
				addUser ( sb, rr.getStep2Author(), rr.getCheck2(), rr.getStep2Date() );
				addUser ( sb, rr.getStep3Author(), rr.getCheck3(), rr.getStep3Date() );
				addUser ( sb, rr.getStep4Author(), rr.getCheck4(), rr.getStep4Date() );
				o.put("previousEndorsement", sb.toString());

				sb = new StringBuffer();
				addPending(sb, rr.getStep1Author(), rr.getStep1Users() );
				addPending(sb, rr.getStep2Author(), rr.getStep2Users() );
				addPending(sb, rr.getStep3Author(), rr.getStep3Users() );
				addPending(sb, rr.getStep4Author(), rr.getStep4Users() );
				o.put("pendingEndorsement", sb.toString());
			}			
		}
		return o;
	}

	private void addUser(StringBuffer sb, String author, Boolean approved, Date date) {
		if (approved != null && author != null && ! author.isEmpty())
		{
			Security.nestedLogin(Security.ALL_PERMISSIONS);
			try { 
				User user = EJBLocator.getUserService().findUserByUserName(author);
				sb.append("<div style='display:block; width: 100%; min-height: 26px'> ");
				if (approved != null)
				{
					sb.append("<div ");
					if (approved.booleanValue())
						sb.append("class='custom-checkbox-green custom-checkbox'>");
					else
						sb.append("class='custom-checkbox-red custom-checkbox'>");
					sb.append("<input type='checkbox' disabled='true' checked='true'/> <label> </label> ")
						.append("</div>");
				}
				sb.append(encode (user.getUserName()+" - "+user.getFullName()+" "));
				if (date != null)
				{
					sb.append(encode ( DateFormats.getDateTimeFormat().format(date) ));
				}
				sb.append("</div>");
			} catch (Exception e) {
				LogFactory.getLog(getClass()).warn ("Error fetching user information", e);
			} finally {
				Security.nestedLogoff();
			}
		}		
	}
	
	private String encode(String string) {
		return string.replace("&", "&amp;")
					.replace("<", "&lt;")
					.replace(">", "&gt;");
	}

	private void addPending(StringBuffer sb, String author, String userName) {
		if (author == null && userName != null && ! userName.trim().isEmpty())
		{
			Security.nestedLogin(Security.ALL_PERMISSIONS);
			try { 
				for (String s: userName.split(" +"))
				{
					User user = EJBLocator.getUserService().findUserByUserName(s);
					if (user != null)
					{
						sb.append("<div>")
							.append(encode(user.getUserName()))
							.append(" ")
							.append(encode(user.getFullName()))
							.append("</div>");
					}
				}
			} catch (Exception e) {
				LogFactory.getLog(getClass()).warn ("Error fetching user information", e);
			} finally {
				Security.nestedLogoff();
			}
		}		
	}
	
}
