/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

// general app utilities
import gov.anl.aps.irmis.apps.SwingThreadWork;
import gov.anl.aps.irmis.apps.QueuedExecutorUtil;

// persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;

// service layer
import gov.anl.aps.irmis.service.component.ComponentService;
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.pv.PVService;
import gov.anl.aps.irmis.service.IRMISException;

// application helpers
import gov.anl.aps.irmis.apps.AbstractTreeModel;
import gov.anl.aps.irmis.apps.AppsUtil;

/**
 * Controls navigation through a component hierarchy using stop-find, configure-find,
 * prev-item, next-item buttons. This will permit finding a desired component in a
 * potentially large hierarchy by iterating over a filtered set of candidates. Also
 * provides a best-attempt text search field which will search several different
 * pieces of data to aid in finding a component.
 */
public class ComponentTreeFindController {

    private JFrame appFrame;
    private JTree tree;
    private int hierarchy;
    private ComponentModel model;
    private JButton stopFindButton;
    private JButton configureFindButton;
    private JButton prevFindButton;
    private JButton nextFindButton;
    private JTextField searchField;
    private ConfigureFindModel findConfig;
    private ConfigureFindDialog findDialog;
    private List findComponentList;

    public ComponentTreeFindController(JFrame appFrame,
                                       JTree tree,
                                       int h,
                                       ComponentModel m,
                                       JButton stopFindButton,
                                       JButton configureFindButton,
                                       JButton prevFindButton,
                                       JButton nextFindButton,
                                       JTextField searchField) {
        this.appFrame = appFrame;
        this.tree = tree;
        this.hierarchy = h;
        this.model = m;
        findConfig = new ConfigureFindModel();

        String dialogTitle = "Advanced Search Criteria";
        this.findDialog = new ConfigureFindDialog(appFrame, null, dialogTitle, h, findConfig);

        // make us a listener for changes in ComponentModel
		model.addComponentModelListener( new ComponentModelListener() {
                public void modified(ComponentModelEvent e) {
                    if (e.getType() == ComponentModelEvent.REDRAW) {
                        findDialog.reset();
                        findConfig.setComponentTypes(model.getComponentTypes());
                        findConfig.setFilteredComponentTypes(model.getComponentTypes());
                        findConfig.setRoomComponents(model.getRoomComponents());
                        findConfig.setSystems(model.getSystems());
                    }
                }
            });

        this.stopFindButton = stopFindButton;
        Action stopFindAction = 
            new StopFindAction(); 
        this.stopFindButton.setAction(stopFindAction);
        this.stopFindButton.setEnabled(false); 

        this.configureFindButton = configureFindButton;
        Action configureFindAction = 
            new ConfigureFindAction(); 
        this.configureFindButton.setAction(configureFindAction);

        this.prevFindButton = prevFindButton;
        Action prevFindAction = 
            new PrevFindAction();
        this.prevFindButton.setAction(prevFindAction);
        this.prevFindButton.setEnabled(false);

        this.nextFindButton = nextFindButton;
        Action nextFindAction = 
            new NextFindAction();
        this.nextFindButton.setAction(nextFindAction);
        this.nextFindButton.setEnabled(false);

        this.searchField = searchField;
        Action searchFieldAction = 
            new SearchFieldAction();
        this.searchField.setAction(searchFieldAction);

    }


    public class StopFindAction extends AbstractAction {

        public StopFindAction() { 
            super();
            ImageIcon icon = new ImageIcon(AppsUtil.getImageURL("Stop16.gif"));
            putValue("SmallIcon",icon);
            putValue("ShortDescription","Stop current find operation.");
        }
        
