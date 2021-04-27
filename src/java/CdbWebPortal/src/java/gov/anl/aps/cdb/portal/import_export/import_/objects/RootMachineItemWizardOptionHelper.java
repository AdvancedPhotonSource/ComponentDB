/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class RootMachineItemWizardOptionHelper {
    
    private String optionRootItemName = null;
    private ItemDomainMachineDesign rootItem = null;

    public String getOptionRootItemName() {
        return optionRootItemName;
    }

    public void setOptionRootItemName(String optionRootItemName) {
        this.optionRootItemName = optionRootItemName;
    }
    
    public ItemDomainMachineDesign getRootItem() {
        return rootItem;
    }
    
    public void reset() {
        rootItem = null;
        optionRootItemName = null;
    }
    
    public static HelperWizardOption rootMachineItemWizardOption() {
        return new HelperWizardOption(
                "Default Root Machine Item",
                "Root of machine hierarchy to locate items within, used to locate items by name without specifying full path.",
                "optionRootItemName",
                HelperOptionType.STRING,
                ImportMode.CREATE);
    }

    public ValidInfo validate() {

        boolean isValid = true;
        String validString = "";

        if (optionRootItemName != null) {
            if ((!optionRootItemName.isEmpty())) {
                List<ItemDomainMachineDesign> topLevelMatches = new ArrayList<>();
                List<ItemDomainMachineDesign> matchingItems
                        = ItemDomainMachineDesignFacade.getInstance().findByName(optionRootItemName);
                for (ItemDomainMachineDesign item : matchingItems) {
                    if (item.getParentMachineDesign() == null) {
                        // top-level item matches name
                        topLevelMatches.add(item);
                    }
                }

                if (topLevelMatches.size() == 1) {
                    rootItem = topLevelMatches.get(0);
                } else if (topLevelMatches.size() == 0) {
                    isValid = false;
                    validString = "no matching top-level machine item with name: " + optionRootItemName;
                } else {
                    // more than one match
                    isValid = false;
                    validString = "multiple matching top-level machine items with name: " + optionRootItemName;
                }
            } else {
                // null out option if empty string
                optionRootItemName = null;
            }
        }

        return new ValidInfo(isValid, validString);
    }
}
