/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;

/**
 *
 * @author djarosz
 */
public class MachineDesignControlRelationshipListObject {
    
    private ItemDomainMachineDesign controlParentItem = null; 
    private PropertyValue controlInterfaceToParent = null;
    private ItemElementRelationship controlRelationshipToParent = null;

    public MachineDesignControlRelationshipListObject() {
    }

    public ItemDomainMachineDesign getControlParentItem() {
        return controlParentItem;
    }

    public void setControlParentItem(ItemDomainMachineDesign controlParentItem) {
        this.controlParentItem = controlParentItem;
    }

    public PropertyValue getControlInterfaceToParent() {
        return controlInterfaceToParent;
    }

    public void setControlInterfaceToParent(PropertyValue controlInterfaceToParent) {
        this.controlInterfaceToParent = controlInterfaceToParent;
    }

    public ItemElementRelationship getControlRelationshipToParent() {
        return controlRelationshipToParent;
    }

    public void setControlRelationshipToParent(ItemElementRelationship controlRelationshipToParent) {
        this.controlRelationshipToParent = controlRelationshipToParent;
    }
    
    
    
}
