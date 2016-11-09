/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Controller used to manage settings for the item member view for the Item Entity. 
 */
@Named("itemMemberViewController")
@SessionScoped
public class ItemMemberViewController extends ItemController {

    private static final String DisplayMemberNumberOfItemsPerPageSettingTypeKey = "Item.Member.List.Display.NumberOfItemsPerPage";
    private static final String DisplayMemberIdSettingTypeKey = "Item.Member.List.Display.Id";
    private static final String DisplayMemberDescriptionSettingTypeKey = "Item.Member.List.Display.Description";
    private static final String DisplayMemberOwnerUserSettingTypeKey = "Item.Member.List.Display.OwnerUser";
    private static final String DisplayMemberOwnerGroupSettingTypeKey = "Item.Member.List.Display.OwnerGroup";
    private static final String DisplayMemberCreatedByUserSettingTypeKey = "Item.Member.List.Display.CreatedByUser";
    private static final String DisplayMemberCreatedOnDateTimeSettingTypeKey = "Item.Member.List.Display.CreatedOnDateTime";
    private static final String DisplayMemberLastModifiedByUserSettingTypeKey = "Item.Member.List.Display.LastModifiedByUser";
    private static final String DisplayMemberLastModifiedOnDateTimeSettingTypeKey = "Item.Member.List.Display.LastModifiedOnDateTime";
    
    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }
        
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayMemberNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberDescriptionSettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());
    }
    
    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }
        
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayMemberNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayMemberIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayMemberDescriptionSettingTypeKey, displayDescription);
        displayOwnerUser = settingEntity.getSettingValueAsBoolean(DisplayMemberOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = settingEntity.getSettingValueAsBoolean(DisplayMemberOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = settingEntity.getSettingValueAsBoolean(DisplayMemberCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayMemberCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = settingEntity.getSettingValueAsBoolean(DisplayMemberLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayMemberLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
    }
    
    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }
        
        settingEntity.setSettingValue(DisplayMemberNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayMemberIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayMemberDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayMemberOwnerUserSettingTypeKey, displayOwnerUser);
        settingEntity.setSettingValue(DisplayMemberOwnerGroupSettingTypeKey, displayOwnerGroup);
        settingEntity.setSettingValue(DisplayMemberCreatedByUserSettingTypeKey, displayCreatedByUser);
        settingEntity.setSettingValue(DisplayMemberCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        settingEntity.setSettingValue(DisplayMemberLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        settingEntity.setSettingValue(DisplayMemberLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
    }

    @Override
    public boolean getEntityDisplayItemIdentifier1() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemIdentifier2() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemCategory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getItemIdentifier1Title() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getItemIdentifier2Title() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Item createEntityInstance() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getEntityTypeName() {
        return "designMember"; 
    } 

    @Override
    public String getDisplayEntityTypeName() {
        return "Design Member";
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayQrId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStyleName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Domain getDefaultDomain() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<Item> getItemList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDefaultDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
