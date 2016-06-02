package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZBaseFormula907 extends BaseFormulaAdapter {

  public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {
    
    UserexitLogger userexitLogger = new UserexitLogger(ZBaseFormula907.class);
    
    IPricingConditionUserExit ruleCondition = null;
    IPricingConditionUserExit chargeCondition = null;
    
    IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
    
    for (int i = 0; i < conditions.length; i++) {
      if (conditions[i].getConditionTypeName() != null && conditions[i].getConditionTypeName().equals(CintasConstants.Conditions.Rules.STOP_MIN))
        ruleCondition = conditions[i];

      if (conditions[i].getConditionTypeName() != null && conditions[i].getConditionTypeName().equals(CintasConstants.Conditions.STOP_MIN))
        chargeCondition = conditions[i];
      
      if (ruleCondition != null && chargeCondition != null)
        break;
    }
    
    BigDecimal kbetr = BigDecimal.ZERO;
    
    if (pricingItem.getAttributeValue(CintasConstants.Attributes.STOP_EXCLUSION).equals(CintasConstants.ABAP_TRUE)) {
      userexitLogger.writeLogDebug("Exclusion is active.");
    }
    else if (ruleCondition != null || chargeCondition != null) {
      if (ruleCondition != null && ruleCondition.getConditionValue().getValue().compareTo(BigDecimal.ZERO) != 0) {
        kbetr = ruleCondition.getConditionValue().getValue();
      }
      else {
        BigDecimal chargeMin;
        BigDecimal chargeMax;
        if (ruleCondition == null) {
          chargeMin = BigDecimal.ZERO;
          chargeMax = BigDecimal.ZERO;
        }
        else {
          String _min = ruleCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.STOP_MIN);
          String _max = ruleCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.STOP_MAX);
          chargeMin = (_min != null ? new BigDecimal(_min) : BigDecimal.ZERO);
          chargeMax = (_max != null ? new BigDecimal(_max) : BigDecimal.ZERO);
        }
        BigDecimal stvKbetr = (chargeCondition != null ? chargeCondition.getConditionRate().getValue() : BigDecimal.ZERO);

        userexitLogger.writeLogDebug("chargeMin = " + chargeMin);
        userexitLogger.writeLogDebug("chargeMax = " + chargeMax);
        userexitLogger.writeLogDebug("chargeVal = " + stvKbetr);
        
        if (stvKbetr.compareTo(BigDecimal.ZERO) == 0) {
//          if (chargeMax.compareTo(BigDecimal.ZERO) != 0) {
//            kbetr = chargeMax;
//          }
//          else {
            kbetr = chargeMin;
//          }
        }
        else {
          if (stvKbetr.compareTo(chargeMin) < 0 && chargeMin.compareTo(BigDecimal.ZERO) != 0) {
            kbetr = chargeMin;
          }
          else if (stvKbetr.compareTo(chargeMax) > 0 && chargeMax.compareTo(BigDecimal.ZERO) != 0) {
            kbetr = chargeMax;
          }
          else {
            kbetr = stvKbetr;
          }
        }
      }
    }
    
    userexitLogger.writeLogDebug("Setting condition  = " + kbetr);
    pricingCondition.setConditionRateValue(kbetr);
    pricingCondition.setConditionValue(kbetr);

    return BigDecimal.ZERO;
  }   
}