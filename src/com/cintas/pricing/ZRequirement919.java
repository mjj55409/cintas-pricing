package com.cintas.pricing;

import java.math.BigInteger;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement919 extends RequirementAdapter {

	public boolean checkRequirement(IConditionFindingManagerUserExit item,
			IStep step, IAccess access) {

	  UserexitLogger userexitLogger = new UserexitLogger(ZRequirement919.class);
	  
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

	  if ((new BigInteger(item.getAttributeValue("ZZ_LR_QTY")).compareTo(BigInteger.ZERO) == 0)) {
	    userexitLogger.writeLogDebug("Excluding product " + productGuid + ", ZZ_LR_QTY = 0.");
	    return false;
	  }
		
	  userexitLogger.writeLogDebug("Requirement satisfied.");
	  return true;

		// TODO: ECC req. sets usage code to "R".  This will
		//       have to be done in a new ABAP callback function.
	}
}