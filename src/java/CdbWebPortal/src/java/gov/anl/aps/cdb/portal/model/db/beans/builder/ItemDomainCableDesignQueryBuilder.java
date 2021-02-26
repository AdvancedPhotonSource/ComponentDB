/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Dariusz
 */
public class ItemDomainCableDesignQueryBuilder extends ItemQueryBuilder {

    private final String CATALOG_ITEM_FIELD_NAME = "catalogItemString";
    private final String CATALOG_ITEM_ATTRIBUTE = "containedItem2.name"; 
    private final String CABLE_ENDPOINT_NAME = "endpointsString";    

    public ItemDomainCableDesignQueryBuilder(Domain domain, Map filterMap, String sortField, SortOrder sortOrder) {
        super(domain, filterMap, sortField, sortOrder);
    }

    @Override
    protected void handleUnandeledFieldFilter(String key, String value) {
        super.handleUnandeledFieldFilter(key, value);

        switch (key) {
            case CATALOG_ITEM_FIELD_NAME:                
                addSelfElementWhereByAttribute(CATALOG_ITEM_ATTRIBUTE, value);
                break;
            case CABLE_ENDPOINT_NAME:
                String cableRelationshipTypeName = ItemElementRelationshipTypeNames.itemCableConnection.getValue();
                addSecondRelationshipParentItemWhere(key, cableRelationshipTypeName, value);
                break;            
        }
    }

    @Override
    protected String getCoreMetadataPropertyName() {
        return ItemDomainCableDesign.CABLE_DESIGN_INTERNAL_PROPERTY_TYPE; 
    }

}
