/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainMachineDesignControllerUtility extends ItemDomainMachineDesignBaseControllerUtility {

    private static final Logger logger = LogManager.getLogger(ItemDomainMachineDesignControllerUtility.class.getName());

    public ItemDomainMachineDesignControllerUtility() {
        super();
    }

    @Override
    public ItemElement performMachineMove(ItemDomainMachineDesign newParent, ItemDomainMachineDesign child, UserInfo sessionUser) throws CdbException {
        ItemElement representsCatalogElement = child.getRepresentsCatalogElement();

        if (representsCatalogElement != null) {
            throw new CdbException("Cannot move assembly representing machine element.");
        }

        return super.performMachineMove(newParent, child, sessionUser);
    }

    /**
     * Creates a machine element that represents an assembly element in the
     * catalog/inventory domains. 
     * 
     * To Save update parentMachine or create @returns. 
     *
     * @param parentMachine parent machine should be assigned to an assembly.
     * @param childMachine new child item if created externally, otherwise will be created by this method
     * @param catalogElement element to represent from the catalog of assigned
     * item to parent machine.
     * @param creatorUser User creating the new assembly element.
     * @return Newly machine element that represents catalog element
     * @throws gov.anl.aps.cdb.common.exceptions.InvalidArgument
     * @throws gov.anl.aps.cdb.common.exceptions.CdbException
     */
    public ItemDomainMachineDesign createRepresentingMachineForAssemblyElement(
            ItemDomainMachineDesign parentMachine,
            ItemDomainMachineDesign childMachine,
            ItemElement catalogElement, 
            UserInfo creatorUser) throws InvalidArgument, CdbException {
        
        Item parentItem = catalogElement.getParentItem();
        if ((parentItem instanceof ItemDomainCatalog
                || parentItem instanceof ItemDomainInventory) == false) {
            throw new InvalidArgument("Cannot create representing machine element for selected item.");
        }

        // Create new machine placeholder       
        ItemElement machinePlaceholder = null;
        machinePlaceholder = prepareMachinePlaceholder(parentMachine, childMachine, creatorUser);
        parentMachine.getFullItemElementList().add(machinePlaceholder);                

        // Ensure to get catalog element
        ItemElement derivedFromItemElement = catalogElement.getDerivedFromItemElement();
        if (derivedFromItemElement != null) {
            catalogElement = derivedFromItemElement;
        }

        ItemDomainMachineDesign newMachine = (ItemDomainMachineDesign) machinePlaceholder.getContainedItem();
        newMachine.setRepresentsCatalogElement(catalogElement);
        
        // Reverse hierarchy save ... when parent is not saved. 
        List<ItemElement> itemElementMemberList = newMachine.getItemElementMemberList();
        if (itemElementMemberList == null) {
            itemElementMemberList = new ArrayList<>();
        }
        itemElementMemberList.add(machinePlaceholder);
        newMachine.setItemElementMemberList(itemElementMemberList);

        String name = catalogElement.getName();
        newMachine.setName(name);

        // Ensure uniqueness with many element names named E1, E2, ...
        String viewUUID = newMachine.getViewUUID();
        newMachine.setItemIdentifier2(viewUUID);

        // Set default project list from the catalog item
        Item catalogItem = catalogElement.getContainedItem();
        List<ItemProject> itemProjectList = catalogItem.getItemProjectList();
        newMachine.setItemProjectList(itemProjectList);
        
        return newMachine; 
    }

}
