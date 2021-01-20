/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCatalogControllerUtility;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.CATALOG_ID + "")  
@Schema(name = "ItemDomainCatalog",
        allOf = Item.class
)
public class ItemDomainCatalog extends ItemDomainCatalogBase<ItemDomainInventory> {
    
    private static final Logger LOGGER = LogManager.getLogger(ItemDomainCatalog.class.getName());

    private transient String machineDesignPlaceholderName = null;         

    @Override
    public Item createInstance() {
        return new ItemDomainCatalog(); 
    } 

    @Override
    public ItemControllerUtility getItemControllerUtility() {
        return new ItemDomainCatalogControllerUtility(); 
    }

    @JsonIgnore
    public String getMachineDesignPlaceholderName() {
        return machineDesignPlaceholderName;
    }

    public void setMachineDesignPlaceholderName(String machineDesignPlaceholderName) {
        this.machineDesignPlaceholderName = machineDesignPlaceholderName;
    }
    
    @Override
    public ItemController getItemDomainController() {
        return ItemDomainCatalogController.getInstance();
    }
    
    public String getAlternateName() {
        return getItemIdentifier2();
    }

    public void setAlternateName(String n) {
        setItemIdentifier2(n);
    }
    
}
