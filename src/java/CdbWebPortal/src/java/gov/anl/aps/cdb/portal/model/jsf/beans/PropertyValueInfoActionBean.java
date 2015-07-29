/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import java.io.Serializable;
import javax.inject.Named;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PdmLinkPropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Named("propertyValueInfoActionBean")
@RequestScoped
public class PropertyValueInfoActionBean implements Serializable {

    private PropertyValue propertyValue = null;
    
    private static final Logger logger = Logger.getLogger(PropertyValueInfoActionBean.class.getName());
    
    @Inject @Named("pdmLinkDrawingBean")
    private PdmLinkDrawingBean pdmLinkDrawingBean; 

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    /**
     * Checks the property handler type and does appropriate action to the specific handler type. 
     * Sets the propertyValue 
     * 
     * @param propertyValue property value of current row 
     */
    public void setPropertyValue(PropertyValue propertyValue) {
        //Check if propertyvalue is of type PDM-Link 
        if (PropertyTypeHandlerFactory.getHandler(propertyValue) instanceof PdmLinkPropertyTypeHandler) {
            logger.debug("Info action of type PDMLink, drawing #: " + propertyValue.getValue());
            
            pdmLinkDrawingBean.findDrawing(propertyValue.getValue());
        }

        this.propertyValue = propertyValue;
    }

}
