/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.wizard;

import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperBase;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ImportInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.OutputColumnModel;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author craig
 */
@Named(ItemDomainImportWizard.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainImportWizard implements Serializable {

    public static final String CONTROLLER_NAMED = "importWizard";

    protected static final String TAB_SELECT_FORMAT = "SelectFormatTab";
    protected static final String TAB_SELECT_MODE = "SelectModeTab";
    protected static final String TAB_SELECT_FILE = "SelectFileTab";
    protected static final String TAB_VALIDATE = "ValidateTab";
    protected static final String TAB_RESULTS = "ResultsTab";

    protected ImportHelperBase importHelper = null;

    protected String currentTab = TAB_SELECT_FORMAT;

    private Boolean disableButtonPrev = true;
    private Boolean disableButtonNext = true;
    private Boolean disableButtonFinish = true;
    private Boolean disableButtonCancel = false;
    
    private DomainImportExportInfo domainInfo = null;
    
    // models for select format tab
    private String selectedFormatName = null;
    
    // models for select mode tab
    private String selectedMode = null;

    // models for select file tab
    private Boolean disableButtonUpload = true;
    protected UploadedFile uploadfileData = null;
    private List<String> sheetNames = new ArrayList<>();
    private String selectedSheet = null;
    private boolean renderSelectSheet = false;
    private String selectFileSummaryMessage = null;
    private String selectFileErrorMessage = null;
    private String rowNumberHeader = null;
    private String rowNumberFirstData = null;
    private String rowNumberLastData = null;
    
    // models for validation tab
    protected boolean importSuccessful = true;
    protected String importResult = "";
    private CdbEntity selectedTableRow = null;
    private TreeNode selectedTreeNode = null;
    
    public static ItemDomainImportWizard getInstance() {
        return (ItemDomainImportWizard) SessionUtility.findBean(
                ItemDomainImportWizard.CONTROLLER_NAMED);
    }

    public void setDomainInfo(DomainImportExportInfo info) {
        reset();
        domainInfo = info;
    }
    
    public List<ImportExportFormatInfo> getFormatInfoList() {
        if (domainInfo != null) {
            return domainInfo.getFormatInfoList();
        } else {
            return new ArrayList<>();
        }
    }
    
    public String getCompletionUrl() {
        if (domainInfo != null) {
            return domainInfo.getCompletionUrl();
        } else {
            return "";
        }
    }
    
    public List<String> getFormatNames() {
        return getFormatInfoList().stream().map(e -> e.toString()).collect(Collectors.toList());
    }

    public String getSelectedFormatName() {
        return selectedFormatName;
    }

    public void setSelectedFormatName(String selectedFormatName) {
        this.selectedFormatName = selectedFormatName;
    }

    public void selectListenerFormat() {
        setEnablementForCurrentTab();
    }

    public String getSelectedMode() {
        return selectedMode;
    }

    public void setSelectedMode(String selectedMode) {
        this.selectedMode = selectedMode;
    }

    /**
     * Handles radio button click for select mode tab of wizard.
     */
    public void clickListenerMode() {
        setEnablementForCurrentTab();
    }
    
    public Boolean getDisableButtonPrev() {
        return disableButtonPrev;
    }

    public void setDisableButtonPrev(Boolean disableButtonPrev) {
        this.disableButtonPrev = disableButtonPrev;
    }

    public Boolean getDisableButtonNext() {
        return disableButtonNext;
    }

    public Boolean getDisableButtonFinish() {
        return disableButtonFinish;
    }

    public Boolean getDisableButtonCancel() {
        return disableButtonCancel;
    }

    public UploadedFile getUploadfileData() {
        return uploadfileData;
    }

    public void setUploadfileData(UploadedFile uploadfileData) {
        this.uploadfileData = uploadfileData;
    }
    
    public Boolean getDisableButtonUpload() {
        return disableButtonUpload;
    }

    public Boolean getRenderFileuploadData() {
        return uploadfileData == null;
    }

    public Boolean getRenderOutputData() {
        return uploadfileData != null;
    }
    
    public Boolean getRenderSelectSheet() {
        return renderSelectSheet;
    }
    
    public Boolean getRenderRowNumberCustomizationOptions() {
            return importHelper.renderRowNumberCustomzationOptions();
    }
    
    public Boolean getRenderFileSummaryMessage() {
        return selectFileSummaryMessage != null;
    }
    
    public Boolean getRenderFileErrorMessage() {
        return selectFileErrorMessage != null;
    }

    public String getUploadfileDataString() {
        if (uploadfileData == null) {
            return "";
        } else {
            return uploadfileData.getFileName();
        }
    }

    public List<String> getSheetNames() {
        return sheetNames;
    }

    public String getSelectedSheet() {
        return selectedSheet;
    }
    
    public String getSelectFileSummaryMessage() {
        return selectFileSummaryMessage;
    }
    
    public String getSelectFileErrorMessage() {
        return selectFileErrorMessage;
    }
    
    public void setSelectedSheet(String selectedSheet) {
        this.selectedSheet = selectedSheet;
        setEnablementForCurrentTab();
    }
    
    public String getRowNumberHeader() {
        return rowNumberHeader;
    }

    public void setRowNumberHeader(String rowNumberHeader) {
        this.rowNumberHeader = rowNumberHeader;
    }

    public String getRowNumberFirstData() {
        return rowNumberFirstData;
    }

    public void setRowNumberFirstData(String rowNumberFirstData) {
        this.rowNumberFirstData = rowNumberFirstData;
    }

    public String getRowNumberLastData() {
        return rowNumberLastData;
    }

    public void setRowNumberLastData(String rowNumberLastData) {
        this.rowNumberLastData = rowNumberLastData;
    }
    
    public void fileUploadListenerData(FileUploadEvent event) {

        uploadfileData = event.getFile();
        
        sheetNames = importHelper.getSheetNames(uploadfileData);
        
        renderSelectSheet = false;
        if (sheetNames.size() > 0) {
            renderSelectSheet = true;
        } else {
            // workbook contains no sheets so not valid for import
            selectFileErrorMessage =
                    "Selected file contains no worksheets. " +
                    "Click cancel to terminate and try again.";
        }
        
        setEnablementForCurrentTab();
    }

    public boolean hasTreeView() {
        if (importHelper != null) {
            return importHelper.hasTreeView();
        } else {
            return false;
        }
    }

    public TreeNode getRootTreeNode() {
        return importHelper.getRootTreeNode();
    }

    public TreeNode getSelectedTreeNode() {
        return selectedTreeNode;
    }
 
    public void setSelectedTreeNode(TreeNode selectedNode) {
        this.selectedTreeNode = selectedNode;
    }
    
    public void treeSelectionChanged(NodeSelectEvent event) {
        CdbEntity selectedItem = (CdbEntity)event.getTreeNode().getData();
        if (getRows().contains(selectedItem)) {
            setSelectedTableRow(selectedItem);
        }
    }
    
    public CdbEntity getSelectedTableRow() {
        return selectedTableRow;
    }
    
    public void setSelectedTableRow(CdbEntity entity) {
        selectedTableRow = entity;
    }
 
    public String getImportResultString() {
        return importResult;
    }
    
    public Boolean supportsModeCreate() {
        return importHelper.supportsModeCreate();
    }

    public Boolean supportsModeUpdate() {
        return importHelper.supportsModeUpdate();
    }

    public Boolean supportsModeDelete() {
        return importHelper.supportsModeDelete();
    }

    public String getValidationMessage() {
        return importHelper.getValidationMessage();
    }

    public String getSummaryMessage() {
        return importHelper.getSummaryMessage();
    }

    public List<CdbEntity> getRows() {
        if (importHelper != null) {
            return importHelper.getRows();
        } else {
            return new ArrayList<>();
        }
    }

    public List<OutputColumnModel> getColumns() {
        if (importHelper != null) {
            return importHelper.getTableViewColumns();
        } else {
            return new ArrayList<>();
        }
    }
    
    public StreamedContent getTemplateExcelFile() {
        return importHelper.getTemplateExcelFile();
    }
    
    /**
     * Handles FlowEvents generated by the wizard component. Determines next tab
     * based on current tab, defaults to visiting all tabs but implements
     * special cases. Skips cableDetailsTab when the type is "unspecified" since
     * that tab is empty.
     */
    public String onFlowProcess(FlowEvent event) {

        String nextStep = event.getNewStep();
        String currStep = event.getOldStep();
        
        // create helper if moving from select format tab
        if ((currStep.endsWith(TAB_SELECT_FORMAT))
                && (nextStep.endsWith(TAB_SELECT_MODE))) {
            createHelperForSelectedFormat();
            if (importHelper == null) {
                // don't allow transition if we couldn't create helper
                SessionUtility.addErrorMessage(
                    "Unable to create import helper for selected format",
                    "Please select a different format or Cancel.");
                setEnablement(currStep);
                currentTab = currStep;
                return currStep;
            }
        }

        // handle select mode tab
        if ((currStep.endsWith(TAB_SELECT_MODE))
                && (nextStep.endsWith(TAB_SELECT_FILE))) {
            
            importHelper.setImportMode(getSelectedMode());
        }
        
        // parse file if moving from select file tab to validate tab
        if ((currStep.endsWith(TAB_SELECT_FILE))
                && (nextStep.endsWith(TAB_VALIDATE))) {
            
            int headerRowNum = -1;
            int firstDataRowNum = -1;
            int lastDataRowNum = -1;
            
            boolean invalidFileOptions = false;
            selectFileErrorMessage = "";
            
            if ((rowNumberHeader != null) && (!rowNumberHeader.isEmpty())) {
                try {
                    headerRowNum = Integer.valueOf(rowNumberHeader);
                } catch (NumberFormatException ex) {
                    invalidFileOptions = true;
                    selectFileErrorMessage
                            = rowNumberHeader + " is not a valid header row number. ";
                }
                if (headerRowNum <= 0) {
                    invalidFileOptions = true;
                    selectFileErrorMessage
                            = "Header row " + headerRowNum + " must be greater than zero. ";
                }
            } 
            
            if ((rowNumberFirstData != null) && (!rowNumberFirstData.isEmpty())) {
                try {
                    firstDataRowNum = Integer.valueOf(rowNumberFirstData);
                } catch (NumberFormatException ex) {
                    invalidFileOptions = true;
                    selectFileErrorMessage = selectFileErrorMessage + rowNumberFirstData + " is not a valid first data row number. ";
                }
                if (firstDataRowNum <= 1) {
                    invalidFileOptions = true;
                    selectFileErrorMessage
                            = "First data row " + firstDataRowNum + " must be greater than one. ";
                }
            }
            
            if ((rowNumberLastData != null) && (!rowNumberLastData.isEmpty())) {
                try {
                    lastDataRowNum = Integer.valueOf(rowNumberLastData);
                } catch (NumberFormatException ex) {
                    invalidFileOptions = true;
                    selectFileErrorMessage = selectFileErrorMessage + rowNumberLastData + " is not a valid last data row number. ";
                }
                if (lastDataRowNum <= 1) {
                    invalidFileOptions = true;
                    selectFileErrorMessage
                            = "Last data row " + lastDataRowNum + " must be greater than one. ";
                }
            } 
            
            if (invalidFileOptions) {
                selectFileSummaryMessage
                        = "Please correct options and try again.";
                setEnablement(currStep);
                currentTab = currStep;
                return currStep;
            }

            boolean result = 
                    importHelper.readXlsxFileData(
                            uploadfileData, 
                            selectedSheet,
                            headerRowNum,
                            firstDataRowNum,
                            lastDataRowNum);
            
            if (!result) {
                uploadfileData = null;
            }
        }

        // trigger import process if moving from validate tab to results tab
        if ((currStep.endsWith(TAB_VALIDATE))
                && (nextStep.endsWith(TAB_RESULTS))) {
            triggerImport();
        }

        setEnablement(nextStep);
        currentTab = nextStep;
        
        return nextStep;
    }
    
    protected void createHelperForSelectedFormat() {
        List<ImportExportFormatInfo> infoList = getFormatInfoList();
        ImportExportFormatInfo selectedFormatInfo = null;
        for (ImportExportFormatInfo info : infoList) {
            if (info.getFormatName().equals(getSelectedFormatName())) {
                selectedFormatInfo = info;
                break;
            }
        }
        try {
            if (selectedFormatInfo != null) {
                Class helperClass = selectedFormatInfo.getHelperClass();
                importHelper = (ImportHelperBase) helperClass.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException ex) {

        }
    }

    protected void triggerImport() {

        ImportInfo info = importHelper.importData();

        importSuccessful = info.isImportSuccessful();
        importResult = info.getMessage();
    }

    /**
     * Resets models for wizard components.
     */
    protected void reset() {
        currentTab = TAB_SELECT_FORMAT;
        selectedFormatName = null;
        selectedMode = null;
        uploadfileData = null;
        importHelper = null;
        importSuccessful = true;
        importResult = "";
        selectedTableRow = null;
        selectedTreeNode = null;
        sheetNames = new ArrayList<>();
        selectedSheet = null;
        rowNumberHeader = null;
        rowNumberFirstData = null;
        rowNumberLastData = null;
        renderSelectSheet = false;
        selectFileErrorMessage = null;
        selectFileSummaryMessage = null;
    }

    /**
     * Implements the cancel operation, invoked by the wizard's "Cancel"
     * navigation button.
     */
    public String cancel() {
        String completionUrl = getCompletionUrl();
        this.reset();
        return completionUrl;
    }

    /**
     * Implements the finish operation, invoked by the wizard's "Finish"
     * navigation button.
     */
    public String finish() {
        String completionUrl = getCompletionUrl();
        this.reset();
        return completionUrl;
    }

    /**
     * Sets enable/disable state for the navigation buttons based on the current
     * tab and input elements.
     */
    public void setEnablementForCurrentTab() {
        setEnablement(currentTab);
    }

    /**
     * Sets enable/disable state for the navigation buttons based on the current
     * tab and input elements.
     */
    protected void setEnablement(String tab) {

        // default
        disableButtonPrev = true;
        disableButtonCancel = false;
        disableButtonFinish = true;
        disableButtonNext = true;

        if (tab.endsWith(TAB_SELECT_FORMAT)) {
            disableButtonPrev = true;
            disableButtonCancel = false;
            disableButtonFinish = true;

            if (getSelectedFormatName()!= null) {
                disableButtonNext = false;
            } else {
                disableButtonNext = true;
            }

        } else if (tab.endsWith(TAB_SELECT_MODE)) {
            disableButtonPrev = false;
            disableButtonCancel = false;
            disableButtonFinish = true;

            if (selectedMode != null) {
                disableButtonNext = false;
            } else {
                disableButtonNext = true;
            }

        } else if (tab.endsWith(TAB_SELECT_FILE)) {
            disableButtonPrev = false;
            disableButtonCancel = false;
            disableButtonFinish = true;

            if (selectedSheet != null) {
                disableButtonNext = false;
            } else {
                disableButtonNext = true;
            }
            
        } else if (tab.endsWith(TAB_VALIDATE)) {
            disableButtonPrev = true;
            disableButtonCancel = false;
            disableButtonFinish = true;
            
            if (importHelper.isValidInput()) {
                disableButtonNext = false;
            }
            
        } else if (tab.endsWith(TAB_RESULTS)) {
            disableButtonPrev = true;
            disableButtonCancel = true;
            disableButtonFinish = false;
            disableButtonNext = true;
        }
    }
}
