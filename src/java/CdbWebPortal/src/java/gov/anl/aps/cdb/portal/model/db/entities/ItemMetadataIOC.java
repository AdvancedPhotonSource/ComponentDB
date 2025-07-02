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
public class ItemMetadataIOC extends ItemMetadata<ItemDomainMachineDesign> {

    public final static String IOC_ITEM_INTERNAL_PROPERTY_TYPE = "ioc_info_internal_property_type";
    public final static String IOC_ITEM_MACHINE_TAG_KEY = "machineTag";
    public final static String IOC_ITEM_FUNCTION_TAG_KEY = "functionTag";
    public final static String IOC_DEPLOYMENT_STATUS_KEY = "deploymentStatus";

    public final static String REPO_URL_KEY = "repoUrl";

    private String machineTag;
    private String functionTag;
    private String deploymentStatus;

    public ItemMetadataIOC(ItemDomainMachineDesign iocItem) {
        super(iocItem);
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

    public String getDeploymentStatus() throws CdbException {
        if (deploymentStatus == null) {
            deploymentStatus = getCoreMetadataPropertyFieldValue(IOC_DEPLOYMENT_STATUS_KEY);
        }
        return deploymentStatus;
    }

    public void setDeploymentStatus(String deploymentStatus) throws CdbException {
        this.deploymentStatus = deploymentStatus;
        setCoreMetadataPropertyFieldValue(IOC_DEPLOYMENT_STATUS_KEY, deploymentStatus);
    }

    public String getRepoUrl() throws CdbException {
        return getCoreMetadataPropertyFieldValue(REPO_URL_KEY);
    }

    public void setRepoUrl(String repoURL) throws CdbException {
        setCoreMetadataPropertyFieldValue(REPO_URL_KEY, repoURL);
    }

    public String getBootInstructions() {
        PropertyValue pv = getCoreMetadataPropertyValue();

        if (pv != null) {
            return pv.getText();
        }

        return null;
    }

    public void setBootInstructions(String bootInstructions) {
        PropertyValue pv = getCoreMetadataPropertyValue();
        pv.setText(bootInstructions);
    }

}
