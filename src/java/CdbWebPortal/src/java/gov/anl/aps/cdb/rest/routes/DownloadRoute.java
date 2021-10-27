/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.constants.CdbPropertyValue;
import gov.anl.aps.cdb.common.exceptions.InvalidRequest;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.DocumentPropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.jsf.handlers.ImagePropertyTypeHandler;
import gov.anl.aps.cdb.portal.utilities.StorageUtility;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.FileNotFoundException;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Path("/Downloads")
@Tag(name = "Downloads")
public class DownloadRoute extends BaseRoute {

    private static final Logger LOGGER = LogManager.getLogger(DownloadRoute.class.getName());

    @EJB
    PropertyValueFacade propertyValueFacade;

    @GET
    @Path("/PropertyValue/Image/{imageName}/{scaling}")
    public Response getImage(@PathParam("imageName") String imageName, @PathParam("scaling") String scaling) throws FileNotFoundException {
        LOGGER.debug("Fetching " + scaling + " image: " + imageName);
        String fullImageName = imageName + "." + scaling;
        String filePath = StorageUtility.getFileSystemPropertyValueImagePath(fullImageName);

        return getFileResponse("Image: " + fullImageName, imageName, filePath, true);
    }

    @GET
    @Path("/PropertyValue/{propertyValueId}")
    @Produces("image/png")
    public Response getDownloadByPropertyValueId(@PathParam("propertyValueId") Integer propertyValueId) throws FileNotFoundException, ObjectNotFound, InvalidRequest {
        PropertyValue result = propertyValueFacade.find(propertyValueId);
        if (result == null) {
            throw new ObjectNotFound("Could not find a property value with id: " + propertyValueId);
        }

        String storedFileName = result.getValue();
        String originalFileName = result.getDisplayValue();

        PropertyTypeHandler propertyTypeHandler = result.getPropertyType().getPropertyTypeHandler();

        // false is document, true is image 
        Boolean imageFileType = null;

        if (propertyTypeHandler != null) {
            String name = propertyTypeHandler.getName();
            String documentHandlerName = DocumentPropertyTypeHandler.HANDLER_NAME;
            String imageHandlerName = ImagePropertyTypeHandler.HANDLER_NAME;

            if (name.equals(documentHandlerName)) {
                imageFileType = false;
            } else if (name.equals(imageHandlerName)) {
                imageFileType = true;
            }
        }

        if (imageFileType == null) {
            throw new InvalidRequest("Property value provided is neither a document or image upload type property value.");
        }
        
        String filePath = ""; 

        if (imageFileType) {
            String scaling = CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION;
            LOGGER.debug("Fetching " + scaling + " image: " + originalFileName);
            String fullImageName = storedFileName + scaling;
            filePath = StorageUtility.getFileSystemPropertyValueImagePath(fullImageName);
        } else {
            filePath = StorageUtility.getFileSystemPropertyValueDocumentPath(storedFileName);
        }

        return getFileResponse("Image: " + originalFileName, originalFileName, filePath, !imageFileType);
    }

    private Response getFileResponse(String errorFileTypeColonName, String fileName, String storageFilePath, boolean isAttachment) throws FileNotFoundException {
        File file = new File(storageFilePath);

        if (file.exists()) {
            String headerObject = "";
            if (isAttachment) {
                headerObject += "attachment; "; 
            }
            headerObject += "filename=" + fileName; 
            
            ResponseBuilder response = Response.ok((Object) file);
            response.header("Content-Disposition",
                    headerObject);
            return response.build();
        }

        FileNotFoundException fileNotFoundException = new FileNotFoundException(errorFileTypeColonName + " requested was not found.");
        LOGGER.error(fileNotFoundException);
        throw fileNotFoundException;
    }

}
