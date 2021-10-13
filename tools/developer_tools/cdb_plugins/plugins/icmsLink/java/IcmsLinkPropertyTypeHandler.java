/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.icmsLink;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * ICMS link property type handler.
 */
public class IcmsLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {
    
    private static final Logger logger = LogManager.getLogger(IcmsLinkPropertyTypeHandler.class.getName());

    public static final String HANDLER_NAME = "ICMS Link";
    public static final String MIME_CONENT_TYPE = "application/octet-stream";

    private static final String IcmsUrl = IcmsLinkPluginManager.getIcmsUrlString(); 
    
    private IcmsWatermarkUtility icmsWatermarkUtility; 

    public IcmsLinkPropertyTypeHandler() {
        // Switch to File Download to utilize the icms watermark utility.
        super(HANDLER_NAME, DisplayType.GENERATED_HTTP_LINK);
        
        icmsWatermarkUtility = new IcmsWatermarkUtility(
                IcmsLinkPluginManager.getSoapEndpointUrl(), 
                IcmsLinkPluginManager.getSoapGetFileActionUrl(), 
                IcmsLinkPluginManager.getSoapUsername(), 
                IcmsLinkPluginManager.getSoapPassword()); 
    }
    
    public StreamedContent fileDownloadActionCommand(PropertyValue propertyValue) {
        try { 
            byte[] stampedPDFByteArray = icmsWatermarkUtility.generateICMSPDFDocument(propertyValue.getValue());
            InputStream inputStream = new ByteArrayInputStream(stampedPDFByteArray); 
            DefaultStreamedContent.Builder builder = DefaultStreamedContent.builder();
            builder.stream(() -> inputStream);
            builder.contentType(MIME_CONENT_TYPE); 
            builder.name(propertyValue.getValue() + ".pdf"); 
            return builder.build(); 
        } catch (CdbException ex) {
            logger.error("ERROR: " + ex.getMessage());
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }
        
        return null; 
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
