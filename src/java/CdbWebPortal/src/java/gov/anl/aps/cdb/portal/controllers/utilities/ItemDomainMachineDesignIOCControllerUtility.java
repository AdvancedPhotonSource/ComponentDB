/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import java.util.List;

import gov.anl.aps.cdb.common.constants.ItemMetadataFieldType;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemMetadataIOC;
import static gov.anl.aps.cdb.portal.model.db.entities.ItemMetadataIOC.IOC_ITEM_INTERNAL_PROPERTY_TYPE;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.view.objects.ItemMetadataPropertyInfo;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainMachineDesignIOCControllerUtility extends ItemDomainMachineDesignBaseControllerUtility {

    private static final Logger logger = LogManager.getLogger(ItemDomainMachineDesignIOCControllerUtility.class.getName());

    @Override
    public List<ItemDomainMachineDesign> getItemList() {
        return itemFacade.getIOCItems();
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "IOC Item";
    }

    @Override
    public ItemMetadataPropertyInfo createCoreMetadataPropertyInfo() {

        ItemMetadataPropertyInfo info
                = new ItemMetadataPropertyInfo("IOC Metadata", IOC_ITEM_INTERNAL_PROPERTY_TYPE);

        info.setDefaultPropertyValue("IOC Instructions");

        info.setDefaultPropertyText("# Preboot:\n"
                + "<!--/Preboot-->\n"
                + "\n"
                + "# Postboot:\n"
                + "<!--/Postboot-->\n"
                + "\n"
                + "# Power Cycle:\n"
                + "<!--/PowerCycle-->\n"
                + "\n"
                + "<!--Additional-->\n"
                + "<!--/Additional-->");

        info.addField(
                ItemMetadataIOC.IOC_ITEM_MACHINE_TAG_KEY,
                "Machine Tag",
                "Machine Tag.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                "Details");

        info.addField(
                ItemMetadataIOC.IOC_ITEM_FUNCTION_TAG_KEY,
                "Function Tag",
                "Function Tag.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                "Details");

        info.addField(
                ItemMetadataIOC.IOC_DEPLOYMENT_STATUS_KEY,
                "Deployment Status",
                "IOC Deployment Status",
                ItemMetadataFieldType.STRING,
                "",
                List.of("Production", "Ancillary", "Test Stand", "Development", "Planned", "Inactive"),
                "Details");

        return info;
    }

    @Override
    protected void assignEntityTypeForNewInstance(ItemDomainMachineDesign ioc) {
        // Assign IOC type
        String iocEntityTypeName = EntityTypeName.ioc.getValue();
        EntityType iocEntityType = entityTypeFacade.findByName(iocEntityTypeName);
        try {
            ioc.setEntityTypeList(new ArrayList<>());
        } catch (CdbException ex) {
            logger.error(ex);
        }
        ioc.getEntityTypeList().add(iocEntityType);
    }

    public void updateMachineParent(ItemDomainMachineDesign iocItem, UserInfo userInfo, ItemDomainMachineDesign parentMachine) throws CdbException {
        if (!ItemDomainMachineDesign.isItemIOC(iocItem)) {
            throw new CdbException("IOC item is not of type ioc");
        }

        ItemElement parentMachineElement = iocItem.getParentMachineElement();
        if (parentMachineElement == null) {
            parentMachineElement = createItemElement(iocItem, userInfo);
            parentMachineElement.setContainedItem(iocItem);

            iocItem.setParentMachineElement(parentMachineElement);
        }

        String elementName = generateUniqueElementNameForItem(parentMachine);
        parentMachineElement.setName(elementName);

        parentMachineElement.setParentItem(parentMachine);

    }

    @Override
    protected boolean isApplyUniqueness() {
        return false;
    }

    @Override
    public boolean isEntityHasItemIdentifier1() {
        return false;
    }

    @Override
    public boolean isEntityHasQrId() {
        return false;
    }

    @Override
    protected void prepareEntityDestroy(ItemDomainMachineDesign item, UserInfo userInfo) throws CdbException {
        // First destroy parent
        ItemElement parentMachineElement = item.getParentMachineElement();
        if (parentMachineElement != null) {
            parentMachineElement.setMarkedForDeletion(true);
        }

        super.prepareEntityDestroy(item, userInfo);
    }

    @Override
    public void checkItem(ItemDomainMachineDesign item) throws CdbException {
        super.checkItem(item);

        ItemDomainMachineDesign parentMachineDesign = item.getParentMachineDesign();

        if (parentMachineDesign != null && parentMachineDesign.getIsItemIOC()) {
            throw new CdbException("The IOC cannot be housed by another IOC");
        }
    }

    @Override
    public void updateAssignedItem(ItemDomainMachineDesign mdItem, Item newAssignment, UserInfo userInfo, Boolean isInstalled) throws CdbException {
        throw new CdbException("Not supported for IOC type items.");
    }

    @Override
    public ItemElement prepareMachinePlaceholder(ItemDomainMachineDesign parentMachine, UserInfo sessionUser) throws CdbException {
        throw new CdbException("IOCs cannot have machine design children");
    }

}
