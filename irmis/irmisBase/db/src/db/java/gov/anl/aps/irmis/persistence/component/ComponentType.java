/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.component;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import gov.anl.aps.irmis.persistence.login.Person;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// dom4j XML
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * IRMIS business object that represents a component type.
 * This class is mapped via hibernate one-to-one with IRMIS component_type table.
 */
public class ComponentType extends IRMISDataObject implements Cloneable, Comparable {

    private FormFactor formFactor;
    private Manufacturer manufacturer;
    private BeamlineInterest beamlineInterest;
    private Person chcContact;

    private List componentPortTemplates = new ArrayList();
    private Set componentTypeFunctions = new HashSet();
    private Set componentTypeInterfaces = new HashSet();
    private Set componentTypePersons = new HashSet();
    private Set componentTypeDocuments = new HashSet();
    private ComponentTypeStatus componentTypeStatus;

    private String componentTypeName;
    private String description;
    private String vDescription;
    private boolean isBaseType = false; // not mapped

    /**
     * Do-nothing constructor.
     */
    public ComponentType() {
    }

    /**
     * 
     */
    public FormFactor getFormFactor() {
        return this.formFactor;
    }
    /**
     * 
     */
    public void setFormFactor(FormFactor value) {
        this.formFactor = value;
    }

    /**
     * 
     */
    public Manufacturer getManufacturer() {
        return this.manufacturer;
    }
    /**
     * 
     */
    public void setManufacturer(Manufacturer value) {
        this.manufacturer = value;
    }

    /**
     * The type of interest the beamline community has in this component type.
     */
    public BeamlineInterest getBeamlineInterest() {
        return this.beamlineInterest;
    }
    public void setBeamlineInterest(BeamlineInterest value) {
        this.beamlineInterest = value;
    }

    /**
     * The person responsible for this component type in the component hardware catalog.
     */
    public Person getChcContact() {
        return this.chcContact;
    }
    public void setChcContact(Person value) {
        this.chcContact = value;
    }


    /**
     * 
     */
    public List getComponentPortTemplates() {
        return this.componentPortTemplates;
    }
    /**
     * 
     */
    public void setComponentPortTemplates(List value) {
        this.componentPortTemplates = value;
    }
    /**
     * Clear current set of ComponentPortTemplate.
     */
    public void clearComponentPortTemplates() {
        componentPortTemplates.clear();
    }

    /**
     * Add one ComponentPortTemplate to list. Establishes bidirectional
     * identity.
     */
    public void addComponentPortTemplate(ComponentPortTemplate template) {
        template.setComponentType(this);
        componentPortTemplates.add(template);
    }

    /**
     * 
     */
    public Set getComponentTypeFunctions() {
        return this.componentTypeFunctions;
    }
    /**
     * 
     */
    public void setComponentTypeFunctions(Set value) {
        this.componentTypeFunctions = value;
    }

    /**
     * Clear current set of ComponentTypeFunction.
     */
    public void clearComponentTypeFunctions() {
        this.componentTypeFunctions.clear();
    }

    /**
     * Add one ComponentTypeFunction to set. Establishes bidirectional
     * identity.
     */
    public void addComponentTypeFunction(ComponentTypeFunction ctf) {
        ctf.setComponentType(this);
        componentTypeFunctions.add(ctf);
    }

    /**
     * 
     */
    public Set getComponentTypeInterfaces() {
        return this.componentTypeInterfaces;
    }
    /**
     * 
     */
    public void setComponentTypeInterfaces(Set value) {
        this.componentTypeInterfaces = value;
    }
    /**
     * Clear set of all interfaces.
     */
    public void clearComponentTypeInterfaces() {
        componentTypeInterfaces.clear();
    }

    /**
     * Add one ComponentTypeInterface to set. Establishes bidirectional
     * identity.
     */
    public void addComponentTypeInterface(ComponentTypeInterface cti) {
        cti.setComponentType(this);
        componentTypeInterfaces.add(cti);
    }

    /**
     * 
     */
    public Set getComponentTypePersons() {
        return this.componentTypePersons;
    }
    /**
     * 
     */
    public void setComponentTypePersons(Set value) {
        this.componentTypePersons = value;
    }

    /**
     * Clear collection of all documents.
     */
    public void clearComponentTypePersons() {
        this.componentTypePersons.clear();
    }

    /**
     * Add one ComponentTypePerson to set. Establishes bidirectional
     * identity.
     */
    public void addComponentTypePerson(ComponentTypePerson ctcp) {
        ctcp.setComponentType(this);
        componentTypePersons.add(ctcp);
    }

    /**
     * 
     */
    public Set getComponentTypeDocuments() {
        return this.componentTypeDocuments;
    }
    /**
     * 
     */
    public void setComponentTypeDocuments(Set value) {
        this.componentTypeDocuments = value;
    }

    /**
     * Clear collection of all documents.
     */
    public void clearComponentTypeDocuments() {
        this.componentTypeDocuments.clear();
    }

    /**
     * Add one ComponentTypeDocument to set. Establishes bidirectional
     * identity.
     */
    public void addComponentTypeDocument(ComponentTypeDocument ctd) {
        ctd.setComponentType(this);
        componentTypeDocuments.add(ctd);
    }

    /**
     * 
     */
    public ComponentTypeStatus getComponentTypeStatus() {
        return this.componentTypeStatus;
    }
    /**
     * 
     */
    public void setComponentTypeStatus(ComponentTypeStatus value) {
        this.componentTypeStatus = value;
    }

    /**
     * 
     */
    public String getComponentTypeName() {
        return this.componentTypeName;
    }
    /**
     * 
     */
    public void setComponentTypeName(String value) {
        this.componentTypeName = value;
    }

