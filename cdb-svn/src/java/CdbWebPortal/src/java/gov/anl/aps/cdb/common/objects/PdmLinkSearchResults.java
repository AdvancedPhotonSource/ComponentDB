/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.common.objects;

import java.util.LinkedList;
import java.util.List;

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
