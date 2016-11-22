/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.pdmLink.objects;

import gov.anl.aps.cdb.common.objects.CdbObject;
import gov.anl.aps.cdb.common.objects.ComponentType;
import java.util.LinkedList;

/**
 * Component Type object.
 */
public class PdmLinkComponent extends CdbObject {

    private LinkedList<ComponentType> suggestedComponentTypes;
    private String[] pdmPropertyValues; 
    private String wbsDescription;  
    private String drawingNumber; 
    private String cdbDescription;
    private String modelNumber; 
    
    public PdmLinkComponent() {
    }

    public String[] getPdmPropertyValues() {
        return pdmPropertyValues;
    }

    public LinkedList<ComponentType> getSuggestedComponentTypes() {
        return suggestedComponentTypes;
    }

    public String getWbsDescription() {
        return wbsDescription;
    }

    public String getDrawingNumber() {
        return drawingNumber;
    }
    
    public String getCdbDescription() {
        return cdbDescription;
    }
    
    public void setCdbDescription(String cdbDescription) {
        this.cdbDescription = cdbDescription; 
    }
    
    public void setPdmPropertyValues(String[] pdmPropertyValues) {
        this.pdmPropertyValues = pdmPropertyValues;
    }

    public void setSuggestedComponentTypes(LinkedList<ComponentType> suggestedComponentTypes) {
        this.suggestedComponentTypes = suggestedComponentTypes;
    }

    public void setWbsDescription(String wbsDescription) {
        this.wbsDescription = wbsDescription;
    }

    public void setDrawingNumber(String drawingNumber) {
        this.drawingNumber = drawingNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getModelNumber() {
        return modelNumber;
    }
    
    public String getDisplayPdmLinkPropertiesString(){
        String result = ""; 
        for (String pdmPropertyValue : pdmPropertyValues) {
            result += pdmPropertyValue + "<br/>";
        }
        return result; 
    }
    
}
