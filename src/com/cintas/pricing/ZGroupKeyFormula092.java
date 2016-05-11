package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.GroupKeyFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IGroupConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingDocumentUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZGroupKeyFormula092 extends GroupKeyFormulaAdapter {

  /* Set a constant group key.  This could be any string.  
   * "092" was used in the original ECC formula, so we're using 
   * it here as well.  By returning the same string every time,
   * all items will be considered part of the same group.
   */
  private static final String groupKey = "092";

  public String setGroupKey(IPricingDocumentUserExit pricingDocument,
      IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition,
      IGroupConditionUserExit groupCondition) {

    IPricingConditionUserExit ZSTR = null;
    IPricingConditionUserExit ZSTV = null;

    String stopExclusion = "";

    BigDecimal stopMin = new BigDecimal(0);
    BigDecimal stopMax = new BigDecimal(0);
    BigDecimal zstrValue = new BigDecimal(0);
    BigDecimal zstvValue = new BigDecimal(0);

    IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
    for (int i=0; i<conditions.length; i++) {
      String conditionType = conditions[i].getConditionTypeName() != null ? conditions[i].getConditionTypeName() : CintasConstants.INITIAL;
      if (conditionType.equals(CintasConstants.Conditions.Rules.STOP_MIN)) {
        stopExclusion = (
            conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.STOP_EXCLUSION) != null ? 
                conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.STOP_EXCLUSION) : stopExclusion);
        stopMin = (
            conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.STOP_MIN) != null ? 
                new BigDecimal(conditions[i].getConditionRecord().getVariableDataValue("ZZ_STOPMIN")) : stopMin
        );
        stopMax = (
            conditions[i].getConditionRecord().getVariableDataValue(CintasConstants.Attributes.STOP_MAX) != null ? 
                new BigDecimal(conditions[i].getConditionRecord().getVariableDataValue("ZZ_STOPMAX")) : stopMax
        );

        ZSTR = conditions[i];
        zstrValue = conditions[i].getConditionRate().getValue();
      }
      else if (conditionType.equals(CintasConstants.Conditions.STOP_MIN)) {
        ZSTV = conditions[i];
        zstvValue = conditions[i].getConditionRate().getValue();
      }
      if (ZSTR != null && ZSTV != null)
        break;
    }

    // If stop rules do not exist, set condition rate to 0 and return.
    if (ZSTR == null && ZSTV == null) {
      pricingCondition.setConditionRateValue(new BigDecimal(0));
      return groupKey;			
    }

    // If stop exclusion indicator is populated, set condition rate to 0 and return.
    if (stopExclusion.equals(CintasConstants.ABAP_TRUE)) {
      pricingCondition.setConditionRateValue(BigDecimal.ZERO);
      return groupKey;
    }

    // If a value is populated on the ZSTR condition, set the condition rate and return.
    if (zstrValue.compareTo(BigDecimal.ZERO) > 0) {
      pricingCondition.setConditionRateValue(zstrValue);
      return groupKey;
    }

    // Default to the value of the ZSTV record
    pricingCondition.setConditionRateValue(zstvValue);

    if (ZSTR != null) {
      if (zstvValue.compareTo(BigDecimal.ZERO) == 0) {
        if (stopMax.compareTo(BigDecimal.ZERO) > 0) {
          pricingCondition.setConditionRateValue(stopMax);
        } else {
          pricingCondition.setConditionRateValue(stopMin);
        }
      } else {
        if (zstvValue.compareTo(stopMin) < 0 && stopMin.compareTo(BigDecimal.ZERO) > 0) {
          pricingCondition.setConditionRateValue(stopMin);
        } else if (zstvValue.compareTo(stopMax) > 0 && stopMax.compareTo(BigDecimal.ZERO) > 0) {
          pricingCondition.setConditionRateValue(stopMax);
        }
      }
    }

    // Always group all items by returning the same group key every time
    return groupKey;
  }
}
