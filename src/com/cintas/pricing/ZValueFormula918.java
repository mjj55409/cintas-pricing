package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.conversion.exc.ConversionMissingDataException;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula918 extends ValueFormulaAdapter {

	public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {

	  String conditionType = (pricingCondition.getConditionTypeName() != null
	      ? pricingCondition.getConditionTypeName() : CintasConstants.INITIAL);

	  if (conditionType.equals(CintasConstants.Conditions.SubTotals.SIZE_PREMIUM)) {
	    // Return the value of ZSPM
	    IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
	    for (int i=0; i<conditions.length; i++) {
	      String loopConditionType = (conditions[i].getConditionTypeName() != null
	          ? conditions[i].getConditionTypeName() : CintasConstants.INITIAL);
	      if (loopConditionType.equals(CintasConstants.Conditions.SIZE_PREMIUM)) {
	        if (conditions[i].getConditionValue().getValue().compareTo(BigDecimal.ZERO) != 0) {
	          BigDecimal conditionRate = conditions[i].getConditionValue().getValue();
	          if (conditions[i].getConditionBase().getValue().compareTo(BigDecimal.ZERO) != 0) {
	            conditionRate = conditionRate.divide(conditions[i].getConditionBase().getValue(), 3, BigDecimal.ROUND_HALF_UP);
	            try {
	              String currency = CintasConstants.GetPrecisionCurrency(pricingItem.getUserExitDocument().getDocumentCurrencyUnit().getUnitName());
	              pricingCondition.setConditionRate(conditionRate, currency);
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
	  else if (conditionType.equals(CintasConstants.Conditions.SubTotals.INSURANCE)) { 
	    // Insurance
	    BigDecimal subtotal3 = pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_3);
	    if (subtotal3 != null && subtotal3.compareTo(BigDecimal.ZERO) != 0)
	      return subtotal3;
	  }
	  else if (conditionType.equals(CintasConstants.Conditions.SubTotals.SERVICE_CHARGE)) {
	    // Service Charge
	    BigDecimal netValue = pricingItem.getNetValueAsBigDecimal();
	    if (netValue != null && netValue.compareTo(BigDecimal.ZERO) != 0)
	      return netValue;
	  }
	  else if (conditionType.equals(CintasConstants.Conditions.SubTotals.MINIMUM_CHARGE)) {
	    // Min Charge
	    BigDecimal netValue = pricingItem.getNetValueAsBigDecimal();
	    if (netValue != null && netValue.compareTo(BigDecimal.ZERO) != 0)
	      return netValue;
	  }
	  else if (conditionType.equals(CintasConstants.Conditions.SubTotals.AMOUNT_SERV_CHARGE)) {
	    BigDecimal subtotal1 = pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_1);
	    if (subtotal1 != null && subtotal1.compareTo(BigDecimal.ZERO) != 0) {
	      return subtotal1;
	    }
	  }
	  else if (conditionType.equals(CintasConstants.Conditions.SubTotals.AMOUNT_MIN_CHARGE)) {
	    BigDecimal subtotal5 = pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_5);
	    if (subtotal5 != null && subtotal5.compareTo(BigDecimal.ZERO) != 0) {
	      return subtotal5;
	    }			
	  }
	  //CR912
	  else if (conditionType.equals(CintasConstants.Conditions.SubTotals.DISCOUNTED_PRICE)) {
	    return pricingItem.getNetValueAsBigDecimal().subtract(pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_3));
	  }
	  //CR912

	  return null;
	}
}