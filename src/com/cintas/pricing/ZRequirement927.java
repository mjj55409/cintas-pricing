/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement927 extends RequirementAdapter {

	public boolean checkRequirement(IConditionFindingManagerUserExit item,
			IStep step, IAccess access) {
		
		String usage = item.getAttributeValue("USAGE") != null
				? item.getAttributeValue("USAGE") : "";
				
		// Requirement: Usage is B, L, R, or X
		if ("BLRX".indexOf(usage) >= 0)
			return true;
				
		return false;
	}
}