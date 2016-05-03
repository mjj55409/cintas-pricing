/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @author Michael Josephson (michael@mertisconsulting.com)
 * @version 1.0
 */
package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement925 extends RequirementAdapter {

	public boolean checkRequirement(IConditionFindingManagerUserExit item,
			IStep step, IAccess access) {
		
	  return (item.getAttributeValue("USAGE").equals("X"));
	}
}