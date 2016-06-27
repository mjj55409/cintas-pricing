package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.base.logging.UserexitLogger;
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
  public String setGroupKey(IPricingDocumentUserExit pricingDocument,
      IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition,
      IGroupConditionUserExit groupCondition) {
    
    UserexitLogger userexitLogger = new UserexitLogger(ZGroupKeyFormula093.class);
    
    String groupKey = "093";

    // Don't group non-ancillary items
    if (!CintasConstants.IsProductAncillary(pricingItem) || !CintasConstants.IsRentalProduct(pricingItem))
      return "";

    //String accountAssignmentGroup = pricingItem.getAttributeValue(CintasConstants.Attributes.ACCOUNT_ASSIGNMENT_GROUP);
    char relevantSubtotal;

    if (CintasConstants.IsAncillaryInsurance(pricingItem)) {
    //if (accountAssignmentGroup.equals(CintasConstants.AccountAssignment.INSURANCE)) {
      relevantSubtotal = PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_E;
    }
    else if (CintasConstants.IsAncillaryService(pricingItem)) {
    //else if (accountAssignmentGroup.equals(CintasConstants.AccountAssignment.SERVICE)) {
      relevantSubtotal = PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_J;
    }
    else if (CintasConstants.IsAncillaryMinimum(pricingItem)) {
    //else if (accountAssignmentGroup.equals(CintasConstants.AccountAssignment.MINIMUM)) {
      relevantSubtotal = PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_G;
    }
    else if (CintasConstants.IsAncillaryFreight(pricingItem)) {
    //else if (accountAssignmentGroup.equals(CintasConstants.AccountAssignment.FREIGHT)) {
      relevantSubtotal = PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_D;
    }
    else {
      return "";
    }
    
    userexitLogger.writeLogDebug("Relevant subtotal = " + relevantSubtotal);

    /* The original ECC code relied on collecting conditions from all items
     * that belong to a particular subtotal; however, in IPC, there
     * doesn't seem to be a method for retrieving the subtotal character
     * of a particular pricing procedure step.  We could add all item subtotals,
     * except that some of the time, the code adds the base value instead
     * of the final value.
     */
    BigDecimal newValue = BigDecimal.ZERO;
    BigDecimal _xkwart = BigDecimal.ZERO;
    BigDecimal _xkwert = BigDecimal.ZERO;
    
    IPricingConditionUserExit[] conditions = pricingDocument.getUserExitConditions();
    for (int i=0; i<conditions.length; i++) {
      String conditionType = (conditions[i].getConditionTypeName() != null 
          ? conditions[i].getConditionTypeName() : "");
      
      if (relevantSubtotal == PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_E &&
          CintasConstants.IsInsuranceCondition(conditionType)) {
        userexitLogger.writeLogDebug("Condition = " + conditionType);
        if (!conditions[i].isStatistical()) {
          userexitLogger.writeLogDebug("...not statistical...");
          if (conditions[i].getConditionBase() != null)
            _xkwart = conditions[i].getConditionBase().getValue();
          else
            _xkwart = BigDecimal.ZERO;
          
          if (conditions[i].getConditionValue() != null)
            _xkwert = conditions[i].getConditionValue().getValue();
          else
            _xkwert = BigDecimal.ZERO;
          
          userexitLogger.writeLogDebug("xkwart = " + _xkwart);
          userexitLogger.writeLogDebug("xkwert = " + _xkwert);
        } 
        else {
          _xkwart = BigDecimal.ZERO;
          _xkwert = BigDecimal.ZERO;
        }
      }
      else if (relevantSubtotal == PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_G && 
          conditionType.equals(CintasConstants.Conditions.SubTotals.AMOUNT_MIN_CHARGE)) {
        _xkwart = conditions[i].getConditionBase().getValue();
        _xkwert = conditions[i].getConditionValue().getValue();
      }
      else if (relevantSubtotal == PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_D &&
          conditionType.equals(CintasConstants.Conditions.BASE_PRICE)) {
        _xkwart = conditions[i].getConditionBase().getValue();
        _xkwert = conditions[i].getConditionValue().getValue();
      }
      else if (relevantSubtotal == PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_J &&
          conditionType.equals("ZSV1"/*CintasConstants.Conditions.SERVICE_CHARGE*/)) {
        _xkwart = conditions[i].getConditionBase().getValue();
        _xkwert = conditions[i].getConditionValue().getValue();
      }
      else {
        _xkwart = BigDecimal.ZERO;
        _xkwert = BigDecimal.ZERO;
      }
      
      if (_xkwart.compareTo(BigDecimal.ZERO) != 0 || _xkwert.compareTo(BigDecimal.ZERO) != 0) {
        if (conditions[i].getCalculationType() == PricingCustomizingConstants.CalculationType.PERCENTAGE && 
            relevantSubtotal != PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_E) {
          // Percentage Condition (but not insurance)
          newValue = newValue.add(_xkwart);
        }
        else {
          // Not a Percentage Condition
          newValue = newValue.add(_xkwert);
          userexitLogger.writeLogDebug("Running total for " + relevantSubtotal + " = " + newValue);
        }
      }
      
    }
    
    if (newValue.compareTo(new BigDecimal(0)) > 0) {
      pricingCondition.setConditionRateValue(newValue);
      pricingCondition.setConditionValue(newValue);
    }

    userexitLogger.writeLogDebug("Group value = " + newValue);

    return groupKey;
  }
}
