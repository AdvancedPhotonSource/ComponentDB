/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.MACHINE_DESIGN_ID + "")
public class ItemDomainMachineDesign extends Item {

    private transient Boolean isItemTemplate = null;

    @Override
    public Item createInstance() {
        return new ItemDomainMachineDesign();
    }

    public Boolean getIsItemTemplate() {
        if (isItemTemplate == null) {
            isItemTemplate = isItemTemplate(this);
        }
        return isItemTemplate;
    }

    public static boolean isItemTemplate(ItemDomainMachineDesign item) {
        if (item != null) {
            List<EntityType> entityTypeList = item.getEntityTypeList();
            if (entityTypeList != null) {
                for (EntityType entityType : entityTypeList) {
                    if (entityType.getName().equals(EntityTypeName.template.getValue())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
