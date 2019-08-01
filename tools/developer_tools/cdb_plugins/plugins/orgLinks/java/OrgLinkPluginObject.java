/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.orgLinks;

import java.util.List;

/**
 *
 * @author djarosz
 */
public class OrgLinkPluginObject {
    
    private String name;
    private String href;
    private List<OrgLinkPluginObject> children; 

    public OrgLinkPluginObject() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<OrgLinkPluginObject> getChildren() {
        return children;
    }

    public void setChildren(List<OrgLinkPluginObject> children) {
        this.children = children;
    }        
    
}
