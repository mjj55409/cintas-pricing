/*

    Copyright (c) 2005 by SAP AG

    All rights to both implementation and design are reserved.

    Use and copying of this software and preparation of derivative works based
    upon this software are not permitted.

    Distribution of this software is restricted. SAP does not make any warranty
    about the software, its performance or its conformity to any specification.

*/

package com.sap.spe.pricing.transactiondata.userexit.pricingcopy;

import com.sap.spe.conversion.IQuantityValue;
import com.sap.spe.pricing.customizing.ICopyType;
import com.sap.spe.pricing.customizing.IPricingType;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingDocumentUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.PricingCopyFormulaAdapter;


/**
 * @author d028100
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HandleHeaderConditions extends PricingCopyFormulaAdapter {
    public void pricingCopy(IPricingDocumentUserExit pricingDocument, IPricingItemUserExit pricingItem,
        IPricingConditionUserExit pricingCondition, IPricingType pricingType, ICopyType copyType,
        IQuantityValue sourceSalesQuantity) {

        // take care of the itemconditions which were created due to a manual headercondition
        // logic comparable with R/3 logic in function module 'PRICING_COPY'
        if ((pricingCondition.getCalculationType() == PricingCustomizingConstants.CalculationType.FIXED_AMOUNT)
                || PricingCustomizingConstants.Control.isFixedValueOrCostOrBilled(
                    pricingCondition.getConditionControl())) {
            if ((pricingCondition.getOrigin() == PricingCustomizingConstants.Origin.HEADERCONDITION)
                    || (pricingCondition.getConditionControl() == PricingCustomizingConstants.Control.FIXED)) {
                pricingCondition.setGroupConditionIndicator(false);

                if (!PricingCustomizingConstants.Control.isFixedDueToCostOrBilledItem(
                            pricingCondition.getConditionControl())) {
                    pricingCondition.setConditionControl(PricingCustomizingConstants.Control.VALUE_AND_BASE_FIXED);
                }
            }
        }

        if (pricingCondition.getOrigin() == PricingCustomizingConstants.Origin.HEADERCONDITION) {
            pricingCondition.setOrigin(PricingCustomizingConstants.Origin.EX_HEADERCONDITION);
        }
    }
}
