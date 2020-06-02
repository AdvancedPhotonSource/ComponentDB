/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase.SimpleInputHandler;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase.ImportInfo;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase.OutputColumnModel;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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

    protected static final String tabSelectFile = "SelectFileTab";
    protected static final String tabValidate = "ValidateTab";
    protected static final String tabResults = "ResultsTab";

    protected ImportHelperBase importHelper = null;

    protected String currentTab = tabSelectFile;

    private Boolean disableButtonPrev = true;
    private Boolean disableButtonNext = true;
    private Boolean disableButtonFinish = true;
    private Boolean disableButtonCancel = false;

    // models for select file tab
    private Boolean disableButtonUpload = true;
    protected UploadedFile uploadfileData = null;
    
    protected boolean importSuccessful = true;
    protected String importResult = "";
    
    private CdbEntity selectedTableRow = null;
    private TreeNode selectedTreeNode = null;

    public static ItemDomainImportWizard getInstance() {
        return (ItemDomainImportWizard) SessionUtility.findBean(
                ItemDomainImportWizard.CONTROLLER_NAMED);
    }

    public void registerHelper(ImportHelperBase helper) {
        reset();
        importHelper = helper;
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
        setSelectedTableRow((CdbEntity)event.getTreeNode().getData());
    }
    
    public CdbEntity getSelectedTableRow() {
        return selectedTableRow;
    }
    
    public void setSelectedTableRow(CdbEntity entity) {
        selectedTableRow = entity;
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

    public String getUploadfileDataString() {
        if (uploadfileData == null) {
            return "";
        } else {
            return uploadfileData.getFileName();
        }
    }

    public String getImportResultString() {
        return importResult;
    }

    public String getValidationMessage() {
        return importHelper.getValidationMessage();
    }

    public boolean getShowValidationMessage() {
        String validationMessage = importHelper.getValidationMessage();
        return (validationMessage != null) && (!validationMessage.isEmpty());
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

    public void fileUploadListenerData(FileUploadEvent event) {

        uploadfileData = event.getFile();
        boolean result = importHelper.readXlsxFileData(uploadfileData);
        if (!result) {
            uploadfileData = null;
        }

        setEnablementForCurrentTab();
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

        // trigger import process if moving from validate tab to results tab
        if ((currStep.endsWith(tabValidate))
                && (nextStep.endsWith(tabResults))) {
            triggerImport();
        }

        setEnablement(nextStep);

        currentTab = nextStep;

        return nextStep;
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
        currentTab = tabSelectFile;
        uploadfileData = null;
        importHelper = null;
        importSuccessful = true;
        importResult = "";
        selectedTableRow = null;
        selectedTreeNode = null;
    }

    /**
     * Implements the cancel operation, invoked by the wizard's "Cancel"
     * navigation button.
     */
    public String cancel() {
        String completionUrl = importHelper.getCompletionUrl();
        this.reset();
        return completionUrl;
    }

    /**
     * Implements the finish operation, invoked by the wizard's "Finish"
     * navigation button.
     */
    public String finish() {
        String completionUrl = importHelper.getCompletionUrl();
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

        if (tab.endsWith(tabSelectFile)) {
            disableButtonPrev = true;
            disableButtonCancel = false;
            disableButtonFinish = true;

            if (uploadfileData != null) {
                disableButtonNext = false;
            } else {
                disableButtonNext = true;
            }
        } else if (tab.endsWith(tabValidate)) {
            disableButtonPrev = true;
            disableButtonCancel = false;
            /* if (importHelper.isValidationOnly()) {
                disableButtonFinish = false;
            } else { */
            disableButtonFinish = true;
            if (importHelper.isValidInput()) {
                disableButtonNext = false;
            }
        } else if (tab.endsWith(tabResults)) {
            disableButtonPrev = true;
            disableButtonCancel = true;
            disableButtonFinish = false;
            disableButtonNext = true;
        }
    }
}
