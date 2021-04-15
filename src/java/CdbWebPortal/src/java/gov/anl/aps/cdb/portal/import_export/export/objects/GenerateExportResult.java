/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author craig
 */
public class GenerateExportResult {

    public ValidInfo getValidInfo() {
        return validInfo;
    }

    public void setValidInfo(ValidInfo validInfo) {
        this.validInfo = validInfo;
    }

    public StreamedContent getContent() {
        return content;
    }

    public void setContent(StreamedContent content) {
        this.content = content;
    }
    
    private ValidInfo validInfo;
    private StreamedContent content;
    
    public GenerateExportResult(ValidInfo validInfo, StreamedContent content) {
        this.validInfo = validInfo;
        this.content = content;
    }
    
}
