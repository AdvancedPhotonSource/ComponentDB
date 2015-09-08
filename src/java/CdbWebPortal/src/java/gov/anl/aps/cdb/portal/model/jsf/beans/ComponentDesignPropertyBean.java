/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.jsf.beans;


import gov.anl.aps.cdb.portal.model.db.beans.DesignDbFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeDbFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeHandlerDbFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.ComponentDesignPropertyTypeHandler;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */

@Named("componentDesignPropertyBean")
@SessionScoped
public class ComponentDesignPropertyBean implements Serializable{

    @EJB 
    private PropertyTypeHandlerDbFacade propertyTypeHandlerDbFacade; 
    
    @EJB 
    private PropertyValueDbFacade propertyValueDbFacade; 
    
    @EJB
    private DesignDbFacade designDbFacade; 

    public void setPropertyDesign(PropertyValue propertyValue) {
        if(propertyValue.getComponentDesignPropertySelected() == null){
            String propertyValueStr = propertyValue.getValue();
            if(!propertyValueStr.equalsIgnoreCase("")){
                Design design = designDbFacade.findById(Integer.parseInt(propertyValue.getValue())); 
                propertyValue.setComponentDesignPropertySelected(design);
            }
        }
    }
    
    public void changeComponentDesignPropertyValue(PropertyValue propertyValue){
        
        if(propertyValue == null){
            return; 
        }
        Design selectedDesign = propertyValue.getComponentDesignPropertySelected(); 
        
        int propertyValueId = -1; 
        if(propertyValue.getId() != null){
            propertyValueId = propertyValue.getId(); 
        }
        
        List<PropertyValue> propertyValueList = propertyValueDbFacade.findByPropertyType(propertyValue.getPropertyType()); 
        
        boolean validDesign = true;
        String newValueStr = selectedDesign.getId().toString(); 
        String newDesignName = selectedDesign.getName(); 

        
        for (PropertyValue iterationValue: propertyValueList){
            int iterationValueId = iterationValue.getId();
            if(iterationValueId != propertyValueId){
                String iterationPropertyValueStr = iterationValue.getValue();
                if (iterationPropertyValueStr.equalsIgnoreCase(newValueStr)) {
                    
                    SessionUtility.addWarningMessage("Warning", "The design \""+newDesignName+"\" is already assigned to a component");
                    validDesign = false; 
                }
            }
        }
        
        if(validDesign){
            propertyValue.setValue(newValueStr); 
        } else {
            propertyValue.setComponentDesignPropertySelected(null);
            setPropertyDesign(propertyValue);
        }
       
    }
    
    public Design getDesignById(int id){
        return designDbFacade.findById(id); 
    }
    

    
}
