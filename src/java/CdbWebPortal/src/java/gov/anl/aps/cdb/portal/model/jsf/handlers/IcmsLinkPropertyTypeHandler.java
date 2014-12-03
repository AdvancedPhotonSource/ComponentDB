package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import static gov.anl.aps.cdb.portal.model.jsf.handlers.EdpLinkPropertyTypeHandler.formatEdpLink;

/**
 *
 * @author sveseli
 */
public class IcmsLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "ICMS Link";

    public IcmsLinkPropertyTypeHandler() {
        super(HANDLER_NAME);
    }

    @Override
    public DisplayType getValueDisplayType() {
        return DisplayType.HTTP_LINK;
    }

    public static String formatIcmsLink(String contentId) {
        //Given a document ContentID # like APS_1273342  or just 1273342, use the link ...
        // https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=APS_1273342

        // Given a drawing ContentID # like U2210207-100102.DRW, use the link ...
        // https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=U2210207-100102.DRW
        String baseUrl = "https://icmsdocs.aps.anl.gov/docs/idcplg?IdcService=DISPLAY_URL&dDocName=";
        if (contentId == null) {
            return null;
        }

        // Drawing
        if (contentId.endsWith("DRW") || contentId.endsWith("drw")) {
            return baseUrl + contentId;
        } else {
            String docId = contentId.replace("APS_", "");
            return baseUrl + "APS_" + docId;
        }
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
