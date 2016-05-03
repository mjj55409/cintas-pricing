/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.sap.spe.pricing.transactiondata.userexit.IPricingDocumentUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.PricingItemCalculateEndFormulaAdapter;

public class SetDynamicTimestamp extends PricingItemCalculateEndFormulaAdapter {
	
	public void calculationEnd(IPricingDocumentUserExit prDocument,
			IPricingItemUserExit prItem) {
		
		/* NOTE: This had its intended effect, but caused a SERIOUS performance issue.
		 * That is why it is no longer configured to run.  Don't put it back unless
		 * you understand the consequences.
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		prItem.setDynamicReturnValue("TIME",sdf.format(new Date()));
	}
}