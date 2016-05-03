/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.conversion.exc.ConversionMissingDataException;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula926 extends ValueFormulaAdapter {

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
		
		String docCurrency = pricingItem.getUserExitDocument().getDocumentCurrencyUnit().getUnitName();
		String condCurrency = docCurrency;
		
		if (docCurrency != null && docCurrency.equals("USD"))
			condCurrency = "US3";
		else if (docCurrency != null && docCurrency.equals("CAD"))
			condCurrency = "CD3";		
		
		BigDecimal subtotal1 = pricingItem.getSubtotal('1').getValue();
		if (subtotal1.compareTo(new BigDecimal(0)) > 0) {
			if (pricingCondition.getConditionBase().getValue().compareTo(new BigDecimal("0")) != 0) {
				try {
					pricingCondition.setConditionRate(subtotal1.divide(pricingCondition.getConditionBase().getValue(),3,BigDecimal.ROUND_HALF_UP), condCurrency);
				}
				catch (ConversionMissingDataException ex) {
					pricingCondition.setConditionRateValue(subtotal1.divide(pricingCondition.getConditionBase().getValue(),3,BigDecimal.ROUND_HALF_UP));
				}
			}			
			return subtotal1;
		}
		else {
			return null;
		}
	}
}