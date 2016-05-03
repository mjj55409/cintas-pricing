/*******************************************************************************

Copyright (c) 2004 by SAP AG

All rights to both implementation and design are reserved.

Use and copying of this software and preparation of derivative works based
upon this software are not permitted.

Distribution of this software is restricted. SAP does not make any warranty
about the software, its performance or its conformity to any specification.

*******************************************************************************/
package com.sap.spc.product.exception;

/**
 * @author d036612
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProductIllegalArgumentException extends ProductRuntimeException {

	public ProductIllegalArgumentException(int messageNumber,
			Object[] args, Throwable rootCause) {
		super(messageNumber, args, rootCause);
	}
	
}
