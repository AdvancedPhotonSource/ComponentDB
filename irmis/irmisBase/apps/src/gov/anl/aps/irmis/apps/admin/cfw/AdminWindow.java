/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.admin.cfw;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;
import java.util.logging.*;
import java.util.EventObject;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import gov.sns.application.*;

// jwizz wizard toolkit
import net.javaprog.ui.wizard.*;

// other IRMIS sub-applications

// persistence layer
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.login.Role;
import gov.anl.aps.irmis.persistence.login.RoleName;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.DAOStaleObjectStateException;

// for registering protected gui components
import gov.anl.aps.irmis.login.LoginUtil;
import gov.anl.aps.irmis.login.RolePrincipal;

// service layer
import gov.anl.aps.irmis.service.shared.PersonService;
import gov.anl.aps.irmis.service.IRMISException;

// application helpers
import gov.anl.aps.irmis.apps.AbstractTreeModel;
import gov.anl.aps.irmis.apps.AppsUtil;
import gov.anl.aps.irmis.apps.irmis.cfw.Main;

// wizards
import gov.anl.aps.irmis.apps.component.cfw.wizard.*;

/**
 * Primary GUI for IRMIS Administrative application. All window layout, and Swing event
 * listeners are done here. Business logic work is delegated to <code>AdminDocument</code>,
 * which in turn requests that the AdminModel notify us here of changes to the data. In short,
 * we listen for <code>AdminModelEvent</code> here.
 */
public class AdminWindow extends XalInternalWindow {

	/** The main model for the document */
	final protected AdminModel _model;

    final protected AdminDocument document;

    static RolePrincipal[] adminPrincipal = { new RolePrincipal(RoleName.ADMIN) };

    // Hold reference to parent desktop frame. Used to put up busy icon during work.
    JFrame _appFrame;    

	// Swing GUI components
    private JTabbedPane topPanel;

    // for Users tab
    private JTable personList;
    private JTable attrList;
    private JTable roleList;
    private List roles;

    // for Export/Import tabs
    private JTable compTypeList;

	/** 
	 * Creates a new instance of CableWindow
	 * @param aDocument The document for this window
	 */
    public AdminWindow(final XalInternalDocument aDocument) {
        super(aDocument);

        document = (AdminDocument)aDocument;
		_model = document.getModel();

        // make AdminWindow a listener for changes in AdminModel
		_model.addAdminModelListener( new AdminModelListener() {
                public void modified(AdminModelEvent e) {
                    updateView(e);
                }
            });

       _appFrame = ((DesktopApplication)Application.getApp()).getDesktopFrame();

        // initial application window size
        setSize(500, 300);

        // build contents
		makeContents();
    }

    /**
     * Top-level method to build up Swing GUI components.
     */
    public void makeContents() {

        // topmost organization 
        topPanel = new JTabbedPane();
        
        JPanel usersPanel = new JPanel();
        makeUsersTab(usersPanel);

        JPanel auditPanel = new JPanel();
        auditPanel.setLayout(new BoxLayout(auditPanel,BoxLayout.LINE_AXIS));
        JLabel niyLabel = new JLabel("Feature not implemented yet.");
        auditPanel.add(niyLabel);

        JPanel exportPanel = new JPanel();
        makeExportTab(exportPanel);

        topPanel.addTab("Users", null, usersPanel, "Administer IRMIS Users");
        topPanel.addTab("Audit Actions", null, auditPanel, "Review IRMIS user activity");
        topPanel.addTab("Export Data", null, exportPanel, "Export Component Types as XML");
    	getContentPane().add(topPanel);        
    }

