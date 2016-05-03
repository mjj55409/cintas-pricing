/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula914 extends ValueFormulaAdapter {

	public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {
		
		if (pricingCondition.getConditionClass() == 'A') {
			BigDecimal conditionValue = pricingCondition.getConditionBase().getValue().multiply(pricingCondition.getConditionRate().getValue());
			if (pricingCondition.getPricingUnit().getValue().compareTo(new BigDecimal("0")) != 0) {
				conditionValue = conditionValue.divide(pricingCondition.getPricingUnit().getValue(),BigDecimal.ROUND_UNNECESSARY);
			}
			
			conditionValue = conditionValue.subtract(pricingItem.getNetValueAsBigDecimal());
			return conditionValue;
		}
		
		return null;
	}
}