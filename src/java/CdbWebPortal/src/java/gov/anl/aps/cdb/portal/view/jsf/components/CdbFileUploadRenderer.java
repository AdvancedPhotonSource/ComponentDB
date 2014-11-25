
package gov.anl.aps.cdb.portal.view.jsf.components;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.fileupload.FileUploadRenderer;

/**
 *
 * Solution for file uploader issue: servlet exception being thrown if request content type is not multipart/form-data
 * 
 * See: http://stackoverflow.com/questions/19262356/file-upload-doesnt-work-with-ajax-in-primefaces-4-0-jsf-2-2-x-javax-servlet-s/19752138#19752138
 */
public class CdbFileUploadRenderer extends FileUploadRenderer 
{
    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (context.getExternalContext().getRequestContentType().toLowerCase().startsWith("multipart/")) {
            super.decode(context, component);
        }
    }
}

