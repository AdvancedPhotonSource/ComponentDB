/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class CableCatalogItemInfo {

    private String name;
    private Integer id;
    private List<String> connectorNames;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getConnectorNames() {
        if (connectorNames == null) {
            connectorNames = new ArrayList<>();
        }
        return connectorNames;
    }

    public void setConnectorNames(List<String> connectorNames) {
        this.connectorNames = connectorNames;
    }
    
    public void addConnectorName(String connectorName) {
        this.getConnectorNames().add(connectorName);
    }
    
}
