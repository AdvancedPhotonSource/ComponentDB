/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenttype.cfw.wizard;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.login.RoleName;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentTypePerson;
import gov.anl.aps.irmis.persistence.component.ComponentTypeFunction;
import gov.anl.aps.irmis.persistence.component.ComponentTypeStatus;
import gov.anl.aps.irmis.persistence.component.ComponentTypeDocument;
import gov.anl.aps.irmis.persistence.component.ComponentTypeInterface;
import gov.anl.aps.irmis.persistence.component.ComponentTypeInterfaceType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.component.ComponentPortTemplate;
import gov.anl.aps.irmis.persistence.component.ComponentPortType;
import gov.anl.aps.irmis.persistence.component.Manufacturer;
import gov.anl.aps.irmis.persistence.component.FormFactor;
import gov.anl.aps.irmis.persistence.component.Function;
import gov.anl.aps.irmis.persistence.component.BeamlineInterest;

// IRMIS service layer
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.shared.PersonService;
import gov.anl.aps.irmis.service.IRMISException;

public class NewTypeWizardModel {

    // are we supporting new type or editing existing type
    private boolean editMode = false;

    // are we jumping to step 5 and adding a port
    private boolean addPortMode = false;

    // are we jumping to step 7 and editing pin usage
    private boolean editPinsMode = false;

    // final step sets this to false
    private boolean cancelled = true;

    // after this wizard returns, application should reload and restart
    private boolean restartRequired = false;

    // new/edited component type 
    private ComponentType componentType = null;

    // existing component types
    private List compTypeList = null;

    private String componentTypeNameError = null;
    private String componentPortTypeNameError = null;
    private String componentTypeSaveError = null;
    private String portEditStatus;
    private String originalComponentTypeName = null;

    // temporary selection, eventually added to ports set
    private ComponentPortTemplate selectedPort;

    // enum lists (ie. available types)
    private List manufacturers = null;
    private List formFactors = null;
    private List functions = null;
    private List persons = null;
    private List beamlineInterestList = null;
    private List controlInterfaceTypes = null;
    private List housingInterfaceTypes = null;
    private List powerInterfaceTypes = null;
    //private List interfaceTypes = null;
    private List portTypes = null;
    private List pinTypes = null;
    private List portTypeGroups = null;
    private List relationships = null;
    private RoleName cognitiveRoleName = null;

    /**
     * Constructor used when creating new component type in wizard.
     */
    public NewTypeWizardModel(List compTypeList) {
        editMode = false;
        portEditStatus = "Add";
        componentType = new ComponentType();
        originalComponentTypeName = "";
        componentType.setComponentTypeName(originalComponentTypeName);
        componentType.setDescription("");
        ComponentTypeStatus cts = new ComponentTypeStatus();
        cts.setComponentType(componentType);
        componentType.setComponentTypeStatus(cts);
        this.compTypeList = compTypeList;
        initializePickLists();
    }

    /**
     * Constructor used when editing existing component type in wizard.
     */
    public NewTypeWizardModel(ComponentType ct, boolean addPortMode) {
        this.addPortMode = addPortMode;
        editMode = true;
        portEditStatus = "Done";
        componentType = ct;
        originalComponentTypeName = ct.getComponentTypeName();
        initializePickLists();
    }

    

