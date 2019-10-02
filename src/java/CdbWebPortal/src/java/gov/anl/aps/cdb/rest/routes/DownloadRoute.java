/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.portal.utilities.StorageUtility;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.FileNotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Path("/Downloads")
@Tag(name = "Downloads")
public class DownloadRoute extends BaseRoute {   
    
    private static final Logger LOGGER = Logger.getLogger(DownloadRoute.class.getName());
    
    @GET
    @Path("/PropertyValue/Image/{imageName}/{scaling}")       
    public Response getImage(@PathParam("imageName") String imageName,@PathParam("scaling") String scaling) throws FileNotFoundException {  
        LOGGER.debug("Fetching " + scaling + " image: " + imageName);
        String fullImageName = imageName+"."+scaling;
        String filePath = StorageUtility.getFileSystemPropertyValueImagePath(fullImageName);
        
        return getFileResponse("Image: " + fullImageName, imageName, filePath);
    }
    
    private Response getFileResponse(String errorFileTypeColonName, String fileName, String storageFilePath) throws FileNotFoundException {
        File file = new File(storageFilePath);
        
        if (file.exists()) {
            ResponseBuilder response = Response.ok((Object) file);
            response.header("Content-Disposition",
                    "attachment; filename=" + fileName);
            return response.build();
        }        
        
        FileNotFoundException fileNotFoundException = new FileNotFoundException(errorFileTypeColonName + " requested was not found."); 
        LOGGER.error(fileNotFoundException);
        throw fileNotFoundException; 
    }
        
}
