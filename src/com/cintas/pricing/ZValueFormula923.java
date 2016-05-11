package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula923 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    UserexitLogger userexitLogger = new UserexitLogger(ZValueFormula923.class);
    
    // Get ZPRT condition record from the conditions table
    BigDecimal programTotal = BigDecimal.ZERO;
    IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
    for (int i = 0; i < conditions.length; i++) {
      String _condition = (conditions[i].getConditionTypeName() != null 
          ? conditions[i].getConditionTypeName() : CintasConstants.INITIAL);

      if (_condition.equals(CintasConstants.Conditions.PROGRAM_TOTAL)) {
        programTotal = conditions[i].getConditionValue().getValue();
        break;
      }
    }

    BigDecimal minimumCharge = pricingCondition.getConditionRate().getValue();
    
    userexitLogger.writeLogDebug("Program total = " + programTotal);
    userexitLogger.writeLogDebug("Minimum charge = " + minimumCharge);

    BigDecimal kbetr = BigDecimal.ZERO;
    if (pricingItem.isReturn()) {
      if (pricingCondition.getConditionValue().getValue().compareTo(programTotal) < 0)
        kbetr = minimumCharge.subtract(programTotal);
    }
    else {
      if (pricingCondition.getConditionValue().getValue().compareTo(programTotal) > 0)
        kbetr = minimumCharge.subtract(programTotal);
    }

    userexitLogger.writeLogDebug("Setting condition = " + kbetr);
    pricingCondition.setConditionValue(kbetr);
    return null;    
  }
}
