package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.api.PdmLinkApi;
import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.objects.PdmLinkDrawing;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import org.apache.log4j.Logger;

/**
 *
 * @author sveseli
 */
public class PdmLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    private static final Logger logger = Logger.getLogger(PdmLinkPropertyTypeHandler.class.getName());

    public static final String HANDLER_NAME = "PDMLink";

    private PdmLinkApi pdmLinkApi;

    public PdmLinkPropertyTypeHandler() {
        super(HANDLER_NAME);
        String webServiceUrl = ConfigurationUtility.getPortalProperty(CdbProperty.WEB_SERVICE_URL_PROPERTY_NAME);
        try {
            pdmLinkApi = new PdmLinkApi(webServiceUrl);
        } catch (ConfigurationError ex) {
            String error = "PDMLink Service is not accessible:  " + ex.getErrorMessage();
            logger.error(error);
            SessionUtility.addErrorMessage("Error", error);
        }
    }

    @Override
    public DisplayType getValueDisplayType() {
        return DisplayType.HTTP_LINK;
    }

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        String targetLink = getDrawingWindchillUrl(propertyValue.getValue(), true);
        propertyValue.setTargetValue(targetLink);
    }

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        String targetLink = getDrawingWindchillUrl(propertyValueHistory.getValue(), false);
        propertyValueHistory.setTargetValue(targetLink);
    }

    private String getDrawingWindchillUrl(String drawingName, boolean displayErrorIfNotFound) {
        if (drawingName == null || drawingName.isEmpty()) {
            return null;
        }

        if (pdmLinkApi == null) {
            logger.error("Cannot get windchill url for drawing: " + drawingName + ": PDMLink Service is not accesible.");
            return null;
        }

        String windchillUrl = null;
        try {
            logger.debug("Searching for drawing: " + drawingName);
            PdmLinkDrawing drawing = pdmLinkApi.getDrawing(drawingName);
            windchillUrl = drawing.getWindchillUrl();
            logger.debug("Found drawing, windchill URL: " + windchillUrl);
        } catch (CdbException ex) {
            if (displayErrorIfNotFound) {
                // Avoid multiple errors for history
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            }
        }
        return windchillUrl;
    }
}
