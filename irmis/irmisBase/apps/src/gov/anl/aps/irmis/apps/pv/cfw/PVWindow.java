/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.pv.cfw;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.logging.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import gov.sns.application.*;

// persistence layer
import gov.anl.aps.irmis.persistence.pv.IOCBoot;
import gov.anl.aps.irmis.persistence.pv.IOCResource;
import gov.anl.aps.irmis.persistence.pv.Record;
import gov.anl.aps.irmis.persistence.pv.RecordType;
import gov.anl.aps.irmis.persistence.pv.Field;
import gov.anl.aps.irmis.persistence.pv.RecordClient;
import gov.anl.aps.irmis.persistence.pv.VURIRelationship;

// service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.pv.PVSearchParameters;

// application helpers
import gov.anl.aps.irmis.apps.AppsUtil;
import gov.anl.aps.irmis.apps.TableSorter;
import gov.anl.aps.irmis.apps.irmis.cfw.Main;

/**
 * Primary GUI for IRMIS PV Viewer application. All window layout, and Swing event
 * listeners are done here. Business logic work is delegated to <code>PVDocument</code>,
 * which in turn requests that the PVModel notify us here of changes to the data. In short,
 * we listen for <code>PVModelEvent</code> here.
 */
public class PVWindow extends XalInternalWindow {

	/** The main model for the document */
	final protected PVModel _model;

    final protected PVDocument document;

    // Container holding latest search parameters
    private PVSearchParameters pvSearchParams = new PVSearchParameters();

    // holds ioc resource objects associated with db files in JList
    private List dbFileListResources = new ArrayList();

    // unique list of Record from search results (used for historyMode)
    private List uniqueRecordList;

    // ordered array of field results for selected PV.
    private Field[] fieldArray;

    // ordered array of db file names associated with selected record
    private String[] dbFileArray;

    // pv search results will instead display history of pv's found
    private boolean historyMode = false;

    // choices for pv search results columns
    private ColumnChoices columnChoices = new ColumnChoices();

	// Swing GUI components
    private JPanel topPanel;
    private JPanel searchPanel;
    private JPanel resultsPanel;
    private JPanel pvListPanel;
    private JPanel pvDetailsPanel;
    private JPanel pvInfoPanel;
    private JPanel pvFieldsPanel;
    private JPanel pvLinksPanel;
    private JPanel searchParamsPanel;
    private JPanel searchActionPanel;
    private JPanel systemListPanel;
    private JPanel iocListPanel;
    private JPanel dbFileListPanel;
    private JPanel recTypeListPanel;
    private JPanel otherParamsPanel;
    private JPanel pvHistoryPanel;
    private JPanel pvBootHistoryPanel;
    private JPanel clientsPanel;
    private JPanel linksAndClientsPanel;
    private JPanel pvResultsPanel;

    private JComponent selectedRecordPanel;         // shown if historyMode == false
    private JComponent selectedRecordHistoryPanel;  // shown if historyMode == true
    private JSplitPane tbSplitPane;
    private JSplitPane lrSplitPane;

    private TitledBorder pvListTitle;

    private JButton prevPVButton;
    private JButton nextPVButton;
    private JTable pvList;
    private TableSorter pvListSorter;
    private AbstractTableModel pvListModel;
    private JTable pvInfoTable;
    private JTable pvInfoDbTable;
    private JTable fieldList;
    private AbstractTableModel fieldListModel;
    private JTable linkList;
    private JTable clientList;
    private JPanel clientListPanel;
    private JTable bootHistoryList;
    private JList systemList;
    private JList iocList;
    private DefaultListModel systemListModel;
    private DefaultListModel iocListModel;
    private JCheckBox allSystemCheckBox;
    private JCheckBox allIocCheckBox;
    private JList dbFileList;
    private DefaultListModel dbFileListModel;
    private JCheckBox allDbFileCheckBox;
    private JList recTypeList;
    private DefaultListModel recTypeListModel;
    private JCheckBox allRecTypeCheckBox;

    private JTextField pvNameText;
    private JTextField fieldNameText;
    private JLabel fieldNameLabel;
    private JTextField fieldValueText;
    private JLabel fieldValueLabel;
    
    private JLabel pvInfoRecordName;
    private JLabel pvInfoRecordType;
    private JLabel pvInfoIoc;
    private JLabel pvInfoDBFile;

    private JButton searchButton;
    private JCheckBox historyModeCheckBox;

    private JButton pvResultsColumnButton;
    private JButton pvResultsReportButton;
	
	/** 
	 * Creates a new instance of PVWindow
	 * @param aDocument The document for this window
	 */
    public PVWindow(final XalInternalDocument aDocument) {
        super(aDocument);

        document = (PVDocument)aDocument;
		_model = document.getModel();

        // make PVWindow a listener for changes in PVModel
		_model.addPvModelListener( new PVModelListener() {
                public void modified(PVModelEvent e) {
                    updateView(e);
                }
            });

        // initial application window size
        setSize(700, 700);

        // build contents
		makeContents();
    }

