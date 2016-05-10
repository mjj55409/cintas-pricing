package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement918 extends RequirementAdapter {

  public boolean checkRequirement(IConditionFindingManagerUserExit item,
      IStep step, IAccess access) {
    
    String product = item.getAttributeValue(CintasConstants.Attributes.PRODUCT);
    String conditionType = (step.getConditionType().getName() != null ? step.getConditionType().getName() : CintasConstants.INITIAL);
    
    if (conditionType.equals(CintasConstants.Conditions.SERVICE_CHARGE) || conditionType.equals(CintasConstants.Conditions.SubTotals.SERVICE_CHARGE))
      return product.equals(item.getAttributeValue(CintasConstants.AncillaryMaterials.SERVICE));
    
    if (conditionType.equals(CintasConstants.Conditions.Rules.STOP_MIN) ||
        conditionType.equals(CintasConstants.Conditions.STOP_MIN) ||
        conditionType.equals(CintasConstants.Conditions.ADJ_MINIMUM) ||
        conditionType.equals(CintasConstants.Conditions.SubTotals.MINIMUM_CHARGE))
      return product.equals(item.getAttributeValue(CintasConstants.AncillaryMaterials.MINIMUM));
    
    String usage = item.getAttributeValue(CintasConstants.Attributes.USAGE);
    
    if (usage.equals(CintasConstants.Usage.BUYBACK))
      return false;
    if (usage.equals(CintasConstants.Usage.CHARGES))
      return false;

   return CintasConstants.IsProductAncillary(item);
  }
}