/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

/**
 * 
 * @author Jolly Khanna
 *
 */

public class ZBaseFormula904 extends BaseFormulaAdapter {
	
	public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {

		/* No charge can be set either on the item in WebUI or automatically
		 * due to exclusion rules in Base Formula 903.
		 */
		String itemNoCharge = pricingItem.getAttributeValue("NO CHARGE") != null
				? pricingItem.getAttributeValue("NO CHARGE") : "";
		String objectNoCharge = pricingItem.getObjectForUserExits("NO CHARGE") != null 
				? (String)pricingItem.getObjectForUserExits("NO CHARGE") : "";
		
		if (itemNoCharge.equals("X") || objectNoCharge.equals("X")) {
			pricingCondition.setConditionRateValue(new BigDecimal(-100));
		}
		else {
			pricingCondition.setInactive('Z');
		}
		
		return null;
	}
}