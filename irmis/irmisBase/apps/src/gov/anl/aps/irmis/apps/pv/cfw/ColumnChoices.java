/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.pv.cfw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class ColumnChoices {

    private boolean recordName = true;
    private boolean recordType = true;
    private boolean ioc = false;
    private boolean system = false;
    private String field1;
    private String field2;
    private String field3;

    /**
     * Return array of strings representing the column names
     * that correspond to the choices for column display.
     */
    public String[] getColumnNames() {
        ArrayList colNames = new ArrayList();
        if (recordName)
            colNames.add("Record Name");
        if (recordType)
            colNames.add("Type");
        if (ioc)
            colNames.add("Ioc");
        if (system)
            colNames.add("System");
        if (field1 != null && field1.length() > 0)
            colNames.add(field1);
        if (field2 != null && field2.length() > 0)
            colNames.add(field2);
        if (field3 != null && field3.length() > 0)
            colNames.add(field3);
        return (String[])colNames.toArray(new String[0]);
    }


    public boolean getRecordName() {
        return recordName;
    }

    public boolean getRecordType() {
        return recordType;
    }
    public void setRecordType(boolean value) {
        recordType = value;
    }

    public boolean getIoc() {
        return ioc;
    }
    public void setIoc(boolean value) {
        ioc = value;
    }

    public boolean getSystem() {
        return system;
    }
    public void setSystem(boolean value) {
        system = value;
    }

    public String getField1() {
        return field1;
    }
    public void setField1(String value) {
        field1 = value;
    }

    public String getField2() {
        return field2;
    }
    public void setField2(String value) {
        field2 = value;
    }

    public String getField3() {
        return field3;
    }
    public void setField3(String value) {
        field3 = value;
    }

}
