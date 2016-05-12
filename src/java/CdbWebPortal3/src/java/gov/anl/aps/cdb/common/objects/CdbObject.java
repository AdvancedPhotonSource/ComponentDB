/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/common/objects/CdbObject.java $
 *   $Date: 2015-04-17 12:25:03 -0500 (Fri, 17 Apr 2015) $
 *   $Revision: 594 $
 *   $Author: sveseli $
 */
package gov.anl.aps.cdb.common.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import java.io.Serializable;

/**
 * Base CDB object class.
 */
public class CdbObject implements Serializable {

    protected Long id = null;
    protected String name = null;
    protected String description = null;

    public CdbObject() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Conversion to JSON string representation.
     *
     * @return JSON string
     */
    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    /**
     * Encode object.
     *
     * @throws CdbException in case of any errors
     */
    public void encode() throws CdbException {
    }

    /**
     * Decode object.
     *
     * @throws CdbException in case of any errors
     */
    public void decode() throws CdbException {
    }
}
