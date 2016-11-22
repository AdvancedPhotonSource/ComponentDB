/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.pdmLink.objects;

import gov.anl.aps.cdb.common.objects.CdbObject;
import java.util.LinkedList;

/**
 *
 * @author djarosz
 */
public class PdmLinkSearchResults extends CdbObject {
    
    private LinkedList<PdmLinkSearchResult> searchResults; 

    public LinkedList<PdmLinkSearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(LinkedList<PdmLinkSearchResult> searchResults) {
        this.searchResults = searchResults;
    }
    
    
}
