package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula933 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    BigDecimal conditionRate = pricingCondition.getConditionRate().getValue();
    if (conditionRate.compareTo(BigDecimal.ZERO) == 0 || conditionRate.compareTo(CintasConstants.ONE_PENNY) == 0) {
      pricingCondition.setConditionRateValue(CintasConstants.ONE_PENNY);
      return BigDecimal.ZERO;
    }

    return null;
  }
}