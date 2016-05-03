/*

    Copyright (c) 2005 by SAP AG

    All rights to both implementation and design are reserved.

    Use and copying of this software and preparation of derivative works based
    upon this software are not permitted.

    Distribution of this software is restricted. SAP does not make any warranty
    about the software, its performance or its conformity to any specification.

*/

package com.sap.spe.condmgnt.masterdata;

import java.io.Serializable;

import java.util.Map;

import com.sap.spe.conversion.IDimensionalValue;


/**
 * Interface for accessing a scale level.
 */
public interface IScaleLevel extends Serializable {

    /**
     * @return the line id.
     */
    public int getLineId();

    /**
     * @return the amount.
     */
    public IDimensionalValue getAmount(String name);

    public Map getVariablePart();

    public String getVariablePart(String name);
}
