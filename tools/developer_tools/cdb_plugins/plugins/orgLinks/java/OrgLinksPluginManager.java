/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.orgLinks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class OrgLinksPluginManager extends PluginManagerBase {
    
    private static final Properties ORGLINKS_PROPERTIES = getDefaultPropertiesForPlugin("orgLinks"); 
    
    private static final String LINK_HIERARCHY_KEY = "linkHierarchy";     
    
    private static List<OrgLinkPluginObject> ORG_LINKS;
    
    public String getLinkHierarchyJsonTextProperty() {
        return ORGLINKS_PROPERTIES.getProperty(LINK_HIERARCHY_KEY, ""); 
    }        
    
    public String getEdpUrl() {
        return getLinkHierarchyJsonTextProperty(); 
    }        
    
    private void loadLinks() {
        Type resultType = new TypeToken<LinkedList<OrgLinkPluginObject>>(){}.getType(); 
        String json = getLinkHierarchyJsonTextProperty(); 
        
        Gson gson = new GsonBuilder().create();
        ORG_LINKS = gson.fromJson(json, resultType);                 
    }   

    public List<OrgLinkPluginObject> getLinks() {
        if (ORG_LINKS == null) {
            loadLinks(); 
        }
        return ORG_LINKS;
    }
}


