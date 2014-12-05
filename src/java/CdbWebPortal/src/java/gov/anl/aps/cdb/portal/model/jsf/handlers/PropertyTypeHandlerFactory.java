package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import java.util.HashMap;

/**
 *
 * @author sveseli
 */
public class PropertyTypeHandlerFactory {

    private static final HashMap<String, PropertyTypeHandlerInterface> handlerMap = createHandlerMap();

    private static HashMap<String, PropertyTypeHandlerInterface> createHandlerMap() {
        HashMap<String, PropertyTypeHandlerInterface> hMap = new HashMap<>();
        hMap.put(NullPropertyTypeHandler.HANDLER_NAME, new NullPropertyTypeHandler());
        hMap.put(ImagePropertyTypeHandler.HANDLER_NAME, new ImagePropertyTypeHandler());
        hMap.put(HttpLinkPropertyTypeHandler.HANDLER_NAME, new HttpLinkPropertyTypeHandler());
        hMap.put(IcmsLinkPropertyTypeHandler.HANDLER_NAME, new IcmsLinkPropertyTypeHandler());
        hMap.put(EdpLinkPropertyTypeHandler.HANDLER_NAME, new EdpLinkPropertyTypeHandler());
        hMap.put(AmosLinkPropertyTypeHandler.HANDLER_NAME, new AmosLinkPropertyTypeHandler());
        hMap.put(ParisLinkPropertyTypeHandler.HANDLER_NAME, new ParisLinkPropertyTypeHandler());
        hMap.put(DocumentPropertyTypeHandler.HANDLER_NAME, new DocumentPropertyTypeHandler());
        return hMap;
    }

    public static PropertyTypeHandlerInterface getHandler(String name) {
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
    }

    ;

    public static PropertyTypeHandlerInterface getHandler(PropertyValue propertyValue) {
        String propertyTypeHandlerName = null;
        if (propertyValue != null) {
            PropertyTypeHandler propertyTypeHandler = propertyValue.getPropertyType().getPropertyTypeHandler();
            if (propertyTypeHandler != null) {
                propertyTypeHandlerName = propertyTypeHandler.getName();
            }
        }
        return getHandler(propertyTypeHandlerName);
    }
}
