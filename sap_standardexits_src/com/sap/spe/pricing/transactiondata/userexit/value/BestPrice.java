/*

    Copyright (c) 2005 by SAP AG

    All rights to both implementation and design are reserved.

    Use and copying of this software and preparation of derivative works based
    upon this software are not permitted.

    Distribution of this software is restricted. SAP does not make any warranty
    about the software, its performance or its conformity to any specification.

*/

package com.sap.spe.pricing.transactiondata.userexit.value;

import java.math.BigDecimal;

import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.PricingTransactiondataConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;


/**
 * @author d028100
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BestPrice extends ValueFormulaAdapter {
    public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
        IPricingConditionUserExit pricingCondition) {
        BigDecimal condValue = pricingCondition.getConditionValue().getValue();
        BigDecimal netValue = pricingItem.getNetValue().getValue();

        if (!pricingItem.isReturn()) {
            if ((condValue.compareTo(netValue) > 0) && (netValue.compareTo(PricingTransactiondataConstants.ZERO) != 0)) {
                pricingCondition.setInactive(PricingCustomizingConstants.InactiveFlag.INACTIVE_DUE_TO_ERROR);

                return PricingTransactiondataConstants.ZERO;
            }
        }
        else {
            if ((condValue.compareTo(netValue) <= 0) && (netValue.compareTo(PricingTransactiondataConstants.ZERO) != 0)) {
                pricingCondition.setInactive(PricingCustomizingConstants.InactiveFlag.INACTIVE_DUE_TO_ERROR);

                return PricingTransactiondataConstants.ZERO;
            }
        }

        return null;
    }
}
