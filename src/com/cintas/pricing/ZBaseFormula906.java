package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.pricing.customizing.IPricingStep;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZBaseFormula906 extends BaseFormulaAdapter {

  public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    UserexitLogger userexitLogger = new UserexitLogger(ZBaseFormula906.class);

    String conditionType = (pricingCondition.getConditionTypeName() != null
        ? pricingCondition.getConditionTypeName() : CintasConstants.INITIAL);

    userexitLogger.writeLogDebug("Condition = " + conditionType);

    if (conditionType.equals(CintasConstants.Conditions.ITEM_DISCOUNT) && CintasConstants.IsItemNoCharge(pricingItem)) {
      return BigDecimal.ZERO;
    }

    IPricingConditionUserExit ruleCondition = null;		
    String ruleConditionType = null;
    int ruleStepNumber = 0;

    if (conditionType.equals(CintasConstants.Conditions.SERVICE_CHARGE) || conditionType.equals(CintasConstants.Conditions.SubTotals.AMOUNT_SERV_CHARGE)) {
      ruleConditionType = CintasConstants.Conditions.Rules.SERVICE_CHARGE;
    }
    else if (conditionType.equals(CintasConstants.Conditions.INVOICE_DISCOUNT)) {
      ruleConditionType = CintasConstants.Conditions.Rules.INVOICE_DISCOUNT;
    }
    else {
      /* Relies on looking at XKOMV-STUNB (lower limit of procedure step numbers for condition base)
       * for generic processing.
       */
      IPricingStep step = (IPricingStep)pricingCondition.getStep();
      if (step != null) {
        ruleStepNumber = step.getFromStep();
      }
    }

    // Find rule condition record based on criteria outlined above.
    if (ruleConditionType != null || ruleStepNumber > 0) {
      IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
      for (int i=0; i<conditions.length; i++) {
        if (conditions[i].getConditionTypeName() != null 
            && ruleConditionType != null
            && conditions[i].getConditionTypeName().equals(ruleConditionType)) {
          ruleCondition = conditions[i];
          break;
        }

        if (ruleStepNumber > 0 && conditions[i].getStepNumber() == ruleStepNumber) {
          ruleCondition = conditions[i];
          break;
        }
      }
    }

    /* Proceed with rule determination if something was found that matches the calculation
     *  type of the current condition.
     */
    if (ruleCondition != null && ruleCondition.getCalculationType() == pricingCondition.getCalculationType()) {
      String stopExclusion = (ruleCondition.getConditionRecord().getVariableDataValue(CintasConstants.Attributes.STOP_EXCLUSION) != null ?
          ruleCondition.getConditionRecord().getVariableDataValue(CintasConstants.Attributes.STOP_EXCLUSION) : CintasConstants.INITIAL);
      BigDecimal priceMin = (ruleCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.PRICE_MIN) != null ?
          new BigDecimal(ruleCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.PRICE_MIN)) : new BigDecimal("0"));
      BigDecimal priceMax = (ruleCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.PRICE_MAX) != null ?
          new BigDecimal(ruleCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.PRICE_MAX)) : new BigDecimal("0"));	

      // Improper storage of price min/price max fields when the record currency is USD
//      if (ruleCondition.getCalculationType() == PricingCustomizingConstants.CalculationType.FIXED_AMOUNT) {
//        if (ruleCondition.getConditionRate().getUnitName().equals(CintasConstants.Currency.US) || 
//            ruleCondition.getConditionRate().getUnitName().equals(CintasConstants.Currency.CA)) {
//          priceMin = priceMin.multiply(BigDecimal.TEN);
//          priceMax = priceMax.multiply(BigDecimal.TEN);
//        }
//      }

      if (stopExclusion.equals(CintasConstants.ABAP_TRUE)) {
        pricingCondition.setConditionRateValue(BigDecimal.ZERO);
      } else {
        // begin CR801
        userexitLogger.writeLogDebug("Rule condition = " + ruleCondition.getConditionTypeName());
        BigDecimal ruleRate = ruleCondition.getConditionRate().getValue();
        if (ruleRate.compareTo(BigDecimal.ZERO) > 0) {
          pricingCondition.setConditionRateValue(ruleRate);
        } else { //CR801
          userexitLogger.writeLogDebug("ZZ_PRICMIN = " + priceMin);
          userexitLogger.writeLogDebug("ZZ_PRCMAX = " + priceMax);
          BigDecimal currentRateAbs = pricingCondition.getConditionRate().getValue().abs();
          if (priceMin.compareTo(BigDecimal.ZERO) != 0 && currentRateAbs.compareTo(priceMin) < 0) {
            if (pricingCondition.getConditionRate().getValue().compareTo(BigDecimal.ZERO) < 0) {
              pricingCondition.setConditionRateValue(priceMin.negate());
            } else {
              pricingCondition.setConditionRateValue(priceMin);
            }
          } else if (priceMax.compareTo(BigDecimal.ZERO) != 0 && currentRateAbs.compareTo(priceMax) > 0) {
            if (pricingCondition.getConditionRate().getValue().compareTo(BigDecimal.ZERO) < 0) { 
              pricingCondition.setConditionRateValue(priceMax.negate());
            } else {
              pricingCondition.setConditionRateValue(priceMax);
            }
          }
        } // end CR801
      }
    }

    // begin CR638
    BigDecimal qtyInventory;
    if (pricingItem.getAttributeValue("ZZ_INVQTY") == null) {
      userexitLogger.writeLogDebug("ZZ_INVQTY is null.");
      qtyInventory = BigDecimal.ZERO;
    } else {
      qtyInventory = new BigDecimal(pricingItem.getAttributeValue(CintasConstants.Attributes.INVENTORY_QTY));
    }
    userexitLogger.writeLogDebug("ZZ_INVQTY = " + qtyInventory);
    if (qtyInventory.compareTo(BigDecimal.ZERO) > 0 &&
        pricingCondition.getCalculationType() == PricingCustomizingConstants.CalculationType.QUANTITY_DEP &&
        pricingCondition.getAccountKey1().equals(CintasConstants.AccountKey.INSURANCE) ) {
      userexitLogger.writeLogDebug("Using inventory quantity for calculation.");
      return qtyInventory;
    }

    // end CR638

    return null;
  }
}