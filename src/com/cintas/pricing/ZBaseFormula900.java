/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZBaseFormula900 extends BaseFormulaAdapter {

	public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {
		
		/* The code in this base formula was implemented in ABAP
		 * function module Z_FM_CRM_QUALIFYING_AGREEMENT, which
		 * is called using a CRM event after pricing on an item
		 * is carried out.
		 * 
		 * It is not necessary to implement any code in this method.
		 */
		return null;
	}
}
