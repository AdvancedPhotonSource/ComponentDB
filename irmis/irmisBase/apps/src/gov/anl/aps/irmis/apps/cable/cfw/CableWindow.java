/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.cable.cfw;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.logging.*;
import java.util.EventObject;
import java.util.Enumeration;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import gov.sns.application.*;

// jwizz wizard toolkit
import net.javaprog.ui.wizard.*;

// other IRMIS sub-applications
import gov.anl.aps.irmis.apps.component.cfw.ComponentDocument;

// persistence layer
import gov.anl.aps.irmis.persistence.DAOStaleObjectStateException;
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentPort;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationship;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.component.Cable;
import gov.anl.aps.irmis.persistence.login.RoleName;

// for registering protected gui components
import gov.anl.aps.irmis.login.LoginUtil;
import gov.anl.aps.irmis.login.RolePrincipal;

// service layer
import gov.anl.aps.irmis.service.component.ComponentService;
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.IRMISException;

// application helpers
import gov.anl.aps.irmis.apps.AbstractTreeModel;
import gov.anl.aps.irmis.apps.AppsUtil;
import gov.anl.aps.irmis.apps.irmis.cfw.Main;

// wizards
import gov.anl.aps.irmis.apps.component.cfw.wizard.*;

/**
 * Primary GUI for IRMIS Cable Viewer application. All window layout, and Swing event
 * listeners are done here. Business logic work is delegated to <code>CableDocument</code>,
 * which in turn requests that the CableModel notify us here of changes to the data. In short,
 * we listen for <code>CableModelEvent</code> here.
 */
public class CableWindow extends XalInternalWindow {

	/** The main model for the document */
	final protected CableModel _model;

    final protected CableDocument document;

    static RolePrincipal[] editPrincipal = { new RolePrincipal(RoleName.CABLE_EDITOR) };

    // Hold reference to parent desktop frame. Used to put up busy icon during work.
    JFrame _appFrame;    

	// Swing GUI components
    private JPanel topPanel;

    private JTree focusedTree; // which tree was just clicked on

    // comp 1
    private JPanel comp1Panel;
    // comp 1 housing
    private JPanel housing1Panel;
    private JTree housing1Tree;
    private JPanel housing1NavPanel;
    private JButton housing1StopButton;
    private JButton housing1FindButton;
    private JButton housing1PrevButton;
    private JButton housing1NextButton;
    private JButton housing1UpButton;
    private JButton housing1DownButton;
    private JTextField housing1SearchField;
    private FilteredHousingComponentPortTreeModel housing1TreeModel;
    private ComponentPortTreeFindController housing1FindController;

    // comp 1 control
    private JPanel control1Panel;
    private JTree control1Tree;
    private JPanel control1NavPanel;
    private JButton control1StopButton;
    private JButton control1FindButton;
    private JButton control1PrevButton;
    private JButton control1NextButton;
    private JButton control1UpButton;
    private JButton control1DownButton;
    private JTextField control1SearchField;
    private FilteredControlComponentPortTreeModel control1TreeModel;
    private ComponentPortTreeFindController control1FindController;

    // cable inter-connect 
    private JPanel connectPanel;
    private JTable cableList;
    private JButton cableRemoveButton;
    private JButton cableSaveButton;
    private JButton cableUndoButton;
    private JLabel cableSearchLabel;
    private JTextField cableSearchField;
    private boolean labelSearchDone = false;

    // comp 2
    private JPanel comp2Panel;

    // comp 2 housing
    private JPanel housing2Panel;
    private JTree housing2Tree;
    private JPanel housing2NavPanel;
    private JButton housing2StopButton;
    private JButton housing2FindButton;
    private JButton housing2PrevButton;
    private JButton housing2NextButton;
    private JButton housing2UpButton;
    private JButton housing2DownButton;
    private JTextField housing2SearchField;
    private FilteredHousingComponentPortTreeModel housing2TreeModel;
    private ComponentPortTreeFindController housing2FindController;

    // comp 2 control
    private JPanel control2Panel;
    private JTree control2Tree;
    private JPanel control2NavPanel;
    private JButton control2StopButton;
    private JButton control2FindButton;
    private JButton control2PrevButton;
    private JButton control2NextButton;
    private JButton control2UpButton;
    private JButton control2DownButton;
    private JTextField control2SearchField;
    private FilteredControlComponentPortTreeModel control2TreeModel;
    private ComponentPortTreeFindController control2FindController;
	
	/** 
	 * Creates a new instance of CableWindow
	 * @param aDocument The document for this window
	 */
    public CableWindow(final XalInternalDocument aDocument) {
        super(aDocument);

        document = (CableDocument)aDocument;
		_model = document.getModel();

        // make CableWindow a listener for changes in CableModel
		_model.addCableModelListener( new CableModelListener() {
                public void modified(CableModelEvent e) {
                    updateView(e);
                }
            });

       _appFrame = ((DesktopApplication)Application.getApp()).getDesktopFrame();

        // initial application window size
        setSize(700, 500);

        // build contents
		makeContents();
    }

