package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.api.PdmLinkApi;
import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.objects.PdmLinkDrawing;
import gov.anl.aps.cdb.common.objects.PdmLinkDrawingRevision;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;


@Named("pdmLinkDrawingBean")
@SessionScoped
public class PdmLinkDrawingBean implements Serializable {
    
    private static final Logger logger = Logger.getLogger(PdmLinkDrawingBean.class.getName());
    
    private String drawingName;
    private PdmLinkDrawing drawing;
    private PdmLinkApi pdmLinkApi;
    
    @PostConstruct
    public void init() {
        String webServiceUrl = ConfigurationUtility.getPortalProperty(CdbProperty.WEB_SERVICE_URL_PROPERTY_NAME);
        try {
            pdmLinkApi = new PdmLinkApi(webServiceUrl);
        } catch (ConfigurationError ex) {
            String error = "PDMLink Service is not accessible:  " + ex.getErrorMessage();
            logger.error(error);            
            SessionUtility.addErrorMessage("Error", error);
        }
    }
    
    public PdmLinkDrawing getDrawing() {
        return drawing;
    }
    
    public void setDrawingName(String drawingName) {
        this.drawingName = drawingName;
    }
    
    public String getDrawingName() {
        return drawingName;
    }
    
    public List<PdmLinkDrawingRevision> getDrawingRevisionList() {
        if (drawing == null) {
            return null;
        }
        return drawing.getRevisionList();
    }
    
    public void findDrawing() {
        drawing = null;
        if (pdmLinkApi == null) {
            SessionUtility.addErrorMessage("Error", "PDMLink Service is not accessible.");   
            return;
        }
        
        if (drawingName != null && !drawingName.isEmpty()) {
            if (!PdmLinkDrawing.isExtensionValid(drawingName)) {
                SessionUtility.addWarningMessage("Warning", "Valid drawing name extensions are: " + PdmLinkDrawing.VALID_EXTENSION_LIST);   
                return;
            }
            
            try {
                logger.debug("Searching for drawing: " + drawingName);
                drawing = pdmLinkApi.getDrawing(drawingName);
                logger.debug("Found drawing, windchill URL: " + drawing.getWindchillUrl());
            } catch (CdbException ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            }
        }
        else {
            SessionUtility.addWarningMessage("Warning", "Drawing name cannot be empty.");   
        }
    }
}
