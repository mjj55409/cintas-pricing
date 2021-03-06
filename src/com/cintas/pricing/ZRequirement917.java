package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement917 extends RequirementAdapter {

  public boolean checkRequirement(IConditionFindingManagerUserExit item,
      IStep step, IAccess access) {
    
    if (CintasConstants.IsProductAncillary(item))
      return false;
    
    String usage = item.getAttributeValue(CintasConstants.Attributes.USAGE);
    
    if (!(usage.equals(CintasConstants.Usage.RENTAL) || usage.equals(CintasConstants.Usage.UNILEASE)))
      return false;

    return true;
  }
}
