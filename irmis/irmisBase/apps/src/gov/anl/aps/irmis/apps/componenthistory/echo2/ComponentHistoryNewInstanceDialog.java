/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/

package gov.anl.aps.irmis.apps.componenthistory.echo2;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;
import java.text.DateFormat;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

// Echo2 
import nextapp.echo2.app.*;
import nextapp.echo2.app.button.*;
import nextapp.echo2.app.list.AbstractListModel;
import nextapp.echo2.app.list.ListModel;
import nextapp.echo2.app.list.ListSelectionModel;
import nextapp.echo2.app.list.DefaultListSelectionModel;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.layout.*;
import nextapp.echo2.app.table.TableModel;
import nextapp.echo2.app.table.AbstractTableModel;
import nextapp.echo2.app.table.DefaultTableCellRenderer;
import nextapp.echo2.app.table.DefaultTableColumnModel;
import nextapp.echo2.app.table.TableColumnModel;

// EchoPointNG
//import echopointng.AutoLookupTextFieldEx;
import echopointng.TableEx;
import echopointng.ContainerEx;
import echopointng.Tree;
import echopointng.SelectFieldEx;
import echopointng.tree.TreeSelectionModel;
import echopointng.tree.DefaultTreeSelectionModel;
import echopointng.tree.TreeSelectionListener;
import echopointng.tree.TreeSelectionEvent;
import echopointng.tree.TreePath;
import echopointng.tree.DefaultTreeCellRenderer;
import echopointng.tree.TreeModelEvent;
import echopointng.layout.DisplayLayoutData;
import echopointng.able.Scrollable;
import echopointng.DateChooser;

// Echo2 support
import gov.anl.aps.irmis.apps.echo2support.DialogBox;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.component.Manufacturer;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstance;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstanceState;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentState;
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.DAOStaleObjectStateException;
import gov.anl.aps.irmis.persistence.DAOException;

// IRMIS service layer
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Dialog box for creating a new component instance.
 */
public class ComponentHistoryNewInstanceDialog extends DialogBox {

    static final String title = "New Component Instance";
    static final int width = 400;
    static final int locationErrorHeightOffset = 18;
    static final int locationHeight = 633;
    static final Extent locationHeightExtent = new Extent(locationHeight);
    static final int treeHeightOffset = -107;
    static final int treeErrorHeightOffset = 18;
    static final int eventDateHeightOffset = 185;
    static final Extent fieldWidth = new Extent(250);

    private Column userColumn;

    private ComponentHistoryController controller;
    private ComponentHistoryModel model;
    private RadioButton yesRadioButton;
    private ComponentType selectedComponentType;
    private Label componentTypeErrorLabel;
    private TableEx typeTable;
    private Label descLabel;
    private ComponentTypeTableModel typeTableModel;
    private ListSelectionModel typeTableSelectionModel;
    private TextField compTypeFilterTextField;
    private Column locationColumn;
    private Label locationLabel;
    private TextField locationTextField;
    private TextField snTextField;
    private Label stateSelectLabel;
    private SelectField stateSelectField;
    private Column treeColumn;

    private Label treeErrorLabel;
    private ContainerEx treeContainer;
    private Tree housingTree;
    private boolean hasInstance;
    private TreeSelectionModel treeSelectionModel;
    private FilteredHousingComponentTreeModel housingTreeModel;
    private SelectFieldEx mfgSelect;

    private final DateFormat sdf = new SimpleDateFormat("MM/dd/yy");        
    private DateChooser eventDateChooser;
    private Date eventDate;
    private Label eventDateLabel;

    private Manufacturer selectedMfg;
    private Component selectedComponent;

