/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.constants.ItemMetadataFieldType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemMetadataIOC;
import static gov.anl.aps.cdb.portal.model.db.entities.ItemMetadataIOC.IOC_ITEM_INTERNAL_PROPERTY_TYPE;
import gov.anl.aps.cdb.portal.view.objects.ItemMetadataPropertyInfo;
import java.util.List;

/**
 *
 * @author darek
 */
public class ItemDomainMachineDesignIOCControllerUtility extends ItemDomainMachineDesignBaseControllerUtility {

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

        return info;
    }

}
