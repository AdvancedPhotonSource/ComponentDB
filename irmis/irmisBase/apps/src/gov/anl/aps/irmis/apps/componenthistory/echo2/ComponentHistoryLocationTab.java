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
 * IRMIS Component History Location Tab. Implements display and entry of
 * irmis component location history (state). 
 */
public class ComponentHistoryLocationTab extends ComponentHistoryTab {

    // component history table
    private TableEx locationHistoryTable;

    public ComponentHistoryLocationTab(ContentPane mainWindowPane, ComponentHistoryController controller, LoginUtil loginUtil) {
        super(mainWindowPane, controller);

        // location history table
        ContainerEx tableContainerEx = new ContainerEx();
        tableContainerEx.setHeight(new Extent(150));
        tableContainerEx.setScrollBarPolicy(Scrollable.AUTO);

        LocationHistoryTableModel locationHistoryTableModel = new LocationHistoryTableModel();
        locationHistoryTable = new TableEx(locationHistoryTableModel);
        locationHistoryTable.setStyleName("Default.TableEx");
        locationHistoryTable.setWidth(new Extent(99, Extent.PERCENT));
        //locationHistoryTable.setHeight(new Extent(150));
        locationHistoryTable.setDefaultRenderer(new TabTableCellRenderer());
        TableColumnModel locationHistoryColumnModel = locationHistoryTable.getColumnModel();
        locationHistoryColumnModel.getColumn(0).setWidth(new Extent(15,Extent.PERCENT));
        locationHistoryColumnModel.getColumn(4).setWidth(new Extent(10,Extent.PERCENT));
        locationHistoryColumnModel.getColumn(1).setWidth(new Extent(10,Extent.PERCENT));
        locationHistoryColumnModel.getColumn(3).setWidth(new Extent(27,Extent.PERCENT));
        locationHistoryColumnModel.getColumn(2).setWidth(new Extent(38,Extent.PERCENT));  // was 37
        //locationHistoryTable.setScrollable(true);
        ListSelectionModel locationHistoryTableSelectionModel = new DefaultListSelectionModel();
        locationHistoryTableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        locationHistoryTable.setSelectionModel(locationHistoryTableSelectionModel);
        locationHistoryTable.setSelectionEnabled(false);
        tableContainerEx.add(locationHistoryTable);
        addToTableContainer(tableContainerEx);  // superclass method

        // "update location..." button
        Button locationAddButton = new Button("Update Location...");
        locationAddButton.setStyleName("Button.Primary");
        loginUtil.registerProtectedComponent(locationAddButton, ComponentHistoryApp.editPrincipal);
        locationAddButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getModel().getSelectedComponentInstance() != null) {
                        WindowPane updateLocationDialog =
                            new ComponentHistoryUpdateLocationDialog(getController());
                        getMainWindowPane().add(updateLocationDialog);
                        /*
                        System.out.println("invoking controller.actionDetermineOpenInstallSlots");
                        getController().actionDetermineOpenInstallSlots();
                        */
                    }
                }
            });
        
        addToButtonRow(locationAddButton);  // superclass method

        // "replace failed component..." button
        /*
        Button replaceFailedButton = new Button("Replace Failed Component...");
        replaceFailedButton.setStyleName("Button.Primary");
        loginUtil.registerProtectedComponent(replaceFailedButton, ComponentHistoryApp.editPrincipal);
        replaceFailedButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getModel().getSelectedComponentInstance() != null) {
                        WindowPane replaceFailedDialog =
                            new ComponentHistoryReplaceFailedDialog(getController());
                        getMainWindowPane().add(replaceFailedDialog);
                    }
                }
            });
        
        addToButtonRow(replaceFailedButton);  // superclass method
        */
        
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
            //   need to redraw location history table as empty
            LocationHistoryTableModel tableModel =
                (LocationHistoryTableModel)locationHistoryTable.getModel();
            tableModel.fireTableDataChanged();
            break;
        }

        case ComponentHistoryModelEvent.HISTORY_SEARCH_COMPLETE: {
            // redraw location history table, since model should now contain
            //    results
            LocationHistoryTableModel tableModel =
                (LocationHistoryTableModel)locationHistoryTable.getModel();
            tableModel.fireTableDataChanged();
            break;
        }

        case ComponentHistoryModelEvent.NEW_LOCATION_HISTORY: {
            // redraw location history table, since we have new entry now
            LocationHistoryTableModel tableModel =
                (LocationHistoryTableModel)locationHistoryTable.getModel();
            tableModel.fireTableDataChanged();
            break;
        }

        case ComponentHistoryModelEvent.OPEN_INSTALL_SLOTS_FOUND: {
            /*
            WindowPane updateLocationDialog =
                new ComponentHistoryUpdateLocationDialog(getController());
            getMainWindowPane().add(updateLocationDialog);
            */
        }

        default: {}
        }
            
    }


    class LocationHistoryTableModel extends AbstractTableModel {
        
        private final DateFormat df = new SimpleDateFormat("MM/dd/yy");
        private String[] columnNames = {"Date", "State","Location","Comment","Entered By"};

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            List lhl = getModel().getLocationHistoryList();
            if (lhl != null)
                size = lhl.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int col, int row) {
            List lhl = getModel().getLocationHistoryList();
            if (lhl != null && lhl.size() > 0) {
                ComponentInstanceState cis = (ComponentInstanceState)lhl.get(row);
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
                    String locationString = cis.getReferenceData1();
                    if (locationString != null && locationString.length() > 0)
                        return locationString;
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

