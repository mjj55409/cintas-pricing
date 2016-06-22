package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.conversion.exc.ConversionMissingDataException;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula918 extends ValueFormulaAdapter {

	public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
			IPricingConditionUserExit pricingCondition) {
	  
	  BigDecimal kbetr = BigDecimal.ZERO;
	  BigDecimal kwert = BigDecimal.ZERO;

	  String conditionType = (pricingCondition.getConditionTypeName() != null
	      ? pricingCondition.getConditionTypeName() : CintasConstants.INITIAL);

	  if (conditionType.equals(CintasConstants.Conditions.SubTotals.SIZE_PREMIUM)) {
	    
	    if (CintasConstants.IsItemNoCharge(pricingItem)) {
	      kbetr = BigDecimal.ZERO;
	      kwert = BigDecimal.ZERO;
        }
	    else {
	      kbetr = CintasConstants.GetConditionRate(pricingItem, CintasConstants.Conditions.SIZE_PREMIUM);
          kwert = CintasConstants.GetConditionValue(pricingItem, CintasConstants.Conditions.SIZE_PREMIUM);
	    }
        try {
          String currency = CintasConstants.GetPrecisionCurrency(pricingItem.getUserExitDocument().getDocumentCurrencyUnit().getUnitName());
          pricingCondition.setConditionRate(kbetr, currency);
        }
        catch (ConversionMissingDataException ex) {
          pricingCondition.setConditionRateValue(kbetr);
        }

	  }
	  else if (conditionType.equals(CintasConstants.Conditions.SubTotals.INSURANCE)) { 
	    // Insurance
	    BigDecimal subtotal3 = pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_3);
	    if (subtotal3 != null && subtotal3.compareTo(BigDecimal.ZERO) != 0)
	      kwert = subtotal3;
	  }
	  else if (conditionType.equals(CintasConstants.Conditions.SubTotals.SERVICE_CHARGE)) {
	    // Service Charge
	    BigDecimal netValue = pricingItem.getNetValueAsBigDecimal();
	    if (netValue != null && netValue.compareTo(BigDecimal.ZERO) != 0)
	      kwert = netValue;
	  }
	  else if (conditionType.equals(CintasConstants.Conditions.SubTotals.MINIMUM_CHARGE)) {
	    // Min Charge
	    BigDecimal netValue = pricingItem.getNetValueAsBigDecimal();
	    if (netValue != null && netValue.compareTo(BigDecimal.ZERO) != 0)
	      kwert = netValue;
	  }
	  else if (conditionType.equals(CintasConstants.Conditions.SubTotals.AMOUNT_SERV_CHARGE)) {
	    BigDecimal subtotal1 = pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_1);
	    if (subtotal1 != null && subtotal1.compareTo(BigDecimal.ZERO) != 0) {
	      kwert = subtotal1;
	    }
	  }
	  else if (conditionType.equals(CintasConstants.Conditions.SubTotals.AMOUNT_MIN_CHARGE)) {
	    BigDecimal subtotal5 = pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_5);
	    if (subtotal5 != null && subtotal5.compareTo(BigDecimal.ZERO) != 0) {
	      kwert = subtotal5;
	    }			
	  }
	  //CR912
	  else if (conditionType.equals(CintasConstants.Conditions.SubTotals.DISCOUNTED_PRICE)) {
	    kwert = pricingItem.getNetValueAsBigDecimal().subtract(pricingItem.getSubtotalAsBigDecimal(PricingCustomizingConstants.ConditionSubtotal.SUBTOTAL_3));
	  }
	  //CR912

	  return kwert;
	}
}