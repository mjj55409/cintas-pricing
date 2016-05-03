package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.GroupKeyFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IGroupConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingDocumentUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZGroupKeyFormula092 extends GroupKeyFormulaAdapter {

	/* Set a constant group key.  This could be any string.  
	 * "092" was used in the original ECC formula, so we're using 
	 * it here as well.  By returning the same string every time,
	 * all items will be considered part of the same group.
	 */
	private static final String groupKey = "092";
	
	public String setGroupKey(IPricingDocumentUserExit pricingDocument,
			IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition,
			IGroupConditionUserExit groupCondition) {
		
		IPricingConditionUserExit ZSTR = null;
		IPricingConditionUserExit ZSTV = null;
		
		String stopExclusion = "";
		
		BigDecimal stopMin = new BigDecimal(0);
		BigDecimal stopMax = new BigDecimal(0);
		BigDecimal zstrValue = new BigDecimal(0);
		BigDecimal zstvValue = new BigDecimal(0);
		
		IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
		for (int i=0; i<conditions.length; i++) {
			String conditionType = conditions[i].getConditionTypeName() != null ? conditions[i].getConditionTypeName() : "";
			if (conditionType.equals("ZSTR")) {
				stopExclusion = conditions[i].getConditionRecord().getVariableDataValue("ZZ_STOPEXCL") != null ? conditions[i].getConditionRecord().getVariableDataValue("ZZ_STOPEXCL") : "";
				String strStopMin = conditions[i].getConditionRecord().getVariableDataValue("ZZ_STOPMIN") != null ? conditions[i].getConditionRecord().getVariableDataValue("ZZ_STOPMIN") : "0";
				String strStopMax = conditions[i].getConditionRecord().getVariableDataValue("ZZ_STOPMAX") != null ? conditions[i].getConditionRecord().getVariableDataValue("ZZ_STOPMAX") : "0";
				
				stopMin = new BigDecimal(strStopMin);
				stopMax = new BigDecimal(strStopMax);
				
				ZSTR = conditions[i];
				zstrValue = conditions[i].getConditionRate().getValue();
			}
			else if (conditionType.equals("ZSTV")) {
				ZSTV = conditions[i];
				zstvValue = conditions[i].getConditionRate().getValue();
				break;
			}
		}
		
		// If stop rules do not exist, set condition rate to 0 and return.
		if (ZSTR == null && ZSTV == null) {
			pricingCondition.setConditionRateValue(new BigDecimal(0));
			return groupKey;			
		}
		
		// If stop exclusion indicator is populated, set condition rate to 0 and return.
		if (stopExclusion.equals("X")) {
			pricingCondition.setConditionRateValue(new BigDecimal(0));
			return groupKey;
		}
		
		// If a value is populated on the ZSTR condition, set the condition rate and return.
		if (zstrValue.compareTo(new BigDecimal(0)) > 0) {
			pricingCondition.setConditionRateValue(zstrValue);
			return groupKey;
		}
		
		// Default to the value of the ZSTV record
		pricingCondition.setConditionRateValue(zstvValue);
		
		if (ZSTR != null) {
			if (zstvValue.compareTo(new BigDecimal(0)) == 0) {
				if (stopMax.compareTo(new BigDecimal(0)) > 0) {
					pricingCondition.setConditionRateValue(stopMax);
				}
				else {
					pricingCondition.setConditionRateValue(stopMin);
				}
			}
			else {
				if (zstvValue.compareTo(stopMin) < 0 && stopMin.compareTo(new BigDecimal(0)) > 0) {
					pricingCondition.setConditionRateValue(stopMin);
				}
				else if (zstvValue.compareTo(stopMax) > 0 && stopMax.compareTo(new BigDecimal(0)) > 0) {
					pricingCondition.setConditionRateValue(stopMax);
				}
			}
		}
		
		// Always group all items by returning the same group key every time
		return groupKey;
	}
}
