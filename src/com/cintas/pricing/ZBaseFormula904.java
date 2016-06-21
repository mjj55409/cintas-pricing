package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.pricing.customizing.IPricingStep;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZBaseFormula904 extends BaseFormulaAdapter {
	
  public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {
    
    UserexitLogger userexitLogger = new UserexitLogger(ZBaseFormula904.class);

    BigDecimal xkwert = (new BigDecimal(100)).negate();
    BigDecimal xkwart = BigDecimal.ZERO;   
    
    if (CintasConstants.IsItemNoCharge(pricingItem)) {
      
      userexitLogger.writeLogDebug("Item is no charge.");
      
      pricingCondition.setStatistical(true);
      
      if (!CintasConstants.IsAncillaryInsurance(pricingItem)) {
      
        IPricingStep step = (IPricingStep) pricingCondition.getStep();
        
        userexitLogger.writeLogDebug("From step = " + step.getFromStep());
        userexitLogger.writeLogDebug("To step = " + step.getToStep());
        
        IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
        
        for (int i=0; i < conditions.length; i++) {
          if (conditions[i].getStepNumber() >= step.getFromStep() && conditions[i].getStepNumber() <= step.getToStep()) {
            if (!CintasConstants.IsStringInitial(conditions[i].getAccountKey1())) {
              xkwart = xkwart.add(conditions[i].getConditionValue().getValue());
            }
          }
        }
        
      }
      else {
        // In CRM, subtotal2 is zero for ancillary insurance (because statistical prices), 
        // so we'll get our base from the calculated price line instead
        xkwart = CintasConstants.GetConditionValue(pricingItem, CintasConstants.Conditions.CALCULATED_PRICE);
      }
      
      userexitLogger.writeLogDebug("Condition base = " + xkwart);
      userexitLogger.writeLogDebug("Condition rate = " + xkwert);

      pricingCondition.setConditionBaseValue(xkwart);
      pricingCondition.setConditionRateValue(xkwert);
    }
    else {
      userexitLogger.writeLogDebug("Item is chargeable.");
      
      if (!CintasConstants.IsAncillaryInsurance(pricingItem))
        pricingCondition.setInactive(PricingCustomizingConstants.InactiveFlag.INVISIBLE);
    }

    return null;
  }
  
}