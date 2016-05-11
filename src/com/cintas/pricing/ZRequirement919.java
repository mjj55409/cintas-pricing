package com.cintas.pricing;

import java.math.BigInteger;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement919 extends RequirementAdapter {

  public boolean checkRequirement(IConditionFindingManagerUserExit item,
      IStep step, IAccess access) {
    
    if (CintasConstants.IsProductAncillary(item))
      return false;

    if ((new BigInteger(item.getAttributeValue(CintasConstants.Attributes.LR_QTY)).compareTo(BigInteger.ZERO) == 0))
      return false;
    
    return true;
  }
}