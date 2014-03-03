/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenttype.cfw;

import java.io.File;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.logging.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import gov.sns.application.*;

import gov.anl.aps.irmis.apps.AppsUtil;
import gov.anl.aps.irmis.apps.irmis.cfw.Main;

// persistence layer
import gov.anl.aps.irmis.persistence.DAOStaleObjectStateException;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentTypeStatus;
import gov.anl.aps.irmis.persistence.component.ComponentTypeFunction;
import gov.anl.aps.irmis.persistence.component.ComponentTypeInterface;
import gov.anl.aps.irmis.persistence.component.ComponentTypePerson;
import gov.anl.aps.irmis.persistence.component.ComponentPortTemplate;
import gov.anl.aps.irmis.persistence.component.PortPinTemplate;
import gov.anl.aps.irmis.persistence.component.PortPinType;
import gov.anl.aps.irmis.persistence.component.PortPinDesignator;
import gov.anl.aps.irmis.persistence.pv.URI;
import gov.anl.aps.irmis.persistence.login.RoleName;

// service layer
import gov.anl.aps.irmis.service.IRMISException;

// for registering protected gui components
import gov.anl.aps.irmis.login.LoginUtil;
import gov.anl.aps.irmis.login.RolePrincipal;

// component type wizard(s)
import gov.anl.aps.irmis.apps.componenttype.cfw.wizard.*;

/**
 * Primary GUI for IRMIS Component Type Viewer/Editor application. 
 * Business logic work is delegated to <code>ComponentTypeDocument</code>, which
 * in turn requests that the ComponentTypeModel notify us here of changes to the data. In short,
 * we listen for <code>ComponentTypeModelEvent</code> here.
 */
public class ComponentTypeWindow extends XalInternalWindow {

	/** The main model for the document */
	final protected ComponentTypeModel _model;
    final protected ComponentTypeDocument document;

    static RolePrincipal[] editPrincipal = { new RolePrincipal(RoleName.COMPONENT_TYPE_EDITOR) };
    static RolePrincipal[] editPortPrincipal = { new RolePrincipal(RoleName.COMPONENT_TYPE_PORT_EDITOR) };

	// Swing GUI components
    private JPanel topPanel;
    private JPanel compTypesPanel;
    private JPanel compTypeListPanel;
    private JPanel selectedCompTypePanel;
    private JPanel compTypeDetailsPanel;
    private JPanel compTypeLogicalPhysicalPanel;
    private JPanel compTypeAttrPanel;
    private JPanel logicalPanel;
    private JPanel physicalPanel;
    private JPanel requiredPresentedPanel;
    private JPanel requiredPanel;
    private JPanel presentedPanel;

    private JSplitPane lrSplitPane;

    private JTable compTypeList;
    private JTable attrList;
    private JTable requiredList;
    private JTable presentedList;

    private JButton compTypeEditButton;
    private JButton compTypeAddButton;

    private JTree physicalTree;
    private DefaultTreeModel physicalTreeModel;
    private DefaultMutableTreeNode physicalTreeModelRoot =
        new DefaultMutableTreeNode("Ports");

    private JTextField compTypeFilterTextField;

    // ordered list of required/presented interfaces
    private List requiredInterfaceList = new ArrayList();
    private List presentedInterfaceList = new ArrayList();

	/** 
	 * Creates a new instance of ComponentWindow
	 * @param aDocument The document for this window
	 */
    public ComponentTypeWindow(final XalInternalDocument aDocument) {
        super(aDocument);
        document = (ComponentTypeDocument)aDocument;

		_model = ((ComponentTypeDocument)aDocument).getModel();

        // make ComponentTypeWindow a listener for changes in ComponentTypeModel
		_model.addComponentTypeModelListener( new ComponentTypeModelListener() {
                public void modified(ComponentTypeModelEvent e) {
                    updateView(e);
                }
            });

        // initial application window size
        setSize(620, 405);

        // build contents
		makeContents();
    }

