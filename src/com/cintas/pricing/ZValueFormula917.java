package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.base.util.event.ClearAllStatusEvent;
import com.sap.spe.base.util.event.ErrorStatusEvent;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula917 extends ValueFormulaAdapter {

  //private ErrorStatusEvent event2 = new ErrorStatusEvent(ZValueFormula917.class,"PRC_PRI",801,new String[] {"ZPR0"},"","MandatoryFieldItem",false);
  private ErrorStatusEvent ZPR0Event = new ErrorStatusEvent(this, "Mandatory Condition ZPR0 is Missing", "MandatoryFieldItem");
  private ErrorStatusEvent ZBOKEvent = new ErrorStatusEvent(this, "Mandatory Condition ZBOK is Missing", "MandatoryFieldItem");
  private ErrorStatusEvent ZPR1Event = new ErrorStatusEvent(this, "Mandatory Condition ZPR1 is Missing", "MandatoryFieldItem");

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    UserexitLogger userexitLogger = new UserexitLogger(ZValueFormula917.class);

    pricingItem.clearStatusMessage(ZPR0Event);
    pricingItem.clearStatusMessage(ZBOKEvent);
    pricingItem.clearStatusMessage(ZPR1Event);
    pricingItem.clearStatusMessage(new ClearAllStatusEvent(this, "MandatoryFieldItem"));

    IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
    IPricingConditionUserExit ZPR0 = null;
    IPricingConditionUserExit ZBOK = null;
    IPricingConditionUserExit ZPR1 = null;

    ErrorStatusEvent errorEvent = null;

    for (int i=0; i<conditions.length; i++) {
      String conditionType = (conditions[i].getConditionTypeName() != null
          ? conditions[i].getConditionTypeName() : CintasConstants.INITIAL);
      if (conditionType.equals(CintasConstants.Conditions.AGREEMENT_PRICE)) {
        userexitLogger.writeLogDebug("Found condition ZPR0.");
        ZPR0 = conditions[i];
      }				
      else if (conditionType.equals(CintasConstants.Conditions.BOOK_PRICE)) {
        userexitLogger.writeLogDebug("Found condition ZBOK.");
        ZBOK = conditions[i];
      }
      else if (conditionType.equals(CintasConstants.Conditions.CALCULATED_PRICE)) {
        userexitLogger.writeLogDebug("Found condition ZPR1.");
        ZPR1 = conditions[i];
      }
    }
    
    if (CintasConstants.IsProductAncillary(pricingItem)) {
      if (ZPR1 == null || ZPR1.getConditionRate().getValue().compareTo(BigDecimal.ZERO) == 0) {
        //pricingItem.setStatusMessage(ZPR1Event);
        //return BigDecimal.ZERO;
        return null;
      } else {
        return null;
      }
    }

    if (ZBOK == null) {
      userexitLogger.writeLogDebug("Condition ZBOK not found.");
      errorEvent = ZBOKEvent;
    }

    // Execute for usage codes that are not B, D, L, R, or X
    String usage = pricingItem.getAttributeValue(CintasConstants.Attributes.USAGE);
    if (!(usage.equals(CintasConstants.Usage.BUYBACK) ||
        usage.equals(CintasConstants.Usage.DIRECT)  ||
        usage.equals(CintasConstants.Usage.LOST)    ||
        usage.equals(CintasConstants.Usage.DESTROY) ||
        usage.equals(CintasConstants.Usage.CHARGES))) {
      if (ZPR0 == null) {
        errorEvent = ZPR0Event;
      }
    }

    if (errorEvent != null) {
      userexitLogger.writeLogDebug("CRM message set: " + errorEvent.getMessage());
      pricingItem.setStatusMessage(errorEvent);
      return BigDecimal.ZERO;
    }
    
//    /* This is necessary for IPC only
//     * Check if I have a value, and if not, grab the value from ZIPR
//     */
//    if (pricingCondition.getConditionValue().getValue().compareTo(BigDecimal.ZERO) == 0) {
//      pricingCondition.setConditionRateValue(CintasConstants.GetConditionRate(pricingItem, CintasConstants.Conditions.INITIAL_PRICE));
//      return CintasConstants.GetConditionValue(pricingItem, CintasConstants.Conditions.INITIAL_PRICE);
//    } else {
      return null;
//    }
  }
}
