package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZBaseFormula904 extends BaseFormulaAdapter {
	
  public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    if (CintasConstants.IsItemNoCharge(pricingItem)) {
      pricingCondition.setConditionRateValue((new BigDecimal(100)).negate());
    }
    else {
      pricingCondition.setInactive(PricingCustomizingConstants.InactiveFlag.INVISIBLE);
    }

    return null;
  }
}