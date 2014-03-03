/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/

package gov.anl.aps.irmis.apps.componenthistory.echo2;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;

// Echo2 
import nextapp.echo2.app.*;
import nextapp.echo2.app.button.*;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.event.WindowPaneListener;
import nextapp.echo2.app.event.WindowPaneEvent;
import nextapp.echo2.app.layout.*;

import nextapp.echo2.app.list.ListSelectionModel;
import nextapp.echo2.app.list.DefaultListSelectionModel;

import nextapp.echo2.app.table.TableModel;
import nextapp.echo2.app.table.AbstractTableModel;
import nextapp.echo2.app.table.DefaultTableCellRenderer;
import nextapp.echo2.app.table.DefaultTableColumnModel;
import nextapp.echo2.app.table.TableColumnModel;

import nextapp.echo2.extras.app.MenuBarPane;
import nextapp.echo2.extras.app.MenuBarPane;
import nextapp.echo2.extras.app.BorderPane;
import nextapp.echo2.extras.app.TabPane;

import nextapp.echo2.extras.app.menu.*;

import nextapp.echo2.webcontainer.ContainerContext;
import nextapp.echo2.webcontainer.command.BrowserRedirectCommand;

// EchoPointNG
import echopointng.ComponentEx;
import echopointng.ButtonEx;
import echopointng.TableEx;
import echopointng.LabelEx;
import echopointng.table.DefaultTableCellRendererEx;
import echopointng.xhtml.XhtmlFragment;
import echopointng.ContainerEx;
import echopointng.TabbedPane;
import echopointng.tabbedpane.DefaultTabModel;
import echopointng.layout.DisplayLayoutData;
import echopointng.layout.TableLayoutDataEx;
import echopointng.able.Scrollable;

// Echo2 support
import gov.anl.aps.irmis.apps.echo2support.MessageBox;
import gov.anl.aps.irmis.apps.echo2support.AppUtils;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstance;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstanceState;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentState;
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.login.RoleName;
import gov.anl.aps.irmis.persistence.DAOStaleObjectStateException;
import gov.anl.aps.irmis.persistence.DAOException;
import gov.anl.aps.irmis.persistence.DAOContext;

// IRMIS service layer
import gov.anl.aps.irmis.service.IRMISException;

// Application Support
import gov.anl.aps.irmis.apps.echo2support.AreYouSureDialog;
import gov.anl.aps.irmis.login.echo2support.LoginUtil;
import gov.anl.aps.irmis.login.echo2support.LoginDialog;
import gov.anl.aps.irmis.login.SimpleCallbackHandler;
import gov.anl.aps.irmis.login.RolePrincipal;

/**
 * IRMIS Component History Application.
 */
public class ComponentHistoryApp extends ApplicationInstance {

    public static RolePrincipal[] editPrincipal = { new RolePrincipal(RoleName.COMPONENT_EDITOR) };

    private TaskQueueHandle taskQueueHandle;
    private ApplicationInstance appInstance;
    private LoginUtil loginUtil;

    // window dressing
    private Window window;
    private ContentPane mainWindowPane;

    // the top level MVC (model, view, controller) objects
    private ComponentHistoryView view;
    private ComponentHistoryController controller;
    private ComponentHistoryModel model;

    // application header with title and login
    private SplitPane topSplitPane;
    private Column mainColumn;
    private Label pleaseWaitLabel;
    private Button loginButton;
    private Label loginLabel;

    // search section
    private Column searchColumn;
    private Row searchMatchRow;
    private RadioButton matchAllRadioButton;
    private RadioButton matchAnyRadioButton;
    private Column searchTermsColumn;
    private Table searchTermsTable;
    private Row searchButtonRow;
    private TextField ctTextField;
    private TextField serialNumberTextField;
    private TextField locationTextField;
    private Row locationSearchRow;
    private Row ctSearchRow;
    ComponentHistoryLocationFindDialog findDialog;

    // component instances section
    private boolean showNoMatches = false;
    private Column instancesColumn;
    private TableEx instancesTable;
    private int ciRow;  // index of selected ci row
    private InstancesTableModel instancesTableModel;
    private ListSelectionModel instancesTableSelectionModel;
    private Row instancesButtonRow;

