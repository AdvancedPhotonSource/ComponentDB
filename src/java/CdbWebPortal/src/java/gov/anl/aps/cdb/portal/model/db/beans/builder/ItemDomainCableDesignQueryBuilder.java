/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Dariusz
 */
public class ItemDomainCableDesignQueryBuilder extends ItemQueryBuilder {

    private static final String CATALOG_ITEM_FIELD_NAME = "CableType";
    private static final String CATALOG_ITEM_ATTRIBUTE = "containedItem2.name"; 
    private static final String CABLE_ENDPOINT_NAME = "Endpoints";
    private static final String CONNECTION_DEVICE = "ConnectionDevice";
    private static final String CONNECTION_CONNECTED_DEVICES = "ConnectionConnectedDevices";
    private static final String CONNECTION_PORT = "ConnectionPort";
    private static final String CONNECTION_CONNECTOR = "ConnectionConnector";
    private static final String END1 = "End1";
    private static final String END2 = "End2";

    public ItemDomainCableDesignQueryBuilder(Integer domainId, Map filterMap, String sortField, SortOrder sortOrder, ItemSettings scopeSettings) {
        super(domainId, filterMap, sortField, sortOrder, scopeSettings);
    }
    
    private String getCableRelationshipDbFieldNameForFilterKey(String key) {
        String fieldName = null; 
        if (key.startsWith(CONNECTION_CONNECTOR)) {
            fieldName = "secondItemConnector.connector.name";
        } else if (key.startsWith(CONNECTION_PORT)) {
            fieldName = "firstItemConnector.connector.name";
        } else if (key.startsWith(CONNECTION_DEVICE)) {
            fieldName = "firstItemElement.parentItem.name";
        } else if (key.startsWith(CONNECTION_CONNECTED_DEVICES)) {
            fieldName = "firstItemElement.parentItem.name";
        }
        
        return fieldName;         
    } 
    
    private void addCableRelationshipSortFilter(String field, String fieldName, String filter_value) {
        String cableRelationshipTypeName = ItemElementRelationshipTypeNames.itemCableConnection.getValue();
        if (filter_value != null) {
            // Handle as a filter
            addSecondRelationshipWhere(field, cableRelationshipTypeName, fieldName, filter_value);
        } else {
            // Handle as a sort
            prepareSecondRelationshipQueryByRelationshipName(sortField, cableRelationshipTypeName);
        }
    }        
    
    private boolean addCableRelationshipPropertyForSortFilter(String key) {
        if (getCableRelationshipDbFieldNameForFilterKey(key) != null) {
            String cableEnd = CdbEntity.VALUE_CABLE_END_1;
            if (key.endsWith(END2)) {
                cableEnd = CdbEntity.VALUE_CABLE_END_2;
            }
            String pvlParentName = key; 
            addPropertyWhereByTypeName(pvlParentName, CdbEntity.CABLE_END_DESIGNATION_PROPERTY_TYPE, "pvlCableEnd", cableEnd);
            
            return true;
        } 

        return false; 
    }

    @Override
    protected void handleUnandeledFieldFilter(String key, String value) {        
        super.handleUnandeledFieldFilter(key, value);        
        
        // handle connection details columns
        if (addCableRelationshipPropertyForSortFilter(key)) {
            String fieldName = getCableRelationshipDbFieldNameForFilterKey(key); 
            addCableRelationshipSortFilter(key, fieldName, value);
        } else {
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
    }

    @Override
    protected String handleUnhandeledSortField() {                
        if (addCableRelationshipPropertyForSortFilter(sortField)) {            
            String fieldName = getCableRelationshipDbFieldNameForFilterKey(sortField); 
            if (fieldName != null) {
                addCableRelationshipSortFilter(sortField, fieldName, null);                                                 

                return sortField + "." + fieldName;
            }
        } else if (sortField.equals(CATALOG_ITEM_FIELD_NAME)) {            
            String fullSortField = getSelfElementNameField(CATALOG_ITEM_ATTRIBUTE);
            includeFiel();
            return fullSortField;
        }
        
        return super.handleUnhandeledSortField();
    }        

    @Override
    protected String getCoreMetadataPropertyName() {
        return ItemDomainCableDesign.CABLE_DESIGN_INTERNAL_PROPERTY_TYPE; 
    }

}
