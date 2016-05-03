/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZBaseFormula902 extends BaseFormulaAdapter {

	public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {

		/* Base Formula 902 loads information from insurance
		 * programs into structure KOMPAZD
		 * in ECC, which is not necessary (or even possible)
		 * in CRM.  
		 * 
		 * Code to handle the dynamic values in this method
		 * was implemented in Requirement 913.
		 * 
		 * It is not necessary to implement any
		 * code in this method.
		 */
		return null;
	}
}
