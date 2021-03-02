/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import java.util.List;

/**
 *
 * @author craig
 */
public class ItemDomanMdHierarchySearchRequest {

    private List<String> itemNames;
    private List<String> rackNames;
    private String rootName;
    
    public List<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }

    public List<String> getRackNames() {
        return rackNames;
    }

    public void setRackNames(List<String> rackNames) {
        this.rackNames = rackNames;
    }

    public String getRootName() {
        return rootName;
    }

    public void setRootName(String rootName) {
        this.rootName = rootName;
    }
    
}
