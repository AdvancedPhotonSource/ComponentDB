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
 * IRMIS Component History NRTL Tab. Implements display and entry of
 * irmis component nrtl history (state). 
 */
public class ComponentHistoryNRTLTab extends ComponentHistoryTab {

    // component history table
    private TableEx nrtlHistoryTable;

    public ComponentHistoryNRTLTab(ContentPane mainWindowPane, ComponentHistoryController controller, LoginUtil loginUtil) {
        super(mainWindowPane, controller);

        // nrtl history table
        ContainerEx tableContainerEx = new ContainerEx();
        tableContainerEx.setHeight(new Extent(150));
        tableContainerEx.setScrollBarPolicy(Scrollable.AUTO);

        NRTLHistoryTableModel nrtlHistoryTableModel = new NRTLHistoryTableModel();
        nrtlHistoryTable = new TableEx(nrtlHistoryTableModel);
        nrtlHistoryTable.setStyleName("Default.TableEx");
        nrtlHistoryTable.setWidth(new Extent(99, Extent.PERCENT));
        //nrtlHistoryTable.setHeight(new Extent(150));
        nrtlHistoryTable.setDefaultRenderer(new TabTableCellRenderer());
        TableColumnModel nrtlHistoryColumnModel = nrtlHistoryTable.getColumnModel();
        nrtlHistoryColumnModel.getColumn(0).setWidth(new Extent(15,Extent.PERCENT));
        nrtlHistoryColumnModel.getColumn(4).setWidth(new Extent(10,Extent.PERCENT));
        nrtlHistoryColumnModel.getColumn(1).setWidth(new Extent(22,Extent.PERCENT));
        nrtlHistoryColumnModel.getColumn(3).setWidth(new Extent(32,Extent.PERCENT));
        nrtlHistoryColumnModel.getColumn(2).setWidth(new Extent(21,Extent.PERCENT)); // was 20
        //nrtlHistoryTable.setScrollable(true);
        ListSelectionModel nrtlHistoryTableSelectionModel = new DefaultListSelectionModel();
        nrtlHistoryTableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nrtlHistoryTable.setSelectionModel(nrtlHistoryTableSelectionModel);
        nrtlHistoryTable.setSelectionEnabled(false);
        tableContainerEx.add(nrtlHistoryTable);
        addToTableContainer(tableContainerEx);  // superclass method

        // "NRTL Status..." button
        Button nrtlButton = new Button("NRTL Status...");
        nrtlButton.setStyleName("Button.Primary");
        loginUtil.registerProtectedComponent(nrtlButton, ComponentHistoryApp.editPrincipal);
        nrtlButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getModel().getSelectedComponentInstance() != null) {
                        WindowPane nrtlDialog =
                            new ComponentHistoryNRTLDialog(getController());
                        getMainWindowPane().add(nrtlDialog);
                    }
                }
            });
        
        addToButtonRow(nrtlButton);  // superclass method
        
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
            NRTLHistoryTableModel tableModel =
                (NRTLHistoryTableModel)nrtlHistoryTable.getModel();
            tableModel.fireTableDataChanged();
            break;
        }

        case ComponentHistoryModelEvent.HISTORY_SEARCH_COMPLETE: {
            // redraw nrtl history table, since model should now contain
            //    results
            NRTLHistoryTableModel tableModel =
                (NRTLHistoryTableModel)nrtlHistoryTable.getModel();
            tableModel.fireTableDataChanged();

            break;
        }

        case ComponentHistoryModelEvent.NEW_NRTL_HISTORY: {
            // redraw nrtl history table, since we have new entry now
            NRTLHistoryTableModel tableModel =
                (NRTLHistoryTableModel)nrtlHistoryTable.getModel();
            tableModel.fireTableDataChanged();
            break;
        }

        default: {}
        }
            
    }


    class NRTLHistoryTableModel extends AbstractTableModel {
        
        private final DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        private final DateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        private String[] columnNames = {"Date", "State","NRTL Agency","Comment","Entered By"};

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            List chl = getModel().getNRTLHistoryList();
            if (chl != null)
                size = chl.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int col, int row) {
            List chl = getModel().getNRTLHistoryList();
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
                    String agency = cis.getReferenceData1();
                    if (agency != null && agency.length() > 0)
                        return agency;
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

