package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.conversion.exc.ConversionMissingDataException;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula926 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    if (CintasConstants.IsItemNoCharge(pricingItem))
      return BigDecimal.ZERO;
    
    BigDecimal subtotal1 = pricingItem.getSubtotal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_1).getValue();
    if (subtotal1.compareTo(BigDecimal.ZERO) > 0) {
      if (pricingCondition.getConditionBase().getValue().compareTo(BigDecimal.ZERO) != 0) {
        try {
          pricingCondition.setConditionRate(
              subtotal1.divide(pricingCondition.getConditionBase().getValue(), 3, BigDecimal.ROUND_HALF_UP), 
              CintasConstants.GetPrecisionCurrency(pricingItem.getUserExitDocument().getDocumentCurrencyUnit().getUnitName()));
        }
        catch (ConversionMissingDataException ex) {
          pricingCondition.setConditionRateValue(subtotal1.divide(pricingCondition.getConditionBase().getValue(),3,BigDecimal.ROUND_HALF_UP));
        }
      }			
      return subtotal1;
    }
    else {
      return null;
    }
  }
}