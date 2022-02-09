/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.CdbDomainEntityController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import javax.faces.event.ActionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */


public abstract class CdbDomainEntitySettings<DomainEntityController extends CdbDomainEntityController> extends CdbEntitySettingsBase<DomainEntityController> {
    
    private static final String DisplayGalleryViewableDocumentsSettingTypeKey = "DomainEntity.Detail.Display.GalleryViewableDocuments";
    private static final Logger logger = LogManager.getLogger(CdbDomainEntitySettings.class.getName());
    
    protected Integer displayPropertyTypeId1 = null;
    protected Integer displayPropertyTypeId2 = null;
    protected Integer displayPropertyTypeId3 = null;
    protected Integer displayPropertyTypeId4 = null;
    protected Integer displayPropertyTypeId5 = null;

    protected Boolean displayRowExpansion = null;
    protected Boolean loadRowExpansionPropertyValues = null;
    protected Boolean displayGalleryViewableDocuments = null;

    protected String filterByPropertyValue1 = null;
    protected String filterByPropertyValue2 = null;
    protected String filterByPropertyValue3 = null;
    protected String filterByPropertyValue4 = null;
    protected String filterByPropertyValue5 = null;
    protected String filterByItemIdentifier1 = null;
    protected String filterByItemIdentifier2 = null;
    protected Boolean filterByPropertiesAutoLoad = null;        

    public CdbDomainEntitySettings(DomainEntityController parentController) {
        super(parentController);
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayGalleryViewableDocuments = Boolean.parseBoolean(settingTypeMap.get(DisplayGalleryViewableDocumentsSettingTypeKey).getDefaultValue());        
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        displayGalleryViewableDocuments = settingEntity.getSettingValueAsBoolean(DisplayGalleryViewableDocumentsSettingTypeKey, displayGalleryViewableDocuments);

        parentController.prepareImageListForCurrent();
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        settingEntity.setSettingValue(DisplayGalleryViewableDocumentsSettingTypeKey, displayGalleryViewableDocuments);
    }        
    
    @Override
    public void saveListSettingsForSessionSettingEntityActionListener(ActionEvent actionEvent) {
        super.saveListSettingsForSessionSettingEntityActionListener(actionEvent);
    }   
    
    public Boolean getDisplayGalleryViewableDocuments() {
        return displayGalleryViewableDocuments;
    }

    public void setDisplayGalleryViewableDocuments(Boolean displayGalleryViewableDocuments) {
        this.displayGalleryViewableDocuments = displayGalleryViewableDocuments;
    }   
    
    @Deprecated
    public Boolean getFilterByPropertiesAutoLoad() {
        return filterByPropertiesAutoLoad;
    }

    @Deprecated
    public void setFilterByPropertiesAutoLoad(Boolean filterByPropertiesAutoLoad) {
        this.filterByPropertiesAutoLoad = filterByPropertiesAutoLoad;
    }
    
    public Integer getDisplayPropertyTypeId1() {
        return displayPropertyTypeId1;
    }

    public void setDisplayPropertyTypeId1(Integer displayPropertyTypeId1) {
        this.displayPropertyTypeId1 = displayPropertyTypeId1;
    }

    public Integer getDisplayPropertyTypeId2() {
        return displayPropertyTypeId2;
    }

    public void setDisplayPropertyTypeId2(Integer displayPropertyTypeId2) {
        this.displayPropertyTypeId2 = displayPropertyTypeId2;
    }

    public Integer getDisplayPropertyTypeId3() {
        return displayPropertyTypeId3;
    }

    public void setDisplayPropertyTypeId3(Integer displayPropertyTypeId3) {
        this.displayPropertyTypeId3 = displayPropertyTypeId3;
    }

    public Integer getDisplayPropertyTypeId4() {
        return displayPropertyTypeId4;
    }

    public void setDisplayPropertyTypeId4(Integer displayPropertyTypeId4) {
        this.displayPropertyTypeId4 = displayPropertyTypeId4;
    }

    public Integer getDisplayPropertyTypeId5() {
        return displayPropertyTypeId5;
    }

    public void setDisplayPropertyTypeId5(Integer displayPropertyTypeId5) {
        this.displayPropertyTypeId5 = displayPropertyTypeId5;
    }

    public Boolean getDisplayRowExpansion() {
        return displayRowExpansion;
    }

    public void setDisplayRowExpansion(Boolean displayRowExpansion) {
        this.displayRowExpansion = displayRowExpansion;
    }

    public Boolean getLoadRowExpansionPropertyValues() {
        return loadRowExpansionPropertyValues;
    }

    public void setLoadRowExpansionPropertyValues(Boolean loadRowExpansionPropertyValues) {
        this.loadRowExpansionPropertyValues = loadRowExpansionPropertyValues;
    }
    
    public String getFilterByPropertyValue1() {
        return filterByPropertyValue1;
    }

    public void setFilterByPropertyValue1(String filterByPropertyValue1) {
        this.filterByPropertyValue1 = filterByPropertyValue1;
    }

    public String getFilterByPropertyValue2() {
        return filterByPropertyValue2;
    }

    public void setFilterByPropertyValue2(String filterByPropertyValue2) {
        this.filterByPropertyValue2 = filterByPropertyValue2;
    }

    public String getFilterByPropertyValue3() {
        return filterByPropertyValue3;
    }

    public void setFilterByPropertyValue3(String filterByPropertyValue3) {
        this.filterByPropertyValue3 = filterByPropertyValue3;
    }

    public String getFilterByPropertyValue4() {
        return filterByPropertyValue4;
    }

    public void setFilterByPropertyValue4(String filterByPropertyValue4) {
        this.filterByPropertyValue4 = filterByPropertyValue4;
    }

    public String getFilterByPropertyValue5() {
        return filterByPropertyValue5;
    }

    public void setFilterByPropertyValue5(String filterByPropertyValue5) {
        this.filterByPropertyValue5 = filterByPropertyValue5;
    }
    
}