    // component history section
    private Column historyColumn;
    private TabbedPane historyTabPane;
    private DefaultTabModel tabModel;

    /**
     * Method to get instance of LoginUtil for this application instance.
     */
    public LoginUtil getLoginUtil() {
        return loginUtil;
    }
    
    /**
     * @see nextapp.echo2.app.ApplicationInstance#init()
     */
    public Window init() {

        appInstance = this;
        loginUtil = new LoginUtil();

        // Some useful techniques, not currently used
        /*
        // a way to redirect current browser to a new URL
        enqueueCommand(new BrowserRedirectCommand("/irmis2"));  - add this to IRMIS logo button
        // a way to listen for session termination
        ContainerContext containerContext =
            (ContainerContext)getContextProperty(ContainerContext.CONTEXT_PROPERTY_NAME);
        HttpSession httpSession = containerContext.getSession();
        httpSession.setAttribute("bindings.listener", new MySessionBindingListener());
        */

        // pass all this to create an application controller
        controller = new ComponentHistoryController(appInstance, loginUtil);

        model = controller.getModel();
        model.addComponentHistoryModelListener(new ComponentHistoryModelListener() {
                public void modified(ComponentHistoryModelEvent e) {
                    updateView(e);
                }
            });

        setStyleSheet(Styles.DEFAULT_STYLE_SHEET);
        window = new Window();
        window.setTitle("IRMIS Component History");

        // construct main window
        mainWindowPane = new MainWindowPane();
        window.setContent(mainWindowPane);

        // create some dialogs in advance that need results of actionReload
        findDialog = new ComponentHistoryLocationFindDialog(controller);
        findDialog.addWindowPaneListener(new WindowPaneListener() {
                public void windowPaneClosing(WindowPaneEvent e) {
                    if (model.getSelectedComponent() != null) {
                        String selectionPath = model.getSelectedComponentPath();
                        locationTextField.setText(selectionPath);

                        // by policy, selecting a component nulls out other search params
                        ctTextField.setText(null);
                        serialNumberTextField.setText(null);
                    }
                }
            });


        // Load up the intial set of data needed.
        controller.actionInitializeData();
        return window;
    }

    class MainWindowPane extends ContentPane {
        MainWindowPane() {
            super();

            topSplitPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL, new Extent(50));
            topSplitPane.setStyleName("Default");
            add(topSplitPane);

            // Build application title bar with IRMIS icon
            SplitPane applicationHeaderSplitPane = 
                new SplitPane(SplitPane.ORIENTATION_HORIZONTAL_RIGHT_LEFT, new Extent(170));

