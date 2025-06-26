/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.common.exceptions.CdbException;

/**
 *
 * @author djarosz
 */
public class ItemMetadataIOC {

    private ItemDomainMachineDesign iocItem;

    public final static String IOC_ITEM_INTERNAL_PROPERTY_TYPE = "ioc_info_internal_property_type";
    public final static String IOC_ITEM_MACHINE_TAG_KEY = "machineTag";
    public final static String IOC_ITEM_FUNCTION_TAG_KEY = "functionTag";

    private String machineTag;
    private String functionTag;

    public ItemMetadataIOC(ItemDomainMachineDesign iocItem) {
        this.iocItem = iocItem;
    }

    public String getMachineTag() throws CdbException {
        if (machineTag == null) {
            machineTag = getCoreMetadataPropertyFieldValue(IOC_ITEM_MACHINE_TAG_KEY);
        }
        return machineTag;
    }

    public void setMachineTag(String machineTag) throws CdbException {
        this.machineTag = machineTag;
        setCoreMetadataPropertyFieldValue(IOC_ITEM_MACHINE_TAG_KEY, machineTag);
    }

    public String getFunctionTag() throws CdbException {
        if (functionTag == null) {
            functionTag = getCoreMetadataPropertyFieldValue(IOC_ITEM_FUNCTION_TAG_KEY);
        }
        return functionTag;
    }

    public void setFunctionTag(String functionTag) throws CdbException {
        this.functionTag = functionTag;
        setCoreMetadataPropertyFieldValue(IOC_ITEM_FUNCTION_TAG_KEY, functionTag);
    }

    public String getBootInstructions() {
        PropertyValue pv = iocItem.getCoreMetadataPropertyValue();

        if (pv != null) {
            return pv.getText();
        }

        return null;
    }

    public void setBootInstructions(String bootInstructions) {
        PropertyValue pv = iocItem.getCoreMetadataPropertyValue();
        pv.setText(bootInstructions);
    }

    protected String getCoreMetadataPropertyFieldValue(String key) throws CdbException {
        return iocItem.getCoreMetadataPropertyFieldValue(key);
    }

    protected void setCoreMetadataPropertyFieldValue(String key, String value) throws CdbException {
        iocItem.setCoreMetadataPropertyFieldValue(key, value);
    }

}