        public void actionPerformed(ActionEvent e) {

            // clear out any find state and request redraw
            QueuedExecutorUtil.execute(new SwingThreadWork(appFrame) {
                    public void doNonUILogic() {
                        model.setIRMISException(null);
                        if (hierarchy == ComponentRelationshipType.HOUSING) {
                            FilteredHousingComponentTreeModel tModel = 
                                (FilteredHousingComponentTreeModel)tree.getModel();                
                            tModel.setRoomConstraint(null);
                        }
                        if (hierarchy == ComponentRelationshipType.CONTROL) {
                            FilteredControlComponentTreeModel tModel = 
                                (FilteredControlComponentTreeModel)tree.getModel();                
                            tModel.setIOCConstraint(null);
                        }
                        ComponentTreeModel gModel = (ComponentTreeModel)tree.getModel();
                        gModel.reset();
                        findDialog.reset();
                        findConfig.setClearSearchField(true);
                    }
                    
                    public void doUIUpdateLogic() {
                        int eventCode = -1;
                        if (hierarchy == ComponentRelationshipType.HOUSING)
                            eventCode = ComponentModelEvent.HOUSING_FIND_CONFIG;
                        else if (hierarchy == ComponentRelationshipType.CONTROL)
                            eventCode = ComponentModelEvent.CONTROL_FIND_CONFIG;
                        else
                            eventCode = ComponentModelEvent.POWER_FIND_CONFIG;

                        // add this selection request to Swing event queue
                        model.notifyComponentModelListeners(new ComponentModelEvent(eventCode, findConfig));
                    }
                });
        }
    }

    public class ConfigureFindAction extends AbstractAction {

        public ConfigureFindAction() { 
            super();
            ImageIcon icon = new ImageIcon(AppsUtil.getImageURL("Zoom16.gif"));
            putValue("SmallIcon",icon);
            putValue("ShortDescription","Advanced search criteria.");
        }
        
        public void actionPerformed(ActionEvent e) {

            findConfig.setCancelled(false);
            findDialog.setVisible(true);
            if (findConfig.getCancelled())
                return;

            // kick off filtering work to separate thread
            QueuedExecutorUtil.execute(new SwingThreadWork(appFrame) {
                    public void doNonUILogic() {
                        model.setIRMISException(null);
                        boolean roomFilterApplied = false;
                        boolean systemFilterApplied = false;
                        try {
                            // if user entered a component id, just use that
                            if (findConfig.getComponentId() != null) {
                                ComponentTreeModel tModel = (ComponentTreeModel)tree.getModel();
                                Component c = ComponentService.findComponentById(findConfig.getComponentId());

                                if (c != null) {  // found it!
                                    List componentList = new ArrayList();
                                    tModel.setComponentList(componentList);
                                    componentList.add(c);
                                    // set up initial find hit
                                    tModel.setComponentListFindIndex(0);
                                    findComponentList = null;

                                } else {
                                    tModel.setComponentList(null);
                                }                              
                                return;
                            }
                            
                            // possibly filter housing tree by room 
                            if (hierarchy == ComponentRelationshipType.HOUSING) {
                                FilteredHousingComponentTreeModel tModel = 
                                    (FilteredHousingComponentTreeModel)tree.getModel();
                                
                                if (findConfig.getSelectedRoomComponents().size() > 0) {
                                    tModel.setRoomConstraint(findConfig.getSelectedRoomComponents());
                                    roomFilterApplied = true;
                                    
                                } else {
                                    tModel.setRoomConstraint(null);
                                }
                            }
                            // possibly filter control tree by system
                            List iocComponentList = null;
                            if (hierarchy == ComponentRelationshipType.CONTROL) {
                                FilteredControlComponentTreeModel tModel = 
                                    (FilteredControlComponentTreeModel)tree.getModel();
                                
                                if (findConfig.getSelectedSystems().size() > 0) {
                                    Iterator systemIt = findConfig.getSelectedSystems().iterator();
                                    List iocs = new ArrayList();
                                    while (systemIt.hasNext()) {
                                        String system = (String)systemIt.next();
                                        List tempList = PVService.findIOCListBySystem(system);
                                        iocs.addAll(tempList);
                                    }
                                    tModel.setIOCConstraint(iocs);
                                    iocComponentList = tModel.getIOCComponentList();
                                    systemFilterApplied = true;
                                    
                                } else {
                                    tModel.setIOCConstraint(null);
                                }
                            }

                            ComponentTreeModel tModel = (ComponentTreeModel)tree.getModel();
                            // set find sequence by component type
                            if (findConfig.getSelectedComponentTypes().size() > 0) {
                                // set up componentList using types
                                List componentList = new ArrayList();
                                tModel.setComponentList(componentList);
                                
                                Iterator typeIt = findConfig.getSelectedComponentTypes().iterator();
                                while (typeIt.hasNext()) {
                                    ComponentType type = (ComponentType)typeIt.next();
                                    List tempList = null;
                                    if (roomFilterApplied) {
                                        tempList = 
                                            ComponentService
                                            .findComponentsByType(findConfig.getSelectedRoomComponents(),
                                                                  type, 
                                                                  ComponentRelationshipType.HOUSING);
                                        if (tempList != null)
                                            componentList.addAll(tempList);

                                    } else if (systemFilterApplied) {
                                        tempList = 
                                            ComponentService
                                            .findComponentsByType(iocComponentList, type,
                                                                  ComponentRelationshipType.CONTROL);
                                        if (tempList != null)
                                            componentList.addAll(tempList);

                                    } else {
                                        tempList = ComponentService.findComponentsByType(type, hierarchy);
                                        if (tempList != null)
                                            componentList.addAll(tempList);
                                    }
                                }
                                // set up initial find hit
                                tModel.setComponentListFindIndex(0);
                                findComponentList = null;
                                
                            } else {
                                tModel.setComponentList(null);
                            }
                        } catch (IRMISException ie) {
                            ie.printStackTrace();
                            model.setIRMISException(ie);
                        }
                        
                    }
                    
                    public void doUIUpdateLogic() {
                        int eventCode = -1;
                        if (hierarchy == ComponentRelationshipType.HOUSING)
                            eventCode = ComponentModelEvent.HOUSING_FIND_CONFIG;
                        else if (hierarchy == ComponentRelationshipType.CONTROL)
                            eventCode = ComponentModelEvent.CONTROL_FIND_CONFIG;
                        else
                            eventCode = ComponentModelEvent.POWER_FIND_CONFIG;

                        // add this selection request to Swing event queue
                        model.notifyComponentModelListeners(new ComponentModelEvent(eventCode, 
                                                                                    findConfig));
                    }
                });
            
            
        }
    }
    
