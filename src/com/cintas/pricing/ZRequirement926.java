package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement926 extends RequirementAdapter {

  public boolean checkRequirement(IConditionFindingManagerUserExit item,
      IStep step, IAccess access) {

    if (CintasConstants.IsProductAncillary(item))
      return false;

    String usage = item.getAttributeValue(CintasConstants.Attributes.USAGE);
    
    if (usage.equals(CintasConstants.Usage.BUYBACK))
      return false;
    if (usage.equals(CintasConstants.Usage.LOST))
      return false;
    if (usage.equals(CintasConstants.Usage.DESTROY))
      return false;
    if (usage.equals(CintasConstants.Usage.CHARGES))
      return false;

    return true;
  }
}