/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula916 extends ValueFormulaAdapter {

	public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {

		IPricingConditionUserExit ZIPR = null;
		IPricingConditionUserExit ZPRR = null;
		
		BigDecimal ZIPRRate = new BigDecimal("0");
		BigDecimal priceMin = new BigDecimal("0");
		BigDecimal priceMax = new BigDecimal("0");
		
		char minmaxIndicator = ' ';
		
		// Get condition types ZIPR and ZPRR from the conditions table
		IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
		for (int i=0; i<conditions.length; i++) {
			String conditionType = conditions[i].getConditionTypeName();
			if (conditionType != null && conditionType.equals("ZIPR")) {
				ZIPR = conditions[i];
				ZIPRRate = ZIPR.getConditionRate().getValue();
			}
			if (conditionType != null && conditionType.equals("ZPRR")) {
				ZPRR = conditions[i];
			}
		}		
		
		// Set ZPRA to 0 if neither ZIPR nor ZPRR was found
		if (ZIPR == null && ZPRR == null)
			return new BigDecimal("0");
		
		if (ZPRR != null) { 
			// Get min and max from the ZPRR record
			priceMin = new BigDecimal(ZPRR.getConditionRecord().getVariableDataValue("ZZ_PRICMIN"));
			priceMax = new BigDecimal(ZPRR.getConditionRecord().getVariableDataValue("ZZ_PRICMAX"));
		
			// Determine Condition Rate
//			if (priceMin.compareTo(new BigDecimal("0")) != 0 && ZIPRRate.compareTo(priceMin) < 0) {
			if (ZIPRRate.compareTo(priceMin) < 0) { //CR789 - zero is an allowed value
				// Minimum is maintained and ZIPR is less than it - use the minimum
				pricingCondition.setConditionRateValue(priceMin);
				minmaxIndicator = 'M';
			}
//			else if (priceMax.compareTo(new BigDecimal("0")) != 0 && ZIPRRate.compareTo(priceMax) > 0) {
			else if (ZIPRRate.compareTo(priceMax) > 0) { //CR789 - zero is an allowed value			  
				// Maximum is maintained and ZIPR is greater than it - use the maximum
				pricingCondition.setConditionRateValue(priceMax);
				minmaxIndicator = 'X';
			}
			else {
				// Use the ZIPR value
				pricingCondition.setConditionRateValue(ZIPRRate);
			}
		}
		
		// Determine Condition Value
		if (pricingCondition.getConditionClass() == 'A' && ZIPR != null) {		
			BigDecimal ZIPRValue = ZIPR.getConditionValue().getValue();
			BigDecimal conditionValue = pricingCondition.getConditionRate().getValue().multiply(pricingCondition.getConditionBase().getValue());
			
			switch (minmaxIndicator) {
			case 'M':
				if (pricingCondition.getConditionValue().getValue().compareTo(ZIPRValue) > 0) {
					return conditionValue.subtract(ZIPRValue);
				}
				break;
				
			case 'X':
				if (pricingCondition.getConditionValue().getValue().compareTo(ZIPRValue) < 0) {
					return conditionValue.subtract(ZIPRValue);
				}
				break;
				
			default:
				break;
			}
		}
		
		return new BigDecimal("0");
	}
}
