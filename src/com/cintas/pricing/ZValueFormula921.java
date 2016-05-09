package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.conversion.exc.ConversionMissingDataException;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula921 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    BigDecimal subtotalH = pricingItem.getSubtotal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_H).getValue();

    // Set condition rate if base value is present
    if (pricingCondition.getConditionBase().getValue().compareTo(BigDecimal.ZERO) > 0) {
      try {
        pricingCondition.setConditionRate(
            subtotalH.divide(pricingCondition.getConditionBase().getValue(), 2, BigDecimal.ROUND_HALF_UP), 
            CintasConstants.GetPrecisionCurrency(pricingItem.getUserExitDocument().getDocumentCurrencyUnit().getUnitName()));
      }
      catch (ConversionMissingDataException ex) {
        pricingCondition.setConditionRateValue(subtotalH.divide(pricingCondition.getConditionBase().getValue(), 2, BigDecimal.ROUND_HALF_UP));
      }
    }

    return subtotalH;
  }
}