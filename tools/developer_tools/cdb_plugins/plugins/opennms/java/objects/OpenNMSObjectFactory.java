/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.plugins.support.opennms.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class OpenNMSObjectFactory {
    
    private static final Logger logger = LogManager.getLogger(OpenNMSObjectFactory.class.getName());
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
        logger.debug("Converting JSON string to object " + objectClass);
        T object = gson.fromJson(jsonString, objectClass);
        return object;
    }
    
}
