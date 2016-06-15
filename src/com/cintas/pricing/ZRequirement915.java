package com.cintas.pricing;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement915 extends RequirementAdapter {

  public boolean checkRequirement(IConditionFindingManagerUserExit item,
      IStep step, IAccess access) {
    
    UserexitLogger userexitLogger = new UserexitLogger(ZRequirement915.class);

    java.util.Vector applicableConditions = new java.util.Vector(3);
    applicableConditions.add(CintasConstants.Conditions.INVOICE_DISCOUNT);
    applicableConditions.add(CintasConstants.Conditions.Exclusions.INVOICE_DISCOUNT);
    applicableConditions.add(CintasConstants.Conditions.Rules.INVOICE_DISCOUNT);

    String conditionType = step.getConditionType().getName();
    String noCharge = item.getAttributeValue(CintasConstants.Attributes.NOCHARGE);
    String usage = item.getAttributeValue(CintasConstants.Attributes.USAGE);
    
    userexitLogger.writeLogDebug("Condition = " + conditionType);
    userexitLogger.writeLogDebug("No Charge = " + noCharge);
    userexitLogger.writeLogDebug("Usage = " + usage);
    
    if (applicableConditions.contains(conditionType)) {
      
      userexitLogger.writeLogDebug("Handling invoice discounts.");

      if (noCharge.equals(CintasConstants.ABAP_TRUE))
        return false;

      if (CintasConstants.IsAncillaryMinimum(item) || CintasConstants.IsAncillaryService(item)) //CR938
        return false;

    } 
    else {

      userexitLogger.writeLogDebug("Handling others.");

      if (noCharge.equals(CintasConstants.ABAP_TRUE))
        return false;

      if (CintasConstants.IsProductAncillary(item))
        return false;

      java.util.Vector usageCodes = new java.util.Vector(5);
      usageCodes.add(CintasConstants.Usage.BUYBACK);
      usageCodes.add(CintasConstants.Usage.LOST);
      usageCodes.add(CintasConstants.Usage.DESTROY);
      usageCodes.add(CintasConstants.Usage.CHARGES);
      usageCodes.add(CintasConstants.Usage.DIRECT);

      if (usageCodes.contains(usage))
        return false;
      
    }

    return true;
  }
}