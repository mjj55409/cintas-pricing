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

    BigDecimal totalVal = BigDecimal.ZERO;
    BigDecimal totalCur = BigDecimal.ZERO;
    
    BigDecimal itemBase = BigDecimal.ZERO;
    BigDecimal itemValue = BigDecimal.ZERO;
    BigDecimal itemRate = BigDecimal.ZERO;
    
    IPricingConditionUserExit[] conditions = pricingDocument.getUserExitConditions();
    for (int i=0; i < conditions.length; i++) {
      
      if (CintasConstants.IsInsuranceCondition(conditions[i].getConditionTypeName())) { // Is insurance
        if (!conditions[i].isStatistical()) {                                           // Is not statistical
          if (conditions[i].getConditionRecord() != null)                               // Has a condition record
          {
            IPricingConditionUserExit[] _cond = pricingItem.getUserExitConditions();
            for (int x = 0; x < _cond.length; x++ ) {
              if (_cond[x].getConditionRecordId().equals(conditions[i].getConditionRecordId())) {
                itemBase = itemBase.add(_cond[i].getConditionBase().getValue());
                itemValue = itemValue.add(_cond[i].getConditionValue().getValue());
                itemRate = itemRate.add(_cond[i].getConditionRate().getValue());
              }
            }
          }
        }
        
      }
      
      totalVal = totalVal.add(itemBase.multiply(itemRate));
      totalCur = totalCur.add(itemValue);      
    }
    
    BigDecimal difference = totalVal.subtract(totalCur);
    pricingCondition.setConditionRateValue(difference);
    pricingCondition.setConditionRateValue(difference);
    
    return groupKey;
  }
}
