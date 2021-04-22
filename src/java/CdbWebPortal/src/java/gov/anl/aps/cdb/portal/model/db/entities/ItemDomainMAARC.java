/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMAARCControllerUtility;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.MAARC_ID + "")  
@Schema(name = "ItemDomainMAARC",
        allOf = Item.class
)
public class ItemDomainMAARC extends Item {
    
    // <editor-fold defaultstate="collapsed" desc="Controller variables for current.">
    private transient List<ItemElementRelationship> relatedRelationshipsForCurrent = null;
    private transient Boolean loadGallery = null;
    // </editor-fold>
    
    @Override
    public Item createInstance() {
        return new ItemDomainMAARC(); 
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
