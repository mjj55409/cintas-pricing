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
      
    if (ruleCondition == null)
      return null;
      
    BigDecimal kbetr = pricingCondition.getConditionRate().getValue();
    
    if (kbetr.compareTo(BigDecimal.ZERO) == 0) {
      
      BigDecimal chargeMin = (ruleCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.STOP_MIN) != null ?
          new BigDecimal(ruleCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.STOP_MIN)) : new BigDecimal("0"));
      BigDecimal chargeMax = (ruleCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.STOP_MAX) != null ?
          new BigDecimal(ruleCondition.getConditionRecord().getVariableKeyValue(CintasConstants.Attributes.STOP_MAX)) : new BigDecimal("0"));  

      BigDecimal stvKbetr = chargeCondition.getConditionRate().getValue();
      
      userexitLogger.writeLogDebug("chargeMin = " + chargeMin);
      userexitLogger.writeLogDebug("chargeMax = " + chargeMax);
      userexitLogger.writeLogDebug("chargeVal = " + stvKbetr);

      if (stvKbetr.compareTo(chargeMin) < 0)
        kbetr = chargeMin;
      else if (stvKbetr.compareTo(chargeMax) > 0)
        kbetr = chargeMax;
      else
        kbetr = stvKbetr;
      
      if (pricingItem.isReturn())
        kbetr = kbetr.negate();
 
      userexitLogger.writeLogDebug("Setting condition  = " + kbetr);
      pricingCondition.setConditionRateValue(kbetr);
      pricingCondition.setConditionValue(kbetr);
    }

    return BigDecimal.ZERO;
  }
}
