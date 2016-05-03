/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula933 extends ValueFormulaAdapter {

	public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {
		
		BigDecimal conditionRate = pricingCondition.getConditionRate().getValue();
		if (conditionRate.compareTo(new BigDecimal("0")) == 0 || conditionRate.compareTo(new BigDecimal("0.001")) == 0) {
			pricingCondition.setConditionRateValue(new BigDecimal("0.001"));
			return new BigDecimal("0");
		}
		
		return null;
	}
}