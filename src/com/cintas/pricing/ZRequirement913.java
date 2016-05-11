package com.cintas.pricing;

import com.sap.spe.condmgnt.customizing.IAccess;
import com.sap.spe.condmgnt.customizing.IStep;
import com.sap.spe.condmgnt.finding.userexit.IConditionFindingManagerUserExit;
import com.sap.spe.condmgnt.finding.userexit.RequirementAdapter;

public class ZRequirement913 extends RequirementAdapter {

	public boolean checkRequirement(IConditionFindingManagerUserExit item,
			IStep step, IAccess access) {
		
	  if (CintasConstants.IsProductAncillary(item))
	    return false;
	  
	  /*
	   * PROBLEM: ZIPD and ZIRL are not determined at the time that this requirement is run!!!
	   * That means that the code to retrieve insurance program information is totally broken.
	   * 
		// Type cast item to get access to pricing condition table
		IPricingItemUserExit pricingItem = (IPricingItemUserExit)item;

		// Initialize values that are stored on other condition records
		String insuranceProgram = "";
		String makeupExclusion = "";
		String trimExclusion = "";


		// Get condition type ZIPD from the conditions table
		IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
		for (int i=0; i<conditions.length; i++) {
			String conditionType = conditions[i].getConditionTypeName();
			if (conditionType != null && conditionType.equals("ZIPD")) {
				insuranceProgram = conditions[i].getConditionRecord().getVariableDataValue("ZZ_INSURP") != null ? conditions[i].getConditionRecord().getVariableDataValue("ZZ_INSURP") : "";
			}
			if (conditionType != null && conditionType.equals("ZIRL")) {
				// Allow ZIRL record to override insurance program from ZIPD
				String ZIRLInsuranceProgram = conditions[i].getConditionRecord().getVariableDataValue("ZZ_INSURP");
				if (ZIRLInsuranceProgram != null && !ZIRLInsuranceProgram.equals(""))
					insuranceProgram = ZIRLInsuranceProgram;

				makeupExclusion = conditions[i].getConditionRecord().getVariableDataValue("ZZ_MUINS_EX") != null ? conditions[i].getConditionRecord().getVariableDataValue("ZZ_MUINS_EX") : "";
				trimExclusion = conditions[i].getConditionRecord().getVariableDataValue("ZZ_TRINS_EX") != null ? conditions[i].getConditionRecord().getVariableDataValue("ZZ_TRINS_EX") : "";
				break;
			}
		}

		// Allow line item insurance program to override determined value
		String itemInsuranceProgram = item.getAttributeValue("INSURANCE PROGRAM") != null ? item.getAttributeValue("INSURANCE PROGRAM") : "";
		if (!itemInsuranceProgram.equals(""))
			insuranceProgram = itemInsuranceProgram;
	   */
		
		// Get additional information from item communication structure
		String insuranceIndicator = item.getAttributeValue("INSURANCE") != null ? item.getAttributeValue("INSURANCE") : "";
		String usageCode = item.getAttributeValue("USAGE") != null ? item.getAttributeValue("USAGE") : "";
		String makeupInsurance = item.getAttributeValue("MAKEUP INSURANCE") != null ? item.getAttributeValue("MAKEUP INSURANCE") : "";
		String trimInsurance = item.getAttributeValue("TRIM INSURANCE") != null ? item.getAttributeValue("TRIM INSURANCE") : "";
		String pricedTrim = item.getAttributeValue("PRICED TRIM") != null ? item.getAttributeValue("PRICED TRIM") : "";
		
		// These attributes are now being determined in ABAP.
		String insuranceProgram = item.getAttributeValue("INSURANCE PROGRAM") != null ? item.getAttributeValue("INSURANCE PROGRAM") : "";
		String makeupExclusion = item.getAttributeValue("MAKEUP EXCLUSION") != null ? item.getAttributeValue("MAKEUP EXCLUSION") : "";
		String trimExclusion = item.getAttributeValue("TRIM EXCLUSION") != null ? item.getAttributeValue("TRIM EXCLUSION") : "";
		
		String conditionType = (step.getConditionType().getName() != null ? step.getConditionType().getName() : CintasConstants.INITIAL);
		
		if (conditionType.equals(CintasConstants.Conditions.INSURANCE_PROGRAM)) {
			// Insurance program must be maintained on the line item
			if (CintasConstants.IsAttributeInitial(item, CintasConstants.Attributes.INSURANCE))
				return false;
			
			String usage = item.getAttributeValue(CintasConstants.Attributes.USAGE);
			if (!(usage.equals(CintasConstants.Usage.RENTAL) ||
			    usage.equals(CintasConstants.Usage.LOST)   ||
			    usage.equals(CintasConstants.Usage.UNILEASE) ||
			    usage.equals(CintasConstants.Usage.DESTROY)  ||
			    usage.equals(CintasConstants.Usage.CHARGES)))
			  return false;
			
		}
		else if (conditionType.equals(CintasConstants.Conditions.Rules.INSURANCE)) {
          String usage = item.getAttributeValue(CintasConstants.Attributes.USAGE);
          if (!(usage.equals(CintasConstants.Usage.RENTAL) ||
              usage.equals(CintasConstants.Usage.LOST)   ||
              usage.equals(CintasConstants.Usage.UNILEASE) ||
              usage.equals(CintasConstants.Usage.DESTROY)  ||
              usage.equals(CintasConstants.Usage.CHARGES)))
            return false;
          
		}
		else if (conditionType.equals("ZICH") || conditionType.equals("ZRIC")) {
			// Insurance Program must be 10 or 20
			if (!insuranceProgram.equals("10") && !insuranceProgram.equals("20"))
				return false;
			
			// Usage code must be in 0P
			if (usageCode.equals("") || "0P".indexOf(usageCode) < 0)
				return false;	
		}
		else if (conditionType.equals("ZIPS")) {
			// Insurance Program must be 12 or 22
			if (!insuranceProgram.equals("12") && !insuranceProgram.equals("22"))
				return false;
			
			// Usage code must be in 0P
			if (usageCode.equals("") || "0P".indexOf(usageCode) < 0)
				return false;	
		}
		else if (conditionType.equals("ZIPI")) {
			// Insurance Program must be 11 or 21 or 30 with usage 0 or P 
			if (!insuranceProgram.equals("11") && !insuranceProgram.equals("21") && !insuranceProgram.equals("30")) 
				return false;

			if (usageCode.equals("") || "0P".indexOf(usageCode) < 0)
				return false;
		}
		else if (conditionType.equals("ZIMU") || conditionType.equals("ZRMU")) {
			// Makeup Insurance flag must be set
			if (!makeupInsurance.equals("X"))
				return false;
			
			// Makeup Exclusion flag must not be set
			if (!makeupExclusion.equals(""))
				return false;
			
			// Usage code must be in 0P
			if (usageCode.equals("") || "0P".indexOf(usageCode) < 0)
				return false;		
		}
		else if (conditionType.equals("ZITR") || conditionType.equals("ZRTR")) {
			// Trim Insurance and Priced Trim flags must be set
			if (!trimInsurance.equals("X") || pricedTrim.equals(""))
				return false;
			
			// Trim Exclusion flag must not be set
			if (!trimExclusion.equals(""))
				return false;
			
			// Usage code must be in 0P
			if (usageCode.equals("") || "0P".indexOf(usageCode) < 0)
				return false;				
		}
		else if (conditionType.equals("ZIND")) {
			if (usageCode.equals("L")) {
				// Insurance Program must be 20, 21, or 22
				if (!insuranceProgram.equals("20") 
						&& !insuranceProgram.equals("21")
						&& !insuranceProgram.equals("22"))
					return false;
			}
			else if (usageCode.equals(CintasConstants.Usage.DESTROY)) {
				// Insurance Program must be 10, 11, 12, 20, 21, or 22
			  if (!CintasConstants.IsInsuranceVantage(item) && !CintasConstants.IsInsuranceVantagePlus(item))
					return false;				
			}
			else if (usageCode.equals(CintasConstants.Usage.CHARGES)) {
				// Makeup Insurance or Trim Insurance must be available
				if ((makeupInsurance.equals(CintasConstants.ABAP_TRUE) && makeupExclusion.equals(CintasConstants.INITIAL))
						|| (trimInsurance.equals(CintasConstants.ABAP_TRUE) && trimExclusion.equals(CintasConstants.INITIAL)))
					return true;
				else
					return false;
			}
			else {
				return false;
			}
		}
		
		return true;
	}
}
