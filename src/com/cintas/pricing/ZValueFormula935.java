/**
 * @author Michael Josephson
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula935 extends ValueFormulaAdapter {

	public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {
	  
	  UserexitLogger userexitLogger = new UserexitLogger(ZValueFormula935.class);
	  
	  BigDecimal kbetr = null;

	  String specialHandling = pricingItem.getAttributeValue("ZZ_SPCHAND");
	  if (specialHandling.length() == 4 && specialHandling.startsWith("T")) {
	    kbetr = pricingCondition.getConditionRate().getValue();
	    kbetr = kbetr.subtract(pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_K));
	    userexitLogger.writeLogDebug("Using condition value (kbetr) = " + kbetr);
	  } else {
	    userexitLogger.writeLogDebug("Missing or invalid special handling code.");
	  }

		return com.cintas.pricing.ZValueFormula915.roundConditionValue(
		    pricingCondition.getConditionRate().getValue(), 
		    pricingCondition.getConditionBase().getValue(),
		    pricingCondition.getConditionRate().getUnitName(),
		    pricingCondition.getPricingUnit().getValue());

//		String unit = pricingCondition.getConditionRate().getUnitName();
//		if (unit != null && (unit.equals("US3") || unit.equals("CD3"))) {
//			if (pricingCondition.getPricingUnit().getValue() != null && pricingCondition.getPricingUnit().getValue().compareTo(BigDecimal.ZERO) > 0) {
//			  kbetr = kbetr
//			      .multiply(pricingCondition.getConditionBase().getValue())
//			      .divide(pricingCondition.getPricingUnit().getValue(), BigDecimal.ROUND_UNNECESSARY);
//			}
//			userexitLogger.writeLogDebug("Using condition value = " + kbetr);
//		}
//		
//		return kbetr;
	}
}