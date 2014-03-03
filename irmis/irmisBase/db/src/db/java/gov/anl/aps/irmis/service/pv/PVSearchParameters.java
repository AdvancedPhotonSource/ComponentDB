/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.service.pv;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// persistence layer
import gov.anl.aps.irmis.persistence.pv.IOCBoot;
import gov.anl.aps.irmis.persistence.pv.IOCResource;
import gov.anl.aps.irmis.persistence.pv.RecordType;


/**
 * Container for process variable search parameters.
 */
public class PVSearchParameters {

    private List iocBootList = new ArrayList();
    private List iocResourceList = new ArrayList();
    private List recTypeList = new ArrayList();
    private String recNameGlob = null;
    private String fieldNameGlob = null;
    private String fieldValueGlob = null;

	public PVSearchParameters() {
	}

    public List getIocBootList() {
        return iocBootList;
    }
    public void setIocBootList(List value) {
        iocBootList = value;
    }
    public void addIocBoot(IOCBoot ib) {
        iocBootList.add(ib);
    }

    public List getIocResourceList() {
        return iocResourceList;
    }
    public void setIocResourceList(List value) {
        iocResourceList = value;
    }
    public void addIocResource(IOCResource ir) {
        iocResourceList.add(ir);
    }


    public List getRecordTypeList() {
        return recTypeList;
    }
    public void setRecordTypeList(List value) {
        recTypeList = value;
    }
    public void addRecordType(RecordType type) {
        recTypeList.add(type);
    }

    public String getRecNameGlob() {
        return recNameGlob;
    }
    public void setRecNameGlob(String val) {
        recNameGlob = val;
    }

    public String getFieldNameGlob() {
        return fieldNameGlob;
    }
    public void setFieldNameGlob(String val) {
        fieldNameGlob = val;
    }

    public String getFieldValueGlob() {
        return fieldValueGlob;
    }
    public void setFieldValueGlob(String val) {
        fieldValueGlob = val;
    }

}
