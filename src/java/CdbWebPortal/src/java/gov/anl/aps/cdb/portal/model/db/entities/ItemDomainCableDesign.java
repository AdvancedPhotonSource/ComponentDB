/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.CABLE_DESIGN_ID + "")
public class ItemDomainCableDesign extends Item {

    @Override
    public Item createInstance() {
        return new ItemDomainCableDesign();
    }

    public List<Item> getEndpointList() {
        {
            ItemElement selfElement = this.getSelfElement();
            List<ItemElementRelationship> ierList
                    = selfElement.getItemElementRelationshipList1();
            if (ierList != null) {
                // find just the cable relationship items
                RelationshipType cableIerType
                        = RelationshipTypeFacade.getInstance().findByName(
                                ItemElementRelationshipTypeNames.itemCableConnection.getValue());
                if (cableIerType != null) {
                    if (ierList.size() > 0) {
                        System.out.println(ierList.get(0).getRelationshipType().getName().equals(cableIerType.getName()));
                    }
                    return ierList.stream().
                            filter(ier -> ier.getRelationshipType().getName().equals(cableIerType.getName())).
                            map(ier -> ier.getFirstItemElement().getParentItem()).
                            collect(Collectors.toList());
                }
            }

            return null;
        }
    }

    public Item getEndpoint1() {
        List<Item> iList = this.getEndpointList();
        if (iList.size() > 0) {
            return iList.get(0);
        }
        else {
            return null;
        }
    }

    public String getEndpoint1String() {
        Item iEndpoint1 = this.getEndpoint1();
        if (iEndpoint1 != null) {
            return iEndpoint1.getName();
        } else {
            return "";
        }
    }

    public Item getEndpoint2() {
        List<Item> iList = this.getEndpointList();
        if (iList.size() > 1) {
            return iList.get(1);
        }
        else {
            return null;
        }
    }

    public String getEndpoint2String() {
        Item iEndpoint2 = this.getEndpoint2();
        if (iEndpoint2 != null) {
            return iEndpoint2.getName();
        } else {
            return "";
        }
    }

}
