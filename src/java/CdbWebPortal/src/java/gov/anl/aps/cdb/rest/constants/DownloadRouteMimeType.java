/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.constants;

public enum DownloadRouteMimeType {

    jpg(new String[]{"jpg", "jpeg"}, "image/jpeg"),
    png(new String[]{"png"}, "image/png"),
    gif(new String[]{"gif"}, "image/gif"),
    pdf(new String[]{"pdf"}, "application/pdf"),
    html(new String[]{"htm", "html"}, "text/html"),
    wildcard(new String[]{""}, "*/*");

    private String[] extension;
    private String mimeType;

    private DownloadRouteMimeType(String[] ext, String value) {
        this.extension = ext;
        this.mimeType = value;
    }

    public static String getTypeForFilename(String fileName) {
        String type = DownloadRouteMimeType.wildcard.mimeType;

        String[] split = fileName.split("[.]");
        String ext = null;         
        if (split.length > 0) {
            ext = split[split.length - 1].toLowerCase();
        } else {
            return type; 
        }
        
        DownloadRouteMimeType[] values = DownloadRouteMimeType.values();
        boolean done = false; 
        for (DownloadRouteMimeType value : values) {
            for (String possibleExt : value.extension) {
                if (possibleExt.equals(ext)) {
                    type = value.mimeType; 
                    done = true; 
                    break;
                }
            }
            if (done) {
                break; 
            }
        }

        return type;
    }
};
