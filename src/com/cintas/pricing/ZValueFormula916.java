package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula916 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    IPricingConditionUserExit ZIPR = null;
    IPricingConditionUserExit ZPRR = null;

    BigDecimal ZIPRRate = new BigDecimal("0");
    BigDecimal priceMin = new BigDecimal("0");
    BigDecimal priceMax = new BigDecimal("0");

    char minmaxIndicator = ' ';

    // Get condition types ZIPR and ZPRR from the conditions table
    IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
    for (int i=0; i<conditions.length; i++) {
      String conditionType = conditions[i].getConditionTypeName();
      if (conditionType != null && conditionType.equals(CintasConstants.Conditions.INITIAL_PRICE)) {
        ZIPR = conditions[i];
        ZIPRRate = ZIPR.getConditionRate().getValue();
        if (ZIPRRate.compareTo(CintasConstants.ONE_PENNY) == 0)
          ZIPRRate = BigDecimal.ZERO;
      }
      if (conditionType != null && conditionType.equals(CintasConstants.Conditions.Rules.AGREEMENT_PRICE)) {
        ZPRR = conditions[i];
      }
    }		

    // Set ZPRA to 0 if neither ZIPR nor ZPRR was found
    if (ZIPR == null && ZPRR == null)
      return BigDecimal.ZERO;

    if (ZPRR != null) { 
      // Get min and max from the ZPRR record
      priceMin = new BigDecimal(ZPRR.getConditionRecord().getVariableDataValue(CintasConstants.Attributes.PRICE_MIN));
      priceMax = new BigDecimal(ZPRR.getConditionRecord().getVariableDataValue(CintasConstants.Attributes.PRICE_MAX));

      // Determine Condition Rate
      //if (priceMin.compareTo(new BigDecimal("0")) != 0 && ZIPRRate.compareTo(priceMin) < 0) {
      if (ZIPRRate.compareTo(priceMin) < 0) { //CR789 - zero is an allowed value
        // Minimum is maintained and ZIPR is less than it - use the minimum
        pricingCondition.setConditionRateValue(priceMin);
        minmaxIndicator = CintasConstants.PRICE_MINIMUM;
      }
      //else if (priceMax.compareTo(new BigDecimal("0")) != 0 && ZIPRRate.compareTo(priceMax) > 0) {
      else if (ZIPRRate.compareTo(priceMax) > 0) { //CR789 - zero is an allowed value			  
        // Maximum is maintained and ZIPR is greater than it - use the maximum
        pricingCondition.setConditionRateValue(priceMax);
        minmaxIndicator = CintasConstants.PRICE_MAXIMUM;
      }
      else {
        // Use the ZIPR value
        pricingCondition.setConditionRateValue(ZIPRRate);
      }
    }

    // Determine Condition Value
    if (pricingCondition.getConditionClass() == PricingCustomizingConstants.ConditionClass.DISCOUNT && ZIPR != null) {		
      BigDecimal ZIPRValue = ZIPR.getConditionValue().getValue();
      if (ZIPRValue.compareTo(CintasConstants.ONE_PENNY) == 0)
        ZIPRValue = BigDecimal.ZERO;
      
      BigDecimal conditionValue = pricingCondition.getConditionRate().getValue().multiply(pricingCondition.getConditionBase().getValue());

      switch (minmaxIndicator) {
      case CintasConstants.PRICE_MINIMUM:
        if (pricingCondition.getConditionValue().getValue().compareTo(ZIPRValue) > 0) {
          return conditionValue.subtract(ZIPRValue);
        }
        break;

      case CintasConstants.PRICE_MAXIMUM:
        if (pricingCondition.getConditionValue().getValue().compareTo(ZIPRValue) < 0) {
          return conditionValue.subtract(ZIPRValue);
        }
        break;

      default:
        break;
      }
    }

    return BigDecimal.ZERO;
  }
}
