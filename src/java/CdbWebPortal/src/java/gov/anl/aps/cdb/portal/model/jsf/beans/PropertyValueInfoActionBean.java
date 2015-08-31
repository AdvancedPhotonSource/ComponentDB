/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import java.io.Serializable;
import javax.inject.Named;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
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
    private String value; 
    
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
    public void setPropertyValue(PropertyValue propertyValue, String value) {
        // Get value for optional parameter
        if(value == null){
            value = propertyValue.getValue();
        }
        this.value = value; 
        
        //Check if propertyvalue is of type PDMLink 
        if(PropertyTypeHandlerFactory.getHandler(propertyValue) instanceof PdmLinkPropertyTypeHandler) { 
            logger.debug("Info action of type PDMLink, drawing #: " + value);
            pdmLinkDrawingBean.resetDrawingInfo();
            pdmLinkDrawingBean.getRelatedDrawings(value);
        }

        this.propertyValue = propertyValue;
    }
    
    public void setPropertyValue(PropertyValue propertyValue) {
        setPropertyValue(propertyValue, null);
    }
   
    public void setPropertyValueHistory(PropertyValueHistory propertyValueHistory){
        setPropertyValue(propertyValueHistory.getPropertyValue(), propertyValueHistory.getValue());
    }

    public String getValue() {
        return value;
    }
}
