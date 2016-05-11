package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula932 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    BigDecimal surchargeRate = BigDecimal.ZERO;
    BigDecimal surchargeValue = BigDecimal.ZERO;

    IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
    for (int i=0; i<conditions.length; i++) {
      String conditionType = (conditions[i].getConditionTypeName() != null 
          ? conditions[i].getConditionTypeName() : CintasConstants.INITIAL);

      if (conditionType.equals(CintasConstants.Conditions.SPECIAL_HANDLING)) {
        surchargeRate = surchargeRate.add(conditions[i].getConditionRate().getValue());
        surchargeValue = surchargeValue.add(conditions[i].getConditionValue().getValue());
      }
    }

    pricingCondition.setConditionRateValue(surchargeRate);

    String stopExclusion = (pricingCondition.getConditionRecord().getVariableDataValue(CintasConstants.Attributes.STOP_EXCLUSION) != null ?
        pricingCondition.getConditionRecord().getVariableDataValue(CintasConstants.Attributes.STOP_EXCLUSION) : CintasConstants.INITIAL);
    BigDecimal priceMin = (pricingCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.PRICE_MIN) != null ?
        new BigDecimal(pricingCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.PRICE_MIN)) : BigDecimal.ZERO);
    BigDecimal priceMax = (pricingCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.PRICE_MAX) != null ?
        new BigDecimal(pricingCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.PRICE_MAX)) : BigDecimal.ZERO);	

    char minMaxIndicator = ' ';

    if (stopExclusion.equals(CintasConstants.ABAP_TRUE)) {
      pricingCondition.setConditionRateValue(BigDecimal.ZERO);
      minMaxIndicator = CintasConstants.PRICE_EXCLUDED;
    }
    else {
      if (priceMin.compareTo(BigDecimal.ZERO) > 0 && surchargeRate.compareTo(priceMin) < 0) {
        pricingCondition.setConditionRateValue(priceMin);
        minMaxIndicator = CintasConstants.PRICE_MINIMUM;
      }
      else if (priceMax.compareTo(BigDecimal.ZERO) > 0 && surchargeRate.compareTo(priceMax) > 0) {
        pricingCondition.setConditionRateValue(priceMax);
        minMaxIndicator = CintasConstants.PRICE_MAXIMUM;
      }
    }

    if (pricingCondition.getConditionClass() == PricingCustomizingConstants.ConditionClass.DISCOUNT) { // Discount or Surcharge
      // Perform standard SAP calculation (rate x base / unit)
      BigDecimal conditionValue = pricingCondition.getConditionRate().getValue().multiply(pricingCondition.getConditionBase().getValue());
      BigDecimal pricingUnit = pricingCondition.getPricingUnit().getValue();
      if (pricingUnit.compareTo(BigDecimal.ZERO) > 0) {
        conditionValue = conditionValue.divide(pricingUnit,BigDecimal.ROUND_UNNECESSARY);
      }

      switch (minMaxIndicator) {
      case CintasConstants.PRICE_MAXIMUM:
        if (conditionValue.compareTo(surchargeValue) < 0) {
          /* Surcharge value is greater than price max.
           * Value is the difference between the two.
           */
          conditionValue = conditionValue.subtract(surchargeValue);
        }
        else
          conditionValue = BigDecimal.ZERO;
        break;
      case CintasConstants.PRICE_MINIMUM:
        if (conditionValue.compareTo(surchargeValue) > 0) {
          /* Surcharge value is less than price min.
           * Value is the difference between the two.
           */
          conditionValue = conditionValue.subtract(surchargeValue);
        }
        else
          conditionValue = BigDecimal.ZERO;
        break;
      case CintasConstants.PRICE_EXCLUDED:
        /* Price is excluded.
         * Value is the negative surcharge value, so that it gets
         * completely canceled out.
         */
        conditionValue = surchargeValue.negate();
        break;
      default:
        conditionValue = BigDecimal.ZERO;
        break;
      }

      if (pricingCondition.getCalculationType() == PricingCustomizingConstants.CalculationType.QUANTITY_DEP) {
        BigDecimal conditionBase = pricingCondition.getConditionBase().getValue();
        if (conditionBase.compareTo(BigDecimal.ZERO) != 0) {
          pricingCondition.setConditionRateValue(conditionValue.divide(conditionBase,BigDecimal.ROUND_UNNECESSARY));
        }
      }

      return conditionValue;
    }

    // Default to zero if it wasn't set to anything else.
    return BigDecimal.ZERO;	
  }
}