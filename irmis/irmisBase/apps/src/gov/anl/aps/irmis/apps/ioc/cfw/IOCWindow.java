/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.ioc.cfw;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Set;
import java.util.EventObject;
import java.util.logging.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import gov.sns.application.*;

// persistence layer
import gov.anl.aps.irmis.persistence.pv.IOC;
import gov.anl.aps.irmis.persistence.pv.IOCStatus;
import gov.anl.aps.irmis.persistence.login.RoleName;
import gov.anl.aps.irmis.persistence.DAOStaleObjectStateException;

// for registering protected gui components
import gov.anl.aps.irmis.login.LoginUtil;
import gov.anl.aps.irmis.login.RolePrincipal;

import gov.anl.aps.irmis.apps.ioc.cfw.plugins.IOCExtendedInfoPlugin;
import gov.anl.aps.irmis.apps.ioc.cfw.plugins.APSIOCInfo;

// service layer
import gov.anl.aps.irmis.service.IRMISException;

// application helpers
import gov.anl.aps.irmis.apps.irmis.cfw.Main;

/**
 * Primary GUI for IRMIS IOC application. All window layout, and Swing event
 * listeners are done here. Business logic work is delegated to <code>IOCDocument</code>,
 * which in turn requests that the IOCModel notify us here of changes to the data. In short,
 * we listen for <code>IOCModelEvent</code> here.
 */
public class IOCWindow extends XalInternalWindow {

	/** The main model for the document */
	final protected IOCModel _model;

    final protected IOCDocument document;

    static RolePrincipal[] editPrincipal = { new RolePrincipal(RoleName.IOC_EDITOR) };

	// Swing GUI components
    private JPanel topPanel;
    private JPanel iocsPanel;
    private JPanel iocListPanel;
    private JPanel selectedIocPanel; // create this via plug-in 

    private JSplitPane lrSplitPane;

    private JTable iocList;
    private JTable attrList;

    private JTextField iocFilterTextField;
    private JComboBox iocSystemCb;
    private JButton iocEditButton;
    private JButton iocAddButton;

