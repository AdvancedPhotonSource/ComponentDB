/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
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
public class ItemDomainInventory extends ItemDomainInventoryBase<ItemDomainCatalog> {

    public static final String ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME = "Component Instance Status";
    public static final String ITEM_DOMAIN_INVENTORY_STATUS_SPARE_VALUE = "Spare";
    
    private transient List<InventoryBillOfMaterialItem> inventoryDomainBillOfMaterialList = null;

    private transient TreeNode itemElementAssemblyRootTreeNode = null;

    private transient InventoryBillOfMaterialItem containedInBOM;

    private transient SparePartsBean sparePartsBean = null;
    
    @Override
    public Item createInstance() {
        return new ItemDomainInventory();
    }
    
    @JsonIgnore
    public String getStatusPropertyTypeName() {
        return ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME;
    }

    public static String generatePaddedUnitName(int itemNumber) {
        return String.format("Unit: %04d", itemNumber);
    }
    
    @Override
    // TODO API Change back to json ignore and utilize the catalog item 
    //@JsonIgnore
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

    @Override
    public ItemController getItemDomainController() {
        return ItemDomainInventoryController.getInstance(); 
    }
    
    /**
     * This method is redundant to the generic method defined in the superclass,
     * ItemDomainInventoryBase.  It is needed here because the import wizard
     * uses reflection to invoke the setter method, and apparently the generic
     * method is not a valid match for invocation by reflection.
     * @param catalogItem 
     */
    public void setCatalogItem(ItemDomainCatalog catalogItem) {
        super.setCatalogItem(catalogItem);
    }

    public String getTag() {
        return getName();
    }

    public void setTag(String name) {
        setName(name);
    }

    public String getSerialNumber() {
        return this.getItemIdentifier1();
    }
    
    public void setSerialNumber(String serialNumber) {
        this.setItemIdentifier1(serialNumber);
    }
    
}
