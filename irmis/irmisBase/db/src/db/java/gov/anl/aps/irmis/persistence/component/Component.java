/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.component;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;
import org.hibernate.*;
import org.hibernate.classic.Lifecycle;

/**
 * IRMIS business object that represents a component instance in the installed system.
 * This class is mapped via hibernate one-to-one with IRMIS component table.
 */
public class Component extends IRMISDataObject implements Cloneable, Lifecycle {

    private ComponentType componentType;
    private APSComponent apsComponent;
    private List componentPorts = new ArrayList();
    private String componentName;
    private String imageURI;

    // explicitly represent the three hierarchies
    private List parentRelationships = new ArrayList();

    private List controlChildRelationships = new ArrayList();
    private List housingChildRelationships = new ArrayList();
    private List powerChildRelationships = new ArrayList();


    /**
     * Do-nothing constructor.
     */
    public Component() {
        setApsComponent(new APSComponent());
        getApsComponent().setComponent(this);
    }

    /**
     * Constructs component based on given <code>ComponentType</code>. 
     */
    public Component(ComponentType type) {
        setComponentType(type);
        setApsComponent(new APSComponent());
        getApsComponent().setComponent(this);

        // create ports based on templates from type
        // note: this automatically propagates to pins if defined
        Iterator portTemplateIt = type.getComponentPortTemplates().iterator();
        while (portTemplateIt.hasNext()) {
            ComponentPortTemplate cpt = (ComponentPortTemplate)portTemplateIt.next();
            ComponentPort cp = new ComponentPort(cpt);
            addComponentPort(cp);
        }        
    }

    /**
     * Set this component to markForDelete, and propagate value to it's ports and pins.
     */
    public void setMarkForDeleteAndPropagate(boolean value) {

        int numParents = getParentRelationships().size();

        if ((value == true && numParents == 0) || value == false) {
            setMarkForDelete(value);
            getApsComponent().setMarkForDelete(value);
            // propagate this markForDelete to associated ports
            Iterator portIt = getComponentPorts().iterator();
            while (portIt.hasNext()) {
                ComponentPort cp = (ComponentPort)portIt.next();
                cp.setMarkForDeleteAndPropagate(value);
            }
        } 
    }

