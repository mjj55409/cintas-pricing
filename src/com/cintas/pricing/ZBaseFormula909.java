/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZBaseFormula909 extends BaseFormulaAdapter {

	public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {

		String materialGroup = pricingItem.getAttributeValue("MATERIAL GROUP") != null
				? pricingItem.getAttributeValue("MATERIAL GROUP") : "";
		String accountAssignmentGroup = pricingItem.getAttributeValue("ACCOUNT ASSIGNMENT GROUP") != null
				? pricingItem.getAttributeValue("ACCOUNT ASSIGNMENT GROUP") : "";
		
				
		/* No charge can be set either on the item in WebUI or automatically
		 * due to exclusion rules in Base Formula 903.
		 */
		String itemNoCharge = pricingItem.getAttributeValue("NO CHARGE") != null
				? pricingItem.getAttributeValue("NO CHARGE") : "";
		String objectNoCharge = pricingItem.getObjectForUserExits("NO CHARGE") != null 
				? (String)pricingItem.getObjectForUserExits("NO CHARGE") : "";
				
		if (materialGroup.equals("ANC") 
				&& accountAssignmentGroup.equals("24") 
				&& (itemNoCharge.equals("X") || objectNoCharge.equals("X"))) {
			
			// Ancillary insurance obtains value from ZPR1 record.
			
			// Get ZPR1 record from the conditions table
			IPricingConditionUserExit ZPR1 = null;
			IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();			
			
			for (int i=0; i<conditions.length; i++) {
				String conditionType = conditions[i].getConditionTypeName();
				if (conditionType != null && conditionType.equals("ZPR1")) {
					ZPR1 = conditions[i];
					break;
				}
			}
			
			if (ZPR1 != null 
					&& ZPR1.getInactive() == ' ' 
					&& ZPR1.getConditionValue().getValue().compareTo(new BigDecimal("0")) > 0) {
				return ZPR1.getConditionValue().getValue();
			}
			else {
				return new BigDecimal("0");
			}
		}
		else {
			// Value is net value - cash discount - insurance subtotal (3)
			return pricingItem.getNetValueAsBigDecimal().add(pricingItem.getCashDiscountAsBigDecimal()).subtract(pricingItem.getSubtotalAsBigDecimal('3'));
		}
	}
}
