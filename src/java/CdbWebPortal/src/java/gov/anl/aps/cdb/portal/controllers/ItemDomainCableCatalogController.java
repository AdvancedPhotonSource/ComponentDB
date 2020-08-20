/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.constants.ItemCoreMetadataFieldType;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.extensions.ImportHelperCableCatalog;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainCableCatalogSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import static gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog.CABLE_CATALOG_INTERNAL_PROPERTY_TYPE;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportFormatInfo;
import gov.anl.aps.cdb.portal.view.objects.ItemCoreMetadataPropertyInfo;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainCableCatalogController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainCableCatalogController extends ItemDomainCatalogBaseController<ItemDomainCableCatalog, ItemDomainCableCatalogFacade, ItemDomainCableCatalogSettings> {
    
    public static final String CONTROLLER_NAMED = "itemDomainCableCatalogController";
    
    @EJB
    ItemDomainCableCatalogFacade itemDomainCableCatalogFacade;
    
    public static ItemDomainCableCatalogController getInstance() {
        if (SessionUtility.runningFaces()) {
            return (ItemDomainCableCatalogController) SessionUtility.findBean(ItemDomainCableCatalogController.CONTROLLER_NAMED);
        } else {
            // TODO add apiInstance
            return null;
        }
    }
    
    @Override
    protected ItemCoreMetadataPropertyInfo initializeCoreMetadataPropertyInfo() {
        ItemCoreMetadataPropertyInfo info = new ItemCoreMetadataPropertyInfo("Cable Type Metadata", CABLE_CATALOG_INTERNAL_PROPERTY_TYPE);
        info.addField(ItemDomainCableCatalog.CABLE_PROPERTY_URL_KEY, "Documentation URL", "Raw URL for documentation pdf file.", ItemCoreMetadataFieldType.URL, "");
        info.addField(ItemDomainCableCatalog.CABLE_PROPERTY_IMAGE_URL_KEY, "Image URL", "Raw URL for image file.", ItemCoreMetadataFieldType.URL, "");
        info.addField(ItemDomainCableCatalog.CABLE_PROPERTY_ALT_PART_NUM_KEY, "Alt Part Num", "Manufacturer's alternate part number.", ItemCoreMetadataFieldType.STRING, "");
        info.addField(ItemDomainCableCatalog.CABLE_PROPERTY_WEIGHT_KEY, "Weight", "Nominal weight in lbs/1000 feet.", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(ItemDomainCableCatalog.CABLE_PROPERTY_DIAMETER_KEY, "Diameter", "Diameter in inches (max).", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(ItemDomainCableCatalog.CABLE_PROPERTY_CONDUCTORS_KEY, "Conductors", "Number of conductors/fibers.", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(ItemDomainCableCatalog.CABLE_PROPERTY_INSULATION_KEY, "Insulation", "Description of cable insulation.", ItemCoreMetadataFieldType.STRING, "");
        info.addField(ItemDomainCableCatalog.CABLE_PROPERTY_JACKET_COLOR_KEY, "Jacket Color", "Jacket color.", ItemCoreMetadataFieldType.STRING, "");
        info.addField(ItemDomainCableCatalog.CABLE_PROPERTY_VOLTAGE_RATING_KEY, "Voltage Rating", "Voltage rating (VRMS).", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(ItemDomainCableCatalog.CABLE_PROPERTY_FIRE_LOAD_KEY, "Fire Load", "Fire load rating.", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(ItemDomainCableCatalog.CABLE_PROPERTY_HEAT_LIMIT_KEY, "Heat Limit", "Heat limit rating.", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(ItemDomainCableCatalog.CABLE_PROPERTY_BEND_RADIUS_KEY, "Bend Radius", "Bend radius in inches.", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(ItemDomainCableCatalog.CABLE_PROPERTY_RAD_TOLERANCE_KEY, "Radiation Tolearance", "Radiation tolerance rating.", ItemCoreMetadataFieldType.NUMERIC, "");
        return info;
    }
    
    @Override
    public ItemDomainCableCatalog createEntityInstance() {
        ItemDomainCableCatalog item = super.createEntityInstance();
        setCurrent(item);
        return item;
    }
    
    @Override
    public ItemMultiEditController getItemMultiEditController() {
        return ItemMultiEditDomainCableCatalogController.getInstance();
    } 

    @Override
    protected ItemDomainCableCatalog instenciateNewItemDomainEntity() {
        return new ItemDomainCableCatalog(); 
    }

    @Override
    protected ItemDomainCableCatalogSettings createNewSettingObject() {
        return new ItemDomainCableCatalogSettings(this);
    }

    @Override
    protected ItemDomainCableCatalogFacade getEntityDbFacade() {
        return itemDomainCableCatalogFacade; 
    }

    @Override
    public String getEntityTypeName() {
        return "cableCatalog"; 
    } 

    @Override
    public String getDisplayEntityTypeName() {
        return "Cable Catalog";
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.cableCatalog.getValue(); 
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return false; 
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        return "Cable Inventory";
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStyleName() {
        return "catalog"; 
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        return ItemDomainName.cableInventory.getValue(); 
    } 
    
    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false; 
    }    

    @Override
    public boolean getEntityDisplayImportButton() {
        return true;
    }

    @Override
    protected DomainImportInfo initializeDomainImportInfo() {
        
        List<ImportFormatInfo> formatInfo = new ArrayList<>();
        formatInfo.add(new ImportFormatInfo("Basic Cable Catalog Format", ImportHelperCableCatalog.class));
        
        String completionUrl = "/views/itemDomainCableCatalog/list?faces-redirect=true";
        
        return new DomainImportInfo(formatInfo, completionUrl);
    }
    
}
