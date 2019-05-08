/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.model.jsf.beans.SparePartsBean;
import gov.anl.aps.cdb.portal.view.objects.InventoryBillOfMaterialItem;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "item")
@DiscriminatorValue(value = ItemDomainName.INVENTORY_ID + "")
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
            name = "item.inventoryItemsWithConnectorType",
            procedureName = "inventory_items_with_avaiable_connector",
            resultClasses = Item.class,
            parameters = {
                @StoredProcedureParameter(
                        name = "connector_type_id",
                        mode = ParameterMode.IN,
                        type = Integer.class
                )
                ,
                @StoredProcedureParameter(
                        name = "is_male",
                        mode = ParameterMode.IN,
                        type = Boolean.class
                )
            }
    ),})
public class ItemDomainInventory extends LocatableItem {

    public static final String ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME = "Component Instance Status";
    public static final String ITEM_DOMAIN_INVENTORY_STATUS_SPARE_VALUE = "Spare";
    
    private transient List<InventoryBillOfMaterialItem> inventoryDomainBillOfMaterialList = null;

    private transient TreeNode itemElementAssemblyRootTreeNode = null;

    private transient InventoryBillOfMaterialItem containedInBOM;

    private transient Boolean sparePartIndicator = null;
    private transient SparePartsBean sparePartsBean = null;

    // Inventory status variables
    private transient PropertyValue inventoryStatusPropertyValue;
    private transient boolean loadedCurrentStatusPropertyValue = false;

    @Override
    public Item createInstance() {
        return new ItemDomainInventory();
    }

    public ItemDomainCatalog getCatalogItem() {
        return (ItemDomainCatalog) getDerivedFromItem();
    } 

    @Override
    @JsonIgnore
    public Item getDerivedFromItem() {
        return super.getDerivedFromItem(); //To change body of generated methods, choose Tools | Templates.
    }

    @JsonIgnore
    public List<InventoryBillOfMaterialItem> getInventoryDomainBillOfMaterialList() {
        return inventoryDomainBillOfMaterialList;
    }

    public void setInventoryDomainBillOfMaterialList(List<InventoryBillOfMaterialItem> inventoryDomainBillOfMaterialList) {
        this.inventoryDomainBillOfMaterialList = inventoryDomainBillOfMaterialList;
    }

    @JsonIgnore
    public TreeNode getItemElementAssemblyRootTreeNode() throws CdbException {
        if (itemElementAssemblyRootTreeNode == null) {
            if (getItemElementDisplayList().size() > 0) {
                itemElementAssemblyRootTreeNode = ItemElementUtility.createItemElementRoot(this);
            }
        }
        return itemElementAssemblyRootTreeNode;
    }

    @JsonIgnore
    public InventoryBillOfMaterialItem getContainedInBOM() {
        return containedInBOM;
    }

    public void setContainedInBOM(InventoryBillOfMaterialItem containedInBOM) {
        this.containedInBOM = containedInBOM;
    }

    @JsonIgnore
    public Boolean getSparePartIndicator() {
        if (sparePartIndicator == null) {
            boolean spare = getInventoryStatusValue().equals(ITEM_DOMAIN_INVENTORY_STATUS_SPARE_VALUE);
            sparePartIndicator = spare;
        }
        return sparePartIndicator;
    }

    @JsonIgnore
    public SparePartsBean getSparePartsBean() {
        if (sparePartsBean == null) {
            sparePartsBean = SparePartsBean.getInstance();
        }
        return sparePartsBean;
    }

    @JsonIgnore
    public PropertyValue getInventoryStatusPropertyValue() {
        if (!loadedCurrentStatusPropertyValue) {
            if (this.getPropertyValueInternalList() != null) {
                for (PropertyValue propertyValue : this.getPropertyValueInternalList()) {
                    String propertyTypeName = propertyValue.getPropertyType().getName();
                    if (propertyTypeName.equals(ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME)) {
                        inventoryStatusPropertyValue = propertyValue;
                        break;
                    }
                }
            }
            loadedCurrentStatusPropertyValue = true;
        }
        return inventoryStatusPropertyValue;
    }

    public void setInventoryStatusPropertyValue(PropertyValue inventoryStatusPropertyValue) {
        this.inventoryStatusPropertyValue = inventoryStatusPropertyValue;
    }

    @JsonIgnore
    public String getInventoryStatusValue() {
        if (getInventoryStatusPropertyValue() != null) {
            String value = getInventoryStatusPropertyValue().getValue();
            if (value != null) {
                return value;
            }
        }
        return "";
    }

    public void setInventoryStatusValue(String status) {
        if (getInventoryStatusPropertyValue() != null) {
            getInventoryStatusPropertyValue().setValue(status);
            sparePartIndicator = null;
        }
    }

}
