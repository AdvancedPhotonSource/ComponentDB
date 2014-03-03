/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.service.component;

// java imports
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.HashSet;
import java.util.regex.*;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentPort;
import gov.anl.aps.irmis.persistence.component.ComponentPortType;
import gov.anl.aps.irmis.persistence.component.ComponentPortTemplate;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentTypeFunction;
import gov.anl.aps.irmis.persistence.component.ComponentTypeInterface;
import gov.anl.aps.irmis.persistence.component.ComponentRelationship;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipTypeDAO;
import gov.anl.aps.irmis.persistence.component.ComponentDAO;
import gov.anl.aps.irmis.persistence.component.ComponentSemaphoreDAO;
import gov.anl.aps.irmis.persistence.component.Cable;
import gov.anl.aps.irmis.persistence.component.CableDAO;
import gov.anl.aps.irmis.persistence.component.Function;
import gov.anl.aps.irmis.persistence.SemaphoreValue;
import gov.anl.aps.irmis.persistence.DAOException;

// IRMIS service layer
import gov.anl.aps.irmis.service.IRMISService;
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Component Service provides a variety of methods to
 * acquire and manipulate the component object graphs.
 * The methods here are themselves purely transactional, 
 * although some take existing object graphs as arguments.
 * In other words, you do not use new to make an instance of this class.
 *
 * An IRMISException is generally thrown when the supplied arguments are
 * not what is expected, or when there is some underlying database access
 * problem. 
 */
public class ComponentService extends IRMISService {

    // DO NOT PUT ANY INSTANCE VARIABLES HERE

    /**
     * Hide constructor, since all methods are static.
     */
    private ComponentService() {
    }

    /**
     * Take a shared binary semaphore as user "userid". This is a shared semaphore for
     * protecting components against concurrent editing. It is a cooperative mechanism,
     * so all editing code must call this method before beginning. The returned 
     * <code>SemaphoreValue</code> object must be examined before proceeding. If
     * SemaphoreValue.getSemaphoreValue() returns 1, you may proceed with editing code.
     * If SemaphoreValue.getSemaphoreValue() returns 0, you must refuse editing and
     * try again later. In this case, you can call SemaphoreValue.getUserid() to find 
     * out who has the semaphore.
     *
     * @param userid the unix userid of the person wishing to begin editing
     * @return a <code>SemaphoreValue</code> object, whose getSemaphoreValue() method
     *         must return 1 before editing may proceed.
     * @throws IRMISException
     */
    public static SemaphoreValue takeSemaphore(String userid) throws IRMISException {
        ComponentSemaphoreDAO csDAO = null;
        SemaphoreValue sv = null;
        try {
            csDAO = new ComponentSemaphoreDAO();
            sv = csDAO.P(userid);
            return sv;
        } catch (DAOException de) {
            throw new IRMISException(de);
        }
    }

    /**
     * Give back the shared binary semaphore acquired from calling takeSemaphore().
     */
    public static boolean giveSemaphore(SemaphoreValue sv) throws IRMISException {
        ComponentSemaphoreDAO csDAO = null;
        try {
            csDAO = new ComponentSemaphoreDAO();
            return csDAO.V(sv);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }
    }

    /**
     * Checks to see if the semaphore acquired from takeSemaphore() has become stale.
     * This should be tested whenever SemaphoreValue.getSemaphoreValue() returns 0, 
     * since another user could conceivably take the semaphore and never return it.
     */
    public static boolean isValidSemaphore(SemaphoreValue sv) throws IRMISException {
        ComponentSemaphoreDAO csDAO = null;
        try {
            csDAO = new ComponentSemaphoreDAO();
            return csDAO.isValid(sv);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }
    }


