/**
 * @author Michael Josephson
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;


public class ZValueFormula915 extends ValueFormulaAdapter {

	public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {
	  
//		String unit = pricingCondition.getConditionRate().getUnitName();
//		// Only process if the condition currency is 3 decimal places.
//		if (unit != null && (unit.equals("US3") || unit.equals("CD3"))) {
//			BigDecimal conditionRate = pricingCondition.getConditionRate().getValue();
//			BigDecimal conditionBase = pricingCondition.getConditionBase().getValue();
//			BigDecimal pricingUnit = pricingCondition.getPricingUnit().getValue();
//				
//			BigDecimal conditionValue = conditionRate.multiply(conditionBase);
//			if (pricingUnit != null && pricingUnit.compareTo(BigDecimal.ZERO) > 0)
//				conditionValue = conditionValue.divide(pricingUnit,BigDecimal.ROUND_UNNECESSARY);
//				
//			return conditionValue;
//		}
//	  return null;
		
		return roundConditionValue(
		    pricingCondition.getConditionRate().getValue(), 
		    pricingCondition.getConditionBase().getValue(),
		    pricingCondition.getConditionRate().getUnitName(),
		    pricingCondition.getPricingUnit().getValue());
	}

	static public BigDecimal roundConditionValue(BigDecimal conditionValue, BigDecimal conditionBase, String currency, BigDecimal quantity)
	{
	  // conditionValue == kbetr
	  // conditionBase == kmein
	  
		if (currency.equals("US3") || currency.equals("CD3")) {
		  if (quantity.compareTo(BigDecimal.ZERO) > 0) {
			  conditionValue = conditionValue.multiply(conditionBase).divide(quantity, BigDecimal.ROUND_UNNECESSARY);
			}
		}
		
		return conditionValue;
	}
}