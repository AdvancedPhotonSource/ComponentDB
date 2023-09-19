/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMAARCControllerUtility;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.MAARC_ID + "")
public class ItemDomainMAARC extends Item {
    
    // <editor-fold defaultstate="collapsed" desc="Controller variables for current.">
    private transient List<ItemElementRelationship> relatedRelationshipsForCurrent = null;
    private transient Boolean loadGallery = null;
    // </editor-fold>
    
    @Override
    public Item createInstance() {
        return new ItemDomainMAARC(); 
    }
    
    // OpenAPI generates incorrect entity definition without any fields within the class. 
    // Remove this after adding any maarc specific field to this class.
    public Integer getPlaceholderValue() {
        return null; 
    }

    @Override
    public ItemDomainMAARCControllerUtility getItemControllerUtility() {
        return new ItemDomainMAARCControllerUtility();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Controller variables for current.">

    @JsonIgnore
    public List<ItemElementRelationship> getRelatedRelationshipsForCurrent() {
        return relatedRelationshipsForCurrent;
    }

    public void setRelatedRelationshipsForCurrent(List<ItemElementRelationship> relatedRelationshipsForCurrent) {
        this.relatedRelationshipsForCurrent = relatedRelationshipsForCurrent;
    }

    @JsonIgnore
    public Boolean getLoadGallery() {
        return loadGallery;
    }

    public void setLoadGallery(Boolean loadGallery) {
        this.loadGallery = loadGallery;
    }
    
    // </editor-fold>
    
}
