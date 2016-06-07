package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement915 extends RequirementAdapter {

  public boolean checkRequirement(IConditionFindingManagerUserExit item,
      IStep step, IAccess access) {

    java.util.Vector applicableConditions = new java.util.Vector(3);
    applicableConditions.add(CintasConstants.Conditions.INVOICE_DISCOUNT);
    applicableConditions.add(CintasConstants.Conditions.Exclusions.INVOICE_DISCOUNT);
    applicableConditions.add(CintasConstants.Conditions.Rules.INVOICE_DISCOUNT);

    if (applicableConditions.contains(step.getConditionType().getName())) {

      if (item.getAttributeValue(CintasConstants.Attributes.NOCHARGE).equals(CintasConstants.ABAP_TRUE))
        return false;

      if (CintasConstants.IsProductAncillary(item))
        if (CintasConstants.IsAncillaryInsurance(item) || CintasConstants.IsAncillaryFreight(item)) //CR938
          return false;

    } 
    else {

      if (item.getAttributeValue(CintasConstants.Attributes.NOCHARGE).equals(CintasConstants.ABAP_TRUE))
        return false;

      if (CintasConstants.IsProductAncillary(item))
        return false;

      java.util.Vector usageCodes = new java.util.Vector(5);
      usageCodes.add(CintasConstants.Usage.BUYBACK);
      usageCodes.add(CintasConstants.Usage.LOST);
      usageCodes.add(CintasConstants.Usage.DESTROY);
      usageCodes.add(CintasConstants.Usage.CHARGES);
      usageCodes.add(CintasConstants.Usage.DIRECT);

      if (usageCodes.contains(item.getAttributeValue(CintasConstants.Attributes.USAGE)))
        return false;
      
    }

    return true;
  }
}