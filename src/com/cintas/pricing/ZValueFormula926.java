package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.conversion.exc.ConversionMissingDataException;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula926 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    UserexitLogger userexitLogger = new UserexitLogger(ZValueFormula926.class);
    
    if (CintasConstants.IsItemNoCharge(pricingItem)) {
      userexitLogger.writeLogDebug("Item is no charge.");
      pricingCondition.setConditionRateValue(BigDecimal.ZERO);
      return BigDecimal.ZERO;
    }
    
    BigDecimal xkwert = BigDecimal.ZERO;
    
    BigDecimal subtotal5 = pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_5);
    userexitLogger.writeLogDebug("subtotal5 = " + subtotal5);
    if (subtotal5.compareTo(BigDecimal.ZERO) != 0) {
      BigDecimal subtotal3 = pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_3);
      userexitLogger.writeLogDebug("subtotal3 = " + subtotal3);
      xkwert  = subtotal5.subtract(subtotal3);
    } else {
      //xkwert = BigDecimal.ZERO;
    }
    
    BigDecimal xkbetr = BigDecimal.ZERO;
    
    if (xkwert.compareTo(BigDecimal.ZERO) == 0) {
      //xkbetr = BigDecimal.ZERO;
    } else {
      if (CintasConstants.GetConditionRate(pricingItem, "ZMAN").compareTo(BigDecimal.ZERO) != 0) {
        xkbetr = CintasConstants.GetConditionRate(pricingItem, "ZMAN");
      }
      else if (CintasConstants.GetSubtotalL(pricingItem).compareTo(BigDecimal.ZERO) != 0) {
        if (CintasConstants.FindCondition(pricingItem, "ZPRA") == null) {
          xkbetr = CintasConstants.GetSubtotalL(pricingItem).add(CintasConstants.GetConditionRate(pricingItem, "ZIPR"));
        }
        else {
          xkbetr = CintasConstants.GetSubtotalL(pricingItem);
        }
      }
      else if (CintasConstants.GetConditionRate(pricingItem, "ZIPR").compareTo(BigDecimal.ZERO) != 0) {
          xkbetr = CintasConstants.GetConditionRate(pricingItem, "ZIPR");
      }
      else if (CintasConstants.GetConditionRate(pricingItem, "ZBOK").compareTo(BigDecimal.ZERO) != 0) {
        if (CintasConstants.FindCondition(pricingItem, "ZDSS") == null) {
          xkbetr = CintasConstants.GetConditionRate(pricingItem, "ZBOK");
        }
        else {
          if (pricingCondition.getConditionBase().getValue().compareTo(BigDecimal.ZERO) != 0)
            xkbetr = xkwert.divide(pricingCondition.getConditionBase().getValue());
          else
            xkbetr = BigDecimal.ZERO;
        }
      }
    }
    
    userexitLogger.writeLogDebug("xkbetr = " + xkbetr);
    userexitLogger.writeLogDebug("xkwert = " + xkwert);
    try {
      pricingCondition.setConditionRate(xkbetr, CintasConstants.GetPrecisionCurrency(pricingItem));
    }
    catch (ConversionMissingDataException ex) {
      pricingCondition.setConditionRateValue(xkbetr);
    }
    return xkwert;
  }
}