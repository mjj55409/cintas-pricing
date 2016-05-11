package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.conversion.exc.ConversionMissingDataException;
import com.sap.spe.pricing.customizing.PricingCustomizingConstants;
import com.sap.spe.pricing.transactiondata.userexit.IPricingConditionUserExit;
import com.sap.spe.pricing.transactiondata.userexit.IPricingItemUserExit;
import com.sap.spe.pricing.transactiondata.userexit.ValueFormulaAdapter;

public class ZValueFormula924 extends ValueFormulaAdapter {

  public BigDecimal overwriteConditionValue(IPricingItemUserExit pricingItem,
      IPricingConditionUserExit pricingCondition) {

    pricingItem.setCalculationDuringPricingCompleteRequired(true);

    IPricingConditionUserExit ZPRT = null;

    // Get ZPRT condition record from the conditions table
    IPricingConditionUserExit[] conditions = pricingItem.getUserExitConditions();
    for (int i=0; i<conditions.length; i++) {
      String conditionType = (conditions[i].getConditionTypeName() != null 
          ? conditions[i].getConditionTypeName() : CintasConstants.INITIAL);

      if (conditionType.equals(CintasConstants.Conditions.PROGRAM_TOTAL)) {
        ZPRT = conditions[i];
        break;
      }
    }
    
    if (ZPRT != null) {
      BigDecimal ZPRTRate = ZPRT.getConditionRate().getValue();
      BigDecimal ZPRTValue = ZPRT.getConditionValue().getValue();
      
      pricingCondition.setConditionRateValue(ZPRTRate);
      pricingCondition.setConditionValue(ZPRTValue);
    }

    if (ZPRT.getConditionValue().getValue().compareTo(BigDecimal.ZERO) > 0) {
      String itemProduct = pricingItem.getProduct().getId();
  
      if (itemProduct.equals(pricingItem.getAttributeValue(CintasConstants.AncillaryMaterials.SERVICE))) {
        conditions = pricingItem.getUserExitConditions();
        for (int i=0; i<conditions.length; i++) {
          String conditionType = conditions[i].getConditionTypeName();
          if (conditionType != null && conditionType.equals(CintasConstants.Conditions.SERVICE_CHARGE)) {
            pricingCondition.setConditionRateValue(conditions[i].getConditionValue().getValue());
            pricingCondition.setConditionValue(conditions[i].getConditionValue().getValue());
          }
        }
        
      } 
      else if (itemProduct.equals(pricingItem.getAttributeValue(CintasConstants.AncillaryMaterials.MINIMUM))) {		  
        conditions = pricingItem.getUserExitConditions();
        for (int i=0; i<conditions.length; i++) {
          String conditionType = conditions[i].getConditionTypeName();
          if (conditionType != null && conditionType.equals(CintasConstants.Conditions.ADJ_MINIMUM)) {
            pricingCondition.setConditionRateValue(conditions[i].getConditionValue().getValue());
            pricingCondition.setConditionValue(conditions[i].getConditionValue().getValue());
          }
        }
        
      } 
      else if (itemProduct.equals(pricingItem.getAttributeValue(CintasConstants.AncillaryMaterials.FREIGHT))) {
        BigDecimal freightCharge = BigDecimal.ZERO;
  
        conditions = pricingItem.getUserExitConditions();
        for (int i=0; i<conditions.length; i++) {
          if (conditions[i].getConditionCategory() == PricingCustomizingConstants.Category.FREIGHT) {
            freightCharge = freightCharge.add(conditions[i].getConditionValue().getValue());
          }
        }
  
        if (freightCharge.compareTo(BigDecimal.ZERO) != 0) {
          pricingCondition.setConditionRateValue(freightCharge);
          pricingCondition.setConditionValue(freightCharge);
        }
        
      }
    }

    /*
     * Set the condition rate to 0.001 if there are no applicable charges for the ancillary material.
     * This will prevent the issuing of an error message by value formula 917.
     * The currency also has to be changed on the condition rate, because otherwise, 0.001 will
     * be automatically rounded to zero.
     */
    BigDecimal conditionRate = pricingCondition.getConditionRate().getValue();
    if (conditionRate.compareTo(BigDecimal.ZERO) == 0 || conditionRate.compareTo(CintasConstants.ONE_PENNY) == 0) {
      try {
        pricingCondition.setConditionRate(
            CintasConstants.ONE_PENNY, 
            CintasConstants.GetPrecisionCurrency(pricingItem.getUserExitDocument().getDocumentCurrencyUnit().getUnitName()));
      }
      catch (ConversionMissingDataException ex) {
        pricingCondition.setConditionRateValue(CintasConstants.ONE_PENNY);
      }
      return BigDecimal.ZERO;
    }

    return null;
  }
}