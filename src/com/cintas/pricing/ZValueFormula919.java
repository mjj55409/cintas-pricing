package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula919 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    BigDecimal netValue = pricingItem.getNetValueAsBigDecimal();

    // Set condition rate
    if (pricingCondition.getConditionBase().getValue().compareTo(new BigDecimal(0)) != 0) {
      pricingCondition.setConditionRateValue(netValue.divide(pricingCondition.getConditionBase().getValue(), 2, BigDecimal.ROUND_HALF_UP));
    }

    // Set condition value to the net value
    return netValue;
  }
}
