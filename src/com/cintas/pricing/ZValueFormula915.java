package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula915 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    return roundConditionValue(
        pricingCondition.getConditionRate().getValue(), 
        pricingCondition.getConditionBase().getValue(),
        pricingCondition.getConditionRate().getUnitName(),
        pricingCondition.getPricingUnit().getValue());
  }

  static public BigDecimal roundConditionValue(BigDecimal conditionValue, BigDecimal conditionBase, String currency, BigDecimal quantity)
  {
    // conditionValue == kbetr
    // conditionBase == kmein

    if (CintasConstants.IsPrecisionCurrency(currency)) {
      if (quantity.compareTo(BigDecimal.ZERO) > 0) {
        conditionValue = conditionValue.multiply(conditionBase).divide(quantity, BigDecimal.ROUND_UNNECESSARY);
      }
    }

    return conditionValue;
  }
}