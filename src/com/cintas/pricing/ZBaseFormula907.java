/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZBaseFormula907 extends BaseFormulaAdapter {

	public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {
		
		return null;
	}
}