    /**
     * Build up Users tab.
     */
    private void makeUsersTab(JPanel usersPanel) {

        // re-usable configurations
        Dimension smallButtonDim = new Dimension(20,20);
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        usersPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;

        JPanel personsPanel = new JPanel();
        personsPanel.setLayout(new BoxLayout(personsPanel,BoxLayout.PAGE_AXIS));

        // person list panel
        JPanel personListPanel = new JPanel(new BorderLayout());
        TitledBorder personListTitle = BorderFactory.createTitledBorder("IRMIS Users");
        personListPanel.setBorder(personListTitle);

        // person list table
        personList = new JTable(new PersonsTableModel());
        personList.setShowHorizontalLines(true);
        personList.setColumnSelectionAllowed(false);
        personList.setRowSelectionAllowed(true);
    	JScrollPane personListScroller = new JScrollPane(personList, vsbp, hsbp);
        personListPanel.add(personListScroller);
        personList.getColumnModel().getColumn(0).setPreferredWidth(156);
        personList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // handle row selection in person list table
        personList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int selectedPersonRow = lsm.getMinSelectionIndex();
                        List persons;
                        persons = _model.getPersons();
                        Person selectedPerson = (Person)persons.get(selectedPersonRow);
                        _model.setSelectedPerson(selectedPerson);

                        _model.setSelectedRole(null);
                        Set temp = selectedPerson.getRoles();
                        if (temp != null)
                            roles = new ArrayList(temp);
                        else
                            roles = null;

                        // request that person attribute table update itself
                        PersonAttrTableModel paTableModel = (PersonAttrTableModel)attrList.getModel();
                        paTableModel.fireTableDataChanged();

                        // request that person role table update itself
                        PersonRolesTableModel prTableModel = (PersonRolesTableModel)roleList.getModel();
                        prTableModel.fireTableDataChanged();
                    }
                }
            });

        // person action button bar
        JPanel personButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton personAddButton = new JButton("Add...");
        LoginUtil.registerProtectedComponent(personAddButton, adminPrincipal);
        personAddButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Person newPerson = NewPersonDialog.showDialog(_appFrame, null);
                    if (newPerson != null) {
                        document.actionSavePerson(newPerson);
                    }
                }
            });

        personButtons.add(personAddButton);

        // add person list and button panels to personsPanel
        personsPanel.add(personListPanel);
        personsPanel.add(personButtons);

        // selected person panel
        JPanel selectedPersonPanel = new JPanel();
        selectedPersonPanel.setLayout(new BoxLayout(selectedPersonPanel,BoxLayout.PAGE_AXIS));
        
        // panel to display the bits of info about a person
        JPanel personAttrPanel = new JPanel(new BorderLayout());
        TitledBorder personAttrTitle = BorderFactory.createTitledBorder("User Details");
        personAttrPanel.setBorder(personAttrTitle);

        // person attributes table
        attrList = new JTable(new PersonAttrTableModel());
        attrList.setTableHeader(null);
        attrList.setShowHorizontalLines(true);
        attrList.setRowSelectionAllowed(true);

        JScrollPane attrListScroller = new JScrollPane(attrList, vsbp, hsbp);
        attrList.getColumnModel().getColumn(0).setMinWidth(80);
        attrList.getColumnModel().getColumn(1).setPreferredWidth(100);
        attrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // handle row selection in attributes table
        attrList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int row = lsm.getMinSelectionIndex();
                    }
                }
            });

        personAttrPanel.add(attrListScroller);

        // person roles
        JPanel personRolePanel = new JPanel(new BorderLayout());
        TitledBorder personRoleTitle = BorderFactory.createTitledBorder("User Roles");
        personRolePanel.setBorder(personRoleTitle);

        JPanel internalPersonRolePanel = new JPanel();
        internalPersonRolePanel.setLayout(new BoxLayout(internalPersonRolePanel,BoxLayout.PAGE_AXIS));

        // person roles table
        roleList = new JTable(new PersonRolesTableModel());
        roleList.setTableHeader(null);
        roleList.setShowHorizontalLines(true);
        roleList.setRowSelectionAllowed(true);

        JScrollPane roleListScroller = new JScrollPane(roleList, vsbp, hsbp);
        roleList.getColumnModel().getColumn(0).setMinWidth(80);
        roleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // handle row selection in attributes table
        roleList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int row = lsm.getMinSelectionIndex();
                        Role selectedRole = (Role)roles.get(row);
                        _model.setSelectedRole(selectedRole);
                    }
                }
            });

        // person roles button bar
        JPanel personRoleButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton personRoleAddButton = new JButton("Add...");
        LoginUtil.registerProtectedComponent(personRoleAddButton, adminPrincipal);
        personRoleAddButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Person person = _model.getSelectedPerson();
                    Role newRole = NewRoleDialog.showDialog(_appFrame, null, _model.getRoleNameList());
                    if (newRole != null) {
                        newRole.setPerson(person);
                        person.addRole(newRole);
                        document.actionSavePerson(person);
                    }
                }
            });

        JButton personRoleRemoveButton = new JButton("Remove");
        LoginUtil.registerProtectedComponent(personRoleRemoveButton, adminPrincipal);
        personRoleRemoveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Person selectedPerson = _model.getSelectedPerson();
                    Role selectedRole = _model.getSelectedRole();
                    if (selectedRole != null) {
                        roles.remove(selectedRole);
                        Set roleSet = selectedPerson.getRoles();
                        roleSet.remove(selectedRole);
                        document.actionSavePerson(selectedPerson);
                    }
                }
            });
        personRoleButtons.add(personRoleAddButton);
        personRoleButtons.add(personRoleRemoveButton);

        internalPersonRolePanel.add(roleListScroller);
        internalPersonRolePanel.add(personRoleButtons);
        personRolePanel.add(internalPersonRolePanel);
        
        selectedPersonPanel.add(personAttrPanel);
        selectedPersonPanel.add(personRolePanel);
        

        // split between personsPanel and selected person info on right
        JSplitPane lrSplitPane = 
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,personsPanel,selectedPersonPanel);
        lrSplitPane.setDividerLocation(170);

        usersPanel.add(lrSplitPane, c);

    }

    /**
     * Build up Export Data tab.
     */
    private void makeExportTab(JPanel exportPanel) {

        // re-usable configurations
        Dimension smallButtonDim = new Dimension(20,20);
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;


        exportPanel.setLayout(new BoxLayout(exportPanel,BoxLayout.PAGE_AXIS));        

        JPanel componentTypesPanel = new JPanel();
        componentTypesPanel.setLayout(new BoxLayout(componentTypesPanel,BoxLayout.PAGE_AXIS));

        // component type list panel
        JPanel compTypeListPanel = new JPanel(new BorderLayout());
        TitledBorder compTypeListTitle = BorderFactory.createTitledBorder("Component Types");
        compTypeListPanel.setBorder(compTypeListTitle);

        // comp type list table
        compTypeList = new JTable(new CompTypeResultsTableModel());
        compTypeList.setShowHorizontalLines(true);
        compTypeList.setColumnSelectionAllowed(false);
        compTypeList.setRowSelectionAllowed(true);
        compTypeList.getColumnModel().getColumn(0).setCellRenderer(new CompTypeTableCellRenderer());
    	JScrollPane compTypeListScroller = new JScrollPane(compTypeList, vsbp, hsbp);
        compTypeListPanel.add(compTypeListScroller);
        compTypeList.getColumnModel().getColumn(0).setPreferredWidth(147);
        compTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // handle row selection in comp type list table
        compTypeList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int selectedCompTypeResultsRow = lsm.getMinSelectionIndex();
                        List ctList = _model.getFilteredComponentTypeList();
                        ComponentType selectedComponentType = 
                            (ComponentType)ctList.get(selectedCompTypeResultsRow);
                        _model.setSelectedComponentType(selectedComponentType);
                    }
                }
            });

        componentTypesPanel.add(compTypeListPanel);

        // export button bar
        JPanel exportButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton exportButton = new JButton("Export...");
        exportButton.setToolTipText("Export the selected component type to an XML file.");
        LoginUtil.registerProtectedComponent(exportButton, adminPrincipal);
        exportButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ComponentType selectedComponentType = _model.getSelectedComponentType();
                    if (selectedComponentType == null)
                        return;

                    // pop up dialog to get file name
                    JFrame appFrame = 
                        ((DesktopApplication)Application.getApp()).getDesktopFrame();
                    JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showSaveDialog(appFrame);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        // check to see if it exists, and pop up warning
                        if (file.exists()) {
                            String msg = "The file you chose exists already. Are you sure you want to overwrite it?";
                            int n = 
                                JOptionPane.showConfirmDialog(appFrame, msg, "Are you sure?", JOptionPane.YES_NO_OPTION);
                            if (n == JOptionPane.YES_OPTION) {
                                document.actionExportComponentTypeToFile(file, selectedComponentType);
                            }
                        } else {
                            document.actionExportComponentTypeToFile(file, selectedComponentType);
                        }
                    }
                }
            });
        exportButtons.add(exportButton);
        componentTypesPanel.add(exportButtons);

        exportPanel.add(componentTypesPanel);
    }

    /**
     * Update graphical view of AdminModel data based on event type.
     *
     * @param event type of data model change
     */
    public void updateView(AdminModelEvent event) {

        // check first for any exception
        IRMISException ie = _model.getIRMISException();
        if (ie != null) {  // uh-oh: figure out if it's fatal or we just need to reload data
            Throwable cause = ie.getCause();
            if (cause != null && cause instanceof DAOStaleObjectStateException) {
                Logger.getLogger("global").log(Level.WARNING, "IRMIS Service Error.", ie);
                Application.displayError("IRMIS Service Warning","Your data is stale. Reloading application data.");
                Main.requestResetOfDocuments();

            } else {
                Logger.getLogger("global").log(Level.WARNING, "IRMIS Service Error.", ie);
                Application.displayError("IRMIS Service Error", "Exiting due to exception... ", ie);
                System.exit(-1);
            }
            return;
        }

        switch(event.getType()) {

        case AdminModelEvent.NEW_PERSONS: {
            // request that persons table update itself
            PersonsTableModel tableModel = (PersonsTableModel)personList.getModel();
            tableModel.fireTableDataChanged();            

            // update person attributes table
            PersonAttrTableModel attrTableModel = (PersonAttrTableModel)attrList.getModel();
            attrTableModel.fireTableDataChanged();

            // update person roles table
            PersonRolesTableModel rolesTableModel = (PersonRolesTableModel)roleList.getModel();
            rolesTableModel.fireTableDataChanged();

            break;
        }

        case AdminModelEvent.PERSON_SAVED: {
            // request that persons table update itself
            PersonsTableModel tableModel = (PersonsTableModel)personList.getModel();
            tableModel.fireTableDataChanged();

            // make sure they are selected in table
            int row = _model.getPersons().indexOf(_model.getSelectedPerson());
            personList.setRowSelectionInterval(row, row);

            // make sure selection is visible (due to scroll window)
            Rectangle rect = personList.getCellRect(row, 0, true);
            personList.scrollRectToVisible(rect);
            break;
        }

        case AdminModelEvent.NEW_COMPONENT_TYPES: {
            // request that component type table update itself
            CompTypeResultsTableModel tableModel = (CompTypeResultsTableModel)compTypeList.getModel();
            tableModel.fireTableDataChanged();            
            break;
        }

        default: {}
        }

    }

    /********************************************************************
     * Inner classes supporting Swing components
     ********************************************************************/
    /**
     * Model for display/editing of component type document table.
     */
    class PersonsTableModel extends AbstractTableModel {
        private String[] columnNames = {"Name"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            List persons = _model.getPersons();
            if (persons != null)
                size = persons.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {

            List persons = _model.getPersons();
            if (persons != null) {
                Person person = (Person)persons.get(row);
                if (col == 0) {
                    return person.getLastName()+", "+person.getFirstName();
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

        public boolean isCellEditable(int row, int col) {
            return false;
        }

    }    

    /**
     * Person attribute table model. Displays the set of 
     * different scalar info about a person.
     */
    class PersonAttrTableModel extends AbstractTableModel {

        private String[] columnNames = {"Name","Value"};

        public PersonAttrTableModel() {
            super();
        }

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int fixedSize = 4;
            return fixedSize;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            Person person = _model.getSelectedPerson();
            if (person != null) {
                switch (row) {
                case 0: {
                    if (col == 0) 
                        return "First Name";
                    else
                        return person.getFirstName();
                }
                case 1: {
                    if (col == 0)
                        return "Middle Name";
                    else
                        return person.getMiddleName();
                }
                case 2: {
                    if (col == 0)
                        return "Last Name";
                    else 
                        return person.getLastName();
                }
                case 3: {
                    if (col == 0)
                        return "User Id";
                    else
                        return person.getUserid();
                }
                default: {
                    return " ";
                }
                }
                
            } else {
                return " ";
            }
        }

        // called after edit of attribute cell is complete
        public void setValueAt(Object value, int row, int col) {
            String strValue = (String)value;
            Person person = _model.getSelectedPerson();
            if (person != null) {
                switch (row) {
                case 0: {
                    person.setFirstName(strValue);
                    break;
                }
                case 1: {
                    person.setMiddleName(strValue);
                    break;
                }
                case 2: {
                    person.setLastName(strValue);
                    break;
                }
                case 3: {
                    person.setUserid(strValue);
                    break;
                }
                }
            }
            try {
                PersonService.savePerson(person);
            } catch (IRMISException ie) {
                ie.printStackTrace();
            }
        }
        
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (LoginUtil.isPermitted(adminPrincipal) && col == 1)
                return true;
            else
                return false;
        }
    }

    /**
     * Person roles table model. Displays the set of 
     * roles a person has.
     */
    class PersonRolesTableModel extends AbstractTableModel {

        private String[] columnNames = {"Role"};

        public PersonRolesTableModel() {
            super();
        }

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            if (roles != null) {
                size = roles.size();
            }
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            if (roles != null) {
                Role role = (Role)roles.get(row);
                return role.getRoleName().getRoleName();
            } else {
                return " ";
            }
        }

        // called after edit of attribute cell is complete
        public void setValueAt(Object value, int row, int col) {

        }
        
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (LoginUtil.isPermitted(adminPrincipal) && col == 1)
                return true;
            else
                return false;
        }
    }

    /**
     * Provides methods to get comp type results table cell data from
     * actual <code>ComponentTypeModel</code>. 
     */
    class CompTypeResultsTableModel extends AbstractTableModel {
        private String[] columnNames = {"Component Type"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            List ctList = _model.getFilteredComponentTypeList();            
            int size = 0;
            if (ctList == null)
                return size;
            size = ctList.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            List ctList = _model.getFilteredComponentTypeList();            
            if (ctList != null) {            
                ComponentType compType = (ComponentType)ctList.get(row);
                return compType.getComponentTypeName();
            } else {
                return null;
            }
        }
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }

    /**
     * Customized table cell renderer used for comp type table. 
     */
    class CompTypeTableCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent (JTable table,
                                                        Object value,
                                                        boolean selected,
                                                        boolean focused,
                                                        int row, int column) {
            setEnabled(table==null || table.isEnabled());
            List compTypeList = null;
            compTypeList = _model.getFilteredComponentTypeList();
            if (compTypeList != null) {
                ComponentType ct = (ComponentType)compTypeList.get(row);
                if (column == 0) {
                    if (!selected) {
                        if (ct.getIsBaseType()) {
                            setBackground(Color.pink);
                            setToolTipText("Base component type (restricted editing)");
                        } else {
                            setBackground(Color.white);
                            setToolTipText(null);
                        }
                    } else {
                        setBackground(new Color(0xCC,0xCC,0xFF));
                        if (ct.getIsBaseType()) {
                            setToolTipText("Base component type (restricted editing)");
                        } else {
                            setToolTipText(null);
                        }

                    }
                    
                    setForeground(Color.blue);
                }
            }
            super.getTableCellRendererComponent(table,value,selected,focused,row,column);
            return this;
        }
    }
}