	/** 
	 * Creates a new instance of ComponentWindow
	 * @param aDocument The document for this window
	 */
    public IOCWindow(final XalInternalDocument aDocument) {
        super(aDocument);
        document = (IOCDocument)aDocument;

		_model = document.getModel();

        // make IOCWindow a listener for changes in IOCModel
		_model.addIOCModelListener( new IOCModelListener() {
                public void modified(IOCModelEvent e) {
                    updateView(e);
                }
            });

        // initial application window size
        setSize(450, 255);

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


        iocsPanel = new JPanel();
        iocsPanel.setLayout(new BoxLayout(iocsPanel,BoxLayout.PAGE_AXIS));

        // ioc list panel
        iocListPanel = new JPanel();
        iocListPanel.setLayout(new BoxLayout(iocListPanel,BoxLayout.PAGE_AXIS));
        TitledBorder iocListTitle = BorderFactory.createTitledBorder("IOCs");
        iocListPanel.setBorder(iocListTitle);        

        // ioc name filter
        iocFilterTextField = new JTextField(10);
        iocFilterTextField.setToolTipText("Enter a wildcard string to filter ioc list.");
        iocFilterTextField.setPreferredSize(new Dimension(350,20));
        iocFilterTextField.setMaximumSize(new Dimension(350,20));
        iocListPanel.add(iocFilterTextField);
        iocFilterTextField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    String filterText = iocFilterTextField.getText();
                    // change any * to reg-ex pattern
                    filterText = filterText.replaceAll("\\*",".*");
                    Pattern filterRegEx = Pattern.compile("^.*"+filterText+".*$");
                    
                    // apply wildcard name filter
                    Iterator iocIt = _model.getIocList().iterator();
                    List nameFilteredIocList = new ArrayList();
                    while (iocIt.hasNext()) {
                        IOC ioc = (IOC)iocIt.next();
                        Matcher matcher = filterRegEx.matcher(ioc.getIocName());
                        if (matcher.matches())
                            nameFilteredIocList.add(ioc);
                    }
                    _model.setNameFilteredIocList(nameFilteredIocList);
                    
                    String iocSystemSelection = (String)iocSystemCb.getSelectedItem();
                    // apply system filter if that is picked too
                    if (!iocSystemSelection.equals("All")) {
                        iocIt = nameFilteredIocList.iterator();
                        List filteredIocList = new ArrayList();
                        while (iocIt.hasNext()) {
                            IOC ioc = (IOC)iocIt.next();
                            if (ioc.getSystem().equals(iocSystemSelection))
                                filteredIocList.add(ioc);
                        }
                        _model.setFilteredIocList(filteredIocList);
                    } else {
                        _model.setFilteredIocList(nameFilteredIocList);
                    }
                    
                    IOCResultsTableModel tableModel = 
                        (IOCResultsTableModel)iocList.getModel();
                    // request that ioc results table update itself
                    tableModel.fireTableDataChanged();
                }
            });

        // ioc system filter
        iocSystemCb = new JComboBox();
        iocSystemCb.setEditable(false);
        iocSystemCb.addItem("All");
        iocSystemCb.setSelectedItem("All");
        iocSystemCb.setToolTipText("Filter ioc list by system.");
        iocSystemCb.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        String filterText = iocFilterTextField.getText();
                        String selectedSystem = (String)e.getItem();

                        // apply system filter
                        List iocs = _model.getIocList();
                        if (iocs != null) {
                            Iterator iocIt = iocs.iterator();
                            List systemFilteredIocList = new ArrayList();
                            while (iocIt.hasNext()) {
                                IOC ioc = (IOC)iocIt.next();
                                if (selectedSystem.equals("All") ||
                                    ioc.getSystem().equals(selectedSystem))
                                    systemFilteredIocList.add(ioc);
                            }
                            _model.setSystemFilteredIocList(systemFilteredIocList);
                            
                            // apply wildcard name filter (if not empty)
                            if (filterText != null && filterText.length() > 0) {
                                // change any * to reg-ex pattern
                                filterText = filterText.replaceAll("\\*",".*");
                                Pattern filterRegEx = Pattern.compile("^.*"+filterText+".*$");
                                iocIt = systemFilteredIocList.iterator();
                                List filteredIocList = new ArrayList();
                                while (iocIt.hasNext()) {
                                    IOC ioc = (IOC)iocIt.next();
                                    Matcher matcher = filterRegEx.matcher(ioc.getIocName());
                                    if (matcher.matches())
                                        filteredIocList.add(ioc);
                                }
                                _model.setFilteredIocList(filteredIocList);
                            } else {
                                _model.setFilteredIocList(systemFilteredIocList);
                            }
                        }
                            
                        IOCResultsTableModel tableModel = 
                            (IOCResultsTableModel)iocList.getModel();
                        // request that ioc results table update itself
                        tableModel.fireTableDataChanged();
                    }
                }
            });           
        iocListPanel.add(iocSystemCb);

        // ioc list table
        iocList = new JTable(new IOCResultsTableModel());
        iocList.setShowHorizontalLines(true);
        iocList.setColumnSelectionAllowed(false);
        iocList.setRowSelectionAllowed(true);
        iocList.getColumnModel().getColumn(0).setCellRenderer(new IOCTableCellRenderer());
    	JScrollPane iocListScroller = new JScrollPane(iocList, vsbp, hsbp);
        iocListPanel.add(iocListScroller);
        iocList.getColumnModel().getColumn(0).setPreferredWidth(147);
        iocList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // handle row selection in ioc list table
        iocList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int selectedIOCResultsRow = lsm.getMinSelectionIndex();
                        List iocList;
                        iocList = _model.getFilteredIocList();
                        IOC selectedIoc = (IOC)iocList.get(selectedIOCResultsRow);
                        _model.setSelectedIoc(selectedIoc);

                        // tell plugin to show info for selected ioc
                        _model.getIOCPlugin().selectIOC(selectedIoc);
                    }
                }
            });

        // ioc action button bar
        JPanel iocButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        iocEditButton = new JButton("Edit...");
        LoginUtil.registerProtectedComponent(iocEditButton, editPrincipal);
        iocEditButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFrame appFrame = 
                        ((DesktopApplication)Application.getApp()).getDesktopFrame();
                    IOC ioc = 
                        IOCEditDialog.showDialog(appFrame,null,_model.getSelectedIoc(),
                                                 _model.getStatusList(), false);
                    document.actionSaveIoc(ioc,true);
                }
            });
        
        iocAddButton = new JButton("Add...");
        LoginUtil.registerProtectedComponent(iocAddButton, editPrincipal);
        iocAddButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFrame appFrame = 
                        ((DesktopApplication)Application.getApp()).getDesktopFrame();
                    IOC ioc = 
                        IOCEditDialog.showDialog(appFrame,null,null,_model.getStatusList(),true);
                    iocSystemCb.setSelectedItem("All");
                    iocFilterTextField.setText(null);
                    document.actionSaveIoc(ioc,true);
                }
            });

        // put back in after pv viewer release
        iocButtons.add(iocEditButton);
        iocButtons.add(iocAddButton);

        // add ioc list and button panels to iocsPanel
        iocsPanel.add(iocListPanel);
        iocsPanel.add(iocButtons);

        // make the panel used to display data after ioc is selected
        selectedIocPanel = makeSelectedIocPanelContents();

        // split between iocsPanel and ioc details info on right
        lrSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,iocsPanel,selectedIocPanel);
        lrSplitPane.setDividerLocation(150);

        // add split panes to topPanel
        topPanel.add(lrSplitPane,c);

    	getContentPane().add(topPanel);
    }

    private JPanel makeSelectedIocPanelContents() {

        // install placeholder for the moment, to be replaced
        // by plugin after it is constructed by document.
        return new JPanel();
    }
        
    /**
     * Update graphical view of IOCModel data based on event type.
     *
     * @param event type of data model change
     */
    public void updateView(IOCModelEvent event) {

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
            
        case IOCModelEvent.NEW_IOC_LIST: {
            // get plugin and create new right-hand component for extended info
            IOCExtendedInfoPlugin plugin = _model.getIOCPlugin();
            java.awt.Component pluginComponent = 
                plugin.createIOCInfoPanel(_model.getIocList());
            lrSplitPane.setRightComponent(pluginComponent);
            lrSplitPane.setDividerLocation(150);

            // populate the systems pulldown, now that we have that info
            iocSystemCb.removeAllItems();
            iocSystemCb.addItem("All");
            iocSystemCb.setSelectedItem("All");
            if (_model.getSystems() != null) {
                Iterator systemIt = _model.getSystems().iterator();
                while (systemIt.hasNext()) {
                    String systemName = (String)systemIt.next();
                    iocSystemCb.addItem(systemName);
                }
            }

            plugin.selectIOC(null);

            // request that ioc results table update itself
            IOCResultsTableModel tableModel = (IOCResultsTableModel)iocList.getModel();
            tableModel.fireTableDataChanged();

            // temporary
            // loop over ioc list, doing one save on each. this will
            // connect existing ioc's to existing components.
            /*
            Iterator iocIt = _model.getIocList().iterator();
            while (iocIt.hasNext()) {
                IOC ioc = (IOC)iocIt.next();
                document.actionSaveIoc(ioc, false);
            }
            */
            break;
        }

        case IOCModelEvent.NEW_IOC_SELECTION: {
            List iocs = _model.getIocList();
            IOC selectedIoc = _model.getSelectedIoc();
            int row = -1;
            boolean found = false;
            if (iocs != null) {
                Iterator iocIt = iocs.iterator();
                while (iocIt.hasNext()) {
                    row++;
                    IOC ioc = (IOC)iocIt.next();
                    if (ioc.getIocName().equals(selectedIoc.getIocName())) {
                        found = true;
                        break;
                    }
                }                
            }

            // weird sequence to get selection changed and visible
            if (found) {
                iocList.changeSelection(row,0,false,false);
                Rectangle cell = iocList.getCellRect(row,0,true);
                iocList.scrollRectToVisible(cell);
            }
            break;
        }

        case IOCModelEvent.IOC_SAVED: {
            IOCResultsTableModel tableModel = (IOCResultsTableModel)iocList.getModel();
            // request that ioc results table update itself
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
     * Provides methods to get ioc results table cell data from
     * actual <code>IOCModel</code>. 
     */
    class IOCResultsTableModel extends AbstractTableModel {
        private String[] columnNames = {"IOC Name"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            if (_model.getFilteredIocList() == null)
                return size;
            size = _model.getFilteredIocList().size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            List iocList = null;

            iocList = _model.getFilteredIocList();

            if (iocList != null) {            
                IOC ioc = (IOC)iocList.get(row);
                return ioc.getIocName();
            } else {
                return null;
            }
        }
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }

    
    /**
     * Customized table cell renderer used for ioc table. It colors
     * the cell background and text foreground based on whether ioc is active
     * or not.
     */
    class IOCTableCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent (JTable table,
                                                        Object value,
                                                        boolean selected,
                                                        boolean focused,
                                                        int row, int column) {
            setEnabled(table==null || table.isEnabled());
            if (column == 0) {
                List iocList = _model.getFilteredIocList();
                if (iocList != null) {
                    IOC ioc = (IOC)iocList.get(row);
                    if (ioc.getStatus().getId() == IOCStatus.INACTIVE) {
                        setBackground(Color.pink);
                        setToolTipText("inactive");
                    } else {
                        if (!selected)
                            setBackground(Color.white);
                        else
                            setBackground(new Color(0xCC,0xCC,0xFF));
                        setToolTipText(null);
                    }
                    setForeground(Color.blue);
                }
            }
            super.getTableCellRendererComponent(table,value,selected,focused,row,column);
            return this;
        }
    }


    /**
     * Extended JText area class that remembers its location within a
     * parent JTable.
     */
    class EmbeddedJTextArea extends JTextArea {

        int row;
        int col;
        JTable table;
        public void setParentTableInfo(JTable table, int row, int col) {
            this.table = table;
            this.row = row;
            this.col = col;
        }
        public JTable getTable() {
            return this.table;
        }
        public int getRow() {
            return this.row;
        }
        public int getColumn() {
            return this.col;
        }
    }
}
