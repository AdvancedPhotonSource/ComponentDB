/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import java.util.List;

/**
 *
 * @author craig
 */
public class DomainImportExportInfo {
    
    private List<ImportExportFormatInfo> formatInfoList;
    private String completionUrl;
    
    public DomainImportExportInfo(List<ImportExportFormatInfo> formatList, String url) {
        formatInfoList = formatList;
        completionUrl = url;
    }

    public List<ImportExportFormatInfo> getFormatInfoList() {
        return formatInfoList;
    }

    public void setFormatInfoList(List<ImportExportFormatInfo> formatInfoList) {
        this.formatInfoList = formatInfoList;
    }

    public String getCompletionUrl() {
        return completionUrl;
    }

    public void setCompletionUrl(String completionUrl) {
        this.completionUrl = completionUrl;
    }
    
}
