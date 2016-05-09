package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;

public class ZValueFormula914 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    if (pricingCondition.getConditionClass() == PricingCustomizingConstants.ConditionClass.DISCOUNT) {
      BigDecimal conditionValue = pricingCondition.getConditionBase().getValue().multiply(pricingCondition.getConditionRate().getValue());
      if (pricingCondition.getPricingUnit().getValue().compareTo(BigDecimal.ZERO) != 0) {
        conditionValue = conditionValue.divide(pricingCondition.getPricingUnit().getValue(),BigDecimal.ROUND_UNNECESSARY);
      }

      conditionValue = conditionValue.subtract(pricingItem.getNetValueAsBigDecimal());
      return conditionValue;
    }

    return null;
  }
}