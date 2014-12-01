package gov.anl.aps.cdb.portal.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

public class ConfigurationUtility {

    public static final String PropertiesPath = "cdb.portal.properties";
    public static final String PropertiesDelimiter = ",";
   
    private static final Logger logger = Logger.getLogger(ConfigurationUtility.class.getName());
    private static final Properties portalProperties = loadProperties(PropertiesPath);

    public Properties getPortalProperties() {
        return portalProperties;
    }

    public static String getPortalProperty(String propertyName) {
        return portalProperties.getProperty(propertyName, "");
    }
    
    public static Integer getPortalPropertyAsInteger(String propertyName) {
        String result = getPortalProperty(propertyName);
        try {
            return new Integer(Integer.parseInt(result));
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }
    
    public static String getPortalProperty(String propertyName, String defaultValue) {
        return portalProperties.getProperty(propertyName, defaultValue);
    }

    public static List<String> getPortalPropertyList(String propertyName) {
        return getPortalPropertyList(propertyName, "");
    }
    
    public static List<String> getPortalPropertyList(String propertyName, String defaultValue) {
        String[] propertyArray = portalProperties.getProperty(propertyName, defaultValue).split(PropertiesDelimiter);
        logger.debug("Looking for property " + propertyName);
        ArrayList propertyList = new ArrayList();
        for (String property : propertyArray) {
            String p = property.trim();
            if (p.length() > 0) {
                propertyList.add(property.trim());
            }
        }
        logger.debug("Resulting property list: " + propertyList);
        return propertyList;
    }    
    
    /**
     * Load properties.
     *
     * @param path
     * @return loaded properties
     */
    public static Properties loadProperties(String path) {
        Properties properties = new Properties();
        if (path != null) {
            try {
                logger.debug("Loading properties from " + path);
                InputStream inputStream = ConfigurationUtility.class.getClassLoader().getResourceAsStream(path);
                properties.load(inputStream);
            } catch (IOException ex) {
                logger.warn("Could not load properties from file " + path + ": " + ex);
            }
        } else {
            logger.warn("Properties path not specified.");
        }
        return properties;
    }

    /**
     * Get system property.
     *
     * @param propertyName property name
     * @return property value
     */
    public static String getSystemProperty(String propertyName) {
        Properties p = System.getProperties();
        return p.getProperty(propertyName);
    }

    /**
     * Get system property.
     *
     * @param propertyName property name
     * @param defaultValue default property value
     * @return property value
     */
    public static String getSystemProperty(String propertyName, String defaultValue) {
        Properties p = System.getProperties();
        return p.getProperty(propertyName, defaultValue);
    }

    /**
     * Get environment variable.
     *
     * @param name Environment variable name
     * @return environment variable value, or null if it is not defined
     */
    public static String getEnvVar(String name) {
        return System.getenv(name);
    }

}
