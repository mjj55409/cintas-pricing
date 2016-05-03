/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import com.sap.spe.pricing.transactiondata.userexit.GroupKeyFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IGroupConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingDocumentUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;

public class ZGroupKeyFormula094 extends GroupKeyFormulaAdapter {
	
	private static final String groupKey = "094";
	
	public String setGroupKey(IPricingDocumentUserExit pricingDocument,
			IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition,
			IGroupConditionUserExit groupCondition) {
		
		String vaKey = groupKey;
		
		String priceProduct = pricingItem.getAttributeValue("PRICE PRODUCT") != null
				? pricingItem.getAttributeValue("PRICE PRODUCT") : "";
		if (!priceProduct.equals(""))
			vaKey += priceProduct;
		else
			vaKey += pricingItem.getProduct().getId();
		
		return vaKey;
	}
}