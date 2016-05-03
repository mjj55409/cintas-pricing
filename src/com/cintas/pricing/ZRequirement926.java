/**
 * @version 1.0
 */

package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;
import com.sap.spe.base.logging.UserexitLogger;

public class ZRequirement926 extends RequirementAdapter {

	public boolean checkRequirement(IConditionFindingManagerUserExit item,
			IStep step, IAccess access) {

	  UserexitLogger userexitLogger = new UserexitLogger(ZRequirement926.class);
	  
	  String productGuid = item.getAttributeValue("PRODUCT");
	  
	  java.util.Vector ancillaryMaterials = new java.util.Vector(4);	  
	  ancillaryMaterials.add(item.getAttributeValue("ZZ_ANC_INSURANCE"));
	  ancillaryMaterials.add(item.getAttributeValue("ZZ_ANC_MINIMUM"));
	  ancillaryMaterials.add(item.getAttributeValue("ZZ_ANC_SERVICE"));
	  ancillaryMaterials.add(item.getAttributeValue("ZZ_ANC_FREIGHT"));

	  if (ancillaryMaterials.contains(productGuid)) {
	    userexitLogger.writeLogDebug("Rejecting product " + productGuid + " as an ancillary material.");
	    return false;
	  }

    java.util.Vector usageCodes = new java.util.Vector(5);
    usageCodes.add("B");
    usageCodes.add("L");
    usageCodes.add("R");
    usageCodes.add("X");
    usageCodes.add("D");
		
    if (usageCodes.contains(item.getAttributeValue("USAGE"))) {
      userexitLogger.writeLogDebug("Excluding usage for product " + productGuid + ".");
      return false;
    }
		
	  userexitLogger.writeLogDebug("Requirement satisfied.");
		return true;
	}
}