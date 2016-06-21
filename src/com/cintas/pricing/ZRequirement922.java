package com.cintas.pricing;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement922 extends RequirementAdapter {

  public boolean checkRequirement(IConditionFindingManagerUserExit item,
      IStep step, IAccess access) {
    
    UserexitLogger userexitLogger = new UserexitLogger(ZRequirement922.class);

    String _name = step.getConditionType().getName();
    if (_name == null)
      _name = CintasConstants.INITIAL;
    
    userexitLogger.writeLogDebug("Step = " + step.getStepNumber());
    userexitLogger.writeLogDebug("Condition = " + _name);
    
    if (CintasConstants.IsProductAncillary(item))
      userexitLogger.writeLogDebug("Product is ancillary.");
    
    if (_name.equals(CintasConstants.Conditions.SubTotals.AMOUNT_MIN_CHARGE)) {
      if (!CintasConstants.IsAncillaryService(item))
        if (CintasConstants.IsProductAncillary(item))
          return false;
    }
    else if (_name.equals(CintasConstants.Conditions.LOCAL_PRODUCT_LINE)) {
      if (item.getAttributeValue(CintasConstants.Attributes.MATERIAL_GROUP).equals(CintasConstants.MaterialGroup.TRIM))
        return false;
      
      if (CintasConstants.IsProductAncillary(item))
        return false;
    }
    else {
      if (CintasConstants.IsProductAncillary(item))
        return false;
    }
    
    userexitLogger.writeLogDebug("Requirement satisfied.");
    return true;
  }
}