    /**
     * Find individual component by it's primary key from database.
     *
     * @param componentId a Long value representing the primary database key id
     * @return a <code>Component</code>, or null if not found.
     * @throws IRMISException
     */
    public static Component findComponentById(Long componentId) throws IRMISException {
        ComponentDAO cDAO = null;
        Component c = null;
        if (componentId == null)
            throw new IRMISException("arguments insufficient");
        try {
            cDAO = new ComponentDAO();
            c = cDAO.findComponentById(componentId);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return c;        
    }

    /**
     * Find list of components of the given type.
     *
     * @param ct a fully instantiated component type object
     * @return list of <code>Component</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findComponentsByType(ComponentType ct) throws IRMISException {
        ComponentDAO cDAO = null;
        List cList = null;
        try {
            cDAO = new ComponentDAO();
            cList = cDAO.findComponentsByType(ct);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return cList;        
    }

    /**
     * Find components whose type matches ct. Restrict further to only those
     * components which exist in the given hierarchy.
     *
     * @param ct component type
     * @param hierarchy integer hierarchy number from ComponentRelationshipType
     * @return list of <code>Component</code> objects.
     * @throws IRMISException
     */
    public static List findComponentsByType(ComponentType ct, int hierarchy) throws IRMISException {
        List components = findComponentsByType(ct);
        if (components != null) {
            Iterator cit = components.iterator();
            ArrayList filteredResults = new ArrayList();
            while (cit.hasNext()) {
                Component c = (Component)cit.next();
                ComponentRelationship cr = c.getParentRelationship(hierarchy);
                // make sure we have parent relationship in hierarchy
                if (cr != null)
                    filteredResults.add(c);
            }
            if (filteredResults.size() == 0)
                return null;
            else
                return filteredResults;
            
        } else {
            return null;
        }
    }

    /**
     * Find all components of the given commponent type ct which reside below
     * the given set of parents in hierarchy.
     */
    public static List findComponentsByType(List parents, ComponentType ct, int hierarchy)
        throws IRMISException {

        List matchList = new ArrayList();
        Iterator parentIt = parents.iterator();
        while (parentIt.hasNext()) {
            Component c = (Component)parentIt.next();
            if (c.getComponentType().equals(ct)) {
                matchList.add(c);
            } 
            List childList = c.getChildComponents(hierarchy);
            if (childList != null && childList.size() > 0) {
                List tempList = findComponentsByType(childList, ct, hierarchy);
                matchList.addAll(tempList);
                
            } 
        }
        return matchList;
    }

    /**
     * Find all components which have searchText in either their component instance
     * name, component type name, or one of their parent relationships (in that order).
     */
    public static List findComponentsByText(String searchText, int hierarchy) 
        throws IRMISException {

        ComponentDAO cDAO = null;
        List cList = null;
        try {
            // add wildcard to both ends of string
            cDAO = new ComponentDAO();
            cList = cDAO.findComponentsByName(searchText, hierarchy);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return cList;        
    }

    /**
     * Find all components which have searchText in either their component instance
     * name, component type name, or one of their parent relationships (in that order).
     * The components to search are given in the first argument, instead of doing
     * a search to the database.
     */
    public static List findComponentsByText(List components, String searchText, int hierarchy) 
        throws IRMISException {
        
        Pattern filterRegEx = Pattern.compile("^.*"+searchText+".*$");
        Iterator compIt = components.iterator();
        List matchedList = new ArrayList();
        while (compIt.hasNext()) {
            Component c = (Component)compIt.next();
            Matcher matcherCompName = filterRegEx.matcher(c.getComponentName());
            boolean matchCompName;
            if (c.getComponentName() == null)
                matchCompName = false;
            else
                matchCompName = matcherCompName.matches();
            Matcher matcherCompType = filterRegEx.matcher(c.getComponentType().getComponentTypeName());
            boolean matchCompType = matcherCompType.matches();
            ComponentRelationship cr = c.getParentRelationship(hierarchy);
            Matcher matcherLogDesc = filterRegEx.matcher(cr.getLogicalDescription());
            boolean matchLogDesc;
            if (cr.getLogicalDescription() == null)
                matchLogDesc = false;
            else
                matchLogDesc = matcherLogDesc.matches();

            if (matchCompName || matchCompType || matchLogDesc)
                matchedList.add(c);
        }
        return matchedList;        
    }

    /**
     * Retrieve the path of components from the given component c to the root
     * of the hierarchy given by relationshipType. Path is returned as a list
     * of components from c to the root.
     *
     * @param c component to trace to root of hierarchy
     * @param relationshipType the chosen hierarchy as in ComponentRelationshipType.HOUSING
     *
     * @return a list of <code>Component</code> from given c to root component
     */
    public static List getComponentPathToRoot(Component c, int relationshipType) {
        return getComponentPathToRoot(c, relationshipType, null);
    }

    /**
     * Retrieve the path of components from the given component c to the root
     * of the hierarchy given by relationshipType. Path is returned as a list
     * of components from c to the root. Null is returned if there does not
     * exist at least one component in the path that is in the given 
     * requiredParentComponents list.
     *
     * @param c component to trace to root of hierarchy
     * @param relationshipType the chosen hierarchy as in ComponentRelationshipType.HOUSING
     * @param requiredParentComponents list of components of which at least one must appear
     *                                 in the returned path to root.
     *
     * @return a list of <code>Component</code> from given c to root component
     */
    public static List getComponentPathToRoot(Component c, int relationshipType,
                                              List requiredParentComponents) {
        //System.out.println("getComponentPathToRoot() for c:"+c.getId()+" rel:"+relationshipType);
        ArrayList tempPath = new ArrayList();
        tempPath.add(c);
        ComponentRelationship parentRel = c.getParentRelationship(relationshipType);
        Component parentC = null;
        boolean found = (requiredParentComponents==null || requiredParentComponents.contains(c));
        while (parentRel != null) {
            parentC = parentRel.getParentComponent();
            //System.out.println("loop... parentC:"+parentC.getId());
            if (requiredParentComponents==null || requiredParentComponents.contains(parentC))
                found = true;
            tempPath.add(parentC);
            parentRel = parentC.getParentRelationship(relationshipType);
        }
        if (found)
            return tempPath;
        else
            return null;
    }

    /**
     * Returns the <code>ComponentRelationshipType</code> object for the corresponding
     * hierarchy. Note that the hierarchy integer enum is defined statically in
     * ComponentRelationshipType.
     *
     * @param hierarchy for example ComponentRelationshipType.HOUSING
     * @return a <code>ComponentRelationshipType</code> or null if not found
     * @throws IRMISException
     */
    public static ComponentRelationshipType findComponentRelationshipType(int hierarchy) 
        throws IRMISException {

        ComponentRelationshipTypeDAO crtDAO = null;
        ComponentRelationshipType crt = null;
        try {
            crtDAO = new ComponentRelationshipTypeDAO();
            crt = crtDAO.findRelationshipType(hierarchy);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return crt;
    }

    /**
     * Add childComponent to parent using a relationship of type given by hierarchy.
     * Child is added to end of list of any existing children.
     *
     * @param parentComponent
     * @param childComponent
     * @param hierarchy from ComponentRelationshipType statics
     *
     * @throws IRMISException
     */
    public static void addChildComponent(Component parentComponent,
                                         Component childComponent,
                                         int hierarchy)
        throws IRMISException {

        if (parentComponent == null || childComponent == null)
            throw new IRMISException("arguments insufficient");
        
        List childList = 
            parentComponent.getChildRelationships(hierarchy);
        ComponentRelationshipType crt = findComponentRelationshipType(hierarchy);
        
        // make new relationship
        ComponentRelationship newCR = new ComponentRelationship();
        
        if (childList == null || childList.size() == 0) {
            // create new first child with logical order 0
            newCR.setRelationshipType(crt);
            newCR.setParentComponent(parentComponent);
            newCR.setChildComponent(childComponent);
            newCR.setLogicalOrder(0);
            newCR.setLogicalDescription("-");
            
        } else {
            // fill out info based on last of existing children
            ComponentRelationship lastCR = (ComponentRelationship)childList.get(childList.size()-1);
            int lastCRlo = lastCR.getLogicalOrder();
            newCR.setRelationshipType(lastCR.getRelationshipType());
            newCR.setParentComponent(parentComponent);
            newCR.setChildComponent(childComponent);
            newCR.setLogicalOrder(++lastCRlo);
            newCR.setLogicalDescription("-");
        }
            
        childComponent.setParentRelationship(newCR);
        parentComponent.addChildRelationship(newCR);
    }

    /**
     * Delete component from parent in given hierarchy. The logical order
     * of remaining children is renumbered.
     *
     * @param component component to remove from hierarchy
     * @param deleteComponent if true, delete the component after removing from hierarchy
     * @param hierarchy from ComponentRelationshipType statics
     *
     * @return index of child in list before deletion
     * @throws IRMISException
     */
    public static int deleteChildComponent(Component component,
                                           boolean deleteComponent,
                                           int hierarchy)
        throws IRMISException {
        
        if (component == null)
            throw new IRMISException("arguments insufficient");

        ComponentRelationship cr = component.getParentRelationship(hierarchy);
        Component pc = null;
        List children = null;
        int childIndex = -1;
        if (cr != null) {
            // remove as c's parent rel
            List pRels = component.getParentRelationships();
            pRels.remove(cr);
            // note: for some damn reason, component.getParentRelationships().remove(cr) fails
            pc = cr.getParentComponent();
            children = pc.getChildRelationships(hierarchy);
            childIndex = children.indexOf(cr);
            // remove as c's parent's child rel (phew!)
            if (!children.remove(cr))
                System.out.println("deleteChildComponent() - unable to remove cr from child list:"+cr);

            // mark for delete
            cr.setMarkForDelete(true);
            if (deleteComponent)
                component.setMarkForDeleteAndPropagate(true);
            
            // renumber logical order of all children
            int i = 0;
            Iterator childIt = children.iterator();
            while (childIt.hasNext()) {
                ComponentRelationship tcr = (ComponentRelationship)childIt.next();
                tcr.setLogicalOrder(i++);
            }
        }
        return childIndex;
    }

    /**
     * Insert parentComponent as new parent of given currentComponent, effectively 
     * demoting currentComponent.
     *
     * @param currentComponent component above which to insert parent
     * @param parentComponent new parent to be added
     * @param hierarchy - from ComponentRelationshipType statics
     *
     * @throws IRMISException
     */
    public static void insertParentComponent(Component currentComponent,
                                             Component parentComponent,
                                             int hierarchy)
        throws IRMISException {

        if (parentComponent == null || currentComponent == null)
            throw new IRMISException("arguments insufficient");

        ComponentRelationship currentComponentParentRel = 
            currentComponent.getParentRelationship(hierarchy);
        int subtreeChildIndex = currentComponentParentRel.getLogicalOrder();
        
        // make new relationship
        ComponentRelationship newParentCR = new ComponentRelationship();
        
        if (currentComponentParentRel != null) {
            // detach subtree at parent of ctc
            Component currentComponentParent = currentComponentParentRel.getParentComponent();
            currentComponentParent.getChildRelationships(hierarchy).remove(currentComponentParentRel);
            // replace with new relationship
            newParentCR.setRelationshipType(currentComponentParentRel.getRelationshipType());
            newParentCR.setParentComponent(currentComponentParent);
            newParentCR.setChildComponent(parentComponent);
            newParentCR.setLogicalOrder(subtreeChildIndex);
            newParentCR.setLogicalDescription("-");
            currentComponentParent.addChildRelationship(newParentCR);
            parentComponent.setParentRelationship(newParentCR);

            // adjust old relationship
            currentComponentParentRel.setVerifiedPerson(null);
            currentComponentParentRel.setLogicalOrder(0);
            currentComponentParentRel.setParentComponent(parentComponent);
            parentComponent.addChildRelationship(currentComponentParentRel);
        }        

    }

    /**
     * Make newParentComponent the new parent of given currentComponent in 
     * the given hierarchy.
     *
     * @param currentComponent component to re-parent
     * @param newParentComponent new parent of currentComponent
     * @param logicalOrder location for new child of newParentComponent, -1 to append
     * @param hierarchy - from ComponentRelationshipType statics
     *
     * @throws IRMISException
     */
    public static void changeParentComponent(Component currentComponent,
                                             Component newParentComponent,
                                             int logicalOrder,
                                             int hierarchy)
        throws IRMISException {

        if (newParentComponent == null || currentComponent == null)
            throw new IRMISException("arguments insufficient");

        // remove from current hierarchy
        ComponentRelationship cr = currentComponent.getParentRelationship(hierarchy);
        Component pc = null;
        List children = null;
        int childIndex = -1;

        pc = cr.getParentComponent();
        children = pc.getChildRelationships(hierarchy);
        childIndex = children.indexOf(cr);
        // remove as c's parent's child rel (phew!)
        if (!children.remove(cr))
            System.out.println("deleteChildComponent() - unable to remove cr from child list:"+cr);
        
        // renumber logical order of all children
        int i = 0;
        Iterator childIt = children.iterator();
        while (childIt.hasNext()) {
            ComponentRelationship tcr = (ComponentRelationship)childIt.next();
            tcr.setLogicalOrder(i++);
        }
        
        // add under new parent
        List newChildList = 
            newParentComponent.getChildRelationships(hierarchy);
        cr.setParentComponent(newParentComponent);
        cr.setVerifiedPerson(null);
        
        if (newChildList == null || newChildList.size() == 0) {
            // create new first child with logical order 0
            cr.setLogicalOrder(0);
            
        } else if (logicalOrder == -1) {
            ComponentRelationship lastCR = (ComponentRelationship)newChildList.get(newChildList.size()-1);
            int lo = lastCR.getLogicalOrder();
            lo++;
            cr.setLogicalOrder(lo);

        } else {
            cr.setLogicalOrder(logicalOrder);
        }
        newParentComponent.addChildRelationship(cr);

        // renumber logical order of all children to be safe
        i = 0;
        childIt = newChildList.iterator();
        while (childIt.hasNext()) {
            ComponentRelationship tcr = (ComponentRelationship)childIt.next();
            tcr.setLogicalOrder(i++);
        }
            
    }

    /**
     * Relocate child component (according to logicalOrder index) within its 
     * set of sibling components. 
     *
     * @throws IRMISException
     */
    public static void relocateChildComponent(Component child,
                                              int relationshipType,
                                              int logicalOrder) 
        throws IRMISException {
        ComponentRelationship parentRel = child.getParentRelationship(relationshipType);
        if (parentRel != null) {
            Component parent = parentRel.getParentComponent();
            List childList = parent.getChildRelationships(relationshipType);
            if (childList == null ||
                logicalOrder < 0 || logicalOrder >= childList.size()) {
                throw new IRMISException("logicalOrder is out of range");
            }
            childList.remove(parentRel);
            childList.add(logicalOrder, parentRel);
            // renumber logical order of all children
            int i = 0;
            Iterator childIt = childList.iterator();
            while (childIt.hasNext()) {
                ComponentRelationship cr = (ComponentRelationship)childIt.next();
                cr.setLogicalOrder(i++);
            }
        }
    }


    /**
     * Returns a list of all components which are children of the given component,
     * regardless of which hierarchy they belong to.
     * Recursively traverses all child relationships depth-first to find these.
     *
     * @param c component to begin traversal with
     *
     * @return list of <code>Component</code>
     */
    public static List findAllChildren(Component c) {
        return findAllChildren(c, -1);
    }

    /**
     * Returns a list of all components which are children of the given component.
     * Recursively traverses all child relationships depth-first to find these.
     *
     * @param c component to begin traversal with
     * @param relationshipType the chosen hierarchy as in ComponentRelationshipType.HOUSING
     *
     * @return list of <code>Component</code>
     */
    public static List findAllChildren(Component c, int relationshipType) {
        
        List childCompList = new ArrayList();
        List childRelList = new ArrayList();
        if (relationshipType == -1) {
            childRelList.addAll(c.getChildRelationships(ComponentRelationshipType.HOUSING));
            childRelList.addAll(c.getChildRelationships(ComponentRelationshipType.CONTROL));
            childRelList.addAll(c.getChildRelationships(ComponentRelationshipType.POWER));
        } else {
            childRelList.addAll(c.getChildRelationships(relationshipType));
        }

        if (childRelList != null && childRelList.size() > 0) {
            Iterator childRelIt = childRelList.iterator();
            while (childRelIt.hasNext()) {
                ComponentRelationship cr = (ComponentRelationship)childRelIt.next();
                List tempList = findAllChildren(cr.getChildComponent(), relationshipType);
                childCompList.add(cr.getChildComponent());
                childCompList.addAll(tempList);
            }
        }
        return childCompList;
    }

    /**
     * Recursively remove all child relationships of given type. Uses depth-first
     * traversal of relationship hierarchy.
     */
    public static void removeAllChildRelationships(Component c, int relationshipType) {
        List childList = c.getChildRelationships(relationshipType);
        if (childList != null && childList.size() > 0) {
            Iterator childIt = childList.iterator();
            while (childIt.hasNext()) {
                ComponentRelationship cr = (ComponentRelationship)childIt.next();
                Component childComponent = cr.getChildComponent();
                // mark for delete
                cr.setMarkForDelete(true);
                childComponent.setMarkForDelete(true);

                // recurse
                removeAllChildRelationships(childComponent, relationshipType);
                // remove parent ref
                childComponent.getParentRelationships().remove(cr);
            }
            // clear child list
            childList.clear();
        }
    }

    /**
     * Test given component to see if it has any parent relationships. If it has
     * not parents, then it is considered a "floating" component with no identity.
     * 
     * @return true if component has no parents, false otherwise
     */
    public static boolean isFloating(Component component) {
        if (component.getParentRelationship(ComponentRelationshipType.CONTROL) == null &&
            component.getParentRelationship(ComponentRelationshipType.HOUSING) == null &&
            component.getParentRelationship(ComponentRelationshipType.POWER) == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the given newType can physically substitute
     * for the current type of given component. For now, any matching
     * form factor can be a potential replacement.
     * 
     *
     * @param component current <code>Component</code>
     * @param newType the potential new <code>ComponentType</code>
     * @return true if newType can be substituted for current type of component
     */
    public static boolean isValidReplacementComponentType(Component component, 
                                                          ComponentType newType) {

        ComponentType type = component.getComponentType();

        // test for matching form factor
        if (!type.getFormFactor().equals(newType.getFormFactor()))
            return false;

        return true;
        /* make sure new type has at least the same set of ports as existing instance
        List ports = component.getComponentPorts();
        List portTemplates = new ArrayList(newType.getComponentPortTemplates());

        // test for matching port template for every port instance
        Iterator portIt = ports.iterator();
        while (portIt.hasNext()) {
            ComponentPort port = (ComponentPort)portIt.next();
            ComponentPortType portType = port.getComponentPortType();
            Iterator portTemplateIt = portTemplates.iterator();
            boolean found = false;
            ComponentPortTemplate portTemplate = null;
            while (portTemplateIt.hasNext()) {
                portTemplate = (ComponentPortTemplate)portTemplateIt.next();
                if (portType.equals(portTemplate.getComponentPortType())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            } else {
                portTemplates.remove(portTemplate);
            }
        }
        return true;
        */
    }

    /**
     * Check to see if component c is valid for automatic placement in the controls
     * or power hierarchy (given by hierarchy argument). This is typically called 
     * when c is being added to the housing hierarchy, and we want to see if we can
     * just automatically place it in the controls and power hierarchies with the
     * same parent.
     *
     * @param component the component which will be placed
     * @param hierarchy the hierarchy in which we want to automatically place c
     */
    public static boolean isValidForAutomaticPlacement(Component component, int hierarchy) {
        ComponentType ct = component.getComponentType();
        Set ctiList = ct.getComponentTypeInterfaces();
        List requiredHousingList = new ArrayList();
        List requiredControlList = new ArrayList();
        List requiredPowerList = new ArrayList();
        // break out interface list into three hierarchies (required interfaces only)
        Iterator ctiListIt = ctiList.iterator();
        while (ctiListIt.hasNext()) {
            ComponentTypeInterface cti = (ComponentTypeInterface)ctiListIt.next();
            if (cti.getRequired()) {
                if (cti.getRelationshipType().getId().equals(new Long(ComponentRelationshipType.HOUSING)))
                    requiredHousingList.add(cti);
                else if (cti.getRelationshipType().getId().equals(new Long(ComponentRelationshipType.CONTROL)))
                    requiredControlList.add(cti);
                else if (cti.getRelationshipType().getId().equals(new Long(ComponentRelationshipType.POWER)))
                    requiredPowerList.add(cti);
            }
        }
        // now check for a match with desired hierarchy
        if (requiredHousingList.size() > 0) {
            Iterator housingIt = requiredHousingList.iterator();
            while (housingIt.hasNext()) {
                ComponentTypeInterface hcti = (ComponentTypeInterface)housingIt.next();
                Iterator destIt = null;
                if (hierarchy == ComponentRelationshipType.CONTROL)
                    destIt = requiredControlList.iterator();
                else if (hierarchy == ComponentRelationshipType.POWER)
                    destIt = requiredPowerList.iterator();
                while (destIt.hasNext()) {
                    ComponentTypeInterface destcti = (ComponentTypeInterface)destIt.next();
                    if (destcti.getInterfaceType().getInterfaceType().
                        equals(hcti.getInterfaceType().getInterfaceType())) {
                        // ok, unless we are a CPU
                        Set ctFunctions = ct.getComponentTypeFunctions();
                        if (ctFunctions != null) {
                            Iterator fIt = ctFunctions.iterator();
                            while (fIt.hasNext()) {
                                ComponentTypeFunction ctf = (ComponentTypeFunction)fIt.next();
                                Function f = ctf.getFunction();
                                if (f.getFunctionName().equals("CPU"))
                                    return false;
                            }
                        }
                        return true;
                    }
                }
            }

        }
        return false;
    }

    /**
     * Copy an existing (persistent) component to a new component that is not
     * yet persistent. We do not copy any existing parent and/or child 
     * relationships. 
     *
     * @param component component to be copied into a non-persistent version
     * @return a non-persistent (transient) component
     * @throws IRMISException
     */
    public static Component copyComponent(Component component) throws IRMISException {

        Component copy = null;
        try {
            copy = (Component)component.clone();
        } catch (CloneNotSupportedException cnse) {
            throw new IRMISException(cnse);
        }
        return copy;
    }

    /**
     * Find all cables attached to a given port. This can be more than one cable if the
     * port permits multidrop connections, such as a binding post. The cable has a port A
     * and port B end, and the connection may be using either.
     *
     * @param port the component port which may have a cable attached
     *
     * @return list of attached <code>Cable</code> objects, null if none
     * @throws IRMISException
     */
    public static List findCablesByPort(ComponentPort port) throws IRMISException {
        CableDAO cDAO = null;
        List cables = null;
        if (port == null)
            return null;
        try {
            cDAO = new CableDAO();
            cables = cDAO.findCablesByPort(port);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }   
        return cables;
    }

    /**
     * Find all cables that have labelSearchText in their label. 
     *
     * @param labelSearchText arbitrary text
     *
     * @return list of <code>Cable</code> objects, null if none
     * @throws IRMISException
     */
    public static List findCablesByLabel(String labelSearchText) throws IRMISException {
        CableDAO cDAO = null;
        List cables = null;
        try {
            cDAO = new CableDAO();
            cables = cDAO.findCablesByLabel(labelSearchText);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }   
        return cables;
    }

    /**
     * Persist the given component to the database. If it is a completely new object
     * in a transient state (the id field is not initialized), a new row will be inserted
     * in the database, otherwise an update will occur. Any other associations will also
     * be inserted or updated. An exception will be thrown if the minimum of data is not
     * provided. 
     *
     * @param component new (transient) or existing (persistent) <code>Component</code> object
     * @throws IRMISException
     */
    public static void saveComponent(Component component) throws IRMISException {
        ComponentDAO cDAO = null;
        try {
            cDAO = new ComponentDAO();
            cDAO.save(component);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }

    /**
     * Persist the given cable to the database. If it is a completely new object
     * in a transient state (the id field is not initialized), a new row will be inserted
     * in the database, otherwise an update will occur. An exception will be thrown if 
     * the minimum of data is not provided. 
     *
     * @param cable new (transient) or existing (persistent) <code>Cable</code> object
     * @throws IRMISException
     */
    public static void saveCable(Cable cable) throws IRMISException {
        CableDAO cDAO = null;
        try {
            cDAO = new CableDAO();
            cDAO.save(cable);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }

    /**
     * Delete the given cable from the database. 
     *
     * @param cable existing (persistent) <code>Cable</code> object
     * @throws IRMISException
     */
    public static void deleteCable(Cable cable) throws IRMISException {
        CableDAO cDAO = null;
        try {
            cDAO = new CableDAO();
            cDAO.delete(cable);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }

}
