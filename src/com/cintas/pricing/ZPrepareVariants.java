package com.cintas.pricing;

import com.sap.spe.document.userexit.IItemUserExitAccess;
import com.sap.spe.document.userexit.PricingPrepareFormulaAdapter;

public class ZPrepareVariants extends PricingPrepareFormulaAdapter {

  public void addAttributeBindings(IItemUserExitAccess itemUserExitAccess) {

    String[] specialHandlingValues = itemUserExitAccess.getAttributeBinding(CintasConstants.Attributes.SPECIAL_HANDLING).getValues();
    String[] packageCodeValues = itemUserExitAccess.getAttributeBinding(CintasConstants.Attributes.PACKAGE_CODE).getValues();

    // Assume that there is only one value maintained for each attribute
    if (specialHandlingValues != null && specialHandlingValues.length > 0) {
      /* Added for Unexplained prices -- check to Tnnn */
      if (specialHandlingValues[0].startsWith("T")) {
        //
        itemUserExitAccess.addVariantCondition(specialHandlingValues[0], 1.0d, specialHandlingValues[0]);
      } else {
        /* */
        // Split at "/" into multiple fields
        String[] specialHandlingArray = specialHandlingValues[0].split("/");
        for (int i=0; i<specialHandlingArray.length; i++) {
          String specialHandling;
//          if (specialHandlingArray[i].startsWith("H"))
            specialHandling = specialHandlingArray[i];
//          else
//            specialHandling = "H" + specialHandlingArray[i];
          itemUserExitAccess.addVariantCondition(specialHandling, 1.0d, "Surcharge:" + specialHandling);
        }
      }
    }

    if (packageCodeValues != null && packageCodeValues.length > 0) {
      String packageCode = "P" + packageCodeValues[0];
      itemUserExitAccess.addVariantCondition(packageCode, 1.0d, "Surcharge:" + packageCode);
    }

    /* It would be nice to add friendly descriptions, like in this example,
     * but I'm not sure how we would get them in here.  They're not even
     * stored in CRM. They are only in ECC table TVARC.
     */
    //itemUserExitAccess.addVariantCondition("HFF", 1.0d, "Fabric Freshener");
    //itemUserExitAccess.addVariantCondition("PBX", 1.0d, "Box");

  }
}
