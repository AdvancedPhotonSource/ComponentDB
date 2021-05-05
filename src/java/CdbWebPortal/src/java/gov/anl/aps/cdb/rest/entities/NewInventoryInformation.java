/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;

/**
 *
 * @author darek
 */
public class NewInventoryInformation {

    private Integer catalogId;
    private String tag;
    private String serialNumber;
    private String description;

    private Integer qrId;

    public NewInventoryInformation() {
    }

    public Integer getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId) {
        this.catalogId = catalogId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQrId() {
        return qrId;
    }

    public void setQrId(Integer qrId) {
        this.qrId = qrId;
    }

    public void updateItemDomainInventoryWithInformation(ItemDomainInventory inventoryItem) throws InvalidArgument {
        if (catalogId == null) {
            throw new InvalidArgument("Catalog id must be provided for new inventoy item.");
        }
        ItemDomainCatalogFacade catalogFacade = ItemDomainCatalogFacade.getInstance();
        ItemDomainCatalog catalogItem = catalogFacade.findById(catalogId);

        if (catalogItem == null) {
            throw new InvalidArgument("Invalid catalog item id was provided.");
        }

        inventoryItem.setCatalogItem(catalogItem);
        if (tag != null) {
            inventoryItem.setTag(tag);
        }

        inventoryItem.setSerialNumber(serialNumber);
        inventoryItem.setDescription(description);
        inventoryItem.setQrId(qrId);

    }
}
