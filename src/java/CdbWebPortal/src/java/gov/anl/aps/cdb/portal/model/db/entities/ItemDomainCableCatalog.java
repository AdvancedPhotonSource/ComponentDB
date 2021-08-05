/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.utilities.HttpLinkUtility;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableCatalogControllerUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
@DiscriminatorValue(value = ItemDomainName.CABLE_CATALOG_ID + "")   
public class ItemDomainCableCatalog extends ItemDomainCatalogBase<ItemDomainCableInventory> {
    
    private static final Logger LOGGER = LogManager.getLogger(ItemDomainCableCatalog.class.getName());

    private transient String url = null;
    private transient String urlDisplay = null;
    private transient String imageUrl = null;
    private transient String imageUrlDisplay = null;
    private transient String altPartNumber = null;
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
    private transient String totalLength = null;
    private transient String reelLength = null;
    private transient String reelQuantity = null;
    private transient String leadTime = null;
    private transient String procurementStatus = null;
    
    public final static String CABLE_CATALOG_INTERNAL_PROPERTY_TYPE = "cable_catalog_internal_property_type"; 
    public final static String CABLE_PROPERTY_URL_KEY = "url"; 
    public final static String CABLE_PROPERTY_ALT_PART_NUM_KEY = "altPartNumber"; 
    public final static String CABLE_PROPERTY_IMAGE_URL_KEY = "imageUrl"; 
    public final static String CABLE_PROPERTY_WEIGHT_KEY = "weight"; 
    public final static String CABLE_PROPERTY_DIAMETER_KEY = "diameter"; 
    public final static String CABLE_PROPERTY_CONDUCTORS_KEY = "conductors"; 
    public final static String CABLE_PROPERTY_INSULATION_KEY = "insulation"; 
    public final static String CABLE_PROPERTY_JACKET_COLOR_KEY = "jacketColor"; 
    public final static String CABLE_PROPERTY_VOLTAGE_RATING_KEY = "voltageRating"; 
    public final static String CABLE_PROPERTY_FIRE_LOAD_KEY = "fireLoad"; 
    public final static String CABLE_PROPERTY_HEAT_LIMIT_KEY = "heatLimit"; 
    public final static String CABLE_PROPERTY_BEND_RADIUS_KEY = "bendRadius"; 
    public final static String CABLE_PROPERTY_RAD_TOLERANCE_KEY = "radTolerance"; 
    public final static String CABLE_PROPERTY_TOTAL_LENGTH_KEY = "totalLength"; 
    public final static String CABLE_PROPERTY_REEL_LENGTH_KEY = "reelLength"; 
    public final static String CABLE_PROPERTY_REEL_QUANTITY_KEY = "reelQuantity"; 
    public final static String CABLE_PROPERTY_LEAD_TIME_KEY = "leadTime"; 
    public final static String CABLE_PROPERTY_PROCUREMENT_STATUS_KEY = "procurementStatus"; 
    
    @Override
    public Item createInstance() {
        return new ItemDomainCableCatalog(); 
    } 

    @Override
    public ItemDomainCableCatalogControllerUtility getItemControllerUtility() {
        return new ItemDomainCableCatalogControllerUtility(); 
    }
    
    @Override
    public List<Item> getDerivedFromItemList() {
        List<Item> itemList = super.getDerivedFromItemList();
        // copy list so we are not modifying original list
        List<Item> itemListCopy = new ArrayList<>(itemList);
        Collections.sort(itemListCopy, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return itemListCopy;        
    }
    
    @JsonIgnore
    public List<ItemDomainCableInventory> getCableInventoryItemList() {
        return (List<ItemDomainCableInventory>)(List<?>) super.getDerivedFromItemList();
    }
    
    @JsonIgnore
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
    
    @JsonIgnore
    public String getUrlDisplay() throws CdbException {
        if (urlDisplay == null && this.getUrl() != null) {
            urlDisplay = HttpLinkUtility.prepareHttpLinkDisplayValue(this.getUrl());
        }
        return urlDisplay;
    }

    @JsonIgnore
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
    
    @JsonIgnore
    public String getImageUrlDisplay() throws CdbException {
        if (imageUrlDisplay == null && this.getImageUrl() != null) {
            imageUrlDisplay = HttpLinkUtility.prepareHttpLinkDisplayValue(this.getImageUrl());
        }
        return imageUrlDisplay;
    }

    @JsonIgnore
    public String getAltPartNumber() throws CdbException {
        if (altPartNumber == null) {
            altPartNumber = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_ALT_PART_NUM_KEY);
        }
        return altPartNumber;
    }
    
    public void setAltPartNumber(String n) throws CdbException {
        altPartNumber = n;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_ALT_PART_NUM_KEY, n);
    }
    
    @JsonIgnore
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
    
    @JsonIgnore
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
    
    @JsonIgnore
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
    
    @JsonIgnore
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
    
    @JsonIgnore
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
    
    @JsonIgnore
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
    
    @JsonIgnore
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
    
    @JsonIgnore
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
    
    @JsonIgnore
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
    
    @JsonIgnore
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
    
    @JsonIgnore
    public String getTotalLength() throws CdbException {
        if (totalLength == null) {
            totalLength = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_TOTAL_LENGTH_KEY);
        }
        return totalLength;
    }
    
    public void setTotalLength(String w) throws CdbException {
        totalLength = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_TOTAL_LENGTH_KEY, w);
    }
    
    @JsonIgnore
    public String getReelLength() throws CdbException {
        if (reelLength == null) {
            reelLength = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_REEL_LENGTH_KEY);
        }
        return reelLength;
    }
    
    public void setReelLength(String w) throws CdbException {
        reelLength = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_REEL_LENGTH_KEY, w);
    }
    
    @JsonIgnore
    public String getReelQuantity() throws CdbException {
        if (reelQuantity == null) {
            reelQuantity = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_REEL_QUANTITY_KEY);
        }
        return reelQuantity;
    }
    
    public void setReelQuantity(String w) throws CdbException {
        reelQuantity = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_REEL_QUANTITY_KEY, w);
    }
    
    @JsonIgnore
    public String getLeadTime() throws CdbException {
        if (leadTime == null) {
            leadTime = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_LEAD_TIME_KEY);
        }
        return leadTime;
    }
    
    public void setLeadTime(String w) throws CdbException {
        leadTime = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_LEAD_TIME_KEY, w);
    }
    
    @JsonIgnore
    public String getProcurementStatus() throws CdbException {
        if (procurementStatus == null) {
            procurementStatus = getCoreMetadataPropertyFieldValue(CABLE_PROPERTY_PROCUREMENT_STATUS_KEY);
        }
        return procurementStatus;
    }
    
    public void setProcurementStatus(String w) throws CdbException {
        procurementStatus = w;
        setCoreMetadataPropertyFieldValue(CABLE_PROPERTY_PROCUREMENT_STATUS_KEY, w);
    }
}
