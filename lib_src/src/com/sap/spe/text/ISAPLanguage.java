/*

    Copyright (c) 2005 by SAP AG

    All rights to both implementation and design are reserved.

    Use and copying of this software and preparation of derivative works based
    upon this software are not permitted.

    Distribution of this software is restricted. SAP does not make any warranty
    about the software, its performance or its conformity to any specification.

*/

package com.sap.spe.text;

import java.io.Serializable;


public interface ISAPLanguage extends Serializable {

    /** @return the ISO language code.
     */
    public String getISOLanguageCode();

    /** @return the SAP code of a language.
     */
    public String getSAPLanguageCode();
}
