/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/portal/model/db/entities/CdbEntity.java $
 *   $Date: 2016-03-04 15:24:44 -0600 (Fri, 04 Mar 2016) $
 *   $Revision: 1155 $
 *   $Author: djarosz $
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Base class for all CDB entities.
 */
public class CdbEntity implements Serializable, Cloneable {
    
    private transient String viewUUID;
    protected static final long serialVersionUID = 1L;
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public Object getId() {
        return null;
    }
    
    public Object getEntityInfo() {
        return null;
    }
    
    public SearchResult search(Pattern searchPattern) {
        return null;
    }
    
    
    public String getViewUUID() {
        if (viewUUID == null) {
            viewUUID = UUID.randomUUID().toString().replaceAll("[-]", "");
        }
        return viewUUID;
    }
    
}
