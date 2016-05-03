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

public class ZValueFormula928 extends ValueFormulaAdapter {

	public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {

		String docCurrency = pricingItem.getUserExitDocument().getDocumentCurrencyUnit().getUnitName();
		String condCurrency = docCurrency;
		
		if (docCurrency != null && docCurrency.equals("USD"))
			condCurrency = "US3";
		else if (docCurrency != null && docCurrency.equals("CAD"))
			condCurrency = "CD3";
		
		BigDecimal subtotalK = pricingItem.getSubtotalAsBigDecimal('K');
		if (subtotalK != null && subtotalK.compareTo(new BigDecimal(0)) > 0) {
			try {
				pricingCondition.setConditionRate(subtotalK,condCurrency);
			}
			catch (ConversionMissingDataException ex) {
				pricingCondition.setConditionRateValue(subtotalK);
			}
			
			// Return calculation considering base quantity.
			BigDecimal conditionValue = pricingCondition.getConditionBase().getValue().multiply(pricingCondition.getConditionRate().getValue());

			BigDecimal pricingUnit = pricingCondition.getPricingUnit().getValue();
			if (pricingUnit != null && pricingUnit.compareTo(new BigDecimal("0")) > 0) {
				conditionValue = conditionValue.divide(pricingUnit,BigDecimal.ROUND_UNNECESSARY);
			}
			
			return conditionValue;		
		}
		else {
			/* No charge requirement: Allow zero value initial price by setting the rate
			 * to 0.001 and the value to zero.
			 */
			IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
			for (int i=0; i<conditions.length; i++) {
				String conditionType = conditions[i].getConditionTypeName() != null 
						? conditions[i].getConditionTypeName() : "";
				
				if (conditionType.equals("ZPR0")) {
					try {
						pricingCondition.setConditionRate(new BigDecimal("0.001"),condCurrency);
					}
					catch (ConversionMissingDataException ex) {
						pricingCondition.setConditionRateValue(new BigDecimal("0.001"));
					}
					return new BigDecimal("0");
				}
			}
			
			pricingCondition.setInactive('Z');
			return null;
		}
	}
}