    /**
     * Top-level method to build up Swing GUI components.
     */
    public void makeContents() {

        // re-usable configurations
        Dimension smallButtonDim = new Dimension(20,20);
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        // topmost organization 
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.PAGE_AXIS));

        // clicks anywhere in this window should stop any editing operation
        MouseListener stopEditingMouseListener = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (cableList.getCellEditor() != null)
                        cableList.getCellEditor().stopCellEditing();
                }
            };
        topPanel.addMouseListener(stopEditingMouseListener);

        // component 1 panel
        comp1Panel = new JPanel();
        comp1Panel.setLayout(new BoxLayout(comp1Panel,BoxLayout.LINE_AXIS));
        Border throwAway1Title = BorderFactory.createTitledBorder("");  // why do i have to do this??
        Border comp1Title = BorderFactory.createTitledBorder(throwAway1Title, "Component A",
                                                             TitledBorder.CENTER, TitledBorder.TOP);
        comp1Panel.setBorder(comp1Title);

        /***********************************************
         **************** CONTROL TREE *****************
         ***********************************************/
        final int controlRelationshipType = ComponentRelationshipType.CONTROL;
        control1Panel = new JPanel();
        control1Panel.setLayout(new BoxLayout(control1Panel,BoxLayout.PAGE_AXIS));
        TitledBorder control1Title = BorderFactory.createTitledBorder("Control");
        control1Panel.setBorder(control1Title);
        control1TreeModel = new FilteredControlComponentPortTreeModel();
        control1Tree = new JTree(control1TreeModel);
        control1Tree.setModel(control1TreeModel);
        control1Tree.setCellRenderer(new ComponentPortTreeCellRenderer(controlRelationshipType));

        // nav
        control1NavPanel = new JPanel();
        control1NavPanel.setLayout(new BoxLayout(control1NavPanel, BoxLayout.LINE_AXIS));
        control1NavPanel.setMinimumSize(new Dimension(140,25));
        control1NavPanel.setPreferredSize(new Dimension(140,25));
        control1NavPanel.setMaximumSize(new Dimension(500,25));
        control1StopButton = new JButton();
        control1StopButton.setMaximumSize(smallButtonDim);
        control1StopButton.setPreferredSize(smallButtonDim);
        control1FindButton = new JButton();
        control1FindButton.setMaximumSize(smallButtonDim);
        control1FindButton.setPreferredSize(smallButtonDim);
        control1PrevButton = new JButton();
        control1PrevButton.setMaximumSize(smallButtonDim);
        control1PrevButton.setPreferredSize(smallButtonDim);
        control1NextButton = new JButton();
        control1NextButton.setMaximumSize(smallButtonDim);
        control1NextButton.setPreferredSize(smallButtonDim);
        control1SearchField = new JTextField();
        control1SearchField.setMaximumSize(new Dimension(100,20));
        control1NavPanel.add(control1StopButton);
        control1NavPanel.add(control1SearchField);
        control1NavPanel.add(control1PrevButton);
        control1NavPanel.add(control1NextButton);
        control1NavPanel.add(control1FindButton);
        control1Panel.add(control1NavPanel);
        control1FindController = 
            new ComponentPortTreeFindController(_appFrame, control1Tree, controlRelationshipType, 1,
                                                _model, control1StopButton, control1FindButton,
                                                control1PrevButton, control1NextButton,
                                                control1SearchField);

        // scrolled tree
        JScrollPane control1TreeScroller = 
            new JScrollPane(control1Tree, vsbp, hsbp);
        control1Panel.add(control1TreeScroller);

        control1Tree.setVisibleRowCount(15);
        control1Tree.setRootVisible(true);

        // we want both a TreeSelectionListener and a MouseListener
        control1Tree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath leadPath = e.getNewLeadSelectionPath();
                    if (leadPath != null) {
                        Object node = leadPath.getLastPathComponent();
                        focusedTree = control1Tree;
                        // do selection work, updating other tree to match
                        setHierarchySelection(node, false, true, controlRelationshipType, 1,
                                              control1Tree, housing1Tree);
                    }
                }
            });
        MouseListener control1MouseListener = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    TreePath leadPath = 
                        control1Tree.getPathForLocation(e.getX(), e.getY());
                    if (leadPath != null) {
                        Object node = leadPath.getLastPathComponent();
                        focusedTree = control1Tree;
                        if (e.getClickCount() == 1) {
                            // do selection work, updating other tree to match
                            setHierarchySelection(node, false, false, controlRelationshipType, 1,
                                                  control1Tree, housing1Tree);
                        } else if (e.getClickCount() == 2) {
                            // do selection work, updating other tree to match
                            setHierarchySelection(node, true, false, controlRelationshipType, 1,
                                                  control1Tree, housing1Tree);
                        }
                    }
                }
            };
        control1Tree.addMouseListener(control1MouseListener);

        ComponentPortPopupMenu control1PopupMenu = 
            new ComponentPortPopupMenu(1, control1FindController);
        control1Tree.addMouseListener(new PopupTrigger(control1Tree, housing1Tree, control1PopupMenu));

        /***********************************************
         **************** HOUSING TREE *****************
         ***********************************************/
        final int housingRelationshipType = ComponentRelationshipType.HOUSING;
        housing1Panel = new JPanel();
        housing1Panel.setLayout(new BoxLayout(housing1Panel,BoxLayout.PAGE_AXIS));
        TitledBorder housing1Title = BorderFactory.createTitledBorder("Housing");
        housing1Panel.setBorder(housing1Title);
        housing1TreeModel = new FilteredHousingComponentPortTreeModel();
        housing1Tree = new JTree(housing1TreeModel);
        housing1Tree.setCellRenderer(new ComponentPortTreeCellRenderer(housingRelationshipType));

        // nav
        housing1NavPanel = new JPanel();
        housing1NavPanel.setLayout(new BoxLayout(housing1NavPanel, BoxLayout.LINE_AXIS));
        housing1NavPanel.setMinimumSize(new Dimension(140,25));
        housing1NavPanel.setPreferredSize(new Dimension(140,25));
        housing1NavPanel.setMaximumSize(new Dimension(500,25));
        housing1StopButton = new JButton();
        housing1StopButton.setMaximumSize(smallButtonDim);
        housing1StopButton.setPreferredSize(smallButtonDim);
        housing1FindButton = new JButton();
        housing1FindButton.setMaximumSize(smallButtonDim);
        housing1FindButton.setPreferredSize(smallButtonDim);
        housing1PrevButton = new JButton();
        housing1PrevButton.setMaximumSize(smallButtonDim);
        housing1PrevButton.setPreferredSize(smallButtonDim);
        housing1NextButton = new JButton();
        housing1NextButton.setMaximumSize(smallButtonDim);
        housing1NextButton.setPreferredSize(smallButtonDim);
        housing1SearchField = new JTextField();
        housing1SearchField.setMaximumSize(new Dimension(100,20));
        housing1NavPanel.add(housing1StopButton);
        housing1NavPanel.add(housing1SearchField);
        housing1NavPanel.add(housing1PrevButton);
        housing1NavPanel.add(housing1NextButton);
        housing1NavPanel.add(housing1FindButton);
        housing1Panel.add(housing1NavPanel);
        housing1FindController = 
            new ComponentPortTreeFindController(_appFrame, housing1Tree, housingRelationshipType, 1,
                                                _model, housing1StopButton, housing1FindButton,
                                                housing1PrevButton, housing1NextButton,
                                                housing1SearchField);

        // scrolled tree
        JScrollPane housing1TreeScroller = 
            new JScrollPane(housing1Tree, vsbp, hsbp);
        housing1Panel.add(housing1TreeScroller);

        housing1Tree.setVisibleRowCount(15);
        housing1Tree.setRootVisible(true);

        // we want both a TreeSelectionListener and a MouseListener
        housing1Tree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath leadPath = e.getNewLeadSelectionPath();
                    if (leadPath != null) {
                        Object node = leadPath.getLastPathComponent();
                        focusedTree = housing1Tree;
                        // do selection work, updating other tree to match
                        setHierarchySelection(node, false, true, housingRelationshipType, 1,
                                              housing1Tree, control1Tree);
                    }
                }
            });
        MouseListener housing1MouseListener = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    TreePath leadPath = 
                        housing1Tree.getPathForLocation(e.getX(), e.getY());
                    if (leadPath != null) {
                        Object node = leadPath.getLastPathComponent();
                        focusedTree = housing1Tree;
                        if (e.getClickCount() == 1) {
                            // do selection work, updating other tree to match
                            setHierarchySelection(node, false, false, housingRelationshipType, 1,
                                                  housing1Tree,control1Tree);
                            
                        } else if (e.getClickCount() == 2) {
                            // do selection work, updating other tree to match
                            setHierarchySelection(node, true, false, housingRelationshipType, 1,
                                                  housing1Tree,control1Tree);
                        }
                    }
                }
            };
        housing1Tree.addMouseListener(housing1MouseListener);

        ComponentPortPopupMenu housing1PopupMenu = 
            new ComponentPortPopupMenu(1, housing1FindController);
        housing1Tree.addMouseListener(new PopupTrigger(housing1Tree, control1Tree, housing1PopupMenu));

        comp1Panel.add(control1Panel);
        comp1Panel.add(housing1Panel);

        
        // component 2 panel
        comp2Panel = new JPanel();
        comp2Panel.setLayout(new BoxLayout(comp2Panel,BoxLayout.LINE_AXIS));
        Border throwAway2Title = BorderFactory.createTitledBorder("");  // why do i have to do this??
        Border comp2Title = BorderFactory.createTitledBorder(throwAway2Title, "Component B",
                                                             TitledBorder.CENTER, TitledBorder.TOP);
        comp2Panel.setBorder(comp2Title);

        /***********************************************
         **************** CONTROL TREE *****************
         ***********************************************/
        control2Panel = new JPanel();
        control2Panel.setLayout(new BoxLayout(control2Panel,BoxLayout.PAGE_AXIS));
        TitledBorder control2Title = BorderFactory.createTitledBorder("Control");
        control2Panel.setBorder(control2Title);
        control2TreeModel = new FilteredControlComponentPortTreeModel();
        control2Tree = new JTree(control2TreeModel);
        control2Tree.setModel(control2TreeModel);
        control2Tree.setCellRenderer(new ComponentPortTreeCellRenderer(controlRelationshipType));

        // nav
        control2NavPanel = new JPanel();
        control2NavPanel.setLayout(new BoxLayout(control2NavPanel, BoxLayout.LINE_AXIS));
        control2NavPanel.setMinimumSize(new Dimension(140,25));
        control2NavPanel.setPreferredSize(new Dimension(140,25));
        control2NavPanel.setMaximumSize(new Dimension(500,25));
        control2StopButton = new JButton();
        control2StopButton.setMaximumSize(smallButtonDim);
        control2StopButton.setPreferredSize(smallButtonDim);
        control2FindButton = new JButton();
        control2FindButton.setMaximumSize(smallButtonDim);
        control2FindButton.setPreferredSize(smallButtonDim);
        control2PrevButton = new JButton();
        control2PrevButton.setMaximumSize(smallButtonDim);
        control2PrevButton.setPreferredSize(smallButtonDim);
        control2NextButton = new JButton();
        control2NextButton.setMaximumSize(smallButtonDim);
        control2NextButton.setPreferredSize(smallButtonDim);
        control2SearchField = new JTextField();
        control2SearchField.setMaximumSize(new Dimension(100,20));
        control2NavPanel.add(control2StopButton);
        control2NavPanel.add(control2SearchField);
        control2NavPanel.add(control2PrevButton);
        control2NavPanel.add(control2NextButton);
        control2NavPanel.add(control2FindButton);
        control2Panel.add(control2NavPanel);
        control2FindController = 
            new ComponentPortTreeFindController(_appFrame, control2Tree, controlRelationshipType, 2,
                                                _model, control2StopButton, control2FindButton,
                                                control2PrevButton, control2NextButton,
                                                control2SearchField);

        // scrolled tree
        JScrollPane control2TreeScroller = 
            new JScrollPane(control2Tree, vsbp, hsbp);
        control2Panel.add(control2TreeScroller);

        control2Tree.setVisibleRowCount(15);
        control2Tree.setRootVisible(true);

        // we want both a TreeSelectionListener and a MouseListener
        control2Tree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath leadPath = e.getNewLeadSelectionPath();
                    if (leadPath != null) {
                        Object node = leadPath.getLastPathComponent();
                        focusedTree = control2Tree;
                        // do selection work, updating other tree to match
                        setHierarchySelection(node, false, true, controlRelationshipType, 2,
                                              control2Tree, housing2Tree);
                    }
                }
            });
        MouseListener control2MouseListener = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    TreePath leadPath = 
                        control2Tree.getPathForLocation(e.getX(), e.getY());
                    if (leadPath != null) {
                        Object node = leadPath.getLastPathComponent();
                        focusedTree = control2Tree;
                        if (e.getClickCount() == 1) {
                            // do selection work, updating other tree to match
                            setHierarchySelection(node, false, false, controlRelationshipType, 2,
                                                  control2Tree, housing2Tree);
                            
                        } else if (e.getClickCount() == 2) {
                            // do selection work, updating other tree to match
                            setHierarchySelection(node, true, false, controlRelationshipType, 2,
                                                  control2Tree, housing2Tree);                            
                        }
                    }
                }
            };
        control2Tree.addMouseListener(control2MouseListener);
        ComponentPortPopupMenu control2PopupMenu = 
            new ComponentPortPopupMenu(2, control2FindController);
        control2Tree.addMouseListener(new PopupTrigger(control2Tree, housing2Tree, control2PopupMenu));


        /***********************************************
         **************** HOUSING TREE *****************
         ***********************************************/
        housing2Panel = new JPanel();
        housing2Panel.setLayout(new BoxLayout(housing2Panel,BoxLayout.PAGE_AXIS));
        TitledBorder housing2Title = BorderFactory.createTitledBorder("Housing");
        housing2Panel.setBorder(housing2Title);
        housing2TreeModel = new FilteredHousingComponentPortTreeModel();
        housing2Tree = new JTree(housing2TreeModel);
        housing2Tree.setCellRenderer(new ComponentPortTreeCellRenderer(housingRelationshipType));

        // nav
        housing2NavPanel = new JPanel();
        housing2NavPanel.setLayout(new BoxLayout(housing2NavPanel, BoxLayout.LINE_AXIS));
        housing2NavPanel.setMinimumSize(new Dimension(140,25));
        housing2NavPanel.setPreferredSize(new Dimension(140,25));
        housing2NavPanel.setMaximumSize(new Dimension(500,25));
        housing2StopButton = new JButton();
        housing2StopButton.setMaximumSize(smallButtonDim);
        housing2StopButton.setPreferredSize(smallButtonDim);
        housing2FindButton = new JButton();
        housing2FindButton.setMaximumSize(smallButtonDim);
        housing2FindButton.setPreferredSize(smallButtonDim);
        housing2PrevButton = new JButton();
        housing2PrevButton.setMaximumSize(smallButtonDim);
        housing2PrevButton.setPreferredSize(smallButtonDim);
        housing2NextButton = new JButton();
        housing2NextButton.setMaximumSize(smallButtonDim);
        housing2NextButton.setPreferredSize(smallButtonDim);
        housing2SearchField = new JTextField();
        housing2SearchField.setMaximumSize(new Dimension(100,20));
        housing2NavPanel.add(housing2StopButton);
        housing2NavPanel.add(housing2SearchField);
        housing2NavPanel.add(housing2PrevButton);
        housing2NavPanel.add(housing2NextButton);
        housing2NavPanel.add(housing2FindButton);
        housing2Panel.add(housing2NavPanel);
        housing2FindController = 
            new ComponentPortTreeFindController(_appFrame, housing2Tree, housingRelationshipType, 2,
                                                _model, housing2StopButton, housing2FindButton,
                                                housing2PrevButton, housing2NextButton,
                                                housing2SearchField);

        // scrolled tree
        JScrollPane housing2TreeScroller = 
            new JScrollPane(housing2Tree, vsbp, hsbp);
        housing2Panel.add(housing2TreeScroller);

        housing2Tree.setVisibleRowCount(15);
        housing2Tree.setRootVisible(true);

        // we want both a TreeSelectionListener and a MouseListener
        housing2Tree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath leadPath = e.getNewLeadSelectionPath();
                    if (leadPath != null) {
                        Object node = leadPath.getLastPathComponent();
                        focusedTree = housing2Tree;
                        // do selection work, updating other tree to match
                        setHierarchySelection(node, false, true, housingRelationshipType, 2,
                                              housing2Tree, control2Tree);
                    }
                }
            });
        MouseListener housing2MouseListener = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    TreePath leadPath = 
                        housing2Tree.getPathForLocation(e.getX(), e.getY());
                    if (leadPath != null) {
                        Object node = leadPath.getLastPathComponent();
                        focusedTree = housing2Tree;
                        if (e.getClickCount() == 1) {
                            // do selection work, updating other tree to match
                            setHierarchySelection(node, false, false, housingRelationshipType, 2,
                                                  housing2Tree,control2Tree);
                            
                        } else if (e.getClickCount() == 2) {
                            // do selection work, updating other tree to match
                            setHierarchySelection(node, true, false, housingRelationshipType, 2,
                                                  housing2Tree,control2Tree);
                        }
                    }
                }
            };
        housing2Tree.addMouseListener(housing2MouseListener);
        ComponentPortPopupMenu housing2PopupMenu = 
            new ComponentPortPopupMenu(2, housing2FindController);
        housing2Tree.addMouseListener(new PopupTrigger(housing2Tree, control2Tree, housing2PopupMenu));

        comp2Panel.add(housing2Panel);
        comp2Panel.add(control2Panel);

        // put jsplitpane between component A(1) and component B(2)
        JSplitPane abSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                comp1Panel, comp2Panel);
        abSplitPane.setResizeWeight(0.5);

        JPanel splitHolderPanel = new JPanel();
        splitHolderPanel.setLayout(new BoxLayout(splitHolderPanel,BoxLayout.LINE_AXIS));
        splitHolderPanel.add(abSplitPane);

        // add split pane and cable pane to topPanel
        topPanel.add(splitHolderPanel);

        // component connect panel
        connectPanel = new JPanel();
        Border connectTitle = BorderFactory.createTitledBorder("");
        Border centeredTitle = BorderFactory.createTitledBorder(connectTitle, "Cable Interconnect",
                                                                TitledBorder.CENTER, TitledBorder.TOP);
        connectPanel.setBorder(centeredTitle);
        connectPanel.setLayout(new BoxLayout(connectPanel,BoxLayout.PAGE_AXIS));

        // cable table
        cableList = new JTable(new CableTableModel());
        cableList.setShowHorizontalLines(true);
        cableList.setRowSelectionAllowed(true);
        cableList.setPreferredScrollableViewportSize(new Dimension(120,45));
        cableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cableList.getColumnModel().getColumn(0).setCellEditor(new CableLabelTableCellEditor());
        cableList.getColumnModel().getColumn(1).setCellEditor(new CableColorTableCellEditor());
        cableList.getColumnModel().getColumn(4).setCellEditor(new CableDestDescTableCellEditor());
        cableList.getColumnModel().getColumn(2).setMaxWidth(50);
        cableList.getColumnModel().getColumn(2).setPreferredWidth(50);        
        cableList.getColumnModel().getColumn(3).setMaxWidth(83);
        cableList.getColumnModel().getColumn(3).setPreferredWidth(83);        

        
        // handle row selection in cable table
        cableList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int selectedCableRow = lsm.getMinSelectionIndex();
                        List modelCableList = _model.getCables();
                        Cable selectedCable = 
                            (Cable)modelCableList.get(selectedCableRow);

                        //System.out.println("selecting from cable table no. "+selectedCableRow);
                        setCableSelection(selectedCable);
                    }
                }
            });

    	JScrollPane cableListScroller = new JScrollPane(cableList, vsbp, hsbp);
        connectPanel.add(cableListScroller);

        // cable table action button bar 1
        JPanel cableButtons1 = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cableRemoveButton = new JButton("Remove");
        cableRemoveButton.setEnabled(false);
        LoginUtil.registerProtectedComponent(cableRemoveButton, false, editPrincipal);
        cableRemoveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Cable selectedCable = _model.getSelectedCable();
                    if (selectedCable != null) {
                        if (cableList.getCellEditor() != null)
                            cableList.getCellEditor().stopCellEditing();
                        removeCable(selectedCable);
                    }
                }
            });

        cableSaveButton = new JButton("Save");
        cableSaveButton.setEnabled(false);
        LoginUtil.registerProtectedComponent(cableSaveButton, false, editPrincipal);
        cableSaveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (cableList.getCellEditor() != null)
                        cableList.getCellEditor().stopCellEditing();

                    // kick off save here
                    document.actionSave();
                }
            });
        
        cableUndoButton = new JButton("Undo");
        cableUndoButton.setToolTipText("Undo any edits to cables.");
        LoginUtil.registerProtectedComponent(cableUndoButton, editPrincipal);
        cableUndoButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (cableList.getCellEditor() != null)
                        cableList.getCellEditor().stopCellEditing();
                    document.actionUndo(false);
                    cableSaveButton.setEnabled(false);
                    //cableRemoveButton.setEnabled(false);
                }
            });
        
        cableSearchLabel = new JLabel("Find by label:");
        cableSearchField = new JTextField(10);
        cableSearchField.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    char keyChar = e.getKeyChar();
                    if (keyChar == '\n') {
                        if (cableList.getCellEditor() != null)
                            cableList.getCellEditor().stopCellEditing();
                        String labelSearchString = cableSearchField.getText();
                        labelSearchDone = true;
                        document.actionFindCablesByLabel(labelSearchString);
                    }
                }
            });

        cableButtons1.add(cableRemoveButton);        
        cableButtons1.add(cableSaveButton);
        cableButtons1.add(cableUndoButton);
        cableButtons1.add(cableSearchLabel);
        cableButtons1.add(cableSearchField);

        connectPanel.add(cableButtons1);

        topPanel.add(connectPanel);
        
    	getContentPane().add(topPanel);
    }


    /**
     * Utility method to expand all children of the node given
     * by path. This method uses recursion.
     */
    public void expandDescendants(JTree tree, TreePath path)
    {
        TreeModel model = tree.getModel();
        Object node = path.getLastPathComponent();
        
        // can't expand a leaf
        if (model.isLeaf(node))
            return;

        for (int count = model.getChildCount(node), i = 0; i < count; i++) {
            Object child = model.getChild(node, i);
            if (!model.isLeaf(child)) {
                TreePath childPath = path.pathByAddingChild(child);
                tree.expandPath(childPath);
                expandDescendants(tree, childPath);
            }
        }
    }

    /**
     * Utility method to collapse all children of the node given
     * by path. This method uses recursion.
     */
    public void collapseDescendants(JTree tree, TreePath path)
    {

        TreeModel model = tree.getModel();
        Object node = path.getLastPathComponent();
        
        for (int count = model.getChildCount(node), i = 0; i < count; i++) {
            Object child = model.getChild(node, i);
            TreePath childPath = path.pathByAddingChild(child);
            collapseDescendants(tree, childPath);
            tree.collapsePath(childPath);
        }
        tree.collapsePath(path);
    }


    /**
     * Given the selection in a particular hierarchy, update the other tree
     * to reflect it. 
     *
     * @param leadSelection component or port chosen from one hierarchy (tree)
     * @param expandBelow if true, fully expand tree below leadSelection
     * @param skipCableSearch if true, do everything but conduct cable search
     * @param relationshipType the hierarchy of the leadSelection
     * @param componentIndex 1 - ComponentA,  2 - ComponentB
     * @param primaryTree the tree in which selection has initially occurred
     * @param otherTree1 the other JTree
     *
     */
    private void setHierarchySelection(Object leadSelection,
                                       boolean expandBelow,
                                       boolean skipCableSearch,
                                       int relationshipType,
                                       int componentIndex,
                                       JTree primaryTree,
                                       JTree otherTree1) {

        //System.out.println("setHierarchySelection(): relType: "+relationshipType+" cIndex: "+componentIndex+" skip cable search: "+skipCableSearch);
        // stop any cable cell editing
        if (cableList.getCellEditor() != null) {
            cableList.getCellEditor().cancelCellEditing();
        }

        // set selection for primary tree
        ComponentPortTreeModel primaryTreeModel = (ComponentPortTreeModel)primaryTree.getModel();
        TreePath primaryTreePath = primaryTreeModel.getTreePath(leadSelection);
        primaryTree.setSelectionPath(primaryTreePath);
        primaryTree.scrollPathToVisible(primaryTreePath);

        // set selection for other tree
        ComponentPortTreeModel otherTree1Model = (ComponentPortTreeModel)otherTree1.getModel();
        TreePath otherTree1Path = otherTree1Model.getTreePath(leadSelection);
        otherTree1.setSelectionPath(otherTree1Path);
        otherTree1.scrollPathToVisible(otherTree1Path);

        if (componentIndex == 1) {  // Component A
            if (leadSelection instanceof ComponentPort) {  // clicked on port
                ComponentPort port = (ComponentPort)leadSelection;
                _model.setSelectedPort1(port);
                _model.setSelectedComponent1(null); // picked port, not component

                // deselect other component's selections
                _model.setSelectedComponent2(null);
                //_model.setSelectedPort2(null);
                housing2Tree.clearSelection();
                control2Tree.clearSelection();

                //System.out.println("CableWindow: skipCableSearch is "+skipCableSearch+", calling actionFindCables with port "+ port + " rel type "+relationshipType+" componentIndex: "+componentIndex);
                if (!skipCableSearch) {
                    // disable Remove button
                    cableRemoveButton.setEnabled(false);
                    labelSearchDone = false;
                    document.actionFindCables(port);
                }

            } else {  // clicked on component
                _model.setSelectedComponent1((Component)leadSelection);
                _model.setSelectedPort1(null);  // picked component, not port
                // clear cable table
                _model.setCables(null);
                CableTableModel cableTableModel = 
                    (CableTableModel)cableList.getModel();
                cableTableModel.fireTableDataChanged();                
            }

        } else {  // Component B
            if (leadSelection instanceof ComponentPort) {
                ComponentPort port = (ComponentPort)leadSelection;

                _model.setSelectedPort2(port);
                _model.setSelectedComponent2(null); // picked port, not component

                if (!skipCableSearch) {
                    ComponentPort port1 = _model.getSelectedPort1();
                    Cable selectedCable = _model.getSelectedCable();
                    if (port1 != null) { // && (selectedCable == null || !selectedCable.isSingleEnded())) {
                        labelSearchDone = false;
                        document.actionFindCable(port1, port);
                    } 
                }

            } else {
                _model.setSelectedComponent2((Component)leadSelection);
                _model.setSelectedPort2(null); // picked component, not port
            }
        }

        if (expandBelow && leadSelection instanceof Component) {
            Component c = (Component)leadSelection;

            // find out whether we are already expanded below primaryTreePath
            Enumeration desc = primaryTree.getExpandedDescendants(primaryTreePath);
            boolean expanded = false;
            if (desc != null)
                expanded = true;

            // invert this logic, since single click preceeding double click expands one level
            expanded = !expanded;

            Component root = (Component)primaryTreeModel.getRoot();
            boolean rootClicked = (c.getId() == root.getId());
            if (!expanded) {
                // expanding the root would take forever, so disallow it
                if (!rootClicked) {
                    // expand all children of node given by path
                    // first retrieve all children from database (which can be slow)
                    document.actionFindAllChildren(c, relationshipType);
                    // updateView() method below will get invoked afterwards to expand tree
                    
                } else {
                    Application.displayWarning("Notice","Fully expanding the root component is not permitted.");
                }
            } else {
                if (!rootClicked) {
                    // we can collapse here, since that is a fast operation
                    collapseDescendants(primaryTree, primaryTreePath);
                } else {
                    TreeModelEvent te = null;
                    if (relationshipType == ComponentRelationshipType.HOUSING) 
                        te = new TreeModelEvent(this, new Object[] {_model.getSiteComponent()});
                    else
                        te = new TreeModelEvent(this, new Object[] {_model.getNetworkComponent()});
                    primaryTreeModel.fireTreeStructureChanged(te);
                }
            }
            
        }

    }

    /**
     * Add new cable to the selected port(s).
     */
    private void addNewCable() {
        Cable newCable = null;
        newCable = document.actionAddNewCable(_model.getSelectedPort1(), _model.getSelectedPort2());

        // Update GUI
        cableSaveButton.setEnabled(true);
        cableRemoveButton.setEnabled(true);
        _model.setSelectedCable(newCable);
        if (_model.getCables() == null)
            _model.setCables(new ArrayList());
        _model.getCables().add(newCable);
        int row = _model.getCables().size() - 1;
        
        CableTableModel cableTableModel = 
            (CableTableModel)cableList.getModel();
        cableTableModel.fireTableRowsInserted(row, row);
        cableList.setRowSelectionInterval(row, row);
        cableList.editCellAt(row,0);
        
        ComponentPort portA = newCable.getComponentPortA();
        ComponentPort portB = newCable.getComponentPortB();
        
        if (portA != null)
            updateJTreeViews(portA);
        if (portB != null)
            updateJTreeViews(portB);

    }

    /**
     * Remove cable from cable list and detatch from ports to which it might be attached.
     */
    private void removeCable(Cable cable) {

        ComponentPort portA = cable.getComponentPortA();
        ComponentPort portB = cable.getComponentPortB();

        // Remove cable from list that drive table
        List modelCableList = _model.getCables();                        
        modelCableList.remove(cable);

        // disconnect cable from component ports
        document.actionRemoveCable(cable);

        // Update GUI
        cableSaveButton.setEnabled(true);
        cableRemoveButton.setEnabled(false);
        _model.setSelectedCable(null);
        
        CableTableModel cableTableModel = 
            (CableTableModel)cableList.getModel();
        cableTableModel.fireTableDataChanged();
        
        if (portA != null) {
            updateJTreeViews(portA);
        }
        if (portB != null) {
            updateJTreeViews(portB);
        }
        
    }


    /**
     * Select the given cable from the cable table. Does some checks to make sure that
     * associated component ports are also selected in the respective trees.
     */
    public void setCableSelection(Cable selectedCable) {

        List modelCableList = _model.getCables();
        int cableIndex = modelCableList.indexOf(selectedCable);
        
        if (cableIndex != -1) {
            _model.setSelectedCable(selectedCable);

            if (labelSearchDone) {
                ComponentPort a = selectedCable.getComponentPortA();
                ComponentPort b = selectedCable.getComponentPortB();
                ComponentPort toShow = null;
                if (a!=null)
                    toShow = a;
                else
                    toShow = b;
                //System.out.println("setCableSelection calling setHierarchySelection with true flag");
                setHierarchySelection(toShow, false, true, ComponentRelationshipType.CONTROL, 1,
                                      control1Tree, housing1Tree);
            }

            //cableList.setRowSelectionInterval(cableIndex, cableIndex);  - not needed
            //System.out.println("setCableSelection invoking actionSetComponentBUsingCable()");
            document.actionSetComponentBUsingCable(selectedCable);
        }

    }

    /**
     * Update the display of the jtree nodes corresponding to the given component port.
     * Typically used when the underlying data has changed, not the structure.
     */
    private void updateJTreeViews(ComponentPort port) {

        Component c = port.getComponent();
        TreePath pPath = null;
        TreeModelEvent tme = null;
        int cIndex = 0;
        ComponentPortTreeModel h1Model = (ComponentPortTreeModel)housing1Tree.getModel();
        ComponentPortTreeModel h2Model = (ComponentPortTreeModel)housing2Tree.getModel();
        ComponentPortTreeModel c1Model = (ComponentPortTreeModel)control1Tree.getModel();
        ComponentPortTreeModel c2Model = (ComponentPortTreeModel)control2Tree.getModel();
        
        pPath = h1Model.getTreePath(port);
        cIndex = h1Model.getIndexOfChild(c, port);
        tme = new TreeModelEvent(this, pPath, new int[] {cIndex}, null);
        h1Model.fireTreeNodesChanged(tme);

        pPath = h2Model.getTreePath(port);
        cIndex = h2Model.getIndexOfChild(c, port);
        tme = new TreeModelEvent(this, pPath, new int[] {cIndex}, null);
        h2Model.fireTreeNodesChanged(tme);

        pPath = c1Model.getTreePath(port);
        cIndex = c1Model.getIndexOfChild(c, port);
        tme = new TreeModelEvent(this, pPath, new int[] {cIndex}, null);
        c1Model.fireTreeNodesChanged(tme);

        pPath = c2Model.getTreePath(port);
        cIndex = c2Model.getIndexOfChild(c, port);
        tme = new TreeModelEvent(this, pPath, new int[] {cIndex}, null);
        c2Model.fireTreeNodesChanged(tme);
        
    }

    /**
     * Update graphical view of CableModel data based on event type.
     *
     * @param event type of data model change
     */
    public void updateView(CableModelEvent event) {

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

        case CableModelEvent.NEW_CABLES: {
            List cables = (List)event.getPayload();
            if (cables != null) {
                _model.setCables(cables);
                CableTableModel cableTableModel = 
                    (CableTableModel)cableList.getModel();
                cableTableModel.fireTableDataChanged();
                if (cables.size() > 0)
                    cableList.setRowSelectionInterval(0,0);  // auto-select first cable
            }
            break;
        }
            
        case CableModelEvent.NEW_CABLE: {
            Cable cable = (Cable)event.getPayload();
            List newCableList = new ArrayList();
            _model.setCables(newCableList);

            if (cable != null) {
                newCableList.add(cable);

                if (LoginUtil.isPermitted(editPrincipal)) {
                    cableRemoveButton.setEnabled(true);
                }
                CableTableModel cableTableModel = 
                    (CableTableModel)cableList.getModel();
                cableTableModel.fireTableDataChanged();  
                cableList.setRowSelectionInterval(0,0);    
            } else {
                if (LoginUtil.isPermitted(editPrincipal)) {
                    cableRemoveButton.setEnabled(false);
                }            
                CableTableModel cableTableModel = 
                    (CableTableModel)cableList.getModel();
                cableTableModel.fireTableDataChanged();      
            }
            break;
        }
        case CableModelEvent.NEW_COMPONENT_B_AND_PORT: {
            ComponentPort port = (ComponentPort)event.getPayload();
            Component c = null;
            //System.out.println("updateView(): NEW_COMPONENT_B_AND_PORT");
            if (port != null) {
                c = port.getComponent();
                _model.setSelectedComponent2(c);
                _model.setSelectedPort2(port);
                //System.out.println("updateView(): NEW_COMPONENT_B_AND_PORT found port, doing setHierarchySelection");
                setHierarchySelection(port, false, true, 2, 2,
                                      housing2Tree, control2Tree);

            } else {
                _model.setSelectedComponent2(null);
                _model.setSelectedPort2(null);
                housing2Tree.clearSelection();
                control2Tree.clearSelection();
            }

            // enable remove button
            if (LoginUtil.isPermitted(editPrincipal)) {
                cableRemoveButton.setEnabled(true);
            }
            
            break;
        }
        case CableModelEvent.CABLE_ADDED: {
            CableAction action = (CableAction)event.getPayload();
            Cable cable = action.getCable();

            cableSaveButton.setEnabled(false);
            cableRemoveButton.setEnabled(false);
            _model.setSelectedCable(cable);
            if (_model.getCables() == null)
                _model.setCables(new ArrayList());
            _model.getCables().add(cable);
            int row = _model.getCables().size() - 1;
            
            CableTableModel cableTableModel = 
                (CableTableModel)cableList.getModel();
            cableTableModel.fireTableRowsInserted(row, row);
            cableList.setRowSelectionInterval(row, row);
            cableList.editCellAt(row,0);
            
            ComponentPort portA = cable.getComponentPortA();
            ComponentPort portB = cable.getComponentPortB();
            
            if (portA != null) {
                updateJTreeViews(portA);
            }
            if (portB != null) {
                updateJTreeViews(portB);
            }
            
            break;
        }
        case CableModelEvent.CABLE_REMOVED: {
            CableAction action = (CableAction)event.getPayload();
            Cable removedCable = action.getCable();

            List modelCableList = _model.getCables();                        
            modelCableList.remove(removedCable);
            cableSaveButton.setEnabled(false);
            cableRemoveButton.setEnabled(false);
            _model.setSelectedCable(null);
            
            CableTableModel cableTableModel = 
                (CableTableModel)cableList.getModel();
            cableTableModel.fireTableDataChanged();

            ComponentPort portA = action.getComponentPort1();
            ComponentPort portB = action.getComponentPort2();

            if (portA != null)
                updateJTreeViews(portA);
            if (portB != null)
                updateJTreeViews(portB);
            
            break;
        }
        case CableModelEvent.CABLE_UPDATED: {
            CableAction action = (CableAction)event.getPayload();
            ComponentPort portA = action.getComponentPort1();
            ComponentPort portB = action.getComponentPort2();

            CableTableModel cableTableModel = 
                (CableTableModel)cableList.getModel();
            cableTableModel.fireTableDataChanged();

            if (portA != null)
                updateJTreeViews(portA);
            if (portB != null)
                updateJTreeViews(portB);

            break;
        }
        case CableModelEvent.REDRAW: {
            // the initial tree painting takes a long time, so
            _appFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // housing
            ComponentPortTreeModel h1Model = (ComponentPortTreeModel)housing1Tree.getModel();
            ComponentPortTreeModel h2Model = (ComponentPortTreeModel)housing2Tree.getModel();
            h1Model.setRoot(_model.getSiteComponent());
            h2Model.setRoot(_model.getSiteComponent());
            if (_model.getSiteComponent() == null) {
                TreeModelEvent he = 
                    new TreeModelEvent(this, (TreePath)null);
                h1Model.fireTreeStructureChanged(he);
                h2Model.fireTreeStructureChanged(he);
            } else {
                TreeModelEvent he = 
                    new TreeModelEvent(this, new Object[] {_model.getSiteComponent()});
                h1Model.fireTreeStructureChanged(he);
                h2Model.fireTreeStructureChanged(he);
            }

            // control
            ComponentPortTreeModel c1Model = (ComponentPortTreeModel)control1Tree.getModel();
            ComponentPortTreeModel c2Model = (ComponentPortTreeModel)control2Tree.getModel();
            c1Model.setRoot(_model.getNetworkComponent());
            c2Model.setRoot(_model.getNetworkComponent());
            if (_model.getNetworkComponent() == null) {
                TreeModelEvent ce = 
                    new TreeModelEvent(this, (TreePath)null);
                c1Model.fireTreeStructureChanged(ce);
                c2Model.fireTreeStructureChanged(ce);
            } else {
                TreeModelEvent ce = 
                    new TreeModelEvent(this, new Object[] {_model.getNetworkComponent()});
                c1Model.fireTreeStructureChanged(ce);
                c2Model.fireTreeStructureChanged(ce);
            }

            // have cable table redraw
            CableTableModel cableTableModel = 
                (CableTableModel)cableList.getModel();
            cableTableModel.fireTableDataChanged();

            // If _model.getSelectedComponent() is not null, preselect it in trees
            final int housingRelationshipType = ComponentRelationshipType.HOUSING;
            Component c1 = _model.getSelectedComponent1();
            if (c1 != null) {
                setHierarchySelection(c1, false, false, housingRelationshipType, 1,
                                      housing1Tree, control1Tree);
            }
            /* haven't got this working yet
            ComponentPort p1 = _model.getSelectedPort1();
            if (p1 != null) {
                setHierarchySelection(p1, false, false, housingRelationshipType, 1,
                                      housing1Tree, control1Tree);
            } else if (c1 != null) {
                setHierarchySelection(c1, false, false, housingRelationshipType, 1,
                                      housing1Tree, control1Tree);
            }
            */

            // go back to regular cursor
            _appFrame.setCursor(null);

            break;
        }

        case CableModelEvent.HOUSING_FIND_CONFIG: {
            ConfigureFindModel findConfig = (ConfigureFindModel)event.getPayload();
            int componentIndex = findConfig.getComponentIndex();

            // housing
            ComponentPortTreeModel hModel = null;
            if (componentIndex == 1) {  // component A side
                hModel = (ComponentPortTreeModel)housing1Tree.getModel();
                
                TreeModelEvent he = 
                    new TreeModelEvent(this, new Object[] {_model.getSiteComponent()});
                hModel.fireTreeStructureChanged(he);

                if (findConfig.getClearSearchField()) {
                    housing1SearchField.setText(null);
                    findConfig.setClearSearchField(false);
                }
                
                List componentList = null;
                int componentListSize = 0;
                componentList = hModel.getComponentList();
                if (componentList != null)
                    componentListSize = componentList.size();
                
                if (componentListSize > 0) {
                    housing1PrevButton.setEnabled(false);
                    housing1NextButton.setEnabled(true);
                    housing1StopButton.setEnabled(true);
                    Component firstFind = (Component)componentList.get(0);
                    TreePath firstFindPath = hModel.getTreePath(firstFind);
                    housing1Tree.setSelectionPath(firstFindPath);
                    housing1Tree.scrollPathToVisible(firstFindPath);
                    
                } else if (findConfig.filterApplied()) {
                    housing1StopButton.setEnabled(true);

                } else {
                    housing1StopButton.setEnabled(false);
                    housing1PrevButton.setEnabled(false);
                    housing1NextButton.setEnabled(false);
                }
                
            } else {   // component B side
                hModel = (ComponentPortTreeModel)housing2Tree.getModel();

                TreeModelEvent he = 
                    new TreeModelEvent(this, new Object[] {_model.getSiteComponent()});
                hModel.fireTreeStructureChanged(he);

                if (findConfig.getClearSearchField()) {
                    housing2SearchField.setText(null);
                    findConfig.setClearSearchField(false);
                }
                
                List componentList = null;
                int componentListSize = 0;
                
                componentList = hModel.getComponentList();
                if (componentList != null)
                    componentListSize = componentList.size();
                
                if (componentListSize > 0) {
                    housing2PrevButton.setEnabled(false);
                    housing2NextButton.setEnabled(true);
                    housing2StopButton.setEnabled(true);
                    Component firstFind = (Component)componentList.get(0);
                    TreePath firstFindPath = hModel.getTreePath(firstFind);
                    housing2Tree.setSelectionPath(firstFindPath);
                    housing2Tree.scrollPathToVisible(firstFindPath);
                    
                } else if (findConfig.filterApplied()) {
                    housing2StopButton.setEnabled(true);

                } else {
                    housing2StopButton.setEnabled(false);
                    housing2PrevButton.setEnabled(false);
                    housing2NextButton.setEnabled(false);
                }
            }

            break;
        }

        case CableModelEvent.CONTROL_FIND_CONFIG: {
            ConfigureFindModel findConfig = (ConfigureFindModel)event.getPayload();
            int componentIndex = findConfig.getComponentIndex();

            // control
            ComponentPortTreeModel cModel = null;
            if (componentIndex == 1) {
                cModel = (ComponentPortTreeModel)control1Tree.getModel();
                
                TreeModelEvent ce = 
                    new TreeModelEvent(this, new Object[] {_model.getNetworkComponent()});
                cModel.fireTreeStructureChanged(ce);
                if (findConfig.getClearSearchField()) {
                    control1SearchField.setText(null);
                    findConfig.setClearSearchField(false);
                }

                List componentList = null;
                int componentListSize = 0;
                componentList = cModel.getComponentList();
                if (componentList != null)
                    componentListSize = componentList.size();
                
                if (componentListSize > 0) {
                    control1PrevButton.setEnabled(false);
                    control1NextButton.setEnabled(true);
                    control1StopButton.setEnabled(true);
                    Component firstFind = (Component)componentList.get(0);
                    TreePath firstFindPath = cModel.getTreePath(firstFind);
                    control1Tree.setSelectionPath(firstFindPath);
                    control1Tree.scrollPathToVisible(firstFindPath);
                    
                } else if (findConfig.filterApplied()) {
                    control1StopButton.setEnabled(true);

                } else {
                    control1StopButton.setEnabled(false);
                    control1PrevButton.setEnabled(false);
                    control1NextButton.setEnabled(false);
                }
                
            } else {
                cModel = (ComponentPortTreeModel)control2Tree.getModel();
                control2SearchField.setText(null);

                TreeModelEvent ce = 
                    new TreeModelEvent(this, new Object[] {_model.getNetworkComponent()});
                cModel.fireTreeStructureChanged(ce);
                if (findConfig.getClearSearchField()) {
                    control2SearchField.setText(null);
                    findConfig.setClearSearchField(false);
                }
                
                List componentList = null;
                int componentListSize = 0;
                componentList = cModel.getComponentList();
                if (componentList != null)
                    componentListSize = componentList.size();
                
                if (componentListSize > 0) {
                    control2PrevButton.setEnabled(false);
                    control2NextButton.setEnabled(true);
                    control2StopButton.setEnabled(true);
                    Component firstFind = (Component)componentList.get(0);
                    TreePath firstFindPath = cModel.getTreePath(firstFind);
                    control2Tree.setSelectionPath(firstFindPath);
                    control2Tree.scrollPathToVisible(firstFindPath);

                } else if (findConfig.filterApplied()) {
                    control2StopButton.setEnabled(true);
                    
                } else {
                    control2StopButton.setEnabled(false);
                    control2PrevButton.setEnabled(false);
                    control2NextButton.setEnabled(false);
                }
            }
            break;
        }

        case CableModelEvent.EXPAND_CHILDREN: {

            Component c = (Component)event.getPayload();
            if (focusedTree != null) {
                // this drawing operation can take a while, so cursor it
                _appFrame.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                _appFrame.getGlassPane().setVisible(true);
                ComponentPortTreeModel treeModel = (ComponentPortTreeModel)focusedTree.getModel();
                TreePath treePath = treeModel.getTreePath(c);
                expandDescendants(focusedTree, treePath);
                _appFrame.getGlassPane().setVisible(false);
                _appFrame.getGlassPane().setCursor(null);
            }
            break;
        }

        case CableModelEvent.SAVE_COMPLETE: {
            Boolean staleData = (Boolean)event.getPayload();

            cableSaveButton.setEnabled(false);
            if (staleData.booleanValue()) {
                Application.displayWarning("IRMIS Service Warning", "Your data became stale before you saved it. Your edit in progress has been undone, and we will reload your data now.");
                Main.requestResetOfDocuments();
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
     * Model for display/editing of cable interconnect table.
     */
    class CableTableModel extends AbstractTableModel {
        private String[] columnNames = {"Label","Color","Virtual","SingleEnded","Dest Desc"};
        private boolean warningDialogUp = false;

        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            List cables = _model.getCables();
            if (cables != null) {
                size = cables.size();
            }
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            List cables = _model.getCables();
            if (cables != null && cables.size() > 0) {
                Cable cable = (Cable)cables.get(row);
                if (col == 0) {
                    if (cable.getLabel()==null)
                        return " ";
                    else
                        return cable.getLabel();
                } else if (col == 1) {
                    if (cable.getColor()==null)
                        return " ";
                    else
                        return cable.getColor();
                } else if (col == 2) {
                    if (cable.getVirtual())
                        return new Boolean(true);  // virtual cable
                    else
                        return new Boolean(false);
                } else if (col == 3) {
                    if (cable.isSingleEnded())
                        return new Boolean(true);  // single-ended cable
                    else
                        return new Boolean(false);
                } else if (col == 4) {
                    if (cable.getDestinationDescription()==null)
                        return " ";
                    else
                        return cable.getDestinationDescription();
                } else {
                    return " ";
                }
            } else {
                return " ";
            }
        }

        // called after edit of table cell is complete
        public void setValueAt(Object value, int row, int col) {
            Cable selectedCable = _model.getSelectedCable();
            if (col == 3) {
                Boolean boolValue = (Boolean)value;
                boolean singleEnded = boolValue.booleanValue();
                ComponentPort portA = _model.getSelectedPort1();
                ComponentPort portB = _model.getSelectedPort2();
                ComponentPort cablePortA = selectedCable.getComponentPortA();
                ComponentPort cablePortB = selectedCable.getComponentPortB();
                if (!singleEnded && selectedCable.isSingleEnded()) {
                    if (portB != null) {
                        //System.out.println("going from single-ended to 2-ended");
                        document.actionUpdateCableSingleToDoubleEnded(selectedCable, portA, portB);
                        updateJTreeViews(portB);
                    } else {
                        if (!warningDialogUp) {
                            warningDialogUp = true;
                            Application.displayWarning("No Port Selected",
                                                       "You must select a Component B port before unchecking the SingleEnded checkbox.");
                            warningDialogUp = false;
                        }
                    }

                } else if (singleEnded && !selectedCable.isSingleEnded()) {
                    //System.out.println("going from 2-ended to single-ended");
                    document.actionUpdateCableDoubleToSingleEnded(selectedCable, portA, portB);
                    housing2Tree.clearSelection();
                    control2Tree.clearSelection();
                    _model.setSelectedComponent2(null);
                    _model.setSelectedPort2(null); 
                }

            } else if (col == 2) {
                Boolean boolValue = (Boolean)value;
                boolean virtual = boolValue.booleanValue();
                document.actionUpdateCableVirtualFlag(selectedCable, virtual);

            } else if (col == 0 || col == 1 || col == 4){
                String strValue = (String)value;
                if (selectedCable != null) {
                    if (col == 0) {  // cable label
                        document.actionUpdateCableLabel(selectedCable, strValue);
                    } else if (col == 1) {  // cable color
                        document.actionUpdateCableColor(selectedCable, strValue);
                    } else if (col == 4) {  // cable dest description
                        document.actionUpdateCableDestination(selectedCable, strValue);
                    }
                }
            }
            if (LoginUtil.isPermitted(editPrincipal)) {
                cableSaveButton.setEnabled(true);
            }            
        }

        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            List cables = _model.getCables();
            Cable cableForRow = (Cable)cables.get(row);
            
            // can only edit if logged in and row of table has first been selected
            if (LoginUtil.isPermitted(editPrincipal)
                && cableForRow.equals(_model.getSelectedCable())) {
                return true;
            } else {
                return false;
            }
        }
    }


    /**
     * Cell editor for cable label column.
     */
    class CableLabelTableCellEditor extends AbstractCellEditor 
        implements TableCellEditor {
        
        JTextField labelTextField = null;
        
        // called when cell value edited by user
        public java.awt.Component getTableCellEditorComponent(JTable table,
                                                              Object value, 
                                                              boolean isSelected,
                                                              int row, int col) {

            if (labelTextField == null) {
                labelTextField = new JTextField();
                labelTextField.setEditable(true);
                labelTextField.addKeyListener(new KeyAdapter() {
                        public void keyTyped(KeyEvent e) {
                            char keyChar = e.getKeyChar();
                            JTextField labelTextField = (JTextField)e.getComponent();
                            
                            if (keyChar == '\n') {
                                // not a good way to get rid of newline,
                                // but I can't figure out how to stop
                                // this from getting entered in text area
                                int cp = labelTextField.getCaretPosition();
                                Document doc = labelTextField.getDocument();
                                try {
                                    doc.remove(labelTextField.getText().length()-1,1);
                                } catch (BadLocationException ble) {}
                                stopCellEditing();
                            }
                        }
                    });
            }
            
            // put the data in the text field
            if (value != null) {
                labelTextField.setText(value.toString());
                cableSaveButton.setEnabled(true);
            }
            
            return labelTextField;
        }

        // called when editing is complete. must return new value
        // to be stored in the cell.
        public Object getCellEditorValue() {
            return labelTextField.getText();
        }

        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                MouseEvent me = (MouseEvent)e;
                if (me.getClickCount() == 1)
                    return true;
                else
                    return false;
            }
            return false;
        }

    }

    /**
     * Cell editor for cable color column.
     */
    class CableColorTableCellEditor extends AbstractCellEditor 
        implements TableCellEditor {
        
        JTextField colorTextField = null;
        
        // called when cell value edited by user
        public java.awt.Component getTableCellEditorComponent(JTable table,
                                                              Object value, 
                                                              boolean isSelected,
                                                              int row, int col) {

            if (colorTextField == null) {
                colorTextField = new JTextField();
                colorTextField.setEditable(true);
                colorTextField.addKeyListener(new KeyAdapter() {
                        public void keyTyped(KeyEvent e) {
                            char keyChar = e.getKeyChar();
                            JTextField colorTextField = (JTextField)e.getComponent();
                            
                            if (keyChar == '\n') {
                                // not a good way to get rid of newline,
                                // but I can't figure out how to stop
                                // this from getting entered in text area
                                int cp = colorTextField.getCaretPosition();
                                Document doc = colorTextField.getDocument();
                                try {
                                    doc.remove(colorTextField.getText().length()-1,1);
                                } catch (BadLocationException ble) {}
                                stopCellEditing();
                            }
                        }
                    });
            }
            
            // put the data in the text field
            if (value != null) {
                colorTextField.setText(value.toString());
                cableSaveButton.setEnabled(true);
            }
            
            return colorTextField;
        }

        // called when editing is complete. must return new value
        // to be stored in the cell.
        public Object getCellEditorValue() {
            return colorTextField.getText();
        }

        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                MouseEvent me = (MouseEvent)e;
                if (me.getClickCount() == 1)
                    return true;
                else
                    return false;
            }
            return false;
        }

    }

    /**
     * Cell editor for cable dest desc column.
     */
    class CableDestDescTableCellEditor extends AbstractCellEditor 
        implements TableCellEditor {
        
        JTextField destTextField = null;
        
        // called when cell value edited by user
        public java.awt.Component getTableCellEditorComponent(JTable table,
                                                              Object value, 
                                                              boolean isSelected,
                                                              int row, int col) {

            if (destTextField == null) {
                destTextField = new JTextField();
                destTextField.setEditable(true);
                destTextField.addKeyListener(new KeyAdapter() {
                        public void keyTyped(KeyEvent e) {
                            char keyChar = e.getKeyChar();
                            JTextField destTextField = (JTextField)e.getComponent();
                            
                            if (keyChar == '\n') {
                                // not a good way to get rid of newline,
                                // but I can't figure out how to stop
                                // this from getting entered in text area
                                int cp = destTextField.getCaretPosition();
                                Document doc = destTextField.getDocument();
                                try {
                                    doc.remove(destTextField.getText().length()-1,1);
                                } catch (BadLocationException ble) {}
                                stopCellEditing();
                            }
                        }
                    });
            }
            
            // put the data in the text field
            if (value != null) {
                destTextField.setText(value.toString());
                cableSaveButton.setEnabled(true);
            }
            
            return destTextField;
        }

        // called when editing is complete. must return new value
        // to be stored in the cell.
        public Object getCellEditorValue() {
            return destTextField.getText();
        }

        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                MouseEvent me = (MouseEvent)e;
                if (me.getClickCount() == 1)
                    return true;
                else
                    return false;
            }
            return false;
        }

    }


    public class ComponentPortTreeCellRenderer extends DefaultTreeCellRenderer {

        private int hierarchy;
        ImageIcon cableIcon = new ImageIcon(AppsUtil.getImageURL("littleCable.png"));

        public ComponentPortTreeCellRenderer(int hierarchy) {
            super();
            this.hierarchy = hierarchy;
        }

        public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value,
                                                               boolean selected, boolean expanded,
                                                               boolean leaf, int row, 
                                                               boolean hasFocus) {

            // text to display
            String displayStr = null;
            if (value instanceof ComponentPort) {
                ComponentPort port = (ComponentPort)value;
                displayStr = port.toString();
                setTextNonSelectionColor(Color.green.darker());
                setTextSelectionColor(Color.green.darker());

                // use this if cable is attached
                List cableList = port.getCables();
                if (getFont() != null && cableList != null && cableList.size() > 0) {
                    setFont(getFont().deriveFont(Font.BOLD));
                    setLeafIcon(cableIcon);
                } else {
                    setFont(getFont().deriveFont(Font.PLAIN));
                    setLeafIcon(null);
                }
                
                setOpenIcon(null);
                setClosedIcon(null);

            } else {
                Component c = (Component)value;
                ComponentType ct = c.getComponentType();
                displayStr = alignIocName(c.toString(hierarchy));
                setTextNonSelectionColor(Color.black);
                setTextSelectionColor(Color.black);
                if (getFont() != null)
                    setFont(getFont().deriveFont(Font.PLAIN));
                setLeafIcon(null);
                setOpenIcon(null);
                setClosedIcon(null);

                ComponentRelationship housingParent = 
                    c.getParentRelationship(ComponentRelationshipType.HOUSING);
                ComponentRelationship controlParent = 
                    c.getParentRelationship(ComponentRelationshipType.CONTROL);
                
            }

            super.getTreeCellRendererComponent(tree, value, selected, expanded, 
                                               leaf, row, hasFocus);
            setText(displayStr);
            return this;
        }
    }

    /**
     * Takes a component.toString() value and adds spaces between
     * component type and ioc or sioc. This is just a bit of custom
     * display string tweaking for iocs in the control tree.
     */
    private String alignIocName(String compString) {
        int indexIoc = compString.indexOf("ioc");
        int indexSioc = compString.indexOf("sioc");
        int index = -1;
        if (indexSioc > 0)
            index = indexSioc;
        else if (indexIoc > 0)
            index = indexIoc;

        if (index == -1)
            return compString;

        String first = compString.substring(0,index);
        String second = compString.substring(index);
        JTextField firstField = new JTextField(first);
        StringBuffer sb = new StringBuffer(first);
        while (firstField.getPreferredSize().getWidth() < 126) {
            sb.append(" ");
            firstField = new JTextField(sb.toString());
        }
        sb.append(second);
        return sb.toString();
        
    }

    public class ComponentPortPopupMenu extends JPopupMenu {

        private TreePath selectedTreePath;
        private JMenuItem cableAddItem;
        private JMenuItem addToFindItem;
        private JMenuItem findInOtherViewItem;
        private int componentIndex;
        private ComponentPortTreeFindController findController;

        public ComponentPortPopupMenu(int cIndex, ComponentPortTreeFindController findCont) {
            super();
            this.componentIndex = cIndex;
            this.findController = findCont;

            if (componentIndex == 1) {

                cableAddItem = new JMenuItem("Add Single Ended Cable");
                cableAddItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (cableList.getCellEditor() != null)
                                cableList.getCellEditor().stopCellEditing();
                            housing2Tree.clearSelection();
                            control2Tree.clearSelection();
                            _model.setSelectedPort2(null);
                            addNewCable();
                        }
                    });
                this.add(cableAddItem);
                this.addSeparator();
                findInOtherViewItem = new JMenuItem("Find in Component B");
                findInOtherViewItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            Object node = selectedTreePath.getLastPathComponent();
                            setHierarchySelection(node, false, false, 2, 2,
                                                  housing2Tree, control2Tree);
                        }
                    });            
                this.add(findInOtherViewItem);
                addToFindItem = new JMenuItem("Add to Find List");
                addToFindItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            Component c = (Component)selectedTreePath.getLastPathComponent();
                            findController.addComponentToFind(c);
                        }
                    });            
                this.add(addToFindItem);
                
            } else {

                cableAddItem = new JMenuItem("Add Cable");
                cableAddItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (cableList.getCellEditor() != null)
                                cableList.getCellEditor().stopCellEditing();
                            if (_model.getSelectedPort1() != null)
                                addNewCable();
                        }
                    });
                this.add(cableAddItem);
                this.addSeparator();
                findInOtherViewItem = new JMenuItem("Find in Component A");
                findInOtherViewItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            Object node = selectedTreePath.getLastPathComponent();
                            setHierarchySelection(node, false, false, 2, 1,
                                                  housing1Tree, control1Tree);
                        }
                    });            
                this.add(findInOtherViewItem);
                
            }

        }

        public void show(JTree tree, int x, int y, boolean enabled, boolean isPort) {

            if (!enabled) {  // not logged in with sufficient permissions
                cableAddItem.setEnabled(false);
            } else {
                cableAddItem.setEnabled(true);
            } 
            if (componentIndex == 1) {
                if (isPort)
                    addToFindItem.setEnabled(false);
                else
                    addToFindItem.setEnabled(true);
            }
            if (isPort)
                findInOtherViewItem.setEnabled(false);
            else
                findInOtherViewItem.setEnabled(true);
            super.show(tree, x, y);
        }

        public void setSelectedTreePath(TreePath path) {
            selectedTreePath = path;
        }

        public int getComponentIndex() {
            return componentIndex;
        }

    }

    public class PopupTrigger extends MouseAdapter {
        private JTree tree;
        private JTree otherTree1;
        private ComponentPortPopupMenu popupMenu;

        public PopupTrigger(JTree tree, JTree otherTree1, 
                            ComponentPortPopupMenu popupMenu) {
            super();
            this.tree = tree;
            this.otherTree1 = otherTree1;
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
                    Object node = selectedTreePath.getLastPathComponent();

                    int cIndex = popupMenu.getComponentIndex();
                    popupMenu.setSelectedTreePath(selectedTreePath);

                    if (cIndex == 1) 
                        setHierarchySelection(node, false, false, 0, cIndex,
                                              control1Tree, housing1Tree);
                    else
                        setHierarchySelection(node, false, false, 0, cIndex,
                                              control2Tree, housing2Tree);
                    boolean isPort = (node instanceof ComponentPort);

                    // show menu
                    popupMenu.show(tree, x, y, LoginUtil.isPermitted(editPrincipal), isPort);
                }
            }
        }
        
    }

}
