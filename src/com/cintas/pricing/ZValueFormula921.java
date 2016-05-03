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

public class ZValueFormula921 extends ValueFormulaAdapter {

	public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {
		
		String docCurrency = pricingItem.getUserExitDocument().getDocumentCurrencyUnit().getUnitName();
		String condCurrency = docCurrency;
		
		if (docCurrency != null && docCurrency.equals("USD"))
			condCurrency = "US3";
		else if (docCurrency != null && docCurrency.equals("CAD"))
			condCurrency = "CD3";		
		
		BigDecimal subtotalH = pricingItem.getSubtotal('H').getValue();
		
		// Set condition rate if base value is present
		if (pricingCondition.getConditionBase().getValue().compareTo(new BigDecimal("0")) > 0) {
			try {
				pricingCondition.setConditionRate(subtotalH.divide(pricingCondition.getConditionBase().getValue(),2,BigDecimal.ROUND_HALF_UP),condCurrency);
			}
			catch (ConversionMissingDataException ex) {
				pricingCondition.setConditionRateValue(subtotalH.divide(pricingCondition.getConditionBase().getValue(),2,BigDecimal.ROUND_HALF_UP));
			}
		}
		
		return subtotalH;
	}
}