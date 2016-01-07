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
     * Sets the propertyValue and value used by specific beans.
     *
     * @param propertyValue property value of current row. Current property value. 
     * @param value History property value. For infoAction display type handlers, only use this value for the bean. 
     */
    public void setPropertyValue(PropertyValue propertyValue, String value) {
        // Get value for optional parameter
        if (value == null) {
            value = propertyValue.getValue();
        }
        this.value = value;

        this.propertyValue = propertyValue;
    }
    
    /**
     * Executes the appropriate function for the currently loaded property value into the bean.
     */
    public void loadInfoActionForLoadedPropertyValue(){
        //Check if propertyvalue is of type PDMLink 
        if (PropertyTypeHandlerFactory.getHandler(propertyValue) instanceof PdmLinkPropertyTypeHandler) {
            performPdmLinkLoad();
        } else if (PropertyTypeHandlerFactory.getHandler(propertyValue) instanceof TravelerInstancePropertyTypeHandler) {
            performTravelerInstanceLoad();
        }
    }

    /**
     * Sets the pdmLinkDrawingBean if needed, executes the required PdmLinkDrawingBean function to fetch data needed for dialog. 
     */
    private void performPdmLinkLoad() {
        if (pdmLinkDrawingBean == null) {
            pdmLinkDrawingBean = (PdmLinkDrawingBean) findBean("pdmLinkDrawingBean");
        }
        logger.debug("Info action of type PDMLink, drawing #: " + value);
        pdmLinkDrawingBean.resetDrawingInfo();
        pdmLinkDrawingBean.getRelatedDrawings(value, propertyValue.getInfoActionCommand());
    }
    
    /**
     * Sets the travelerBean if needed, executes the required travelerBean function to fetch data needed for dialog. 
     */
    private void performTravelerInstanceLoad(){
        if (travelerBean == null) {
            travelerBean = (TravelerBean) findBean("travelerBean");
        }
        logger.debug("Info action of type Traveler Instance, id: " + value);
        
        travelerBean.setPropertyValue(propertyValue);
        travelerBean.loadCurrentTravelerInstance(propertyValue.getInfoActionCommand());
    }

    /**
     * Executes setPropertyValue function. 
     * 
     * @param propertyValue propertyValue passed from the entity DataTable upon user infoAction request. 
     */
    public void setPropertyValue(PropertyValue propertyValue) {
        setPropertyValue(propertyValue, null);
    }

    /**
     * Executes setPropertyValue function.
     * 
     * @param propertyValueHistory stores current property value as well as selected history value. 
     */
    public void setPropertyValueHistory(PropertyValueHistory propertyValueHistory) {
        setPropertyValue(propertyValueHistory.getPropertyValue(), propertyValueHistory.getValue());
    }

    public String getValue() {
        return value;
    }
    
    /**
     * Finds a named bean for local use within the current bean.
     * 
     * @param beanName Name of the named bean needed for further execution. 
     * @return Named bean that has been requested. 
     */
    @SuppressWarnings("unchecked")
    public static Object findBean(String beanName) {
        FacesContext context = FacesContext.getCurrentInstance();
        return (Object) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
    }
}
