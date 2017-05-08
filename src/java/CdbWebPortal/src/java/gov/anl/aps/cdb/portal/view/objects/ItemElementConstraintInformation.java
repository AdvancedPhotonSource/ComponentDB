/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import java.util.ArrayList;
import java.util.List;

public class ItemElementConstraintInformation {

    protected List<ItemElementConstraintInformation> relatedConstraintInfo;
    protected ItemElement itemElement;

    public ItemElementConstraintInformation(ItemElement primaryItemElement) {
        this.itemElement = primaryItemElement;

        this.relatedConstraintInfo = new ArrayList<>();
        if (itemElement.getDerivedFromItemElementList() != null) {
            for (ItemElement derivedItemElement : itemElement.getDerivedFromItemElementList()) {
                ItemElementConstraintInformation ieci = getNewInstance();
                ieci.itemElement = derivedItemElement;
                this.relatedConstraintInfo.add(ieci);
            }
        }
    }

    public boolean isSafeToRemove() {
        if (this.isPreventsDelete()) {
           return false; 
        }
        
        for (ItemElementConstraintInformation ieci : relatedConstraintInfo) {
            if (ieci.isPreventsDelete()) {
                return false;
            }
        }

        return true;
    }

    public boolean isSafeToUpdateContainedItem() {
        for (ItemElementConstraintInformation ieci : relatedConstraintInfo) {
            if (ieci.isPreventUpdateContainedItem()) {
                return false;
            }
        }

        return true;
    }

    protected ItemElementConstraintInformation getNewInstance() {
        return new ItemElementConstraintInformation();
    }

    protected ItemElementConstraintInformation() {
    }

    protected boolean isPreventUpdateContainedItem() {
        return false;
    }

    protected boolean isPreventsDelete() {
        // the primary element cannot prevent delete
        if (isHasProperties()) {
            return true;
        } else if (isHasLogs()) {
            return true;
        }
        return false;
    }

    public boolean isHasProperties() {
        List<PropertyValue> propertyValueList = itemElement.getPropertyValueList(); 
        return propertyValueList != null && propertyValueList.isEmpty() == false;
    }

    public boolean isHasLogs() {
        List<Log> logList = itemElement.getLogList(); 
        return logList != null && logList.isEmpty() == false;
    }

    public String getRelatedItemElementToString() {
        return itemElement.toString();
    }

    public String getRowStyle() {
        if (isPreventsDelete() || isPreventUpdateContainedItem()) {
            return "redRow";
        }
        return "greenRow";
    }

    public ItemElement getItemElement() {
        return itemElement;
    }

    public List<ItemElementConstraintInformation> getRelatedConstraintInfo() {
        return relatedConstraintInfo;
    }

}