    /**
     * Top-level method to build up Swing GUI components.
     */
    public void makeContents() {

        // scrollbar policy
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        // topmost panel
        topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;

        compTypesPanel = new JPanel();
        compTypesPanel.setLayout(new BoxLayout(compTypesPanel,BoxLayout.PAGE_AXIS));

        // comp type list panel
        //compTypeListPanel = new JPanel(new BorderLayout());
        compTypeListPanel = new JPanel();
        compTypeListPanel.setLayout(new BoxLayout(compTypeListPanel, BoxLayout.PAGE_AXIS));
        TitledBorder compTypeListTitle = BorderFactory.createTitledBorder("Component Types");
        compTypeListPanel.setBorder(compTypeListTitle);

        // comp type name filter
        compTypeFilterTextField = new JTextField(10);
        compTypeFilterTextField.setToolTipText("Enter a wildcard string to filter component type list.");
        compTypeFilterTextField.setPreferredSize(new Dimension(350,20));
        compTypeFilterTextField.setMaximumSize(new Dimension(350,20));
        compTypeListPanel.add(compTypeFilterTextField);
        compTypeFilterTextField.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    char keyChar = e.getKeyChar();
                    if (keyChar == '\n') {
                        String filterText = compTypeFilterTextField.getText();
                        // uppercase it for case insensitive match
                        filterText = filterText.toUpperCase();                        
                        // change any * to reg-ex pattern
                        filterText = filterText.replaceAll("\\*",".*");
                        Pattern filterRegEx = Pattern.compile("^.*"+filterText+".*$");
                        Iterator compTypeIt = _model.getComponentTypeList().iterator();
                        List filteredComponentTypeList = new ArrayList();
                        while (compTypeIt.hasNext()) {
                            ComponentType ct = (ComponentType)compTypeIt.next();
                            Matcher matcher1 = 
                                filterRegEx.matcher(ct.getComponentTypeName().toUpperCase());
                            Matcher matcher2 = 
                                filterRegEx.matcher(ct.getDescription().toUpperCase());
                            if (matcher1.matches() || matcher2.matches())
                                filteredComponentTypeList.add(ct);
                        }
                        _model.setFilteredComponentTypeList(filteredComponentTypeList);
                        CompTypeResultsTableModel tableModel = 
                            (CompTypeResultsTableModel)compTypeList.getModel();
                        // request that comp type table update itself
                        tableModel.fireTableDataChanged();
                    }
                }
            });

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
                        if (ctList != null) {
                            ComponentType selectedComponentType = 
                                (ComponentType)ctList.get(selectedCompTypeResultsRow);
                            //_model.setSelectedComponentType(selectedComponentType);
                            document.actionAttributeSearch(selectedComponentType);
                        }
                    }
                }
            });

        // component type action button bar
        JPanel compTypeButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        compTypeEditButton = new JButton("Edit...");
        LoginUtil.registerProtectedComponent(compTypeEditButton, editPrincipal);
        compTypeEditButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFrame appFrame = 
                        ((DesktopApplication)Application.getApp()).getDesktopFrame();
                    ComponentType ct = _model.getSelectedComponentType();
                    NewTypeWizardModel wizardData = NewTypeWizard.showWizard(appFrame, ct);
                    if (wizardData.getCancelled() == false) {
                        document.actionSaveComponentType(wizardData.getComponentType());
                    }
                }
            });
        
        compTypeAddButton = new JButton("Add...");
        LoginUtil.registerProtectedComponent(compTypeAddButton, editPrincipal);
        compTypeAddButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFrame appFrame = 
                        ((DesktopApplication)Application.getApp()).getDesktopFrame();
                    NewTypeWizardModel wizardData = NewTypeWizard.showWizard(appFrame, _model.getComponentTypeList());
                    if (wizardData.getCancelled() == false) {
                        document.actionSaveComponentType(wizardData.getComponentType());
                    }
                }
            });

        compTypeButtons.add(compTypeEditButton);
        compTypeButtons.add(compTypeAddButton);

        // add comp type list and button panels to compTypesPanel
        compTypesPanel.add(compTypeListPanel);
        compTypesPanel.add(compTypeButtons);

        // make the panel used to display data after comp type is selected
        selectedCompTypePanel = makeSelectedCompTypePanelContents();

        // split between iocsPanel and ioc details info on right
        lrSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                     compTypesPanel,
                                     selectedCompTypePanel);
        lrSplitPane.setDividerLocation(150);

        // add split panes to topPanel
        topPanel.add(lrSplitPane,c);

    	getContentPane().add(topPanel);
    }

    /**
     *
     */
    private JPanel makeSelectedCompTypePanelContents() {
        
        // scrollbar policy
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        // comp type details panel containing details of a particular type
        compTypeDetailsPanel = new JPanel();
        compTypeDetailsPanel.setLayout(new BoxLayout(compTypeDetailsPanel,BoxLayout.PAGE_AXIS));

        // panel to display the many bits of info about a comp type
        compTypeAttrPanel = new JPanel(new BorderLayout());
        TitledBorder compTypeAttrTitle = BorderFactory.createTitledBorder("General Info");
        compTypeAttrPanel.setBorder(compTypeAttrTitle);

        // component type attributes table
        attrList = new JTable(new CompTypeAttrTableModel());
        attrList.setTableHeader(null);
        attrList.setShowHorizontalLines(true);
        attrList.setRowSelectionAllowed(true);
        attrList.getColumnModel().getColumn(1).setCellRenderer(new CompTypeAttrValueTableCellRenderer());

        JScrollPane attrListScroller = new JScrollPane(attrList, vsbp, hsbp);
        attrList.getColumnModel().getColumn(0).setMinWidth(80);
        attrList.getColumnModel().getColumn(1).setPreferredWidth(300);
        attrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // handle row selection in attributes table
        attrList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int row = lsm.getMinSelectionIndex();
                        if (row > 13) {
                            String urlString = (String)attrList.getValueAt(row,1);
                            AppsUtil.showURL(topPanel, urlString);
                        }

                    }
                }
            });

        compTypeAttrPanel.add(attrListScroller);

        // panel to display the logical and physical interfaces
        compTypeLogicalPhysicalPanel = new JPanel();
        compTypeLogicalPhysicalPanel.
            setLayout(new BoxLayout(compTypeLogicalPhysicalPanel,BoxLayout.LINE_AXIS));

        // logical interface panel
        logicalPanel = new JPanel(new BorderLayout());
        TitledBorder logicalTitle = BorderFactory.createTitledBorder("Logical Interfaces");
        logicalPanel.setBorder(logicalTitle);        

        // required and presented parts of logical interface panel
        requiredPresentedPanel = new JPanel();
        requiredPresentedPanel.
            setLayout(new BoxLayout(requiredPresentedPanel,BoxLayout.PAGE_AXIS));

        // required
        requiredPanel = new JPanel(new BorderLayout());
        TitledBorder requiredTitle = BorderFactory.createTitledBorder("required");
        requiredPanel.setBorder(requiredTitle);                
        requiredList = new JTable(new RequiredTableModel());
        JScrollPane requiredListScroller = new JScrollPane(requiredList, vsbp, hsbp);
        requiredPanel.add(requiredListScroller);
        requiredList.getColumnModel().getColumn(0).setMaxWidth(80);
        requiredList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // presented
        presentedPanel = new JPanel(new BorderLayout());
        TitledBorder presentedTitle = BorderFactory.createTitledBorder("presented");
        presentedPanel.setBorder(presentedTitle);
        presentedList = new JTable(new PresentedTableModel());
        JScrollPane presentedListScroller = new JScrollPane(presentedList, vsbp, hsbp);
        presentedPanel.add(presentedListScroller);
        presentedList.getColumnModel().getColumn(0).setMaxWidth(80);
        presentedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // build up
        requiredPresentedPanel.add(requiredPanel);
        requiredPresentedPanel.add(presentedPanel);
        logicalPanel.add(requiredPresentedPanel);

        // physical interface panel
        physicalPanel = new JPanel(new BorderLayout());
        TitledBorder physicalTitle = BorderFactory.createTitledBorder("Physical Interfaces");
        physicalPanel.setBorder(physicalTitle);
        physicalPanel.setMinimumSize(new Dimension(160,60));

        // tree of physical ports/pins
        physicalTreeModel = new DefaultTreeModel(null);
        physicalTree = new JTree(physicalTreeModel);
        ToolTipManager.sharedInstance().registerComponent(physicalTree);
        physicalTree.setCellRenderer(new PhysicalTreeRenderer());
        JScrollPane physicalTreeScroller = 
            new JScrollPane(physicalTree, vsbp, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        physicalPanel.add(physicalTreeScroller);

        // split between iocsPanel and ioc details info on right
        JSplitPane lpSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                logicalPanel,
                                                physicalPanel);
        lpSplitPane.setDividerLocation(300);

        compTypeLogicalPhysicalPanel.add(lpSplitPane);

        compTypeDetailsPanel.add(compTypeAttrPanel);
        compTypeDetailsPanel.add(compTypeLogicalPhysicalPanel);


        return compTypeDetailsPanel;
    }


    /**
     * Update graphical view of ComponentTypeModel data based on event type.
     *
     * @param event type of data model change
     */
    public void updateView(ComponentTypeModelEvent event) {

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

        case ComponentTypeModelEvent.NEW_COMPONENT_TYPE_LIST: {
            // request that comp type results table update itself
            CompTypeResultsTableModel tableModel = 
                (CompTypeResultsTableModel)compTypeList.getModel();
            tableModel.fireTableDataChanged();

            // request that comp type attr table update itself
            CompTypeAttrTableModel attrTableModel = 
                (CompTypeAttrTableModel)attrList.getModel();
            attrTableModel.fireTableDataChanged();

            // update the interface required and presented tables
            requiredInterfaceList = null;
            presentedInterfaceList = null;
            RequiredTableModel rtm = (RequiredTableModel)requiredList.getModel();
            rtm.fireTableDataChanged();
            PresentedTableModel ptm = (PresentedTableModel)presentedList.getModel();
            ptm.fireTableDataChanged();

            // update the port type tree
            physicalTreeModelRoot.removeAllChildren();
            physicalTreeModel.nodeStructureChanged(physicalTreeModelRoot);

            if (_model.getSelectedComponentType() == null)
                compTypeList.changeSelection(0,0,false,false);

            break;
        }

        case ComponentTypeModelEvent.NEW_ATTR_LIST: {
            ComponentType selectedComponentType = _model.getSelectedComponentType();
            //attrList.setModel(new CompTypeAttrTableModel(selectedComponentType));

            CompTypeAttrTableModel tableModel = 
                (CompTypeAttrTableModel)attrList.getModel();
                
            // request that comp type attr table update itself
            tableModel.fireTableDataChanged();

            // break up component type interfaces into required/presented arrays
            Set interfaces = selectedComponentType.getComponentTypeInterfaces();
            Iterator it = interfaces.iterator();
            requiredInterfaceList = new ArrayList();
            presentedInterfaceList = new ArrayList();
            while (it.hasNext()) {
                ComponentTypeInterface cti = (ComponentTypeInterface)it.next();
                if (cti.getRequired())
                    requiredInterfaceList.add(cti);
                else
                    presentedInterfaceList.add(cti);
            }

            RequiredTableModel rtm = (RequiredTableModel)requiredList.getModel();
            rtm.fireTableDataChanged();
            PresentedTableModel ptm = (PresentedTableModel)presentedList.getModel();
            ptm.fireTableDataChanged();

            // build up physical interfaces tree widget
            physicalTreeModelRoot.removeAllChildren();
            List componentPortTemplates = 
                selectedComponentType.getComponentPortTemplates();
            if (componentPortTemplates.size() > 0) {
                physicalTreeModelRoot.removeAllChildren();
                physicalTreeModel.setRoot(physicalTreeModelRoot);
                // build up ports
                Iterator portIt = componentPortTemplates.iterator();
                while (portIt.hasNext()) {
                    ComponentPortTemplate cpte = (ComponentPortTemplate)portIt.next();
                    DefaultMutableTreeNode portLeaf = 
                        new DefaultMutableTreeNode(cpte);
                    physicalTreeModelRoot.add(portLeaf);

                    // build up pins
                    Set portPinTemplates = cpte.getPortPinTemplates();
                    if (portPinTemplates.size() > 0) {
                        Iterator pinIt = portPinTemplates.iterator();
                        while (pinIt.hasNext()) {
                            PortPinTemplate ppte = (PortPinTemplate)pinIt.next();
                            DefaultMutableTreeNode pinLeaf =
                                new DefaultMutableTreeNode(ppte);
                            portLeaf.add(pinLeaf);
                        }
                    }
                }

            } else {  // componentPortTemplates.size() == 0
                physicalTreeModel.setRoot(null);
            }
            
            physicalTreeModel.nodeStructureChanged(physicalTreeModelRoot);
            ComponentPortPopupMenu portPopupMenu = 
                new ComponentPortPopupMenu();
            physicalTree.addMouseListener(new PopupTrigger(physicalTree, portPopupMenu));
            break;
        }

        case ComponentTypeModelEvent.NEW_COMPONENT_TYPE_SELECTION: {
            // reset any filter that might be applied
            /*
            _model.setFilteredComponentTypeList(_model.getComponentTypeList());
            CompTypeResultsTableModel tableModel = 
                (CompTypeResultsTableModel)compTypeList.getModel();
            tableModel.fireTableDataChanged();
            compTypeFilterTextField.setText(null);
            */

            List ctList = _model.getFilteredComponentTypeList();
            ComponentType selectedCt = _model.getSelectedComponentType();
            int row = -1;
            boolean found = false;
            if (ctList != null) {
                Iterator ctIt = ctList.iterator();
                while (ctIt.hasNext()) {
                    row++;
                    ComponentType ct = (ComponentType)ctIt.next();
                    if (ct.getComponentTypeName().equals(selectedCt.getComponentTypeName())) {
                        found = true;
                        break;
                    }
                }                
            }

            // weird sequence to get selection changed and visible
            if (found) {
                compTypeList.changeSelection(row,0,false,false);
                Rectangle cell = compTypeList.getCellRect(row,0,true);
                compTypeList.scrollRectToVisible(cell);
            }
            break;
        }

        case ComponentTypeModelEvent.NEW_COMPONENT_TYPE: {
            CompTypeResultsTableModel tableModel = 
                (CompTypeResultsTableModel)compTypeList.getModel();
            // request that comp type table update itself
            tableModel.fireTableDataChanged();

            // get the list index of the newly added component
            ComponentType selectedCt = _model.getSelectedComponentType();
            List ctList = _model.getFilteredComponentTypeList();
            int index = ctList.indexOf(selectedCt);
            if (index != -1) {
                // select the entry we just added
                compTypeList.changeSelection(index,index,false,false);
                Rectangle cell = compTypeList.getCellRect(index,index,true);
                compTypeList.scrollRectToVisible(cell);
            }
            break;
        }
            
        default: {}
        }
            
    }

    /********************************************************************
     * Inner classes supporting Swing components
     ********************************************************************/

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

    /**
     * Component type attribute table model. Displays the myriad of 
     * different scalar info about a component type.
     */
    class CompTypeAttrTableModel extends AbstractTableModel {

        private String[] columnNames = {"Name","Value"};
        private ComponentType compType;

        public CompTypeAttrTableModel() {
            super();
        }

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int fixedSize = 14;
            compType = _model.getSelectedComponentType();
            // will be some fixed minimum size plus number of documents
            int numDocs = 0;
            if (compType != null) 
                numDocs = compType.getComponentTypeDocuments().size();

            return fixedSize + numDocs;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            compType = _model.getSelectedComponentType();
            if (compType != null) {
                ComponentTypeStatus status = compType.getComponentTypeStatus();
                ComponentTypePerson cogPerson = null;
                if (compType.getComponentTypePersons().size() > 0) {
                    // just grab the one entry for now, since we don't yet support multiple
                    Iterator it = compType.getComponentTypePersons().iterator();
                    cogPerson = (ComponentTypePerson)it.next();
                }

                switch (row) {
                case 0: {
                    if (col == 0) 
                        return "Component Type";
                    else
                        return compType.getComponentTypeName();
                }
                case 1: {
                    if (col == 0)
                        return "Description";
                    else
                        return compType.getDescription();
                }
                case 2: {
                    if (col == 0)
                        return "Manufacturer";
                    else {
                        if (compType.getManufacturer() == null)
                            return "none given";
                        else
                            return compType.getManufacturer().getManufacturerName();
                    }
                }
                case 3: {
                    if (col == 0)
                        return "Form Factor";
                    else {
                        if (compType.getFormFactor() == null)
                            return "none given";
                        else
                            return compType.getFormFactor().getFormFactor();
                    }
                }
                case 4: {
                    if (col == 0)
                        return "Spare Quantity";
                    else
                        return Integer.toString(status.getSpareQuantity());
                }
                case 5: {
                    if (col == 0)
                        return "Spare Location";
                    else
                        return status.getSpareLocation();
                }
                case 6: {
                    if (col == 0)
                        return "Stock Quantity";
                    else
                        return Integer.toString(status.getStockQuantity());
                }
                case 7: {
                    if (col == 0)
                        return "Function(s)";
                    else {
                        if (compType.getComponentTypeFunctions() == null) {
                            return "none";
                        } else {
                            Iterator it = compType.getComponentTypeFunctions().iterator();
                            StringBuffer sb = new StringBuffer();
                            while (it.hasNext()) {
                                ComponentTypeFunction ctf = (ComponentTypeFunction)it.next();
                                sb.append(ctf.getFunction().getFunctionName() + ",");
                            }
                            if (sb.length() == 0) {
                                return "none";
                            } else {
                                sb.deleteCharAt(sb.length()-1);
                                return sb.toString();
                            }
                        }
                    }
                }
                case 8: {
                    if (col == 0)
                        return "Cognitive Person";
                    else {
                        if (cogPerson != null) 
                            return cogPerson.getPerson().toString();
                        else
                            return "None";
                    }
                }
                case 9: {
                    if (col == 0)
                        return "Verbose Description";
                    else {
                        if (compType.getVerboseDescription() == null)
                            return "";
                        else
                            return compType.getVerboseDescription();
                    }
                }
                case 10: {
                    if (col == 0)
                        return "Beamline Interest";
                    else {
                        if (compType.getBeamlineInterest() == null)
                            return "Unknown";
                        else
                            return compType.getBeamlineInterest().getInterest();
                    }
                }
                case 11: {
                    if (col == 0)
                        return "CHC Contact";
                    else {
                        if (compType.getChcContact() == null)
                            return "None";
                        else
                            return compType.getChcContact().toString();
                    }
                }
                case 12: {
                    if (col == 0)
                        return "NRTL Status";
                    else {
                        if (status.getNrtlStatus() == null)
                            return "Not Applicable";
                        else {
                            // map to more user-friendly strings
                            String nrtlStatus = status.getNrtlStatus();
                            if (nrtlStatus.equalsIgnoreCase("NA") || nrtlStatus.equalsIgnoreCase("NH")) {
                                return "Not Applicable";
                            } else if (nrtlStatus.equalsIgnoreCase("TBD")) {
                                return "TBD";
                            } else if (nrtlStatus.equalsIgnoreCase("ANL")) {
                                return "ANL Inspection Required";
                            } else if (nrtlStatus.equalsIgnoreCase("NRTL")) {
                                return "NRTL Approved";
                            } else {
                                return "Not Applicable";
                            }
                        }
                    }
                }
                case 13: {
                    if (col == 0)
                        return "NRTL Agency";
                    else {
                        if (status.getNrtlAgency() == null)
                            return " ";
                        else
                            return status.getNrtlAgency();
                    }
                }
                }
                if (row > 13) {
                    if (col == 0)
                        return "Document";
                    else {
                        int docIndex = row - 14;
                        Iterator it = compType.getComponentTypeDocuments().iterator();
                        int index = 0;
                        while (it.hasNext()) {
                            gov.anl.aps.irmis.persistence.component.ComponentTypeDocument ctd = 
                                (gov.anl.aps.irmis.persistence.component.ComponentTypeDocument)it.next();
                            if (index == docIndex)
                                return ctd.getUri().getUri();
                            index++;
                        }
                        return " ";
                    }
                }
                return " ";

            } else {
                return " ";
            }
        }

        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }

    /**
     * Customized table cell renderer used for comp type attribute table.
     * This cell renderer allows for a display line sized to fit text.
     */
    class CompTypeAttrValueTableCellRenderer 
        implements TableCellRenderer {
        
        public Component getTableCellRendererComponent(JTable table, 
                                                       Object value, 
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row, int col) {
            
            JTextArea textArea = new JTextArea();
            // modify properties of text area here
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEnabled(table == null || table.isEnabled());
            
            if (value != null)
                textArea.setText(value.toString());
            
            if (isSelected) {
                textArea.setBackground(new Color(0xCC,0xCC,0xFF));
            } else {
                textArea.setBackground(Color.white);
            }
            if (col == 1 && row > 13)
                textArea.setForeground(Color.blue);
            else
                textArea.setForeground(Color.black);
            
            // autosize the row height to fit text exactly
            textArea.setSize(table.getColumnModel().getColumn(col).getWidth(), Integer.MAX_VALUE);
            int desiredHeight = (int) textArea.getPreferredSize().getHeight();
            if (desiredHeight > table.getRowHeight(row))
                table.setRowHeight(row,desiredHeight);
            
            return textArea;
        }
    }

    /**
     * Provides methods to get comp type required interface table cell data from
     * actual <code>ComponentTypeModel</code>. 
     */
    class RequiredTableModel extends AbstractTableModel {
        private String[] columnNames = {"relationship","interface"};  // ,"max children"
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            if (requiredInterfaceList != null)
                size = requiredInterfaceList.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            ComponentTypeInterface cti = 
                (ComponentTypeInterface)requiredInterfaceList.get(row);
            if (col == 0) {
                return cti.getRelationshipType().getRelationshipType();
            } else if (col == 1) {
                return cti.getInterfaceType().getInterfaceType();
            } else if (col == 2) {
                return "not impl yet";
            } else {
                return " ";
            }
        }
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }

    /**
     * Provides methods to get comp type presented interface table cell data from
     * actual <code>ComponentTypeModel</code>. 
     */
    class PresentedTableModel extends AbstractTableModel {
        private String[] columnNames = {"relationship","interface"}; // ,"max children"
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            if (presentedInterfaceList != null)
                size = presentedInterfaceList.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            ComponentTypeInterface cti = 
                (ComponentTypeInterface)presentedInterfaceList.get(row);
            if (col == 0) {
                return cti.getRelationshipType().getRelationshipType();
            } else if (col == 1) {
                return cti.getInterfaceType().getInterfaceType();
            } else if (col == 2) {
                return "not impl yet";
            } else {
                return " ";
            }
        }
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }

    class PhysicalTreeRenderer extends DefaultTreeCellRenderer {

        public PhysicalTreeRenderer() {
            super();
            Icon icon = new ImageIcon(AppsUtil.getImageURL("leaf.gif"));
            setClosedIcon(icon);
            setOpenIcon(icon);
            icon = new ImageIcon(AppsUtil.getImageURL("pin.gif"));
            setLeafIcon(icon);
        }
        public Component getTreeCellRendererComponent(JTree tree,
                                                      Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row,
                                                      boolean hasFocus) {

            if (leaf)
                setToolTipText("Pin Designator : Pin Usage");
            else
                setToolTipText("Port Name : Port Type : Group");

            super.getTreeCellRendererComponent(tree, value, selected,
                                               expanded, leaf, row, hasFocus);
            return this;
        }
    }

    public class ComponentPortPopupMenu extends JPopupMenu {

        private TreePath selectedTreePath;

        private JMenuItem addPortItem;
        private JMenuItem deletePortItem;
        private JMenuItem editPinsItem;

        public ComponentPortPopupMenu() {
            super();

            addPortItem = new JMenuItem("Add Port...");
            addPortItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ComponentType ct = _model.getSelectedComponentType();
                        JFrame appFrame = 
                            ((DesktopApplication)Application.getApp()).getDesktopFrame();
                        NewTypeWizardModel wizardModel =
                            NewTypeWizard.showWizard(appFrame, ct, 5, null);
                        if (!wizardModel.getCancelled()) 
                            document.actionSaveComponentType(ct);
                    }
                });
            this.add(addPortItem);

            deletePortItem = new JMenuItem("Delete Port");
            deletePortItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        DefaultMutableTreeNode tn = 
                            (DefaultMutableTreeNode)selectedTreePath.getLastPathComponent();
                        ComponentType ct = _model.getSelectedComponentType();
                        ComponentPortTemplate cpt = (ComponentPortTemplate)tn.getUserObject();
                        if (ct.getComponentTypeStatus().getInstantiated()) {
                            Application.displayWarning("Cannot Delete Port","You cannot delete ports after instances have been created based on this component type.");
                        } else {
                            document.actionDeletePortFromComponentType(ct, cpt);
                        }
                    }
                });
            this.add(deletePortItem);

            editPinsItem = new JMenuItem("Edit Pins...");
            editPinsItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        DefaultMutableTreeNode tn = 
                            (DefaultMutableTreeNode)selectedTreePath.getLastPathComponent();
                        ComponentType ct = _model.getSelectedComponentType();
                        ComponentPortTemplate cpt = (ComponentPortTemplate)tn.getUserObject();
                        JFrame appFrame = 
                            ((DesktopApplication)Application.getApp()).getDesktopFrame();
                        NewTypeWizardModel wizardModel =
                            NewTypeWizard.showWizard(appFrame, ct, 7, cpt);
                        if (!wizardModel.getCancelled()) 
                            document.actionSaveComponentType(ct);
                    }
                });
            this.add(editPinsItem);

        }

        public void show(JTree tree, int x, int y, boolean enabled) {

            if (!enabled) {  // not logged in with sufficient permissions
                addPortItem.setEnabled(false);
                deletePortItem.setEnabled(false);
                editPinsItem.setEnabled(false);

            } else {
                DefaultMutableTreeNode leadSelection = 
                    (DefaultMutableTreeNode)selectedTreePath.getLastPathComponent();
                Object uo = leadSelection.getUserObject();
                // enable different menu items depending on which node click is on
                if (uo instanceof String) {   // root "Ports"
                    addPortItem.setEnabled(true);
                    deletePortItem.setEnabled(false);
                    editPinsItem.setEnabled(false);
                } else {
                    addPortItem.setEnabled(false);
                    deletePortItem.setEnabled(true);
                    editPinsItem.setEnabled(true);
                }
            } 
            super.show(tree, x, y);
        }

        public void setSelectedTreePath(TreePath path) {
            selectedTreePath = path;
        }

    }

    public class PopupTrigger extends MouseAdapter {
        private JTree tree;
        private ComponentPortPopupMenu popupMenu;

        public PopupTrigger(JTree tree, ComponentPortPopupMenu popupMenu) {
            super();
            this.tree = tree;
            this.popupMenu = popupMenu;
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                int x = e.getX();
                int y = e.getY();
                TreePath selectedTreePath = tree.getPathForLocation(x,y);
                if (selectedTreePath != null) {
                    tree.setSelectionPath(selectedTreePath);

                    DefaultMutableTreeNode leadSelection = 
                        (DefaultMutableTreeNode)selectedTreePath.getLastPathComponent();
                    popupMenu.setSelectedTreePath(selectedTreePath);

                    // only show popup menu when right clicking on ports (not pins)
                    Object uo = leadSelection.getUserObject();
                    if (!(uo instanceof PortPinTemplate)) {
                        boolean isPermitted =
                            LoginUtil.isPermitted(editPrincipal) ||
                            LoginUtil.isPermitted(editPortPrincipal);
                        popupMenu.show(tree, x, y, isPermitted);
                    }
                }
            }
        }
        
    }
}
