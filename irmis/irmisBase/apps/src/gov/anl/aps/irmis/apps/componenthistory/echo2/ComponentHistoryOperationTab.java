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

// Echo2 
import nextapp.echo2.app.Button;
import nextapp.echo2.app.ContentPane;
import nextapp.echo2.app.WindowPane;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.table.TableModel;
import nextapp.echo2.app.table.TableColumnModel;
import nextapp.echo2.app.table.AbstractTableModel;
import nextapp.echo2.app.list.ListSelectionModel;
import nextapp.echo2.app.list.DefaultListSelectionModel;

// EchoPointNG
import echopointng.TableEx;
import echopointng.ContainerEx;
import echopointng.able.Scrollable;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstanceState;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentState;
import gov.anl.aps.irmis.persistence.login.Person;

// Application Support
import gov.anl.aps.irmis.login.echo2support.LoginUtil;

/**
 * IRMIS Component History Operation Tab. Implements display and entry of
 * irmis component operation history (state). 
 */
public class ComponentHistoryOperationTab extends ComponentHistoryTab {

    // component history table
    private TableEx operationHistoryTable;

    public ComponentHistoryOperationTab(ContentPane mainWindowPane, ComponentHistoryController controller, LoginUtil loginUtil) {
        super(mainWindowPane, controller);

        // operation history table
        ContainerEx tableContainerEx = new ContainerEx();
        tableContainerEx.setHeight(new Extent(150));
        tableContainerEx.setScrollBarPolicy(Scrollable.AUTO);

        OperationHistoryTableModel operationHistoryTableModel = new OperationHistoryTableModel();
        operationHistoryTable = new TableEx(operationHistoryTableModel);
        operationHistoryTable.setStyleName("Default.TableEx");
        operationHistoryTable.setWidth(new Extent(99, Extent.PERCENT));
        //operationHistoryTable.setHeight(new Extent(150));
        operationHistoryTable.setDefaultRenderer(new TabTableCellRenderer());
        TableColumnModel operationHistoryColumnModel = operationHistoryTable.getColumnModel();
        operationHistoryColumnModel.getColumn(0).setWidth(new Extent(15,Extent.PERCENT));
        operationHistoryColumnModel.getColumn(4).setWidth(new Extent(10,Extent.PERCENT));
        operationHistoryColumnModel.getColumn(1).setWidth(new Extent(10,Extent.PERCENT));
        operationHistoryColumnModel.getColumn(3).setWidth(new Extent(37,Extent.PERCENT));
        operationHistoryColumnModel.getColumn(2).setWidth(new Extent(28,Extent.PERCENT));  // was 27
        //operationHistoryTable.setScrollable(true);
        ListSelectionModel operationHistoryTableSelectionModel = new DefaultListSelectionModel();
        operationHistoryTableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        operationHistoryTable.setSelectionModel(operationHistoryTableSelectionModel);
        operationHistoryTable.setSelectionEnabled(false);
        tableContainerEx.add(operationHistoryTable);
        addToTableContainer(tableContainerEx);  // superclass method

        // "Failure..." button
        Button operationFailureButton = new Button("Mark as Failed/Questionable...");
        operationFailureButton.setStyleName("Button.Primary");
        loginUtil.registerProtectedComponent(operationFailureButton, ComponentHistoryApp.editPrincipal);
        operationFailureButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getModel().getSelectedComponentInstance() != null) {
                        WindowPane failureDialog =
                            new ComponentHistoryFailureDialog(getController());
                        getMainWindowPane().add(failureDialog);
                    }
                }
            });
        
        addToButtonRow(operationFailureButton);  // superclass method

        // "Repaired..." button
        Button operationRepairButton = new Button("Mark as Repaired...");
        operationRepairButton.setStyleName("Button.Primary");
        loginUtil.registerProtectedComponent(operationRepairButton, ComponentHistoryApp.editPrincipal);
        operationRepairButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getModel().getSelectedComponentInstance() != null) {
                        WindowPane repairDialog =
                            new ComponentHistoryRepairDialog(getController());
                        getMainWindowPane().add(repairDialog);
                    }
                }
            });
        
        addToButtonRow(operationRepairButton);  // superclass method

        // "Tested..." button
        Button operationTestedButton = new Button("Mark as Tested...");
        operationTestedButton.setStyleName("Button.Primary");
        loginUtil.registerProtectedComponent(operationTestedButton, ComponentHistoryApp.editPrincipal);
        operationTestedButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getModel().getSelectedComponentInstance() != null) {
                        WindowPane testedDialog =
                            new ComponentHistoryTestedDialog(getController());
                        getMainWindowPane().add(testedDialog);
                    }
                }
            });
        
        addToButtonRow(operationTestedButton);  // superclass method

        // "Inspected..." button
        Button operationInspectedButton = new Button("Mark as Inspected...");
        operationInspectedButton.setStyleName("Button.Primary");
        loginUtil.registerProtectedComponent(operationInspectedButton, ComponentHistoryApp.editPrincipal);
        operationInspectedButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getModel().getSelectedComponentInstance() != null) {
                        WindowPane inspectedDialog =
                            new ComponentHistoryInspectedDialog(getController());
                        getMainWindowPane().add(inspectedDialog);
                    }
                }
            });
        
        addToButtonRow(operationInspectedButton);  // superclass method

        // "Validated..." button
        Button operationValidatedButton = new Button("Mark as Validated...");
        operationValidatedButton.setStyleName("Button.Primary");
        loginUtil.registerProtectedComponent(operationValidatedButton, ComponentHistoryApp.editPrincipal);
        operationValidatedButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getModel().getSelectedComponentInstance() != null) {
                        WindowPane validatedDialog =
                            new ComponentHistoryValidatedDialog(getController());
                        getMainWindowPane().add(validatedDialog);
                    }
                }
            });
        
        addToButtonRow(operationValidatedButton);  // superclass method
        
    }

    /**
     * Update graphical view of model data based on event type.
     *
     * @param event type of data model change
     */
    public void updateView(ComponentHistoryModelEvent event) {

        switch(event.getType()) {
            
        case ComponentHistoryModelEvent.INSTANCE_SEARCH_COMPLETE: {
            // instance search may produce 0 results, in which case we
            //   need to redraw operation history table as empty
            OperationHistoryTableModel tableModel =
                (OperationHistoryTableModel)operationHistoryTable.getModel();
            tableModel.fireTableDataChanged();
            break;
        }

        case ComponentHistoryModelEvent.HISTORY_SEARCH_COMPLETE: {
            // redraw operation history table, since model should now contain
            //    results
            OperationHistoryTableModel tableModel =
                (OperationHistoryTableModel)operationHistoryTable.getModel();
            tableModel.fireTableDataChanged();
            break;
        }

        case ComponentHistoryModelEvent.NEW_OPERATION_HISTORY: {
            // redraw operation history table, since we have new entry now
            OperationHistoryTableModel tableModel =
                (OperationHistoryTableModel)operationHistoryTable.getModel();
            tableModel.fireTableDataChanged();
            break;
        }

        default: {}
        }
            
    }


    class OperationHistoryTableModel extends AbstractTableModel {
        
        private final DateFormat df = new SimpleDateFormat("MM/dd/yy");
        private String[] columnNames = {"Date","State","CTLLOG#/Test/Procedure Used","Comment","Entered By"};

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            List ohl = getModel().getOperationHistoryList();
            if (ohl != null)
                size = ohl.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int col, int row) {
            List ohl = getModel().getOperationHistoryList();
            if (ohl != null && ohl.size() > 0) {
                ComponentInstanceState cis = (ComponentInstanceState)ohl.get(row);
                switch (col) {
                case 0: {
                    Date date = cis.getEnteredDate();
                    if (date != null)
                        return df.format(date);
                    else
                        return "unknown";
                }
                case 4: {
                    Person person = cis.getPerson();
                    if (person != null)
                        return person.getUserid();
                    else
                        return "unknown";
                }
                case 1: {
                    ComponentState cs = cis.getComponentState();
                    if (cs != null)
                        return cs.getState();
                    else
                        return "unknown";
                }
                case 3: {
                    String comment = cis.getComment();
                    if (comment != null && comment.length() > 0)
                        return comment;
                    else
                        return "-none-";
                }
                case 2: {
                    String ctllogString = cis.getReferenceData1();
                    if (ctllogString != null && ctllogString.length() > 0)
                        return ctllogString;
                    else
                        return "-none-";
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


}

