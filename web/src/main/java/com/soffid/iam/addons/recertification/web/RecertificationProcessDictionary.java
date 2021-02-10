package com.soffid.iam.addons.recertification.web;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.soffid.iam.addons.recertification.common.ProcessStatus;
import com.soffid.iam.web.SearchAttributeDefinition;
import com.soffid.iam.web.SearchDictionary;

import es.caib.seycon.ng.comu.TypeEnumeration;

public class RecertificationProcessDictionary extends SearchDictionary {
	List<SearchAttributeDefinition> attributes;

	public RecertificationProcessDictionary() {
		attributes = new LinkedList<SearchAttributeDefinition>();
		SearchAttributeDefinition type;
		
		type = new SearchAttributeDefinition("name", TypeEnumeration.STRING_TYPE, String.class);
		type.setLabelName("recertification.Name");
		attributes.add(type);

		type = new SearchAttributeDefinition("status", TypeEnumeration.STRING_TYPE, String.class);
		type.setLabelName("recertification.Status");
		type.setValues(Arrays.asList(ProcessStatus.FINISHED.getValue(), ProcessStatus.CANCELLED.getValue(), ProcessStatus.PREPARATION.getValue(), ProcessStatus.ACTIVE.getValue()));
		type.setLabels(Arrays.asList("Finished", "Cancelled", "Preparation", "Active"));
		attributes.add(type);

		type = new SearchAttributeDefinition("template.name", TypeEnumeration.STRING_TYPE, String.class);
		type.setLabelName("recertification.template");
		attributes.add(type);

		type = new SearchAttributeDefinition("startDate", TypeEnumeration.DATE_TYPE, Date.class);
		type.setLabelName("recertification.StartDate");
		attributes.add(type);

		type = new SearchAttributeDefinition("finishDate", TypeEnumeration.DATE_TYPE, Date.class);
		type.setLabelName("recertification.FinishDate");
		attributes.add(type);

	}
	
	@Override
	public List<SearchAttributeDefinition> getAttributes() {
		return attributes;
	}

}
