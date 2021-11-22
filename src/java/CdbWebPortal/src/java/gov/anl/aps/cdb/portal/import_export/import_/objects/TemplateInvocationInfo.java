/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.view.objects.KeyValueObject;
import java.util.List;

/**
 *
 * @author craig
 */
public class TemplateInvocationInfo {
    
    private ItemDomainMachineDesign template;
    private List<KeyValueObject> varNameList;
    private ValidInfo validInfo;

    public TemplateInvocationInfo(ItemDomainMachineDesign template, List<KeyValueObject> varNameList, ValidInfo validInfo) {
        this.template = template;
        this.varNameList = varNameList;
        this.validInfo = validInfo;
    }

    public TemplateInvocationInfo(
            ItemDomainMachineDesign template, List<KeyValueObject> varNameList, boolean isValid, String validString) {
        this(template, varNameList, new ValidInfo(isValid, validString));
    }

    public ItemDomainMachineDesign getTemplate() {
        return template;
    }
    
    public List<KeyValueObject> getVarNameList() {
        return varNameList;
    }

    public ValidInfo getValidInfo() {
        return validInfo;
    }
    
    public boolean isValid() {
        if (validInfo != null) {
            return validInfo.isValid;
        } else {
            return false;
        }
    }

}