    /**
     * Indicates this component type exists in the base component
     * type table, and should not be modified.
     */
    public boolean getIsBaseType() {
        return this.isBaseType;
    }

    public void setIsBaseType(boolean value) {
        this.isBaseType = value;
    }

    /**
     * 
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * 
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * 
     */
    public String getVerboseDescription() {
        return this.vDescription;
    }
    /**
     * 
     */
    public void setVerboseDescription(String value) {
        this.vDescription = value;
    }

    /**
     * Converts this data object to an XML Element, with all associated information
     * as attributes and sub-elements. This Element is then readily output as an XML
     * document, or added as an element to a larger document.
     *
     * @return dom4j <code>Element</code> containing a variety of sub-elements
     */
    public Element toElement() {
        Element ctElement = DocumentHelper.createElement("component-type");
        ctElement.addAttribute("name", getComponentTypeName());
        ctElement.addAttribute("description", getDescription());
        ctElement.addAttribute("base-type", ((getIsBaseType()==true)?"true":"false"));
        ctElement.add(getFormFactor().toElement());
        ctElement.add(getManufacturer().toElement());

        Element cptElement = DocumentHelper.createElement("component-port-templates");
        Iterator cptIt = getComponentPortTemplates().iterator();
        while (cptIt.hasNext()) {
            ComponentPortTemplate cpt = (ComponentPortTemplate)cptIt.next();
            cptElement.add(cpt.toElement());
        }
        ctElement.add(cptElement);

        Element ctfElement = DocumentHelper.createElement("component-type-functions");
        Iterator ctfIt = getComponentTypeFunctions().iterator();
        while (ctfIt.hasNext()) {
            ComponentTypeFunction ctf = (ComponentTypeFunction)ctfIt.next();
            ctfElement.add(ctf.getFunction().toElement());
        }
        ctElement.add(ctfElement);

        Element ctiElement = DocumentHelper.createElement("component-type-interfaces");
        Iterator ctiIt = getComponentTypeInterfaces().iterator();
        while (ctiIt.hasNext()) {
            ComponentTypeInterface cti = (ComponentTypeInterface)ctiIt.next();
            ctiElement.add(cti.toElement());
        }
        ctElement.add(ctiElement);
        
        return ctElement;
    }


    public String toString() {
        return getComponentTypeName();
    }

    /**
     * Have any collection of these objects be sorted by the component type name.
     * This is to be a case-insensitive sort.
     */
    public int compareTo(Object o) {
        ComponentType other = (ComponentType)o;
        return this.getComponentTypeName().toUpperCase().
            compareTo(other.getComponentTypeName().toUpperCase());
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentType) ) return false;
        final ComponentType castOther = (ComponentType)other;
        return this.getComponentTypeName().equals(castOther.getComponentTypeName());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getComponentTypeName());
        return result;
    }

    /**
     * Performs deep copy of this component type. Does not preserve 
     * object id fields in all cases, since this copy is intended
     * to become a new component type.
     */
    public Object clone() throws CloneNotSupportedException {

        if (getId() == null)
            throw new CloneNotSupportedException();
        
        ComponentType ctCopy = new ComponentType();

        // copy attributes of this class
        ctCopy.setComponentTypeName("");
        ctCopy.setDescription("");
        ctCopy.setIsBaseType(getIsBaseType());

        // copy associated data
        ctCopy.setManufacturer(getManufacturer());
        ctCopy.setFormFactor(getFormFactor());
        ComponentTypeStatus ctsCopy = (ComponentTypeStatus)getComponentTypeStatus().clone();
        ctsCopy.setComponentType(ctCopy);
        ctCopy.setComponentTypeStatus(ctsCopy);

        // copy collections of associated data
        Iterator portIt = getComponentPortTemplates().iterator();
        while (portIt.hasNext()) {
            ComponentPortTemplate cpt = (ComponentPortTemplate)portIt.next();
            ComponentPortTemplate cptCopy = (ComponentPortTemplate)cpt.clone();
            ctCopy.addComponentPortTemplate(cptCopy);
        }

        Iterator ctfIt = getComponentTypeFunctions().iterator();
        while (ctfIt.hasNext()) {
            ComponentTypeFunction ctf = (ComponentTypeFunction)ctfIt.next();
            ComponentTypeFunction ctfCopy = (ComponentTypeFunction)ctf.clone();
            ctCopy.addComponentTypeFunction(ctfCopy);
        }

        Iterator ctiIt = getComponentTypeInterfaces().iterator();
        while (ctiIt.hasNext()) {
            ComponentTypeInterface cti = (ComponentTypeInterface)ctiIt.next();
            ComponentTypeInterface ctiCopy = (ComponentTypeInterface)cti.clone();
            ctCopy.addComponentTypeInterface(ctiCopy);
        }

        Iterator ctpIt = getComponentTypePersons().iterator();
        while (ctpIt.hasNext()) {
            ComponentTypePerson ctp = (ComponentTypePerson)ctpIt.next();
            ComponentTypePerson ctpCopy = (ComponentTypePerson)ctp.clone();
            ctCopy.addComponentTypePerson(ctpCopy);
        }

        Iterator ctdIt = getComponentTypeDocuments().iterator();
        while (ctdIt.hasNext()) {
            ComponentTypeDocument ctd = (ComponentTypeDocument)ctdIt.next();
            ComponentTypeDocument ctdCopy = (ComponentTypeDocument)ctd.clone();
            ctCopy.addComponentTypeDocument(ctdCopy);
        }
        
        return ctCopy;
    }

}
