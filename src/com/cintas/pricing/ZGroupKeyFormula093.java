package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.GroupKeyFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IGroupConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingDocumentUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZGroupKeyFormula093 extends GroupKeyFormulaAdapter {

  /* Use a constant group value to group all ancillary items together.
   * Non-ancillary items and other non-qualifying cases will be 
   * grouped into a blank group with no value assigned.
   */
  private static final String groupKey = "093";

  public String setGroupKey(IPricingDocumentUserExit pricingDocument,
      IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition,
      IGroupConditionUserExit groupCondition) {

    // Don't group non-ancillary items
    if (CintasConstants.IsProductAncillary(pricingItem) || !CintasConstants.IsRentalProduct(pricingItem))
      return "";

    String accountAssignmentGroup = pricingItem.getAttributeValue(CintasConstants.Attributes.ACCOUNT_ASSIGNMENT_GROUP);
    char relevantSubtotal;

    if (accountAssignmentGroup.equals(CintasConstants.AccountAssignment.INSURANCE)) {
      relevantSubtotal = PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_E;
    }
    else if (accountAssignmentGroup.equals(CintasConstants.AccountAssignment.SERVICE)) {
      relevantSubtotal = PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_J;
    }
    else if (accountAssignmentGroup.equals(CintasConstants.AccountAssignment.MINIMUM)) {
      relevantSubtotal = PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_G;
    }
    else if (accountAssignmentGroup.equals(CintasConstants.AccountAssignment.FREIGHT)) {
      relevantSubtotal = PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_D;
    }
    else {
      return "";
    }

    /* The original ECC code relied on collecting conditions from all items
     * that belong to a particular subtotal; however, in IPC, there
     * doesn't seem to be a method for retrieving the subtotal character
     * of a particular pricing procedure step.  We could add all item subtotals,
     * except that some of the time, the code adds the base value instead
     * of the final value.
     */
    BigDecimal newValue = new BigDecimal(0);

    IPricingConditionUserExit[] conditions = pricingDocument.getUserExitConditions();
    for (int i=0; i<conditions.length; i++) {
      String conditionType = conditions[i].getConditionTypeName() != null 
          ? conditions[i].getConditionTypeName() : "";

          switch (relevantSubtotal) {
          case PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_E:
            if (!conditionType.equals(CintasConstants.Conditions.INSURANCE_CHARGE) &&
                !conditionType.equals(CintasConstants.Conditions.INSURANCE_AUTOLR) &&
                !conditionType.equals(CintasConstants.Conditions.INSURANCE_PCT) &&
                !conditionType.equals(CintasConstants.Conditions.INSURANCE_MAKEUP) &&
                !conditionType.equals(CintasConstants.Conditions.INSURANCE_TRIM))
              continue;
            break;

          case PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_G:
            // Invoice Min/Max
            if (!conditionType.equals(CintasConstants.Conditions.SubTotals.AMOUNT_MIN_CHARGE))
              continue;
            break;

          case PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_D:
            // Freight Charges
            if (!conditionType.equals(CintasConstants.Conditions.BASE_PRICE))
              continue;
            break;

          case PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_J:
            if (!conditionType.equals(CintasConstants.Conditions.SERVICE_CHARGE))
              continue;
            break;

          default:
            continue;
          }

          if (conditions[i].getCalculationType() == PricingCustomizingConstants.CalculationType.PERCENTAGE && 
              !accountAssignmentGroup.equals(CintasConstants.AccountAssignment.INSURANCE)) {
            // Percentage Condition (but not insurance)
            newValue = newValue.add(conditions[i].getConditionBase().getValue());
          }
          else {
            // Not a Percentage Condition
            newValue = newValue.add(conditions[i].getConditionValue().getValue());
          }
    }

    if (newValue.compareTo(new BigDecimal(0)) > 0) {
      pricingCondition.setConditionRateValue(newValue);
      pricingCondition.setConditionValue(newValue);
    }

    return groupKey;
  }
}
