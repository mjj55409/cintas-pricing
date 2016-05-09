package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZBaseFormula903 extends BaseFormulaAdapter {

  public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    // Can't set item as statistical, as the original routine does.  But is it necessary anyway?

    String currentConditionType = pricingCondition.getConditionTypeName();
    
    if (currentConditionType == null || !currentConditionType.equals(CintasConstants.Conditions.Rules.NONCOMPLIANCE))
      return null;


    String materialExclusionRule = (
        pricingCondition.getConditionRecord().getVariableDataValue(CintasConstants.Attributes.MATERIAL_EXCLUSION_R) != null 
        ? pricingCondition.getConditionRecord().getVariableDataValue(CintasConstants.Attributes.MATERIAL_EXCLUSION_R) : CintasConstants.INITIAL);
    
    String quantityErrorRule = (
        pricingCondition.getConditionRecord().getVariableDataValue(CintasConstants.Attributes.QUANTITY_ERROR_R) != null 
        ? pricingCondition.getConditionRecord().getVariableDataValue(CintasConstants.Attributes.QUANTITY_ERROR_R) : CintasConstants.INITIAL);
    
    BigDecimal minQuantity = new BigDecimal(0);
    BigDecimal maxQuantity = new BigDecimal(0);
    String materialExcluded = "";

    IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
    for (int i=0; i < conditions.length; i++) {
      String conditionType = (conditions[i].getConditionTypeName() != null ? conditions[i].getConditionTypeName() : CintasConstants.INITIAL);
      if (conditionType.equals(CintasConstants.Conditions.MATERIAL_LIST)) {
        materialExcluded = (
            conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.MATERIAL_EXCLUSION) != null 
            ? conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.MATERIAL_EXCLUSION) : CintasConstants.INITIAL);
      }
      else if (conditionType.equals(CintasConstants.Conditions.Rules.QTY_RESTRICTION)) {
        minQuantity = (
            conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.QUANTITY_MIN) != null 
            ? new BigDecimal(conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.QUANTITY_MIN)) : minQuantity);

        maxQuantity = (
            conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.QUANTITY_MAX) != null 
            ? new BigDecimal(conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.QUANTITY_MAX)) : maxQuantity);

        break;
      }
    }

    // Apply material exclusion errors
    if (materialExcluded.equals(CintasConstants.ABAP_TRUE)) {
      if (materialExclusionRule.equals(CintasConstants.MATERIAL_EXCL_NOCHG)) {
        // Set no-charge flag to be used in other pricing formulas
        pricingItem.setObjectForUserExits(CintasConstants.Attributes.NOCHARGE, CintasConstants.ABAP_TRUE);

        // Apply quantity exclusion errors
        if (!quantityErrorRule.equals(CintasConstants.INITIAL)) {
          BigDecimal orderedQuantity = pricingItem.getBaseQuantity().getValue();
          if ((minQuantity.compareTo(BigDecimal.ZERO) > 0 && orderedQuantity.compareTo(minQuantity) < 0) 
              || (maxQuantity.compareTo(BigDecimal.ZERO) > 0 && orderedQuantity.compareTo(maxQuantity) > 0)) {
            if (quantityErrorRule.equals(CintasConstants.QUANTITY_RULE_NOCHG)) {
              pricingItem.setObjectForUserExits(CintasConstants.Attributes.NOCHARGE, CintasConstants.ABAP_TRUE);
            }
            else if (quantityErrorRule.equals(CintasConstants.QUANTITY_RULE_MAX)) {
              /* Original routine changed the pricing quantity (komp-mgame) here
               * This is now done instead in ABAP function module Z_FM_CRM_RESTRICTIONS
               * and BADI implementation ZBA_CRM_TECH_PRICING.
               */
            }
          }
        }
      }
    }


    return null;
  }
}
