/**

 * @version 1.0
 */

package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;
import com.sap.spe.base.logging.UserexitLogger;


public class ZRequirement914 extends RequirementAdapter {

	public boolean checkRequirement(IConditionFindingManagerUserExit item,
			IStep step, IAccess access) {
		
 	  UserexitLogger userexitLogger = new UserexitLogger(ZRequirement914.class);
	  
	  String productGuid = item.getAttributeValue("PRODUCT");
	  
	  java.util.Vector ancillaryMaterials = new java.util.Vector(4);	  
	  ancillaryMaterials.add(item.getAttributeValue("ZZ_ANC_INSURANCE"));
	  ancillaryMaterials.add(item.getAttributeValue("ZZ_ANC_MINIMUM"));
	  ancillaryMaterials.add(item.getAttributeValue("ZZ_ANC_SERVICE"));
	  ancillaryMaterials.add(item.getAttributeValue("ZZ_ANC_FREIGHT"));

	  if (ancillaryMaterials.contains(productGuid)) {
	    userexitLogger.writeLogDebug("Product " + productGuid + " is an ancillary material.");
	    return false;
	  }

		String specialHandling = item.getAttributeValue("SPECIAL HANDLING") != null ? item.getAttributeValue("SPECIAL HANDLING") : "";
		String packageCode = item.getAttributeValue("PACKAGE CODE") != null ? item.getAttributeValue("PACKAGE CODE") : "";
		
		userexitLogger.writeLogDebug("ZZ_SPCHAND = " + specialHandling);
		userexitLogger.writeLogDebug("ZZ_PACKCD = " + packageCode);
		
		if (specialHandling.equals("") && packageCode.equals("")) {
		  userexitLogger.writeLogDebug("Requirement returning false.");
			return false;
		}
		
		/* The rest of the code in requirement 914 was moved to ZPrepareVariants.
		 * The reason for this is that IPC does not allow you to set variant 
		 * conditions in a requirement.
		 */
		userexitLogger.writeLogDebug("Requirement satisfied.");
		return true;
	}
}