            Row applicationTitleRow = new Row();
            Label applicationTitle = new Label("IRMIS Component History");
            applicationTitle.setStyleName("Title.Main");
            Button irmisButton = new Button();
            irmisButton.setStyleName("IRMIS.Button");
            irmisButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        enqueueCommand(new BrowserRedirectCommand("http://ctlappsirmis/irmis2"));
                    }
                });
            irmisButton.setIcon(Styles.IRMIS_IMAGE);
            applicationTitleRow.add(irmisButton);
            applicationTitleRow.add(applicationTitle);

            Column loginColumn = new Column();
            loginColumn.setInsets(new Insets(0, 1));
            Row loginLabelRow = new Row();
            loginLabel = new Label("not logged in");
            loginLabelRow.add(loginLabel);

            Row loginButtonRow = new Row();
            loginButton = new Button("Login");
            loginButton.setStyleName("Login.Button");
            loginButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Button b = (Button)e.getSource();
                        String text = b.getText();
                        if (text.equals("Login")) {
                            LoginDialog loginDialog = new LoginDialog(false, 240, 187);
                            loginDialog.addWindowPaneListener(new WindowPaneListener() {
                                    public void windowPaneClosing(WindowPaneEvent e) {
                                        LoginDialog d = (LoginDialog)e.getSource();
                                        SimpleCallbackHandler ch = d.getCallbackHandler();
                                        controller.login(ch);
                                    }
                                });       
                            mainWindowPane.add(loginDialog);
                            
                        } else {  // logout
                            controller.logout();
                        }
                    }
                });
            loginButtonRow.add(loginButton);

            loginColumn.add(loginLabelRow);
            loginColumn.add(loginButtonRow);

            applicationHeaderSplitPane.add(loginColumn);
            applicationHeaderSplitPane.add(applicationTitleRow);

            topSplitPane.add(applicationHeaderSplitPane);

            view = new ComponentHistoryView(this);
            topSplitPane.add(view);

        }
    }

    /**
     * A <code>Column</code> component which renders GUI for component history application.
     * This is everything below the application header.
     */
    class ComponentHistoryView extends Column {

        ComponentHistoryView(ContentPane mwp) {
            super();

            setCellSpacing(new Extent(2));

            // Build top level search, component instances, and 
            //   component history sections, each with a title.
            searchColumn = new Column();
            searchColumn.setStyleName("Section.Column");
            instancesColumn = new Column();
            instancesColumn.setStyleName("Section.Column");
            historyColumn = new Column();
            historyColumn.setStyleName("Section.Column");
            add(searchColumn);
            add(instancesColumn);
            add(historyColumn);

            // title bar for search section
            Row searchTitleRow = new Row();
            searchTitleRow.setStyleName("Title.Row");
            Label searchTitleLabel = new Label("Search");
            searchTitleLabel.setStyleName("Section.Title");
            searchTitleRow.add(searchTitleLabel);

            // title bar for component instances section
            Row instancesTitleRow = new Row();
            instancesTitleRow.setStyleName("Title.Row");
            Label instancesTitleLabel = new Label("Component Instances");
            instancesTitleLabel.setStyleName("Section.Title");
            instancesTitleRow.add(instancesTitleLabel);

            // title bar for component history section
            Row historyTitleRow = new Row();
            historyTitleRow.setStyleName("Title.Row");
            Label historyTitleLabel = new Label("Component History");
            historyTitleLabel.setStyleName("Section.Title");
            historyTitleRow.add(historyTitleLabel);

            searchColumn.add(searchTitleRow);
            instancesColumn.add(instancesTitleRow);
            historyColumn.add(historyTitleRow);

            // Build search section: match radio button row
            searchMatchRow = new Row();
            searchMatchRow.setStyleName("RadioButton.Row");
            matchAllRadioButton = new RadioButton("match all");
            matchAllRadioButton.setSelected(true);
            matchAnyRadioButton = new RadioButton("match any");
            ButtonGroup matchButtonGroup = new ButtonGroup();
            matchAllRadioButton.setGroup(matchButtonGroup);
            matchAnyRadioButton.setGroup(matchButtonGroup);
            matchAllRadioButton.setEnabled(false);
            matchAnyRadioButton.setEnabled(false);
            searchMatchRow.add(matchAllRadioButton);
            searchMatchRow.add(matchAnyRadioButton);
            //searchColumn.add(searchMatchRow);

            // Build search section: search terms table
            Row searchTermsTableRow = new Row();
            searchTermsTableRow.setStyleName("Default.Row");
            SearchTermsTableModel searchTermsTableModel = new SearchTermsTableModel();
            searchTermsTable = new Table(searchTermsTableModel);
            searchTermsTable.setDefaultRenderer(searchTermsTableModel.getColumnClass(0), 
                                                new SearchTermsTableCellRenderer());
            searchTermsTable.setDefaultRenderer(searchTermsTableModel.getColumnClass(1), 
                                                new SearchTermsTableCellRenderer());
            searchTermsTable.setHeaderVisible(false);
            searchTermsTable.setStyleName("Default.Table");
            searchTermsTableRow.add(searchTermsTable);
            searchColumn.add(searchTermsTableRow);

            // Build search section: search buttons row
            searchButtonRow = new Row();
            searchButtonRow.setStyleName("Button.Row");
            Button searchButton = new Button("Search");
            searchButton.setStyleName("Button.Primary");
            searchButton.setToolTipText("Finds component instances based on search parameters.");
            searchButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        initiateSearch();
                    }
                });
            Button searchClearButton = new Button("Clear");
            searchClearButton.setStyleName("Button.Primary");
            searchClearButton.setToolTipText("Clears out search parameters.");
            searchClearButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ctTextField.setText(null);
                        model.setSelectedComponentType(null);
                        serialNumberTextField.setText(null);
                        model.setSelectedSerialNumber(null);
                        locationTextField.setText(null);
                        model.setSelectedLocation(null);
                        model.setSelectedComponent(null);
                        model.setSelectedComponentPath(null);
                        matchAllRadioButton.setSelected(true);
                        matchAnyRadioButton.setSelected(false);
                        model.setMatchAllOption(true);
                    }
                });
            
            searchButtonRow.add(searchButton);
            searchButtonRow.add(searchClearButton);
            searchColumn.add(searchButtonRow);

            // Build instances section: instances table
            ContainerEx tableContainerEx = new ContainerEx();
            tableContainerEx.setHeight(new Extent(130));
            tableContainerEx.setScrollBarPolicy(Scrollable.AUTO);
            instancesTableModel = new InstancesTableModel();
            instancesTable = new TableEx(instancesTableModel);
            instancesTable.setStyleName("Default.TableEx");
            instancesTable.setDefaultRenderer(new InstancesTableCellRenderer());
            instancesTable.setWidth(new Extent(99, Extent.PERCENT));
            TableColumnModel columnModel = instancesTable.getColumnModel();
            columnModel.getColumn(0).setWidth(new Extent(20,Extent.PERCENT));
            columnModel.getColumn(1).setWidth(new Extent(22,Extent.PERCENT));
            columnModel.getColumn(2).setWidth(new Extent(8,Extent.PERCENT));
            columnModel.getColumn(3).setWidth(new Extent(50,Extent.PERCENT));

            // can't use TableEx scrolling AND have cell text line-wrap - booo!
            //instancesTable.setScrollable(true);
            instancesTableSelectionModel = new DefaultListSelectionModel();
            instancesTableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            instancesTable.setSelectionModel(instancesTableSelectionModel);
            instancesTable.setSelectionEnabled(true);
            instancesTable.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ciRow = instancesTableSelectionModel.getMinSelectedIndex();
                        List ciList = model.getComponentInstanceList();
                        if (ciList != null && ciList.size() > ciRow) {
                            ComponentInstance ci = (ComponentInstance)ciList.get(ciRow);
                            model.setSelectedComponentInstance(ci);
                            controller.actionGetHistory(ci);
                        }
                    }
                });
            tableContainerEx.add(instancesTable);
            instancesColumn.add(tableContainerEx);

            // Build instances section: button row
            instancesButtonRow = new Row();
            instancesButtonRow.setStyleName("Button.Row");
            Button instanceAddButton = new Button("New Instance...");
            instanceAddButton.setStyleName("Button.Primary");
            instanceAddButton.setToolTipText("Creates a new component instance.");
            getLoginUtil().registerProtectedComponent(instanceAddButton, editPrincipal);
            instanceAddButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        WindowPane newInstanceDialog =
                            new ComponentHistoryNewInstanceDialog(controller, ctTextField.getText());

                        newInstanceDialog.addWindowPaneListener(new WindowPaneListener() {
                                public void windowPaneClosing(WindowPaneEvent e) {
                                    if (model.getNewInstanceSerialNumber() != null) {
                                        controller.actionNewInstance();
                                    }
                                }
                            });
                        mainWindowPane.add(newInstanceDialog);
                    }
                });
            Button instanceClearButton = new Button("Clear List");
            instanceClearButton.setStyleName("Button.Primary");
            instanceClearButton.setToolTipText("Clears display of component instances.");
            instanceClearButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        model.setSelectedComponentInstance(null);
                        model.resetInstanceList();
                        model.resetHistoryLists();
                        showNoMatches = false;

                        // fire off 2 events to force update of instance and history tables
                        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.INSTANCE_SEARCH_COMPLETE));
                        
                        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.HISTORY_SEARCH_COMPLETE));                        

                    }
                });
            Button instanceEditButton = new Button("Edit Serial Number...");
            instanceEditButton.setStyleName("Button.Primary");
            instanceEditButton.setToolTipText("Edit component instance serial number.");
            getLoginUtil().registerProtectedComponent(instanceEditButton, editPrincipal);
            instanceEditButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ComponentInstance ci = model.getSelectedComponentInstance();
                        if (ci != null) {
                            WindowPane editSerialDialog =
                                new ComponentHistoryEditSerialDialog(controller);
                            
                            editSerialDialog.addWindowPaneListener(new WindowPaneListener() {
                                    public void windowPaneClosing(WindowPaneEvent e) {
                                        String sn = model.getNewInstanceSerialNumber();
                                        if (sn != null) {
                                            model.getSelectedComponentInstance().setSerialNumber(sn);
                                            controller.actionSaveInstance();
                                        }
                                    }
                                });
                            mainWindowPane.add(editSerialDialog);                        
                        }
                    }
                });

            Button removeButton = new Button("Remove Instance");
            removeButton.setStyleName("Button.Primary");
            removeButton.setToolTipText("Remove this component instance from database");
            getLoginUtil().registerProtectedComponent(removeButton, editPrincipal);
            removeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ComponentInstance ci = model.getSelectedComponentInstance();
                        if (ci != null) {
                            final AreYouSureDialog areYouSureDialog = new AreYouSureDialog();
                            
                            areYouSureDialog.addWindowPaneListener(new WindowPaneListener() {
                                    public void windowPaneClosing(WindowPaneEvent e) {
                                        boolean isYes = areYouSureDialog.isYesResponse();
                                        if (isYes)
                                            controller.actionRemoveInstance();
                                    }
                                });
                            mainWindowPane.add(areYouSureDialog);                        
                        }
                    }
                });


            instancesButtonRow.add(instanceAddButton);
            instancesButtonRow.add(instanceEditButton);
            instancesButtonRow.add(removeButton);
            instancesButtonRow.add(instanceClearButton);
            instancesColumn.add(instancesButtonRow);
            
            // Build history section: tabbed
            Label foo1 = new Label("This tab not yet developed.");
            Column locationHistoryTab = new ComponentHistoryLocationTab(mwp, controller, loginUtil);
            Column operationHistoryTab = new ComponentHistoryOperationTab(mwp, controller, loginUtil);
            Column calibrationHistoryTab = new ComponentHistoryCalibrationTab(mwp, controller, loginUtil);
            Column nrtlHistoryTab = new ComponentHistoryNRTLTab(mwp, controller, loginUtil);
            tabModel = new DefaultTabModel();
            tabModel.addTab("Location",locationHistoryTab);
            tabModel.addTab("Component Status",operationHistoryTab);
            tabModel.addTab("Periodic Maintenance",calibrationHistoryTab);
            tabModel.addTab("NRTL",nrtlHistoryTab);
            historyTabPane = new TabbedPane(tabModel);
            historyTabPane.setStyleName("Default.TabbedPane");
            historyColumn.add(historyTabPane);
        }
        
    }

    /**
     * Collect all the search terms from the widgets, prepare the model with that data,
     * and then kick off controller.actionSearchForComponentInstances().
     */
    private void initiateSearch() {
        model.setSelectedComponentInstance(null);
        showNoMatches = false;
        
        // set up component type search term in model
        String ctName = ctTextField.getText();
        if (ctName.length() > 0)
            model.setSelectedComponentTypeName(ctName);
        else
            model.setSelectedComponentTypeName(null);
        
        ComponentType ct = model.findComponentType(ctName);
        // note: this may be null if just a substring of type entered
        model.setSelectedComponentType(ct); 
        
        // set up serial number search term in model
        String sn = serialNumberTextField.getText();
        if (sn.length() == 0)
            model.setSelectedSerialNumber(null);
        else 
            model.setSelectedSerialNumber(sn);
        
        // set up location search term in model
        String location = locationTextField.getText();
        String selectedComponentLocation = model.getSelectedComponentPath();
        
        if (location.length() == 0) {
            model.setSelectedLocation(null);
            
        } else {
            model.setSelectedLocation(location);
            
            /* This test here checks to see if user has selected a
               component from housing tree, but then later edited
               the location text field. In that case, assume they
               are no longer searching for that component.
            */
            if (model.getSelectedComponent() != null &&
                !location.equals(selectedComponentLocation)) {
                model.setSelectedComponent(null);
                model.setSelectedComponentPath(null);
            }
        }
        
        // set up match all/any flag
        model.setMatchAllOption(matchAllRadioButton.isSelected());
        
        controller.actionSearchForComponentInstances();
    }
    
    /**
     * Update graphical view of model data based on event type.
     *
     * @param event type of data model change
     */
    public void updateView(ComponentHistoryModelEvent event) {

        // check first for any exception
        IRMISException ie = model.getIRMISException();
        if (ie != null) {  // uh-oh: probably lost db connection - pop up message to user
            MessageBox warningMessage = 
                new MessageBox("IRMIS Service Error","Exiting due to exception, application will restart: "+ie);
            warningMessage.addWindowPaneListener(new WindowPaneListener() {
                    public void windowPaneClosing(WindowPaneEvent e) {
                        // cause session invalidation and restart of app
                        AppUtils.exitAndKillSession(appInstance,"/component_history/restart.html");
                    }
                });
            mainWindowPane.add(warningMessage);
            return;
        }

        switch(event.getType()) {
            
        case ComponentHistoryModelEvent.INSTANCE_SEARCH_COMPLETE: {
            List ciList = model.getComponentInstanceList();
            if (ciList != null && ciList.size() > 0)
                showNoMatches = false;
            else
                showNoMatches = true;

            InstancesTableModel tableModel =
                (InstancesTableModel)instancesTable.getModel();
            tableModel.fireTableDataChanged();

            // kick off history search for first instance in table
            if (ciList != null && ciList.size() > 0) {
                instancesTableSelectionModel.setSelectedIndex(0, true);
                ComponentInstance ci = (ComponentInstance)ciList.get(0);
                model.setSelectedComponentInstance(ci);
                controller.actionGetHistory(ci);

            } else { // clear out history if no instances found
                model.resetHistoryLists();
                model.setSelectedComponentInstance(null);
            }
            break;
        }

        case ComponentHistoryModelEvent.HISTORY_SEARCH_COMPLETE: {
            // Highlight tabs that have results with blue title
            List locationHistory = model.getLocationHistoryList();
            ButtonEx b = (ButtonEx)tabModel.getTabAt(historyTabPane, 0, false);
            if (locationHistory != null && locationHistory.size() > 0)
                b.setForeground(Color.BLUE);
            else 
                b.setForeground(Color.BLACK);

            List operationHistory = model.getOperationHistoryList();
            b = (ButtonEx)tabModel.getTabAt(historyTabPane, 1, false);
            if (operationHistory != null && operationHistory.size() > 0)
                b.setForeground(Color.BLUE);
            else 
                b.setForeground(Color.BLACK);

            List calibrationHistory = model.getCalibrationHistoryList();
            b = (ButtonEx)tabModel.getTabAt(historyTabPane, 2, false);
            if (calibrationHistory != null && calibrationHistory.size() > 0)
                b.setForeground(Color.BLUE);
            else 
                b.setForeground(Color.BLACK);

            List nrtlHistory = model.getNRTLHistoryList();
            b = (ButtonEx)tabModel.getTabAt(historyTabPane, 3, false);
            if (nrtlHistory != null && nrtlHistory.size() > 0)
                b.setForeground(Color.BLUE);
            else 
                b.setForeground(Color.BLACK);

            break;
        }

        case ComponentHistoryModelEvent.LOGIN_COMPLETE: {
            // Handle GUI updates after login/logout is complete
            String username = getLoginUtil().getUsername();

            if (username != null && username.length() > 0) {  // just logged in
                loginLabel.setText("logged in as "+username);
                loginButton.setText("Logout");

            } else {  // just logged out
                loginLabel.setText("not logged in");
                loginButton.setText("Login");                
            }

            getLoginUtil().updateProtectedComponents();
            break;
        }

        case ComponentHistoryModelEvent.NEW_LOCATION_HISTORY: {
            // redraw component instance table, since current location probably changed
            InstancesTableModel tableModel =
                (InstancesTableModel)instancesTable.getModel();
            tableModel.fireTableDataChanged();
            
            // scroll to selection
            // can't do this, apparently -boooo!

            // highlight tab
            List locationHistory = model.getLocationHistoryList();
            ButtonEx b = (ButtonEx)tabModel.getTabAt(historyTabPane, 0, false);
            if (locationHistory != null && locationHistory.size() > 0)
                b.setForeground(Color.BLUE);
            break;
        }

        case ComponentHistoryModelEvent.NEW_OPERATION_HISTORY: {
            // highlight tab
            List operationHistory = model.getOperationHistoryList();
            ButtonEx b = (ButtonEx)tabModel.getTabAt(historyTabPane, 1, false);
            if (operationHistory != null && operationHistory.size() > 0)
                b.setForeground(Color.BLUE);
            break;
        }

        case ComponentHistoryModelEvent.NEW_CALIBRATION_HISTORY: {
            // highlight tab
            List calibrationHistory = model.getCalibrationHistoryList();
            ButtonEx b = (ButtonEx)tabModel.getTabAt(historyTabPane, 2, false);
            if (calibrationHistory != null && calibrationHistory.size() > 0)
                b.setForeground(Color.BLUE);
            break;
        }

        case ComponentHistoryModelEvent.NEW_NRTL_HISTORY: {
            // highlight tab
            List nrtlHistory = model.getNRTLHistoryList();
            ButtonEx b = (ButtonEx)tabModel.getTabAt(historyTabPane, 3, false);
            if (nrtlHistory != null && nrtlHistory.size() > 0)
                b.setForeground(Color.BLUE);
            break;
        }

        default: {}
        }
            
    }

    class SearchTermsTableModel extends AbstractTableModel {
        private String[] columnNames = {"Type","Entry"};

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 3;
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int col, int row) {
            if (col == 0) {
                switch (row) {
                case 0: {
                    return "Component Type:";
                }
                case 1: {
                    return "Serial Number/Identifier:";
                }
                case 2: {
                    return "Current Location:";
                }
                default: {
                    return "unknown";
                }
                }
                
            } else {  // column == 1

                switch (row) {
                case 0: {
                    ComponentType ct = model.getSelectedComponentType();
                    if (ct == null)
                        return "";
                    else
                        return ct.getComponentTypeName();
                }
                case 1: {
                    String selectedSerialNumber = model.getSelectedSerialNumber();
                    if (selectedSerialNumber == null)
                        return "";
                    else
                        return selectedSerialNumber;
                }
                case 2: {
                    String selectedLocation = model.getSelectedLocation();
                    if (selectedLocation == null)
                        return "";
                    else
                        return selectedLocation;
                }
                default: {
                    return "unknown";
                }
                }
                
            }

        }

        public Class getColumnClass(int c) {
            Object value = getValueAt(c,0);
            if (value != null) {
                return value.getClass();
            } else {
                return null;
            }
        }
    }

    /**
     * Customized table cell renderer used for search terms table.
     */
    class SearchTermsTableCellRenderer extends DefaultTableCellRenderer {
        public nextapp.echo2.app.Component getTableCellRendererComponent (Table table,
                                                                          Object value,
                                                                          int column, int row) {

            nextapp.echo2.app.Component c = null;
            if (column == 0) {
                switch (row) {
                case 0: {
                    c = super.getTableCellRendererComponent(table, value, column, row);
                    break;
                }
                case 1: {
                    c = super.getTableCellRendererComponent(table, value, column, row);
                    break;
                }
                case 2: {
                    c = super.getTableCellRendererComponent(table, value, column, row);
                    break;
                }
                default: {
                }
                }

            } else {  // column == 1

                switch (row) {
                case 0: {
                    if (ctSearchRow == null) {
                        ctSearchRow = new Row();
                        ctTextField = new TextField();
                        ctTextField.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    initiateSearch();
                                }
                            });
                        ctTextField.setWidth(new Extent(340));
                        ctSearchRow.add(ctTextField);
                        Button ctSearchButton = new Button();
                        ctSearchButton.setStyleName("Button.Mini");
                        ctSearchButton.setToolTipText("Find a component type.");
                        ctSearchButton.setIcon(Styles.MAGNIFIER);
                        ctSearchRow.add(ctSearchButton);
                        ctSearchButton.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    ComponentTypeFindDialog ctDialog = 
                                        new ComponentTypeFindDialog(controller);
                                    ctDialog.addWindowPaneListener(new WindowPaneListener() {
                                            public void windowPaneClosing(WindowPaneEvent e) {
                                                ComponentType ct = model.getSelectedComponentType();
                                                if (ct != null) {
                                                    ctTextField.setText(ct.getComponentTypeName());
                                                }
                                            }
                                        });
                                    mainWindowPane.add(ctDialog);
                                }
                            });
                    }
                    ctTextField.setText((String)value);
                    c = ctSearchRow;
                    break;
                }
                case 1: {
                    if (serialNumberTextField == null) {
                        serialNumberTextField = new TextField();
                        serialNumberTextField.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    initiateSearch();
                                }
                            });
                        serialNumberTextField.setWidth(new Extent(340));
                    }
                    serialNumberTextField.setText((String)value);
                    c = serialNumberTextField;
                    break;
                }
                case 2: {
                    if (locationSearchRow == null) {
                        locationSearchRow = new Row();
                        locationTextField = new TextField();
                        locationTextField.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    initiateSearch();
                                }
                            });
                        locationTextField.setWidth(new Extent(340));
                        locationSearchRow.add(locationTextField);
                        Button locationSearchButton = new Button();
                        locationSearchButton.setStyleName("Button.Mini");
                        locationSearchButton.setToolTipText("Find installed component using housing tree.");
                        locationSearchButton.setIcon(Styles.MAGNIFIER);
                        locationSearchRow.add(locationSearchButton);
                        locationSearchButton.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    mainWindowPane.add(findDialog);
                                }
                            });
                                                               }
                    locationTextField.setText((String)value);
                    c = locationSearchRow;
                    break;
                }
                default: {

                }
                }

            }
            return c;
        }
    }

    class InstancesTableModel extends AbstractTableModel {
        private String[] columnNames = {"Serial#/Identifier","Component Type", "Installed","Current Location"};

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            List cil = model.getComponentInstanceList();
            if (cil != null)
                size = cil.size();
            if (showNoMatches)
                size = 1;
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int col, int row) {
            if (showNoMatches) {
                if (col == 0)
                    return "<no matches>";
                else
                    return "<>";
            }

            List cil = model.getComponentInstanceList();
            if (cil != null && cil.size() > 0) {
                ComponentInstance ci = (ComponentInstance)cil.get(row);
                String currentLocation = ci.getCurrentLocation();
                switch (col) {
                case 0: {
                    String sn = ci.getSerialNumber();
                    if (sn != null && sn.length() > 0)
                        return sn;
                    else
                        return "-none-";
                }
                case 1: {
                    return ci.getComponentType().getComponentTypeName();                    
                }
                case 2: {
                    if (ci.getComponent() != null)
                        return "yes";
                    else
                        return "no";
                }
                case 3: {
                    if (currentLocation != null && currentLocation.length() > 0)
                        return currentLocation;
                    else
                        return "unknown";
                }
                default: {
                    return "unknown";
                }
                }
            }
            return "unknown";
        }

        public Class getColumnClass(int c) {
            Object value = getValueAt(c,0);
            if (value != null) {
                return value.getClass();
            } else {
                return null;
            }
        }
    }

    /**
     * Customized table cell renderer.
     */
    class InstancesTableCellRenderer extends DefaultTableCellRenderer {

        public nextapp.echo2.app.Component getTableCellRendererComponent (Table table,
                                                                          Object value,
                                                                          int column, int row) {

            Label c = new Label((String)value);
            c.setStyleName("Default.TableEx.Label");
            if (row == 0 && showNoMatches) {
                c.setForeground(Color.RED);
            }
            return c;
        }

    }


    /**
     * When an object of this class (one that implements HttpSessionBindingListener)
     * is added to the HttpSession, it will get notified whenever the session
     * terminates. The valueUnbound is called when session goes away. Great way
     * to do any cleanup in application if http session expires.
     */
    class MySessionBindingListener implements HttpSessionBindingListener {

        
        public MySessionBindingListener() {
        }

        public void valueBound(HttpSessionBindingEvent e) {
            
        }
        public void valueUnbound(HttpSessionBindingEvent e) {
            System.err.println("HTTP session closed - do your cleanup here");
        }

    }

}

