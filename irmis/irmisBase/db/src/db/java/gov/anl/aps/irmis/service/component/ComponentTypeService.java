/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.service.component;

// java imports
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.HashSet;
import java.util.ArrayList;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentTypeDAO;
import gov.anl.aps.irmis.persistence.component.BaseComponentType;
import gov.anl.aps.irmis.persistence.component.BaseComponentTypeDAO;
import gov.anl.aps.irmis.persistence.component.ComponentTypeInterface;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipTypeDAO;
import gov.anl.aps.irmis.persistence.component.ComponentPortType;
import gov.anl.aps.irmis.persistence.component.ComponentPortTypeDAO;
import gov.anl.aps.irmis.persistence.component.ComponentTypeInterfaceType;
import gov.anl.aps.irmis.persistence.component.ComponentTypeInterfaceTypeDAO;
import gov.anl.aps.irmis.persistence.component.PortPinType;
import gov.anl.aps.irmis.persistence.component.PortPinTypeDAO;
import gov.anl.aps.irmis.persistence.component.Manufacturer;
import gov.anl.aps.irmis.persistence.component.ManufacturerDAO;
import gov.anl.aps.irmis.persistence.component.FormFactor;
import gov.anl.aps.irmis.persistence.component.FormFactorDAO;
import gov.anl.aps.irmis.persistence.component.Function;
import gov.anl.aps.irmis.persistence.component.FunctionDAO;
import gov.anl.aps.irmis.persistence.component.BeamlineInterest;
import gov.anl.aps.irmis.persistence.DAOException;

// IRMIS service layer
import gov.anl.aps.irmis.service.IRMISService;
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Component Type Service provides a variety of methods to acquire
 * and manipulate the component type object graphs.
 * The methods here are themselves purely transactional, 
 * although some take existing object graphs as arguments.
 * In other words, you do not use new to make an instance of this class.
 *
 * An IRMISException is generally thrown when the supplied arguments are
 * not what is expected, or when there is some underlying database access
 * problem. 
 */
public class ComponentTypeService extends IRMISService {

    // DO NOT PUT ANY INSTANCE VARIABLES HERE

    /**
     * Hide constructor, since all methods are static.
     */
    private ComponentTypeService() {
    }


