package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement916 extends RequirementAdapter {

  public boolean checkRequirement(IConditionFindingManagerUserExit item,
      IStep step, IAccess access) {
    
    return !CintasConstants.IsProductAncillary(item);
  }
}