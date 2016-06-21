package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula921 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {
    
    UserexitLogger userexitLogger = new UserexitLogger(ZValueFormula921.class);

    BigDecimal subtotalH = pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_H);
    BigDecimal subtotal1 = pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_1);
    BigDecimal ziprValue = CintasConstants.GetConditionValue(pricingItem, CintasConstants.Conditions.INITIAL_PRICE);
    BigDecimal zbokValue = CintasConstants.GetConditionValue(pricingItem, CintasConstants.Conditions.BOOK_PRICE);
    BigDecimal zdssValue = CintasConstants.GetConditionValue(pricingItem, CintasConstants.Conditions.DIRECT_SALE_SURCHG);
    BigDecimal _kbetr = BigDecimal.ZERO;
    
    userexitLogger.writeLogDebug("Sub1 = " + subtotal1);
    userexitLogger.writeLogDebug("SubH = " + subtotalH);
    userexitLogger.writeLogDebug("ZIPR = " + ziprValue);
    userexitLogger.writeLogDebug("ZBOK = " + zbokValue);
    userexitLogger.writeLogDebug("ZDSS = " + zdssValue);
    
    if (subtotal1.compareTo(BigDecimal.ZERO) > 0) {
      _kbetr = subtotal1;
    }
    else if (ziprValue.compareTo(BigDecimal.ZERO) > 0) {
      _kbetr = CintasConstants.GetConditionRate(pricingItem, CintasConstants.Conditions.INITIAL_PRICE);
    }
    else if (zbokValue.compareTo(BigDecimal.ZERO) > 0) {
      if (zdssValue.compareTo(BigDecimal.ZERO) == 0) {
        _kbetr = CintasConstants.GetConditionRate(pricingItem, CintasConstants.Conditions.BOOK_PRICE);
      }
      else {
        if (pricingCondition.getConditionBase() != null &&
            pricingCondition.getConditionBase().getValue().compareTo(BigDecimal.ZERO) != 0) {
          _kbetr = subtotalH.divide(
              pricingCondition.getConditionBase().getValue(), 
              CintasConstants.Currency.PRECISION_SCALE, 
              BigDecimal.ROUND_HALF_UP);
        }
        else {
          _kbetr = BigDecimal.ZERO;
        }
      }
    }
    
    try {
      pricingCondition.setConditionRate(_kbetr, CintasConstants.GetPrecisionCurrency(pricingItem));
      userexitLogger.writeLogDebug("Setting condition rate = " +_kbetr + " " + CintasConstants.GetPrecisionCurrency(pricingItem));
    }
    catch (Exception ex) {
      pricingCondition.setConditionRateValue(_kbetr);
      userexitLogger.writeLogDebug("Setting condition rate = " +_kbetr + " ERR");
    }

    userexitLogger.writeLogDebug("Setting condition value = " + subtotalH);
    return subtotalH;
  }
}