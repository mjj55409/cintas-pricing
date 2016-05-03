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

public class ZValueFormula918 extends ValueFormulaAdapter {

	public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {

		String docCurrency = pricingItem.getUserExitDocument().getDocumentCurrencyUnit().getUnitName();
		String condCurrency = docCurrency;
		
		if (docCurrency != null && docCurrency.equals("USD"))
			condCurrency = "US3";
		else if (docCurrency != null && docCurrency.equals("CAD"))
			condCurrency = "CD3";
		
		String conditionType = pricingCondition.getConditionTypeName() != null
				? pricingCondition.getConditionTypeName() : "";
				
		if (conditionType.equals("ZTC1")) {
			// Return the value of ZSPM
			IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
			for (int i=0; i<conditions.length; i++) {
				String loopConditionType = conditions[i].getConditionTypeName() != null
						? conditions[i].getConditionTypeName() : "";
				if (loopConditionType.equals("ZSPM")) {
					if (conditions[i].getConditionValue().getValue().compareTo(new BigDecimal("0")) != 0) {
						BigDecimal conditionRate = conditions[i].getConditionValue().getValue();
						if (conditions[i].getConditionBase().getValue().compareTo(new BigDecimal("0")) != 0) {
							conditionRate = conditionRate.divide(conditions[i].getConditionBase().getValue(),3,BigDecimal.ROUND_HALF_UP);
							try {
								pricingCondition.setConditionRate(conditionRate,condCurrency);
							}
							catch (ConversionMissingDataException ex) {
								pricingCondition.setConditionRateValue(conditionRate);
							}
						}
						
						return conditions[i].getConditionValue().getValue();
					}
					break;
				}
			}
		}
		else if (conditionType.equals("ZTC2")) { 
			// Insurance
			BigDecimal subtotal3 = pricingItem.getSubtotalAsBigDecimal('3');
			if (subtotal3 != null && subtotal3.compareTo(new BigDecimal("0")) != 0)
				return subtotal3;
		}
		else if (conditionType.equals("ZTC3")) {
			// Service Charge
			BigDecimal netValue = pricingItem.getNetValueAsBigDecimal();
			if (netValue != null && netValue.compareTo(new BigDecimal("0")) != 0)
				return netValue;
		}
		else if (conditionType.equals("ZTC4")) {
			// Min Charge
			BigDecimal netValue = pricingItem.getNetValueAsBigDecimal();
			if (netValue != null && netValue.compareTo(new BigDecimal("0")) != 0)
				return netValue;
		}
		else if (conditionType.equals("ZSV1")) {
			BigDecimal subtotal1 = pricingItem.getSubtotalAsBigDecimal('1');
			if (subtotal1 != null && subtotal1.compareTo(new BigDecimal("0")) != 0) {
				return subtotal1;
			}
		}
		else if (conditionType.equals("ZST$")) {
			BigDecimal subtotal5 = pricingItem.getSubtotalAsBigDecimal('5');
			if (subtotal5 != null && subtotal5.compareTo(new BigDecimal("0")) != 0) {
				return subtotal5;
			}			
		}
		//CR912
		else if (conditionType.equals("ZTC6")) {
			return pricingItem.getNetValueAsBigDecimal().subtract(pricingItem.getSubtotalAsBigDecimal('3'));
		}
		//CR912
		
		return null;
	}
}