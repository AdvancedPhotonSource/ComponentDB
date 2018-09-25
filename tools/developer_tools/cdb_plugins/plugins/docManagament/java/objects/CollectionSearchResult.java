/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament.objects;

import gov.anl.aps.cdb.common.objects.CdbObject;
import java.util.List;

/**
 *
 * @author djarosz
 */
public class CollectionSearchResult extends CdbObject{
    
    private Integer total; 
    private List<Collection>  docs; 

    public CollectionSearchResult(Integer total, List<Collection> docs) {
        this.total = total;
        this.docs = docs;
    }

    public List<Collection> getDocs() {
        return docs;
    }

    public Integer getTotal() {
        return total;
    }
    
}
