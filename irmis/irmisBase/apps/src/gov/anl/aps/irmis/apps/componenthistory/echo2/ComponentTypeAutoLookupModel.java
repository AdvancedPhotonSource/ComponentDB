package gov.anl.aps.irmis.apps.componenthistory.echo2;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// EchoPointNG
import echopointng.text.AutoLookupModel;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentType;


/**
 * Model representing set of component types that can be searched
 * by entering substrings. Displayed list is reduced by search. This
 * model is used by EchoPointNG AutoLookupTextFieldEx component.
 */
class ComponentTypeAutoLookupModel implements AutoLookupModel {
    
    private List componentTypeList = null;
    
    public List getComponentTypeList() {
        return componentTypeList;
    }
    public void setComponentTypeList(List componentTypeList) {
        this.componentTypeList = componentTypeList;
    }
    
    public int getMatchOptions() {
        return 0;
    }
    
    public int getMaximumCacheAge() {
        return -1;
    }
    
    public int getMaximumCacheSize() {
        return -1;
    }
	
    public Entry[] prePopulate() {
        
        List entries = new ArrayList();
        if (componentTypeList != null && componentTypeList.size() > 0) {
            Iterator it = componentTypeList.iterator();
            while (it.hasNext()) {
                ComponentType ct = (ComponentType)it.next();
                entries.add(new DefaultEntry(ct.getComponentTypeName()));
            }
        }
        return (Entry[]) entries.toArray(new Entry[entries.size()]);
    }
    
    public Entry[] searchEntries(String partialSearchValue, int matchOptions) {
        boolean caseSensitive = (matchOptions & AutoLookupModel.MATCH_IS_CASE_SENSITIVE) == AutoLookupModel.MATCH_IS_CASE_SENSITIVE;
        boolean matchFromStart = (matchOptions & AutoLookupModel.MATCH_ONLY_FROM_START) == AutoLookupModel.MATCH_ONLY_FROM_START;
        List matches = new ArrayList();
		
        if (! caseSensitive) {
            partialSearchValue = partialSearchValue.toUpperCase();
        }
        
        if (componentTypeList != null && componentTypeList.size() > 0) {
            Iterator it = componentTypeList.iterator();
            while (it.hasNext()) {
                ComponentType ct = (ComponentType)it.next();
                String value = ct.getComponentTypeName();
                String comparisonValue = value;
                if (!caseSensitive)
                    comparisonValue = value.toUpperCase();
                
                if (matchFromStart) {
                    if (partialSearchValue.length() == 0 ||
                        comparisonValue.indexOf(partialSearchValue) == 0) {
                        matches.add(new DefaultEntry(value));
                    }
                } else {
                    if (partialSearchValue.length() == 0 ||
                        comparisonValue.indexOf(partialSearchValue) != -1) {
                        matches.add(new DefaultEntry(value));
                    }
                }
            }
        }
		
        return (Entry[]) matches.toArray(new Entry[matches.size()]);
    }
}