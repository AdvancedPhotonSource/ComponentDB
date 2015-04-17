/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import java.util.HashMap;

/**
 * Property type handler factory.
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
        hMap.put(PdmLinkPropertyTypeHandler.HANDLER_NAME, new PdmLinkPropertyTypeHandler());
        hMap.put(DocumentPropertyTypeHandler.HANDLER_NAME, new DocumentPropertyTypeHandler());
        hMap.put(CurrencyPropertyTypeHandler.HANDLER_NAME, new CurrencyPropertyTypeHandler());
        hMap.put(DatePropertyTypeHandler.HANDLER_NAME, new DatePropertyTypeHandler());
        hMap.put(BooleanPropertyTypeHandler.HANDLER_NAME, new BooleanPropertyTypeHandler());
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
