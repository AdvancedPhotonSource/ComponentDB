/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

// persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationship;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;

// application helpers
import gov.anl.aps.irmis.apps.AbstractTreeModel;
import gov.anl.aps.irmis.apps.AppsUtil;

/**
 * Dialog to query user for component find constraints. The dialog varies a bit
 * depending on whether it's being used for housing, control, or power.
 */
public class ConfigureFindDialog extends JDialog
                        implements ActionListener {
    private int hierarchy;
    private ConfigureFindModel findConfig;
    private JTable typeList;
    private JTable roomList;
    private JTable systemList;
    private JTextField compTypeFilterTextField;
    private JTextField compIdTextField;

    public ConfigureFindDialog(Frame frame,
                               java.awt.Component locationComp, 
                               String title, int hierarchy,
                               ConfigureFindModel model) {

        super(frame, title, true);

        this.hierarchy = hierarchy;
        findConfig = model;

        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        //Create and initialize the buttons.
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);
        //
        final JButton applyButton = new JButton("Find");
        applyButton.setActionCommand("Find");
        applyButton.addActionListener(this);
        getRootPane().setDefaultButton(applyButton);

        //main part of the dialog

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        contentPane.setMaximumSize(new Dimension(400,300));
        contentPane.setPreferredSize(new Dimension(400,400));

        // find by component id (applies to all 3 hierarchies)
        JPanel compIdPanel = new JPanel();
        compIdPanel.setLayout(new BoxLayout(compIdPanel,BoxLayout.PAGE_AXIS));
        TitledBorder compIdTitle = BorderFactory.createTitledBorder("By Component ID:");
        compIdPanel.setBorder(compIdTitle);        

        // comp id text field
        compIdTextField = new JTextField(10);
        compIdTextField.setToolTipText("Enter the numeric-only component id (from PHP pages).");
        compIdTextField.setPreferredSize(new Dimension(370,20));
        compIdTextField.setMaximumSize(new Dimension(370,20));
        compIdPanel.add(compIdTextField);
        contentPane.add(compIdPanel);

        // if we are in housing hierarchy, add widget for room selection
        if (hierarchy == ComponentRelationshipType.HOUSING) {
            JPanel roomListPanel = new JPanel();
            roomListPanel.setLayout(new BoxLayout(roomListPanel,BoxLayout.PAGE_AXIS));
            TitledBorder roomListTitle = BorderFactory.createTitledBorder("By Room ( single-select ):");
            roomListPanel.setBorder(roomListTitle);

            // room components table
            roomList = new JTable(new ComponentTableModel());
            roomList.setShowHorizontalLines(true);
            roomList.setRowSelectionAllowed(true);
            roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            JScrollPane roomListScroller = new JScrollPane(roomList, vsbp, hsbp);
            roomListPanel.add(roomListScroller);
            contentPane.add(roomListPanel);
        }

        // if we are in control hierarchy, add widget for system selection
        if (hierarchy == ComponentRelationshipType.CONTROL) {
            JPanel systemListPanel = new JPanel();
            systemListPanel.setLayout(new BoxLayout(systemListPanel,BoxLayout.PAGE_AXIS));
            TitledBorder systemListTitle = BorderFactory.createTitledBorder("By System ( single-select ):");
            systemListPanel.setBorder(systemListTitle);

            // systems table
            systemList = new JTable(new SystemTableModel());
            systemList.setShowHorizontalLines(true);
            systemList.setRowSelectionAllowed(true);
            systemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            JScrollPane systemListScroller = new JScrollPane(systemList, vsbp, hsbp);
            systemListPanel.add(systemListScroller);
            contentPane.add(systemListPanel);
        }

        // find by component type (applies to all 3 hierarchies)
        JPanel typeListPanel = new JPanel();
        typeListPanel.setLayout(new BoxLayout(typeListPanel,BoxLayout.PAGE_AXIS));
        TitledBorder typeListTitle = BorderFactory.createTitledBorder("By Component Type ( multi-select ):");
        typeListPanel.setBorder(typeListTitle);        

        // comp type name filter
        compTypeFilterTextField = new JTextField(10);
        compTypeFilterTextField.setToolTipText("Enter a wildcard string to filter component type list.");
        compTypeFilterTextField.setPreferredSize(new Dimension(350,20));
        compTypeFilterTextField.setMaximumSize(new Dimension(350,20));
        typeListPanel.add(compTypeFilterTextField);
        compTypeFilterTextField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                        String filterText = compTypeFilterTextField.getText();
                        // uppercase it for case insensitive match
                        filterText = filterText.toUpperCase();
                        // change any * to reg-ex pattern
                        filterText = filterText.replaceAll("\\*",".*");
                        Pattern filterRegEx = Pattern.compile("^.*"+filterText+".*$");
                        Iterator compTypeIt = findConfig.getComponentTypes().iterator();
                        List filteredComponentTypeList = new ArrayList();
                        findConfig.setFilteredComponentTypes(filteredComponentTypeList);
                        while (compTypeIt.hasNext()) {
                            ComponentType ct = (ComponentType)compTypeIt.next();
                            Matcher matcher1 = 
                                filterRegEx.matcher(ct.getComponentTypeName().toUpperCase());
                            Matcher matcher2 = 
                                filterRegEx.matcher(ct.getDescription().toUpperCase());
                            if (matcher1.matches() || matcher2.matches())
                                filteredComponentTypeList.add(ct);
                        }
                        ComponentTypeTableModel tableModel = 
                            (ComponentTypeTableModel)typeList.getModel();
                        // request that comp type table update itself
                        tableModel.fireTableDataChanged();
                }
            });

        // component types table
        typeList = new JTable(new ComponentTypeTableModel());
        typeList.setShowHorizontalLines(true);
        typeList.setRowSelectionAllowed(true);
        typeList.getColumnModel().getColumn(0).setMinWidth(20);
        typeList.getColumnModel().getColumn(0).setPreferredWidth(110);        
        typeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    	JScrollPane typeListScroller = new JScrollPane(typeList, vsbp, hsbp);
        typeListPanel.add(typeListScroller);
        contentPane.add(typeListPanel);

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(applyButton);

        //Put everything together, using the content pane's BorderLayout.
        Container dialogPane = getContentPane();
        dialogPane.add(contentPane, BorderLayout.CENTER);
        dialogPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        pack();
        setLocationRelativeTo(locationComp);
    }

    // Handle clicks on the Find and Cancel buttons.
    public void actionPerformed(ActionEvent e) {
        if ("Find".equals(e.getActionCommand())) {

            findConfig.setCancelled(false);
            findConfig.setClearSearchField(true);

            // copy widget data to ConfigureFindDialog.findConfig
            String componentId = compIdTextField.getText();
            try {
                Long cid = new Long(componentId);
                if (cid != null)
                    findConfig.setComponentId(cid);
            } catch (NumberFormatException nfe) {
                findConfig.setComponentId(null);
            }

            int[] rows = typeList.getSelectedRows();
            List types = new ArrayList();
            findConfig.setSelectedComponentTypes(types);
            for (int i=0 ; i < rows.length ; i++) {
                int index = rows[i];
                ComponentType type = (ComponentType)(findConfig.getFilteredComponentTypes()).get(index);
                types.add(type);
            }
            if (hierarchy == ComponentRelationshipType.HOUSING) {
                rows = roomList.getSelectedRows();
                List rooms = new ArrayList();
                findConfig.setSelectedRoomComponents(rooms);
                for (int i=0 ; i < rows.length ; i++) {
                    int index = rows[i];
                    Component room = (Component)(findConfig.getRoomComponents()).get(index);
                    rooms.add(room);
                }
            }
            if (hierarchy == ComponentRelationshipType.CONTROL) {
                rows = systemList.getSelectedRows();
                List selectedSystems = new ArrayList();
                findConfig.setSelectedSystems(selectedSystems);
                for (int i=0 ; i < rows.length ; i++) {
                    int index = rows[i];
                    String system = (String)(findConfig.getSystems()).get(index);
                    selectedSystems.add(system);
                }
            }

        } else if ("Cancel".equals(e.getActionCommand())) {
            // note cancellation in findConfig model
            findConfig.setCancelled(true);
            findConfig.setClearSearchField(false);
        }
        setVisible(false);
    }

    public void reset() {
        findConfig.reset();
        typeList.clearSelection();
        if (roomList != null)
            roomList.clearSelection();
        if (systemList != null)
            systemList.clearSelection();

        compIdTextField.setText(null);
        compTypeFilterTextField.setText(null);
        ComponentTypeTableModel tableModel = 
            (ComponentTypeTableModel)typeList.getModel();
        // request that comp type table update itself
        tableModel.fireTableDataChanged();
    }

    /**
     * Model for display of component type table.
     */
    class ComponentTypeTableModel extends AbstractTableModel {
        private String[] columnNames = {"Type","Description"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            List filteredComponentTypeList = findConfig.getFilteredComponentTypes();
            int size = 0;
            if (filteredComponentTypeList != null)
                size = filteredComponentTypeList.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            List filteredComponentTypeList = findConfig.getFilteredComponentTypes();
            if (filteredComponentTypeList != null) {
                ComponentType type = 
                    (ComponentType)filteredComponentTypeList.get(row);
                if (col == 0) {
                    return type.getComponentTypeName();
                } else {
                    return type.getDescription();
                }
            } else {
                return " ";
            }

        }

        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }

    }

    /**
     * Model for display of component table.
     */
    class ComponentTableModel extends AbstractTableModel {
        private String[] columnNames = {"Room"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            List roomComponentList = findConfig.getRoomComponents();
            if (roomComponentList != null)
                size = roomComponentList.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            List roomComponentList = findConfig.getRoomComponents();
            if (roomComponentList != null) {
                Component room = 
                    (Component)roomComponentList.get(row);
                if (col == 0) {
                    ComponentRelationship rel = 
                        room.getParentRelationship(ComponentRelationshipType.HOUSING);
                    return rel.getLogicalDescription();
                } else {
                    return " ";
                }
            } else {
                return " ";
            }
        }

        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }

    }

    /**
     * Model for display of systems table.
     */
    class SystemTableModel extends AbstractTableModel {
        private String[] columnNames = {"System"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            List systems = findConfig.getSystems();
            if (systems != null)
                size = systems.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            List systems = findConfig.getSystems();
            if (systems != null) {
                String system = 
                    (String)systems.get(row);
                if (col == 0) {
                    return system;
                } else {
                    return " ";
                }
            } else {
                return " ";
            }
        }

        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }
}
