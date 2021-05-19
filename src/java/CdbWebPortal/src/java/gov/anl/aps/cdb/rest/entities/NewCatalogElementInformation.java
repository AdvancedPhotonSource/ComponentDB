/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;

/**
 *
 * @author darek
 */
public class NewCatalogElementInformation {
    
    private Integer catalogItemId;
    private String partName; 
    private String partDescription; 

    public NewCatalogElementInformation() {
    }

    public Integer getCatalogItemId() {
        return catalogItemId;
    }

    public void setCatalogItemId(Integer catalogItemId) {
        this.catalogItemId = catalogItemId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartDescription() {
        return partDescription;
    }

    public void setPartDescription(String partDescription) {
        this.partDescription = partDescription;
    }
    
    public void updateItemElement(ItemElement element) throws InvalidArgument {
        if (catalogItemId == null) {
            throw new InvalidArgument("The catalog id is required."); 
        }
        
        if (partName != null) {
            element.setName(partName);
        }
        element.setDescription(partDescription);
        
        ItemDomainCatalogFacade catalogFacade = ItemDomainCatalogFacade.getInstance();
        ItemDomainCatalog containedItem = catalogFacade.find(catalogItemId);
        
        if (containedItem == null) {
            throw new InvalidArgument("Invalid catalog id was provided."); 
        }
        
        element.setContainedItem(containedItem);        
    }
        
}
