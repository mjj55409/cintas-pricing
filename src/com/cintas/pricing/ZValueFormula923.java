package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula923 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    BigDecimal subtotalF = pricingItem.getSubtotal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_F).getValue();
    BigDecimal factor = (pricingCondition.getFactor() != null ? pricingCondition.getFactor() : new BigDecimal(0));
    BigDecimal tempValue = null;

    IPricingConditionUserExit ZSTA = null;

    // Get ZSTA condition from the conditions table
    IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
    for (int i=0; i<conditions.length; i++) {
      String conditionType = conditions[i].getConditionTypeName() != null ? conditions[i].getConditionTypeName() : CintasConstants.INITIAL;
      if (conditionType.equals(CintasConstants.Conditions.ADJ_MINIMUM)) {
        ZSTA = conditions[i];
      }
    }

    if (ZSTA != null) {
      if (factor.compareTo(BigDecimal.ZERO) == 0) {
        tempValue = ZSTA.getConditionValue().getValue();
      }
      else {
        tempValue = ZSTA.getConditionValue().getValue().multiply(factor);
      }
    }
    else {
      if (factor.compareTo(BigDecimal.ZERO) == 0) {
        tempValue = subtotalF;
      }
      else {
        tempValue = subtotalF.multiply(factor);
      }
    }

    if (tempValue.compareTo(pricingItem.getNetValueAsBigDecimal()) > 0) {
      //pricingItem.setCalculationDuringPricingCompleteRequired(true);
      return tempValue.subtract(pricingItem.getNetValueAsBigDecimal());
    }
    else {
      return BigDecimal.ZERO;
    }
  }
}
