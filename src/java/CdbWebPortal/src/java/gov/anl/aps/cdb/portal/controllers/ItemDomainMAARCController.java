/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMAARCSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMAARCFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMAARC;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named("itemDomainMAARCController")
@SessionScoped
public class ItemDomainMAARCController extends ItemController<ItemDomainMAARC, ItemDomainMAARCFacade, ItemDomainMAARCSettings> {
    
    protected final String FILE_ENTITY_TYPE_NAME = "File"; 
    
    protected final String FILE_PROPERTY_TYPE_NAME = "File";
    
    private Integer filePropertyTypeId = null; 
    private boolean attemptedFetchFilePropertyType = false; 
    
    @EJB
    ItemDomainMAARCFacade itemDomainMAARCFacade; 
    
    @EJB
    PropertyTypeFacade propertyTypeFacade; 
    
    @Override
    protected ItemDomainMAARC instenciateNewItemDomainEntity() {
        return new ItemDomainMAARC();
    }

    @Override
    protected ItemDomainMAARCSettings createNewSettingObject() {
        return new ItemDomainMAARCSettings(this);
    }

    @Override
    protected ItemDomainMAARCFacade getEntityDbFacade() {
        return itemDomainMAARCFacade;
    }

    @Override
    public String getEntityTypeName() {
        return "itemMAARC";
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.maarc.getValue();
    } 

    @Override
    public String getDisplayEntityTypeName() {
        return "MAARC Item"; 
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return true;
    } 

    @Override
    public boolean getEntityDisplayItemElementsForCurrent() {
        boolean result = super.getEntityDisplayItemElementsForCurrent(); 
        if (getCurrent() == null 
                || getCurrent().getEntityTypeList() == null 
                || getCurrent().getEntityTypeList().isEmpty()) {
            return result; 
        }
        List<EntityType> entityTypeList = getCurrent().getEntityTypeList();
        for (EntityType entityType : entityTypeList) {
            if (entityType.getName().equals(FILE_ENTITY_TYPE_NAME)) {
                result = false; 
                break; 
            }
        }
                
        return result; 
    } 

    public Integer getFilePropertyTypeId() {
        if (filePropertyTypeId == null && !attemptedFetchFilePropertyType) {
            PropertyType filePropertyType = propertyTypeFacade.findByName(FILE_PROPERTY_TYPE_NAME);
            if (filePropertyType != null) {
                filePropertyTypeId = filePropertyType.getId(); 
            }
            attemptedFetchFilePropertyType = true;
        }
        return filePropertyTypeId;
    }

    @Override
    public String getItemEntityTypeTitle() {
        return "MAARC Type"; 
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStyleName() {
        // TODO -- Update with a new style.
        return "inventory";
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        return null;
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        return null;
    }
    
}
