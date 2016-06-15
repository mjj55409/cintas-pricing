package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula940 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {
    
    UserexitLogger userexitLogger = new UserexitLogger(ZValueFormula940.class);
    
    if (pricingCondition.getConditionTypeName().equals("ZIPI")) {
      if (pricingItem.getAttributeValue(CintasConstants.Attributes.INSURANCE_PROGRAM).equals("30")) { //Auto-LR
        userexitLogger.writeLogDebug("ZIPI with auto-lr (30)");
        pricingCondition.setStatistical(true);
        return BigDecimal.ZERO;
      }
    }
 
    if (CintasConstants.IsInsuranceCondition(pricingCondition.getConditionTypeName())) {
      userexitLogger.writeLogDebug("Is an insurance condition.");
      String usage = pricingItem.getAttributeValue(CintasConstants.Attributes.USAGE);
      if (usage.equals(CintasConstants.Usage.LOST) ||
          usage.equals(CintasConstants.Usage.DESTROY) ||
          usage.equals(CintasConstants.Usage.CHARGES)) {
        userexitLogger.writeLogDebug("Usage in ('LRX')");
        pricingCondition.setInactive(PricingCustomizingConstants.InactiveFlag.INVISIBLE);
        return BigDecimal.ZERO;
      }
      else {
        userexitLogger.writeLogDebug("Usage not in ('LRX')");
        if (pricingCondition.getCalculationType() == PricingCustomizingConstants.CalculationType.PERCENTAGE)
          return null;

        return ZValueFormula915.roundConditionValue(
          pricingCondition.getConditionRate().getValue(), 
          pricingCondition.getConditionBase().getValue(),
          pricingCondition.getConditionRate().getUnitName(),
          pricingCondition.getPricingUnit().getValue());
      }
        
    }
    else {
      userexitLogger.writeLogDebug("Is not an insurance condition.");
      if (!pricingItem.getAttributeValue(CintasConstants.Attributes.MATERIAL_GROUP).equals(CintasConstants.MaterialGroup.TRIM)) {
        if (!CintasConstants.HasInsuranceCondition(pricingItem)) {
          return BigDecimal.ZERO;
        }
      }
    }

    userexitLogger.writeLogDebug("Why am I here?");
    return null;
  }
}