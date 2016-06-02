package com.cintas.pricing;

import com.sap.spe.base.logging.UserexitLogger;
import com.sap.spe.pricing.transactiondata.userexit.IPricingDocumentUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.PricingItemCalculateEndFormulaAdapter;

import java.math.BigDecimal;

import com.sap.spc.document.ISPCItem;

public class ZItemCalculationFormulaEnd extends PricingItemCalculateEndFormulaAdapter {
  
  public void calculationBegin(IPricingDocumentUserExit prDocument, IPricingItemUserExit prItem) {
    UserexitLogger userexitLogger = new UserexitLogger(ZItemCalculationFormulaEnd.class);
    userexitLogger.writeLogDebug("CalcFormulaEnd -- Begin");
    
    ISPCItem _item = (ISPCItem)prItem;
    
    userexitLogger.writeLogDebug("No Charge = " + prItem.getAttributeValue(CintasConstants.Attributes.NOCHARGE));
    userexitLogger.writeLogDebug(" Zero Qty = " + prItem.getAttributeValue(CintasConstants.Attributes.ZEROQTY));
    userexitLogger.writeLogDebug("Net Value = " + _item.getNetValue());

    if (prItem.getAttributeValue(CintasConstants.Attributes.NOCHARGE).equals(CintasConstants.ABAP_TRUE))
      _item.setStatistical(true);
    else if (prItem.getAttributeValue(CintasConstants.Attributes.ZEROQTY).equals(CintasConstants.ABAP_TRUE))
      if (_item.getNetValue().getValue().compareTo(BigDecimal.ZERO) == 0)
        _item.setStatistical(true);

    userexitLogger.writeLogDebug("CalcFormulaEnd -- End");
  } 
  
}