    private void initializePickLists() {
        // load up pick-list data from database
        try {
            manufacturers = ComponentTypeService.findManufacturerList();
            if (manufacturers == null)
                manufacturers = new ArrayList();
            
            formFactors = ComponentTypeService.findFormFactorList();
            if (formFactors == null)
                formFactors = new ArrayList();
            
            functions = ComponentTypeService.findFunctionList();
            if (functions == null)
                functions = new ArrayList();
            
            persons = PersonService.findPersonList();
            if (persons == null)
                persons = new ArrayList();

            beamlineInterestList = ComponentTypeService.findBeamlineInterestList();
            if (beamlineInterestList == null)
                beamlineInterestList = new ArrayList();

            relationships = ComponentTypeService.findRelationshipTypeList();
            if (relationships == null)
                relationships = new ArrayList();

            Iterator relIt = relationships.iterator();
            while (relIt.hasNext()) {
                ComponentRelationshipType crt = (ComponentRelationshipType)relIt.next();
                if (crt.getRelationshipType().equals("control")) {
                    controlInterfaceTypes = ComponentTypeService.findInterfaceTypeList(crt);
                    if (controlInterfaceTypes == null)
                        controlInterfaceTypes = new ArrayList();

                } else if (crt.getRelationshipType().equals("housing")) {
                    housingInterfaceTypes = ComponentTypeService.findInterfaceTypeList(crt);
                    if (housingInterfaceTypes == null)
                        housingInterfaceTypes = new ArrayList();

                } else if (crt.getRelationshipType().equals("power")) {
                    powerInterfaceTypes = ComponentTypeService.findInterfaceTypeList(crt);
                    if (powerInterfaceTypes == null)
                        powerInterfaceTypes = new ArrayList();
                }
            }

            portTypes = ComponentTypeService.findPortTypeList();
            if (portTypes == null)
                portTypes = new ArrayList();

            pinTypes = ComponentTypeService.findPinTypeList();
            if (pinTypes == null)
                pinTypes = new ArrayList();

            portTypeGroups = ComponentTypeService.findPortTypeGroupList();
            if (portTypeGroups == null)
                portTypeGroups = new ArrayList();

            cognitiveRoleName = PersonService.findRoleName("cognitive person");

        } catch (IRMISException ie) {
            ie.printStackTrace();
        }
    }

    public ComponentType getComponentType() {
        return componentType;
    }
    public void setComponentType(ComponentType value) {
        componentType = value;
    }

    public String getOriginalComponentTypeName() {
        return originalComponentTypeName;
    }

    public String getComponentTypeNameError() {
        return componentTypeNameError;
    }

    public void setComponentTypeNameError(String value) {
        componentTypeNameError = value;
    }

    public String getComponentPortTypeNameError() {
        return componentPortTypeNameError;
    }

    public void setComponentPortTypeNameError(String value) {
        componentPortTypeNameError = value;
    }

    public String getComponentTypeSaveError() {
        return componentTypeSaveError;
    }

    public void setComponentTypeSaveError(String value) {
        componentTypeSaveError = value;
    }

    public String getPortEditStatus() {
        return portEditStatus;
    }
    public void setPortEditStatus(String value) {
        portEditStatus = value;
    }

    public ComponentPortTemplate getSelectedPort() {
        return selectedPort;
    }
    public void setSelectedPort(ComponentPortTemplate value) {
        selectedPort = value;
    }

    public List getComponentTypes() {
        return compTypeList;
    }

    /**
     * Read-only list of <code>Manufacturer</code> objects.
     */
    public List getManufacturers() {
        return manufacturers;
    }
    /**
     * Find manufacturer on read-only list. Return null if
     * not found.
     */
    public Manufacturer findManufacturer(String mfgName) {
        Iterator mfgIt = manufacturers.iterator();
        Manufacturer mfg = null;
        while (mfgIt.hasNext()) {
            Manufacturer temp = (Manufacturer)mfgIt.next();
            if (temp.getManufacturerName().equals(mfgName)) {
                mfg = temp;
                break;
            }
        }
        return mfg;
    }

    /**
     * Read-only list of <code>FormFactor</code> objects.
     */
    public List getFormFactors() {
        return formFactors;
    }
    /**
     * Find form factor on read-only list. Return null if
     * not found.
     */
    public FormFactor findFormFactor(String ffName) {
        Iterator ffIt = formFactors.iterator();
        FormFactor ff = null;
        while (ffIt.hasNext()) {
            FormFactor temp = (FormFactor)ffIt.next();
            if (temp.getFormFactor().equals(ffName)) {
                ff = temp;
                break;
            }
        }
        return ff;
    }

