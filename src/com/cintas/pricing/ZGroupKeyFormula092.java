package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.base.logging.UserexitLogger;
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
    
    UserexitLogger userexitLogger = new UserexitLogger(ZGroupKeyFormula092.class);
    
    BigDecimal totalVal = BigDecimal.ZERO;
    BigDecimal totalCur = BigDecimal.ZERO;
    
    BigDecimal itemBase = BigDecimal.ZERO;
    BigDecimal itemValue = BigDecimal.ZERO;
    BigDecimal itemRate = BigDecimal.ZERO;
    BigDecimal itemExt = BigDecimal.ZERO;
    
    userexitLogger.writeLogDebug("Condition = " + pricingCondition.getConditionTypeName());
    
    // Only interested in non-statistical insurance conditions with a condition record.
    
    IPricingConditionUserExit[] conditions = pricingDocument.getUserExitConditions();
    for (int i=0; i < conditions.length; i++) {
      
      if (CintasConstants.IsInsuranceCondition(conditions[i].getConditionTypeName())) { // Is insurance
        userexitLogger.writeLogDebug("...looping at condition: " + conditions[i].getConditionTypeName());
        if (!conditions[i].isStatistical()) {                                           // Is not statistical
          userexitLogger.writeLogDebug("...not statistical...");
          if (conditions[i].getConditionRecord() != null)                               // Has a condition record
          {
            userexitLogger.writeLogDebug("...has a condition record: " + conditions[i].getConditionRecordId());
            IPricingConditionUserExit[] _cond = pricingDocument.getUserExitConditions();
            for (int x = 0; x < _cond.length; x++ ) {
              if (_cond[x].getConditionRecord() != null) {
                if (_cond[x].getConditionRecordId().equals(conditions[i].getConditionRecordId())) {
                  userexitLogger.writeLogDebug("...found condition record...");
                  
                  if (!_cond[x].isStatistical()) {
                    
                    if (_cond[i].getConditionBase() != null)
                      itemBase = itemBase.add(_cond[i].getConditionBase().getValue());
                    if (_cond[i].getConditionValue() != null)
                      itemValue = itemValue.add(_cond[i].getConditionValue().getValue());
                    if (_cond[i].getConditionRate() != null)
                        itemRate = itemRate.add(_cond[i].getConditionRate().getValue());
                    
                    if (itemBase.compareTo(BigDecimal.ZERO) != 0 &&
                        itemValue.compareTo(BigDecimal.ZERO) != 0 &&
                        itemRate.compareTo(BigDecimal.ZERO) != 0) {
                      
                      itemExt = itemBase.multiply(itemRate);
                      if (_cond[i].getCalculationType() == 'A')
                        itemExt = itemExt.divide(CintasConstants.ONE_HUNDRED);
                    }
                      
                    userexitLogger.writeLogDebug("Item rate  = " + itemRate);               
                    userexitLogger.writeLogDebug("Item base  = " + itemBase);
                    userexitLogger.writeLogDebug("Item value = " + itemValue);
                    userexitLogger.writeLogDebug("Item extended = " + itemExt);
                    
                  }
                }
              }
            }
          }
          
          if (itemBase.compareTo(BigDecimal.ZERO) != 0 &&
              itemValue.compareTo(BigDecimal.ZERO) != 0 &&
              itemRate.compareTo(BigDecimal.ZERO) != 0) {
            
            totalVal = totalVal.add(itemExt).setScale(2, BigDecimal.ROUND_HALF_UP);
            totalCur = totalCur.add(itemValue).setScale(2, BigDecimal.ROUND_HALF_UP);
            userexitLogger.writeLogDebug("totalVal = " + totalVal);
            userexitLogger.writeLogDebug("totalCur = " + totalCur);
          }
          
          itemBase = BigDecimal.ZERO;
          itemValue = BigDecimal.ZERO;
          itemRate = BigDecimal.ZERO;
          itemExt = BigDecimal.ZERO;
          
        }
        
      }
      
    }
    
    userexitLogger.writeLogDebug("totalVal = " + totalVal);
    userexitLogger.writeLogDebug("totalCur = " + totalCur);
    
    BigDecimal difference = totalVal.subtract(totalCur).setScale(2, BigDecimal.ROUND_HALF_UP);
    userexitLogger.writeLogDebug("Difference = " + difference);

    pricingCondition.setConditionRateValue(difference);
    //pricingCondition.setConditionValue(difference);
    
    return groupKey;
  }
}
