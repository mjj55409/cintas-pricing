package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZBaseFormula911 extends BaseFormulaAdapter {

  public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {
    
    if (CintasConstants.IsItemNoCharge(pricingItem))
      pricingCondition.setStatistical(true);

    return (pricingCondition.getConditionTypeName().equals("ZPR1") ? BigDecimal.ONE : null);
  }
}
