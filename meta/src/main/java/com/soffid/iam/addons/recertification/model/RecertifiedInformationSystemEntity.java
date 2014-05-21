//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.recertification.model;
import com.soffid.mda.annotation.*;

@Entity (table="SCR_RECIS" )
@Depends ({com.soffid.iam.addons.recertification.common.RecertifiedInformationSystem.class,
	com.soffid.iam.addons.recertification.model.RecertificationProcessEntity.class,
	es.caib.seycon.ng.model.AplicacioEntity.class})
public abstract class RecertifiedInformationSystemEntity {

	@Column (name="RIS_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="RIS_PROCES")
	public com.soffid.iam.addons.recertification.model.RecertificationProcessEntity process;

	@Column (name="RIS_IS")
	public es.caib.seycon.ng.model.AplicacioEntity informationSystem;

}
