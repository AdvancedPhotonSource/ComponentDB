/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * CDB object factory class.
 */
public class CdbObjectFactory {

    private static final Logger logger = Logger.getLogger(CdbObjectFactory.class.getName());
    private static final Gson gson = new GsonBuilder().create();

    /**
     * Create object from JSON string.
     *
     * @param <T> template class
     * @param jsonString JSON string
     * @param objectClass object class
     * @return generated object
     */
    public static <T extends Object> T createObject(String jsonString, Class<T> objectClass) {
        logger.debug("Converting JSON string to object " + objectClass + ": " + jsonString);
        T object = gson.fromJson(jsonString, objectClass);
        return object;
    }

    /**
     * Create CDB object from JSON string.
     *
     * @param <T> template class
     * @param jsonString JSON string
     * @param cdbClass CDB object class
     * @return generated CDB object
     * @throws CdbException in case of any errors
     */
    public static <T extends CdbObject> T createCdbObject(String jsonString, Class<T> cdbClass) throws CdbException {
        logger.debug("Converting JSON string to CDB object " + cdbClass + ": " + jsonString);
        T cdbObject = gson.fromJson(jsonString, cdbClass);
        cdbObject.decode();
        return cdbObject;
    }

    /**
     * Create list of CDB objects from JSON string.
     *
     * @param <T> template class
     * @param jsonString CDB string
     * @return generated list of CDB objects
     */
    public static <T extends CdbObject> List<T> createCdbObjectList(String jsonString) {
        // This method does not appear to work as template, so we have
        // to write specific methods for each object type.
        logger.debug("Converting JSON string to cdb object list: " + jsonString);
        Type cdbType = new TypeToken<LinkedList<T>>() {
        }.getType();
        List<T> cdbObjectList = gson.fromJson(jsonString, cdbType);
        return cdbObjectList;
    }

    /**
     * Create list of string objects from JSON string.
     *
     * @param jsonString JSON string
     * @return generated list of string objects
     */
    public static List<String> createStringObjectList(String jsonString) {
        logger.debug("Converting JSON string to string object list: " + jsonString);
        Type cdbType = new TypeToken<LinkedList<String>>() {
        }.getType();
        List<String> cdbObjectList = gson.fromJson(jsonString, cdbType);
        return cdbObjectList;
    }

}
