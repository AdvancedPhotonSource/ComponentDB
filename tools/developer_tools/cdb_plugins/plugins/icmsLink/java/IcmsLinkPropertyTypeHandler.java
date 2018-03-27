/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.icmsLink;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;

/**
 * ICMS link property type handler.
 */
public class IcmsLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "ICMS Link";

    private static final String IcmsUrl = IcmsLinkPluginManager.getIcmsUrlString(); 

    public IcmsLinkPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.GENERATED_HTTP_LINK);
    }

    public static String formatIcmsLink(String contentId) {
        //Given a document ContentID # like APS_1273342  or just 1273342, use link
        // https://icms.....DocName=APS_1273342

        // Given a drawing ContentID # like U2210207-100102.DRW, use link
        // https://icms.....DocName=U2210207-100102.DRW
        if (contentId == null) {
            return null;
        }

        // Drawing
        String docId = contentId;
        if (!contentId.endsWith("DRW") && !contentId.endsWith("drw") &&
                !contentId.endsWith("DWG") && !contentId.endsWith("dwg")
                && !contentId.startsWith("APSU")) {
            docId = contentId.replace("APS_", "");
            docId = "APS_" + docId;
        }
        return IcmsUrl.replace("CONTENT_ID", docId);
    }

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        String targetLink = formatIcmsLink(propertyValue.getValue());
        propertyValue.setTargetValue(targetLink);
    }

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        String targetLink = formatIcmsLink(propertyValueHistory.getValue());
        propertyValueHistory.setTargetValue(targetLink);
    }
}
