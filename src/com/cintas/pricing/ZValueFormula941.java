package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula941 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {
    
    UserexitLogger userexitLogger = new UserexitLogger(ZValueFormula941.class);
    
    BigDecimal xkwert = ZValueFormula915.roundConditionValue(
        pricingCondition.getConditionRate().getValue(), 
        pricingCondition.getConditionBase().getValue(),
        pricingCondition.getConditionRate().getUnitName(),
        pricingCondition.getPricingUnit().getValue());
    //BigDecimal xkwert = pricingCondition.getConditionValue().getValue();
    
    userexitLogger.writeLogDebug("xkwert == " + xkwert);
    
    BigDecimal xkbetr = pricingCondition.getConditionRate().getValue();
    BigDecimal lv_kwert = BigDecimal.ZERO;
    
    userexitLogger.writeLogDebug("xkbetr = " + xkbetr);
    
    if (xkbetr.compareTo(BigDecimal.ZERO) != 0) {
      xkbetr = xkbetr.add(CintasConstants.GetConditionRate(pricingItem, CintasConstants.Conditions.BASE_PRICE));
      userexitLogger.writeLogDebug("SizePremium + BasePrice = " + xkbetr);
      lv_kwert = xkbetr.multiply(pricingCondition.getConditionBase().getValue());
      userexitLogger.writeLogDebug("SizePremium + BasePrice (extended) = " + lv_kwert);
      
      BigDecimal netValue = pricingItem.getNetValueAsBigDecimal();
      
      if (netValue.compareTo(BigDecimal.ZERO) != 0) {
        BigDecimal lv_diff = lv_kwert.subtract(netValue.add(xkwert));
        userexitLogger.writeLogDebug("diff = " + lv_diff);
        xkwert = xkwert.add(lv_diff);
        userexitLogger.writeLogDebug("Return value = " + xkwert);
        return xkwert;
      }
    }
    
    return null;
  }
}