/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.pricing.transactiondata.userexit.GroupKeyFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IGroupConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingDocumentUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZGroupKeyFormula093 extends GroupKeyFormulaAdapter {

	/* Use a constant group value to group all ancillary items together.
	 * Non-ancillary items and other non-qualifying cases will be 
	 * grouped into a blank group with no value assigned.
	 */
	private static final String groupKey = "093";
	
	public String setGroupKey(IPricingDocumentUserExit pricingDocument,
			IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition,
			IGroupConditionUserExit groupCondition) {
		
		String materialGroup = pricingItem.getAttributeValue("MATERIAL GROUP");
		String accountAssignmentGroup = pricingItem.getAttributeValue("ACCOUNT ASSIGNMENT GROUP") != null
				? pricingItem.getAttributeValue("ACCOUNT ASSIGNMENT GROUP") : "";
		String usage = pricingItem.getAttributeValue("USAGE") != null
				? pricingItem.getAttributeValue("USAGE") : "";
		char relevantSubtotal = ' ';
		
		// Don't group non-ancillary items
		if (materialGroup == null || !materialGroup.equals("ANC"))
			return "";
		
		if (accountAssignmentGroup.equals("24")) {
			// Insurance
			if (!usage.equals("0"))
				return "";
			
			relevantSubtotal = 'E';
		}
		else if (accountAssignmentGroup.equals("25")) {
			// Service Charge
			if (!usage.equals("0"))
				return "";
			
			relevantSubtotal = 'J';
		}
		else if (accountAssignmentGroup.equals("26")) {
			// Invoice Minimum
			if (!usage.equals("0"))
				return "";
			
			relevantSubtotal = 'G';
		}
		else if (accountAssignmentGroup.equals("27")) {
			// Freight
			if (!usage.equals("D"))
				return "";
			
			relevantSubtotal = 'D';
		}
		else {
			return "";
		}
		
		/* The original ECC code relied on collecting conditions from all items
		 * that belong to a particular subtotal; however, in IPC, there
		 * doesn't seem to be a method for retrieving the subtotal character
		 * of a particular pricing procedure step.  We could add all item subtotals,
		 * except that some of the time, the code adds the base value instead
		 * of the final value.
		 */
		BigDecimal newValue = new BigDecimal(0);
		
		IPricingConditionUserExit[] conditions = pricingDocument.getUserExitConditions();
		for (int i=0; i<conditions.length; i++) {
			String conditionType = conditions[i].getConditionTypeName() != null 
					? conditions[i].getConditionTypeName() : "";
			
			switch (relevantSubtotal) {
				case 'E':
					if (!conditionType.equals("ZICH")
							&& !conditionType.equals("ZIPI")
							&& !conditionType.equals("ZIPS")
							&& !conditionType.equals("ZIMU")
							&& !conditionType.equals("ZITR"))
						continue;
					break;
			
				case 'G':
					// Invoice Min/Max
					if (!conditionType.equals("ZST$"))
						continue;
					break;
					
				case 'D':
					// Freight Charges
					if (!conditionType.equals("ZBPR"))
						continue;
					break;
					
				case 'J':
					if (!conditionType.equals("ZSVC"))
						continue;
					break;
					
				default:
					continue;
			}
					
			if ((conditions[i].getCalculationType() == 'A') && !accountAssignmentGroup.equals("24")) {
				// Percentage Condition (but not insurance)
				newValue = newValue.add(conditions[i].getConditionBase().getValue());
			}
			else {
				// Not a Percentage Condition
				newValue = newValue.add(conditions[i].getConditionValue().getValue());
			}
		}
		
		if (newValue != null && newValue.compareTo(new BigDecimal(0)) > 0) {
			pricingCondition.setConditionRateValue(newValue);
			pricingCondition.setConditionValue(newValue);
		}
		
		return groupKey;
	}
}
