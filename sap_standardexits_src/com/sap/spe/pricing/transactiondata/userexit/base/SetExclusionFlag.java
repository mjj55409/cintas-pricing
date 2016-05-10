/*

    Copyright (c) 2005 by SAP AG

    All rights to both implementation and design are reserved.

    Use and copying of this software and preparation of derivative works based
    upon this software are not permitted.

    Distribution of this software is restricted. SAP does not make any warranty
    about the software, its performance or its conformity to any specification.

*/

package com.sap.spe.pricing.transactiondata.userexit.base;

import java.math.BigDecimal;

import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.BaseFormulaAdapter;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;


/**
 * @author d028100
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetExclusionFlag extends BaseFormulaAdapter {
    public BigDecimal overwriteConditionBase(IPricingItemUserExit pricingItem,
        IPricingConditionUserExit pricingCondition) {
        char inactiveFlag = pricingCondition.getInactive();

        if ((inactiveFlag == PricingCustomizingConstants.InactiveFlag.NOT_INACTIVE)
                || (inactiveFlag == PricingCustomizingConstants.InactiveFlag.INACTIVE_DUE_TO_SUBSEQUENT_PRICE)
                || (inactiveFlag == PricingCustomizingConstants.InactiveFlag.INACTIVE_DUE_TO_STATISTICAL_ITEM)) {
            pricingItem.setExclusionFlag('$');
        }

        return null;
    }
}