package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZBaseFormula909 extends BaseFormulaAdapter {

  public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    if (pricingItem.getId().equals(pricingItem.getAttributeValue(CintasConstants.AncillaryMaterials.INSURANCE))) {
      if (CintasConstants.IsItemNoCharge(pricingItem)) {
        // Ancillary insurance obtains value from ZPR1 record.
        IPricingConditionUserExit ZPR1 = null;
        IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();           

        for (int i=0; i < conditions.length; i++) {
          String conditionType = conditions[i].getConditionTypeName();
          if (conditionType != null && conditionType.equals(CintasConstants.Conditions.CALCULATED_PRICE)) {
            ZPR1 = conditions[i];
            break;
          }
        }

        if (ZPR1 != null && 
            ZPR1.getInactive() == PricingCustomizingConstants.InactiveFlag.NOT_INACTIVE && 
            ZPR1.getConditionValue().getValue().compareTo(BigDecimal.ZERO) > 0) {
          return ZPR1.getConditionValue().getValue();
        } else {
          return BigDecimal.ZERO;
        }
      }
    }

    return pricingItem
        .getNetValueAsBigDecimal()
        .add(pricingItem.getCashDiscountAsBigDecimal())
        .subtract(pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_3));
  }
}
