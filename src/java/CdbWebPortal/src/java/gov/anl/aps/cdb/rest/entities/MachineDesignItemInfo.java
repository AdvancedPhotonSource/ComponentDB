/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

/**
 *
 * @author craig
 */
public class MachineDesignItemInfo {
    
    private Integer id;
    private String catalogName;
    
    public MachineDesignItemInfo(Integer id, String catalogName) {
        this.id = id;
        this.catalogName = catalogName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

}
