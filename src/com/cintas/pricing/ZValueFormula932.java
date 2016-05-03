/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula932 extends ValueFormulaAdapter {

	public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {
		
		BigDecimal surchargeRate = new BigDecimal("0");
		BigDecimal surchargeValue = new BigDecimal("0");
		
		IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
		for (int i=0; i<conditions.length; i++) {
			String conditionType = conditions[i].getConditionTypeName() != null 
					? conditions[i].getConditionTypeName() : "";
					
			if (conditionType.equals("ZSUR")) {
				surchargeRate = surchargeRate.add(conditions[i].getConditionRate().getValue());
				surchargeValue = surchargeValue.add(conditions[i].getConditionValue().getValue());
			}
		}
		
		pricingCondition.setConditionRateValue(surchargeRate);
		
		String stopExclusion = pricingCondition.getConditionRecord().getVariableDataValue("ZZ_STOPEXCL") != null ?
				pricingCondition.getConditionRecord().getVariableDataValue("ZZ_STOPEXCL") : "";
		BigDecimal priceMin = pricingCondition.getConditionRecord().getVariableKeyValue("ZZ_PRICMIN") != null ?
				new BigDecimal(pricingCondition.getConditionRecord().getVariableKeyValue("ZZ_PRICMIN")) : new BigDecimal("0");
		BigDecimal priceMax = pricingCondition.getConditionRecord().getVariableKeyValue("ZZ_PRICMAX") != null ?
				new BigDecimal(pricingCondition.getConditionRecord().getVariableKeyValue("ZZ_PRICMAX")) : new BigDecimal("0");	
				
		char minMaxIndicator = ' ';
		
		if (stopExclusion.equals("X")) {
			pricingCondition.setConditionRateValue(new BigDecimal("0"));
			minMaxIndicator = 'E';
		}
		else {
			if (priceMin.compareTo(new BigDecimal("0")) > 0 && surchargeRate.compareTo(priceMin) < 0) {
				pricingCondition.setConditionRateValue(priceMin);
				minMaxIndicator = 'M';
			}
			else if (priceMax.compareTo(new BigDecimal("0")) > 0 && surchargeRate.compareTo(priceMax) > 0) {
				pricingCondition.setConditionRateValue(priceMax);
				minMaxIndicator = 'X';
			}
		}
		
		if (pricingCondition.getConditionClass() == 'A') { // Discount or Surcharge
			// Perform standard SAP calculation (rate x base / unit)
			BigDecimal conditionValue = pricingCondition.getConditionRate().getValue().multiply(pricingCondition.getConditionBase().getValue());
			BigDecimal pricingUnit = pricingCondition.getPricingUnit().getValue();
			if (pricingUnit.compareTo(new BigDecimal("0")) > 0) {
				conditionValue = conditionValue.divide(pricingUnit,BigDecimal.ROUND_UNNECESSARY);
			}
			
			switch (minMaxIndicator) {
				case 'X':
					if (conditionValue.compareTo(surchargeValue) < 0) {
						/* Surcharge value is greater than price max.
						 * Value is the difference between the two.
						 */
						conditionValue = conditionValue.subtract(surchargeValue);
					}
					else
						conditionValue = new BigDecimal("0");
					break;
				case 'M':
					if (conditionValue.compareTo(surchargeValue) > 0) {
						/* Surcharge value is less than price min.
						 * Value is the difference between the two.
						 */
						conditionValue = conditionValue.subtract(surchargeValue);
					}
					else
						conditionValue = new BigDecimal("0");
					break;
				case 'E':
					/* Price is excluded.
					 * Value is the negative surcharge value, so that it gets
					 * completely canceled out.
					 */
					conditionValue = surchargeValue.multiply(new BigDecimal("-1"));
					break;
				default:
					conditionValue = new BigDecimal("0");
					break;
			}
			
			if (pricingCondition.getCalculationType() == 'C') {
				BigDecimal conditionBase = pricingCondition.getConditionBase().getValue();
				if (conditionBase.compareTo(new BigDecimal("0")) != 0) {
					pricingCondition.setConditionRateValue(conditionValue.divide(conditionBase,BigDecimal.ROUND_UNNECESSARY));
				}
			}
			
			return conditionValue;
		}
		
		// Default to zero if it wasn't set to anything else.
		return new BigDecimal("0");	
	}
}