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

    private String preBoot = null;
    private String postBoot = null;
    private String powerCycle = null;
    private String additionalMd = null;

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

    public String getPreBoot() {
        loadInstructions();
        return preBoot;
    }

    public void setPreBoot(String preBoot) {
        this.preBoot = preBoot;
    }

    public String getPostBoot() {
        loadInstructions();
        return postBoot;
    }

    public void setPostBoot(String postBoot) {
        this.postBoot = postBoot;
    }

    public String getPowerCycle() {
        loadInstructions();
        return powerCycle;
    }

    public void setPowerCycle(String powerCycle) {
        this.powerCycle = powerCycle;
    }

    public String getAdditionalMd() {
        loadInstructions();
        return additionalMd;
    }

    public void setAdditionalMd(String additionalMd) {
        this.additionalMd = additionalMd;
    }

    private void loadInstructions() {
        if (preBoot != null) {
            // Loaded
            return;
        }

        String bootInstructions = getBootInstructions();

        if (bootInstructions != null) {
            int preBootStart = bootInstructions.indexOf("# Preboot:");
            int preBootEnd = bootInstructions.indexOf("<!--/Preboot-->");
            if (preBootStart != -1 && preBootEnd != -1) {
                preBoot = bootInstructions.substring(preBootStart + "# Preboot:".length(), preBootEnd).trim();
            }

            int postBootStart = bootInstructions.indexOf("# Postboot:");
            int postBootEnd = bootInstructions.indexOf("<!--/Postboot-->");
            if (postBootStart != -1 && postBootEnd != -1) {
                postBoot = bootInstructions.substring(postBootStart + "# Postboot:".length(), postBootEnd).trim();
            }

            int powerCycleStart = bootInstructions.indexOf("# Power Cycle:");
            int powerCycleEnd = bootInstructions.indexOf("<!--/PowerCycle-->");
            if (powerCycleStart != -1 && powerCycleEnd != -1) {
                powerCycle = bootInstructions.substring(powerCycleStart + "# Power Cycle:".length(), powerCycleEnd).trim();
            }

            int additionalMdStart = bootInstructions.indexOf("<!--Additional-->");
            int additionalMdEnd = bootInstructions.indexOf("<!--/Additional-->");
            if (additionalMdStart != -1 && additionalMdEnd != -1) {
                additionalMd = bootInstructions.substring(additionalMdStart + "<!--Additional-->".length(), additionalMdEnd).trim();
            }
        }
    }
    
    public void generateUpdatedInstructionsMarkdown() {
        StringBuilder bootInstructions = new StringBuilder();

        bootInstructions.append("# Preboot: ");
        if (preBoot != null && !preBoot.isEmpty()) {
            bootInstructions.append(preBoot);
        }
        bootInstructions.append("<!--/Preboot-->");

        bootInstructions.append("# Postboot: ");
        if (postBoot != null && !postBoot.isEmpty()) {
            bootInstructions.append(postBoot);
        }
        bootInstructions.append("<!--/Postboot-->");

        bootInstructions.append("# Power Cycle: ");
        if (powerCycle != null && !powerCycle.isEmpty()) {
            bootInstructions.append(powerCycle);
        }
        bootInstructions.append("<!--/PowerCycle-->");

        bootInstructions.append("<!--Additional-->");
        if (additionalMd != null && !additionalMd.isEmpty()) {
            if (!additionalMd.startsWith("#")) {
                bootInstructions.append("# Additional Notes");
            }
            bootInstructions.append(additionalMd);
        }
        bootInstructions.append("<!--/Additional-->");
        setBootInstructions(bootInstructions.toString().trim());
    }

    public String getBootInstructions() {
        PropertyValue pv = getCoreMetadataPropertyValue();

        if (pv != null) {
            return pv.getText();
        }

        return null;
    }

    private void setBootInstructions(String bootInstructions) {
        PropertyValue pv = getCoreMetadataPropertyValue();
        pv.setText(bootInstructions);
    }

}
