package com.soffid.iam.addons.recertification.core;

import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.VariableResolver;

import com.soffid.iam.ServiceLocator;
import com.soffid.iam.api.Group;
import com.soffid.iam.api.GroupUser;
import com.soffid.iam.api.Role;
import com.soffid.iam.api.RoleGrant;
import com.soffid.iam.api.System;
import com.soffid.iam.api.User;
import com.soffid.iam.model.SystemEntity;
import com.soffid.iam.service.ApplicationService;
import com.soffid.iam.service.DispatcherService;
import com.soffid.iam.service.GroupService;
import com.soffid.iam.service.UserService;

import es.caib.seycon.ng.exception.InternalErrorException;

public class CustomVariableResolver implements VariableResolver {
	private static UserService userService = ServiceLocator.instance().getUserService();
	private static GroupService groupService = ServiceLocator.instance().getGroupService();
	private static ApplicationService appService = ServiceLocator.instance().getApplicationService();
	private static DispatcherService dispatcherService = ServiceLocator.instance().getDispatcherService();
	
	private String url;
	private String user;

	public CustomVariableResolver(String s, String url) {
		this.user = s;
		this.url = url;
	}

	@Override
	public Object resolveVariable(String pName) throws ELException {
		if (pName.equals("url"))
			return url;
		else if (pName.equals("userName"))
			return user;
		else if (pName.equals("fullName"))
		{
			try {
				return getFullName(user);
			} catch (InternalErrorException e) {
				throw new ELException(e);
			}
		}
		else
			return null;
	}

	private String getFullName(String actorId) throws InternalErrorException {
		User usuari = userService.findUserByUserName(actorId);
		if (usuari != null)
		{
			return usuari.getFullName();
		}
		else
		{
			Group grup = groupService.findGroupByGroupName(actorId);
			if (grup != null)
			{
				return grup.getDescription();
			}
			else
			{
				int i = actorId.indexOf('@');
				String roleName;
				String dispatcher;
				String scope = null;
				if (i >= 0)
				{
					roleName = actorId.substring(0, i);
					dispatcher = actorId.substring(i+1);
				}
				else
				{
					roleName = actorId;
					System defaultDispatcher = dispatcherService.findSoffidDispatcher();
					dispatcher = defaultDispatcher.getName();
				}
				i = roleName.lastIndexOf('/');
				if (i >= 0)
				{
					scope = roleName.substring(i+1);
					roleName = roleName.substring(0, i);
				}
				Role role = appService.findRoleByNameAndSystem(roleName, dispatcher);
				if (role != null)
					return role.getDescription();
			}
		}
		return "";
	}

}
