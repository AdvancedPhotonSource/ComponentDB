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
public class DomainImportInfo {
    
    private List<ImportFormatInfo> formatInfoList;
    private String completionUrl;
    
    public DomainImportInfo(List<ImportFormatInfo> formatList, String url) {
        formatInfoList = formatList;
        completionUrl = url;
    }

    public List<ImportFormatInfo> getFormatInfoList() {
        return formatInfoList;
    }

    public void setFormatInfoList(List<ImportFormatInfo> formatInfoList) {
        this.formatInfoList = formatInfoList;
    }

    public String getCompletionUrl() {
        return completionUrl;
    }

    public void setCompletionUrl(String completionUrl) {
        this.completionUrl = completionUrl;
    }
    
}