    /**
     * Read-only list of <code>Function</code> objects.
     */
    public List getFunctions() {
        return functions;
    }
    /**
     * Find function on read-only list. Return null if
     * not found.
     */
    public Function findFunction(String fName) {
        Iterator fIt = functions.iterator();
        Function f = null;
        while (fIt.hasNext()) {
            Function temp = (Function)fIt.next();
            if (temp.getFunctionName().equals(fName)) {
                f = temp;
                break;
            }
        }
        return f;
    }

    /**
     * Read-only list of <code>Person</code> objects.
     */
    public List getPersons() {
        return persons;
    }

    /**
     * Read-only list of <code>BeamlineInterest</code> objects.
     */
    public List getBeamlineInterestList() {
        return beamlineInterestList;
    }

    /**
     * Read-only list of <code>ComponentTypeInterfaceType</code> objects
     * pertaining to control relationships.
     */
    public List getControlInterfaceTypes() {
        return controlInterfaceTypes;
    }
    /**
     * Read-only list of <code>ComponentTypeInterfaceType</code> objects
     * pertaining to housing relationships.
     */
    public List getHousingInterfaceTypes() {
        return housingInterfaceTypes;
    }
    /**
     * Read-only list of <code>ComponentTypeInterfaceType</code> objects
     * pertaining to power relationships.
     */
    public List getPowerInterfaceTypes() {
        return powerInterfaceTypes;
    }
    /**
     * Find interface type on read-only list for given relationship type. 
     * Return null if not found.
     */
    public ComponentTypeInterfaceType findInterfaceType(ComponentRelationshipType crt, 
                                                        String ifName) {
        Iterator ifIt = null;
        if (crt.getRelationshipType().equals("control"))
            ifIt = controlInterfaceTypes.iterator();
        else if (crt.getRelationshipType().equals("power"))
            ifIt = powerInterfaceTypes.iterator();
        else if (crt.getRelationshipType().equals("housing"))
            ifIt = housingInterfaceTypes.iterator();

        ComponentTypeInterfaceType ife = null;
        while (ifIt.hasNext()) {
            ComponentTypeInterfaceType temp = (ComponentTypeInterfaceType)ifIt.next();
            if (temp.getInterfaceType().equals(ifName)) {
                ife = temp;
                break;
            }
        }
        return ife;
    }

    /**
     * Read-only list of <code>ComponentPortType</code> objects.
     */
    public List getPortTypes() {
        return portTypes;
    }

    /**
     * Read-only list of <code>PortPinType</code> objects.
     */
    public List getPinTypes() {
        return pinTypes;
    }

    /**
     * Read-only list of strings representing the current
     * set of known port type groups.
     */
    public List getPortTypeGroups() {
        return portTypeGroups;
    }

    public RoleName getCognitiveRoleName() {
        return cognitiveRoleName;
    }

    /**
     * Read-only list of <code>ComponentRelationshipType</code>
     * objects.
     */
    public List getRelationships() {
        return relationships;
    }
    /**
     * Find relationship type on read-only list for given relationship string.
     * Return null if not found.
     */
    public ComponentRelationshipType findRelationship(String relName) {
        Iterator relIt = relationships.iterator();
        ComponentRelationshipType crt = null;
        while (relIt.hasNext()) {
            ComponentRelationshipType temp = (ComponentRelationshipType)relIt.next();
            if (temp.getRelationshipType().equals(relName)) {
                crt = temp;
                break;
            }
        }
        return crt;
    }

    public boolean getEditMode() {
        return editMode;
    }
    public void setEditMode(boolean value) {
        editMode = value;
    }

    public boolean getAddPortMode() {
        return addPortMode;
    }
    public void setAddPortMode(boolean value) {
        addPortMode = value;
    }

    public boolean getEditPinsMode() {
        return editPinsMode;
    }
    public void setEditPinsMode(boolean value) {
        editPinsMode = value;
    }

    public boolean getCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean value) {
        cancelled = value;
    }

    public boolean getRestartRequired() {
        return restartRequired;
    }
    public void setRestartRequired(boolean restart) {
        restartRequired = restart;
    }

}
