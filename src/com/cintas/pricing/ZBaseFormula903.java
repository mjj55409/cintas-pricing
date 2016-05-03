/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZBaseFormula903 extends BaseFormulaAdapter {

	public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {
		
		// Can't set item as statistical, as the original routine does.  But is it necessary anyway?
		
		String currentConditionType = pricingCondition.getConditionTypeName();
		
		if (currentConditionType != null && currentConditionType.equals("ZNCR")) {
			String materialExclusionRule = pricingCondition.getConditionRecord().getVariableDataValue("ZZ_MEXCL_R") != null ? pricingCondition.getConditionRecord().getVariableDataValue("ZZ_MEXCL_R") : "";
			String quantityErrorRule = pricingCondition.getConditionRecord().getVariableDataValue("ZZ_QTYERR_R") != null ? pricingCondition.getConditionRecord().getVariableDataValue("ZZ_QTYERR_R") : "";
			String materialExcluded = "";
			BigDecimal minQuantity = new BigDecimal(0);
			BigDecimal maxQuantity = new BigDecimal(0);
			String noCharge = "";
			
			IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
			for (int i=0; i<conditions.length; i++) {
				String conditionType = conditions[i].getConditionTypeName() != null ? conditions[i].getConditionTypeName() : "";
				if (conditionType.equals("ZMLE")) {
					materialExcluded = conditions[i].getConditionRecord().getVariableDataValue("ZZ_MEXCL") != null ? conditions[i].getConditionRecord().getVariableDataValue("ZZ_MEXCL") : "";
				}
				else if (conditionType.equals("ZQTR")) {
					String strMinQty = conditions[i].getConditionRecord().getVariableDataValue("ZZ_MINQTY") != null ? conditions[i].getConditionRecord().getVariableDataValue("ZZ_MINQTY") : "0";
					String strMaxQty = conditions[i].getConditionRecord().getVariableDataValue("ZZ_MAXQTY") != null ? conditions[i].getConditionRecord().getVariableDataValue("ZZ_MAXQTY") : "0";
					
					minQuantity = new BigDecimal(strMinQty);
					maxQuantity = new BigDecimal(strMaxQty);
					break;
				}
			}
			
			// Apply material exclusion errors
			if (materialExcluded.equals("X")) {
				if (materialExclusionRule.equals("1")) {
					// Set no-charge flag to be used in other pricing formulas
					noCharge = "X";
					pricingItem.setObjectForUserExits("NO CHARGE",noCharge);
				}
			}
			
			// Apply quantity exclusion errors
			if (!noCharge.equals("X") && !quantityErrorRule.equals("")) {
				BigDecimal orderedQuantity = pricingItem.getBaseQuantity().getValue();
				if ((minQuantity.compareTo(new BigDecimal(0)) > 0 && orderedQuantity.compareTo(minQuantity) < 0) 
						|| (maxQuantity.compareTo(new BigDecimal(0)) > 0 && orderedQuantity.compareTo(maxQuantity) > 0)) {
					if (quantityErrorRule.equals("1")) {
						noCharge = "X";
						pricingItem.setObjectForUserExits("NO CHARGE",noCharge);
					}
					else if (quantityErrorRule.equals("2")) {
						/*Original routine changed the pricing quantity (komp-mgame) here
						 This is now done instead in ABAP function module Z_FM_CRM_RESTRICTIONS
						 and BADI implementation ZBA_CRM_TECH_PRICING.
						*/
					}
				}
			}
		}
		
		return null;
	}
}
