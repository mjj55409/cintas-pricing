/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula925 extends ValueFormulaAdapter {
	
	public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {
		
		/* No charge can be set either on the item in WebUI or automatically
		 * due to exclusion rules in Base Formula 903.
		 */
		String itemNoCharge = pricingItem.getAttributeValue("NO CHARGE") != null
				? pricingItem.getAttributeValue("NO CHARGE") : "";
		String objectNoCharge = pricingItem.getObjectForUserExits("NO CHARGE") != null 
				? (String)pricingItem.getObjectForUserExits("NO CHARGE") : "";
		
		if (itemNoCharge.equals("X") || objectNoCharge.equals("X")) {
			return new BigDecimal(0);
		}
		
		BigDecimal subtotalI = pricingItem.getSubtotal('I').getValue();
		if (subtotalI.compareTo(new BigDecimal(0)) > 0) {
			return subtotalI;
		}
		else {
			return pricingItem.getSubtotal('E').getValue();
		}
	}
}
