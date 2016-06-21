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
    
    BigDecimal xkwert = BigDecimal.ZERO;
    
    BigDecimal zbok = CintasConstants.GetConditionRate(pricingItem, "ZBOK");
    if (zbok.compareTo(CintasConstants.ONE_PENNY) == 0)
      zbok = BigDecimal.ZERO;
    
    BigDecimal zipr = CintasConstants.GetConditionRate(pricingItem, "ZIPR");
    if (zipr.compareTo(CintasConstants.ONE_PENNY) == 0)
      zipr = BigDecimal.ZERO;
    
    BigDecimal subtotal5 = pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_5);
    userexitLogger.writeLogDebug("subtotal5 = " + subtotal5);
    if (subtotal5.compareTo(BigDecimal.ZERO) != 0) {
      BigDecimal subtotal3 = pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_3);
      userexitLogger.writeLogDebug("subtotal3 = " + subtotal3);
      xkwert  = subtotal5.subtract(subtotal3);
    } else {
      xkwert = BigDecimal.ZERO;
    }
    
    if (CintasConstants.IsItemNoCharge(pricingItem)) {
      userexitLogger.writeLogDebug("Item is no charge.");
      //pricingCondition.setConditionRateValue(BigDecimal.ZERO);
      xkwert = BigDecimal.ZERO;
      //return BigDecimal.ZERO;
    }
    
    BigDecimal xkbetr = BigDecimal.ZERO;
    
    if (CintasConstants.GetConditionRate(pricingItem, "ZMAN").compareTo(BigDecimal.ZERO) != 0) {
      xkbetr = CintasConstants.GetConditionRate(pricingItem, "ZMAN");
    }
    else if (CintasConstants.GetSubtotalL(pricingItem).compareTo(BigDecimal.ZERO) != 0) {
      BigDecimal zpra = CintasConstants.GetConditionRate(pricingItem, "ZPRA");
      if (zpra.compareTo(BigDecimal.ZERO) == 0) {
        xkbetr = CintasConstants.GetSubtotalL(pricingItem).add(CintasConstants.GetConditionRate(pricingItem, "ZIPR"));
      }
      else {
        xkbetr = CintasConstants.GetSubtotalL(pricingItem);
      }
    }
    else if (zipr.compareTo(BigDecimal.ZERO) != 0) {
        xkbetr = zipr;
    }
    else if (zbok.compareTo(BigDecimal.ZERO) != 0) {
      BigDecimal zdss = CintasConstants.GetConditionValue(pricingItem, "ZDSS");
      if (zdss.compareTo(BigDecimal.ZERO) == 0) {
        xkbetr = zbok;
      }
      else {
        if (pricingCondition.getConditionBase().getValue().compareTo(BigDecimal.ZERO) != 0)
          xkbetr = xkwert.divide(pricingCondition.getConditionBase().getValue());
        else
          xkbetr = BigDecimal.ZERO;
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