    public class PrevFindAction extends AbstractAction {

        public PrevFindAction() { 
            super();
            ImageIcon icon = new ImageIcon(AppsUtil.getImageURL("Back16.gif"));
            putValue("SmallIcon",icon);
            putValue("ShortDescription","Go to previous component.");
        }
        
        public void actionPerformed(ActionEvent e) {
            ComponentTreeModel tModel = (ComponentTreeModel)tree.getModel();
            List componentList = tModel.getComponentList();
            int findIndex = tModel.getComponentListFindIndex();
            if (componentList.size() > 0) {
                if (findIndex > 0)
                    tModel.setComponentListFindIndex(--findIndex);

                // maintain button enabled state
                if (findIndex <= (componentList.size()-1)) {
                    nextFindButton.setEnabled(true);
                }
                if (findIndex == 0) {
                    prevFindButton.setEnabled(false);
                }

                Component find = (Component)componentList.get(findIndex);
                TreePath findPath = tModel.getTreePath(find);
                tree.setSelectionPath(findPath);
                tree.scrollPathToVisible(findPath);
            }
        }
    }

    public class NextFindAction extends AbstractAction {

        public NextFindAction() { 
            super();
            ImageIcon icon = new ImageIcon(AppsUtil.getImageURL("Forward16.gif"));
            putValue("SmallIcon",icon);
            putValue("ShortDescription","Go to next component.");
        }
        
        public void actionPerformed(ActionEvent e) {
            ComponentTreeModel tModel = (ComponentTreeModel)tree.getModel();
            List componentList = tModel.getComponentList();
            int findIndex = tModel.getComponentListFindIndex();
            if (componentList.size() > 0) {
                if (findIndex < (componentList.size()-1))
                    tModel.setComponentListFindIndex(++findIndex);

                // maintain button enabled state
                if (findIndex >= 0) {
                    prevFindButton.setEnabled(true);
                }
                if (findIndex == (componentList.size()-1)) {
                    nextFindButton.setEnabled(false);
                }

                Component find = (Component)componentList.get(findIndex);
                TreePath findPath = tModel.getTreePath(find);
                tree.setSelectionPath(findPath);
                tree.scrollPathToVisible(findPath);
            }
        }
    }

