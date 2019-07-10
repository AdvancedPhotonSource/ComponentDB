/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

/**
 *
 * @author djarosz
 */
public class FileUploadObject {
    
    private String fileName;
    private String base64Binary;        

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBase64Binary() {
        return base64Binary;
    }

    public void setBase64Binary(String base64Binary) {
        this.base64Binary = base64Binary;
    }

}
