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
 * IRMIS Component History Calibration Tab. Implements display and entry of
 * irmis component calibration history (state). 
 */
public class ComponentHistoryCalibrationTab extends ComponentHistoryTab {

    // component history table
    private TableEx calibrationHistoryTable;

    public ComponentHistoryCalibrationTab(ContentPane mainWindowPane, ComponentHistoryController controller, LoginUtil loginUtil) {
        super(mainWindowPane, controller);

        // calibration history table
        ContainerEx tableContainerEx = new ContainerEx();
        tableContainerEx.setHeight(new Extent(150));
        tableContainerEx.setScrollBarPolicy(Scrollable.AUTO);

        CalibrationHistoryTableModel calibrationHistoryTableModel = new CalibrationHistoryTableModel();
        calibrationHistoryTable = new TableEx(calibrationHistoryTableModel);
        calibrationHistoryTable.setStyleName("Default.TableEx");
        calibrationHistoryTable.setWidth(new Extent(99, Extent.PERCENT));
        //calibrationHistoryTable.setHeight(new Extent(150));
        calibrationHistoryTable.setDefaultRenderer(new TabTableCellRenderer());
        TableColumnModel calibrationHistoryColumnModel = calibrationHistoryTable.getColumnModel();
        calibrationHistoryColumnModel.getColumn(0).setWidth(new Extent(15,Extent.PERCENT));
        calibrationHistoryColumnModel.getColumn(5).setWidth(new Extent(10,Extent.PERCENT));
        calibrationHistoryColumnModel.getColumn(1).setWidth(new Extent(15,Extent.PERCENT));
        calibrationHistoryColumnModel.getColumn(4).setWidth(new Extent(32,Extent.PERCENT));
        calibrationHistoryColumnModel.getColumn(2).setWidth(new Extent(13,Extent.PERCENT));
        calibrationHistoryColumnModel.getColumn(3).setWidth(new Extent(15,Extent.PERCENT)); // was 14
        //calibrationHistoryTable.setScrollable(true);
        ListSelectionModel calibrationHistoryTableSelectionModel = new DefaultListSelectionModel();
        calibrationHistoryTableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        calibrationHistoryTable.setSelectionModel(calibrationHistoryTableSelectionModel);
        calibrationHistoryTable.setSelectionEnabled(false);
        tableContainerEx.add(calibrationHistoryTable);
        addToTableContainer(tableContainerEx);  // superclass method

        // "Calibrated..." button
        Button calibrationButton = new Button("Calibrated...");
        calibrationButton.setStyleName("Button.Primary");
        loginUtil.registerProtectedComponent(calibrationButton, ComponentHistoryApp.editPrincipal);
        calibrationButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getModel().getSelectedComponentInstance() != null) {
                        WindowPane calibrationDialog =
                            new ComponentHistoryCalibrationDialog(getController());
                        getMainWindowPane().add(calibrationDialog);
                    }
                }
            });

        // "Changed Battery..." button
        Button batteryButton = new Button("Changed Battery...");
        batteryButton.setStyleName("Button.Primary");
        loginUtil.registerProtectedComponent(batteryButton, ComponentHistoryApp.editPrincipal);
        batteryButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getModel().getSelectedComponentInstance() != null) {
                        WindowPane batteryDialog =
                            new ComponentHistoryBatteryDialog(getController());
                        getMainWindowPane().add(batteryDialog);
                    }
                }
            });

        // "Updated Firmware..." button
        Button firmwareButton = new Button("Updated Firmware...");
        firmwareButton.setStyleName("Button.Primary");
        loginUtil.registerProtectedComponent(firmwareButton, ComponentHistoryApp.editPrincipal);
        firmwareButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getModel().getSelectedComponentInstance() != null) {
                        WindowPane firmwareDialog =
                            new ComponentHistoryFirmwareDialog(getController());
                        getMainWindowPane().add(firmwareDialog);
                    }
                }
            });

        
        addToButtonRow(calibrationButton);  // superclass method
        addToButtonRow(batteryButton);  // superclass method
        addToButtonRow(firmwareButton);  // superclass method
        
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
            CalibrationHistoryTableModel tableModel =
                (CalibrationHistoryTableModel)calibrationHistoryTable.getModel();
            tableModel.fireTableDataChanged();
            break;
        }

        case ComponentHistoryModelEvent.HISTORY_SEARCH_COMPLETE: {
            // redraw calibration history table, since model should now contain
            //    results
            CalibrationHistoryTableModel tableModel =
                (CalibrationHistoryTableModel)calibrationHistoryTable.getModel();
            tableModel.fireTableDataChanged();

            break;
        }

        case ComponentHistoryModelEvent.NEW_CALIBRATION_HISTORY: {
            // redraw calibration history table, since we have new entry now
            CalibrationHistoryTableModel tableModel =
                (CalibrationHistoryTableModel)calibrationHistoryTable.getModel();
            tableModel.fireTableDataChanged();
            break;
        }

        default: {}
        }
            
    }


    class CalibrationHistoryTableModel extends AbstractTableModel {
        
        private final DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        private final DateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        private String[] columnNames = {"Date", "State","Version", "Next Maint. Date","Comment","Entered By"};

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            List chl = getModel().getCalibrationHistoryList();
            if (chl != null)
                size = chl.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int col, int row) {
            List chl = getModel().getCalibrationHistoryList();
            if (chl != null && chl.size() > 0) {
                ComponentInstanceState cis = (ComponentInstanceState)chl.get(row);
                switch (col) {
                case 0: {
                    Date date = cis.getEnteredDate();
                    if (date != null)
                        return sdf.format(date);
                    else
                        return "unknown";
                }
                case 5: {
                    Person person = cis.getPerson();
                    if (person != null)
                        return person.getUserid();
                    else
                        return "unknown";
                }
                case 1: {
                    ComponentState cs = cis.getComponentState();
                    if (cs != null) {
                        String state = cs.getState();
                        if (state.equals("changed"))
                            return "battery changed";
                        else if (state.equals("updated"))
                            return "firmware updated";
                        else
                            return cs.getState();
                    } else {
                        return "unknown";
                    }
                }
                case 4: {
                    String comment = cis.getComment();
                    if (comment != null && comment.length() > 0)
                        return comment;
                    else
                        return "-none-";
                }
                case 2: {
                    String version = cis.getReferenceData1();
                    if (version != null && version.length() > 0)
                        return version;
                    else
                        return "N/A";
                }
                case 3: {
                    Date nextCalibDate = cis.getReferenceData2();
                    if (nextCalibDate != null && 
                        !cis.getComponentState().getComponentStateCategory().getCategory().equals("firmware"))
                        return sdf.format(nextCalibDate);
                    else
                        return "N/A";
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

