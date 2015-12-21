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
import gov.anl.aps.cdb.portal.model.jsf.handlers.TravelerInstancePropertyTypeHandler;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Named("propertyValueInfoActionBean")
@SessionScoped
public class PropertyValueInfoActionBean implements Serializable {

    private PropertyValue propertyValue = null;
    private String value;

    private static final Logger logger = Logger.getLogger(PropertyValueInfoActionBean.class.getName());

    private PdmLinkDrawingBean pdmLinkDrawingBean;
    private TravelerBean travelerBean; 

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    /**
     * Checks the property handler type and does appropriate action to the
     * specific handler type. Sets the propertyValue
     *
     * @param propertyValue property value of current row
     */
    public void setPropertyValue(PropertyValue propertyValue, String value) {
        // Get value for optional parameter
        if (value == null) {
            value = propertyValue.getValue();
        }
        this.value = value;

        this.propertyValue = propertyValue;
    }
    
    public void loadInfoActionForLoadedPropertyValue(){
        //Check if propertyvalue is of type PDMLink 
        if (PropertyTypeHandlerFactory.getHandler(propertyValue) instanceof PdmLinkPropertyTypeHandler) {
            performPdmLinkLoad();
        } else if (PropertyTypeHandlerFactory.getHandler(propertyValue) instanceof TravelerInstancePropertyTypeHandler) {
            performTravelerInstanceLoad();
        }
    }

    private void performPdmLinkLoad() {
        if (pdmLinkDrawingBean == null) {
            pdmLinkDrawingBean = (PdmLinkDrawingBean) findBean("pdmLinkDrawingBean");
        }
        logger.debug("Info action of type PDMLink, drawing #: " + value);
        pdmLinkDrawingBean.resetDrawingInfo();
        pdmLinkDrawingBean.getRelatedDrawings(value, propertyValue.getInfoActionCommand());
    }
    
    private void performTravelerInstanceLoad(){
        if (travelerBean == null) {
            travelerBean = (TravelerBean) findBean("travelerBean");
        }
        logger.debug("Info action of type Traveler Instance, id: " + value);
        
        travelerBean.setPropertyValue(propertyValue);
        travelerBean.loadCurrentTravelerInstance(propertyValue.getInfoActionCommand());
    }

    public void setPropertyValue(PropertyValue propertyValue) {
        setPropertyValue(propertyValue, null);
    }

    public void setPropertyValueHistory(PropertyValueHistory propertyValueHistory) {
        setPropertyValue(propertyValueHistory.getPropertyValue(), propertyValueHistory.getValue());
    }

    public String getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    public static Object findBean(String beanName) {
        FacesContext context = FacesContext.getCurrentInstance();
        return (Object) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
    }
}
