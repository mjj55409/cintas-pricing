/*

    Copyright (c) 2005 by SAP AG

    All rights to both implementation and design are reserved.

    Use and copying of this software and preparation of derivative works based
    upon this software are not permitted.

    Distribution of this software is restricted. SAP does not make any warranty
    about the software, its performance or its conformity to any specification.

*/

package com.sap.sxe.db;

public class SqlEvent extends java.util.EventObject {
    private String m_sqlString = null;

    /** Constructs a new instance.
     *        @param source the creator object of this event.
     */
    public SqlEvent(Object source, String sqlString) {
        super(source);
        m_sqlString = sqlString;
    }

    public String getSQLString() {
        return m_sqlString;
    }
}