    public class SearchFieldAction extends AbstractAction {

        public SearchFieldAction() { 
            super();
            putValue("ShortDescription","Find component with text search.");
        }
        
        public void actionPerformed(ActionEvent e) {
            // kick off filtering work to separate thread
            QueuedExecutorUtil.execute(new SwingThreadWork(appFrame) {
                    public void doNonUILogic() {
                        model.setIRMISException(null);

                        try {

                            // initialize find prev/next with components matching text search string
                            ComponentTreeModel tModel = (ComponentTreeModel)tree.getModel();
                            String searchText = searchField.getText();
                            if (searchText != null && searchText.length() > 0) {
                                List componentList = tModel.getComponentList();

                                // see if there is a find next/prev in progress
                                if (componentList == null || componentList.size() == 0) {  // no
                                    List tempList = 
                                        ComponentService.findComponentsByText(searchText, hierarchy);
                                    
                                    if (tempList != null) {
                                        // take into account any filtered out components by eliminating
                                        // those in tempList which do not exist in the tree
                                        List matchedList = null;
                                        if (findConfig.filterApplied()) {
                                            matchedList = new ArrayList();
                                            Iterator tempIt = tempList.iterator();
                                            while (tempIt.hasNext()) {
                                                Component c = (Component)tempIt.next();
                                                if (tModel.getTreePath(c) != null) {
                                                    matchedList.add(c);
                                                }
                                            }
                                        } else {
                                            matchedList = tempList;
                                        }

                                        tModel.setComponentList(matchedList);
                                        findComponentList = matchedList; // remember this locally
                                        // set up initial find hit
                                        tModel.setComponentListFindIndex(0);
                                        
                                    } 
                                    
                                } else {  // work with existing componentList

                                    if (findComponentList == null)
                                        findComponentList = componentList;
                                    
                                    // if there is a componentList already, then just
                                    // search that, further constraining it. 
                                    List tempList = 
                                        ComponentService.findComponentsByText(findComponentList, 
                                                                              searchText, 
                                                                              hierarchy);
                                    tModel.setComponentList(tempList);
                                    if (tempList != null && tempList.size() > 0) {
                                        // set up initial find hit
                                        tModel.setComponentListFindIndex(0);
                                    }
                                }
                                
                            } 

                        } catch (IRMISException ie) {
                            ie.printStackTrace();
                            model.setIRMISException(ie);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                    }
                    
                    public void doUIUpdateLogic() {
                        int eventCode = -1;
                        if (hierarchy == ComponentRelationshipType.HOUSING)
                            eventCode = ComponentModelEvent.HOUSING_FIND_CONFIG;
                        else if (hierarchy == ComponentRelationshipType.CONTROL)
                            eventCode = ComponentModelEvent.CONTROL_FIND_CONFIG;
                        else
                            eventCode = ComponentModelEvent.POWER_FIND_CONFIG;

                        // add this selection request to Swing event queue
                        model.notifyComponentModelListeners(new ComponentModelEvent(eventCode, 
                                                                                    findConfig));
                    }
                });
        }
    }


    /**
     * Explicitly adds a component to the list of prev/next components. Assumes we
     * caller is running in the Swing event thread.
     */
    public void addComponentToFind(Component c) {
        ComponentTreeModel tModel = (ComponentTreeModel)tree.getModel();
        List componentList = tModel.getComponentList();
        if (componentList == null)
            componentList = new ArrayList();
        tModel.setComponentList(componentList);
        componentList.add(c);

        // set up initial find hit
        tModel.setComponentListFindIndex(0);                        

        // tell GUI to draw buttons properly
        int eventCode = -1;
        if (hierarchy == ComponentRelationshipType.HOUSING)
            eventCode = ComponentModelEvent.HOUSING_FIND_CONFIG;
        else if (hierarchy == ComponentRelationshipType.CONTROL)
            eventCode = ComponentModelEvent.CONTROL_FIND_CONFIG;
        else
            eventCode = ComponentModelEvent.POWER_FIND_CONFIG;
        
        model.notifyComponentModelListeners(new ComponentModelEvent(eventCode, 
                                                                    findConfig));
    }


}
