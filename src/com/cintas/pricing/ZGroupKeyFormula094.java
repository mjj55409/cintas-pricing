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

    if (pricingItem.getAttributeValue(CintasConstants.Attributes.PRICE_PRODUCT) != null) {
      vaKey += pricingItem.getAttributeValue(CintasConstants.Attributes.PRICE_PRODUCT);
    } else {
      vaKey += pricingItem.getProduct().getId();
    }

    return vaKey;
  }
}