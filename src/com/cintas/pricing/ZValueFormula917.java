/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.base.util.event.ClearAllStatusEvent;
import com.sap.spe.base.util.event.ErrorStatusEvent;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula917 extends ValueFormulaAdapter {

	//private ErrorStatusEvent event2 = new ErrorStatusEvent(ZValueFormula917.class,"PRC_PRI",801,new String[] {"ZPR0"},"","MandatoryFieldItem",false);
	private ErrorStatusEvent ZPR0Event = new ErrorStatusEvent(this, "Mandatory Condition ZPR0 is Missing", "MandatoryFieldItem");
	private ErrorStatusEvent ZBOKEvent = new ErrorStatusEvent(this, "Mandatory Condition ZBOK is Missing", "MandatoryFieldItem");
	private ErrorStatusEvent ZPR1Event = new ErrorStatusEvent(this, "Mandatory Condition ZPR1 is Missing", "MandatoryFieldItem");

	public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {
	  
	  UserexitLogger userexitLogger = new UserexitLogger(ZValueFormula917.class);

	  pricingItem.clearStatusMessage(ZPR0Event);
		pricingItem.clearStatusMessage(ZBOKEvent);
		pricingItem.clearStatusMessage(ZPR1Event);
		pricingItem.clearStatusMessage(new ClearAllStatusEvent(this, "MandatoryFieldItem"));

		String productGuid = pricingItem.getAttributeValue("PRODUCT");

		String usage = pricingItem.getAttributeValue("ZZ_USAGE") != null
				? pricingItem.getAttributeValue("ZZ_USAGE") : "";

		String[] ancillaryMaterials = new String[4];
	  ancillaryMaterials[0] = pricingItem.getAttributeValue("ZZ_ANC_INSURANCE");
	  ancillaryMaterials[1] = pricingItem.getAttributeValue("ZZ_ANC_MINIMUM");
	  ancillaryMaterials[2] = pricingItem.getAttributeValue("ZZ_ANC_SERVICE");
	  ancillaryMaterials[3] = pricingItem.getAttributeValue("ZZ_ANC_FREIGHT");
	  
    userexitLogger.writeLogDebug("Pricing for product: " + productGuid );

		IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
		IPricingConditionUserExit ZPR0 = null;
		IPricingConditionUserExit ZBOK = null;
		IPricingConditionUserExit ZPR1 = null;
		
		ErrorStatusEvent errorEvent = null;
		
		for (int i=0; i<conditions.length; i++) {
			String conditionType = conditions[i].getConditionTypeName() != null
					? conditions[i].getConditionTypeName() : "";
			if (conditionType.equals("ZPR0")) {
			  userexitLogger.writeLogDebug("Found condition ZPR0.");
				ZPR0 = conditions[i];
			}				
			else if (conditionType.equals("ZBOK")) {
			  userexitLogger.writeLogDebug("Found condition ZBOK.");
			  ZBOK = conditions[i];
			}
			else if (conditionType.equals("ZPR1")) {
			  userexitLogger.writeLogDebug("Found condition ZPR1.");
			  ZPR1 = conditions[i];
			}
		}		
		
		//if (materialGroup.equals("ANC")) {
		for (int i = 0; i < ancillaryMaterials.length; i++) {
		  if (ancillaryMaterials[i].equals(productGuid)) {
		    userexitLogger.writeLogDebug("Checking ancillary material for ZPR1...");
		    if (ZPR1 == null || ZPR1.getConditionRate().getValue().compareTo(new BigDecimal("0")) == 0) {
				  //pricingItem.setStatusMessage(ZPR1Event);
				  return new BigDecimal("0");
			  } else {
			    userexitLogger.writeLogDebug("...Condition OK.");
			    return null;
			  }
		  }
		}

		if (ZBOK == null) {
		  userexitLogger.writeLogDebug("Condition ZBOK not found.");
			errorEvent = ZBOKEvent;
		}
		
		// Execute for usage codes that are not B, D, L, R, or X
		if ("BDLRX".indexOf(usage) < 0) {
			if (ZPR0 == null) {
			  userexitLogger.writeLogDebug("Condition ZPR0 not found.");
				errorEvent = ZPR0Event;
			}
		}

		if (errorEvent != null) {
		  userexitLogger.writeLogDebug("CRM message set: " + errorEvent.getMessage());
			pricingItem.setStatusMessage(errorEvent);
			return new BigDecimal("0");
		}
		
		return null;
	}
	
//  private void clearErrorMessage(IPricingItemUserExit pricingItem){
//    pricingItem.clearStatusMessage(new ClearAllStatusEvent(this, "MandatoryFieldItem"));
//  }
}
