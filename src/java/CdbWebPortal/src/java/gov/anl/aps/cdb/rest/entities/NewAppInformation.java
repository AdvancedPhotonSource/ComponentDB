/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainApp;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import java.util.List;

/**
 *
 * @author darek
 */
public class NewAppInformation {
    
    private String name;
    
    private String description; 
    
    private List<ItemCategory> technicalSystemList; 
    private List<ItemType> typeList; 
        

    public NewAppInformation() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<ItemType> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<ItemType> typeList) {
        this.typeList = typeList;
    }
    
    public void updateItemDomainAppwithInformation(ItemDomainApp itemDomainApp) throws InvalidArgument {
        if (name == null) {
            throw new InvalidArgument("Name for new item must be provided."); 
        }                
        
        itemDomainApp.setName(name);
        itemDomainApp.setDescription(description); 

        
        if (technicalSystemList != null && technicalSystemList.size() > 0) {
            itemDomainApp.setItemCategoryList(technicalSystemList);
        }
        if (typeList != null && technicalSystemList.size() > 0) {
            itemDomainApp.setItemTypeList(typeList); 
        }                
    }
        
}
