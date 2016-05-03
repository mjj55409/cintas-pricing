/**
 */

package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;
import com.sap.spe.base.logging.UserexitLogger;

public class ZRequirement922 extends RequirementAdapter {

	public boolean checkRequirement(IConditionFindingManagerUserExit item,
			IStep step, IAccess access) {
		
	  UserexitLogger userexitLogger = new UserexitLogger(ZRequirement922.class);
	  
	  String productGuid = item.getAttributeValue("PRODUCT");
	  
	  java.util.Vector ancillaryMaterials = new java.util.Vector(4);	  
	  ancillaryMaterials.add(item.getAttributeValue("ZZ_ANC_INSURANCE"));
	  ancillaryMaterials.add(item.getAttributeValue("ZZ_ANC_MINIMUM"));
	  ancillaryMaterials.add(item.getAttributeValue("ZZ_ANC_SERVICE"));
	  ancillaryMaterials.add(item.getAttributeValue("ZZ_ANC_FREIGHT"));

	  if (ancillaryMaterials.contains(productGuid)) {
	    userexitLogger.writeLogDebug("Excluding product " + productGuid + " as an ancillary material.");
	    return false;
	  }
		
	  userexitLogger.writeLogDebug("Requirement satisfied.");
	  return true;
	}
}