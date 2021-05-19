/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import java.util.List;

/**
 *
 * @author darek
 */
public class NewCatalogInformation {
    
    private String name;
    private String modelNumber; 
    private String alternateName; 
    
    private String description; 
    
    private List<ItemCategory> technicalSystemList; 
    private List<ItemType> functionList; 
    
    private List<ItemProject> itemProjectsList; 

    public NewCatalogInformation() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getAlternateName() {
        return alternateName;
    }

    public void setAlternateName(String alternateName) {
        this.alternateName = alternateName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ItemCategory> getTechnicalSystemList() {
        return technicalSystemList;
    }

    public void setTechnicalSystemList(List<ItemCategory> technicalSystemList) {
        this.technicalSystemList = technicalSystemList;
    }

    public List<ItemType> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(List<ItemType> functionList) {
        this.functionList = functionList;
    }

    public List<ItemProject> getItemProjectsList() {
        return itemProjectsList;
    }

    public void setItemProjectsList(List<ItemProject> itemProjectsList) {
        this.itemProjectsList = itemProjectsList;
    }
    
    public void updateItemDomainCatalogWithInformation(ItemDomainCatalog itemDomainCatalog) throws InvalidArgument {
        if (name == null) {
            throw new InvalidArgument("Name for new item must be provided."); 
        }
        
        if (itemProjectsList == null) {
            throw new InvalidArgument("Item project list for new item must be provided."); 
        }
        
        itemDomainCatalog.setName(name);
        itemDomainCatalog.setItemIdentifier1(modelNumber);
        itemDomainCatalog.setItemIdentifier2(alternateName);
        itemDomainCatalog.setDescription(description); 
        itemDomainCatalog.setItemProjectList(itemProjectsList); 
        
        if (technicalSystemList != null && technicalSystemList.size() > 0) {
            itemDomainCatalog.setItemCategoryList(technicalSystemList);
        }
        if (functionList != null && technicalSystemList.size() > 0) {
            itemDomainCatalog.setItemTypeList(functionList); 
        }                
    }
        
}
