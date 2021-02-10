package com.soffid.iam.addons.recertification.web;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.soffid.iam.addons.recertification.common.RecertificationProcess;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationService;
import com.soffid.iam.addons.recertification.core.ejb.RecertificationServiceHome;
import com.soffid.iam.api.AsyncList;
import com.soffid.iam.web.menu.DynamicMenuHandler;
import com.soffid.iam.web.menu.MenuOption;

public class RecertificationMenuHandler implements DynamicMenuHandler {
    int l = 0;
    
	@Override
	public List<MenuOption> getOptions(MenuOption option) {
		return null;
	}

	@Override
	public String getTip(MenuOption option) {
		return Integer.toString(l);
	}

	public boolean isVisible(MenuOption option) {
		try {
			RecertificationService svc = (RecertificationService) new InitialContext().lookup(RecertificationServiceHome.JNDI_NAME);
			List<RecertificationProcess> list = svc.findActiveRecertificationProcesses();
			l = list.size();
			return l > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isLeaf() {
		return true;
	}

}
