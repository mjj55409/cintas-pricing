/**
 * @author Jolly Khanna (jollykh@yahoo.com)
 * @version 1.0
 */

package com.cintas.pricing;

import java.math.BigDecimal;

import com.sap.spe.conversion.exc.ConversionMissingDataException;
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
			String conditionType = conditions[i].getConditionTypeName() != null 
					? conditions[i].getConditionTypeName() : "";
			
			if (conditionType.equals("ZPRT")) {
				ZPRT = conditions[i];
			}
		}
		
		if (ZPRT != null) {
			BigDecimal ZPRTRate = ZPRT.getConditionRate().getValue();
			BigDecimal ZPRTValue = ZPRT.getConditionValue().getValue();
			
			if (ZPRTRate != null && ZPRTRate.compareTo(new BigDecimal(0)) > 0) {
				pricingCondition.setConditionRateValue(ZPRTRate);
			}
			
			if (ZPRTValue != null && ZPRTValue.compareTo(new BigDecimal(0)) > 0) {
				pricingCondition.setConditionValue(ZPRTValue);
			}
		}

		/*
		String accountAssignmentGroup = pricingItem.getAttributeValue("ACCOUNT ASSIGNMENT GROUP") != null
				? pricingItem.getAttributeValue("ACCOUNT ASSIGNMENT GROUP") : "";
		*/
//		String ancillaryInsuranceProduct = pricingItem.getAttributeValue("ANCILLARY INSURANCE") != null
//				? pricingItem.getAttributeValue("ANCILLARY INSURANCE") : "";
		String ancillaryMinimumProduct = pricingItem.getAttributeValue("ANCILLARY MINIMUM") != null
				? pricingItem.getAttributeValue("ANCILLARY MINIMUM") : "";
		String ancillaryServiceProduct = pricingItem.getAttributeValue("ANCILLARY SERVICE") != null
						? pricingItem.getAttributeValue("ANCILLARY SERVICE") : "";
		String ancillaryFreightProduct = pricingItem.getAttributeValue("ANCILLARY FREIGHT") != null
				? pricingItem.getAttributeValue("ANCILLARY FREIGHT") : "";				
		String itemProduct = pricingItem.getProduct().getId();		
		
		if (itemProduct != null && itemProduct.equals(ancillaryServiceProduct)) {		
			// Service Charge
			conditions = pricingItem.getUserExitConditions();
			for (int i=0; i<conditions.length; i++) {
				String conditionType = conditions[i].getConditionTypeName();
				if (conditionType != null && conditionType.equals("ZSVC")) {
					pricingCondition.setConditionRateValue(conditions[i].getConditionValue().getValue());
					pricingCondition.setConditionValue(conditions[i].getConditionValue().getValue());
				}
			}
		}
		else if (itemProduct != null && itemProduct.equals(ancillaryMinimumProduct)) {
			// Invoice Minimum
			conditions = pricingItem.getUserExitConditions();
			for (int i=0; i<conditions.length; i++) {
				String conditionType = conditions[i].getConditionTypeName();
				if (conditionType != null && conditionType.equals("ZSTA")) {
					pricingCondition.setConditionRateValue(conditions[i].getConditionValue().getValue());
					pricingCondition.setConditionValue(conditions[i].getConditionValue().getValue());
				}
			}			
		}
		else if (itemProduct != null && itemProduct.equals(ancillaryFreightProduct)) {
			// Freight Charge
			BigDecimal freightCharge = new BigDecimal("0");
			
			conditions = pricingItem.getUserExitConditions();
			for (int i=0; i<conditions.length; i++) {
				if (conditions[i].getConditionCategory() == 'F') {
					// Freight type condition
					freightCharge = freightCharge.add(conditions[i].getConditionValue().getValue());
				}
			}
			
			if (freightCharge.compareTo(new BigDecimal("0")) != 0) {
				pricingCondition.setConditionRateValue(freightCharge);
				pricingCondition.setConditionValue(freightCharge);
			}
		}
		
		/*
		 * Set the condition rate to 0.001 if there are no applicable charges for the ancillary material.
		 * This will prevent the issuing of an error message by value formula 917.
		 * The currency also has to be changed on the condition rate, because otherwise, 0.001 will
		 * be automatically rounded to zero.
		 */
		BigDecimal conditionRate = pricingCondition.getConditionRate().getValue();
		if (conditionRate.compareTo(new BigDecimal("0")) == 0 || conditionRate.compareTo(new BigDecimal("0.001")) == 0) {
			String docCurrency = pricingItem.getUserExitDocument().getDocumentCurrencyUnit().getUnitName();
			String condCurrency = docCurrency;
			
			if (docCurrency != null && docCurrency.equals("USD"))
				condCurrency = "US3";
			else if (docCurrency != null && docCurrency.equals("CAD"))
				condCurrency = "CD3";			
			
			try {
				pricingCondition.setConditionRate(new BigDecimal("0.001"),condCurrency);
			}
			catch (ConversionMissingDataException ex) {
				pricingCondition.setConditionRateValue(new BigDecimal("0.001"));
			}
			return new BigDecimal("0");
		}
		
		return null;
	}
}