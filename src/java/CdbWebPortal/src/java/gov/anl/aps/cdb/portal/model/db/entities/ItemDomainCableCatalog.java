/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.common.constants.ItemCoreMetadataFieldType;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.utilities.HttpLinkUtility;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.view.objects.ItemCoreMetadataPropertyInfo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.CABLE_CATALOG_ID + "")   
public class ItemDomainCableCatalog extends ItemDomainCatalogBase<ItemDomainCableInventory> {
    
    private transient String url = null;
    private transient String urlDisplay = null;
    private transient String imageUrl = null;
    private transient String imageUrlDisplay = null;
    private transient String manufacturer = null;
    private transient String weight = null;
    private transient String diameter = null;
    private transient String conductors = null;
    private transient String insulation = null;
    private transient String jacketColor = null;
    private transient String voltageRating = null;
    private transient String fireLoad = null;
    private transient String heatLimit = null;
    private transient String bendRadius = null;
    private transient String radTolerance = null;
    private transient String team = null;
    private transient boolean isValid = false;
    private transient String validString = null;
    
    public final static String CABLE_CATALOG_INTERNAL_PROPERTY_TYPE = "cable_catalog_internal_property_type"; 
    private final static String CABLE_PROPERTY_URL_KEY = "url"; 
    private final static String CABLE_PROPERTY_IMAGE_URL_KEY = "imageUrl"; 
    private final static String CABLE_PROPERTY_WEIGHT_KEY = "weight"; 
    private final static String CABLE_PROPERTY_DIAMETER_KEY = "diameter"; 
    private final static String CABLE_PROPERTY_CONDUCTORS_KEY = "conductors"; 
    private final static String CABLE_PROPERTY_INSULATION_KEY = "insulation"; 
    private final static String CABLE_PROPERTY_JACKET_COLOR_KEY = "jacketColor"; 
    private final static String CABLE_PROPERTY_VOLTAGE_RATING_KEY = "voltageRating"; 
    private final static String CABLE_PROPERTY_FIRE_LOAD_KEY = "fireLoad"; 
    private final static String CABLE_PROPERTY_HEAT_LIMIT_KEY = "heatLimit"; 
    private final static String CABLE_PROPERTY_BEND_RADIUS_KEY = "bendRadius"; 
    private final static String CABLE_PROPERTY_RAD_TOLERANCE_KEY = "radTolerance"; 
    
    static {
        ItemCoreMetadataPropertyInfo info = new ItemCoreMetadataPropertyInfo("Cable Type Metadata", CABLE_CATALOG_INTERNAL_PROPERTY_TYPE);
        info.addField(CABLE_PROPERTY_URL_KEY, "Documentation URL", "Documentation URL", ItemCoreMetadataFieldType.URL, "");
        info.addField(CABLE_PROPERTY_IMAGE_URL_KEY, "Image URL", "Image URL", ItemCoreMetadataFieldType.URL, "");
        info.addField(CABLE_PROPERTY_WEIGHT_KEY, "Weight", "Nominal weight", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(CABLE_PROPERTY_DIAMETER_KEY, "Diameter", "Nominal diameter", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(CABLE_PROPERTY_CONDUCTORS_KEY, "Conductors", "Number of conductors", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(CABLE_PROPERTY_INSULATION_KEY, "Insulation", "Insulation type", ItemCoreMetadataFieldType.STRING, "");
        info.addField(CABLE_PROPERTY_JACKET_COLOR_KEY, "Jacket Color", "Jacket color", ItemCoreMetadataFieldType.STRING, "");
        info.addField(CABLE_PROPERTY_VOLTAGE_RATING_KEY, "Voltage Rating", "Voltage rating", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(CABLE_PROPERTY_FIRE_LOAD_KEY, "Fire Load", "Fire load", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(CABLE_PROPERTY_HEAT_LIMIT_KEY, "Heat Limit", "Heat limit", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(CABLE_PROPERTY_BEND_RADIUS_KEY, "Bend Radius", "Bend radius", ItemCoreMetadataFieldType.NUMERIC, "");
        info.addField(CABLE_PROPERTY_RAD_TOLERANCE_KEY, "Radiation Tolearance", "Radiation tolerance", ItemCoreMetadataFieldType.NUMERIC, "");
        registerCoreMetadataPropertyInfo(ItemDomainCableCatalog.class, info);
    }

    @Override
    public Item createInstance() {
        return new ItemDomainCableCatalog(); 
    }

    public List<ItemDomainCableInventory> getCableInventoryItemList() {
        return (List<ItemDomainCableInventory>)(List<?>) super.getDerivedFromItemList();
    }
    
    public String getUrl() throws CdbException {
        if (url == null) {
            url = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_URL_KEY);
        }
        return url;
    }
    
    public void setUrl(String w) throws CdbException {
        url = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_URL_KEY, w);
    }
    
    public String getUrlDisplay() throws CdbException {
        if (urlDisplay == null && this.getUrl() != null) {
            urlDisplay = HttpLinkUtility.prepareHttpLinkDisplayValue(this.getUrl());
        }
        return urlDisplay;
    }

    public String getImageUrl() throws CdbException {
        if (imageUrl == null) {
            imageUrl = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_IMAGE_URL_KEY);
        }
        return imageUrl;
    }
    
    public void setImageUrl(String w) throws CdbException {
        imageUrl = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_IMAGE_URL_KEY, w);
    }
    
    public String getImageUrlDisplay() throws CdbException {
        if (imageUrlDisplay == null && this.getImageUrl() != null) {
            imageUrlDisplay = HttpLinkUtility.prepareHttpLinkDisplayValue(this.getImageUrl());
        }
        return imageUrlDisplay;
    }

    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String sourceName) {        
        Source source = SourceController.getInstance().findByName(sourceName);
        if (source != null) {
            this.setManufacturerSource(source);
        }
    }
    
    public void setManufacturerId(String sourceId) {  
        Source source = SourceController.getInstance().findById(Integer.valueOf(sourceId));
        if (source != null) {
            this.setManufacturerSource(source);
        }
    }
    
    private void setManufacturerSource(Source source) {           
        List<ItemSource> sourceList = new ArrayList<>();
        ItemSource itemSource = new ItemSource();
        itemSource.setItem(this);
        itemSource.setSource(source);
        sourceList.add(itemSource);
        this.setItemSourceList(sourceList);
        manufacturer = source.getName();
    }
    
    public String getPartNumber() {
        return this.getItemIdentifier1();
    }
    
    public void setPartNumber(String n) {
        this.setItemIdentifier1(n);
    }
    
    public String getWeight() throws CdbException {
        if (weight == null) {
            weight = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_WEIGHT_KEY);
        }
        return weight;
    }
    
