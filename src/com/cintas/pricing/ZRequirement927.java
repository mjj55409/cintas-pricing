package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement927 extends RequirementAdapter {

  public boolean checkRequirement(IConditionFindingManagerUserExit item,
      IStep step, IAccess access) {

    String usage = item.getAttributeValue(CintasConstants.Attributes.USAGE);
    
    if (usage.equals(CintasConstants.Usage.BUYBACK))
      return true;
    if (usage.equals(CintasConstants.Usage.LOST))
      return true;
    if (usage.equals(CintasConstants.Usage.DESTROY))
      return true;
    if (usage.equals(CintasConstants.Usage.CHARGES))
      return true;

    return false;
  }
}