    /**
     * Top-level method to build up Swing GUI components.
     */
    public void makeContents() {

        // topmost organization (pv search above, results below)
        topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;

        // pv search panel
        searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel,BoxLayout.PAGE_AXIS));
        TitledBorder searchTitle = BorderFactory.createTitledBorder("PV Search");
        searchPanel.setBorder(searchTitle);

        // pv search results panel
        resultsPanel = new JPanel(new BorderLayout());

        // add search and results panels to topPanel
        c.gridx = 0;
        c.gridy = 0;
        topPanel.add(searchPanel,c);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 1.0;
        topPanel.add(resultsPanel,c);

        // fill in contents of two main panels
        makeSearchPanelContents(searchPanel);
        makeResultsPanelContents(resultsPanel);
        
    	getContentPane().add(topPanel);
    	
    }

    /**
     * Build up top half of window that contains search constraints and search button.
     *
     * @param searchPanel add components to this panel
     */
    private void makeSearchPanelContents(JPanel searchPanel) {

        int visibleRowCount = 5;
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        // hidden search params panel to organize search param components
        searchParamsPanel = new JPanel();
        searchParamsPanel.setLayout(new BoxLayout(searchParamsPanel,BoxLayout.LINE_AXIS));

        // search action panel to organize search button
        searchActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // pack these into overall searchPanel
        searchPanel.add(searchParamsPanel);
        searchPanel.add(searchActionPanel);

        // create system list panel
        systemListPanel = new JPanel(new BorderLayout());
        TitledBorder systemListTitle = BorderFactory.createTitledBorder("Systems");
        systemListPanel.setBorder(systemListTitle);
        systemListModel = new DefaultListModel();
    	systemList = new JList(systemListModel);
    	systemList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // handle system list selection events
        systemList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting() == false) {
                        updateIOCView(systemList.getSelectedIndices());
                        // logic so list selection plays well with "all" checkbox
                        if (allSystemCheckBox.isSelected() &&
                            systemList.getSelectedIndices().length != systemListModel.size())
                            allSystemCheckBox.setSelected(false);
                    }
                }
            });
    	JScrollPane systemListScroller = new JScrollPane(systemList, vsbp, hsbp);
        systemList.setVisibleRowCount(visibleRowCount);
        systemListPanel.add(systemListScroller);

        // create "all" checkbox for sytem list
        allSystemCheckBox = new JCheckBox("all");
        allSystemCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent ie) {
                    if (ie.getStateChange() == ItemEvent.SELECTED) {
                        systemList.addSelectionInterval(0,systemListModel.size()-1);
                    } else {
                        // logic so "all" checkbox plays well with list selection
                        if (systemList.getSelectedIndices().length == systemListModel.size()) {
                            systemList.clearSelection();
                            systemList.setSelectedIndex(0);
                        }
                    }
                }
            });
        systemListPanel.add(allSystemCheckBox, BorderLayout.SOUTH);

        // create ioc list panel
        iocListPanel = new JPanel(new BorderLayout());
        TitledBorder iocListTitle = BorderFactory.createTitledBorder("IOCs");
        iocListPanel.setBorder(iocListTitle);
        iocListModel = new DefaultListModel();
    	iocList = new JList(iocListModel);
    	iocList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // handle ioc list selection events
        iocList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting() == false) {
                        updateDBFileView(iocList.getSelectedIndices());
                        // logic so list selection plays well with "all" checkbox
                        if (allIocCheckBox.isSelected() &&
                            iocList.getSelectedIndices().length != iocListModel.size())
                            allIocCheckBox.setSelected(false);
                    }
                }
            });
    	JScrollPane iocListScroller = new JScrollPane(iocList, vsbp, hsbp);
        iocList.setVisibleRowCount(visibleRowCount);
        iocListPanel.add(iocListScroller);

        // create "all" checkbox for ioc list
        allIocCheckBox = new JCheckBox("all");
        allIocCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent ie) {
                    if (ie.getStateChange() == ItemEvent.SELECTED) {
                        iocList.addSelectionInterval(0,iocListModel.size()-1);
                    } else {
                        // logic so "all" checkbox plays well with list selection
                        if (iocList.getSelectedIndices().length == iocListModel.size()) {
                            iocList.clearSelection();
                            iocList.setSelectedIndex(0);
                        }
                    }
                }
            });
        iocListPanel.add(allIocCheckBox, BorderLayout.SOUTH);

        // create db file list panel
        dbFileListPanel = new JPanel(new BorderLayout());
        TitledBorder dbFileListTitle = BorderFactory.createTitledBorder("DB Files");
        dbFileListPanel.setBorder(dbFileListTitle);
        dbFileListModel = new DefaultListModel();
    	dbFileList = new JList(dbFileListModel);
    	dbFileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // handle db file list selection events
        dbFileList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting() == false) {
                        // logic so list selection plays well with "all" checkbox
                        if (allDbFileCheckBox.isSelected() &&
                            dbFileList.getSelectedIndices().length != dbFileListModel.size())
                            allDbFileCheckBox.setSelected(false);
                    }
                }
            });
    	JScrollPane dbFileListScroller = new JScrollPane(dbFileList, vsbp, hsbp);
        dbFileList.setVisibleRowCount(visibleRowCount);
        dbFileListPanel.add(dbFileListScroller);

        // create "all" checkbox for db file list
        allDbFileCheckBox = new JCheckBox("all");
        allDbFileCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent ie) {
                    if (ie.getStateChange() == ItemEvent.SELECTED) {
                        dbFileList.addSelectionInterval(0,dbFileListModel.size()-1);
                    } else {
                        // logic so "all" checkbox plays well with list selection
                        if (dbFileList.getSelectedIndices().length == dbFileListModel.size()) {
                            dbFileList.clearSelection();
                            dbFileList.setSelectedIndex(0);
                        }
                    }
                }
            });
        dbFileListPanel.add(allDbFileCheckBox, BorderLayout.SOUTH);

        // create record type list panel
        recTypeListPanel = new JPanel(new BorderLayout());
        TitledBorder recTypeListTitle = BorderFactory.createTitledBorder("Record Types");
        recTypeListPanel.setBorder(recTypeListTitle);
        recTypeListModel = new DefaultListModel();
    	recTypeList = new JList(recTypeListModel);
    	recTypeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // handle record type list selection events
        recTypeList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting() == false) {
                        // logic so list selection plays well with "all" checkbox
                        if (allRecTypeCheckBox.isSelected() &&
                            recTypeList.getSelectedIndices().length != recTypeListModel.size())
                            allRecTypeCheckBox.setSelected(false);
                    }
                }
            });
    	JScrollPane recTypeListScroller = new JScrollPane(recTypeList, vsbp, hsbp);
        recTypeList.setVisibleRowCount(visibleRowCount);
        recTypeListPanel.add(recTypeListScroller);

        // create "all" checkbox for record type list
        allRecTypeCheckBox = new JCheckBox("all");
        allRecTypeCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent ie) {
                    if (ie.getStateChange() == ItemEvent.SELECTED) {
                        recTypeList.addSelectionInterval(0,recTypeListModel.size()-1);
                    } else {
                        // logic so "all" checkbox plays well with list selection
                        if (recTypeList.getSelectedIndices().length == recTypeListModel.size()) {
                            recTypeList.clearSelection();
                            recTypeList.setSelectedIndex(0);
                        }
                    }
                }
            });
        recTypeListPanel.add(allRecTypeCheckBox, BorderLayout.SOUTH);

        // create other search params panel
        otherParamsPanel = new JPanel(new BorderLayout());
        //otherParamsPanel.setLayout(new BoxLayout(otherParamsPanel,BoxLayout.PAGE_AXIS));
        TitledBorder otherParamsTitle = BorderFactory.createTitledBorder("Other Search Params");
        otherParamsPanel.setBorder(otherParamsTitle);

        JPanel otherParamsPanel2 = new JPanel();
        otherParamsPanel2.setLayout(new BoxLayout(otherParamsPanel2,BoxLayout.PAGE_AXIS));

        JPanel pvNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel pvNameLabel = new JLabel("PV:", SwingConstants.LEFT);
        pvNameText = new JTextField(12);
        pvNamePanel.add(pvNameLabel);
        pvNamePanel.add(pvNameText);

        JPanel fieldNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fieldNameLabel = new JLabel("Field: ", SwingConstants.LEFT);
        fieldNameText = new JTextField(7);
        fieldNamePanel.add(fieldNameLabel);
        fieldNamePanel.add(fieldNameText);

        JPanel fieldValuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fieldValueLabel = new JLabel("Value:", SwingConstants.LEFT);
        fieldValueText = new JTextField(7);
        fieldValuePanel.add(fieldValueLabel);
        fieldValuePanel.add(fieldValueText);

        JLabel wildcardText = new JLabel("* allowed", SwingConstants.LEFT);

        otherParamsPanel2.add(pvNamePanel);
        otherParamsPanel2.add(fieldNamePanel);
        otherParamsPanel2.add(fieldValuePanel);
        otherParamsPanel2.add(wildcardText);
        otherParamsPanel.add(otherParamsPanel2, BorderLayout.NORTH);
        otherParamsPanel.setMinimumSize(new Dimension(180,150));
        otherParamsPanel.setMinimumSize(new Dimension(180,150));
        //otherParamsPanel.setPreferredSize(new Dimension(180,150));

        // create search button
        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(251,251,87));  // IRMIS yellow !

        // invoke PVDocument.actionPVSearch() when button is pressed
        searchButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // clear out previous results from model
                    _model.setRecordList(null);
                    _model.setLinkList(null);
                    _model.setClientList(null);
                    _model.setSelectedRecord(null);
                    _model.setSelectedRecordBootHistory(null);

                    // build up contents of pvSearchParams
                    updateRecordTypeInSearchParams();
                    updateDbFilesInSearchParams();
                    updateIocsInSearchParams();

                    pvSearchParams.setRecNameGlob(pvNameText.getText());
                    pvSearchParams.setFieldNameGlob(fieldNameText.getText());
                    pvSearchParams.setFieldValueGlob(fieldValueText.getText());
                    // do search
                    document.actionPVSearch(pvSearchParams, historyMode);
                }
            });
               

        // create history mode checkbox
        historyModeCheckBox = new JCheckBox("History Mode");
        historyModeCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent ie) {
                    if (ie.getStateChange() == ItemEvent.SELECTED) {
                        historyMode = true;

                        // clear out previous results from model
                        _model.setRecordList(null);
                        _model.setLinkList(null);
                        _model.setClientList(null);
                        _model.setSelectedRecord(null);
                        _model.setSelectedRecordBootHistory(null);
                        fieldArray = null;
                        dbFileArray = null;

                        // request that the pvBootHistory table update itself
                        PVBootHistoryTableModel pvBootHistoryTableModel = 
                            (PVBootHistoryTableModel)bootHistoryList.getModel();
                        pvBootHistoryTableModel.fireTableDataChanged();

                        // change the lower right panel to display boot history instead
                        lrSplitPane.setRightComponent(selectedRecordHistoryPanel);
                        lrSplitPane.setDividerLocation(200);
                        selectedRecordHistoryPanel.validate();

                        // disable some stuff
                        systemList.setEnabled(false);
                        allSystemCheckBox.setEnabled(false);
                        allSystemCheckBox.setSelected(true);
                        iocList.setEnabled(false);
                        allIocCheckBox.setEnabled(false);
                        allIocCheckBox.setSelected(true);
                        dbFileList.setEnabled(false);
                        allDbFileCheckBox.setEnabled(false);
                        allDbFileCheckBox.setSelected(true);
                        recTypeList.setEnabled(false);
                        allRecTypeCheckBox.setEnabled(false);
                        allRecTypeCheckBox.setSelected(true);
                        fieldNameText.setEnabled(false);
                        fieldNameLabel.setEnabled(false);
                        fieldValueText.setEnabled(false);
                        fieldValueLabel.setEnabled(false);
                        
                    } else {
                        historyMode = false;

                        // clear out previous results from model
                        _model.setRecordList(null);
                        _model.setLinkList(null);
                        _model.setClientList(null);
                        _model.setSelectedRecord(null);
                        _model.setSelectedRecordBootHistory(null);
                        fieldArray = null;
                        dbFileArray = null;

                        // request that field table update itself
                        fieldListModel.fireTableDataChanged();
                        
                        // request that the table with list of db files update itself
                        SelectedPVInfoDbTableModel dbTableModel = 
                            (SelectedPVInfoDbTableModel)pvInfoDbTable.getModel();
                        dbTableModel.fireTableDataChanged();

                        // put the regular pv info/fields/links display panel back
                        lrSplitPane.setRightComponent(selectedRecordPanel);
                        lrSplitPane.setDividerLocation(200);
                        selectedRecordPanel.validate();

                        // re-enable some stuff
                        systemList.setEnabled(true);
                        allSystemCheckBox.setEnabled(true);
                        iocList.setEnabled(true);
                        allIocCheckBox.setEnabled(true);
                        dbFileList.setEnabled(true);
                        allDbFileCheckBox.setEnabled(true);
                        recTypeList.setEnabled(true);
                        allRecTypeCheckBox.setEnabled(true);
                        fieldNameText.setEnabled(true);
                        fieldNameLabel.setEnabled(true);
                        fieldValueText.setEnabled(true);
                        fieldValueLabel.setEnabled(true);
                    }
                }
            }); 

        // pack panels
        searchParamsPanel.add(systemListPanel);
        searchParamsPanel.add(iocListPanel);
        searchParamsPanel.add(dbFileListPanel);
        searchParamsPanel.add(recTypeListPanel);
        searchParamsPanel.add(otherParamsPanel);

        searchActionPanel.add(searchButton);
        searchActionPanel.add(historyModeCheckBox);
    }

    /**
     * Build up lower half of window that contains pv search results list,
     * and associated fields and links.
     *
     * @param resultsPanel add components to this panel
     */
    private void makeResultsPanelContents(JPanel resultsPanel) {
        
        // re-usable configurations
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
        Dimension smallButtonDim = new Dimension(20,20);

        pvResultsPanel = new JPanel();
        pvResultsPanel.setLayout(new BoxLayout(pvResultsPanel,BoxLayout.PAGE_AXIS));

        // pv list results panel
        pvListPanel = new JPanel();
        pvListPanel.setLayout(new BoxLayout(pvListPanel,BoxLayout.PAGE_AXIS));
        pvListTitle = BorderFactory.createTitledBorder("PV Search Results");
        pvListPanel.setBorder(pvListTitle);

        // back/forward buttons
        JPanel pvNavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pvNavPanel.setMinimumSize(new Dimension(100,25));
        pvNavPanel.setPreferredSize(new Dimension(100,25));
        pvNavPanel.setMaximumSize(new Dimension(700,25));

        prevPVButton = new JButton();
        prevPVButton.setMaximumSize(smallButtonDim);
        prevPVButton.setPreferredSize(smallButtonDim);
        prevPVButton.setAction(new PrevPVAction());
        prevPVButton.setEnabled(false);
        nextPVButton = new JButton();
        nextPVButton.setMaximumSize(smallButtonDim);
        nextPVButton.setPreferredSize(smallButtonDim);
        nextPVButton.setAction(new NextPVAction());
        nextPVButton.setEnabled(false);
        pvNavPanel.add(prevPVButton);
        pvNavPanel.add(nextPVButton);
        //pvListPanel.add(pvNavPanel);  not yet implemented

        // pv list table (with click to sort on columns)
        pvListModel = new PVResultsTableModel();
        pvListSorter = new TableSorter(pvListModel);
        pvList = new JTable(pvListSorter);
        pvListSorter.setTableHeader(pvList.getTableHeader());
        pvList.setShowHorizontalLines(true);
        pvList.getColumnModel().getColumn(0).setCellRenderer(new PVLinksTableCellRenderer());
    	JScrollPane pvListScroller = new JScrollPane(pvList, vsbp, hsbp);
        pvListPanel.add(pvListScroller);
        pvList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        pvList.getColumnModel().getColumn(0).setPreferredWidth(150);
        pvList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // handle row selection in pv list table
        pvList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int selectedPVResultsRow = lsm.getMinSelectionIndex();
                        List recordList;
                        if (historyMode) {
                            recordList = uniqueRecordList;
                        } else {
                            recordList = _model.getRecordList();
                        }
                        // list get needs to be aware of possible changes to sort order
                        Record selectedRecord = 
                            (Record)recordList.get(pvListSorter.modelIndex(selectedPVResultsRow));
                        _model.setSelectedRecord(selectedRecord);

                        if (historyMode) {
                            _model.setSelectedRecordBootHistory(null);
                            PVBootHistoryTableModel pvBootHistoryTableModel = 
                                (PVBootHistoryTableModel)bootHistoryList.getModel();
                            pvBootHistoryTableModel.fireTableDataChanged();
                            document.actionBootHistorySearch();
                        } else {
                            // clear out old data before searching
                            _model.setLinkList(null);
                            _model.setClientList(null);
                            SelectedPVInfoTableModel pvInfoTableModel = 
                                (SelectedPVInfoTableModel)pvInfoTable.getModel();
                            pvInfoTableModel.fireTableDataChanged();
                            // kick off field and link searches
                            document.actionFieldSearch();
                            document.actionLinkSearch();
                            document.actionClientSearch();
                        }
                    }
                }
            });

        // pv results list button bar
        JPanel pvResultsButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pvResultsColumnButton = new JButton("Columns...");
        pvResultsColumnButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFrame appFrame = 
                        ((DesktopApplication)Application.getApp()).getDesktopFrame();
                    boolean newChoices = 
                        ResultsColumnsDialog.showDialog(appFrame,null,columnChoices);
                    if (newChoices) {
                        pvListModel.fireTableStructureChanged();
                        //document.actionSaveIoc(ioc,true);
                    }
                }
            });
        
        pvResultsReportButton = new JButton("Save As...");
        pvResultsReportButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
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
                                // save the pv results to this file here
                                document.actionSaveResultsToFile(file, pvList.getModel());
                            }
                        } else {
                            document.actionSaveResultsToFile(file, pvList.getModel());
                        }
                    }
                }
            });

        // build button panel
        pvResultsButtons.add(pvResultsColumnButton);
        pvResultsButtons.add(pvResultsReportButton);

        // build results list with buttons below
        pvResultsPanel.add(pvListPanel);
        pvResultsPanel.add(pvResultsButtons);

        // make the panel used to display data after pv result is selected
        selectedRecordPanel = makeSelectedRecordPanelContents();

        // make, but don't display the panel to display pv history data
        selectedRecordHistoryPanel = makeSelectedRecordHistoryPanelContents();

        // split between pv list and pv details info on right
        lrSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,pvResultsPanel,selectedRecordPanel);
        lrSplitPane.setDividerLocation(210);

        // add split panes to resultsPanel
        resultsPanel.add(lrSplitPane);

        return;
    }


    private JComponent makeSelectedRecordPanelContents() {
        
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        // pv details panel containing pv info and pv fields 
        pvDetailsPanel = new JPanel();
        pvDetailsPanel.setLayout(new BoxLayout(pvDetailsPanel,BoxLayout.PAGE_AXIS));

        // pv info 
        pvInfoPanel = new JPanel();
        pvInfoPanel.setLayout(new BoxLayout(pvInfoPanel,BoxLayout.PAGE_AXIS));
        TitledBorder pvInfoTitle = BorderFactory.createTitledBorder("PV Info");
        pvInfoPanel.setBorder(pvInfoTitle);
        pvInfoTable = new JTable(new SelectedPVInfoTableModel());
        pvInfoTable.setCellSelectionEnabled(true);
        pvInfoTable.getColumnModel().getColumn(2).setCellRenderer(new SelectedPVInfoTableCellRenderer());
        pvInfoTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        pvInfoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel columnLSM = pvInfoTable.getColumnModel().getSelectionModel();
        columnLSM.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int selectedCol = lsm.getMinSelectionIndex();
                        if (selectedCol == 2) {
                            document.actionProduceIOCDocument();
                            lsm.clearSelection();
                        }
                    }
                }                


            });

        pvInfoPanel.add(pvInfoTable.getTableHeader());
        pvInfoPanel.add(pvInfoTable);

        // pv db file info
        pvInfoDbTable = new JTable(new SelectedPVInfoDbTableModel());
    	JScrollPane pvInfoDbTableScroller = new JScrollPane(pvInfoDbTable, vsbp, hsbp);
        pvInfoDbTableScroller.setPreferredSize(new Dimension(100,45));
        pvInfoDbTableScroller.setMinimumSize(new Dimension(50,35));
        pvInfoPanel.add(pvInfoDbTableScroller);

        // pv fields
        pvFieldsPanel = new JPanel(new BorderLayout());
        TitledBorder pvFieldsTitle = BorderFactory.createTitledBorder("PV Fields");
        pvFieldsPanel.setBorder(pvFieldsTitle);

        // pv fields table (with click to sort on columns)
        fieldListModel = new PVFieldsTableModel();
        TableSorter sorter = new TableSorter(fieldListModel);
        fieldList = new JTable(sorter);
        sorter.setTableHeader(fieldList.getTableHeader());
        fieldList.setShowHorizontalLines(true);
        fieldList.getColumnModel().getColumn(1).setCellRenderer(new PVFieldsTableCellRenderer());
    	JScrollPane fieldListScroller = new JScrollPane(fieldList, vsbp, hsbp);
        pvFieldsPanel.add(fieldListScroller);
        fieldList.getColumnModel().getColumn(1).setPreferredWidth(380);
        fieldList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // handle row selection in field list table
        fieldList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int selectedRow = lsm.getMinSelectionIndex();
                        Field selectedField = fieldArray[selectedRow];

                        // make sure field is a link type before kicking off any search
                        String dbdType = selectedField.getFieldType().getDbdType();
                        if ((dbdType.equals("DBF_INLINK") ||
                             dbdType.equals("DBF_OUTLINK") ||
                             dbdType.equals("DBF_FWDLINK")) &&
                            PVUtils.isPurePV(selectedField.getFieldValue())) {

                            // deselect current pv from pv search results
                            pvList.clearSelection();

                            // this should be record name
                            String linkFieldValue = selectedField.getFieldValue();
                            document.actionSinglePVSearch(linkFieldValue);
                        }
                    }
                }
            });
        
        // add info and fields subpanels to pv details panel
        pvDetailsPanel.add(pvInfoPanel);
        pvDetailsPanel.add(pvFieldsPanel);

        // links panel
        pvLinksPanel = new JPanel(new BorderLayout());
        TitledBorder pvLinksTitle = BorderFactory.createTitledBorder("PV Links");
        pvLinksPanel.setBorder(pvLinksTitle);

        // pv links table
        linkList = new JTable(new PVLinksTableModel());
        linkList.setShowHorizontalLines(true);
        linkList.getColumnModel().getColumn(0).setCellRenderer(new PVLinksTableCellRenderer());
    	JScrollPane linkListScroller = new JScrollPane(linkList, vsbp, hsbp);
        pvLinksPanel.add(linkListScroller);
        linkList.getColumnModel().getColumn(0).setPreferredWidth(130);
        linkList.getColumnModel().getColumn(1).setMaxWidth(40);
        linkList.getColumnModel().getColumn(2).setMaxWidth(50);
        linkList.getColumnModel().getColumn(3).setPreferredWidth(220);
        linkList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // handle row selection in link list table
        linkList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int selectedRow = lsm.getMinSelectionIndex();
                        
                        List linkList = _model.getLinkList();
                        Field selectedField = (Field)linkList.get(selectedRow);
                        
                        // deselect current pv from pv search results
                        pvList.clearSelection();

                        // this should be record name
                        String linkRecordName = selectedField.getRecord().getRecordName();
                        document.actionSinglePVSearch(linkRecordName);
                    }
                }
            });


        // clients panel
        clientsPanel = new JPanel();
        clientsPanel.setLayout(new BoxLayout(clientsPanel,BoxLayout.PAGE_AXIS));

        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox showClientsCheckBox = new JCheckBox("PV Clients");
        showClientsCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent ie) {
                    if (ie.getStateChange() == ItemEvent.SELECTED) {
                        clientsPanel.add(clientListPanel);
                        linksAndClientsPanel.revalidate();
                        _model.setShowClients(true);
                        document.actionClientSearch();
                    } else {
                        clientsPanel.remove(clientListPanel);
                        linksAndClientsPanel.revalidate();
                        _model.setShowClients(false);
                    }
                }
            });
        checkBoxPanel.add(showClientsCheckBox);
        clientsPanel.add(checkBoxPanel);

        // clients table
        clientListPanel = new JPanel(new BorderLayout());
        Border clientListPanelBorder = BorderFactory.createTitledBorder("");
        clientListPanel.setBorder(clientListPanelBorder);
        clientList = new JTable(new PVClientsTableModel());
        clientList.setShowHorizontalLines(true);
        clientList.getColumnModel().getColumn(2).setCellRenderer(new PVClientsTableCellRenderer());
        clientList.getColumnModel().getColumn(3).setCellRenderer(new PVClientsTableCellRenderer());
    	JScrollPane clientListScroller = new JScrollPane(clientList, vsbp, hsbp);
        clientListPanel.add(clientListScroller);
        clientList.getColumnModel().getColumn(0).setMaxWidth(70);
        clientList.getColumnModel().getColumn(1).setMaxWidth(40);
        clientList.getColumnModel().getColumn(2).setPreferredWidth(200);
        clientList.getColumnModel().getColumn(3).setPreferredWidth(120);
        clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // links and clients panel
        linksAndClientsPanel = new JPanel();
        linksAndClientsPanel.setLayout(new BoxLayout(linksAndClientsPanel, BoxLayout.PAGE_AXIS));
        linksAndClientsPanel.add(pvLinksPanel);
        linksAndClientsPanel.add(clientsPanel);

        // split between pv fields and links/clients
        tbSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,pvDetailsPanel,linksAndClientsPanel);
        tbSplitPane.setDividerLocation(300);

        return tbSplitPane;
    }

    private JComponent makeSelectedRecordHistoryPanelContents() {
        
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        // pv history panel containing pv's ioc boot history
        pvHistoryPanel = new JPanel();
        pvHistoryPanel.setLayout(new BoxLayout(pvHistoryPanel,BoxLayout.PAGE_AXIS));

        // pv boot history panel
        pvBootHistoryPanel = new JPanel();
        pvBootHistoryPanel.setLayout(new BoxLayout(pvBootHistoryPanel,BoxLayout.PAGE_AXIS));
        TitledBorder pvBootHistoryTitle = BorderFactory.createTitledBorder("Boot History");
        pvBootHistoryPanel.setBorder(pvBootHistoryTitle);

        // boot history table
        bootHistoryList = new JTable(new PVBootHistoryTableModel());
        bootHistoryList.setShowHorizontalLines(true);
        bootHistoryList.getColumnModel().getColumn(1).setCellRenderer(new PVBootHistoryTableCellRenderer());
    	JScrollPane bootHistoryListScroller = new JScrollPane(bootHistoryList, vsbp, hsbp);
        pvBootHistoryPanel.add(bootHistoryListScroller);
        bootHistoryList.getColumnModel().getColumn(1).setPreferredWidth(380);
        bootHistoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        pvHistoryPanel.add(pvBootHistoryPanel);

        return pvHistoryPanel;
    }

    /**
     * Update graphical view of PVModel data based on event type.
     *
     * @param event type of data model change
     */
    public void updateView(PVModelEvent event) {

        // check first for any exception
        IRMISException ie = _model.getIRMISException();
        if (ie != null) {  // uh-oh
            Logger.getLogger("global").log(Level.WARNING, "IRMIS Service Error.", ie);
            Application.displayError("IRMIS Service Error", "Exiting due to exception... ", ie);
            System.exit(-1);
        }

        switch(event.getType()) {

        case PVModelEvent.NEW_IOC_BOOT_LIST: {  // redraw pretty much everything

            // initialize ioc list box
            List bootList = _model.getIocBootList();
            iocListModel.clear();
            systemListModel.clear();
            if (bootList != null) {
                Iterator iocIt = bootList.iterator();
                // iterate over ioc boots (one per ioc)
                while (iocIt.hasNext()) {
                    IOCBoot ib = (IOCBoot)iocIt.next();
                    // explicitly add ioc names to selection list
                    iocListModel.addElement(ib.getIoc().getIocName());
                    if (ib.getIoc().getSystem() != null &&
                        !systemListModel.contains(ib.getIoc().getSystem())) {
                        systemListModel.addElement(ib.getIoc().getSystem());
                    }
                }
                allSystemCheckBox.setSelected(true);
            }

            // intialize record type list box
            List recordTypeList = _model.getRecordTypeList();
            recTypeListModel.clear();
            if (recordTypeList != null) {
                Iterator rtIt = recordTypeList.iterator();
                while (rtIt.hasNext()) {
                    RecordType rt = (RecordType)rtIt.next();
                    recTypeListModel.addElement(rt.getRecordType());
                }
                allRecTypeCheckBox.setSelected(true);
            }
            searchPanel.validate();

            // request that pv results table update itself
            pvListModel.fireTableDataChanged();

            // request that field table update itself
            fieldListModel.fireTableDataChanged();

            // request that the table with list of db files update itself
            SelectedPVInfoDbTableModel dbTableModel = 
                (SelectedPVInfoDbTableModel)pvInfoDbTable.getModel();
            dbTableModel.fireTableDataChanged();

            PVLinksTableModel linksTableModel = (PVLinksTableModel)linkList.getModel();
            // request that links table update itself
            linksTableModel.fireTableDataChanged();

            PVClientsTableModel clientsTableModel = (PVClientsTableModel)clientList.getModel();
            // request that links table update itself
            clientsTableModel.fireTableDataChanged();

            break;
        }

        case PVModelEvent.NEW_RECORD_LIST: {

            // request that pv results table update itself
            pvListModel.fireTableDataChanged();

            // display number of results
            int numResults = 0;
            if (_model.getRecordList() != null) {
                if (historyMode)
                    numResults = _model.getCollapsedRecordList().size();
                else
                    numResults = _model.getRecordList().size();
            }
            pvListTitle.setTitle("PV Search Results (" + numResults + ")");
            if (numResults == 0)
                pvListTitle.setTitleColor(Color.red);
            else
                pvListTitle.setTitleColor(Color.black);

            // some secondary data that might be needed for history mode
            if (_model.getRecordList() != null && _model.getRecordList().size() > 0) {
                if (historyMode) {
                    // get info in uniqueRecordMap into an ArrayList for use here
                    uniqueRecordList = new ArrayList();
                    Iterator crlIt = _model.getCollapsedRecordList().iterator();
                    while (crlIt.hasNext()) {
                        ArrayList chain = (ArrayList)crlIt.next();
                        // put zero'th element in, since we are guaranteed at least one
                        uniqueRecordList.add(chain.get(0)); // add a Record instance
                    }
                }
                pvList.setRowSelectionInterval(0,0);  // preselect row 0
            }
            pvListPanel.repaint();
            break;
        }

        case PVModelEvent.NEW_FIELD_LIST: {
            // get fields into more usable array form, and build up db file array
            if (_model.getSelectedRecord() != null) {
                // convert fields set to an ordered array
                fieldArray = (Field[])_model.getSelectedRecord().getFields().toArray(new Field[0]);
                // make db file names unique by adding to set 
                HashSet dbFileHash = new HashSet();
                Iterator fieldIt = _model.getSelectedRecord().getFields().iterator();
                while (fieldIt.hasNext()) {
                    Field field = (Field)fieldIt.next();
                    // note: some fields come from dbd, and hence have no assoc ioc resource
                    if (field.getIocResource() != null) {
                        String uriString = field.getIocResource().getUri().getUri();
                        dbFileHash.add(uriString);
                    }
                }
                // then convert the set to an ordered array
                dbFileArray = (String[])dbFileHash.toArray(new String[0]);
            } else {
                fieldArray = null;
                dbFileArray = null;
            }

            // request that field table update itself
            fieldListModel.fireTableDataChanged();

            // request that the table with list of db files update itself
            SelectedPVInfoDbTableModel dbTableModel = 
                (SelectedPVInfoDbTableModel)pvInfoDbTable.getModel();
            dbTableModel.fireTableDataChanged();
            
            break;
        }

        case PVModelEvent.NEW_LINK_LIST: {
            PVLinksTableModel tableModel = (PVLinksTableModel)linkList.getModel();
            // request that links table update itself
            tableModel.fireTableDataChanged();
            break;
        }

        case PVModelEvent.NEW_RECORD: {
            // kick off field and link searches
            document.actionFieldSearch();
            document.actionLinkSearch();
            document.actionClientSearch();
            break;
        }

        case PVModelEvent.NEW_CLIENT_LIST: {
            PVClientsTableModel tableModel = (PVClientsTableModel)clientList.getModel();
            // request that links table update itself
            tableModel.fireTableDataChanged();
            break;
        }

        case PVModelEvent.NEW_RECORD_BOOT_HISTORY_LIST: {
            PVBootHistoryTableModel pvBootHistoryTableModel = 
                (PVBootHistoryTableModel)bootHistoryList.getModel();
            pvBootHistoryTableModel.fireTableDataChanged();
            break;
        }
        default: {}
        }
            
    }

    /**
     * Invoked whenever a new system is chosen from system list. Updates the set of
     * IOC's viewed in the ioc list.
     *
     * @param systemListSelectedIndices array of indices for system list selection
     */
    public void updateIOCView(int[] systemListSelectedIndices) {
        List bootList = _model.getIocBootList();

        ArrayList systemChoices = new ArrayList();
        for (int i=0 ; i < systemListSelectedIndices.length ; i++) {
            int index = systemListSelectedIndices[i];
            String system = (String)systemListModel.elementAt(index);
            systemChoices.add(system);
        }


        // set the ioc list based on the system names chosen
        iocList.clearSelection();
        iocListModel.clear();

        // scan bootList, looking for iocs with system name in systemChoices
        Iterator bootIt = bootList.iterator();
        while (bootIt.hasNext()) {
            IOCBoot ib = (IOCBoot)bootIt.next();
            if (systemChoices.contains(ib.getIoc().getSystem())) {
                // add this ioc to ioc list 
                iocListModel.addElement(ib.getIoc().getIocName());
            }
        }

        if (!iocListModel.isEmpty())
            iocList.setSelectedIndex(0);

        searchPanel.validate();
    }

    /**
     * Invoked whenever a new ioc is chosen from ioc list. Updates the set of
     * DB files viewed in the db file list.
     *
     * @param iocListSelectedIndices array of indices for ioc list selection
     */
    public void updateDBFileView(int[] iocListSelectedIndices) {
        List bootList = _model.getIocBootList();

        // Fill db file list based on iocList selection(s).
        // Also save a parallel list of ioc resource id's
        // associated with the file names. I'll need those 
        // for search parameters later.

        dbFileListModel.clear();
        dbFileListResources.clear();

        // don't populate some lists if set of selected IOCs is large
        boolean tooManyIOCs = iocListSelectedIndices.length > 50;

        for (int i=0 ; i < iocListSelectedIndices.length ; i++) {
            int index = iocListSelectedIndices[i];
            String selectedIocName = (String)iocListModel.elementAt(index);
            // get IOCBoot for index using ioc name, not index itself
            // yes, i'm looping for it, but doesn't seem worth hash overhead
            Iterator bootIt = bootList.iterator();
            IOCBoot ib = null;
            while (bootIt.hasNext()) {
                IOCBoot candidateIb = (IOCBoot)bootIt.next();
                if (candidateIb.getIoc().getIocName().equals(selectedIocName))
                    ib = candidateIb;
            }
            
            Iterator irIt = ib.getIocResources().iterator();
            while (irIt.hasNext()) {
                IOCResource ir = (IOCResource)irIt.next();
                if (!tooManyIOCs) {
                    String fileName = "";
                    try {
                        URI uri = new URI(ir.getUri().getUri());
                        File file = new File(uri.getPath());
                        // get just trailing file name, not whole path
                        fileName = file.getName();
                    } catch (URISyntaxException use) {
                        fileName = "";
                    }
                    dbFileListModel.addElement(fileName);
                } 
                dbFileListResources.add(ir);
            }
        }
        if (tooManyIOCs)
            dbFileListModel.addElement("too many to list...");

        // make sure all items are selected initially
        dbFileList.addSelectionInterval(0,dbFileListModel.size()-1);
        allDbFileCheckBox.setSelected(true);
        searchPanel.validate();
    }

    /**
     * Loads chosen iocs into <code>PVSearchParameters</code> for use
     * by <code>PVDocument.actionPVSearch()</code>.
     */
    public void updateIocsInSearchParams() {
        pvSearchParams.setIocBootList(new ArrayList());
        if (!historyMode) {
            List bootList = _model.getIocBootList();
            int[] iocListSelectedIndices = iocList.getSelectedIndices();
            for (int i=0 ; i < iocListSelectedIndices.length ; i++) {
                int index = iocListSelectedIndices[i];
                String selectedIocName = (String)iocListModel.elementAt(index);
                // get IOCBoot for index using ioc name, not index itself
                // yes, i'm looping for it, but doesn't seem worth hash overhead
                Iterator bootIt = bootList.iterator();
                IOCBoot ib = null;
                while (bootIt.hasNext()) {
                    IOCBoot candidateIb = (IOCBoot)bootIt.next();
                    if (candidateIb.getIoc().getIocName().equals(selectedIocName))
                        ib = candidateIb;
                }
                
                // add this ioc boot instance to pvSearchParams
                pvSearchParams.addIocBoot(ib);                
            }
        }
    }

    /**
     * Loads chosen db files into <code>PVSearchParameters</code> for use
     * by <code>PVDocument.actionPVSearch()</code>.
     */
    public void updateDbFilesInSearchParams() {
        pvSearchParams.setIocResourceList(new ArrayList());
        if (!allDbFileCheckBox.isSelected()) {
            int[] dbFileListSelectedIndices = dbFileList.getSelectedIndices();
            for (int i=0 ; i < dbFileListSelectedIndices.length ; i++) {
                int index = dbFileListSelectedIndices[i];
                IOCResource ir = (IOCResource)dbFileListResources.get(index);
                pvSearchParams.addIocResource(ir);
            }
        }
    }

    /**
     * Loads chosen record types into <code>PVSearchParameters</code> for use
     * by <code>PVDocument.actionPVSearch()</code>.
     */
    public void updateRecordTypeInSearchParams() {
        pvSearchParams.setRecordTypeList(new ArrayList());
        List recordTypeList = _model.getRecordTypeList();
        if (!allRecTypeCheckBox.isSelected()) {
            int[] recTypeListSelectedIndices = recTypeList.getSelectedIndices();
            for (int i=0 ; i < recTypeListSelectedIndices.length ; i++) {
                int index = recTypeListSelectedIndices[i];
                RecordType rt = (RecordType)recordTypeList.get(index);
                pvSearchParams.addRecordType(rt);
            }
        }
    }


    /********************************************************************
     * Inner classes supporting Swing components
     ********************************************************************/

    /**
     * Provides methods to get pv results table cell data from
     * actual <code>PVModel</code>. Normally uses the recordList
     * from the model, but if we are in history mode, then use
     * the uniqueRecordMap from the model instead.
     */
    class PVResultsTableModel extends AbstractTableModel {

        public int getColumnCount() {
            return columnChoices.getColumnNames().length;
        }
        public int getRowCount() {
            int size = 0;
            if (_model.getRecordList() == null)
                return size;

            if (historyMode && uniqueRecordList != null) {
                    size = uniqueRecordList.size();
            } else {
                    size = _model.getRecordList().size();
            }
            return size;
        }
        public String getColumnName(int col) {
            return columnChoices.getColumnNames()[col];
        }
        public Object getValueAt(int row, int col) {
            List recordList = null;
            if (historyMode) {
                recordList = uniqueRecordList;
            } else {
                recordList = _model.getRecordList();
            }
            if (recordList != null) {            
                Record rec = (Record)recordList.get(row);
                String colName = columnChoices.getColumnNames()[col];
                if (colName.equals("Record Name")) {
                    return rec.getRecordName();

                } else if (colName.equals("Type")) {
                    return rec.getRecordType().getRecordType();

                } else if (colName.equals("Ioc")) {
                    return rec.getIocBoot().getIoc().getIocName();
                    
                } else if (colName.equals("System")) {
                    return rec.getIocBoot().getIoc().getSystem();

                } else {
                    // search for field data by colName
                    Field field = rec.findField(colName);
                    if (field != null)
                        return field.getFieldValue();
                    else 
                        return "n/a";
                }
            } else {
                return null;
            }
        }
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }

    /**
     * Provides methods to get ioc boot history info for dipslay.
     */
    class PVBootHistoryTableModel extends AbstractTableModel {
        private String[] columnNames = {"IOC","Boot Date"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            List selectedRecordBootHistory = _model.getSelectedRecordBootHistory();
            if (selectedRecordBootHistory != null) {
                size = selectedRecordBootHistory.size();
            }
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            List bootHistory = _model.getSelectedRecordBootHistory();
            if (bootHistory != null) {            
                IOCBoot boot = (IOCBoot)bootHistory.get(row);
                switch(col) {
                case 0: {
                    return boot.getIoc().getIocName();
                }
                case 1: {
                    if (boot.getIocBootDate() == null)
                        return "Unable to crawl IOC, rescan scheduled";
                    else
                        return boot.getIocBootDate().toString();
                }
                default: {
                    return "   ";
                }
                }
            } else {
                return "     ";
            }
        }
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }

    /**
     * Provides methods to get miscellaneous pv info table cell data from
     * actual <code>PVModel</code>. See Swing tutorial.
     */
    class SelectedPVInfoTableModel extends AbstractTableModel {
        private String[] columnNames = {"Record Name","Type","IOC"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            return 1;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            Record rec = _model.getSelectedRecord();
            if (rec != null) {            
                switch(col) {
                case 0: {
                    return rec.getRecordName();
                }
                case 1: {
                    return rec.getRecordType().getRecordType();
                }
                case 2: {
                    return rec.getIocBoot().getIoc().getIocName();
                }
                default: {
                    return null;
                }
                }
            } else {
                return "     ";
            }
        }
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }

    /**
     * Provides methods to get db file(s) associated with a given pv from
     * <code>PVModel</code>. See Swing tutorial.
     */
    class SelectedPVInfoDbTableModel extends AbstractTableModel {
        
        private String[] columnNames = {"DB File(s)"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            if (dbFileArray != null)
                return dbFileArray.length;
            else
                return 1;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            if (dbFileArray != null) {
                return dbFileArray[row];
            } else {
                return "   ";
            }
        }
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }

    /**
     * Provides methods to get pv field table cell data from actual
     * <code>PVModel</code>. See Swing tutorial.
     */
    class PVFieldsTableModel extends AbstractTableModel {
        private String[] columnNames = {"Field","Value"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            if (fieldArray != null) {
                return fieldArray.length;
            } else {
                return 0;
            }
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            if (fieldArray != null) {            
                Field field = fieldArray[row];
                if (col == 0) {
                    return field.getFieldType().getFieldType();
                } else {
                    return field.getFieldValue();
                }
            } else {
                return null;
            }
        }
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }

    /**
     * Provides methods to get pv links table cell data from actual
     * <code>PVModel</code>. See Swing tutorial.
     */    
    class PVLinksTableModel extends AbstractTableModel {
        private String[] columnNames = {"Used By","Field","Type","With Field Value"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            List linkList = _model.getLinkList();
            if (linkList != null) {
                return linkList.size();
            } else {
                return 0;
            }
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            List linkList = _model.getLinkList();
            if (linkList != null) {            
                Field field = (Field)linkList.get(row);
                switch (col) {
                case 0: {return field.getRecord().getRecordName();}
                case 1: {return field.getFieldType().getFieldType();}
                case 2: {return field.getRecord().getRecordType().getRecordType();}
                case 3: {return field.getFieldValue();}
                default: {return null;}
                }
            } else {
                return null;
            }
        }
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }

    /**
     * Provides methods to get pv clients table cell data from actual
     * <code>PVModel</code>. See Swing tutorial.
     */    
    class PVClientsTableModel extends AbstractTableModel {
        private String[] columnNames = {"App","Field","File","Extra Info"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            List clientList = _model.getClientList();
            if (clientList != null) {
                return clientList.size();
            } else {
                return 0;
            }
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            List clientList = _model.getClientList();
            if (clientList != null) {            
                RecordClient rc = (RecordClient)clientList.get(row);
                // note: redo this to use actual type table description
                String clientType = "unknown";
                switch (rc.getClientTypeId().intValue()) {
                case 1: { clientType = "MEDM"; break; }
                case 2: { clientType = "ALH"; break; }
                case 3: { clientType = "Save/Restore"; break; }
                case 4: { clientType = "Sequencer"; break; }
                case 5: { clientType = "sddslogger"; break; }
                case 6: { clientType = "PEM"; break; }
                default: { clientType = "unknown"; break; }
                }

                // assume just one edge per VURI
                VURIRelationship vr = null;
                Iterator edgesIt = rc.getVuri().getEdges().iterator();
                if (edgesIt.hasNext())
                    vr = (VURIRelationship)edgesIt.next();
                String extraInfo = "none";
                if (vr != null)
                    extraInfo = vr.getRelationshipInfo();
                        
                switch (col) {
                case 0: {return clientType;}
                case 1: {return rc.getFieldType();}
                case 2: {return rc.getVuri().getUri().getUri();}
                case 3: {return extraInfo;}
                default: {return null;}
                }
            } else {
                return null;
            }
        }
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }

    /**
     * Customized table cell renderer used for pv fields table. It colors
     * the cell background and text foreground based on what kind of field
     * it is. Also provides tool-tip text to explain colors.
     */
    class PVFieldsTableCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent (JTable table,
                                                        Object value,
                                                        boolean selected,
                                                        boolean focused,
                                                        int row, int column) {
            setEnabled(table==null || table.isEnabled());
            if (column == 1) {
                if (fieldArray != null) {
                    Field field = fieldArray[row];
                    if (field.getFieldState() == Field.OVERWRITTEN) {
                        setBackground(new Color(0xFF,0xFF,0xCC));  // pale yellow
                        setToolTipText("Overwritten");
                    } else if (field.getFieldState() == Field.USER_DEFINED) {
                        setBackground(new Color(0xAD,0xD8,0xE6));  // pale blue
                        setToolTipText("User Defined");
                    } else {
                        setBackground(Color.white);
                        setToolTipText("Default");
                    }
                    
                    // highlight clickable links a certain way
                    String dbdType = field.getFieldType().getDbdType();
                    if ((dbdType.equals("DBF_INLINK") ||
                        dbdType.equals("DBF_OUTLINK") ||
                        dbdType.equals("DBF_FWDLINK")) &&
                        PVUtils.isPurePV(field.getFieldValue())) {
                        setForeground(Color.blue);
                    } else {
                        setForeground(Color.black);
                    }
                }
            }
            super.getTableCellRendererComponent(table,value,selected,focused,row,column);
            return this;
        }
    }

    /**
     * Customized table cell renderer used for pv links table. It colors
     * the cell text foreground for the first column.
     */
    class PVLinksTableCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent (JTable table,
                                                        Object value,
                                                        boolean selected,
                                                        boolean focused,
                                                        int row, int column) {
            setEnabled(table==null || table.isEnabled());
            if (column == 0) {
                setToolTipText("Click to find PV");
                setForeground(Color.blue);
            }
            super.getTableCellRendererComponent(table,value,selected,focused,row,column);
            return this;
        }
    }

    /**
     * Customized table cell renderer used for pv info table. It colors
     * the cell text foreground for the third (ioc) column.
     */
    class SelectedPVInfoTableCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent (JTable table,
                                                        Object value,
                                                        boolean selected,
                                                        boolean focused,
                                                        int row, int column) {
            setEnabled(table==null || table.isEnabled());
            if (column == 2) {
                setToolTipText("Click to get details on this IOC");
                setForeground(Color.blue);
            }
            super.getTableCellRendererComponent(table,value,selected,focused,row,column);
            return this;
        }
    }


    class PVClientsTableCellRenderer 
        implements TableCellRenderer {
        
        public Component getTableCellRendererComponent(JTable table, 
                                                       Object value, 
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row, int col) {
            JTextArea textArea = new JTextArea();
            // modify properties of text area here
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(false);

            if (value != null) {
                textArea.setText(value.toString());
                if (col == 3)
                    textArea.setToolTipText(value.toString());
            }

            // autosize the row height to fit text exactly
            if (col == 2) {
                textArea.setSize(table.getColumnModel().getColumn(col).getWidth(), Integer.MAX_VALUE);
                int desiredHeight = (int) textArea.getPreferredSize().getHeight();
                if (desiredHeight > table.getRowHeight(row))
                    table.setRowHeight(row,desiredHeight);
            }
            
            return textArea;
        }
    }

    /**
     * Customized table cell renderer used for pv boot history table. It colors
     * the cell background based on whether pv was in boot or not, and when
     * it dissappeared.
     */
    class PVBootHistoryTableCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent (JTable table,
                                                        Object value,
                                                        boolean selected,
                                                        boolean focused,
                                                        int row, int column) {
            setEnabled(table==null || table.isEnabled());
            List selectedRecordBootList = _model.getSelectedRecordBootHistory();
            if (selectedRecordBootList != null) {
                IOCBoot boot = (IOCBoot)selectedRecordBootList.get(row);
                if (!boot.getParticipates()) {
                    setBackground(Color.red);
                    setToolTipText("Record no longer in this IOC");
                } else {
                    setBackground(Color.white);
                    setToolTipText(null);
                }
            }
            super.getTableCellRendererComponent(table,value,selected,focused,row,column);
            return this;
        }
    }

    public class PrevPVAction extends AbstractAction {

        public PrevPVAction() { 
            super();
            ImageIcon icon = new ImageIcon(AppsUtil.getImageURL("Back16.gif"));
            putValue("SmallIcon",icon);
            putValue("ShortDescription","Go back to previously selected PV.");
        }
        
        public void actionPerformed(ActionEvent e) {
            System.out.println("go to prev pv");

        }
    }

    public class NextPVAction extends AbstractAction {

        public NextPVAction() { 
            super();
            ImageIcon icon = new ImageIcon(AppsUtil.getImageURL("Forward16.gif"));
            putValue("SmallIcon",icon);
            putValue("ShortDescription","Go forward to next PV.");
        }
        
        public void actionPerformed(ActionEvent e) {
            System.out.println("go to next pv");
        }
    }
}