    /**
     * Determines if given component is contained in the hierarchy beginning
     * with this component. Recursively checks children, going breadth first
     * instead of depth-first. Limit depth of search to limit, -1 meaning
     * no limit.
     */
    public boolean contains(Component c, int hierarchy, int limit) {
        if (limit == 0)
            return false;

        List childList = getChildRelationships(hierarchy);
        if (childList == null || childList.size() == 0) {
            return false;
        } else {
            Iterator childIt = childList.iterator();
            while (childIt.hasNext()) {
                ComponentRelationship childCR = (ComponentRelationship)childIt.next();
                Component childComponent = childCR.getChildComponent();
                if (childComponent.equals(c))
                    return true;
            }
            if (limit > 1) {
                childIt = childList.iterator();
                while (childIt.hasNext()) {
                    ComponentRelationship childCR = (ComponentRelationship)childIt.next();
                    Component childComponent = childCR.getChildComponent();
                    if (childComponent.contains(c, hierarchy, (limit-1)))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     */
    public ComponentType getComponentType() {
        return componentType;
    }
    /**
     * 
     */
    public void setComponentType(ComponentType value) {
        componentType = value;
    }

    /**
     * 
     */
    public APSComponent getApsComponent() {
        return apsComponent;
    }
    /**
     * 
     */
    public void setApsComponent(APSComponent value) {
        apsComponent = value;
    }

    /**
     * 
     */
    public List getComponentPorts() {
        return this.componentPorts;
    }
    /**
     * 
     */
    public void setComponentPorts(List value) {
        this.componentPorts = value;
    }
    /**
     * Clear current set of ComponentPort.
     */
    public void clearComponentPorts() {
        componentPorts.clear();
    }

    /**
     * Add one ComponentPort to list. Establishes bidirectional
     * identity.
     */
    public void addComponentPort(ComponentPort port) {
        port.setComponent(this);
        componentPorts.add(port);
    }

    /**
     * Determines if this component has at least one cable attached to
     * one of its ports. Returns true if it has at least one cable, 
     * false otherwise.
     */
    public boolean hasCable() {
        Iterator portIt = componentPorts.iterator();
        while (portIt.hasNext()) {
            ComponentPort port = (ComponentPort)portIt.next();
            List cables = port.getCables();
            if (cables != null && cables.size() > 0)
                return true;
        }
        return false;
    }

    /**
     * 
     */
    public String getComponentName() {
        return componentName;
    }
    /**
     * 
     */
    public void setComponentName(String value) {
        componentName = value;
    }

    /**
     * 
     */
    public String getImageURI() {
        return imageURI;
    }
    /**
     * 
     */
    public void setImageURI(String value) {
        imageURI = value;
    }

    public List getParentRelationships() {
        return parentRelationships;
    }

    public void setParentRelationships(List value) {
        parentRelationships = value;
    }

    public Component getParentComponent(int relationshipType) {
        ComponentRelationship cr = getParentRelationship(relationshipType);
        if (cr == null)
            return null;
        else
            return cr.getParentComponent();
    }

    public ComponentRelationship getParentRelationship(int relationshipType) {
        // break it out
        Iterator it = parentRelationships.iterator();
        while (it.hasNext()) {
            ComponentRelationship rel = (ComponentRelationship)it.next();
            Long relid = rel.getRelationshipType().getId();
            Long relType = new Long(relationshipType);
            if (relid.equals(relType))
                return rel;
         }
        return null;        
    }

    public void setParentRelationship(ComponentRelationship rel) {
        rel.setChildComponent(this);
        getParentRelationships().add(rel);
    }

    public List getChildComponents(int relationshipType) {
        List childCRList = getChildRelationships(relationshipType);
        List childCList = new ArrayList();
        Iterator crIt = childCRList.iterator();
        while (crIt.hasNext()) {
            ComponentRelationship cr = (ComponentRelationship)crIt.next();
            childCList.add(cr.getChildComponent());
        }
        return childCList;
    }

    public List getChildRelationships(int relationshipType) {
        switch (relationshipType) {
        case ComponentRelationshipType.CONTROL: {
            return controlChildRelationships;
        }
        case ComponentRelationshipType.HOUSING: {
            return housingChildRelationships;
        }
        case ComponentRelationshipType.POWER: {
            return powerChildRelationships;
        }
        default: {
            return null;
        }
        }
    }

    public void setChildRelationships(List rels, int relationshipType) {
        switch (relationshipType) {
        case ComponentRelationshipType.CONTROL: {
            controlChildRelationships = rels;
            break;
        }
        case ComponentRelationshipType.HOUSING: {
            housingChildRelationships = rels;
            break;
        }
        case ComponentRelationshipType.POWER: {
            powerChildRelationships = rels;
        }
        }
    }

    public void addChildRelationship(ComponentRelationship rel) {
        rel.setParentComponent(this);
        switch (rel.getRelationshipType().getId().intValue()) {
        case ComponentRelationshipType.CONTROL: {
            controlChildRelationships.add(rel.getLogicalOrder(),rel);
            break;
        }
        case ComponentRelationshipType.HOUSING: {
            housingChildRelationships.add(rel.getLogicalOrder(),rel);
            break;
        }
        case ComponentRelationshipType.POWER: {
            powerChildRelationships.add(rel.getLogicalOrder(),rel);
            break;
        }
        }

    }
    
    private List getControlChildRelationships() {
        return controlChildRelationships;
    }
    private void setControlChildRelationships(List value) {
        controlChildRelationships = value;
    }

    private List getHousingChildRelationships() {
        return housingChildRelationships;
    }
    private void setHousingChildRelationships(List value) {
        housingChildRelationships = value;
    }

    private List getPowerChildRelationships() {
        return powerChildRelationships;
    }
    private void setPowerChildRelationships(List value) {
        powerChildRelationships = value;
    }


    public String toString() {
        String cName = getComponentName();
        String ctName = getComponentType().getComponentTypeName();

        StringBuffer sb = new StringBuffer();
        if (cName != null && cName.length() > 0 && !ctName.equals(cName))
            sb.append(ctName + " " + cName);
        else
            sb.append(ctName);

        ComponentRelationship rel = getParentRelationship(ComponentRelationshipType.HOUSING);
        sb.append(" ");
        if (rel != null)
            sb.append(rel.getLogicalDescription());
        else
            sb.append("-");
        sb.append("/");
        rel = getParentRelationship(ComponentRelationshipType.CONTROL);
        if (rel != null)
            sb.append(rel.getLogicalDescription());
        else
            sb.append("-");
        sb.append("/");
        rel = getParentRelationship(ComponentRelationshipType.POWER);
        if (rel != null)
            sb.append(rel.getLogicalDescription());
        else
            sb.append("-");

        return sb.toString();
    }

    /**
     * Provides a descriptive string tailored to the hierarchy in which
     * this component is being displayed. A context-sensitive toString.
     * @param hierarchy integer enumerated in ComponentRelationshipType
     *
     */
    public String toString(int hierarchy) {
        // set text to display
        String ctName = getComponentType().getComponentTypeName();
        String cName = getComponentName();

        StringBuffer sb = new StringBuffer();
        if (cName != null && cName.length() > 0 && !ctName.equals(cName))
            sb.append(ctName + " " + cName);
        else
            sb.append(ctName);

        ComponentRelationship parentRel = getParentRelationship(hierarchy);
        if (parentRel != null && parentRel.getLogicalDescription() != null) {
            if (cName == null || !cName.equals(parentRel.getLogicalDescription()))
                sb.append(" "+parentRel.getLogicalDescription());
        }

        return sb.toString();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof Component) ) return false;
        final Component castOther = (Component)other;
        
        ComponentRelationship housingParentRel = 
            getParentRelationship(ComponentRelationshipType.HOUSING);
        ComponentRelationship otherHousingParentRel = 
            castOther.getParentRelationship(ComponentRelationshipType.HOUSING);

        ComponentRelationship controlParentRel = 
            getParentRelationship(ComponentRelationshipType.CONTROL);
        ComponentRelationship otherControlParentRel = 
            castOther.getParentRelationship(ComponentRelationshipType.CONTROL);

        ComponentRelationship powerParentRel = 
            getParentRelationship(ComponentRelationshipType.POWER);
        ComponentRelationship otherPowerParentRel = 
            castOther.getParentRelationship(ComponentRelationshipType.POWER);

        // if we have no relationships, assume we're unique for now
        if (housingParentRel == null &&
            controlParentRel == null &&
            powerParentRel == null)
            return false;

        if (housingParentRel != null && otherHousingParentRel != null &&
            !housingParentRel.equals(otherHousingParentRel))
            return false;
        else if (housingParentRel != otherHousingParentRel)
            return false;
        if (controlParentRel != null && otherControlParentRel != null &&
            !controlParentRel.equals(otherControlParentRel))
            return false;
        else if (controlParentRel != otherControlParentRel)
            return false;
        if (powerParentRel != null && otherPowerParentRel != null &&
            !powerParentRel.equals(otherPowerParentRel))
            return false;
        else if (powerParentRel != otherPowerParentRel)
            return false;

        return true;
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getParentRelationship(ComponentRelationshipType.HOUSING));
        result = HashCodeUtil.hash(result, getParentRelationship(ComponentRelationshipType.CONTROL));
        result = HashCodeUtil.hash(result, getParentRelationship(ComponentRelationshipType.POWER));
        return result;
    }

    /**
     * Performs deep copy of this component, but not the parent and child
     * relationships. Does not preserve object id fields in all cases.
     */
    public Object clone() throws CloneNotSupportedException {

        if (getId() == null)
            throw new CloneNotSupportedException();
        
        Component componentCopy = new Component();
        APSComponent apsComponentCopy = (APSComponent)(getApsComponent().clone());
        apsComponentCopy.setComponent(componentCopy);

        // copy type and apsComponent
        componentCopy.setApsComponent(apsComponentCopy);
        componentCopy.setComponentType(getComponentType());
        componentCopy.setComponentName(getComponentName());

        // copy ports
        Iterator portIt = getComponentPorts().iterator();
        while (portIt.hasNext()) {
            ComponentPort cp = (ComponentPort)portIt.next();
            ComponentPort cpCopy = (ComponentPort)cp.clone();
            componentCopy.addComponentPort(cpCopy);
        }
        
        return componentCopy;
    }

    /** Scans over all child relationships and makes sure logicalOrder
     * is numbered sequentially starting from 0. This should already be
     * the case, but there seem to be some User Interface bugs that give
     * rise to a sequence like 0, 1, 2, 2, 3, 4.
     */
    private void ensureLogicalOrderSequence() 
    {
        // renumber logical order of children
        int i = 0;
        Iterator childIt = controlChildRelationships.iterator();
        while (childIt.hasNext()) {
            ComponentRelationship tcr = (ComponentRelationship)childIt.next();
            tcr.setLogicalOrder(i++);
        }

        i = 0;
        childIt = housingChildRelationships.iterator();
        while (childIt.hasNext()) {
            ComponentRelationship tcr = (ComponentRelationship)childIt.next();
            tcr.setLogicalOrder(i++);
        }

        i = 0;
        childIt = powerChildRelationships.iterator();
        while (childIt.hasNext()) {
            ComponentRelationship tcr = (ComponentRelationship)childIt.next();
            tcr.setLogicalOrder(i++);
        }
    }

    /************************************************************
     * Implementation of Hibernate Lifecycle interface. Yes,
     * this violates layers, but we are still on hibernate
     * 2.x. The newer version 3.x has an event driven lifecycle
     * mechanism, which keeps our business objects strictly
     * business. But what we have here is pretty trivial.
     ************************************************************/
    public boolean onDelete(Session s) throws CallbackException {
        // if we have any parent relationships, veto the delete
        if (getParentRelationships().size() == 0)
            return Lifecycle.NO_VETO;
        else
            return Lifecycle.VETO;
    }
    public void onLoad(Session s, Serializable id) {
        return;
    }
    public boolean onSave(Session s) {
        ensureLogicalOrderSequence();
        return Lifecycle.NO_VETO;
    }
    public boolean onUpdate(Session s) {
        ensureLogicalOrderSequence();
        return Lifecycle.NO_VETO;
    }
    // end Lifecycle implementation
}
