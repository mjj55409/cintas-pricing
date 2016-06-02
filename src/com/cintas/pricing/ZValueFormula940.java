package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula940 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {
    
    //UserexitLogger userexitLogger = new UserexitLogger(ZValueFormula940.class);
    
    if (CintasConstants.IsInsuranceCondition(pricingCondition.getConditionTypeName())) {
      String usage = pricingItem.getAttributeValue(CintasConstants.Attributes.USAGE);
      if (usage.equals(CintasConstants.Usage.LOST) ||
          usage.equals(CintasConstants.Usage.DESTROY) ||
          usage.equals(CintasConstants.Usage.CHARGES)) {
        pricingCondition.setInactive(PricingCustomizingConstants.InactiveFlag.INVISIBLE);
        return BigDecimal.ZERO;
      }
      else {
        return ZValueFormula915.roundConditionValue(
          pricingCondition.getConditionRate().getValue(), 
          pricingCondition.getConditionBase().getValue(),
          pricingCondition.getConditionRate().getUnitName(),
          pricingCondition.getPricingUnit().getValue());
      }
        
    }
    else {
      if (!pricingItem.getAttributeValue(CintasConstants.Attributes.MATERIAL_GROUP).equals(CintasConstants.MaterialGroup.TRIM)) {
        if (!CintasConstants.HasInsuranceCondition(pricingItem)) {
          return BigDecimal.ZERO;
        }
      }
    }

    return null;
  }
}