/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.view.objects.InventoryItemElementConstraintInformation;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainInventoryControllerUtility extends ItemDomainInventoryBaseControllerUtility<ItemDomainInventory, ItemDomainInventoryFacade> {

    private static final Logger logger = LogManager.getLogger(ItemDomainInventoryControllerUtility.class.getName());

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.inventory.getValue();
    }

    @Override
    protected ItemDomainInventoryFacade getItemFacadeInstance() {
        return ItemDomainInventoryFacade.getInstance();
    }

    @Override
    public String getDerivedFromItemTitle() {
        return "Catalog Item";
    }

    @Override
    public String getEntityTypeName() {
        return "componentInstance";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Inventory Item";
    }

    @Override
    public List<ItemDomainInventory> getItemList() {
        return itemFacade.findByDomainOrderByDerivedFromItem(getDefaultDomainName());
    }

    @Override
    protected ItemDomainInventory instenciateNewItemDomainEntity() {
        return new ItemDomainInventory();
    }

    @Override
    public String getStatusPropertyTypeName() {
        return ItemDomainInventory.ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME;
    }

    @Override
    public List<Item> getParentItemList(ItemDomainInventory itemEntity) {
        List<Item> parentItemList = itemEntity.getParentItemList();
        List<Item> parentItems = super.getParentItemList(itemEntity);
        if (parentItemList != null) {
            return parentItems;
        }

        List<Item> additionalParents = new ArrayList<>();

        // First time executing. Process represented element references. 
        for (Item parentItem : parentItems) {
            if (parentItem instanceof ItemDomainInventory) {
                ItemElement membershipItemElement = parentItem.getMembershipItemElement();
                ItemElement catalogElement = membershipItemElement.getDerivedFromItemElement();
                List<ItemElement> represntedItemElements = catalogElement.getRepresentsItemElementList();
                if (represntedItemElements.isEmpty()) {
                    // No representation set up for current element. 
                    continue;
                }
                List<ItemElement> machineMembers = parentItem.getItemElementMemberList2();
                if (machineMembers.size() != 1) {
                    continue;
                }
                ItemElement machineItemElement = machineMembers.get(0);
                Item machineItem = machineItemElement.getParentItem();
                if (machineItem instanceof ItemDomainMachineDesign == false) {
                    continue;
                }

                for (ItemElement repElement : represntedItemElements) {
                    ItemDomainMachineDesign repParent = (ItemDomainMachineDesign) repElement.getParentItem();
                    ItemDomainMachineDesign repCommonParent = repParent.getParentMachineDesign();

                    if (repCommonParent.equals(machineItem)) {
                        repParent.setMembershipByItem(parentItem);
                        additionalParents.add(repParent);
                        break;
                    }
                }
            }
        }

        parentItems.addAll(additionalParents);

        return parentItems;
    }

    @Override
    public ItemElementConstraintInformation loadItemElementConstraintInformation(ItemElement itemElement) {
        return new InventoryItemElementConstraintInformation(itemElement);
    }

}
