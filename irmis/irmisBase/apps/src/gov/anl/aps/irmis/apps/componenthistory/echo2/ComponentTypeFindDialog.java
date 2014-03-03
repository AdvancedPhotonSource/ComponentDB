/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/

package gov.anl.aps.irmis.apps.componenthistory.echo2;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;

// Echo2 
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Button;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.TextField;
import nextapp.echo2.app.Table;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;

import nextapp.echo2.app.list.ListSelectionModel;
import nextapp.echo2.app.list.DefaultListSelectionModel;
import nextapp.echo2.app.list.AbstractListModel;
import nextapp.echo2.app.list.ListModel;

import nextapp.echo2.app.table.TableModel;
import nextapp.echo2.app.table.AbstractTableModel;
import nextapp.echo2.app.table.DefaultTableCellRenderer;
import nextapp.echo2.app.table.DefaultTableColumnModel;
import nextapp.echo2.app.table.TableColumnModel;

// EchoPointNG
import echopointng.TableEx;
import echopointng.able.Scrollable;
import echopointng.ContainerEx;
import echopointng.SelectFieldEx;

// Echo2 support
import gov.anl.aps.irmis.apps.echo2support.DialogBox;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.component.Manufacturer;


/**
 * Dialog box for finding and selecting a component type.
 */
public class ComponentTypeFindDialog extends DialogBox {

    static final String title = "Find A Component Type";
    static final int width = 400;
    static final int height = 480;
    static final Extent fieldWidth = new Extent(250);

    private ComponentHistoryController controller = null;
    private ComponentHistoryModel model = null;

    private TableEx typeTable;
    private ComponentTypeTableModel typeTableModel;
    private ListSelectionModel typeTableSelectionModel;
    private TextField compTypeFilterTextField;
    private ComponentType selectedComponentType;
    private SelectFieldEx mfgSelect;
    private Manufacturer selectedMfg;
    private Label descLabel;

    public ComponentTypeFindDialog(ComponentHistoryController controller) {
        super(title, width, height);

        this.controller = controller;
        model = controller.getModel();

        Column userColumn = getUserColumn();
        userColumn.setCellSpacing(new Extent(5));
        userColumn.setInsets(new Insets(5));

        Label nameLabel = new Label("Select component type:");
        userColumn.add(nameLabel);
        
        Row filterRow = new Row();
        Label compTypeFilterLabel = new Label("Find:");
        filterRow.add(compTypeFilterLabel);

        // text field for filtering component types
        model.setFilteredComponentTypeList(model.getComponentTypeList()); // reset any previous filtering
        compTypeFilterTextField = new TextField();
        compTypeFilterTextField.setWidth(fieldWidth);
        filterRow.add(compTypeFilterTextField);
        compTypeFilterTextField.setToolTipText("Enter a wildcard string to filter component type list.");
        compTypeFilterTextField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    typeTableSelectionModel.clearSelection();

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

        userColumn.add(filterRow);

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
        userColumn.add(mfgRow);
        
        // component types table
        ContainerEx tableContainerEx = new ContainerEx();
        tableContainerEx.setHeight(new Extent(260));
        typeTableModel = new ComponentTypeTableModel();
        typeTable = new TableEx(typeTableModel);
        typeTable.setStyleName("Default.TableEx");
        typeTable.setWidth(new Extent(99, Extent.PERCENT));
        typeTable.setHeight(new Extent(250));
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
                }
            });
        tableContainerEx.add(typeTable);

        userColumn.add(tableContainerEx);

        Label descBelowLabel = new Label("Description:");
        userColumn.add(descBelowLabel);        
        descLabel = new Label();
        userColumn.add(descLabel);
        
    }

    public boolean okAction(ActionEvent e) {
        model.setSelectedComponentType(selectedComponentType);
        return true;
    }

    public void cancelAction(ActionEvent e) {
        model.setSelectedComponentType(null);
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