/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.wizard;

import gov.anl.aps.cdb.portal.import_export.export.objects.GenerateExportResult;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperBase;
import gov.anl.aps.cdb.portal.import_export.import_.objects.HelperWizardOption;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author craig
 */
@Named(ItemDomainExportWizard.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainExportWizard implements Serializable {
    
    public static final String CONTROLLER_NAMED = "exportWizard";

    protected static final String TAB_SELECT_FORMAT = "SelectFormatTab";
    protected static final String TAB_SELECT_MODE = "SelectModeTab";
    protected static final String TAB_SELECT_OPTIONS = "SelectOptionsTab";
    protected static final String TAB_CONFIRMATION = "ConfirmationTab";
    protected static final String TAB_DOWNLOAD_FILE = "DownloadFileTab";
    
    protected String currentTab = TAB_SELECT_FORMAT;

    protected ImportHelperBase importHelper = null;
    
    private Boolean disableButtonPrev = true;
    private Boolean disableButtonNext = true;
    private Boolean disableButtonFinish = true;
    private Boolean disableButtonCancel = false;
    
    private DomainImportExportInfo domainInfo = null;
    
    // models for select format tab
    private String selectedFormatName = null;
    
    // models for select mode tab
    private String selectedMode = null;

    // models for confirmation tab
    private String confirmationMessage = null;
    private boolean hasError = false;
    
    // models for file download tab
    private StreamedContent downloadFile;

    public static ItemDomainExportWizard getInstance() {
        return (ItemDomainExportWizard) SessionUtility.findBean(
                ItemDomainExportWizard.CONTROLLER_NAMED);
    }

    public ImportHelperBase getImportHelper() {
        return importHelper;
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
    
    public Boolean supportsModeExport() {
        if (importHelper == null) {
            return false;
        }
        return importHelper.supportsModeExport();
    }

    public Boolean supportsModeTransfer() {
        if (importHelper == null) {
            return false;
        }
        return importHelper.supportsModeTransfer();
    }

    public StreamedContent getDownloadFile() {
        return downloadFile;
    }

    /**
     * Handles radio button click for select mode tab of wizard.
     */
    public void clickListenerFormat() {
        setEnablementForCurrentTab();
    }
    
    public Boolean getDisableButtonPrev() {
        return disableButtonPrev;
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

    public List<HelperWizardOption> getWizardOptions() {
        if (importHelper == null) {
            return new ArrayList<>();
        } else {
            return importHelper.getExportWizardOptions();
        }
    }   
    
    public boolean isRenderOptions() {
        return (!getWizardOptions().isEmpty());
    }
    
    public String getConfirmationMessage() {
        return confirmationMessage;
    }
    
    public String onFlowProcess(FlowEvent event) {
        String result = onFlowProcessHandler(event);
        SessionUtility.executeRemoteCommand("PF('loadingDialog').hide();");
        return result;
    }
    
    public String onFlowProcessHandler(FlowEvent event) {

        String nextStep = event.getNewStep();
        String currStep = event.getOldStep();
        
        // handle transition from select format tab
        if ((currStep.endsWith(TAB_SELECT_FORMAT))
                && (nextStep.endsWith(TAB_SELECT_MODE))) {
            
            createHelperForSelectedFormat();
            
            if (importHelper == null) {
                // don't allow transition if we couldn't create helper
                SessionUtility.addErrorMessage(
                    "Unable to create export helper for selected format",
                    "Please select a different format or Cancel.");
                setEnablement(currStep);
                currentTab = currStep;
                return currStep;
            }
            
        }

        // handle select mode tab
        if ((currStep.endsWith(TAB_SELECT_MODE))
                && (nextStep.endsWith(TAB_SELECT_OPTIONS))) {
            
            importHelper.setExportMode(getSelectedMode());
            
            // skip options tab if no options specified
            if (importHelper.getExportWizardOptions().isEmpty()) {
                nextStep = "exportWizard" + TAB_CONFIRMATION;
            }
        }
        
        // handle transition to confirmation tab file
        if (((currStep.endsWith(TAB_SELECT_OPTIONS)) || (currStep.endsWith(TAB_SELECT_MODE)))
                && (nextStep.endsWith(TAB_CONFIRMATION))) {
            
            // validate and handle wizard options if appropriate
            if (currStep.endsWith(TAB_SELECT_OPTIONS)) {
                
                // validate options
                ValidInfo validOptionsInfo = importHelper.validateExportWizardOptions();
                if (!validOptionsInfo.isValid()) {
                    // don't allow transition if options validation fails
                    SessionUtility.addErrorMessage(
                            "Invalid format options",
                            validOptionsInfo.getValidString());
                    setEnablement(currStep);
                    currentTab = currStep;
                    return currStep;
                }
                
                // handle options
                ValidInfo handleOptionsInfo = importHelper.handleExportWizardOptions();
                if (!handleOptionsInfo.isValid()) {
                    // don't allow transition if handling options fails
                    SessionUtility.addErrorMessage(
                            "Error handling format options",
                            handleOptionsInfo.getValidString());
                    setEnablement(currStep);
                    currentTab = currStep;
                    return currStep;
                }
            }
            
            hasError = false;
            ValidInfo entityListInfo = importHelper.generateExportEntityList();
            if (!entityListInfo.isValid()) {
                hasError = true;
                confirmationMessage = entityListInfo.getValidString();
            } else {
                int entityCount = importHelper.getExportEntityCount();

                if (entityCount == 0) {
                    hasError = true;
                    confirmationMessage = "No rows selected for export, please cancel "
                            + "and modify filters to select data for export.";
                } else {
                    confirmationMessage = "Click 'Next Step' to export "
                            + importHelper.getExportEntityCount()
                            + " items, or 'Cancel' to modify item selection.";
                }
            }
        }
        
        // handle transition to download tab file
        if ((currStep.endsWith(TAB_CONFIRMATION)) 
                && (nextStep.endsWith(TAB_DOWNLOAD_FILE))) {
                        
            generateExportFile();
            
            if (downloadFile == null) {
                // don't allow transition if we couldn't gemerate export file
                setEnablement(currStep);
                currentTab = currStep;
                return currStep;
            }
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
    
    private void generateExportFile() {
        GenerateExportResult result = importHelper.generateExportFile();
        if (result.getValidInfo().isValid()) {
            downloadFile = result.getContent();
        } else {
                SessionUtility.addErrorMessage(
                    result.getValidInfo().getValidString(),
                    "Select Cancel to continue.");
        }
    }

    /**
     * Resets models for wizard components.
     */
    protected void reset() {
        currentTab = TAB_SELECT_FORMAT;
        selectedFormatName = null;
        selectedMode = null;
        importHelper = null;
        hasError = false;
        confirmationMessage = null;
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

        }  else if (tab.endsWith(TAB_SELECT_MODE)) {
            disableButtonPrev = false;
            disableButtonCancel = false;
            disableButtonFinish = true;

            if (selectedMode != null) {
                disableButtonNext = false;
            } else {
                disableButtonNext = true;
            }

        } else if (tab.endsWith(TAB_SELECT_OPTIONS)) {
            disableButtonPrev = false;
            disableButtonCancel = false;
            disableButtonFinish = true;
            disableButtonNext = false;

        } else if (tab.endsWith(TAB_CONFIRMATION)) {
            if (!hasError) {
                disableButtonCancel = false;
                disableButtonFinish = true;
                disableButtonNext = false;
            } else {
                disableButtonCancel = false;
                disableButtonFinish = true;
                disableButtonNext = true;
            }

        } else if (tab.endsWith(TAB_DOWNLOAD_FILE)) {
            disableButtonCancel = true;
            disableButtonFinish = false;
            disableButtonNext = true;
        }
    }

}