    public ComponentHistoryNewInstanceDialog(ComponentHistoryController ctrl,
                                             String componentTypeText) {
        super(title, width, locationHeightExtent.getValue());

        controller = ctrl;
        model = controller.getModel();
        
        userColumn = getUserColumn();
        userColumn.setCellSpacing(new Extent(5));
        userColumn.setInsets(new Insets(5));

        // prompt for date of event
        eventDate = new Date();
        String nowString = sdf.format(eventDate);
        Label eventLabel = new Label("Date of entry:");
        userColumn.add(eventLabel);
        Row eventDateRow = new Row();
        eventDateRow.setCellSpacing(new Extent(8));
        eventDateLabel = new Label(nowString);
        eventDateRow.add(eventDateLabel);
        eventDateChooser = new DateChooser();
        eventDateChooser.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    eventDate = eventDateChooser.getSelectedDate().getTime();
                    String eventDateString = sdf.format(eventDate);
                    eventDateLabel.setText(eventDateString);
                }
            });
        CheckBox selectEventDateCheckBox = new CheckBox("pick a different date");
        selectEventDateCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    CheckBox cb = (CheckBox)e.getSource();
                    if (cb.isSelected()) {
                        getUserColumn().add(eventDateChooser, 2);
                        setHeight(new Extent(getHeight().getValue()+eventDateHeightOffset));
                    } else {
                        getUserColumn().remove(2);
                        setHeight(new Extent(getHeight().getValue()-eventDateHeightOffset));
                    }
                }
            });
        eventDateRow.add(selectEventDateCheckBox);
        userColumn.add(eventDateRow);

        // prompt for serial number
        Label snLabel = new Label("Enter Serial Number or Identifier:");
        snTextField = new TextField();
        snTextField.setWidth(fieldWidth);
        userColumn.add(snLabel);
        userColumn.add(snTextField);

        // ask if this instance is currently installed
        Label yesNoLabel = new Label("Is this instance currently installed (or recently removed)?");
        Row yesNoRow = new Row();
        yesNoRow.setStyleName("RadioButton.Row");
        yesRadioButton = new RadioButton("Yes");
        RadioButton noRadioButton = new RadioButton("No");
        ButtonGroup buttonGroup = new ButtonGroup();
        yesRadioButton.setGroup(buttonGroup);
        noRadioButton.setGroup(buttonGroup);
        noRadioButton.setSelected(true);
        yesRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setHeight(new Extent(getHeight().getValue()+treeHeightOffset));
                    userColumn.remove(locationColumn);
                    userColumn.add(treeColumn);
                }
            });
        noRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setHeight(new Extent(getHeight().getValue()-treeHeightOffset));
                    userColumn.remove(treeColumn);
                    userColumn.add(locationColumn);
                    selectedComponent = null;
                }
            });
        yesNoRow.add(yesRadioButton);
        yesNoRow.add(noRadioButton);
        userColumn.add(yesNoLabel);
        userColumn.add(yesNoRow);

        // this colum will hold simple location text box, or housing tree,
        //   depending on whether yes or no was answered above
        locationColumn = new Column();
        locationColumn.setCellSpacing(new Extent(5));

        // prompt for component type
        Label nameLabel = new Label("Select component type:");
        locationColumn.add(nameLabel);
        
        Row filterRow = new Row();
        Label compTypeFilterLabel = new Label("Find:");
        filterRow.add(compTypeFilterLabel);

        // text field for filtering component types
        model.setFilteredComponentTypeList(model.getComponentTypeList()); // reset any previous filtering
        compTypeFilterTextField = new TextField();
        compTypeFilterTextField.setWidth(new Extent(200));
        filterRow.add(compTypeFilterTextField);
        compTypeFilterTextField.setToolTipText("Enter a wildcard string to filter component type list.");
        compTypeFilterTextField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    typeTableSelectionModel.clearSelection();
                    selectedComponentType = null;
                    List filteredList = 
                        applyComponentTypeListFilters(model.getComponentTypeList(),
                                                      compTypeFilterTextField.getText(),
                                                      selectedMfg);
                    model.setFilteredComponentTypeList(filteredList);

                    ComponentTypeTableModel tableModel = 
                        (ComponentTypeTableModel)typeTable.getModel();
                    // request that comp type table update itself
                    tableModel.fireTableDataChanged();
                }
            });

        locationColumn.add(filterRow);

        Row mfgRow = new Row();
        Label mfgLabel = new Label("Mfg:");
        mfgRow.add(mfgLabel);
        mfgSelect = new SelectFieldEx();
        mfgSelect.setWidth(fieldWidth);
        mfgSelect.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String choice = (String)mfgSelect.getSelectedItem();
                    if (choice.equals("All")) {
                        selectedMfg = null;
                    } else {
                        Manufacturer mfg = model.findMfg(choice);
                        selectedMfg = mfg;
                    }
                    typeTableSelectionModel.clearSelection();
                    selectedComponentType = null;
                    List filteredList = 
                        applyComponentTypeListFilters(model.getComponentTypeList(),
                                                      compTypeFilterTextField.getText(),
                                                      selectedMfg);
                    model.setFilteredComponentTypeList(filteredList);
                    ComponentTypeTableModel tableModel = 
                        (ComponentTypeTableModel)typeTable.getModel();
                    // request that comp type table update itself
                    tableModel.fireTableDataChanged();
                }
            });
        ListModel mfgListModel = new MfgListModel();
        mfgSelect.setModel(mfgListModel);
        mfgSelect.setSelectedIndex(0);
        mfgRow.add(mfgSelect);
        locationColumn.add(mfgRow);
        
        // component types table
        componentTypeErrorLabel = new Label("");
        componentTypeErrorLabel.setForeground(Color.RED);
        locationColumn.add(componentTypeErrorLabel);
        ContainerEx tableContainerEx = new ContainerEx();
        tableContainerEx.setHeight(new Extent(160));
        typeTableModel = new ComponentTypeTableModel();
        typeTable = new TableEx(typeTableModel);
        typeTable.setStyleName("Default.TableEx");
        typeTable.setWidth(new Extent(99, Extent.PERCENT));
        typeTable.setHeight(new Extent(150));
        TableColumnModel columnModel = typeTable.getColumnModel();
        //columnModel.getColumn(0).setWidth(new Extent(38,Extent.PERCENT));
        //columnModel.getColumn(1).setWidth(new Extent(60,Extent.PERCENT));
        typeTable.setScrollable(true);
        typeTableSelectionModel = new DefaultListSelectionModel();
        typeTableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        typeTable.setSelectionModel(typeTableSelectionModel);
        typeTable.setSelectionEnabled(true);
        typeTable.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = typeTableSelectionModel.getMinSelectedIndex();
                    selectedComponentType = 
                        (ComponentType)model.getFilteredComponentTypeList().get(row);
                    String desc = selectedComponentType.getDescription();
                    descLabel.setText(desc);
                    setHeight(locationHeightExtent);
                    componentTypeErrorLabel.setText("");
                }
            });
        tableContainerEx.add(typeTable);

        // prefill Find: from search term
        if (componentTypeText != null && componentTypeText.length() > 0) {
            compTypeFilterTextField.setText(componentTypeText);
            typeTableSelectionModel.clearSelection();
            List filteredList = 
                applyComponentTypeListFilters(model.getComponentTypeList(),
                                              compTypeFilterTextField.getText(),
                                              selectedMfg);
            model.setFilteredComponentTypeList(filteredList);
        }

        // preselect component type if possible
        ComponentType selectedComponentType = model.getSelectedComponentType();
        if (selectedComponentType != null) {
            List ctl = model.getFilteredComponentTypeList();
            if (ctl != null) {
                int idx = ctl.indexOf(selectedComponentType);
                if (idx != -1) {
                    typeTableSelectionModel.clearSelection();
                    typeTableSelectionModel.setSelectedIndex(idx, true);
                }
            }
        }

        locationColumn.add(tableContainerEx);

        ContainerEx descContainerEx = new ContainerEx();
        descContainerEx.setHeight(new Extent(50));
        Label descBelowLabel = new Label("Description:");
        descContainerEx.add(descBelowLabel);        
        descLabel = new Label();
        descContainerEx.add(descLabel);
        locationColumn.add(descContainerEx);

        // prompt for location string
        locationLabel = new Label("Enter location as a string:");
        locationTextField = new TextField();
        locationTextField.setWidth(fieldWidth);
        locationColumn.add(locationLabel);
        locationColumn.add(locationTextField);

        // prompt for initial location state
        stateSelectLabel = new Label("Pick an initial location state:");
        stateSelectField = new SelectField();
        stateSelectField.setWidth(fieldWidth);
        ListModel stateListModel = new StateListModel();
        stateSelectField.setModel(stateListModel);
        stateSelectField.setSelectedIndex(0);
        locationColumn.add(stateSelectLabel);
        locationColumn.add(stateSelectField);

        userColumn.add(locationColumn);

        // build up content used if user chooses Yes
        treeColumn = new Column();
        treeColumn.setCellSpacing(new Extent(5));
        treeContainer = new ContainerEx();
        treeContainer.setBackground(Color.WHITE);
        treeContainer.setHeight(new Extent(250));
        treeContainer.setScrollBarPolicy(Scrollable.ALWAYS);
        Label treeLocationLabel = new Label("Select it from the housing tree:");
        treeErrorLabel = new Label("");
        treeErrorLabel.setForeground(Color.RED);
        treeColumn.add(treeLocationLabel);
        treeColumn.add(treeErrorLabel);
        housingTree = new Tree();
        housingTree.setStyleName("Default.Tree");
        treeSelectionModel = new DefaultTreeSelectionModel();
        treeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeSelectionModel.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath path = e.getNewLeadSelectionPath();
                    if (path != null) {
                        selectedComponent = (Component)path.getLastPathComponent();
                        hasInstance = 
                            controller.actionComponentHasInstance(selectedComponent);
                        if (hasInstance) {
                            setHeight(new Extent(getHeight().getValue()+treeErrorHeightOffset));
                            treeErrorLabel.setText("This component already has an instance defined.");
                        } else {
                            setHeight(new Extent(getHeight().getValue()-treeErrorHeightOffset));
                            treeErrorLabel.setText(null);
                        }
                    } else {
                        selectedComponent = null;
                    }
                }
            });
        housingTree.setSelectionModel(treeSelectionModel);
        ComponentTreeCellRenderer treeCellRenderer = new ComponentTreeCellRenderer();
        treeCellRenderer.setStyleName("Default.ComponentTreeCellRenderer");
        housingTree.setCellRenderer(treeCellRenderer);
        housingTree.setRootVisible(true);

        housingTreeModel = new FilteredHousingComponentTreeModel();
        housingTreeModel.setRoot(model.getSiteComponent());
        housingTree.setModel(housingTreeModel);
        TreeModelEvent he = 
            new TreeModelEvent(this, new Object[] {model.getSiteComponent()});
        housingTreeModel.fireTreeStructureChanged(he);            
        treeContainer.add(housingTree);
        treeColumn.add(treeLocationLabel);
        treeColumn.add(treeContainer);

    }


    public boolean okAction(ActionEvent e) {

        // copy data from widgets to model
        model.setNewInstanceSerialNumber(snTextField.getText());
        model.setNewInstanceEventDate(eventDate);

        if (yesRadioButton.isSelected()) {
            if (selectedComponent == null) {
                setHeight(new Extent(getHeight().getValue()+treeErrorHeightOffset));
                treeErrorLabel.setText("You must select a component from the housing tree.");
                return false;
            } else if (hasInstance) {
                setHeight(new Extent(getHeight().getValue()+treeErrorHeightOffset));
                treeErrorLabel.setText("This component already has an instance defined.");
                return false;
            }
            model.setNewInstanceAssociatedComponent(selectedComponent);
            model.setNewInstanceLocationString(null);
            model.setNewInstanceComponentTypeName(null);
            model.setNewInstanceInitialState("installed");
            
        } else {
            if (selectedComponentType == null) {
                setHeight(new Extent(getHeight().getValue()+locationErrorHeightOffset));
                componentTypeErrorLabel.setText("You must select a component type from the list.");
                return false;
            }
            model.setNewInstanceAssociatedComponent(null);
            model.setNewInstanceLocationString(locationTextField.getText());
            model.setNewInstanceComponentTypeName(selectedComponentType.getComponentTypeName());
            model.setNewInstanceInitialState((String)stateSelectField.getSelectedItem());
        }
        return true;
    }

    public void cancelAction(ActionEvent e) {
        model.setNewInstanceSerialNumber(null);
        model.setNewInstanceAssociatedComponent(null);
        model.setNewInstanceLocationString(null);
        model.setNewInstanceComponentTypeName(null);
    }

    /**
     * Take a component type list, and filter it based on supplied criteria. 
     *
     * @param ctList original full list of component types
     * @param filterText wildcard string to match against component type name and description
     * @param mfg the component type manufacturer to match against
     *
     * @returns list of component types filtered by criteria
     */
    private List applyComponentTypeListFilters(List ctList, String filterText, Manufacturer mfg) {
        
        // uppercase it for case insensitive match
        filterText = filterText.toUpperCase();                        
        // change any * to reg-ex pattern
        //filterText = filterText.replaceAll("\\*",".*");
        Pattern filterRegEx = Pattern.compile("^.*\\Q"+filterText+"\\E.*$");
        
        Iterator compTypeIt = ctList.iterator();
        List filteredList = new ArrayList();
        while (compTypeIt.hasNext()) {
            ComponentType ct = (ComponentType)compTypeIt.next();
            Matcher matcher1 = 
                filterRegEx.matcher(ct.getComponentTypeName().toUpperCase());
            Matcher matcher2 = 
                filterRegEx.matcher(ct.getDescription().toUpperCase());
            boolean mfgMatches = true;
            if (mfg != null) {
                if (ct.getManufacturer().equals(mfg))
                    mfgMatches = true;
                else
                    mfgMatches = false;
            }
            if ((matcher1.matches() || matcher2.matches()) && mfgMatches)
                filteredList.add(ct);
        }
        return filteredList;
    }

    /**
     * Model for display of component type table.
     */
    class ComponentTypeTableModel extends AbstractTableModel {
        private String[] columnNames = {"Type"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            if (model.getFilteredComponentTypeList() != null)
                size = model.getFilteredComponentTypeList().size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int col, int row) {
            List ctl = model.getFilteredComponentTypeList();
            if (ctl != null && ctl.size() > 0) {
                ComponentType type = 
                    (ComponentType)ctl.get(row);
                if (col == 0) {
                    return type.getComponentTypeName();
                } else {
                    if (type.getDescription() == null)
                        return " ";
                    else 
                        return type.getDescription();
                }
            } else {
                return " ";
            }

        }

        public Class getColumnClass(int c) {
            return getValueAt(c, 0).getClass();
        }

    }

    public class StateListModel extends AbstractListModel {

        public int size() {
            return model.getLocationStateList().size();
        }
        
        public Object get(int index) {
            List stateList = model.getLocationStateList();
            ComponentState cs = (ComponentState)stateList.get(index);
            return cs.getState();
        }

    }

    public class ComponentTreeCellRenderer extends DefaultTreeCellRenderer {

        public ComponentTreeCellRenderer() {
            super();
        }

        public Label getTreeCellRendererText(Tree tree,
                                             Object node,
                                             boolean selected,
                                             boolean expanded,
                                             boolean leaf) {

            gov.anl.aps.irmis.persistence.component.Component c = 
                (gov.anl.aps.irmis.persistence.component.Component)node;
            String text = c.toString(ComponentRelationshipType.HOUSING);
            Label label = super.getTreeCellRendererText(tree, node, selected, expanded, leaf);
            label.setText(text);
            label.setIcon(null);
            return label;

        }

    }

    public class MfgListModel extends AbstractListModel {

        private List<Manufacturer> mfgList;

        public MfgListModel() {
            mfgList = model.getMfgList();
        }

        public int size() {
            return mfgList.size() + 1; // +1 for "All" entry
        }
        
        public Object get(int index) {
            if (index == 0)
                return "All";
            Manufacturer mfg = (Manufacturer)mfgList.get(index-1);
            return (String)mfg.getManufacturerName();
        }
        
    }

}