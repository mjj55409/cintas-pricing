package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;

public class ZBaseFormula901 extends BaseFormulaAdapter {

  public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    UserexitLogger userexitLogger = new UserexitLogger(ZBaseFormula901.class);

    // New logic for min/max rules in Auto-LR (CR419).
    BigDecimal priceMin = new BigDecimal("0");
    BigDecimal priceMax = new BigDecimal("0");

    IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
    for (int i=0; i<conditions.length; i++) {
      String conditionType = conditions[i].getConditionTypeName();
      if (conditionType != null && conditionType.equals(CintasConstants.Conditions.Rules.INSURANCE_PCT_INV)) {
        priceMin = (conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.PRICE_MIN) != null
            ? new BigDecimal(conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.PRICE_MIN)) : priceMin);
        priceMax = (conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.PRICE_MAX) != null
            ? new BigDecimal(conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.PRICE_MAX)) : priceMax);
        break;
      }
    }

    BigDecimal baseValue = pricingCondition.getConditionBase().getValue();

    userexitLogger.writeLogDebug("ZZ_PRICMIN = " + priceMin);		
    userexitLogger.writeLogDebug("ZZ_PRICMAX = " + priceMax);
    userexitLogger.writeLogDebug("Initial condition base = " + baseValue);

    // begin CR638
    BigDecimal qtyInventory = new BigDecimal(pricingItem.getAttributeValue(CintasConstants.Attributes.INVENTORY_QTY));
    userexitLogger.writeLogDebug("ZZ_INVQTY = " + qtyInventory);
    if (qtyInventory.compareTo(BigDecimal.ZERO) > 0 &&
        pricingCondition.getCalculationType() == PricingCustomizingConstants.CalculationType.QUANTITY_DEP &&
        pricingCondition.getAccountKey1().equals(CintasConstants.AccountKey.INSURANCE) ) {
      baseValue = qtyInventory;
      userexitLogger.writeLogDebug("Using inventory quantity for calculation.");
    }
    // end CR638

    BigDecimal percentInventory = new BigDecimal(pricingCondition.getConditionRecord().getVariableDataValue(CintasConstants.Attributes.INVENTORY_PCT));

    if (percentInventory.compareTo(BigDecimal.ZERO) > 0) {

      userexitLogger.writeLogDebug("Using ZZ_PCTINV = " + percentInventory);

      // Adjust % Inventory according to min/max rules in ZRPI.
      if (priceMin.compareTo(BigDecimal.ZERO) > 0 && percentInventory.compareTo(priceMin) < 0)
        percentInventory = priceMin;
      else if (priceMax.compareTo(BigDecimal.ZERO) > 0 && percentInventory.compareTo(priceMax) > 0)
        percentInventory = priceMax;

      /* Formula = Base * % Inventory, rounded to nearest integer,
       * but the value is not allowed to be less than one.
       */

      baseValue = baseValue.multiply(percentInventory).divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);

      if (baseValue.compareTo(BigDecimal.ONE) < 0) {
        baseValue = BigDecimal.ONE;
      }


      /* Previous formula was Base * % Inventory, rounded always up to the next integer.
       * % Inventory was also at one point expressed as "0.20" for 20% instead of how it is now,
       * where "20.00" is 20%.
       */
      //return baseValue.multiply(percentInventory).divide(new BigDecimal(100),0,BigDecimal.ROUND_UP);
      //return baseValue.multiply(percentInventory).setScale(0,BigDecimal.ROUND_UP);
    }
    //		else {
    //			return null;
    //		}

    userexitLogger.writeLogDebug("Adjusted condition base value = " + baseValue);			
    return baseValue;
  }
}