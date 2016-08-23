/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/portal/model/jsf/handlers/IcmsLinkPropertyTypeHandler.java $
 *   $Date: 2016-05-11 07:12:20 -0500 (Wed, 11 May 2016) $
 *   $Revision: 1343 $
 *   $Author: djarosz $
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import static gov.anl.aps.cdb.portal.model.jsf.handlers.EdpLinkPropertyTypeHandler.formatEdpLink;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;

/**
 * ICMS link property type handler.
 */
public class IcmsLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "ICMS Link";

    private static final String IcmsUrl = ConfigurationUtility.getPortalProperty(
            CdbProperty.ICMS_URL_STRING_PROPERTY_NAME);

    public IcmsLinkPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.HTTP_LINK);
    }

    public static String formatIcmsLink(String contentId) {
        //Given a document ContentID # like APS_1273342  or just 1273342, use link
        // https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=APS_1273342

        // Given a drawing ContentID # like U2210207-100102.DRW, use link
        // https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=U2210207-100102.DRW
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
        String targetLink = formatEdpLink(propertyValueHistory.getValue());
        propertyValueHistory.setTargetValue(targetLink);
    }
}
