/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import java.util.ArrayList;
import java.util.List;
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
public class ItemDomainCatalog extends ItemDomainCatalogBase<ItemDomainInventory> {
    
    private static final Logger LOGGER = LogManager.getLogger(ItemDomainCatalog.class.getName());

    private transient String machineDesignPlaceholderName = null;         
    private transient String importSource = null;

    @Override
    public Item createInstance() {
        return new ItemDomainCatalog(); 
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
    
    public String getModelNumber() {
        return this.getItemIdentifier1();
    }
    
    public void setModelNumber(String modelNumber) {
        this.setItemIdentifier1(modelNumber);
    }

    public String getAlternateName() {
        return getItemIdentifier2();
    }

    public void setAlternateName(String n) {
        setItemIdentifier2(n);
    }
    
    @JsonIgnore
    public String getImportSource() {
        return importSource;
    }

    public void setImportSource(Source source) {           
        List<ItemSource> sourceList = new ArrayList<>();
        ItemSource itemSource = new ItemSource();
        itemSource.setItem(this);
        itemSource.setSource(source);
        sourceList.add(itemSource);
        this.setItemSourceList(sourceList);
        importSource = source.getName();
    }
    
    public String getTechnicalSystem() {
        return this.getItemCategoryString();
    }
    
    public void setTechnicalSystem(ItemCategory category) throws CdbException {
        if (category != null) {
            String domainName = category.getDomain().getName();
            if (!domainName.equals(this.getDomain().getName())) {
                String msg = "invalid domain: " + domainName +
                        " expected: " + this.getDomain().getName();
                LOGGER.error("setTechnicalSystem() " + msg);
                throw new CdbException(msg);
            }

            List<ItemCategory> categoryList = new ArrayList<>();
            categoryList.add(category);
            this.setItemCategoryList(categoryList);
        } else {
            LOGGER.error("setTechnicalSystemId() null item category");
        }
    }
    
    public String getFunction() {
        return this.getItemTypeString();
    }
    
    public void setFunction(ItemType function) throws CdbException {
        if (function != null) {
            String domainName = function.getDomain().getName();
            if (!domainName.equals(this.getDomain().getName())) {
                String msg = "invalid domain: " + domainName +
                        " expected: " + this.getDomain().getName();
                LOGGER.error("setFunction() " + msg);
                throw new CdbException(msg);
            }

            List<ItemType> functionList = new ArrayList<>();
            functionList.add(function);
            this.setItemTypeList(functionList);
        } else {
            LOGGER.error("setFunction() null item function");
        }
    }
    
}
