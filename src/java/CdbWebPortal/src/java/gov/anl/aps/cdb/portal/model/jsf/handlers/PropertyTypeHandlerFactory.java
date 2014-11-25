
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import java.util.HashMap;

/**
 *
 * @author sveseli
 */
public class PropertyTypeHandlerFactory 
{
    private static final HashMap<String, PropertyTypeHandlerInterface> handlerMap = createHandlerMap();
      
    private static HashMap<String, PropertyTypeHandlerInterface> createHandlerMap() 
    {
        HashMap<String, PropertyTypeHandlerInterface> hMap = new HashMap<>();
        hMap.put(NullPropertyTypeHandler.HANDLER_NAME, new NullPropertyTypeHandler());
        hMap.put(ImagePropertyTypeHandler.HANDLER_NAME, new ImagePropertyTypeHandler());
        hMap.put(HttpLinkPropertyTypeHandler.HANDLER_NAME, new HttpLinkPropertyTypeHandler());
        hMap.put(ApsLinkPropertyTypeHandler.HANDLER_NAME, new ApsLinkPropertyTypeHandler());
        return hMap;
    }  
    
    public static PropertyTypeHandlerInterface getHandler(String name) 
    {
        PropertyTypeHandlerInterface propertyTypeHandler = null;
        if (name == null || name.isEmpty()) {
            propertyTypeHandler = handlerMap.get(NullPropertyTypeHandler.HANDLER_NAME);
        }
        if (propertyTypeHandler == null) {
            propertyTypeHandler = handlerMap.get(name);
        }
        if (propertyTypeHandler == null) {
            propertyTypeHandler = handlerMap.get(NullPropertyTypeHandler.HANDLER_NAME);
        }
        return propertyTypeHandler;
    };

    public static PropertyTypeHandlerInterface getHandler(PropertyValue propertyValue) {
        String propertyTypeName = null;
        if (propertyValue != null) {
            propertyTypeName = propertyValue.getPropertyType().getName();
        }
        return getHandler(propertyTypeName);
    }    
}
