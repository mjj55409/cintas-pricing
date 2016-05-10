package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement926 extends RequirementAdapter {

  public boolean checkRequirement(IConditionFindingManagerUserExit item,
      IStep step, IAccess access) {

    if (!item.getAttributeValue(CintasConstants.Attributes.PRODUCT).equals(item.getAttributeValue(CintasConstants.AncillaryMaterials.SERVICE)))
      return false;

    String usage = item.getAttributeValue(CintasConstants.Attributes.USAGE);
    
    if (usage.equals(CintasConstants.Usage.BUYBACK))
      return false;
    if (usage.equals(CintasConstants.Usage.CHARGES))
      return false;

    return true;
  }
}