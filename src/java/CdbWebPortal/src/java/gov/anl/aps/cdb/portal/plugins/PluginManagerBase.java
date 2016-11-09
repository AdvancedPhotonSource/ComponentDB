/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import java.util.Properties;

/**
 *
 * @author djarosz
 */
public abstract class PluginManagerBase {

    protected static final String PLUGIN_BASE_PATH = "gov/anl/aps/cdb/portal/plugins/support";

    protected PropertyTypeHandlerInterface propertyTypeHandler = null;

    protected String pluginName = null;

    /**
     * A Plug-in may contain a property type handler for special display
     * capabilities in the system.
     *
     * @return null if no property type Handler exists. Return corresponding
     * property type handler for property of plugin.
     */
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        return propertyTypeHandler;
    }

    /**
     * Define function if it requires loading of information for a pop-up dialog
     * on details of property value. Loads information required for a info
     * dialog on a property value.
     *
     * @param propertyValue - property value to load the information for.
     */
    public void performInfoActionLoad(PropertyValue propertyValue) {

    }

    /**
     * Based on the naming convention of PluginManager, this gets a plug-in
     * name.
     *
     * @return
     */
    public String getPluginName() {
        if (pluginName == null) {
            pluginName = generatePluginName();
        }
        return pluginName;
    }

    /**
     * Based on the naming convention of PluginManager, this generates a plug-in
     * name.
     *
     * @return
     */
    protected String generatePluginName() {
        String generatedPluginName = this.getClass().getName();
        String[] splitName = generatedPluginName.split("\\.");
        generatedPluginName = splitName[splitName.length - 1];

        int endNameIndex = generatedPluginName.indexOf("PluginManager");
        if (endNameIndex != -1) {
            generatedPluginName = generatedPluginName.substring(0, endNameIndex);
        }
        generatedPluginName = Character.toLowerCase(generatedPluginName.charAt(0)) + generatedPluginName.substring(1);
        return generatedPluginName;
    }

    /**
     * Helper method for Plug-ins with configurations; get properties path.
     *
     * @param pluginName
     * @return
     */
    protected static String generateDefaultPropertiesPath(String pluginName) {
        return PLUGIN_BASE_PATH + "/" + pluginName + "/" + pluginName + ".properties";
    }

    /**
     * Helper method for Plug-ins with configurations; gets properties contents.
     *
     * @param pluginName
     * @return
     */
    protected static Properties getDefaultPropertiesForPlugin(String pluginName) {
        return ConfigurationUtility.loadProperties(generateDefaultPropertiesPath(pluginName));
    }

}