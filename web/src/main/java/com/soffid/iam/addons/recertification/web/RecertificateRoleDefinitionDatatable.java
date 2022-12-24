package com.soffid.iam.addons.recertification.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

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
import com.soffid.iam.addons.recertification.common.RecertifiedRoleDefinition;
import com.soffid.iam.api.Role;
import com.soffid.iam.api.RoleDependencyStatus;
import com.soffid.iam.api.RoleGrant;
import com.soffid.iam.api.User;
import com.soffid.iam.utils.Security;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.component.DataTable;
import es.caib.zkib.component.DateFormats;
import es.caib.zkib.datamodel.DataModelCollection;
import es.caib.zkib.datamodel.DataNode;

public class RecertificateRoleDefinitionDatatable extends DataTable {

	@Override
	protected JSONObject getClientValue(Object element) throws JSONException {
		JSONObject o = wrap(element);
		DataNode dn = (DataNode) element;
		if (dn != null)
		{
			RecertifiedRoleDefinition rr = (RecertifiedRoleDefinition) dn.getInstance();
			if (rr != null)
			{
				StringBuffer sb = new StringBuffer();
				addUser ( sb, rr.getStep1Author(), rr.getCheck1(), rr.getStep1Date() );
				addUser ( sb, rr.getStep2Author(), rr.getCheck2(), rr.getStep2Date() );
				addUser ( sb, rr.getStep3Author(), rr.getCheck3(), rr.getStep3Date() );
				addUser ( sb, rr.getStep4Author(), rr.getCheck4(), rr.getStep4Date() );
				if (rr.getComments() != null)
					sb.append("<div>")
					.append(rr.getComments().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"))
					.append("</div>") ;
				o.put("previousEndorsement", sb.toString());
				
				Boolean checked  = null;
				if (rr.getStep1Date() == null)
					checked = rr.getCheck1();
				else if (rr.getStep2Date() == null)
					checked = rr.getCheck2();
				else if (rr.getStep3Date() == null)
					checked = rr.getCheck3();
				else if (rr.getStep4Date() == null)
					checked = rr.getCheck4();
				o.put ("accepted", Boolean.TRUE.equals(checked) ? "checked": "false");
				o.put ("rejected", Boolean.FALSE.equals(checked) ? "checked": "false");
				sb = new StringBuffer();
				addPending(sb, rr.getStep1Author(), rr.getStep1Users() );
				addPending(sb, rr.getStep2Author(), rr.getStep2Users() );
				addPending(sb, rr.getStep3Author(), rr.getStep3Users() );
				addPending(sb, rr.getStep4Author(), rr.getStep4Users() );
				o.put("pendingEndorsement", sb.toString());
				
				DataModelCollection rolesNode = dn.getListModel("role");
				if (rolesNode != null && rolesNode.getSize() > 0) {
					DataNode roleNode = (DataNode) rolesNode.getDataModel(0);
					if (roleNode != null && ! roleNode.isDeleted()) {
						Role role = (Role) roleNode.getInstance();
						LinkedList<RolePointer> columns = (LinkedList<RolePointer>) getAttribute("columns");
						if (columns != null) {
							for (int i = 0; i < columns.size(); i++) {
								String title = "";
								String image = "blank";
								String hidden="";
								RolePointer col = columns.get(i);
								for (RoleGrant grant: role.getOwnedRoles()) {				
									if (grant.getRoleId().equals(col.id)) {
										if (grant.getStatus() == RoleDependencyStatus.STATUS_ACTIVE) {
											title="Enabled";
											image = "ok";
										}
										if (grant.getStatus() == RoleDependencyStatus.STATUS_TOAPPROVE) {
											title="To add";
											image = "add";
										}
										if (grant.getStatus() == RoleDependencyStatus.STATUS_TOREMOVE) {
											title="To remove";
											image = "trash";
										}
									}
								}
								o.put("r"+i+"_hidden", hidden);
								o.put("r"+i, image);
								o.put("r"+i+"title", title);
							}
						}
					}
				}

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
				sb.append("<div>");
				if (approved != null)
				{
					sb.append("<input type='checkbox' disabled='true' checked='true' ");
					if (approved.booleanValue())
						sb.append("class='custom-checkbox-green custom-checkbox'");
					else
						sb.append("class='custom-checkbox-red custom-checkbox'");
					sb.append("/>");
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
