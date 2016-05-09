package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement914 extends RequirementAdapter {

  public boolean checkRequirement(IConditionFindingManagerUserExit item,
      IStep step, IAccess access) {

    if (CintasConstants.IsProductAncillary(item))
      return false;

    if (CintasConstants.IsAttributeInitial(item, CintasConstants.Attributes.SPECIAL_HANDLING) && 
        CintasConstants.IsAttributeInitial(item, CintasConstants.Attributes.PACKAGE_CODE))
      return false;

    /* The rest of the code in requirement 914 was moved to ZPrepareVariants.
     * The reason for this is that IPC does not allow you to set variant 
     * conditions in a requirement.
     */
    return true;
  }
}
