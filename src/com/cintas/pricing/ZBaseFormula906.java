/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.pricing.customizing.IPricingStep;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZBaseFormula906 extends BaseFormulaAdapter {

	public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {
	  
	  UserexitLogger userexitLogger = new UserexitLogger(ZBaseFormula906.class);
		
		String conditionType = pricingCondition.getConditionTypeName() != null
				? pricingCondition.getConditionTypeName() : "";
				
		userexitLogger.writeLogDebug("Condition = " + conditionType);
		
		IPricingConditionUserExit ruleCondition = null;		
		String ruleConditionType = null;
		int ruleStepNumber = 0;
				
		if (conditionType.equals("ZIDC")) {
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
		}
		else if (conditionType.equals("ZSVC") || conditionType.equals("ZSV1")) {
			ruleConditionType = "ZRSV";
		}
		else if (conditionType.equals("ZDIS")) {
			ruleConditionType = "ZRDI";
		}
		else {
			/* Relies on looking at XKOMV-STUNB (lower limit of procedure step numbers for condition base)
			 * for generic processing.
			 */
			IPricingStep step = (IPricingStep)pricingCondition.getStep();
			if (step != null) {
				ruleStepNumber = step.getFromStep();
			}
		}
		
		// Find rule condition record based on criteria outlined above.
		if (ruleConditionType != null || ruleStepNumber > 0) {
			IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
			for (int i=0; i<conditions.length; i++) {
				if (conditions[i].getConditionTypeName() != null 
						&& ruleConditionType != null
						&& conditions[i].getConditionTypeName().equals(ruleConditionType)) {
					ruleCondition = conditions[i];
					break;
				}
				
				if (ruleStepNumber > 0 && conditions[i].getStepNumber() == ruleStepNumber) {
					ruleCondition = conditions[i];
					break;
				}
			}
		}
		
		/* Proceed with rule determination if something was found that matches the calculation
		 *  type of the current condition.
		 */
		if (ruleCondition != null && ruleCondition.getCalculationType() == pricingCondition.getCalculationType()) {
			String stopExclusion = ruleCondition.getConditionRecord().getVariableDataValue("ZZ_STOPEXCL") != null ?
					ruleCondition.getConditionRecord().getVariableDataValue("ZZ_STOPEXCL") : "";
			BigDecimal priceMin = ruleCondition.getConditionRecord().getVariableKeyValue("ZZ_PRICMIN") != null ?
					new BigDecimal(ruleCondition.getConditionRecord().getVariableKeyValue("ZZ_PRICMIN")) : new BigDecimal("0");
			BigDecimal priceMax = ruleCondition.getConditionRecord().getVariableKeyValue("ZZ_PRICMAX") != null ?
					new BigDecimal(ruleCondition.getConditionRecord().getVariableKeyValue("ZZ_PRICMAX")) : new BigDecimal("0");	
			
			// Improper storage of price min/price max fields when the record currency is USD
			if (ruleCondition.getCalculationType() == 'B' && 
					(ruleCondition.getConditionRate().getUnitName().equals("USD") || ruleCondition.getConditionRate().getUnitName().equals("CAD"))) {
				priceMin = priceMin.multiply(new BigDecimal("10"));
				priceMax = priceMax.multiply(new BigDecimal("10"));
			}
					
			if (stopExclusion.equals("X")) {
				pricingCondition.setConditionRateValue(new BigDecimal("0"));
			} else {
			  // begin CR801
			  userexitLogger.writeLogDebug("Rule condition = " + ruleCondition.getConditionTypeName());
			  BigDecimal ruleRate = ruleCondition.getConditionRate().getValue();
			  if (ruleRate.compareTo(BigDecimal.ZERO) > 0) {
			    pricingCondition.setConditionRateValue(ruleRate);
			  } else { //CR801
  			  userexitLogger.writeLogDebug("ZZ_PRICMIN = " + priceMin);
  			  userexitLogger.writeLogDebug("ZZ_PRCMAX = " + priceMax);
  				BigDecimal currentRateAbs = pricingCondition.getConditionRate().getValue().abs();
  				if (priceMin.compareTo(new BigDecimal("0")) != 0 && currentRateAbs.compareTo(priceMin) < 0) {
  					if (pricingCondition.getConditionRate().getValue().compareTo(new BigDecimal("0")) < 0)
  						pricingCondition.setConditionRateValue(priceMin.multiply(new BigDecimal("-1")));
  					else
  						pricingCondition.setConditionRateValue(priceMin);
  				}
  				else if (priceMax.compareTo(new BigDecimal("0")) != 0 && currentRateAbs.compareTo(priceMax) > 0) {
  					if (pricingCondition.getConditionRate().getValue().compareTo(new BigDecimal("0")) < 0)
  						pricingCondition.setConditionRateValue(priceMax.multiply(new BigDecimal("-1")));
  					else
  						pricingCondition.setConditionRateValue(priceMax);
  				}
			  } // end CR801
			}
		}
		
		// begin CR638
		BigDecimal qtyInventory;
		if (pricingItem.getAttributeValue("ZZ_INVQTY") == null) {
		  userexitLogger.writeLogDebug("ZZ_INVQTY is null.");
		  qtyInventory = BigDecimal.ZERO;
		} else {
		  qtyInventory = new BigDecimal(pricingItem.getAttributeValue("ZZ_INVQTY"));
		}
		userexitLogger.writeLogDebug("ZZ_INVQTY = " + qtyInventory);
		if (qtyInventory.compareTo(BigDecimal.ZERO) > 0 &&
		    pricingCondition.getCalculationType() == PricingCustomizingConstants.CalculationType.QUANTITY_DEP &&
		    pricingCondition.getAccountKey1().equals("ZIN") ) {
		  userexitLogger.writeLogDebug("Using inventory quantity for calculation.");
		  return qtyInventory;
	  }

		// end CR638
		
		return null;
	}
}