    public void setWeight(String w) throws CdbException {
        weight = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_WEIGHT_KEY, w);
    }
    
    public String getDiameter() throws CdbException {
        if (diameter == null) {
            diameter = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_DIAMETER_KEY);
        }
        return diameter;
    }
    
    public void setDiameter(String d) throws CdbException {
        diameter = d;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_DIAMETER_KEY, d);
    }
    
    public String getConductors() throws CdbException {
        if (conductors == null) {
            conductors = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_CONDUCTORS_KEY);
        }
        return conductors;
    }
    
    public void setConductors(String w) throws CdbException {
        conductors = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_CONDUCTORS_KEY, w);
    }
    
    public String getInsulation() throws CdbException {
        if (insulation == null) {
            insulation = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_INSULATION_KEY);
        }
        return insulation;
    }
    
    public void setInsulation(String w) throws CdbException {
        insulation = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_INSULATION_KEY, w);
    }
    
    public String getJacketColor() throws CdbException {
        if (jacketColor == null) {
            jacketColor = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_JACKET_COLOR_KEY);
        }
        return jacketColor;
    }
    
    public void setJacketColor(String w) throws CdbException {
        jacketColor = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_JACKET_COLOR_KEY, w);
    }
    
    public String getVoltageRating() throws CdbException {
        if (voltageRating == null) {
            voltageRating = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_VOLTAGE_RATING_KEY);
        }
        return voltageRating;
    }
    
    public void setVoltageRating(String w) throws CdbException {
        voltageRating = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_VOLTAGE_RATING_KEY, w);
    }
    
    public String getFireLoad() throws CdbException {
        if (fireLoad == null) {
            fireLoad = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_FIRE_LOAD_KEY);
        }
        return fireLoad;
    }
    
    public void setFireLoad(String w) throws CdbException {
        fireLoad = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_FIRE_LOAD_KEY, w);
    }
    
    public String getHeatLimit() throws CdbException {
        if (heatLimit == null) {
            heatLimit = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_HEAT_LIMIT_KEY);
        }
        return heatLimit;
    }
    
    public void setHeatLimit(String w) throws CdbException {
        heatLimit = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_HEAT_LIMIT_KEY, w);
    }
    
    public String getBendRadius() throws CdbException {
        if (bendRadius == null) {
            bendRadius = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_BEND_RADIUS_KEY);
        }
        return bendRadius;
    }
    
    public void setBendRadius(String w) throws CdbException {
        bendRadius = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_BEND_RADIUS_KEY, w);
    }
    
    public String getRadTolerance() throws CdbException {
        if (radTolerance == null) {
            radTolerance = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_RAD_TOLERANCE_KEY);
        }
        return radTolerance;
    }
    
    public void setRadTolerance(String w) throws CdbException {
        radTolerance = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_RAD_TOLERANCE_KEY, w);
    }
    
    public String getTeam() {
        if (team == null) {
            team = this.getItemCategoryString();
        }
        return team;
    }
    
    public void setTeamId(String categoryId) {
        ItemCategory category = ItemCategoryController.getInstance().findById(Integer.valueOf(categoryId));
        if (category != null) {
            List<ItemCategory> categoryList = new ArrayList<>();
            categoryList.add(category);
            this.setItemCategoryList(categoryList);
            team = this.getItemCategoryString();
        }
    }
    
    public boolean isIsValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getValidString() {
        return validString;
    }

    public void setValidString(String validString) {
        this.validString = validString;
    }
        
}