    public static ComponentType findComponentTypeByName(String typeName) throws IRMISException {
        
        ComponentTypeDAO ctDAO = null;
        ComponentType ct = null;
        try {
            ctDAO = new ComponentTypeDAO();
            ct = ctDAO.findComponentType(typeName);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return ct;
    }

    /**
     * Get the current list of all known component type objects. Does additional
     * work of determining if each component type is in the base component type
     * table, initializing the isBaseType field to true or false. A base
     * component type (a <code>ComponentType</code> object with isBaseType true)
     * is considered locked and mostly uneditable. This allows for a defined set
     * of guaranteed component types that someone can't modify from under you.
     *
     * @return list of <code>ComponentType</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findComponentTypeList() throws IRMISException {
        ComponentTypeDAO ctDAO = null;
        List ctList = null;
        try {
            ctDAO = new ComponentTypeDAO();
            // get all base component types
            List bctList = ComponentTypeService.findBaseComponentTypeList();
            // build a hash set so we can do quick comparo
            Iterator bctIt = bctList.iterator();
            HashSet bctSet = new HashSet(bctList.size());
            while (bctIt.hasNext()) {
                BaseComponentType bct = (BaseComponentType)bctIt.next();
                bctSet.add(bct.getComponentTypeName());
            }
            ctList = ctDAO.findAllComponentTypes();
            // check each component type to see if it's a base type
            Iterator ctIt = ctList.iterator();
            while (ctIt.hasNext()) {
                ComponentType ct = (ComponentType)ctIt.next();
                if (bctSet.contains(ct.getComponentTypeName()))
                    ct.setIsBaseType(true);
                else
                    ct.setIsBaseType(false);
            }
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return ctList;
    }

    /**
     * Find list of all known base component type objects. A base component type
     * object contains a single string which is the component type name. A base
     * component type (a <code>ComponentType</code> object with isBaseType true)
     * is considered locked and mostly uneditable. This allows for a defined set
     * of guaranteed component types that someone can't modify from under you.
     *
     * @return list of <code>BaseComponentType</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findBaseComponentTypeList() throws IRMISException {
        BaseComponentTypeDAO bctDAO = null;
        List bctList = null;
        try {
            bctDAO = new BaseComponentTypeDAO();
            bctList = bctDAO.findAllComponentTypes();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return bctList;
    }

    /**
     * Validates new component type name by checking to see if
     * it exists in the component_type or base_component_type
     * tables already. A new component type must have a name that
     * is completely unique.
     *
     * @param typeName name user has chosen for a new component type
     * @return true if valid (unique), false otherwise
     * @throws IRMISException
     */
    public static boolean isComponentTypeNameValid(String typeName) 
        throws IRMISException {
        ComponentTypeDAO ctDAO = null;
        BaseComponentTypeDAO bctDAO = null;
        ComponentType ct = null;
        BaseComponentType bct = null;
        boolean isValid = false;
        try {
            ctDAO = new ComponentTypeDAO();
            ct = ctDAO.findComponentType(typeName);
            if (ct == null) {
                bctDAO = new BaseComponentTypeDAO();
                bct = bctDAO.findComponentType(typeName);
                if (bct == null)
                    isValid = true;
                else
                    isValid = false;

            } else {
                isValid = false;
            }

        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return isValid;
    }

    /**
     * Takes (complete) list of component types, and returns a list of component
     * types that have an interface that matches at least one of the interfaces
     * of the given peerComponentType. The componentTypes argument is typically
     * acquired via a call to <code>findComponentTypeList()</code>.
     *
     * @param componentTypes a list of <code>ComponentType</code> objects to filter
     * @param peerComponentType the type that will serve to constrain list of component types
     * @param peerRequired if true, match against peerComponent interface required
     * @param peerPresented if true, match against peerComponent interface presented
     * @param relationshipType one of the types of relationships (housing, control, power), 0 for all
     * @return list of filtered <code>ComponentType</code> objects
     */
    public static List filterComponentTypeListByPeerInterface(List componentTypes, 
                                                              ComponentType peerComponentType,
                                                              boolean peerRequired,
                                                              boolean peerPresented,
                                                              int relationshipType) {

        if (peerComponentType == null)
            return componentTypes;

        List newList = new ArrayList();
        Iterator it = componentTypes.iterator();
        while (it.hasNext()) {
            ComponentType candidateType = (ComponentType)it.next();
            boolean matchingInterface = false;
            Set peerTypeInterfaces = peerComponentType.getComponentTypeInterfaces();
            Set candidateTypeInterfaces = candidateType.getComponentTypeInterfaces();
            Iterator candidateTypeIt = candidateTypeInterfaces.iterator();
            while (candidateTypeIt.hasNext()) {
                ComponentTypeInterface ccti = (ComponentTypeInterface)candidateTypeIt.next();
                if ((relationshipType == 0 || 
                     ccti.getRelationshipType().getId().intValue()==relationshipType) &&
                    (!peerRequired || ccti.getPresented()) &&
                    (!peerPresented || ccti.getRequired())) {
                    Iterator peerTypeIt = peerTypeInterfaces.iterator();
                    while (peerTypeIt.hasNext()) {
                        ComponentTypeInterface pcti = (ComponentTypeInterface)peerTypeIt.next();
                        if ((!peerRequired || pcti.getRequired()) &&
                            (!peerPresented || pcti.getPresented()) &&
                            (relationshipType == 0 || 
                             pcti.getRelationshipType()
                            .equals(ccti.getRelationshipType())) &&
                            pcti.getInterfaceType()
                            .equals(ccti.getInterfaceType())) {
                            matchingInterface = true;
                            break;
                        }
                    }
                    if (matchingInterface) {
                        newList.add(candidateType);
                        break;
                    }
                }
            }

        }
        return newList;
    }


    /**
     * Validates new component port type name by checking to see if
     * it exists in the database already or not.
     *
     * @param typeName proposed new port type name
     * @return true if valid (unique), false otherwise
     * @throws IRMISException
     */
    public static boolean isComponentPortTypeNameValid(String typeName) 
        throws IRMISException {
        ComponentPortTypeDAO cptDAO = null;
        ComponentPortType cpt = null;
        boolean isValid = false;
        try {
            cptDAO = new ComponentPortTypeDAO();
            cpt = cptDAO.findComponentPortType(typeName);
            if (cpt == null)
                isValid = true;
            else
                isValid = false;

        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return isValid;
    }

    /**
     * Find list of all known manufacturers.
     *
     * @return list of <code>Manufacturer</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findManufacturerList() throws IRMISException {
        ManufacturerDAO mDAO = null;
        List mList = null;
        try {
            mDAO = new ManufacturerDAO();
            mList = mDAO.findAllManufacturers();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return mList;
    }

    /**
     * Find list of all known form factors. A form factor is something
     * like 1U or 2U, and describes the physical form of a component type.
     *
     * @return list of <code>FormFactor</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findFormFactorList() throws IRMISException {
        FormFactorDAO ffDAO = null;
        List ffList = null;
        try {
            ffDAO = new FormFactorDAO();
            ffList = ffDAO.findAllFormFactors();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return ffList;
    }

    /**
     * Find list of all known BeamlineInterest. This is a list of the
     * types of interest the beamline community can have in a given
     * component type.
     *
     * @return list of <code>BeamlineInterest</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findBeamlineInterestList() throws IRMISException {
        ComponentTypeDAO ctDAO = null;
        List biList = null;
        try {
            ctDAO = new ComponentTypeDAO();
            biList = ctDAO.findAllBeamlineInterest();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return biList;
    }

    /**
     * Find list of all known functions. A function describes the potential
     * use of a component type, such as DAC, ADC, etc.
     *
     * @return list of <code>Function</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findFunctionList() throws IRMISException {
        FunctionDAO fDAO = null;
        List fList = null;
        try {
            fDAO = new FunctionDAO();
            fList = fDAO.findAllFunctions();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return fList;
    }


    /**
     * Find list of <code>ComponentTypeInterfaceType</code> for the given
     * component relationship type. These interface types are grouped by the type
     * of relationship between component, such as control, power, housing.
     *
     * @param crt a <code>ComponentRelationshipType</code> retrieved via findRelationshipTypeList() method
     * @return list of <code>ComponentTypeInterfaceType</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findInterfaceTypeList(ComponentRelationshipType crt) throws IRMISException {
        ComponentTypeInterfaceTypeDAO ctitDAO = null;
        List itList = null;
        try {
            ctitDAO = new ComponentTypeInterfaceTypeDAO();
            itList = ctitDAO.findAllInterfaceTypes(crt);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return itList;
    }

    /**
     * Find list of all known component port types. A port type describes the
     * port type and pin configuration for a component's port.
     *
     * @return list of <code>ComponentPortType</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findPortTypeList() throws IRMISException {
        ComponentPortTypeDAO cptDAO = null;
        List ptList = null;
        try {
            cptDAO = new ComponentPortTypeDAO();
            ptList = cptDAO.findAllComponentPortTypes();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return ptList;
    }

    /**
     * Find list of all known pin types. A pin type describes the
     * type of an individual pin (IN, OUT, BIDIR, COM, GND, BUS, PWR).
     *
     * @return list of <code>PortPinType</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findPinTypeList() throws IRMISException {
        PortPinTypeDAO pptDAO = null;
        List ptList = null;
        try {
            pptDAO = new PortPinTypeDAO();
            ptList = pptDAO.findAllPortPinTypes();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return ptList;
    }

    /**
     * Find the list of all port type groups that can be found in the current database.
     * Returned is simply a list of strings.
     *
     * @return list of strings
     * @throws IRMISException
     */
    public static List findPortTypeGroupList() throws IRMISException {
        ComponentPortTypeDAO cptDAO = null;
        List groupList = null;
        try {
            cptDAO = new ComponentPortTypeDAO();
            groupList = cptDAO.findAllComponentPortTypeGroups();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return groupList;
    }


    /**
     * Find list of all known component relationship types. A relationship type 
     * describes the association between two components. The current irmisBase
     * distribution uses three relationship types: control, power, and housing.
     *
     * @return list of <code>ComponentRelationshipType</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findRelationshipTypeList() throws IRMISException {
        ComponentRelationshipTypeDAO crtDAO = null;
        List rtList = null;
        try {
            crtDAO = new ComponentRelationshipTypeDAO();
            rtList = crtDAO.findAllComponentRelationshipTypes();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return rtList;
    }


    /**
     * Persist the given compType to the database. If it is a completely new object
     * in a transient state (the id field is not initialized), a new row will be inserted
     * in the database, otherwise an update will occur. Any other associations will also
     * be inserted or updated. An exception will be thrown if the minimum of data is not
     * provided.
     *
     * @param compType new (transient) or existing (persistent) <code>ComponentType</code> object
     * @throws IRMISException
     */
    public static void saveComponentType(ComponentType compType) throws IRMISException {
        ComponentTypeDAO ctDAO = null;
        try {
            ctDAO = new ComponentTypeDAO();
            ctDAO.save(compType);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }

    /**
     * Persist the given compPortType to the database. Typically this should only
     * be done for a truly new (transient) <code>ComponentPortType</code>. 
     *
     * @param compPortType new (transient, meaning id field not initialized) <code>ComponentPortType</code>
     * @throws IRMISException
     */
    public static void saveComponentPortType(ComponentPortType compPortType) 
        throws IRMISException {
        ComponentPortTypeDAO cptDAO = null;
        try {
            cptDAO = new ComponentPortTypeDAO();
            cptDAO.save(compPortType);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }

    /**
     * Persist the given mfg to the database. Typically this should only
     * be done for a truly new (transient) <code>Manufacturer</code>. 
     *
     * @param mfg new (transient, meaning id field not initialized) <code>Manufacturer</code>
     * @throws IRMISException
     */
    public static void saveManufacturer(Manufacturer mfg) throws IRMISException {
        ManufacturerDAO mfgDAO = null;
        try {
            mfgDAO = new ManufacturerDAO();
            mfgDAO.save(mfg);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }

    /**
     * Persist the given ff to the database. Typically this should only
     * be done for a truly new (transient) <code>FormFactor</code>. 
     *
     * @param ff new (transient, meaning id field not initialized) <code>FormFactor</code>
     * @throws IRMISException
     */
    public static void saveFormFactor(FormFactor ff) throws IRMISException {
        FormFactorDAO ffDAO = null;
        try {
            ffDAO = new FormFactorDAO();
            ffDAO.save(ff);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }

    /**
     * Persist the given f to the database. Typically this should only
     * be done for a truly new (transient) <code>Function</code>. 
     *
     * @param f new (transient, meaning id field not initialized) <code>Function</code>
     * @throws IRMISException
     */
    public static void saveFunction(Function f) throws IRMISException {
        FunctionDAO fDAO = null;
        try {
            fDAO = new FunctionDAO();
            fDAO.save(f);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }

    /**
     * Persist the given ctit to the database. Typically this should only
     * be done for a truly new (transient) <code>ComponentTypeInterfaceType</code>. 
     *
     * @param ctit new (transient, meaning id field not initialized) <code>ComponentTypeInterfaceType</code>
     * @throws IRMISException
     */
    public static void saveInterfaceType(ComponentTypeInterfaceType ctit) throws IRMISException {
        ComponentTypeInterfaceTypeDAO ctitDAO = null;
        try {
            ctitDAO = new ComponentTypeInterfaceTypeDAO();
            ctitDAO.save(ctit);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }

}
